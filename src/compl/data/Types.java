/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.data;

/**
 * @author kieda
 */
class s{
    public int sadf(){
        try{
            while(3/0 == 3/0){
            }
            if(true)return 1;
            
            if(true)return 2;
        }catch(Exception e){
            
        }throw new Error();
    }
}
public interface Types {
    
    //TYPES are bitwise or-ed with types to give it a type property.
    //16 properties available in an integer (positions 17 through 32 on an int)
    //positions 1 through 16 are reserved for specific types.
    
    /**
     * represents tokens that are of a comment type<br>
     * Valid c0 comment types (in regex):
     * <ul>
     *  <li> //(.)*
     *  <li> /(\*)(.|\n)*(\*)/
     * </ul>
     */
    public static final int COMMENT_TYPE           = 1<<17;
    /**
     * represents a token that is unknown. 
     */
    public static final int UNKNOWN_TYPE           = 1<<18;
    public static final int DATA_TYPE              = 1<<20;
    public static final int INTEGER_TYPE           = 1<<19 | DATA_TYPE;
    public static final int BOOLEAN_TYPE           = 1<<26 | DATA_TYPE;
    public static final int OPERATION_TYPE         = 1<<21;
    public static final int INT_OPERATION_TYPE     = OPERATION_TYPE | INTEGER_TYPE;
    public static final int BOOLEAN_OPERATION_TYPE = OPERATION_TYPE | BOOLEAN_TYPE;
    public static final int POINTER_TYPE           = 1<<22 | DATA_TYPE;
    public static final int POINTER_OPERATION_TYPE = OPERATION_TYPE | POINTER_TYPE;
    public static final int COMPARE_TYPE           = 1<<23 | OPERATION_TYPE;
    public static final int EQUAL_TYPE             = 1<<24;
    public static final int OPERATION_EQUAL_TYPE   = EQUAL_TYPE | OPERATION_TYPE;
    public static final int WORD_TYPE              = 1<<25;
    public static final int OPENING                = 1<<26;
    public static final int CLOSING                = 1<<27;
    
    public static final int BLOCK_COMMENT = 0x0001 | COMMENT_TYPE;
    public static final int LINE_COMMENT  = 0x0002 | COMMENT_TYPE;
    public static final int ANY           = 0x0003;
    public static final int DEC_NUMBER    = 0x0004 | INTEGER_TYPE;
    public static final int HEX_NUMBER    = 0x0005 | INTEGER_TYPE;
    public static final int STRING        = 0x0006 | DATA_TYPE;
    public static final int CHAR          = 0x0007 | DATA_TYPE;
    public static final int PRAGMA        = 0x0008;
    
    public static final int DIVIDE        = 0x0009 | INT_OPERATION_TYPE;
    public static final int MULTIPLY      = 0x000A | INT_OPERATION_TYPE;
    public static final int ADD           = 0x000B | INT_OPERATION_TYPE;
    public static final int SUBTRACT      = 0x000C | INT_OPERATION_TYPE;
    public static final int BITWISE_AND   = 0x000D | INT_OPERATION_TYPE;
    public static final int BITWISE_OR    = 0x000D | INT_OPERATION_TYPE;
    public static final int BITWISE_XOR   = 0x000E | INT_OPERATION_TYPE;
    public static final int MODULUS       = 0x000F | INT_OPERATION_TYPE;
    public static final int INCREMENT     = 0x0010 | INT_OPERATION_TYPE;
    public static final int DECREMENT     = 0x0011 | INT_OPERATION_TYPE;
    public static final int SHIFT_L       = 0x0012 | INT_OPERATION_TYPE;
    public static final int SHIFT_R       = 0x0013 | INT_OPERATION_TYPE;
    public static final int BITWISE_NOT   = 0x0014 | INT_OPERATION_TYPE;
    
    public static final int STRUCT_DEREF  = 0x0015 | POINTER_OPERATION_TYPE;
    public static final int STRUCT_SELECT = 0x0016 | POINTER_OPERATION_TYPE;
    
