package compl;

import org.kieda.data_structures.Stack;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.kieda.util.ReadFile;
import org.kieda.util.console.Console;
import compl.etc.CompileJava;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import project.J0VM_Info;
/**
 * 
 * @author kieda
 */
public class C0Compiler {
    /**raise an error during args parsing*/
    private static boolean raise(String err){
        System.err.println(err);
        //an error was raised, return false
        return false;
    }
    
//    //the output file where we want to compile the bullshit to 
//    private static File output;
    
    private static class Arg{
        public Arg(Object data, String name){this.data = data; this.name = name;}
        public Arg(String name){this.data = null; this.name = name;}
        /**storage for something like a filepath or a file*/
        private Object data;
        /**the name of the arg to be searched for while parsing the inputs*/
        private String name;
        @Override public boolean equals(Object other){
            if(other instanceof Arg) return name.equals(((Arg)other).name);//two names are the same
            else if(other instanceof String) return name.equals((String)other);//support for only passing in the name
            return false;
        }
        @Override public int hashCode(){
            return name.hashCode();//hash on the name
        }
    }
    //the set of user-input args
    private static Set<Arg> args = new HashSet<Arg>();
    
    private static final Arg HELP_ARG = new Arg("-h");
    private static final Arg DEBUG_ARG = new Arg("-d");
    private static final Arg VERSION_ARG = new Arg("-V");
    private static final Arg EXEC_ARG = new Arg("-x");
    
    private static Arg Output_Arg = new Arg("-o");
    private static Arg Input_Arg = new Arg("INPUT");
    
    /**
     * choices:
     *      java C0Compiler {[-d], [-o <file>], [-x], <c0file...>}
     *      java C0Compiler {[-V], [-h]}
     * 
     * "{}" represents an unordered set
     * "[]" represents an optional argument
     * "<>" represents a token following an argument.
     * 
     * supported operations:
     *      -h            --help           [displays help massage]
     *      -d            --dyn-check      [dynamically check contracts]
     *      -V            --version        [prints out the version of the j0vm]
     *      -o <file>     --output=<file>  [sets the output path]
     *      -x            --exec           [executes the function after compilation] 
     * 
     * unsupported operations:
     *      -v            --verbose        [verbose status and error messages]
     *      --dump-ast                     [pretty print (?) the program's abstract syntax tree]
     *      -s            --save-files     [produce .c and .h files]
     *      -r <rt>       --runtime=<rt>   [select a runtime]
     *      -O <opt>      --optimize=<opt> [Optimize (?) to level <opt>]
     *      -l <lib>      --library=<lib>  [load the library <lib>]
     *      -L <dir>                       [add <dir> to the search path for libraries]
     *      -b            --bytecode       [generate bytecode (this generates bytecode, silly!)]
     *      -n            --no-log         [disable logging for this compile]
     * 
     * @param a the args passed from the main.
     * @return true on successful execution, and false otherwise.
     * 
     * The return value is useful in knowing whether or not to continue 
     * executing the function.
     */
    static boolean parse_compiler_args(String[] a){
        if(a.length == 0) return raise("no arguments! input \"-h\" for help.");
        int c = 0;
        //we go through the args
        while(c<a.length){
            //run through the list of args
            switch(a[c]){
               /**
                *      -h            --help           [displays help massage]
                *      -d            --dyn-check      [dynamically check contracts]
                *      -V            --version        [prints out the version of the j0vm]
                *      -o <file>     --output=<file>  [sets the output path]
                *      -x            --exec           [executes the function after compilation]                
                */
                
                case "-h":
                case "--help":
                    //we don't want crossover between compilation and help
                    if(args.contains(Output_Arg)
                     ||args.contains(DEBUG_ARG)
                     ||args.contains(Input_Arg)
                     ||args.contains(EXEC_ARG)) 
                        return raise("Can't have compilation flags and help flags.");
                    else if(args.contains(HELP_ARG))
                        return raise("Can't ask for help twice. Once is enough.");
                    args.add(HELP_ARG);
                        //add on the help arg onto the list of args
                    c++;//go to next argument
                    break;
                case "-d":
                case "-dyn-check":
                    if(args.contains(DEBUG_ARG))
                        return raise("Can't dynamically check twice. That's rediculous.");
                    else if(args.contains(HELP_ARG)|| args.contains(VERSION_ARG))
                        return raise("Can't have help flags and compilation flags.");
                    
                    args.add(DEBUG_ARG);
                        //add on the debug flag onto the list of args.
                    c++;//go to next argument
                    break;
                case "-V":
                case "--version":
                    //we don't want crossover between compilation and help
                    if(args.contains(Output_Arg)
                     ||args.contains(DEBUG_ARG)
                     ||args.contains(Input_Arg)
                     ||args.contains(EXEC_ARG)) 
                        return raise("Can't have compilation flags and help flags.");
                    else if(args.contains(VERSION_ARG))
                        return raise("Can't ask for version twice. Once is enough.");
                    
                    args.add(VERSION_ARG);
                    c++;//go to next argument
                    break;
                case "-o":
                    if(args.contains(Output_Arg))
                        return raise("More than one output? Whaaat?");
                    else if(args.contains(HELP_ARG)|| args.contains(VERSION_ARG))
                        return raise("Can't have help flags and compilation flags.");
                    c++;//next arg for the output file path
                    if(c == a.length) return raise("\"-o\" requires a filepath");
                    
                    {
                        File f = new File(a[c]);
                        if(f.exists() && f.isDirectory()) {return raise("file already exists as a directory.");}
                        //create a new file
//                        if(!f.exists()) {f.createNewFile();}
//                        else if(!f.canWrite()) return raise("can't write to file " + a[c] + ".");
                        Output_Arg.data = a[c];//we just set the name, we'll deal with creating a new file later.
                        args.add(Output_Arg);
                    }
                    c++;//go to next argument
                    break;
                case "-x":
                case "--exec":
                    if(args.contains(EXEC_ARG))
                        return raise("Executing twice? Whaaat?");
                    else if(args.contains(HELP_ARG)|| args.contains(VERSION_ARG))
                        return raise("Can't have help flags and compilation flags.");
                    
                    args.add(EXEC_ARG);
                    c++;
                    break;
                default:
                    def:{
                    //parse output
                    if(a[c].startsWith("-output=")) {
                        if(args.contains(Output_Arg))
                        return raise("More than one output? Whaaat?");
                        else if(args.contains(HELP_ARG)|| args.contains(VERSION_ARG))
                            return raise("Can't have help flags and compilation flags.");
                        String s = a[c].substring(8);//remove the "-output=" part
                        if(s.isEmpty()) return raise("\"-output=\" requires a filepath");
                        {
                            File f = new File(s);
                            if(!f.exists()) return raise("incorrect filepath: " + a[c]);
                            else if(!f.canRead()) return raise("file " + a[c] + " is not readable.");

                            Output_Arg.data = f;
                            args.add(Output_Arg);
                        }
                        c++;//go to next argument
                    } else tok:{//we assume that we've begun our list of input c0 files
                        if(args.contains(HELP_ARG)|| args.contains(VERSION_ARG) || args.contains(Input_Arg))
                            break tok;//list of c0 files and help functions don't go together
                                      //we also don't want to input twice.
                        
                        ArrayList<File> alf = new ArrayList<File>();//list of files to be parsed
                        
                        
                        loop: while(c<a.length){
                            //we go until something is not recognized as a file,
                            //or when we run out of args.
                            File gg = new File(a[c]);
                            if(gg.exists()){
                                if(!gg.canRead()) return raise("Can't read file "+a[c] + ".");
                                //we know the file exists and that we can read it.
                                if(gg.isDirectory()) return raise("Input can't be a directory.");
                                 alf.add(gg);//add the current file onto the file list
                            }else{
                                //the file doesn' exist, so we might be on the
                                //next command, so we exit this loop so we can 
                                //add on the commands
                                break loop;
                            }
                            c++;
                        }
                        if(alf.isEmpty()) break tok;
                            //no files were added, so the user must have input 
                            //a wrong filename or wrong token.
                        Input_Arg.data = alf;//set the list of files
                        args.add(Input_Arg);
                        break def;
                    }
                    return raise("unknown token: " + a[c]+".");
                }
            }
        }
        return true;//we completed!
    }
    
