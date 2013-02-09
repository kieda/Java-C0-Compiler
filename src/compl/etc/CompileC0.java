/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

import org.kieda.data_structures.Queue;
import org.kieda.data_structures.Stack;
import org.kieda.data_structures.StackSet;
import org.kieda.data_structures.Trie;
import compl.data.C0Scanner;
import compl.data.FileManip;
import compl.data.FileManipScanner;
import compl.data.TokenList;
import java.io.File;
import java.util.*;
import test.data_types.various.Tuple2;

/**
 * C0 to java compiler.
 * 
 * NOTE: interfaces.
 * 
 * 
 * NOTE: all private static parsing methods rely that the input is parsed into 
 * tokens according to the C0Scanner standard.
 * 
 * @author kieda
 */

public class CompileC0 implements compl.data.Types{
    static File source_root;
    
    static HashMap<String, C0TypeDef_parsed> typedefs_parsed;
    static HashMap<String, C0TypeDef_unparsed> typedefs_unparsed;
    
    static HashSet<String> reserved_words;
    
    static Stack<Point> pointers_to_be_loaded;
    
    static HashMap<String, C0Interface> interfaces;
    static HashMap<String, C0Struct_interface> struct_interfaces;
    
    //at the moment, I won't include polymorphism due to C0's restrictions.
    static Trie<C0Method_unparsed> methods;//placed by the method name
    static ArrayList<C0Method_completely_unparsed> bad_methods;
        //methods with the same name. done so we can continue analysis.

    static ArrayList<C0Method_completely_unparsed> unparsed_methods;
    static ArrayList<C0Interface_completely_unparsed> unparsed_interfaces;
    
    static Trie<C0Struct_unparsed> structs_unparsed;//placed by the struct name
    static Trie<C0Struct_parsed> structs_parsed;//placed by the struct name
    static ArrayList<C0Struct> bad_structs;
    
    //or maybe a hashmap from the name of the method to the output?
    
    static StackSet<C0Variable> local_data;
        //the data stored in the local {syntax}. Basically, the names the 
        //data holds, and the type of the data.
        //used to ensure that there are no name conflicts, and that the data 
        //under the name being used will be of the correct data type.
    static FileManip current_file;
    
    
    
    //for native imports, we just add the value inside the #use<____> as a 
    //HashSet of integers (the masks)
    //we import them later by coffee
    private static HashSet<Integer> native_imports;
    
    //import by literally copying and pasting the code from the file. 
    //user_imports is a list of things we've imported, so we won't have multiple 
    //imports for the same thing.
    private static HashSet<File> user_imports;
    
    //the list of things we should import. In the #use "___" or in a native 
    //import. We
    private static Queue<Tuple2<String, Integer>> to_import;
    
    
    //1. initialize objects
    //
    //2. scan the main file for #use statements
    //
    //3. gather all of the files under the #use statements and put it into the 
    //   main file. Continue until there are no more #use statements, or when we
    //   have already #used a certain file or native library. Delete all #use
    //   statements after it has finished.
    //
    //4. scan ground-level syntax. (bottom level, out of all {}). First, we scan 
    //   for structs. If the struct is already named, or infringes on a reserved 
    //   word, we flag an error and record the position. Otherwise, we add the 
    //   struct to the list of structs (by adding the name and the body. The 
    //   body will be compiled later). We then delete the struct from the file 
    //   being edited. Next, we search for typedefs. If the typedef infringes on 
    //   a name (like using a reserved word or the name of a struct), or if the 
    //   typedef uses an unknown name, or if the typedef gives something which 
    //   is an illegal name (illegal characters for a name), we flag an error, 
    //   record the position, and continue. We add the typedef to the HashSet 
    //   typedefs if the typedef was correct. We then delete the typedef from 
    //   the file we are analyzing. Next, we scan for interfaces. Each interface
    //   we come by we add to the unfilled_methods. We determine if a method is 
    //   an interface if we have some return type followed by a name, followed 
    //   by paraentheses, followed by a terminator (";"). We scan for the 
    //   terminator by searching after the end of the ")", and searching the 
    //   next lines. If the line is empty (only whitespace), continue to the 
    //   next line. If the line contains a comment, copy the comment from the 
    //   end till the beginning and remove it from the file. If the comment is 
    //   an @requires, add it to the list of @requires statements. If the 
    //   statement is an @ensures, add it to the list of @ensures statements. 
    //   If the statement is otherwise @*, log an error as a method can only 
    //   have @requires and @ensures. If the comment is not @*, discard the 
    //   comment. Continue scanning after each comment. If the next statement 
    //   does not begin with a ";" or a "{", log an error, end checking 
    //   interfaces, and only check methods up till that method. Otherwise If 
    //   we're scanning and see a declaration with parentheses, and then see 
    //   syntax "{}" in the next statement, drop all recorded errors and jump to 
    //   the end of the syntax block. We do this if by incrementing an integer 
    //   for all of the starts of syntax blocks "{", and decrementing the 
    //   integer for all ends "}". We are at the end of this method's syntax 
    //   when the integer reaches zero. If we reach the end of the file and the 
    //   integer is greater than zero, we know that the user has missed a 
    //   bracket "}". We create a new method of the given return type, name, 
    //   parameters, and body. If the method existed as an unfilled_method, we 
    //   remove the unfilled method. If the method's return type does not match, 
    //   record the error. If the method and the interface's parameters do not 
    //   match, we record the error. If a method already exists under the same 
    //   name, record that error, and do not add the method to the list of 
    //   methods. Otherwise add the method to the list of methods. Then, delete 
    //   the method till the end of the file. We then log that there is a 
    //   missing } in the method, and break our checking for interfaces. We 
    //   continue to check the methods. Next, we know that we are at an 
    //   interface. If the interface's name infringes on a reserved word, or if 
    //   the interface's name infringes on a declared struct, or if the 
    //   interfaces name infringes on a declared typedef, we log an error. We 
    //   then check the parameters. If a parameter's values are not usable as 
    //   values (or recognizable values), we log an error for that parameter and
    //   continue to the next parameter. If a parameter's value is type-def-ed, 
    //   we replace the value with the type-def-ed value. If a parameter's name 
    //   infringes on a type-def-ed value, a struct name, or a reserved_word, 
    //   log the error, and continue on to the next parameter. If none of these
    //   errors occured, we make a new C0Data Variable with the name and the 
    //   data type. (We do this for all parameters that work.) When we reach the 
    //   end of the parameters, we place all of the C0Data Variables into an 
    //   array. If unfilled_methods already contains a method under the same 
    //   name, we then check to see that all of the parameter types match up in 
    //   the same order. If they do not, we log an error for the entire new 
    //   interface, and we remove the new interface from our list of interfaces 
    //   to be added. If the parameters and return type match, we add the new 
    //   @requires and @ensures statements to the existing ones. The hashmap 
    //   does not contain the C0Interface under the name, we then create a new 
    //   C0Interface with the given return type, name, and parameters. We then 
    //   add the C0Interface to unfilled_methods. We do this process till we 
    //   reach the end of the file or ontil we otherwise break. We then remove 
    //   the interface, as a whole, from the file. (even if the interface had 
    //   errors. If the interface had an error that caused the compiler to stop
    //   scanning prematurely, remove from the beginning of the interface till
    //   the end of the file.) If something that we scan is neither struct, nor
    //   typedef, not interface, nor method, we record the instance until a 
    //   we either find something in the form of "known type  ___"("expression") 
    //   [a method or interface], a semicolon, a typedef, or a struct. We then 
    //   take the range from the beginning of the unknown expression, till right
    //   before the begining of the struct, typedef, or known type. If the 
    //   expression is terminated by a semicolon, include the semicolon. We then
    //   log the error on the range, and delete the range from the file.
    //   We then go through a second-pass and add methods to the system. We know
    //   that we have removed all interfaces from the system, and we have 
    //   removed all erroneous methods that do not close in syntax. We have also
    //   removed all structs, typedefs, and all statements that don't make any
    //   sense. So, the only thing left are methods. We go through the same 
    //   routine of adding parameters to the method and checking that the method 
    //   is correct. When we finish constructing a method, we check if the 
    //   method is in the set unfilled_methods. If so, we make sure that all of 
    //   the parameters, return type, etc, match up. If they don't, log the 
    //   error. In either case remove the method from the unfilled_methods.
    //   If the method already exists under m0_methods, we log the error that 
    //   there cannot be two of one method. If the debugging flag is on, we add 
    //   @requires and @ensures assertions the same way we did for interfaces. 
    //   We then create a new method with all of the return types and parameters
    //   that were recognizable, and all @requires, @ensures, statements, and 
    //   the body of the file. If the method was a duplicate, add the method
    //   to bad_methods, to be analyzed later. Otherwise, add it to methods.
    //   now, we are done refactoring all parts of the files, and we are left 
    //   with an empty file.
    //
    //5. We then want to look at all of the methods and make sure that they make
    //   sense. A few things we will have to do while looking at methods - 
    //   replace any typedefs, convert all bools to booleans, and convert all
    //   booleans to bools. 
    //   
    //   We define a statement as some string terminated by a semicolon (;).
    //   A statement may be an assignment x = y, where x is a variable and y is
    //   a value. Alternatively, a statement can also just be a value y. (note:
    //   an expression is a value as it evaluates to a value, and a value is an
    //   expression.) There is a difference in how java and C0 handle 
    //   expressions only with a statement. So, on our second pass, when 
    //   compiling to java, we set just an expression as a statement 
    //   from:
    //           expr;
    //   to: 
    //           {Object NULL = expr;}
    //   in order to avoid any conflicts.
    //
    //   So, in a way, all statements are assignments.
    //
    //   we define a code block as the expressions and code blocks contained in
    //   a set of brackets, {}. There are some other cases where something else 
    //   is a code block: the statement immediately after an if, the statement 
    //   immediately after an else, the statement immediately after a for, and
    //   the statement immediately after a while. We use the StackSet to keep 
    //   of local data within a method to ensure there are no name conflicts.
    //
    //   In a method, if there is a return type, we ensure that there is at 
    //   least one return statement. We make sure that the return value type 
    //   matches the function return type.
    //   
    //   Another restriction that the Java compiler has that the C0 compiler 
    //   does not have is that Java recognizes when a function will run on 
    //   forever (on a very basic level), or when there are unreachable 
    //   statements. C0 does not have this restriction. I think I'll leave it 
    //   for now, and just print out the error through the Java compiler.
    //
    
