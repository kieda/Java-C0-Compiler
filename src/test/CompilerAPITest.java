/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

 
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale; 

 
import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
 
/**
 * A test class to test dynamic compilation API.
 *
 */
public class CompilerAPITest {
//    final Logger logger = Logger.getLogger(CompilerAPITest.class.getName()) ;
 
    /**Java source code to be compiled dynamically*/
    static String sourceCode = //"package com.accordess.ca;" +
//        "class DynamicCompilationHelloWorld{" +
//            "public static void main (String args[]){" +
//                "System.out.println (\"Hello, dynamic compilation world!\");" +
//            "}" +
//        "}" ;
 "public class Test { "
            + "public int main(){\n"
            + "    System.out.print(\"hello\b\");\n"
            + "    return 3 + 5;\n"
            + "     return sadf;\n"
            + "}\n"
            + " }";
    /**
     * Does the required object initialization and compilation.
     */
    public static void doCompilation (){
        File mainFile = new File(".");
        /*Creating dynamic java source code file object*/
        SimpleJavaFileObject fileObject = new DynamicJavaSourceCodeObject ("Test", sourceCode) ;
        JavaFileObject javaFileObjects[] = new JavaFileObject[]{fileObject} ;
        
        /*Instantiating the java compiler*/
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 
        /**
         * Retrieving the standard file manager from compiler object, which is used to provide
         * basic building block for customizing how a compiler reads and writes to files.
         *
         * The same file manager can be reopened for another compiler task.
         * Thus we reduce the overhead of scanning through file system and jar files each time
         */
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, Locale.getDefault(), null);
 
        /* Prepare a list of compilation units (java source code file objects) to input to compilation task*/
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(javaFileObjects);
 
        /*Prepare any compilation options to be used during compilation*/
        //In this example, we are asking the compiler to place the output files under bin folder.
        String[] compileOptions = new String[]{};//"-d", "bin"} ;
        Iterable<String> compilationOptionss = Arrays.asList(compileOptions);
 
        /*Create a diagnostic controller, which holds the compilation problems*/
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
 
        /*Create a compilation task from compiler by passing in the required input objects prepared above*/
        CompilationTask compilerTask = compiler.getTask(null, stdFileManager, diagnostics, compilationOptionss, null, compilationUnits) ;
 
        //Perform the compilation by calling the call method on compilerTask object.
        boolean status = compilerTask.call();
 
        if (!status){//If compilation error occurs
            /*Iterate through each compilation problem and print it*/
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()){
//                System.out.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
                
                //DO SOMETHING ABOUT UNREACHABLE STATEMENTS
                System.err.println(diagnostic.getMessage(Locale.ENGLISH));
                //SOMETHING HERE THAT CONVERTS A POSITION FROM OUR LARGE FILE TO
                //A POSITION FROM OUR ORIGINAL FILE.
            }
        }
        try {
            stdFileManager.close() ;//Close the file manager
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(status){//compilation was successful
            try {
//                System.out.println("asdf"+new URL(fileObject.toUri()));
                URLClassLoader classLoader = URLClassLoader.newInstance(
                        new URL[] { mainFile.toURI().toURL() }
                        );
                Class<?> cls = Class.forName("Test", true, classLoader); // Should print "hello".
                Object instance;
                instance = cls.newInstance(); // Should print "world".
                for(Method m : cls.getMethods()){
                    if(m.getName() == "main"){
                        Object o = m.invoke(instance);
                        System.out.println(o);
                    }
                }
//                [0].invoke(null);
//                System.out.println(instance); // Should print "test.Test@hashcode".
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException| InvocationTargetException ex) {ex.printStackTrace();}
        }
    }
 
    public static void main(String args[]){
         CompilerAPITest.doCompilation() ;
    }
 
}
 
/**
 * Creates a dynamic source code file object
 *
 * This is an example of how we can prepare a dynamic java source code for compilation.
 * This class reads the java code from a string and prepares a JavaFileObject
 *
 */
class DynamicJavaSourceCodeObject extends SimpleJavaFileObject{
    private String qualifiedName ;
    private String sourceCode ;
 
    /**
     * Converts the name to an URI, as that is the format expected by JavaFileObject
     *
     *
     * @param fully qualified name given to the class file
     * @param code the source code string
     */
    protected DynamicJavaSourceCodeObject(String name, String code) {
        super(URI.create("string:///" +name.replaceAll("\\.", "/") + Kind.SOURCE.extension), Kind.SOURCE);
        this.qualifiedName = name ;
        this.sourceCode = code ;
    }
 
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors)
            throws IOException {
        return sourceCode ;
    }
 
    public String getQualifiedName() {
        return qualifiedName;
    }
 
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }
 
    public String getSourceCode() {
        return sourceCode;
    }
 
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}