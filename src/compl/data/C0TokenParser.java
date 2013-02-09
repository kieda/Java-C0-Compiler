/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.data;

/**
 *
 * @author kieda
 */
/**
 * I decided to abandon my previous idea of using regexes and advanced 
 * parsing in light of some time constraints. I decided to use a mode 
 * non-expandable method, which should be able to run faster and have less 
 * of a load on the memory.
 */
public class C0TokenParser implements ParsingConstants{
    
    abstract class Node implements Types{
        int type;
        /**
         * returns an action based on the input char. 
         * 
         */
        abstract int action(char c);
    }

Node next;
//just a head and a node to cover the next operations.

/**
 * PLEASE do not look at this class if you know what's good for you.
 */
final Node till_end_of_line = new Node() {int action(char c) {switch(c){            
    case '\n':
    case '\r':
        return EXIT_SUCCESSFUL_REWIND;
    default:
        return CONTINUE_AND_RECORD;
}}};
final Node string_escape_chars = new Node() {int action(char c) {int retval;switch(c){            
    case't':case'r':case'f':case'b':case'n':case'\'':case'"':case'\\':
        retval = CONTINUE_AND_RECORD;break;
    case'a':case'?':case'v':
        //log error
        retval= ERROR_AND_RECORD;break;
    case'0':retval= ERROR_AND_RECORD;break;
    default:
        //log error
        retval= ERROR_AND_RECORD;break;
        //inside, we are allowed \t, \r, \f, \a, \b, \n, \v, \', \", \\, \? and \0
        //java, we do not have \a, \?, or \v. 
        //throw an UnsupportedEscapeSequence error if so.
        //if a string contains \0, then throw an IllegalEscapeSequence.
}next = in_string; return retval;}};
final Node in_string = new Node() {int action(char c) {switch(c){
    case '\\':
        next = string_escape_chars;
        return CONTINUE_AND_RECORD;
    //exit sequences
    case '\"':
        return EXIT_SUCCESSFUL;
    case '\n':
    case '\r':
        //line ended before string was closed
        return EXIT_UNSUCCESSFUL;
    default:
        return CONTINUE_AND_RECORD;
}}};
final Node char_escape_chars = new Node() {int action(char c) {int retval;switch(c){            
    case't':case'r':case'f':case'b':case'n':case'\'':case'"':case'\\':case'0':
        retval = CONTINUE_AND_RECORD;break;
    case'a':case'?':case'v':
        //log error
        retval= ERROR_AND_RECORD;break;
    default:
        //log error
        retval= ERROR_AND_RECORD;break;
        //inside, we are allowed \t, \r, \f, \a, \b, \n, \v, \', \", \\, \? and \0
        //java, we do not have \a, \?, or \v. 
        //throw an UnsupportedEscapeSequence error if so.
        //if a string contains \0, then throw an IllegalEscapeSequence.
}next = in_char; return retval;}};
final Node in_char = new Node() {int action(char c) {switch(c){
    case '\\':
        next = char_escape_chars;
        return CONTINUE_AND_RECORD;
    //exit sequences
    case '\'':
        return EXIT_SUCCESSFUL;
    case '\n':
    case '\r':
        //line ended before string was closed
        return EXIT_UNSUCCESSFUL;
    default:
        return CONTINUE_AND_RECORD;
}}};
//final Node equals = new Node() {int action(char c) {switch(c){
//    case '=':
//        return EXIT_SUCCESSFUL;
//    default:return EXIT_SUCCESSFUL_REWIND; 
//}}};
final Node decimal = new Node() {int action(char c) {switch(c){
    case'0':case'1':case'2':case'3':case'4':
    case'5':case'6':case'7':case'8':case'9':
        return CONTINUE_AND_RECORD;
    default:return EXIT_SUCCESSFUL_REWIND; 
}}};
final Node hexadecimal = new Node() {int action(char c) {switch(c){
    case'0':case'1':case'2':case'3':case'4':
    case'5':case'6':case'7':case'8':case'9':
    
    case'a':case'b':case'c':case'd':case'e':case'f':
    case'A':case'B':case'C':case'D':case'E':case'F':
        return CONTINUE_AND_RECORD;
    default:return EXIT_SUCCESSFUL_REWIND; 
}}};
final Node head = new Node(){
    public void type(int t){
        this.type = t;
    }
    @Override
    int action(char c) {switch(c){
        case '/':
            next = new Node() {int action(char c) {switch(c){
                case '*':
                    type(BLOCK_COMMENT);
                    next = BLOCK_COMMENT_END1;
                    return CONTINUE_AND_RECORD;
                case '/':
                    type(LINE_COMMENT);
                    next = till_end_of_line;
                    return CONTINUE_AND_RECORD;
                case '=':
                    type(DIVIDE_EQUAL);
                    return EXIT_SUCCESSFUL;
                default:
                    type(DIVIDE);
                    return EXIT_SUCCESSFUL_REWIND;
            }}};
            type(UNKNOWN_TYPE);
            return -CONTINUE_AND_RECORD;
            
        //(phew) all the parts for the beginning of a word
        case'a':case'b':case'c':case'd':case'e':case'f':case'g':case'h':case'i':
        case'j':case'k':case'l':case'm':case'n':case'o':case'p':case'q':case'r':
        case's':case't':case'u':case'v':case'w':case'x':case'y':case'z':case'A':
        case'B':case'C':case'D':case'E':case'F':case'G':case'H':case'I':case'J':
        case'K':case'L':case'M':case'N':case'O':case'P':case'Q':case'R':case'S':
        case'T':case'U':case'V':case'W':case'X':case'Y':case'Z':case '_':
        //search till a non-word character occurs (after this set we include numbers)
            type(WORD);
            /**todo: words**/
            next = new Node() {int action(char c) {switch(c){
                case'a':case'b':case'c':case'd':case'e':case'f':case'g':case'h':
                case'i':case'j':case'k':case'l':case'm':case'n':case'o':case'p':
                case'q':case'r':case's':case't':case'u':case'v':case'w':case'x':
                case'y':case'z':case'A':case'B':case'C':case'D':case'E':case'F':
                case'G':case'H':case'I':case'J':case'K':case'L':case'M':case'N':
                case'O':case'P':case'Q':case'R':case'S':case'T':case'U':case'V':
                case'W':case'X':case'Y':case'Z':case'_':case'0':case'1':case'2':
                case'3':case'4':case'5':case'6':case'7':case'8':case'9':
                    return CONTINUE_AND_RECORD;
                default: return EXIT_SUCCESSFUL_REWIND;
            }}};
            
            return -CONTINUE_AND_RECORD;
        case '#'://pragma : we return till the end of the line.
            type(PRAGMA);
            next = till_end_of_line;
            return -CONTINUE_AND_RECORD;
        case '\"':
            type(STRING);
            next = in_string;
            //inside, we are allowed \t, \r, \f, \a, \b, \n, \v, \', \", \\, \? and \0
            //java, we do not have \a, \?, or \v. 
            //throw an UnsupportedEscapeSequence error if so.
            //if a string contains \0, then throw an IllegalEscapeSequence.
            return -CONTINUE_AND_RECORD;
        case '\'':
            type(CHAR);
            /**todo: strings/chars**/
            return -CONTINUE_AND_RECORD;
        //single chars:
        case'{': type(BRACE_LEFT);          return -INDIVIDUAL_CHAR;
        case'[': type(BRACKET_LEFT);        return -INDIVIDUAL_CHAR;
        case'(': type(PARENTHESES_LEFT);    return -INDIVIDUAL_CHAR;
        case';': type(SEMI_COLON);          return -INDIVIDUAL_CHAR;
        case'?': type(QUESTION_MARK);       return -INDIVIDUAL_CHAR;
        case',': type(COMMA);               return -INDIVIDUAL_CHAR;
        case'}': type(BRACE_RIGHT);         return -INDIVIDUAL_CHAR;
        case']': type(BRACKET_RIGHT);       return -INDIVIDUAL_CHAR;
        case')': type(PARENTHESES_RIGHT);   return -INDIVIDUAL_CHAR;
        case':': type(COLON);               return -INDIVIDUAL_CHAR;
        case'.': type(STRUCT_SELECT);       return -INDIVIDUAL_CHAR;
        case'~': type(BITWISE_NOT);         return -INDIVIDUAL_CHAR;
            
        //integers that do not start with zero
        case'1':case'2':case'3':case'4':case'5':case'6':case'7':case'8':case'9':
            type(DEC_NUMBER);
            next = new Node() {int action(char c) {switch(c){
                case'0':case'1':case'2':case'3':case'4':
                case'5':case'6':case'7':case'8':case'9':
                    return CONTINUE_AND_RECORD;
                default:
                    return EXIT_SUCCESSFUL_REWIND;
            }}};
            return -CONTINUE_AND_RECORD;
        //enumerations are shown beyond this point
        case '<':
            
            next = new Node() {int action(char c) {switch(c){
                case '<':
                    next = new Node() {int action(char c) {switch(c){
                        case '=':
                            type(SHIFT_L_EQUAL);
                            return EXIT_SUCCESSFUL;
                        default: 
                            type(SHIFT_L);
                            return EXIT_SUCCESSFUL_REWIND;
                    }}};
                return CONTINUE_AND_RECORD;
                case '=':
                    type(LESS_THAN_OR_EQUAL_TO);
                    return EXIT_SUCCESSFUL;
                default:
                    type(LESS_THAN);
                    return EXIT_SUCCESSFUL_REWIND; 
            }}};
//            <
//              <<
//                  <<=
//              <=
            return -CONTINUE_AND_RECORD;
        case '>':
            next = new Node() {int action(char c) {switch(c){
                case '>':
                    next = new Node() {int action(char c) {switch(c){
                        case '=':
                            type(SHIFT_R_EQUAL);
                            return EXIT_SUCCESSFUL;
                        default: 
                            type(SHIFT_R);
                            return EXIT_SUCCESSFUL_REWIND;
                    }}};
                return CONTINUE_AND_RECORD;
                case '=':
                    type(GREATER_THAN_OR_EQUAL_TO);
                    return EXIT_SUCCESSFUL;
                default:type(GREATER_THAN);return EXIT_SUCCESSFUL_REWIND; 
            }}};
//            >
//              >>
//                  >>=
//              >=
            return -CONTINUE_AND_RECORD;
        case '*':
//            next = equals;
            next = new Node() {int action(char c) {switch(c){
            case '=':
                type(MULTIPLY_EQUAL);
                return EXIT_SUCCESSFUL;
            default:
                type(MULTIPLY | POINTER_TYPE);
                return EXIT_SUCCESSFUL_REWIND; 
        }}};
//            *
//              *=
            return -CONTINUE_AND_RECORD;
        case '=':
            next = new Node() {int action(char c) {switch(c){
                case '=':
                    type(EQUAL_TO);
                    return EXIT_SUCCESSFUL;
                default:
                    type(EQUAL);
                    return EXIT_SUCCESSFUL_REWIND; 
            }}};
//            =
//              ==
            return -CONTINUE_AND_RECORD;
        case '!':
            next = new Node() {int action(char c) {switch(c){
                case '=':
                    type(NOT_EQUAL_TO);
                    return EXIT_SUCCESSFUL;
                default:
                    type(LOGICAL_NOT);
                    return EXIT_SUCCESSFUL_REWIND; 
            }}};
//            !
//              !=
            return -CONTINUE_AND_RECORD;
        case '+':
            next = new Node() {int action(char c) {switch(c){
                case '=':
                    type(ADD_EQUAL); return EXIT_SUCCESSFUL;
                case '+':
                    type(INCREMENT);
                    return EXIT_SUCCESSFUL;
                default:
                    type(ADD);
                    return EXIT_SUCCESSFUL_REWIND; 
            }}};
//            +
//              ++
//              +=
            return -CONTINUE_AND_RECORD;
        case '-':
            next = new Node() {int action(char c) {switch(c){
                case '=':
                    type(SUBTRACT_EQUAL);
                    return EXIT_SUCCESSFUL;
                case '-':
                    type(DECREMENT);
                    return EXIT_SUCCESSFUL;
                case '>':
                    type(STRUCT_DEREF);
                    return EXIT_SUCCESSFUL;
                default:type(SUBTRACT);return EXIT_SUCCESSFUL_REWIND; 
            }}};
//            -
//              --
//              -=
//              ->
            return -CONTINUE_AND_RECORD;
        case '&':
            next = new Node() {int action(char c) {switch(c){
                case '=':
                    type(BITWISE_AND_EQUAL);
                    return EXIT_SUCCESSFUL;
                case '&':
                    type(LOGICAL_AND);
                    return EXIT_SUCCESSFUL;
                default:type(BITWISE_AND);return EXIT_SUCCESSFUL_REWIND; 
            }}};
//            &
//              &&
//              &=
            return -CONTINUE_AND_RECORD;
        case '|':
            next = new Node() {int action(char c) {switch(c){
                case '=':
                    type(BITWISE_OR_EQUAL);
                    return EXIT_SUCCESSFUL;
                case '|':
                    type(LOGICAL_OR);
                    return EXIT_SUCCESSFUL;
                default:type(BITWISE_OR);return EXIT_SUCCESSFUL_REWIND; 
            }}};
//            |
//              ||
//              |=
            return -CONTINUE_AND_RECORD;
        case '^':
            next = new Node() {int action(char c) {switch(c){
                case '=':
                    type(BITWISE_XOR_EQUAL);
                    return EXIT_SUCCESSFUL;
                default:type(BITWISE_XOR);return EXIT_SUCCESSFUL_REWIND; 
            }}};
//            ^
//              ^=
            return -CONTINUE_AND_RECORD;
        case '%':
            next = new Node() {int action(char c) {switch(c){
                case '=':
                    type(MODULUS_EQUAL);
                    return EXIT_SUCCESSFUL;
                default:type(MODULUS);return EXIT_SUCCESSFUL_REWIND; 
            }}};
//            %
//              %=
            return -CONTINUE_AND_RECORD;
        case '0':/**todo: hexadecimal/ints that start with 0**/
            next = new Node() {int action(char c) {switch(c){
                case'x':case'X':
                    type(HEX_NUMBER);
                    next = hexadecimal;
                    return CONTINUE_AND_RECORD;
                case'0':case'1':case'2':case'3':case'4':
                case'5':case'6':case'7':case'8':case'9':
                    type(DEC_NUMBER);
                    next = decimal;
                    return CONTINUE_AND_RECORD;
                default: type(DEC_NUMBER); return EXIT_SUCCESSFUL_REWIND;
            }}};
            return -CONTINUE_AND_RECORD;
        case ' ':case '\t':case '\n':case '\r':
            return -CONTINUE;
        default:
            //log an IllegalC0TokenError.
            //then re-start on the next char.
            type(UNKNOWN_CHAR);
            return -C0TokenParser.UNKNOWN_CHAR;
    }
}};
private final Node BLOCK_COMMENT_END2 = new Node(){int action(char c) {
//    System.out.println("block comm 2: input " + c+ ", options - / = exit, else = block comm 1");
    switch(c){
    case '/':
        return EXIT_SUCCESSFUL;
    default:
        next = BLOCK_COMMENT_END1;
        return CONTINUE_AND_REWIND;
}}};
private final Node BLOCK_COMMENT_END1 = new Node(){int action(char c) {
//    System.out.println("block comm 1: input " + c+ ", options - * = block comm 2");
    switch(c){
    case '*':
        next = BLOCK_COMMENT_END2;
        return CONTINUE_AND_RECORD;
    default:
        return CONTINUE_AND_RECORD;
}}};
    
    //strictly for testing.
    public String next(String s){
        next = head;
        char[] cs = s.toCharArray();
        String n = "";
        
        for(int i = 0; i < cs.length; i++){
            char c = cs[i];
            int action = next.action(c);
            switch(action){
                case EXIT_SUCCESSFUL_REWIND:
                    return n;
                case CONTINUE_AND_RECORD:
                    n+=c;
                    break;
                case EXIT_SUCCESSFUL:
                    n+=c;
                    return n;
                case EXIT_UNSUCCESSFUL:
                    return null;
            }
        }
        return n;
    }
    public C0TokenParser(){}
}