    //          option: put if(true) before loops, break statements, and return
    //                  statements.
    //
    //   Anyway, now to define the process that will be made for checking the C0
    //   code in the methods.
    //   We first check the requires and assert statements.
    //   Something create new pointers for every **. Break up syntax to a list 
    //   of statements. Make sure statements are good in C0. Then we go through 
    //   a second pass and turn all of the C0-language specifics into java 
    //   specifics.
    
//       whatever, I'm too tired now. I think I;ll bed.
    //   lol look at this guy ^^
    
    //note: if file is in the args flag, we add in 
    //struct file;
    //typedef struct file* file_t;
    //NOTE: WE ARE ALLOWED TO 
    //struct file;
    //as long as if we don't alloc(struct file);
    //as struct file is not defined.

    /**
     * returns a list of either user or native imports. User imports will be 
     * returned in the fashion "import", and native imports will be returned in 
     * the fashion <import>
     */
    private static Tuple2<FileManip, Tuple2<String, Integer>[]> find_import(File path){
        //if there's an import for a file (we have to check the the pragma's 
        //first token is "use", we add the entire token.)
        
        //after we have reached something that is neither a comment nor a 
        //pragma, we stop our import search for the given file. We do not need 
        //to search the internal #use -es, as this is handled later.
        int size = 0;
        Queue<Tuple2<String, Integer>> import_list = new Queue<Tuple2<String, Integer>>();
        FileManip this_file = new FileManip(path);
        FileManipScanner scanner = new FileManipScanner(this_file.copy());
        
        //we delete the range from the TextPosition till the end of the line
        Stack<FileManip.TextPosition> lines_to_remove = new Stack<FileManip.TextPosition>();
        FileManipScanner.C0Token w;
        boolean in_imports = true;
        int brace_count = 0;
        while((w = scanner.nextToken()) != null){
            if(in_imports){
                //only pragmas and comments here.
                if(w.baseTypeEqual(PRAGMA)){
                    //remove the pragma from the FileManip
                    lines_to_remove.push(w.position);
                    
                    C0Scanner cos = new C0Scanner(w.text.substring(1));
                    
                    //remove the #.
                    success:{
                        if(cos.hasNext()){
                            String w1 = cos.next();
                            if(w1.equals("use")){
                                Tuple2<String, Integer> imp = new Tuple2<String, Integer>();
                                imp.a = w.text;
                                imp.b = w.position.getLine();
                                size++;
                                import_list.enq(imp);
                                break success;
                            }
                        }
                        //warning : ignoring pragma + w1
                    }
                } else if(!w.isOfType(COMMENT_TYPE)){
                    in_imports = false;
                }
            } else{
                //enter syntax
                if(w.getTokenType() == BRACE_LEFT){
                    brace_count++;
                }
                //exit syntax
                else if(w.getTokenType() == BRACE_RIGHT){
                    brace_count--;
                    if(brace_count < 0){
                        //log an error.
                        //we should also delete the brace (in order to prevent 
                        //this error to be logged on multiple occasions)
                        brace_count++;
                        assert brace_count == 0;
                    }
                }
                //check for no pragmas after the flag
                if(w.baseTypeEqual(PRAGMA)){
                    if(brace_count == 0){
                        C0Scanner cos = new C0Scanner(w.text.substring(1));
                        unexpected_import:{
                            if(cos.hasNext()){
                                String w1 = cos.next();
                                if(w1.equals("use")){
                                    //log an error: #use directives must precedes 
                                    //all other declatations
                                    break unexpected_import;
                                }
                            }
                            //warning : ignoring pragma + w1 (pragma line)
                        }
                        lines_to_remove.push(w.position);
                    } else{
                        //error: expected statement, received #<pragma>.
                        lines_to_remove.push(w.position);
                        //remove the pragma
                    }
                }
            }
        }
        while(!lines_to_remove.isEmpty()){
            FileManip.TextPosition ttp = lines_to_remove.pop();
            if(ttp.getColumn() == 0){
                this_file.delete(ttp.getLine());
            } else
                this_file.delete(new FileManip.Range(ttp, new FileManip.TextPosition(ttp.getLine(), this_file.getLine(ttp.getLine()).length())));
        }
        {
        Tuple2<String, Integer>[] return_val1 = new Tuple2[size];
        while(!import_list.isEmpty())
            return_val1[--size] = import_list.deq();
        Tuple2<FileManip, Tuple2<String, Integer>[]> ret = new Tuple2<FileManip, Tuple2<String, Integer>[]>();
        ret.a = this_file; ret.b = return_val1;
        return ret;
        }
    }
    private static class Import{
        /**
         * the raw string for the import
         */
        String imp;
        int line;
        enum IMP{
            /**
             * an import that is in the form 
             *      #use "_import_"
             */
            USER_IMPORT,
            
