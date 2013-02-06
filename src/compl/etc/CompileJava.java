/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

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

/**
 *
 * @author NOT kieda
 */
public class CompileJava {
    /**
     * Does the required object initialization and compilation.
     */
    //root_src - the root for the source files
    public static boolean doCompilation (File root_src, String class_name, String source_code){
        return doCompilation(root_src, null, class_name, source_code);
    }
    /**
     * 
     * Not by me, by http://www.accordess.com/wpblog/an-overview-of-java-compilation-api-jsr-199/
     * 
     * I believe this (might) be fine, as the intended use is for educational 
     * purposes, and the code source was posted online in a public fashion...
     */
    public static boolean doCompilation (File root_src, File output_dir, String class_name, String source_code){
//        File root = new File(".");
        /*Creating dynamic java source code file object*/
        SimpleJavaFileObject fileObject = new DynamicJavaSourceCodeObject (class_name, source_code) ;
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
        String[] compileOptions;
        if(output_dir == null) compileOptions = new String[]{};
        else compileOptions = new String[]{"-d", output_dir.getPath()};//"-d", "bin"} ;
        //NOTE : this program will not work if the directory output_dir does not exist.
        //We are relying on the fact that we "created" the file while parsing the 
        //args that the folder was also created.
        //we also are relying on that we are inputting a folder that is created.
        Iterable<String> compilationOptionss = Arrays.asList(compileOptions);
 
        /*Create a diagnostic controller, which holds the compilation problems*/
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
 
        /*Create a compilation task from compiler by passing in the required input objects prepared above*/
        JavaCompiler.CompilationTask compilerTask = compiler.getTask(null, stdFileManager, diagnostics, compilationOptionss, null, compilationUnits) ;
 
        //Perform the compilation by calling the call method on compilerTask object.
        boolean status = compilerTask.call();
 
        if (!status){//If compilation error occurs
            /*Iterate through each compilation problem and print it*/
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()){
                System.out.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
            }
        }
        try {
            stdFileManager.close() ;//Close the file manager
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
//        if(status){//compilation was successful
//            try {
////                System.out.println("asdf"+new URL(fileObject.toUri()));
//                URLClassLoader classLoader = URLClassLoader.newInstance(
//                        new URL[] { root.toURI().toURL() }
//                        );
//                Class<?> cls = Class.forName(class_name, true, classLoader); // Should print "hello".
//                Object instance;
//                instance = cls.newInstance(); // Should print "world".
//                for(Method m : cls.getMethods()){
//                    if(m.getName() == "main"){
//                        Object o = m.invoke(instance);
//                        System.out.println(o);
//                    }
//                }
////                [0].invoke(null);
////                System.out.println(instance); // Should print "test.Test@hashcode".
//            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException| InvocationTargetException ex) {ex.printStackTrace();}
//        }
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
        super(URI.create("string:///" +name.replaceAll("\\.", "/") + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
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