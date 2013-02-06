package compl.data;

import compl.data.FileManipScanner.C0Token;

/**
 * parses the syntax on the "base" level
 * @author kieda
 */
public class C0BaseSyntaxParser{
    abstract class Node implements Types{
        int type;
        
        /**
         * the input for "s" is a single valid c0 token. The string determines 
         * which next node we should go to.
         */
        abstract void action(FileManipScanner.C0Token s);
    }
    Node next;
        //which node is the next one?

boolean bad_syn = false;
int number_braces = 0;
    //the number of braces to keep track of syntax
int number_parens = 0;
    //the number of parentheses to keep track of syntax  

final Node head = new Node() {@Override void action(FileManipScanner.C0Token s){

//handle comments.
if(s.getTokenType() == COMMENT_TYPE){
    if(s.text.length()>2 && s.text.charAt(type)=='@')
        //this is an assertion. We should keep the comment.
        return;
    else return;
        //otherwise, this is a comment, so we should keep the token.
}
assert number_braces == 0;
switch(s.text){
    case "{":
        //error: syntax must be part of method body
        //continue scanning till }, add syntax between { and } to the
        //bad methods (with null name and null )
        bad_syn = true;
        number_braces++;
        //we are in syntax
        next = new Node() {@Override void action(C0Token s) {
            switch(s.getTokenType()){
                case BRACE_LEFT:
                    number_braces++;
                case BRACE_RIGHT:
                    number_braces--;
                    if(number_braces == 0)
                        //we want to delete the tokens in the 
                        return;
                    else return;
                default:
                    return;
            }
        }};
        return;
    case "}":
        //some sort of exit?
        
        //excess } at ground level. Delete it.
        return;
    case "struct":
        
    case "typedef":
        //typedef environment.
        return;
    default:
        /**
         * we will assume, for the time-being, that this is a method or an 
         * interface.
         * 
         * we will know that it is truly an interface or struct if we detect 
         * an opening parentheses. 
         * if (
         *      we open a new node:
         *          if )
         *          if {
         *          if 
         *          
         *      
         *      
         *      
         */
        next = new Node() { @Override void action(FileManipScanner.C0Token s) {
                switch(s.text){
                    case "{":        
                    case "}":
                    case "typedef":
                    case "struct":
                        //some sort of exit?
                    case "(":
                        //enter parameter scanning enironment
                    case ")":
                }
                return;
            }
        };
        return;
}}};
}