            /**
             * a import thought to be a user import, but has flaws in its
             * parsing
             * 
             * a import that is in the form 
             *      #use "
             * or
             *      #use "_something_
             * or
             *      #use "_something_"unexpected_token
             */
            FAULTY_USER_IMPORT,
            
            /**
             * an import in the form
             *      #use <_import_>
             */
            NATIVE_IMPORT,
            
            /**
             * an import thought to be a native import, but has flaws in 
             * parsing
             */
            FAULTY_NATIVE_IMPORT,
            
            /**
             * an import that starts with 
             *      #use
             * but does not have a recognizable identifier to distinguish 
             * whether or not its a user import or a native import
             */
            UNKNOWN
        }
        /**
         * the type of this import. Faulty imports and unknown imports will not 
         * be imported.
         */
        IMP type;
    }
    private static Import import_type(String imp, int line){
        Import ret = new Import();
        ret.line = line;
        //import_type should also contain 
        
        //case 1: #use <import>
        //case 2: #use "import"
        //case 3: import
        
        //the imp should ONLY come from a file, or from args. 
        
        if(imp.startsWith("#")){
            //we know that there is one element, so the substring should be 
            C0Scanner cos = new C0Scanner(imp.substring(1));
            assert cos.hasNext();//we made sure it had "use" after the pragma
            assert cos.next().equals("use");//we made sure of this earlier.
            
            //now, because the pragma had the correct name for an import, we now
            //look at whether or not the import is a user import, a native 
            //import, or an unknown type
            alright:{dance:{
                //dance is broken if there is a faulty import, alright is broken 
                //if the import parsed correctly.
            if(!cos.hasNext()){
                //log an error: #use expects an import
                break dance;
            }
            String token = cos.next();
            if(token.equals("<")){
                ret.type = Import.IMP.NATIVE_IMPORT;
                if(!cos.hasNext()){
                    break dance;
                }//log an error. expected an import for native import. instead found:
                token = cos.next();
                if(token.equals(">")){
                    //error: cannot have an empty string.
                    break dance;
                }
                String name = token;//otherwise the token is the name
                if(!cos.hasNext()){
                    //error: expected >
                    break dance;
                }
                token = cos.next();
                if(!token.equals(">")){
                    //error: expected >. Found X
                    
                    break dance;
                }
                if(cos.hasNext()){//should not happen
                    //unexpected token "_token_" after a library import.
                    break dance;
                }
                
                ret.imp = name;
                //our import completed sucessfully.
                break alright;
            } else if(token.startsWith("\"")){
                ret.type = Import.IMP.USER_IMPORT;
                token = token.substring(1);
                //now in the format:
                //  library"
                if(token.isEmpty()){
                    //expected a file name followed by a closing quote (\")
                    break dance;
                }//log an error. expected an import for file import
                if(token.charAt(token.length()-1) != '\"'){
                    //expected \" at the end of file import.
                    break dance;
                }
                token = token.substring(0, token.length()-1);
                if(token.isEmpty()){
                    //cannot have an empty string as a file import.
                    break dance;
                }
                if(cos.hasNext()){
                    //unexpected token "_token_" after a file import.
                    break dance;
                }
                
                //otherwise the token is completely parsed and is correct.
                ret.imp = token;
                break alright;//exit successful.
            } else{
                ret.type = Import.IMP.UNKNOWN;//unknown import type.
                
                //log an error on it. (do not log it here, it happens later)
                //unknown import type error
                
                //expected one of \" or <. Instead received token "token", for
                //an unknown/invalid import method.
                
                //hint: surround native libraries with <>, and user imported 
                //libraries with "".
                
                String name = imp.substring(1);
                //went from #use to use
                int index = name.indexOf("use");
                
                assert index != -1;
                //should be in, as we know that the pragma is on a single line, 
                //and that we already asserted that the next token was equal to
                //"use". So, we chop the name till right after the "use" 
                //statement

                name = name.substring(index + 3, name.length());
                //+3 for the length of "use".
                ret.imp = name;
            }
            }//end dance
            if(ret.type == Import.IMP.USER_IMPORT){
                ret.type = Import.IMP.FAULTY_USER_IMPORT;
            } else if(ret.type == Import.IMP.NATIVE_IMPORT){
                ret.type = Import.IMP.FAULTY_NATIVE_IMPORT;
            }
            //should be correct otherwise.
           }//end alright
        }/*end if*/else{
            assert !imp.isEmpty();
            assert line == -1;
            //because this is an input from args, we assume that it is for a
            //user import.
            ret.type = Import.IMP.USER_IMPORT;
            ret.imp = imp;
        }//import from our args.
        return ret;
    }
    /**
     * these are the list of files passed to by the C0 main args[]
     * String... points to a list of main files in the source directory.
     */
    private static void imports(){
        user_imports = new HashSet<File>();
        assert to_import != null && !to_import.isEmpty();
        //should not be empty, because we added on at leastt one during the args
        //parsing.
        
        while(!to_import.isEmpty()){
            Tuple2<String, Integer> iimport = to_import.deq();
            Import this_import = import_type(iimport.a, iimport.b);
            
            switch(this_import.type){
                case USER_IMPORT:
                    File this_file = new File(source_root, this_import.imp);
                    if(!this_file.canRead()){
                        //throw user import not found exception
                    }
                    else {
                        if(!user_imports.contains(this_file)){
                            user_imports.add(this_file);
                            Tuple2<FileManip, Tuple2<String, Integer>[]> new_file_imports = find_import(this_file);
                            for(Tuple2<String, Integer> imp : new_file_imports.b){
                                to_import.enq(imp);
                            }//add any imports on from a new file.
                            main.attach(new_file_imports.a);
                        }
                    }
                    break;
                case NATIVE_IMPORT:
                    int i = coffee.getImport(this_import.imp);
                    if(i == -1){
                        //throw invalid native import exception
                    } else
                        native_imports.add(i);
                    break;
                case UNKNOWN:
                    //log an error (invalid input type exception)
                    break;
                case FAULTY_NATIVE_IMPORT:
                    //log an error ()
                    break;
                case FAULTY_USER_IMPORT:
                    //log an error
                    break;
                default : assert false;//should not happen.
            }
        }
    }
    /**
     * this method is used to pre-emptively delete all non-assertive comments,
     * and to remove any extra closing braces.
     */
    private static void scan_for_consistancy(){
        Stack<FileManip.Range> ranges_to_remove = new Stack<FileManip.Range>();
        //find all of the structs in the file, and add them to the list of 
        //types
        assert main != null;
        //we want to parse the structs in our main file. we need our main to be 
        //non-null and initiated. Typically this occurs in the find_imports 
        //method
        FileManipScanner scanner = new FileManipScanner(main);
            //scan a copy of main. we will be deleting from main while we are 
            //parsing.
        FileManipScanner.C0Token tok;
        int brace_count = 0;
        int parentheses_count = 0;
        while((tok = scanner.nextToken()) != null){
            if(tok.isAllOfType(COMMENT_TYPE)){
                
                if(tok.text.length() > 2 && tok.text.charAt(2)=='@'){
                    //this is an assertive statement. We should keep it
                } else{
                    //this is not an assertive statement. we should remove it from main.

                    ranges_to_remove.push(new FileManip.Range(tok.position, scanner.getCurrentPos()));
                    //by the nature of the scanner, the current position is right 
                    //before we start scanning for the next token (at the end of 
                    //this token)
                }
            } else switch(tok.getTokenType()){
                case BRACE_LEFT:
                    //we entered in some syntax {
                    brace_count++;
                    break;
                case BRACE_RIGHT:
                    //we exited some syntax }
                    brace_count--;
                    if(brace_count < 0){
                        //log an error.
                        brace_count++;
                        ranges_to_remove.push(
                                new FileManip.Range(
                                    //goes from the beginning of the token's 
                                    //position, till one after. We just remove the 
                                    //one parentheses.
                                    tok.position, 
                                    new FileManip.TextPosition(
                                        tok.position.getLine(), tok.position.getColumn()+1
                                    )
                                )
                            );
                        assert brace_count == 0;
                    }
                    break;
                case PARENTHESES_LEFT: if(brace_count == 0) parentheses_count++; break;
                case PARENTHESES_RIGHT: if(brace_count == 0) parentheses_count--;
                if(parentheses_count < 0){
                    //log an error.
                    parentheses_count++;
//                    ranges_to_remove.push(
//                        new FileManip.Range(
//                                tok.position, 
//                                new FileManip.TextPosition(
//                                    tok.position.getLine(), 
//                                    tok.position.getColumn()+1
//                                )));
                    assert parentheses_count == 0;
                } break;
            }
        }
        
        while(!ranges_to_remove.isEmpty()){
            main.delete(ranges_to_remove.pop());
        }
    }
    /**
     * should be the first method called. parses structs, removes excess }, and
     * removes comments that are non-assertions.
     */
    private static void structs(){
        //IMPORTANT NOTE: we want to check that struct is not being used as a 
        //type. i.e. we don't confuse
        //      struct A{};
        //with
        //      struct A* b();
        
        //we will never have struct*. If we find it, we throw
        //error: unexpected identifier, found '*'
        
        //we should do this by checking if there are parentheses.
        
        //like:
        
        //  if next is '*'
        //      abort struct
        //  else if next is '('
        //      abort struct
        //  else
        //      
        
        //scans for structs and removes them. there should be no parsed structs 
        //at this time
        assert structs_parsed == null;
        assert structs_unparsed != null;
        assert struct_interfaces != null;
            //this is exppected to be initialized
        Stack<FileManip.Range> ranges_to_remove = new Stack<FileManip.Range>();
        //find all of the structs in the file, and add them to the list of 
        //types
        assert main != null;
        //we want to parse the structs in our main file. we need our main to be 
        //non-null and initiated. Typically this occurs in the find_imports 
        //method
        FileManipScanner scanner = new FileManipScanner(main);
            //scan a copy of main. we will be deleting from main while we are 
            //parsing.
        FileManipScanner.C0Token tok;
        
    //   scan ground-level syntax. (bottom level, out of all {}). First, we scan 
    //   for structs. If the struct is already named, or infringes on a reserved 
    //   word, we flag an error and record the position. Otherwise, we add the 
    //   struct to the list of structs (by adding the name and the body. The 
    //   body will be compiled later). We then delete the struct from the file 
    //   being edited.
        int brace_count = 0;
        int parentheses_count = 0;
        while((tok = scanner.nextToken()) != null){
            switch(tok.getTokenType()){
                case BRACE_LEFT:
                    //we entered in some syntax {
                    brace_count++;
                    break;
                case BRACE_RIGHT:
                    //we exited some syntax }
                    brace_count--;
                    if(brace_count < 0){
                        assert false;
                        //should have been handled
                    }
                    break;
                case PARENTHESES_LEFT: if(brace_count == 0) parentheses_count++; break;
                case PARENTHESES_RIGHT: if(brace_count == 0) parentheses_count--;
                if(parentheses_count < 0){
                    parentheses_count++;
//                    assert false;
                    //should have been handled.
                } break;
            }
            if(brace_count == 0 && parentheses_count == 0 &&tok.text.equals("typedef"))fail:{
                //we don't want to accidentally parse something like
                //typedef struct a b;
                //by recognizing (struct a) before recognizing the typedef
                //we go till the next ;, or the next {.
                //because we are not inside of syntax, we know that we will
                //not meet any ) or }.
                l:while((tok = scanner.nextToken()) != null){
                    switch(tok.getTokenType()){
                        //it's not our job here to manage errors. We're just 
                        //here to skip material...
                        case BRACE_LEFT:{
                            brace_count++;
                            break l;
                        }case SEMI_COLON:{
                            break l;
                        }
                    }
                }
            }
            //we also do not want to detect the word "struct" while in 
            //parentheses at ground level synteax, i.e. we don't want to detect 
            //parameter structs as new structs we must account for.
            
            //we have to see the keyword struct and it has to be out of syntax.
            if(brace_count == 0 && parentheses_count == 0 &&tok.text.equals("struct"))fail:{
                    //fail is for urgent needs. Also used if we noticed the item
                    //that we are parsing is not a struct.
                
                boolean include = true;
                FileManip.TextPosition beginning = tok.position;
                if((tok = scanner.nextToken()) == null) {
                    //unexpected end of file.
                    //(file ended while parsing struct)
                    break fail;
                    //delete this token.
                }
                if(tok.getTokenType() == (MULTIPLY | POINTER_TYPE)){
                    
                    //this is a pointer rather than a struct.
                    //we exit this parsing, in hope that this token will be 
                    //re-scanned later as a method or interface.
                    break fail;
                }
                if(tok.getTokenType() != WORD) {
                    //unexpected token type for struct name: (get type)
                    include = false;
                }
                
                
                String struct_name = tok.text;
                
                if((tok = scanner.nextToken()) == null) {
                    //unexpected end of file.
                    //(file ended while parsing struct)
                    break fail;
                    //delete this token.
                }
                
                if(reserved_words.contains(struct_name)){
                    //we do NOT want a struct to infringe upon a reserved word.
                    include = false;
                }
                for(C0Struct cos : bad_structs)
                    if(cos.struct_name.equals(struct_name)){
                        //log error: typedef name "tok_pre.text" is already the name of a struct.
                        include = false;
                        break;
                    }
                if(tok.getTokenType() == PARENTHESES_LEFT){
                    //we are in a method or interface of type
                    //struct asdf()
                    
                    //even though that structs can't be passed around unless if 
                    //done by pointer, we won't log that error till later, when 
                    //we're scanning interfaces/methods.
                    
                    parentheses_count++;//we are now in parentheses
                    break fail;
                }
                exit:{//syntax for scanning a struct
                if(tok.getTokenType() == SEMI_COLON){
                    //we have a C0 struct interface.
//                    main.delete(new FileManip.Range(beginning.getLine(), beginning.getColumn(), tok.position.getLine(), tok.position.getColumn()+1));//for the end of the semicolon
                    ranges_to_remove.push(new FileManip.Range(beginning.getLine(), beginning.getColumn(), tok.position.getLine(), tok.position.getColumn()+1));//for the end of the semicolon
                    if(include && !struct_interfaces.containsKey(struct_name))
                        struct_interfaces.put(struct_name, new C0Struct_interface(struct_name));

                    break exit;
                    //we added the interface, so we don't need to do any further
                    //parsing. (struct interfaces have no body.)
                }
                if(tok.getTokenType() != BRACE_LEFT){
                    //log an error: expected {
                    //we scan till the next method/typedef/interface, and delete 
                    //from the beginning of the struct till the beginning of the 
                    //next method.
                    
                    //till there
                    break fail;
                }
                brace_count++;
                //we should not have parsed structs yet, and we don't care about
                //re-naming interfaces.
                if(structs_unparsed.get(struct_name) != null){
                    //struct is already defined.
                    include = false;
                }
                if((tok = scanner.nextToken()) == null) {
                    //unexpected end of file.
                    //(file ended while parsing struct)
                    break fail;
                    //delete this token.
                }
                FileManip.TextPosition begin_body = tok.position;
                do{
                    if(tok.getTokenType() == BRACE_RIGHT){
                        brace_count --;
                        //exit the struct
                        FileManip.TextPosition end_body = tok.position;
                        boolean flag = true;
                        if((tok = scanner.nextToken()) == null) {
                            //unexpected end of file.
                            //(file ended while parsing struct)
                            //delete till end of file
                            ranges_to_remove.push(new FileManip.Range(beginning, main.getEndPosition()));
                            break fail;
                        }
                        if(tok.getTokenType() != SEMI_COLON){
                            //error: expected ;
                            //delete till right before where the semi-colon was 
                            //expected
                            //break something
                            FileManip struct_body = scanner.getFileManip().subRange(new FileManip.Range(begin_body, end_body));//for the end of the semicolon
                            bad_structs.add(new C0Struct_unparsed(struct_body, struct_name));
//                            main.delete(new FileManip.Range(beginning.getLine(), beginning.getColumn(), tok.position.getLine(), tok.position.getColumn()));//for the end of the semicolon\
                            flag = false;
                            ranges_to_remove.push(new FileManip.Range(beginning.getLine(), beginning.getColumn(), tok.position.getLine(), tok.position.getColumn()));//for the end of the semicolon
                        }
                      //  System.out.println("##"+scanner.getFileManip().getLine(end_body.getLine()) + " "+end_body);
                        //scanner.getFileManip().getCharAt(end_body);
                        FileManip struct_body = scanner.getFileManip().subRange(new FileManip.Range(begin_body, end_body));//for the end of the semicolon
//                        main.delete(new FileManip.Range(beginning.getLine(), beginning.getColumn(), tok.position.getLine(), tok.position.getColumn()+1));//for the end of the semicolon
                        if(flag)
                            ranges_to_remove.push(new FileManip.Range(beginning.getLine(), beginning.getColumn(), tok.position.getLine(), tok.position.getColumn()+1));//for the end of the semicolon
                        if(include && flag){
                            structs_unparsed.put(struct_name, new C0Struct_unparsed(struct_body, struct_name));
                        } else{
                            bad_structs.add(new C0Struct_unparsed(struct_body, struct_name));
                        }
                        //exit the struct syntax.
                        
                        break;
                    } 
                    //this will come later while parsing.
//                    else if(!(tok.getWordType()==WORD || tok.getWordType() == SEMI_COLON||tok.isAllOfType(POINTER_TYPE) || tok.baseTypeEqual(BRACKET))){
//                        //error: expected expression. instead found + tok.
//                    }
                    tok = scanner.nextToken();
                    if(tok == null){
                        //error: reached end of file while parsing struct + name.
                        //then delete from beginning_position till the end.
                        break exit;
                    }
                }while(true);
                }
            }
            
            
        }
        //delete the ranges from main
        while(!ranges_to_remove.isEmpty()){
            main.delete(ranges_to_remove.pop());
        }
    }
    /**
     * this method is supposed to scan all of the ground-level syntax to ensure 
     * that it is "fit" to compile. Due to the non-linear this compiler works; 
     * it does a "find first" method, parse later of the individual parts.
     * This is useful, as it allows us to have methods, structs, etc, in 
     * any order. Unfortunately, because it scans for specific phrases, it might
     * not notice if a phrase is inherently incorrect on the ground level.
     * 
     * for example, 
     * 
     * int typedef bool* BB; main(){
     *  return 0;
     * }
     * 
     * would first find
     * typedef bool* BB;, put it on the list of typedefs to compile, and remove 
     * the string range from the file. The file then would look like:
     * 
     * int main(){
     *  return 0;
     * }
     * 
     * which compiles perfectly correctly!
     * 
     * (I consider this an interesting feature, to say the least. In order to 
     * prevent the client from being scared and confused, (sometimes an 
     * impossible task, but here's my crack at it) I implemented this method
     * that just scans the ground-level syntax, token by token and checks that 
     * every expression in the ground-level syntax is either a(n)
     * 
     *      > interface
     *      > typedef
     *      > struct
     *      > method
     * 
     * We will not have any imports, as we are assuming that the import
     * function has already been called (so all #use statements should have been
     * removed)
     * 
     * In addition, this function removes all non-assertive comments
     * 
     * If there is an error on the ground level syntax, we remove it to the best
     * of this parser's ability, before continuing.
     * 
     * todo: finish this algorithm.
     */
    private static void scan_ground_level_syntax(){
       FileManipScanner fms = new FileManipScanner(main);
       FileManipScanner.C0Token tok;
       Stack<FileManip.Range> ranges_to_remove = new Stack<FileManip.Range>();
       while((tok = fms.nextToken()) != null){
           //just a quick check for comments.
           if(tok.isAllOfType(COMMENT_TYPE)){
               if(tok.text.length() > 2 && tok.text.charAt(2)=='@'){
                   //this is an assertive statement. We should keep it
               } else{
                   //this is not an assertive statement. we should remove it from main.
                   
                   ranges_to_remove.push(new FileManip.Range(tok.position, fms.getCurrentPos()));
                   //by the nature of the scanner, the current position is right 
                   //before we start scanning for the next token (at the end of 
                   //this token)
               }
           } else switch(tok.getTokenType()){
               //this is the very ground level for syntax, we have no 
               //expectations of what to come next.
               
               //otherwise we go by a case-by case basis for figuring out what 
               //to do.
               case WORD:
                   switch(tok.text){
                        case "struct":{
                            //we are in a struct environment.
                            
                            FileManip.TextPosition begin_position = tok.position;
                            
                            if((tok = fms.nextToken()) == null){
                                //we have encountered an unexpected end of the 
                                //file. We raise an error, then delete the token
                                
                                //we will handle all future errors like this 
                                //through  "assert false", as it should have 
                                //been handled here.
                                
                                ranges_to_remove.
                                    push(new FileManip.Range(
                                        begin_position,
                                        main.getEndPosition()
                                //remove from this position to the end of the 
                                //file.
                                    ));
                            }
                            //we are currently at the name of the struct.
                            //struct sadf_asfd { ... }
                            
                            //if this is not the case, we continue scanning till
                            //we reach the next {. then, we delete the 
                            //information in between the struct name (the first 
                            //tokan after struct) and the {.
                            
                            
                            break;
                        }case "typedef":{
                            break;
                        }default:
                   }
               case BRACE_LEFT:
                   
               case BRACE_RIGHT:
               case PARENTHESES_LEFT:
               case PARENTHESES_RIGHT:
           }
       }
       while(!ranges_to_remove.isEmpty()){
           main.delete(ranges_to_remove.pop());
       }
    }
    private static void grab_anonymous_syntax(FileManipScanner scanner, FileManipScanner.C0Token tok, Stack<FileManip.Range> ranges_to_remove, FileManip name){
        //we are in some piece of blank syntax. we record until 
        //the syntax finishes.
        FileManip.TextPosition syntax_begin = tok.position;
        int brace_count = 1;
        loop:while(true){
            if((tok = scanner.nextToken()) == null){
                //we managed to end the file before we could find 
                //an ending of the syntax. 
                //Make the internals, and delete till the end of 
                //the file.
                bad_methods.add(
                        new C0Method_completely_unparsed(name, null, null, 
                            main.subRange(new FileManip.Range(
                                syntax_begin.getLine(),
                                syntax_begin.getColumn()+1,
                                //right after the beginning of the 
                                //syntax till right before the end.
                                main.getEndPosition()
                        )))
                    );
                //remove all of it.
                ranges_to_remove.push(
                        new FileManip.Range(
                            syntax_begin,
                            main.getEndPosition()
                        ));
                break;
            }
            switch(tok.getTokenType()){
                case BRACE_RIGHT:{
                    brace_count--;
                    if(brace_count==0){
                        //we should exit. We add a new to the 
                        //bad_interfaces, delete from beginning till end, 
                        //then exit.


                        //this is just a block of syntax, so all we can 
                        //do is parse the internals.
                        bad_methods.add(
                                new C0Method_completely_unparsed(name, null, null, 
                                    main.subRange(new FileManip.Range(
                                        syntax_begin.getLine(),
                                        syntax_begin.getColumn()+1,
                                        //right after the beginning of the 
                                        //syntax till right before the end.
                                        tok.position
                                )))
                            );
                        //remove the entire syntax.
                        ranges_to_remove.push(
                                new FileManip.Range(
                                    syntax_begin,
                                    tok.position.getLine(),
                                    tok.position.getColumn()+1
                                ));
                        break loop;
                    }
                    break;
                } case BRACE_LEFT:{
                    brace_count++;
                    break;
                }
            }
        }
    }
    private static void scan_scraps(){
        FileManipScanner scanner = new FileManipScanner(main);
        FileManipScanner.C0Token tok;
        while((tok = scanner.nextToken()) != null){
            //log error: unexpected token
        }
        main = null;
    }
    //here we scan and place unparsed methods and interfaces from the main file.
    //this is the last stage for grabbing raw data.
    private static void methods_and_interfaces(){
        
        //scan the file for methods and adds them to the trie of interfaces
        FileManipScanner scanner = new FileManipScanner(main);
        //we scan main.
        FileManipScanner.C0Token tok ;//=/;scanner.nextToken();
            //the current token.
        Stack<FileManip.Range> ranges_to_remove = new Stack<FileManip.Range>();
        while((tok = scanner.nextToken()) != null){
            switch(tok.getTokenType()){
                case BRACE_LEFT:{
                    //just grab the syntax that's in range.
                    //log error: expected return type, name, and paramters for 
                    //block of syntax
                    grab_anonymous_syntax(scanner, tok, ranges_to_remove, null);
                    break;
                }case WORD:{
                    //because we deleted all other typedefs, structs, etc., 
                    //we know that we should be in an interface or a method.
                    assert !tok.text.equals("struct");
                    assert !tok.text.equals("typedef");
                    
                    //we have found a word, so we should expect some arbitrary
                    //sequence of other words, *, [, or ]. This is followed by 
                    //a (, then some arbitrary words followed by ), then a 
                    //series of zero or more assertions, then {, then }
                    
                    FileManip.TextPosition begin_name = tok.position;
                    
                    name_loop:while(true){
//                        FileManipScanner.C0Token previous = tok;
                        if((tok = scanner.nextToken())==null){
                            break name_loop;
                            //we actually were not in some syntax, we actually
                            //were just in a chunk of text that did not have any
                            //parentheses
                        }
                        switch(tok.getTokenType()){
                            case BRACE_LEFT:{
                                //this is not a method (or at least not a very 
                                //good one). We just grab the internal syntax
                                //rather than creating a method.
                                grab_anonymous_syntax(scanner, tok, ranges_to_remove, main.subRange(new FileManip.Range(begin_name, scanner.getCurrentPos())));
                                //log error: expected paramters before a 
                                //block of syntax.
                                break name_loop;
                            } case PARENTHESES_LEFT:{
                                //should be the beginning of parameters.
                                FileManip name_and_return_t = main.subRange(new FileManip.Range(begin_name, tok.position));
                                FileManip params;
                                FileManip.TextPosition begin_params = new FileManip.TextPosition(tok.position.getLine(), tok.position.getColumn()+1);
                                List<FileManipScanner.C0Token> assertions = new ArrayList<>();
                                int parentheses_count = 1;
                                //we have a parentheses
                                ll:while(true){
                                    if((tok = scanner.nextToken())==null){
                                        break name_loop;
                                        //we actually were not in some syntax, we actually
                                        //were just in a chunk of text that had 
                                        //parentheses, but did not contain syntax
                                    }
                                    switch(tok.getTokenType()){
                                        case PARENTHESES_RIGHT: 
                                            parentheses_count--;
                                            if(parentheses_count==0){
                                                
                                                //semicolon?
                                                //assertions?
                                                params = main.subRange(new FileManip.Range(begin_params, tok.position));
                                                //the params
                                                while(true){
                                                    tok = scanner.nextToken();
                                                    if(tok == null){
                                                        //log error: expected {
                                                        break name_loop;
                                                        //break the name loop, these tokens will be left over to log errors over.
                                                    }
                                                    switch(tok.getTokenType()){
                                                        case BRACE_LEFT:{
                                                            //we enter a method 
                                                            tok = scanner.nextToken();//after the {
                                                            break ll;
                                                        } case SEMI_COLON:{
                                                            //we enter an interface
                                                            ranges_to_remove.push(
                                                            new FileManip.Range(
                                                                begin_name,
                                                                tok.position.getLine(),
                                                                tok.position.getColumn()+1
                                                            ));
                                                            //we add it to the list of interfaces and exit.
                                                            unparsed_interfaces.add(new C0Interface_completely_unparsed(name_and_return_t, params, assertions));
                                                            
                                                            break name_loop;
                                                        } case BLOCK_COMMENT:case LINE_COMMENT:{
                                                            //undefined yet. we add too the list of assertions, and go in a loop
                                                            assertions.add(tok);
                                                            break;
                                                        } default: 
                                                            //throw error: expected {, ;, or an assertion
                                                            break name_loop;
                                                    }
                                                }
                                                
                                            }
                                            break;
                                        case PARENTHESES_LEFT: 
                                            parentheses_count++;
                                            break;
                                        case BRACE_LEFT:
                                            //log error: ) expected
                                            params = main.subRange(new FileManip.Range(begin_params, tok.position.getLine(), tok.position.getColumn()+1));
                                            break ll;
                                        case BRACE_RIGHT: assert false;
                                        
                                    }
                                }
                                
                                FileManip.TextPosition syntax_begin = tok.position;
                                
                                
//                                System.out.println(name_and_return_t);
//                                System.out.println(params);
                                int brace_count = 1;
                                loop:while(true){
                                    if((tok = scanner.nextToken()) == null){
                                        //we managed to end the file before we could find 
                                        //an ending of the syntax. 
                                        //Make the internals, and delete till the end of 
                                        //the file.
                                        
                                        //throw error
                                        unparsed_methods.add(
                                                new C0Method_completely_unparsed(name_and_return_t, params, assertions, 
                                                    main.subRange(new FileManip.Range(
                                                        syntax_begin.getLine(),
                                                        syntax_begin.getColumn()+1,
                                                        //right after the beginning of the 
                                                        //syntax till right before the end.
                                                        main.getEndPosition()
                                                )))
                                            );
                                        //remove all of it.
                                        ranges_to_remove.push(
                                                new FileManip.Range(
                                                    begin_name,
                                                    main.getEndPosition()
                                                ));
                                        break;
                                    }
                                    switch(tok.getTokenType()){
                                        case BRACE_RIGHT:{
                                            brace_count--;
                                            if(brace_count==0){
                                                //we should exit. We add a new to the 
                                                //bad_interfaces, delete from beginning till end, 
                                                //then exit.


                                                //this is just a block of syntax, so all we can 
                                                //do is parse the internals.
                                                unparsed_methods.add(
                                                        new C0Method_completely_unparsed(name_and_return_t, params, assertions, 
                                                            main.subRange(new FileManip.Range(
                                                                syntax_begin.getLine(),
                                                                syntax_begin.getColumn()+1,
                                                                //right after the beginning of the 
                                                                //syntax till right before the end.
                                                                tok.position
                                                        )))
                                                    );
                                                //remove the entire syntax.
                                                ranges_to_remove.push(
                                                        new FileManip.Range(
                                                            begin_name,
                                                            tok.position.getLine(),
                                                            tok.position.getColumn()+1
                                                        ));
                                                break loop;
                                            }
                                            break;
                                        } case BRACE_LEFT:{
                                            brace_count++;
                                            break;
                                        }
                                    }
                                }
                                
                                break;
                            }case BRACE_RIGHT: assert false;
                                //cannot happen; we should be outside of all 
                                //code blocks
                        }
                    }
                    break;
                }
                //all other cases we leave to mark with errors, if anything is 
                //left over.
            }
        }
       while(!ranges_to_remove.isEmpty()){
           main.delete(ranges_to_remove.pop());
       }
    }
    //should input a valid c0 type.
    private static C0Data parse_c0_type(TokenList info){     
        C0Data base_type = null;
        FileManipScanner.C0Token tok;
        boolean is_void = false;
        stage1: while((tok = info.next()) != null){//iterable!
            switch(tok.text){
                case "struct":{
                    if((tok = info.next()) == null){
                        //throw error: struct needs a name
                        info.rewind(); return null;
                    }
                    C0Struct c0s;
                    {
                        String struct_name = tok.text;
                        if((c0s = structs_unparsed.get(struct_name)) != null
                                || (c0s = structs_parsed.get(struct_name)) != null){
                            //c0s should be defined and non-null
                            
                            assert c0s != null;
                            
                            base_type = c0s;
                            break stage1;
                        } else{
                            //throw error; we could not find the base type.
                            info.rewind(); return null;
                        }
                    }
                }case "int":{
                    base_type = Types.INT;
                    break stage1;
                }case "bool":{
                    base_type = Types.BOOL;
                    break stage1;
                }case "string":{
                    base_type = Types.STRI;
                    break stage1;
                }case "char":{
                    base_type = Types.CHAR;
                    break stage1;
                }case "void":{
                    base_type = Types.VOID;
                    is_void = true;
                    break stage1;
                } default:{
                    C0TypeDef_unparsed ctd = typedefs_unparsed.get(tok.text);
                    if(ctd != null){
                        //we don't hae atypedef already parsed.
                        base_type = parse_c0_type(new TokenList(ctd.data));

                        //we should optimize this later to remove things that 
                        //are already parsed.
                        
                        
                        //we will not go into an infinite loop, as we made sure,
                        //while looking up the typedef, that the typedef was not 
                        //already defined.
                        break stage1;
                    } else{
                        C0TypeDef_parsed ct = typedefs_parsed.get(tok.text);
                        if(ct != null){
                             base_type = ct.type;break stage1;
                        }
                        else{
                            //throw error
                            info.rewind(); return null;
                        }
                    }
                }
            }
        }
        //we exit stage 1 knowing what our base type is.
        assert base_type != null;
        
        //we then look for any extra modifications.
        
        boolean brak = false;//the number of brakets
        while((tok = info.next()) != null){
            //should never execute if the type is null
            if(is_void) {
                //throw an error: unkown data type
                info.rewind();return null;
            }
            switch(tok.getTokenType()){
                case BRACKET_LEFT:{//   [
                     if(brak){
                        //throw error
                        info.rewind();return null;
                    } brak = true; break;
                }case BRACKET_RIGHT:{// ]
                    if(!brak){
                        //throw error
                        info.rewind();return null;
                    } brak = false; base_type = Types.ARRAY(base_type); break;
                }case MULTIPLY | POINTER_TYPE:{// *
                    if(brak){
                        //throw error
                        info.rewind();return null;
                    } base_type = Types.POINTER(base_type); break;
                }default:{
                    //throw error
                    //should be only *, [, or ] after a type.
                    info.rewind();return null;
                }
            }
        }
        
        info.rewind();//goes back to the list
        return base_type;
    }
    private static void parse_typedefs(){
        for(Map.Entry<String, C0TypeDef_unparsed>  e: typedefs_unparsed.entrySet()){
            C0Data val = parse_c0_type(new TokenList(e.getValue().data));
            typedefs_parsed.put(e.getKey(), new C0TypeDef_parsed(e.getKey(), val));
        }
        typedefs_unparsed.clear();
    }
    private static void typedefs(){
        //   Next, we search for typedefs. If the typedef infringes on 
        //   a name (like using a reserved word or the name of a struct), or if the 
        //   typedef uses an unknown name, or if the typedef gives something which 
        //   is an illegal name (illegal characters for a name), we flag an error, 
        //   record the position, and continue. We add the typedef to the HashSet 
        //   typedefs if the typedef was correct. We then delete the typedef from 
        //   the file we are analyzing. 
        
        
        FileManipScanner fms = new FileManipScanner(main);
        //we start scanning for typedefs.
        
        //we have already removed all structs from the main file, and have put 
        //them into storage to be parsed
        
        FileManipScanner.C0Token tok;
        int brace_count = 0; int parentheses_count = 0;
        Stack<FileManip.Range> ranges_to_remove = new Stack<FileManip.Range>();
        while((tok = fms.nextToken()) != null){
            switch(tok.getTokenType()){
                  case BRACE_LEFT:{
                      //we entered in some syntax {
                    brace_count++;
                    break;
                } case BRACE_RIGHT:{
                    //we exited some syntax }
                    brace_count--;
                    if(brace_count < 0){
                        assert false;
                            //should not be happening. we already checked this.
                    }
                    break;
                } case PARENTHESES_LEFT:{
                    if(brace_count == 0)parentheses_count++;
                    break;
                } case PARENTHESES_RIGHT:{
                    if(brace_count == 0){
                        parentheses_count--;
                        if(parentheses_count < 0){
                            parentheses_count++;
//                            assert false;
                        }
                    }
                    break;
                }
            }
            
            if(brace_count == 0 && parentheses_count ==  0 && tok.getTokenType() == WORD && tok.text.equals("typedef")) fail:{
                //we found a typedef (in the top-level syntax, as well; and 
                //outside of any parentheses)
                
                FileManip.TextPosition begin_delete = tok.position;
                    //the beginning of the deletion point, right before the word 
                    //typedef.
                
                if((tok = fms.nextToken()) == null){
                    //unexpected end of file
                    //(file ended while parsing typedef)
                    ranges_to_remove.push(new FileManip.Range(begin_delete, begin_delete.getLine(), begin_delete.getColumn()+7));
                    //delete the token from main.
                    break fail;
                }
                FileManip.TextPosition begin_body = tok.position;
                //typedef A... B;
                //like typedef struct asdf * B;
                //B is one valid word.
                //A... must be of a valid type.
                
                //we parse the types later (in case there is a typedef C A, 
                //then a typedef B C)
                
                //here, we just grab the info from the beginning of the first 
                //token (after typedef), till the next semicolon (;). We delete
                //from the beginning of the word typedef till after the 
                //semicolon.
                boolean first_run = true;
                dw:do{
                    FileManipScanner.C0Token tok_pre = tok;
                    if((tok = fms.nextToken()) == null){
                        //unexpected end of file (file ended while parsing typedef). Expected ";"
                        ranges_to_remove.push(new FileManip.Range(begin_delete, main.getEndPosition()));
                        //delete the token from main.
                        break fail;
                    }
                    
                    //something here about {, (, }, and )
                    switch(tok.getTokenType()){
                        case BRACE_LEFT:{
                            //this means we're now infringing upon some other
                            //block of syntax. The user likely forgot a ";". We 
                            //exit this typedef, and then delete the info from 
                            //the brginning of the typedef till where the { 
                            //began.
                            
                            ranges_to_remove.push(
                                    new FileManip.Range(
                                        begin_delete, tok.position
                                        //from right before the word typedef 
                                        //till right  before the beginning of 
                                        //the {.
                                    ));
                            //log error: expected ;
                            break fail;
                        }
                        case BRACE_RIGHT:{
                            assert false : "internal error: expected removal of }.";
                                //this means that we are outside of syntax, and
                                //there is an unexpected }. We supposedly 
                                //removed all of these.
                        }
                        //we do not break on parentheses. We delete them, and 
                        //instead parse it with the token. (Sounds silly, right?)
                        //the reasoning behind this is because if the programmer
                        //set up the next block of syntax as a method, we will
                        //just analyze the internals of the method (in between {})
                        //Otherwise, by the method we're using to parse, we 
                        //wouldn't know when to start the method!
                            
                        case SEMI_COLON:{
                            ranges_to_remove.push(new FileManip.Range(begin_delete, tok.position.getLine(), tok.position.getColumn()+1));
                            if(first_run){
                                //throw an error: typedefs can't be empty (no 
                                //typedef;)
                                break dw;
                            }
                            if(!(tok_pre.getTokenType() == WORD)){
                                //log error: invalid typedef name: 
                                break dw;
                            }
                            if(reserved_words.contains(tok_pre.text)){
                                //log error: typedef name "tok_pre.text" is a reserved word.
                                break dw;
                            } if(typedefs_unparsed.containsKey(tok_pre.text)){
                                //log error: typedef can only be named once.
                                break dw;
                            }
                            if(struct_interfaces.containsKey(tok_pre.text)){
                                //log error: typedef name "tok_pre.text" is already the name of a struct.
                                break dw;
                            }
                            if(structs_unparsed.get(tok_pre.text) != null){
                                //log error: typedef name "tok_pre.text" is already the name of a struct.
                                break dw;
                            }
                            for(C0Struct cos : bad_structs)
                                if(cos.struct_name.equals(tok_pre.text)){
                                    //log error: typedef name "tok_pre.text" is already the name of a struct.
                                    break dw;
                                }
                            //put a new unparsed typedef.
                            typedefs_unparsed.put(
                                    tok_pre.text,
                                    new C0TypeDef_unparsed(tok_pre.text, main.subRange(new FileManip.Range(begin_body, tok_pre.position)))
                                );
                            break dw;
                        }
                    }
                    first_run = false;
                }while(true);
            }
        }
        while(!ranges_to_remove.isEmpty()){
            main.delete(ranges_to_remove.pop());
        }
    }
    private static void c0ToJava(){
    }
    private static void parse_interfaces(){
    }
    private static void parse_methods(){
    }
    private static void parse_structs(){
        for(C0Struct_unparsed e: structs_unparsed.list()){
            TokenList tl = new TokenList(e.internals);
            FileManipScanner.C0Token tok;
            while((tok = tl.next()) != null){
                
            }
        }
    }
    
