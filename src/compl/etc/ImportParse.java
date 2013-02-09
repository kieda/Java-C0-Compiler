/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

import org.kieda.util.k_error.K_Error;
import compl.data.FileManip;
import compl.data.FileManipScanner;
import org.kieda.util.k_error.Err;
import org.kieda.util.k_error.ExitableError;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/**
 * @Version .02
 * @author kieda
 */
class TypeDef_replace{
    
    public TypeDef_replace(C0TypeDef_parsed orig, String new_name) {
        this.orig = orig;
        this.new_name = new_name;
    }
    final C0TypeDef_parsed orig;
    final String    new_name;
    @Override public boolean equals(Object other){
        if(other instanceof TypeDef_replace){
            return this.orig.equals(other);
        }
        return this.orig.equals(other);
    } @Override public int hashCode(){
        return orig.hashCode();
    }
}
/**
 * A class dedicated to checking the IML ("import markup language") file for 
 * correctness, and for importing native c0 libraries.
 */
public class ImportParse {
    /**
     * represents an error in the import markup language
     */
    public static class IMLDetails extends Parsers.ParseErrorDetails{
        public static String source;
        protected IMLDetails(String error, String text, FileManip.TextPosition pos){
            super(error, text, source, pos);
        }
        public void introError(){
            System.err.println("error in parsing IML (import markup language), file \""+ getSource()+"\"");
        }
        
    }
    /**
     * represents a "packet" of information extracted when parsing the IML
     */
    public static class ImportPackage{
        /**
         * the native C0 structs that should be removed after checking the C0 
         * code for correctness, and parsing the C0 code.
         * 
         * key is the name of the struct, value is the struct itself
         */
        public HashMap<String,C0Struct> native_c0_structs__Remove;
        
        /**
         * the native C0 structs that should be kept after checking the C0 code 
         * for correctness, and parsing the C0 code.
         * (i.e. the struct will be converted to a class)
         * 
         * key is the name of the struct, value is the struct itself
         */
        public HashMap<String,C0Struct> native_c0_structs__Keep;
        
        /**
         * the typedefs that will be used while parsing the IML and while 
         * checking/parsing the C0 code.
         * 
         * key is the new type, value is the typedef
         */
        public HashMap<String,C0TypeDef_parsed> native_c0_typedefs;
        
        /**
         * these typedefs will be used while checking the C0 code for 
         * correctness, but while parsing the C0 code typedefs under the given
         * replace will be replaced by the string in the Typedef_replace.
         * 
         * key is the new type, value is the typedef with the name it will be 
         * replaced by.
         */
        public HashMap<String,TypeDef_replace> native_c0_typedefs__Replace;
        
        /**
         * these are native methods with a body written in C0. The body will
         * be parsed after the C0 code is parsed, and will be inserted into the 
         * final code.
         * 
         * Currently unsupported.
         */
        public HashMap<String,C0Method_unparsed> native_c0_methods__Keep;
        
        /**
         * might be useless.
         * 
         * Currently unsupported.
         */
        public HashMap<String,C0Method_unparsed> native_c0_methods__Remove;
        
        /**
         * these are C0 interfaces that the client can use and the compiler will
         * recognize while checking the C0 code for correctness. This is 
         * supposed to be written in combination with a java method under the 
         * same name, and the java equivalent for parameter types and return 
         * types for the C0 interface.
         * 
         * key is the interface name, value is the interface itself
         */
        public HashMap<String,C0Interface> native_c0_interfaces__Remove;
        
        /**
         * these interfaces are kept after checking the code for correctness, 
         * meaning that there should not be a java equivalent for this 
         * interface. Used if you want to "force" the user to fill in an 
         * interface by importing a library (like a hash function)
         * 
         * key is the interface name, value is the interface itself
         */
        public HashMap<String,C0Interface> native_c0_interfaces__Keep;
        
        /**
         * pointers that will be used for the java-implementation side of 
         * pointers. Will add these pointers (internal classes) to the final 
         * java class.
         * 
         * i.e. we load bool* to be
         *  class $bool$1 {boolean p;}
         * 
         * key is the string pointer, and the key is the pointer itself
         */
        public HashMap<String,Point> native_c0_pointers__Keep;
        
        /**
         * pointers that will be used for the java-implementation side of 
         * pointers. These pointers (internal classes) will NOT be added to the 
         * final java class.
         * 
         * i.e. we load bool* to be
         *  class $bool$1 {boolean p;}
         * 
         * key is the string pointer, and the key is the pointer itself
         */
        public HashMap<String,Point> native_c0_pointers__Remove;
        
        /**
         * a string of java methods that will be put into the final C0 file.
         */
        public String java_import;
        
        /**
         * A list of errors that occurred while parsing the IML. If no errors 
         * occured, this should be null. If not, this will contain the list of 
         * errors, and all other fields will be null.
         * 
         * check if this is null before parsing anything else.
         */
        public List<IMLDetails> errors = null;
        
        /**
         * we add an error on. If we don't have any errors yet, we instantiate
         * the list of errors.
         */
        private void addError(IMLDetails e){
            if(errors == null){
                errors = new ArrayList<IMLDetails>();
            } 
            errors.add(e);
        }
        /**
         * we call this at the end of parsing the IML. We don't want to set the
         * internal values when we might ad something to one of these variables.
         * So, instead, we see if an error has been added yet, and if so, we set
         * all parsed values to null.
         */
        private void close(){
            if(!iml_flag){
                native_c0_interfaces__Keep = null;
                native_c0_interfaces__Remove= null;
                native_c0_methods__Keep= null;
                native_c0_methods__Remove= null;
                native_c0_pointers__Keep= null;
                native_c0_pointers__Remove= null;
                native_c0_structs__Keep= null;
                native_c0_structs__Remove= null;
                native_c0_typedefs= null;
                native_c0_typedefs__Replace= null;
                java_import= null;
            }
        }
    }
    
    private static HashMap<String,C0Struct> native_c0_structs__Remove;
            
                //native C0 structs that we will want to keep after cheking 
                //the C0 code for correctness. The struct will be imported as
                //a whole into the C0 code, and converted into a java class.
    private static HashMap<String,C0Struct> native_c0_structs__Keep;
    private static HashMap<String,C0TypeDef_parsed> native_c0_typedefs;
                //want to hash on the new name.
            
    
    private static HashMap<String,TypeDef_replace> native_c0_typedefs__Replace;
    private static String[] ALL_IMPORT_LIBS = new String[]{"string", "parse", "img", "file", "conio", "args"};
    
    private static List<IMLDetails> errors;
    
    public static String[] getAllImportLibs(){
        return ALL_IMPORT_LIBS;
    }
    public static void setAllImportLibs(String[] all_libs){
        ALL_IMPORT_LIBS = all_libs;
    }
    /**
     * returns the closest number to -1 without going below zero, between two 
     * integers a and b. In the case that both numbers are -1, the function 
     * returns -1.
     */
    static int clos(int a, int b){
        return (a==-1)?b:((b==-1)?a:(a <= b) ? a : b);
    }
    