    //args takes each token individually. For example:
    //      java drivers.Main hello world
    //gives
    //      [hello, world]
    
    //also neat - this parser requires no order, and won't start running 
    //arguments that are incorrect.
    public static void main(String[] aas) {
        String[] aags  = new String[]{"./src/Test.c0", "-o", "Test", "-x"};
//        String[] aags  = new String[]{"-h", "-V"};
        J0VM_Info.open();
        //classloader - load classes into the JVM
        if(!parse_compiler_args(aags)){ //we weren't able to parse correctly.
            raise("unsucessful parse of command line args.");
            return;
        }
        //we parsed correctly
        { boolean flag = false;   //in the case we're doing a helper thing.
          if(args.contains(HELP_ARG)){//helper
            //help the user out.
            Console.openMC(30);
                Console.fillel("-");//neat!
                Console.printel(J0VM_Info.getName() + " version " + J0VM_Info.getVersion());
                Console.printel("Developed by "+J0VM_Info.getAuthor());
                Console.fillel("-");
            Console.end();
            File ff = new File("./src/info/help");
            if(!(ff.exists() && ff.canRead())) {System.out.println("well this is embarrasing, we can't find the help file!"); return;}
            System.out.println("\n"+ReadFile.read(ff));//print out the help file.
            flag = true;
        } if(args.contains(VERSION_ARG)){//version
             Console.openMC(13, ":");
                Console.fillel("-");//neat!
                Console.printel(J0VM_Info.getName() + ":version " + J0VM_Info.getVersion()+":by "+J0VM_Info.getAuthor());
                Console.fillel("-");
            Console.end();
            flag = true;
        } if(flag) return;       }//exit.
        
        File output_path;
        String class_name = "";
        //we know that the args compile was successful, and that we are not 
        //in the help-options, and we know that the compilation of the C0 
        //code was successful, so we know that we can create an output file.
        if(args.contains(Output_Arg)){
            String file_name = (String)Output_Arg.data;
            
            //output class file.
            if(!file_name.endsWith(".class")){//doesn't end with class
                if(!file_name.startsWith("./")){//and it doesn't begin with ./
                    if(!file_name.startsWith("/")){//and it doesn't begin with /
                        //the file name is the thing we need.
                        class_name = file_name;
                    } else{//it begins with /
                        class_name = file_name.substring(1);//name without the /
                    }
                } else {//it begins with ./
                    class_name = file_name.substring(2);//without the ./
                }
            }
            else{
                class_name = file_name.substring(0, file_name.length()-6);
                    //without the .class at the end
            }
            file_name = "./"+class_name+".class";
            output_path = new File(file_name);
        } else{
            class_name = "a";
            output_path = new File("./a.class");
        }
        //the file path does not exist
        if(!output_path.getParentFile().exists()) {
            //we create the parent folder.
            
            //a quick work-around for the fact that you can't create a file nested
            //in many folders.
            File highest_parent = output_path.getParentFile();
            //lightweight Stack courtesy of the Kieda Framework!
            Stack<File> files = new Stack<File>();
            while(!highest_parent.exists()){
                files.push(highest_parent);
                highest_parent = highest_parent.getParentFile();
            }
            tag:while(!files.isEmpty()){
                File ff = files.pop();
                if(ff.mkdir()) continue tag;
                //the creation of this filewas sucessful
                else {raise("Unable to create root directory.");
                        //probably because the path is too long.
                      return;}
            }
//            boolean sucessful_create = output_path.mkdir();
            //previous method would only be able to create up to one leven for 
            //the files
        } 
        //otherwise the file exists, and we'll over-write it during 
        //compilation if the compilation is successful.
        /**todo - finish this case up.*/
        if(args.contains(Input_Arg)){
            //the data that we stored there
            ArrayList<File> inputs = (ArrayList<File>)Input_Arg.data;
            //accurding to the way we added them in, inputs is guaranteed not
            //to be empty.
            String source = "";
            File source_parent;
            if(args.contains(DEBUG_ARG)){
                //some kind of different debugging in the C0 to java
                source_parent = null;
                raise("-d flag currently unsupported."); return;
                
                //the compilation from java to bytecode can remain the same, 
                //though
            }
            else{
                File f = inputs.get(0);
                source = "public class " + class_name + "{public static void main(String[] args){System.out.println(main());}";
                source += "static "+ReadFile.read(f);
                source += "}";
                //COMPILE C0 FILE To JAVA FILE
                    //not implemented
                //current - convert java file to a string.
                
                
                
                //f is guaranteed to exist by our previous assertion while 
                //creating the list.
                
//                source += ReadFile.read(f);
                
                //something better than this plz 
                
                source_parent = inputs.get(0).getParentFile();
                System.out.println(source_parent.getPath());
                    //NOTE we should calculate this later by looking at the 
                    //packages
                
                    //though, C0 does not have packages so it will not matter.
                    //the source file would just be the parent file to main.
            }
            //COMPILE JAVA FILE TO BYTECODE
            CompileJava.doCompilation(
                    source_parent,//File - root for the source code
                    output_path.getParentFile(),//File - the output directory
                    class_name,//String - the class name
                    source);//String - one long source code file.
        } else{
            raise("no input files."); return;//exit
        }
        if(args.contains(EXEC_ARG)){
            //execute the function.
            //we know that we can call execute as the function was compiled,
            //and there were input files.
            try {
                File root_dir = output_path.getParentFile();
                //guaranteed not to be null, as we know that output_path goes to
                //a non-directory
                
                URLClassLoader classLoader = URLClassLoader.newInstance(
                        new URL[] {root_dir.toURI().toURL() }
                        );
                Class<?> cls = Class.forName(class_name, true, classLoader);
                    //load the class from the class we created
                Object instance;//instance of the class.
                instance = cls.newInstance();
                type:{
                    tt:for(Method m : cls.getMethods()){
                        Type t[] = m.getGenericParameterTypes();
                        if(m.getName() == "main"){
                            if(t.length!=1) continue tt;
//                            System.out.println(t[0].toString());
                            else if(!t[0].toString().endsWith("[Ljava.lang.String;")) continue tt;
                            //main (java.lang.String[] args)

                            //we pass empty args into the C0 function in the case of -x
                            m.invoke(instance, (Object)(new String[]{}));

                            //we found the main method, and it ran. So, we can exit.
                            break type;
                            
                            //according to java standard, and because the 
                            //program compiled correctly, there should only
                            //be one method under main(String[] args)
                        }
                    }
                    //we went through the methods and did not find a main.
                    //(type was not broken). So, this file is not executable.
                    raise("Error: compiled file does not have a main method.");return;
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException| InvocationTargetException ex) {ex.printStackTrace();}
        }
    }
}