    //operations on the data types
    static void putInterface(C0Interface coi){
        assert coi != null;
        interfaces.put(coi.name, coi);
    }
//    static String current_token = "";
    static FileManip main = null;
    static FileManipScanner scanner = null;
    
    //returns the java code
    public static String compile(File source_root, String file_name, String... files_to_compile){
        if(files_to_compile.length == 0) return "";
        main = FileManip.generateEmpty(file_name);
        //File source_root
        reserved_words = new HashSet<String>();
        //add on the basic used names.
        
        //types
        reserved_words.add("bool");
        reserved_words.add("int");
        reserved_words.add("char");
        reserved_words.add("string");
        reserved_words.add("struct");
        reserved_words.add("typedef");
        reserved_words.add("void");
        
        //standard static final variables
        reserved_words.add("NULL");
        reserved_words.add("true");
        reserved_words.add("false");
        
        //operations on a block of syntax
        reserved_words.add("for");
        reserved_words.add("while");
        reserved_words.add("switch");
        reserved_words.add("if");
        reserved_words.add("else");
        
        //keywords used in a block of syntax
        reserved_words.add("return");
        reserved_words.add("break");
        reserved_words.add("continue");
        reserved_words.add("assert");
        
        //methods
        reserved_words.add("alloc");
        reserved_words.add("alloc_array");
        
        methods = new Trie<C0Method_unparsed>();
        //alloc and alloc_array maybe? I think I'll check these in the
        
        native_imports = new HashSet<Integer>();
        
        interfaces = new HashMap<String, C0Interface>();
        
        //add the main function
        putInterface(new C0Interface(new C0Variable[]{}, Types.INT, "main"));
            //int main() function
        
        //the root's source
        CompileC0.source_root = source_root;
        //C0Token
        to_import = new Queue<Tuple2<String, Integer>>();
        for(String fil: files_to_compile){
            assert fil != null;
            Tuple2<String, Integer> ts = new Tuple2<String, Integer>(); ts.a = fil; ts.b = -1;
            to_import.enq(ts);
        }
        
        coffee.brew();
        
        System.out.println("finding imports:");
        imports(); 
            //find imports, initialize the "main" FileManip variable, and put 
            //all of the files into one large file (so they could all be 
            //analyzed together, like C0)
        
        System.out.println("finding structs:");
        
        //initialize the objects that hold the structs that will be parsed later
        //on
        structs_unparsed = new Trie<C0Struct_unparsed>();
        struct_interfaces = new HashMap<String, C0Struct_interface>();
        bad_structs = new ArrayList<C0Struct>();
        
        scan_for_consistancy();
        
        structs();      
            //find structs. Unparsed structs are structs that have a body, but 
            //we do not want to parse them just yet in case there is a typedef 
            //that is used later or a struct that is used later.
        
        System.out.println("finding typedefs:");
        typedefs_unparsed = new HashMap<String, C0TypeDef_unparsed>();
        typedefs();
            //find typedefs. We do not parse the typedef actual types, in case 
            //there is a typedef that points to a later typedef. We will parse 
            //the names here, and parse the actual type later.
        unparsed_methods = new ArrayList<>();
        unparsed_interfaces = new ArrayList<>();
        bad_methods = new ArrayList<>();
        methods_and_interfaces();
            //find interfaces. We scan for these separate from the methods in 
            //case the user has an interface that has certain parameters, but 
            //the user has a later implementation of the method (or a method 
            //under the same name) that has diffent parameters. C0 does not 
            //(currently) have polymorphism. I will not implement it in the J0,
            //as the purpose of C0 is to keep it "simple", so we will not 
            //confuse a person if the C0 code compiles correctly but the methods
            //do not work.       
            //find methods. If there is an interface under the name that we are 
            //scanning, we want to mark the interface as "completed". If there 
            //are any interfaces left that are not completed, we will flag an 
            //error and say that there are some unfilled interfaces.
        
            //we do not scan the internals of the method, in case the method is 
            //used later on.
//        System.out.println(unparsed_methods);
//        System.out.println(unparsed_interfaces);
//        System.out.println(bad_methods);
        //all of the leftovers. Should be empty if the file is correct.
        scan_scraps();
        
        structs_parsed = new Trie<>();
        parse_structs();//parse the info within the structs.
            //convert typedefs, C0 keywords to Java keywords as we go.
        
        typedefs_parsed = new HashMap<>();
        parse_typedefs();
        
        parse_interfaces();
        
        parse_methods();//parse the info within the methods.
            //convert typedefs, and C0 keywords to Java keywords as we go.
        
        c0ToJava();
            //take our information we gathered about structs, interfaces, 
            //imports, pointers, methods, etc, and put it into one large java 
            //file.
        return main + "";
    }
}


//REPRESENTATION OF C0 DATA*****************************************************

//we have a way of knowing the inputs and outputs of a function, but we do not 
//have a way of analyzing the data within a function. Here's an idea of what we 
//could do: (we already have the String body in the c0 method, and String assert
//statements.)

/**Variables - data in c0 represented with a name*/


class Test{
    public static void main(String[] args){
        CompileC0.compile(new File("./src/"), "a", "Test2.c0");
//        System.out.println("##"+);
        
    }
}