    private static String token;
    private static FileManip.TextPosition token_position;
    private static FileManipScanner string_scan;
    private static void subs(int i){
        token = substring(token, i, token_position);
        ce();
    }
    /**stands for "check end"*/
    private static void ce(){
        if(token.isEmpty()){
            _assert(string_scan.hasNext(),"unexpected end of file", string_scan.finalPos());
            {FileManipScanner.Word werd = string_scan.next(); 
            token += werd.text; token_position = werd.position;}
        }
    }
    private static void tokBeg(String beg){
        _assert(token.startsWith(beg), "expected \"" + beg+"\", instead received " + token, token_position);
    }
    
    private static FileManip fm;
    /**
     * test-parsing of the native_fx file.
     */
    public static ImportPackage parseIML(String filepath, String[] imports){
        File f = new File(filepath);//"./src/include/native_fx");
        IMLDetails.source = f.getPath();
        Err._assert(f.canRead(), URGENT_IML_ERROR_S, "IML filepath is unreadable");
        
        fm = new FileManip(f);
        Err._assert(fm.getLine(0).equals("/**@@__@@_I__LIKE_COFFEE!__%%%%%%%%%__COFFEE_IS_A_GOOD_DRINK_TO_DRINK!_@@__@@**/"), "the first line does not contain the magic string");
        /**
        * remove all comments from the file. Works in all situations (text 
        * in a string or character block is not recognized as a comment; 
        * comments within comments are not recognized as comments. Done by
        * a "blocking" technique by beginning and terminating blocks.)
        **/
        {
        String end_flag = null;
            //what is the string that will tereminate the end of the block?
        boolean block = false;
            //we're not currently in a block.
        String file = "";
            //the file we're creating and appending information to. Will 
            //replace the original file.
        //we run through all of the lines of the file.
        for(int i = 0; i < fm.getEndPosition().getLine(); i++){
            String l = fm.getLine(i);
                //the current line

            legs:for(;;){//analysis of blocks on a particular line

                if(block){
                    //if we're in a block, we should look for termination.
                    int pos = l.indexOf(end_flag);
                        //the position of the current end flag.

                    if(pos == -1){
                        //if we did not find the end point in our current 
                        //line
                        if(end_flag.equals("*/")){
                            //if we are in a block comment we want to skip 
                            //the line and not add the text to the file.

                            //We know that the current line we are on 
                            //is shortened such that the text till the 
                            //beginning of a block is cut out (see below), 
                            //and the previous information is added to the 
                            //file.

                            //So, by completely breaking the line, we don't 
                            //add in any information we do not need.

                            break legs;
                                //breaking legs exits the current line.
                        } else{
                            file += l;
                                //in the case we did not find the end point,
                                //and we are not in a block comment, we are 
                                //either in a single quote, a double quote,
                                //or a single line comment. We know, below,
                                //that when we enter the single line comment
                                //we add on the information before the line 
                                //comment, don't add any information from 
                                //the commented material, and exit the 
                                //current line without entering a new block.
                                //So, the only other possibilities are 
                                //single quotes or double quotes.
                                //So, as per the java/netbeans/something 
                                //standard that I've modeled my language 
                                //(somewhat, not really) out of, I want a 
                                //quote to end at the end of the line (even 
                                //if there are no exiting quotes)
                                //So, we add on the rest of the line and 
                                //exit the current line
                            break legs;
                        }//only two options, nothing else needed here.
                    } else{
                        //in the other case, we are in a block, and we found 
                        //the end of the block in the currrent line.

                        //Here, we add on "parts" of the line to the file,
                        //and make the line shorter (in case if there are 
                        //multiple blocks on the same line.)

                        //In the case we were in a block comment, we don't 
                        //want to add the information before the block ends.
                        if(end_flag.equals("*/")){
                            l = l.substring(pos+2, l.length());
                            //the current line is now reduced by removing 
                            //the information in the comment. We also want 
                            //to remove the physical end of the block 
                            //comment, or "*/".length(), which is two.

                            block = false;  end_flag = null;
                            //we are no longer in a block, and there is no 
                            //end flag as we are not in a block.

                            //we do not add any information to the file, as 
                            //we are removing all comments.

                            if(l.isEmpty()){
                                //in the chance that the end of the comment
                                //reduced the line to nothing, we exit the 
                                //current line and continue.
                                break legs;
                            }
                        } else{
                            //in the other case we are in a single quote or 
                            //double quote. (We will not be in a single line 
                            //comment, as explained previously.)
                            //Both the single quote and the double quotes
                            //are one character long. So, we copy till past 
                            //the end of the block (including the quote!)
                            file += l.substring(0, pos+1);

                            //we want to check that we won't be exiting too
                            //early by an escape character in the string.
                            //If that happened, there could be room for 
                            //errors (like in the string "hello \" /* world"
                            //could be viewed as everything after the /* as 
                            //a comment, because it exited prematurely after
                            //the escape character \")
                            if(pos>0 && l.length()>= pos 
                                    //a range check to make sure that we 
                                    //won't be getting into any "funky" 
                                    //business.
                                    && l.charAt(pos-1)=='\\'){
                                    //if the previous conditions are 
                                    //satisfied for the range, and the 
                                    //previous character has the escape 
                                    //sequence, then we know that we should 
                                    //not exit the block.

                                l = l.substring(pos+1, l.length());
                                //we add on the block and continue on the 
                                //line.
                                continue legs;
                            }
                            //now, we know that we are either ending a 
                            //single quote or a double quote, and we are not 
                            //in an escape character sequence. So, we exit 
                            //the corrent block, add it onto the line, and 
                            //continue analyzing the rest of the line.

                            //we grab till the character after the end of 
                            //the quote (one char long)
                            l = l.substring(pos+1, l.length());

                            //we are no longer in a block and we no longer 
                            //need an end flag.
                            block = false;  end_flag = null;

                            //in the case exiting brought us to the end of 
                            //the line, start analyzing the next line.
                            if(l.isEmpty()){
                                break legs;//exit this line
                            }
                        }
                    }
                } else{
                    //the positions where each of our "blocks" could be in 
                    //the current line. -1 is that the block is not in the 
                    //line.
                    int pos_line_comm = l.indexOf("//");
                    int pos_block_comm = l.indexOf("/*");
                    int pos_doub_quote = l.indexOf("\"");
                    int pos_sing_quote = l.indexOf("\'");

                    int winner = clos(clos(pos_block_comm, pos_doub_quote), clos(pos_line_comm, pos_sing_quote));
                    //the block we should enter is the very first one, or 
                    //the one that is closest to 0, not -1 (or -1 if no one 
                    //is on the line.)

                    if(winner == -1){
                        //there is no winner (no blocks began)
                        file+=l;
                        //just add on the rest of the line. We know this 
                        //will work as the current line is reduced such that
                        //there are no comments on the line, and we're not 
                        //currently in a block so we can add on the line.
                        break legs;
                        //exit the current line
                    } else if(winner == pos_line_comm){
                        //in this case we begin a single line comment
                        //we add in the text till the beginning of the 
                        //single line.
                        file += l.substring(0, winner);
                        l = l.substring(winner + 2);
                        //we then take the substring of the rest of the line
                        block = false;
                        //because this language completely eliminates all 
                        //single line comments, we delete till the end of 
                        //the line; i.e. don't add in the information on 
                        //this line and go till the next line.
                        break legs;
                    } else if(winner == pos_block_comm){
                        //in this case we start a block comment. 
                        file += l.substring(0, winner);
                        //We add in information before the bock comment but 
                        //not after the block comment.

                        l = l.substring(winner + 2);
                        //and shorten the string to the information after 
                        //the block comment began.

                        end_flag = "*/";
                        //we then begin to look for the end of a block 
                        //comment, */

                        block = true;
                        //we are now in a block.

                        if(l.isEmpty()){//if we're at the end of the line,
                            break legs;//leave the line.
                        }
                    } else if(winner == pos_doub_quote){
                        //in this case we start a double quote.
                        file += l.substring(0, winner);
                        //add in the information till just before the double 
                        //quote
                        file+="\"";//add in the double quote

                        l = l.substring(winner + 1);
                        //we reduce the line till the information just after
                        //the double quote

                        end_flag = "\"";
                        //we search for a double quote to end the block.

                        if(l.isEmpty()){
                            block = false;
                            end_flag = null;
                            //if we end the line at the beginning of the 
                            //double quote immediately exit the block (as 
                            //per some random standard)
                            break legs;
                        }
                        block = true;
                        //otherwise, we're in a block and so we begin a 
                        //search for the ending of the block.
                    } else{ //pos_sing_quote
                        file += l.substring(0, winner);
                        //in this case we're in a single quote.
                        //pretty much the same as the double quote.
                        file+="\'";
                        end_flag = "\'";

                        l = l.substring(winner + 1);
                        if(l.isEmpty()){
                            block = false;
                            end_flag = null;
                            break legs;
                        }
                        block = true;
                    }
                }}

                file += "\n";
                //add on a new line at the end of the line.
        }
        //replace the entire range of the file manipulation with the new 
        //file.
        fm.replace(file, new FileManip.Range(fm.getBeginPosition(), fm.getEndPosition()));
        }

        //all comments, at this point, have been removed.

        //we then remove all lines that are filled with whitespace.
        //we do <=, as the getLine() returns the last index rather than the
        //size of the number of lines.
        for(int i = 0; i <= fm.getEndPosition().getLine(); i++){
            //the fm.getEndPosition().getLine() dynamically changes with 
            //every line removal, so there is no need to subract anything 
            //from the maximum.

            String l = fm.getLine(i);
            boolean flag = true;
            for(char c: l.toCharArray())//run through the entire line
                if(!Character.isWhitespace(c)){ flag = false; break;}
                //if a character in the line is not whitespace, then the
                //line is considered important and thus not deleted.
            if(flag) {
                fm.delete(i);
                i--;//we go back a line
            }
        }

        //now, at this point, all comments are removed and all excess 
        //whitespace over a single line has been removed. So, we know that 
        //the data on a line has some function beyond whitespace or a 
        //comment, so we can assume each line will be useful.

        //we then analyze each section of the code and add it to some 
        //data-structure.

        //We'll start out small by analyzing a single stucture (probably)
        //<string>. We'll load the C0 methods, then perform operations/
        //bullshit as requested, then we'll use the code as per the <string>
        //library.
        HashSet<String> known_libs = new HashSet<String>();
        HashSet<String> import_libs = new HashSet<String>();

        {

        known_libs.addAll(Arrays.asList(ALL_IMPORT_LIBS));
//        String[] imports = new String[]{"args", "conio"};
        import_libs.addAll(Arrays.asList(imports));
        }

        for(String lib: import_libs){
            Err._assert(known_libs.contains(lib), INIT_ERROR, "unknown lib: \""+ lib+"\"");
        }

        //the libs we want to include.

        /****we check the IML for correctness, and fill in the following***\
        \********variables as we go as part of the importing process*******/

        //BTW IL is the "import markup language"

        /*********the native methods from the #LOAD<METHOD>*****************/

            //the methods we should keep after we analyze the C0 code. 
            //Contains a field <body:____> which is placed into the C0 code 
            //itself. The ____ is not the body itself, but the local path to
            //a file that contains the body. (In order to prevent the code 
            //from exiting early by something in the C0 code)
        //C0Method... native_c0_methods__Keep
        HashMap<String, C0Method_unparsed> native_c0_methods__Keep = new HashMap<String,C0Method_unparsed>();
            //the methods we should remove after we analyze the C0 code. 
            //Contains a field <body:_____> which is placed into the C0 code
            //itself. The ____ is not the body itself, but the local path to
            //a file that contains the body. (In order to prevent the code 
            //from exiting early by something in the C0 code)
        //C0Method... native_c0_methods__Remove
        HashMap<String,C0Method_unparsed> native_c0_methods__Remove = new HashMap<String,C0Method_unparsed>();

        /*********the native methods from the #LOAD<INTERFACE>*************/


            //Interfaces that we want to remove after checking the C0 code 
            //for correctness. The type values, etc should match the java
            //equivalent of each method. I.e. a c0 method can have a string
            //param, and you must fill in a java method that will go in with
            //the import that takes type String.
        //C0Interface... native_c0_methods__Remove
        HashMap<String,C0Interface> native_c0_interfaces__Remove = new HashMap<String,C0Interface>();

            //Interfaces that we should keep in the C0 code. Ideal if we 
            //want to force the user to fill in a method by importing a 
            //certain library. Contains feilds <name:____> (for the name of 
            //the interface), <ret:_____> (for the return value of the 
            //interface), and <params:____, ____> (for the parameter types 
            //for the inputs of the function). Order does not matter on a 
            //function, but it does require all fields to be filled in.
        //C0Interface... native_c0_methods__Keep
        HashMap<String,C0Interface> native_c0_interfaces__Keep = new HashMap<String,C0Interface>();

            //native C0 structs that we will want to remove after cheking 
            //the C0 code for correctness.
        //C0Struct... native_c0_structs__Remove
        native_c0_structs__Remove = new HashMap<String,C0Struct>();

            //native C0 structs that we will want to keep after cheking 
            //the C0 code for correctness. The struct will be imported as
            //a whole into the C0 code, and converted into a java class.
        //C0Struct... native_c0_structs__Keep
        native_c0_structs__Keep = new HashMap<String,C0Struct>();

            //the pointers we want to create in the native 
        //C0Data...   native_c0_pointers__Keep
        HashMap<String,Point> native_c0_pointers__Keep = new HashMap<String,Point>();

        //C0Data...   native_c0_pointers__Remove
        HashMap<String,Point> native_c0_pointers__Remove = new HashMap<String,Point>();

        //C0Typedef... native_c0_typedefs
        native_c0_typedefs = new HashMap<String,C0TypeDef_parsed>();
            //want to hash on the new name.

        //C0Typedef... native_c0_typedefs__Replace
        native_c0_typedefs__Replace = new HashMap<String,TypeDef_replace>();

        errors = new ArrayList<IMLDetails>();
        
        String java_import = "";
        //we do NOT check the imported java methods/C0 methods for 
        //correctness
        {
        //it's important we have access to libs[], as we want to know if
        //we're looking for a library that sdoes not exist.

        boolean block_import = false;     
            //are we in an import block? 
            //(i.e. between a \\$$BEGIN<?> and \\$$END<?>)
        boolean block_pre = false;        
            //are we in a pre block? 
            //(i.e. between a \\$$PRE<?> and \\$$END<?>)
        boolean block_pre_HASH = false;   
            //are we in a loading block in the pre block? 
            //(i.e. between a \\#LOAD<?> and \\#END<?>)
        boolean will_import_lib = false;
            //should we import the library we are currently in?

        //we start in no block, so it's false by default.
//            FileManip fm_copy = fm.subRange(new FileManip.Range(fm.getBeginPosition(), fm.getEndPosition()));
//            Scanner string_scan = new Scanner(fm.getText(new FileManip.Range(fm.getBeginPosition(), fm.getEndPosition())));
        string_scan = new FileManipScanner(fm.copy());
        //we create a copy that we eventually reduce to nothing as we scan 
        //through the lines.
//            HashSet<String> next_expected_tokens = new HashSet<String>();


        String current_lib = null;//the current lib we're in.
        int load_val = -1;
        ArrayList<Object[]> c0_natives = new ArrayList<Object[]>();
        while(string_scan.hasNext()){
            token = null;
            token_position = null;

            {FileManipScanner.Word werd = string_scan.next(); 
            token = werd.text; token_position = werd.position;}
            tok:for(;;){
                    if(block_import){
                        //\\$$BEGIN
                        if(block_pre){
                            //\\$$PRE
                            if(block_pre_HASH){
                                if(token.startsWith("\\\\#")){
                                    subs(3);

                                    //can only be an end statement when 
                                    //you're done loading.
                                    tokBeg("END");
                                    subs(3);
                                    //#LOAD should have a single <KEEP>, 
                                    //<REMOVE> or <REPLACE> following
                                    tokBeg("<");
                                    subs(1);

                                    int action;
                                    String replace_info = null;
                                    //begin the end load sequence. Here we
                                    //add on information.
                                    if(token.startsWith("KEEP")){
                                        subs(4);
                                        action = 0;
                                    } else if (token.startsWith("REPLACE")){
                                        subs(7);
                                        action = 1;
                                        FileManip.TextPosition start_position = token_position;
                                            //the start position of the text we are getting.


                                        string_scan.set(token_position);
                                        FileManipScanner.Word werd = string_scan.next(">");

                                        token_position = new FileManip.TextPosition(werd.position.getLine(), werd.position.getColumn());
                                        string_scan.set(token_position);

                                        _assert(werd!=null,"expected \">\", instead unexpected end of file.");

                                        replace_info = fm.getText(new FileManip.Range(start_position, werd.position));
                                        token = string_scan.next().text;
                                        ce();
                                    } else if (token.startsWith("REMOVE")){
                                        action = 2;
                                        subs(6);
                                    } else{
                                        action = -1;
                                        _assert(false, "expected one of \"KEEP\", \"REPLACE\", or \"REMOVE\".");
                                    }

                                    //action
                                    //      0 keep
                                    //      1 replace
                                    //      2 remove
                                    //load_val
                                    //      0 interface
                                    //      1 method
                                    //      2 struct
                                    //      3 typedef
                                    //      4 pointer
                                    if(will_import_lib){
                                    HashMap c0_data;
                                    HashMap parser_data = null;
                                    switch(action*10 + load_val){
                                        case 00://keep interface
                                            c0_data = native_c0_interfaces__Keep;
                                            parser_data = Parsers.interfaces;
                                            break;
                                        case 02://keep struct
                                            c0_data = native_c0_structs__Keep;
                                            parser_data = Parsers.structs;
                                            break;
                                        case 03://keep typedef
                                        case 23://remove typedef
                                            c0_data = native_c0_typedefs;
                                            parser_data = Parsers.typedefs;
                                            break;
                                        case 04://keep pointer
                                            c0_data = native_c0_pointers__Keep;
                                            break;

                                        case 10://replace interface (cannot do)
                                        case 11://replace method (cannot do)
                                        case 12://replace struct (cannot do)
                                        case 14://replace pointer (cannot do)
                                            //error - cannot replace
                                            _assert(false, "cannot replace an interface, method, struct or pointer. currently the only replace available is typedef.");
                                            return null;
                                        case 01://keep method
                                        case 21://remove method
                                            //not supported
                                            _assert(false, "c0 methods are unsupported at the moment.");
                                            return null;
                                        case 22://remove struct
                                            c0_data = native_c0_structs__Remove;
                                            parser_data = Parsers.structs;
                                            break;
                                        case 13://replace typedef (can do)
                                            c0_data = native_c0_typedefs__Replace;
                                            parser_data = Parsers.typedefs;
                                            break;

                                        case 20://remove interface
                                            c0_data = native_c0_interfaces__Remove;
                                            break;

                                        case 24://remove pointer
                                            c0_data = native_c0_pointers__Remove;
                                            break;
                                        default:
                                            _assert(false, "internal error.");
                                            return null;
                                    }
                                    switch(load_val){
                                        case 0:{//INTERFACE
                                            while(!c0_natives.isEmpty()){
                                                String name;
                                                C0Data return_type;
                                                C0Data[] para;
                                                {
                                                String ret;
                                                ArrayList<String> params;
                                                {
                                                Object[] o = c0_natives.remove(0);
//                                                ASSERT(o.length==3, "internal error: expected three elements of an interface");
//                                                ASSERT(o[0] instanceof String, "internal error: expected 0th index of c0_native to be of type String");
//                                                ASSERT(o[1] instanceof String, "internal error: expected 1st index of c0_native to be of type String");
//                                                ASSERT(o[2] instanceof ArrayList, "internal error: expected 2nd index of c0_native to be of type ArrayList");
                                                name = (String)o[0];
                                                ret = (String)o[1];
                                                params = (ArrayList<String>)o[2];
                                                }
                                                return_type = Parsers.parse_c0_type(ret);
                                                para = new C0Data[params.size()];
                                                for(int i = 0; i < para.length; i++){
                                                    para[i] = Parsers.parse_c0_type(params.get(i));
                                                }
                                                }
                                                C0Interface addition = new C0Interface(para, return_type, name);
                                                c0_data.put(name, addition);
                                                if(parser_data != null) parser_data.put(name, addition);
                                            }
//                                                System.out.println(c0_data);
                                        } break;
//                                                case 1://METHOD
                                        case 2:{//STRUCT
                                            while(!c0_natives.isEmpty()){
                                                String name;
                                                C0Variable[] vars;
                                                FileManip manip_body;
                                                {String body; 
                                                {
                                                Object[] o = c0_natives.remove(0);
//                                                ASSERT(o.length==1, "internal error: expected one element of a struct");
//                                                ASSERT(o[0] instanceof String, "internal error: expected 0th index of c0_native to be of type String");
                                                body = (String)o[0];
//                                                    System.out.println(Arrays.toString(o) + "%%");
                                                } manip_body = FileManip.generate(body, "native_fx");
                                                }
                                                FileManipScanner struct_scan = new FileManipScanner(manip_body);
                                                
                                                _assert(struct_scan.hasNext(), "empty struct.");
                                                FileManipScanner.Word struct_token = struct_scan.next();
                                                
                                                _assert(struct_token.text.equals("struct"), "Expected \"struct\" as for the beginning of the struct.");

                                                _assert(struct_scan.hasNext(), "struct requires a name.");_assert(struct_scan.hasNext(), "struct requires a name.");
                                                struct_token = struct_scan.next();


                                                FileManip.TextPosition begin_name = struct_token.position;
//                                                    System.out.println(struct_token.text);

                                                //the beginning of the name (immediately after the struct)
                                                struct_token = struct_scan.next("{");//skip to the beginning of the body of the struct

                                                if(struct_token==null){
                                                    //a struct without a body.
                                                    struct_scan.set(begin_name);//go back to the beginning of the name
                                                    struct_token = struct_scan.next(";");
                                                    //scan for the ; at the end of the struct
                                                    _assert(struct_token != null, "expected \";\" at end of struct.");
                                                    name = manip_body.getText(new FileManip.Range(begin_name, struct_token.position)).trim();
//                                                        System.out.println(name);
                                                    C0Struct_parsed addition = new C0Struct_parsed(null, name);
                                                    c0_data.put(name, addition);
                                                    if(parser_data != null) parser_data.put(name, addition);
                                                        //the null for the c0 
                                                        //data types represents 
                                                        //a struct interface
                                                } else {
                                                    ArrayList<C0Variable> var_list = new ArrayList<C0Variable>();
                                                    FileManip.TextPosition end_name = struct_token.position;
                                                    name = manip_body.getText(new FileManip.Range(begin_name, end_name)).trim();
                                                        //the name is between the token after struct and the beginning of {
                                                    FileManip.TextPosition begin_body;
                                                    begin_body = new FileManip.TextPosition(end_name.getLine(), end_name.getColumn()+1);

                                                    struct_scan.set(begin_body);
                                                        //we set the position to immediately after the {


                                                    struct_token = struct_scan.next("}");//skip to the end of the struct
                                                    _assert(struct_token != null, "expected \"}\"");
                                                    String body = manip_body.getText(new FileManip.Range(begin_body, struct_token.position)).trim();
//                                                        System.out.println(body);
                                                    int i = 0;
                                                    for(;;){
                                                        String var_name;
                                                        C0Data var_type;

                                                        char[] cc = body.toCharArray();


                                                        String statement = "";

                                                        boolean in_white = false;
                                                        boolean in_statement = false;
                                                        for(int j = i;;j++){
                                                            char c =  cc[j];
                                                            i++;
                                                            if(c==';'){
                                                                in_statement = false;
                                                                break;
                                                            }else if(c=='\t' || c==' ' || c== '\n'|| c== '\r'){
                                                                if(j == cc.length-1)
                                                                    if(in_statement)
                                                                        _assert(false, "expected \";\"");
                                                                    else break;
                                                                if(!in_white)
                                                                    statement+=' ';
                                                                in_white = true;
                                                            } else{
                                                                if(j == cc.length-1) _assert(false, "expected \";\"");
                                                                in_white = false;
                                                                statement+=c;
                                                                in_statement = true;
                                                            }
                                                        }
                                                        statement = statement.trim();
//                                                            System.out.println(statement);
                                                        {
                                                            var_name = "";
                                                            Scanner scannn = new Scanner(statement);
                                                            String orig = "";
                                                            int count = 0;
                                                            while(scannn.hasNext()){
                                                                count++;
                                                                String nex = scannn.next();

                                                                var_name = nex;
                                                                orig += nex + " ";
                                                            }
                                                            _assert(count > 1, "expected more than one element in a struct");

                                                            //struct asdf* hello
                                                            //name = "hello"
                                                            //orig = "struct asdf hello "

                                                            orig = orig.substring(0, orig.length() - var_name.length()-1).trim();//include the trailing space
                                                            var_type = Parsers.parse_c0_type(orig);

                                                            var_list.add(Variables.create(var_type, var_name));

                                                        }

                                                        if(i == cc.length) break;
                                                        //name and variable in the current ;
                                                    }
                                                    vars = new C0Variable[var_list.size()];
                                                    for(int ff = 0; ff < vars.length; ff++){
                                                        vars[ff] = var_list.get(ff);
                                                    }
                                                    C0Struct_parsed addition = new C0Struct_parsed(vars, name);
                                                    c0_data.put(name, addition);
                                                    if(parser_data != null) parser_data.put(name, addition);
                                                    //analyze body here to find name, vars.

//                                                    System.out.println(body + "%%");


                                                }
                                            }
//                                                System.out.println(c0_data);
//                                                    native_c0_structs__Remove
                                        } break;
                                        case 3:{//TYPEDEF
                                            ArrayList<C0TypeDef_parsed> tds = new ArrayList<C0TypeDef_parsed>();


                                            while(!c0_natives.isEmpty()){
                                                Object[] o = c0_natives.remove(0);
                                                _assert(o.length==1, "internal error: expected one element of a typedef");
                                                _assert(o[0] instanceof String, "internal error: expected 0th index of c0_native to be of type String");
                                                String typed = (String)o[0];
                                                //the last index is the new name
                                                Scanner scannn = new Scanner(typed);
                                                String name = "";
                                                String orig = "";
                                                int count = 0;
                                                while(scannn.hasNext()){
                                                    count++;
                                                    String nex = scannn.next();
                                                    name = nex;
                                                    orig += nex + " ";
                                                }
                                                _assert(count > 1, "expected more than one token in a typedef");
                                                //struct asdf* hello
                                                //name = "hello"
                                                //orig = "struct asdf hello "
                                                orig = orig.substring(0, orig.length()-name.length()-1).trim();//include the trailing space

                                                C0TypeDef_parsed thiss = new C0TypeDef_parsed(name, Parsers.parse_c0_type(orig));
                                                tds.add(thiss);
                                            }
                                            if(replace_info != null){
                                                //go through and add all tds 
                                                //with the replace typedefs
                                                //that are specified in the
                                                //replace. Remove them from 
                                                //tds
                                                C0Data to_replace;
                                                String replace_name = "";
                                                {
                                                Scanner scannn = new Scanner(replace_info);
                                                String orig = "";
                                                int count = 0;
                                                while(scannn.hasNext()){
                                                    count++;
                                                    String nex = scannn.next();

                                                    replace_name = nex;
                                                    orig += nex + " ";
                                                }
                                                _assert(count > 1, "expected more than one token in a typedef");
                                                //struct asdf* hello
                                                //name = "hello"
                                                //orig = "struct asdf hello "

                                                orig = orig.substring(0, orig.length() - replace_name.length()-1).trim();//include the trailing space
                                                to_replace = Parsers.parse_c0_type(orig);
                                                }
                                                Object[] oo = tds.toArray();
                                                for(int i = 0; i < oo.length; i++){
                                                    C0TypeDef_parsed ty = (C0TypeDef_parsed)oo[i];
                                                    if(ty.type.equals(to_replace)){
                                                        native_c0_typedefs__Replace.put(ty.name, new TypeDef_replace(ty, replace_name));
                                                        if(parser_data != null) parser_data.put(ty.name, ty);
                                                        tds.set(i, null);
                                                    }
                                                }
                                            }
                                            while(!tds.isEmpty()){
                                                C0TypeDef_parsed ty = tds.remove(0);
                                                //add the rest.
                                                if(ty!=null){
                                                    c0_data.put(ty.name, ty);
                                                    if(parser_data != null) parser_data.put(ty.name, ty);
                                                }
                                            }
//                                                System.out.println(c0_data);
//                                                    native_c0_typedefs
                                        } break;
                                        case 4:{//POINTER
                                            while(!c0_natives.isEmpty()){
                                                C0Data pointer_to;
                                                {
                                                String type;
                                                {
                                                Object[] o = c0_natives.remove(0);
                                                _assert(o.length==1, "internal error: expected one element of a struct");
                                                _assert(o[0] instanceof String, "internal error: expected 0th index of c0_native to be of type String");
                                                type = (String)o[0];
                                                }
                                                pointer_to = Parsers.parse_c0_type(type);
                                                }
                                                Point finall = Types.POINTER(pointer_to);
                                                c0_data.put(finall.toString(), finall);
                                                if(parser_data != null) parser_data.put(finall.toString(), finall);
                                            }
//                                                System.out.println(c0_data+ ";;;;;;;;;;");
//                                                    native_c0_pointers__Keep
                                        } break;
                                    }
                                    }
                                    _assert(token.startsWith(">"), "expected \">\"", token, token_position);
                                    subs(1);
                                    block_pre_HASH = false;
                                    load_val = -1;
                                } else if(token.startsWith("<")){
                                    switch(load_val){
                                        case 0:{//INTERFACE
                                            ArrayList<String> hs = new ArrayList<String>();
                                            hs.add("name");
                                            hs.add("ret");
                                            hs.add("params");
                                            boolean start_any;
                                            Object[] addition = new Object[3];
                                            while(!hs.isEmpty()){
                                                start_any = false;
                                                ff:for(Object part: hs.toArray()){
                                                    _assert(token.startsWith("<"), "expected \"<\"", token, token_position);
                                                    subs(1);

                                                    if(token.startsWith((String)part)){
                                                        start_any = true;

                                                        //get contents...
                                                        subs(((String)part).length());

                                                        _assert(token.startsWith(":"), "expected \":\"",token, token_position);
                                                        subs(1);

                                                        //we know we have found a parameter with the name we need, and we are getting the information out of it.
                                                        //we don't know exactly when it will end. We go from the : to the next > token.

                                                        FileManip.TextPosition start_position = token_position;
                                                        //the start position of the text we are getting.


                                                        string_scan.set(token_position);
                                                        FileManipScanner.Word werd = string_scan.next(">");

                                                        token_position = new FileManip.TextPosition(werd.position.getLine(), werd.position.getColumn());
                                                        substring(null, 1, token_position);
                                                        string_scan.set(token_position);

                                                        _assert(werd!=null,"expected \">\", instead unexpected end of file.");

                                                        String info = fm.getText(new FileManip.Range(start_position, werd.position));

                                                            //could be name, ret, or param.
                                                        Scanner info_scan = new Scanner(info);

                                                        switch((String)part){
                                                            case "name":{
                                                                _assert(info_scan.hasNext(), "cannot have an empty string or whitespace as a C0 interface name");

                                                                String name = info_scan.next();
                                                                if(info_scan.hasNext())
                                                                    _assert(false, "too many tokens in name field : " + info_scan.next());
                                                                addition[0] = name;
                                                                break;
                                                            }case "ret":{
                                                                _assert(info_scan.hasNext(), "cannot have an empty string or whitespace as a C0 return value. Use void instead.");
                                                                String ret = info_scan.next();
                                                                if(info_scan.hasNext())
                                                                    _assert(false, "too many tokens in name field : " + info_scan.next());
                                                                addition[1] = ret;
                                                                break;
                                                            }case "params":{
                                                                ArrayList<String> params = new ArrayList<String>();
                                                                char[] cc;
                                                                {
                                                                String ii = "";
                                                                while(info_scan.hasNext()){
                                                                    ii += info_scan.next() + " ";
                                                                }

                                                                cc = ii.toCharArray();
                                                                }
                                                                int i = 0;
                                                                while(i<cc.length){
                                                                    info = "";
                                                                    while(i<cc.length && cc[i]!=','){
                                                                        info+= cc[i];
                                                                        i++;
                                                                    } 
                                                                    info = info.trim();
                                                                    _assert(!info.isEmpty(), "cannot have an empty string as a parameter");
                                                                    params.add(info);
                                                                    i++;
                                                                }
                                                                addition[2] = params;
                                                                break;
                                                            }
                                                        }

                                                        token = werd.text;
                                                        subs(1);
                                                        hs.remove((String)part);
                                                        break ff;
                                                    }
                                                }

                                                _assert(start_any, "\""+token + "\" is unrecognized or duplicate.");
                                            }
                                            if(will_import_lib)
                                                c0_natives.add(addition);
                                        } break;
//                                                case 1://METHOD
                                        case 2:{//STRUCT

                                            Object[] addition = new Object[1];
                                            _assert(token.startsWith("<"), "expected \"<\"", token, token_position);
                                            subs(1);

                                            //we know we have found a parameter with the name we need, and we are getting the information out of it.
                                            //we don't know exactly when it will end. We go from the : to the next > token.

                                            FileManip.TextPosition start_position = token_position;
                                            //the start position of the text we are getting.


                                            string_scan.set(token_position);
                                            FileManipScanner.Word werd = string_scan.next(">");

                                            token_position = new FileManip.TextPosition(werd.position.getLine(), werd.position.getColumn());
                                            substring(null, 1, token_position);
                                            string_scan.set(token_position);

                                            _assert(werd!=null,"expected \">\", instead unexpected end of file.");

                                            addition[0] = fm.getText(new FileManip.Range(start_position, werd.position));

                                            token = string_scan.next().text;
                                            ce();
                                            if(will_import_lib)
                                                c0_natives.add(addition);
                                        } break;
                                        case 3:{//TYPEDEF
                                            Object[] addition = new Object[1];
                                            _assert(token.startsWith("<"), "expected \"<\"", token, token_position);
                                            subs(1);

                                            //we know we have found a parameter with the name we need, and we are getting the information out of it.
                                            //we don't know exactly when it will end. We go from the : to the next > token.

                                            FileManip.TextPosition start_position = token_position;
                                            //the start position of the text we are getting.


                                            string_scan.set(token_position);
                                            FileManipScanner.Word werd = string_scan.next(">");

                                            token_position = new FileManip.TextPosition(werd.position.getLine(), werd.position.getColumn());
                                            substring(null, 1, token_position);
                                            string_scan.set(token_position);

                                            _assert(werd!=null,"expected \">\", instead unexpected end of file.");

                                            addition[0] = fm.getText(new FileManip.Range(start_position, werd.position));

                                            token = string_scan.next().text;
                                            ce();
                                            if(will_import_lib)
                                                c0_natives.add(addition);
                                        } break;
                                        case 4:{//POINTER
                                            ArrayList<String> hs = new ArrayList<String>();
                                            hs.add("type");
                                            boolean start_any;
                                            Object[] addition = new Object[1];
                                            while(!hs.isEmpty()){
                                                start_any = false;
                                                ff:for(Object part: hs.toArray()){
                                                    _assert(token.startsWith("<"), "expected \"<\"", token, token_position);
                                                    subs(1);

                                                    if(token.startsWith((String)part)){
                                                        start_any = true;

                                                        //get contents...
                                                        subs(((String)part).length());
                                                        
                                                        _assert(token.startsWith(":"), "expected \":\"", token, token_position);
                                                        subs(1);

                                                        //we know we have found a parameter with the name we need, and we are getting the information out of it.
                                                        //we don't know exactly when it will end. We go from the : to the next > token.

                                                        FileManip.TextPosition start_position = token_position;
                                                        //the start position of the text we are getting.


                                                        string_scan.set(token_position);
                                                        FileManipScanner.Word werd = string_scan.next(">");

                                                        token_position = new FileManip.TextPosition(werd.position.getLine(), werd.position.getColumn());
                                                        substring(null, 1, token_position);
                                                        string_scan.set(token_position);

                                                        _assert(werd!=null,"expected \">\", instead unexpected end of file.", token, start_position);

                                                        String info = fm.getText(new FileManip.Range(start_position, werd.position)).trim();

                                                            //could be name, ret, or param.
//                                                            Scanner info_scan = new Scanner(info);

                                                        switch((String)part){
                                                            case "type":{
                                                                _assert(!info.isEmpty(), "cannot have an empty string or whitespace as a C0 interface name");

                                                                addition[0] = info;
                                                                break;
                                                            }
                                                        }

                                                        token = werd.text;
                                                        subs(1);
                                                        hs.remove((String)part);
                                                        break ff;
                                                    }
                                                }
                                                _assert(start_any, "\""+token + "\" is unrecognized or duplicate.");
                                            }
                                            if(will_import_lib)
                                                c0_natives.add(addition);
                                        } break;
                                    }
                                } else{
                                    _assert(false, "expected \"<\". Found " + token);
                                }

                                //\\#END
                            } else{
                                //in $$BEGIN in $$PRE not #LOAD

                                //should be a \\#LOAD or an \\$$END
                                if(token.startsWith("\\\\$$")){

                                    subs(4);

                                    _assert(token.startsWith("END"), "expected \"END\", instead " + token);
                                    subs(3);
                                    block_pre = false;
                                } else if(token.startsWith("\\\\#")){
                                    subs(3);
                                    _assert(token.startsWith("LOAD"), "expected \"LOAD\", instead unknown token "+token);
                                    //currently only LOAD
                                    subs(4);
                                    _assert(token.startsWith("<"), "expected \"<\", instead unknown token "+token);
                                    subs(1);
                                    block_pre_HASH = true;
                                    if(token.startsWith("INTERFACE")){
                                        subs(9);
                                        load_val = 0;//INTERFACE
                                    } else if(token.startsWith("METHOD")){
                                        _assert(false, "METHOD is currently unsupported");
                                        //load_val = 1;
                                    } else if(token.startsWith("STRUCT")){
                                        subs(6);
                                        load_val = 2;
                                    }
                                    else if(token.startsWith("TYPEDEF")){
                                        subs(7);
                                        load_val = 3;
                                    }
                                    else if(token.startsWith("POINTER")){
                                        subs(7);
                                        load_val = 4;
                                    } else{
                                        load_val = -1;
                                        block_pre_HASH = false;
                                        _assert(false, "unknown \\\\#LOAD type " + token);
                                    }
                                    //we added till after the value, so we 
                                    //know that the 
                                    
                                    //we're completely done figuring out 
                                    //which LOAD environment we should be 
                                    //in.
                                    _assert(token.startsWith(">"), "expected \">\"", token, token_position);
                                    subs(1);
                                } else{
                                    _assert(false, "unknown command in \\\\$$PRE", token, token_position);
                                }
                            }
                        } else{
                            //IN block $$BEGIN, NOT in block $$PRE.


                            //the java code we are analyzing should NOT 
                            //contain the string \\$$END in it. If it does, 
                            //an error will be thrown.

                            if(token.startsWith("\\\\$$")){
                                //we should start a pre statement.
                                subs(4);
                                //should be a pre-statement or an $$\\END 
                                //(only ones supported so far!)
                                if(token.startsWith("PRE")){
                                    block_pre = true;
                                    //we are now in a pre block.
                                    subs(3);
                                } else if(token.startsWith("END")){

                                    block_import = false;
                                    will_import_lib = false;
                                    current_lib = null;

                                    token = substring(token, 3, token_position);
                                    if(token.isEmpty()){
                                        if(!string_scan.hasNext()) break tok;
                                        {FileManipScanner.Word werd = string_scan.next(); 
                                        token += werd.text; token_position = werd.position;}
                                    }
                                    //exit the import block.
                                } else{
                                    _assert(false, "expected \"PRE\" or \"END\"");
                                }
                            } else{
                                //token_position is the beginning line.
                                //we check that no other tokens are in the 
                                //java line.
                                int line = token_position.getLine();
                                {
                                String token2 = new FileManipScanner(fm).skipTo(new FileManip.TextPosition(line, 0)).text;

                                _assert(token2.equals(token), "java code MUST begin on its own line.");
                                }

                                FileManipScanner.Word werd = string_scan.next("\\\\$$");
                                _assert(werd!=null,"expected \"\\\\$$\", instead unexpected end of file.");

                                token = werd.text;//start over new

                                subs(4);
                                _assert(token.startsWith("END"), "expected \"END\", instead " + token);
                                token = substring(token, 3, token_position);
                                if(will_import_lib){
                                    java_import += fm.getText(new FileManip.Range(line, 0, werd.position.getLine(), werd.position.getColumn()));
                                }
                                if(token.isEmpty()){
                                    if(!string_scan.hasNext()) break tok;
                                    //we have reached an END statement at 
                                    //the end of a java part.
                                    //if we don't have another token, it's 
                                    //fine for us to be at the end of the 
                                    //file
                                    {FileManipScanner.Word wwerd = string_scan.next(); 
                                    token += wwerd.text; token_position = wwerd.position;}
                                }

                                block_import = false;
                                will_import_lib = false;
                                current_lib = null;
                            }
                        }
                    }
                    else{
                        if(!string_scan.hasNext()){
                            break tok;
                        }
                        int begin_index = token.indexOf("\\\\$$");
                        _assert(begin_index != -1, "expected \"\\\\$$\"");
                        _assert(begin_index == 0, "expected token should begin with \"\\\\$$\"");
                        //we know that every line is useful, so we know that when we
                        //are out of all other blocks, we must have a \\$$ tag.
                        //At the moment, the two tags usable are \\$$BEGIN and
                        //\\$$END. However, we know we are not in a block, so the
                        //only one that makes sense is when we are in a \\$$BEGIN


                        subs(begin_index + 4);
                        //otherwise, look at the rest of this token 
                        //to make sure that we have a BEGIN. The BEGIN 
                        //should be IMMEDIATE, as we split the tokens based
                        //on white space.
                        _assert(token.startsWith("BEGIN"), "expected \"BEGIN\"");                            
//                            token = token.substring(begin_index + 5, token.length());
                        subs(begin_index + 5);

                        _assert(token.startsWith("<"), "expected \"<\"");

                        subs(1);
                        {
                        int index = token.indexOf(">");
                        String this_lib;
                        if(index == -1){
                            //the end bracket is not on this token, so let's
                            //try the next one.
                            //we also know that a lib is a single word, so
                            //it won't wrap over to the next line.
                            this_lib = token;//copy till the end of the token
                            token = "";//token should be empty.
                            
                            ce();
                            //we add on the next token to keep scanning
                        } else {
                            //we know that this the lib is contained in the 
                            //bracket, so we just set this_lib from 0 till
                            //right before >. We also shorten the token.

                                this_lib = token.substring(0, index);
                                token = substring(token, index, token_position);
                        }
                        _assert(known_libs.contains(this_lib), "duplicate library : \""+this_lib+"\"");
                        current_lib = this_lib;//set the current lib.

                        //if we passed, we have a valid import.
                        will_import_lib = import_libs.contains(this_lib);
                        known_libs.remove(this_lib);

                        //in the current version, I won't allow multiple 
                        //instances of the same lib. (There might be silly 
                        //things with special cases that currently aren't 
                        //too important)
                        }

                        //we then scan for the end of the bracket to begin 
                        //the library import. We should exit on a >

                        //otherwise, if we aren't at the end of our token, 
                        //the very next char should be a closing bracket.
                        //we then scan for the end of the bracket to begin 
                        //the library import. We should exit on a >

                        //we know by the method above that we found a >.
                        //so we can substring the token.

                        token = substring(token, 1, token_position); 
                        //if we passed all the tests, we can enter the 
                        //block_import environment.
                        block_import = true;

                        if(token.isEmpty()){
                            ce();
                            //we add on the next token to keep scanning
                        }
                    }
            }
            }

        }
        ImportPackage imp = new ImportPackage();
        fm.delete(fm.getRange());
        known_libs.clear();
        known_libs.addAll(Arrays.asList(ALL_IMPORT_LIBS));
        
        
        
        imp.native_c0_interfaces__Keep = native_c0_interfaces__Keep;
        imp.native_c0_interfaces__Remove = native_c0_interfaces__Remove;

        imp.native_c0_pointers__Keep = native_c0_pointers__Keep;
        imp.native_c0_pointers__Remove = native_c0_pointers__Remove;

        imp.native_c0_structs__Keep = native_c0_structs__Keep;
        imp.native_c0_structs__Remove = native_c0_structs__Remove;

        imp.native_c0_typedefs = native_c0_typedefs;
        imp.native_c0_typedefs__Replace = native_c0_typedefs__Replace;
        
        imp.java_import = java_import;
        
        imp.errors = errors;
        
        imp.close();
        
        Err._assert(iml_flag, IML_EXIT, null);
        return imp;
    }
    private static String substring(String token, int num, FileManip.TextPosition shifter){
        if(token == null){FileManip.setTextPositionCol(shifter, shifter.getColumn()+num);
        return null;
        }
//        System.out.println("@0: "+token);
        FileManip.setTextPositionCol(shifter, shifter.getColumn()+num);
//        System.out.println("@1: "+token.substring(num, token.length()) + "\n");
        return token.substring(num, token.length());
    }
    
    
    static void _assert(boolean b, String error){
        Err._assert(b, IML_ERROR, new IMLDetails(error, null, (token_position)));
    }
    static void _assert(boolean b, String error, String text, FileManip.TextPosition pos){
        Err._assert(b, IML_ERROR, new IMLDetails(error, text, (pos)));
    }
    static void _assert(boolean b, String error, FileManip.TextPosition pos){
        Err._assert(b, BASIC_IML_ERROR, new IMLDetails(error, null, (pos)));
    }
    
    
    private static boolean iml_flag = true;
    static final K_Error<IMLDetails> IML_ERROR = new K_Error<IMLDetails>() {
        @Override public void _throw(IMLDetails input) {
            if(iml_flag) {input.introError();iml_flag = false;}
            errors.add(input);
            System.err.println("  error:  " +input.error);
            System.err.println("     in:  " +input.text);
            System.err.println("     at:  " +input.text_position);
        }
    };
    static final K_Error<IMLDetails> BASIC_IML_ERROR = new K_Error<IMLDetails>() {
        @Override public void _throw(IMLDetails input) {
            if(iml_flag) {input.introError();iml_flag = false;}
            errors.add(input);
            System.err.println("  error:  " +input.error);
            System.err.println("     at:  " +input.text_position);
        }
    };
    static final ExitableError<String> INIT_ERROR = new ExitableError<String>() {
        @Override public void exit() {
            System.exit(0);
        }
        @Override public void _throw(String input) {
            System.err.println(input);
            System.err.println("(initialization aborted)");
            exit();
        }
    };
    static final ExitableError<String> IML_EXIT = new ExitableError<String>() {
        @Override public void exit() {
            System.exit(0);
        }
        @Override public void _throw(String input) {
            System.err.println("(IML parsing aborted)");
            exit();
        }
    };
    static final ExitableError<IMLDetails> URGENT_IML_ERROR = new ExitableError<IMLDetails>() {
        @Override public void exit() {
            System.err.println("(IML parsing aborted)");
            System.exit(0);
        }
        @Override public void _throw(IMLDetails input) {
            if(iml_flag) {input.introError();iml_flag = false;}
            System.err.println("(urgent error)");
            System.err.println("  error:  " +input.error);
            System.err.println("     in:  " +input.text);
            System.err.println("     at:  " +input.text_position);
            exit();
        }
    };
    static final ExitableError<String> URGENT_IML_ERROR_S = new ExitableError<String>() {
        @Override public void exit() {
            System.err.println("(IML parsing aborted)");
            System.exit(0);
        }
        @Override public void _throw(String input) {
            if(iml_flag) {System.err.println("error in parsing IML (import markup language), file \""+ IMLDetails.source+"\"");iml_flag = false;}
            System.err.println("(urgent error)");
            System.err.println(input);
            exit();
        }
    };
}