    public static final int LOGICAL_AND   = 0x0017 | BOOLEAN_OPERATION_TYPE;
    public static final int LOGICAL_OR    = 0x0018 | BOOLEAN_OPERATION_TYPE;
    public static final int NOT           = 0x0019;
    public static final int LOGICAL_NOT   = NOT | BOOLEAN_OPERATION_TYPE;
    
    
    
    public static final int DIVIDE_EQUAL      = DIVIDE      | OPERATION_EQUAL_TYPE;
    public static final int MULTIPLY_EQUAL    = MULTIPLY    | OPERATION_EQUAL_TYPE;
    public static final int ADD_EQUAL         = ADD         | OPERATION_EQUAL_TYPE;
    public static final int SUBTRACT_EQUAL    = SUBTRACT    | OPERATION_EQUAL_TYPE;
    public static final int BITWISE_AND_EQUAL = BITWISE_AND | OPERATION_EQUAL_TYPE;
    public static final int BITWISE_OR_EQUAL  = BITWISE_OR  | OPERATION_EQUAL_TYPE;
    public static final int BITWISE_XOR_EQUAL = BITWISE_XOR | OPERATION_EQUAL_TYPE;
    public static final int MODULUS_EQUAL     = MODULUS     | OPERATION_EQUAL_TYPE;
    public static final int SHIFT_L_EQUAL     = SHIFT_L     | OPERATION_EQUAL_TYPE;
    public static final int SHIFT_R_EQUAL     = SHIFT_R     | OPERATION_EQUAL_TYPE;
    public static final int EQUAL             = 0x0023      | EQUAL_TYPE;
    
    public static final int COMMA        =  0x001A;
    public static final int QUESTION_MARK=  0x001B;
    public static final int COLON        =  0x001C;
    public static final int SEMI_COLON   =  0x001D;
    
    public static final int BRACKET      =  0x001E;
    public static final int BRACE        =  0x001F;
    public static final int PARENTHESES  =  0x0020;
    
    /**[*/
    public static final int BRACKET_LEFT      =  OPENING | BRACKET;
    
    /**{*/
    public static final int BRACE_LEFT        =  OPENING | BRACE;
    
    /**(*/
    public static final int PARENTHESES_LEFT  =  OPENING | PARENTHESES;
    
    /**]*/
    public static final int BRACKET_RIGHT     =  CLOSING | BRACKET;
    
    /**}*/
    public static final int BRACE_RIGHT       =  CLOSING | BRACE;
    
    /*)*/
    public static final int PARENTHESES_RIGHT =  CLOSING | PARENTHESES;
    
    
    /**any char that is not recognized as a token*/
    public static final int UNKNOWN_CHAR      =  CHAR | UNKNOWN_TYPE;
    
    public static final int LESS     =  0x0021;
    public static final int GREATER  =  0x0022;
    
    public static final int WORD     =  0x0024 | WORD_TYPE;
    
    public static final int LESS_THAN        =  LESS        | COMPARE_TYPE;
    public static final int GREATER_THAN     =  GREATER     | COMPARE_TYPE;
    public static final int EQUAL_TO         =  EQUAL       | COMPARE_TYPE | EQUAL_TYPE;
    public static final int NOT_EQUAL_TO     =  NOT         | COMPARE_TYPE | EQUAL_TYPE;
    public static final int LESS_THAN_OR_EQUAL_TO     
                                             =  LESS        | COMPARE_TYPE | EQUAL_TYPE;
    public static final int GREATER_THAN_OR_EQUAL_TO  
                                             =  GREATER     | COMPARE_TYPE | EQUAL_TYPE;
    
    public final class VALS{
        private VALS(){};
        public static boolean isOfType(int TYPE_MASK, int val){
            return (val & TYPE_MASK) != 0;
        }
        public static boolean baseTypeEqual(int VALUE, int val){
            assert (VALUE & 0x0000FFFF) != 0;
            return (VALUE & 0x0000FFFF) == (val & 0x0000FFFF);
        }
    }

}
