/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

import com.kieda.util.k_error.Err;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kieda
 */
public class Parsers {
    public abstract static class ParseErrorDetails{
        protected ParseErrorDetails(String error, String text, String source, compl.data.FileManip.TextPosition range){
            this.error = error;
            this.text = text;
            this.source = source;
            this.text_position = range;
        }
        /**the error message (what is wrong?)**/
        protected String error;
        
        /**the text at where the error occurs (what text is incorrect?)**/
        protected String text;
        
        /**What is the source file?**/
        protected String source;
        
        /**the range where the text is incorrect (where in which file is incorrect?)**/
        protected compl.data.FileManip.TextPosition text_position;
        
        public String getError(){
            return error;
        }
        public String getText(){
            return text;
        }
        
        public String getSource(){
            return source;
        }
        
        public compl.data.FileManip.TextPosition getPosition(){
            return text_position;
        }
        
        //error in parsing IML (import markup language), file "getSource()"
        //    error:    getError()
        //       in:    getText()
        //     from:    getRange().getBeginning()
        //       to:    getRange().getEnd()
        public void printError(){
            System.err.println("    error:    " + getError());
            System.err.println("       in:    " + getText());
            System.err.println("       at:    " + getPosition());
        }
        public abstract void introError();
    }
    static HashMap<String, compl.etc.C0Struct> structs;
    static HashMap<String, compl.etc.C0Interface> interfaces;
    static HashMap<String, compl.etc.C0TypeDef_parsed> typedefs;
    
    
    /**
     * note - inputs are assumed such that the string is on one line, and that 
     * the only type of whitespace is from spaces (' '). We also assume that
     * the info has no white space at the beginning of the string or at the end
     * of the string. We also assume that the info has only one space between
     * each word. 
     * 
     * We can assume this as calls to this method are only happening within this
     * package.
     */
    static C0Data parse_c0_type(String info){
        ArrayList<String> tokens = new ArrayList<String>();
        //the "parts" of the data. i.e. a struct name is a part, or the word 
        //struct is one part, or a * is one part, or a pair of brackets is one \
        //part.
        
        compl.etc.C0Data ret;
        {
            char[] c = info.toCharArray();
            //we put words, brackets, and pointers as words. Originally, we are 
            //in none of them
            boolean word = false;
            boolean pointer = false;
            boolean bracket = false;
            String s = "";
            for(int i = 0; i < c.length; i++){
                char cc = c[i];
                if(cc == ' '){
                    if(word){
                        tokens.add(s);
                        word = false;
                        s = "";
                    }
                    //represents the end of a word.
                    //skip while in brackets.
                    //cannot occur while in pointer
                } else if(cc == '['){
                    //represents the beginning of a bracket. Also marks the end 
                    //of a word
                    if(word){
                        tokens.add(s);
                        word = false;
                        s="";
                    }
                    bracket = true;
                } else if(cc == ']'){
                    if(!bracket || word){
                        Err._assert(false, Err.BASIC_EXITABLE_ERROR, "Bad Type: " + (word?"\"]\" in the middle of a name":"no matching bracket for an array."));
                    }
                    tokens.add("[]");
                    bracket = false;
                } else if(cc == '*'){
                    //add a pointer.
                    if(word){
                        tokens.add(s);
                        word = false;
                        s="";
                    }
                    if(bracket){
                        Err._assert(false, Err.BASIC_EXITABLE_ERROR, "Bad Type: expected a matching bracket \"]\", instead char \"" + cc + "\"");
                    }
                    pointer = true;
                    tokens.add("*");
                    pointer = false;
                } else if(
                        //all chars possible in a word/name
                    (48 <= (int)cc && (int)cc<=57)||//numbers 0-9 (possible in a name)
                    (65 <= (int)cc && (int)cc<=90)||//letters A-Z
                    ((int)cc == 95)||//underscore
                    (97 <= (int)cc && (int)cc<=122)//letters a-z
                        ){
                    if(bracket){
                        Err._assert(false, Err.BASIC_EXITABLE_ERROR, "Bad Type: expected a matching bracket \"]\", instead char \"" + cc + "\"");
                    }
                    word = true;
                    s+=cc;
                } else{
                    Err._assert(false, Err.BASIC_EXITABLE_ERROR, "\""+cc + "\" is not a valid char for a c0 name.");
                }
            }
            if(!s.isEmpty()){
                tokens.add(s);
            }
        }
        if(tokens.isEmpty()){
            return null;
        }
        C0Data base_type;
        {
        String first = tokens.remove(0);
        switch(first){
            case "struct":{
            Err._assert(!tokens.isEmpty(), Err.BASIC_EXITABLE_ERROR, "struct requires a name");
            //another.
            C0Struct c0s;
            {
            String struct_name = tokens.remove(0);
                c0s = structs.get(struct_name);
            
            Err._assert(c0s != null, Err.BASIC_EXITABLE_ERROR, "Unknown/undefined struct type: \"" + struct_name+"\"");
            }
            
            /**
             * DIFFERENCE FROM C0 - in c0 you can struct my_struct[] = alloc_array(my_struct, 2)
             * compiles, but is unusable.
             * Let's just require small type rep.
             */
            {
                if(!tokens.isEmpty()){
                    String rep = tokens.remove(0);
                    Err._assert(rep.equals("*"), Err.BASIC_EXITABLE_ERROR, "struct cannot be passed around by itself, requires a pointer (\"*\") representation.");
                    base_type = Types.POINTER(c0s);//pointer to a struct.
                }
                    
                else base_type = c0s;
            }
            
            }break;
            case "int":{
                base_type = Types.INT;
            } break;
            case "bool":{
                base_type = Types.BOOL;
            } break;
            case "char":{
                base_type = Types.CHAR;
            } break;
            case "string":{
                base_type = Types.STRI;
            } break;
            case "void":{
                Err._assert(tokens.isEmpty(), Err.BASIC_EXITABLE_ERROR, "void type cannot be in an array or a pointer.");
                return Types.VOID;
            } 
            default:
                String $first = new String(first);
                base_type = null;
                C0TypeDef_parsed ctd = typedefs.get(first);
                Err._assert(ctd != null, Err.BASIC_EXITABLE_ERROR, "unknown type: " + $first);
                //replace the first type with the typedef
                base_type = ctd.type;
                break;
        }
        }
        //now all we should have are [] and *.
        while(!tokens.isEmpty()){
            String ty = tokens.remove(0);
            switch(ty){
                case "[]": 
                    base_type = Types.ARRAY(base_type);
                    break;
                case "*": 
                    base_type = Types.POINTER(base_type);
                    break;
                default:
                    Err._assert(false, Err.BASIC_EXITABLE_ERROR, "expected \"[]\" or \"*\", instead received " + ty);
            }
        }
        return base_type;
    }
}
