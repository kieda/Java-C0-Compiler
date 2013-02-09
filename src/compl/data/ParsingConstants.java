/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.data;

/**
 *
 * @author kieda
 */
public interface ParsingConstants {
    
    /**parser results**/
    /**the operation was successful. Include the last character you put in.*/
    final static int EXIT_SUCCESSFUL = 0;
    
    /**
     * the operation was successful. Rewind one character. You may this constant
     * multiply by an integer to rewind more one value.
     */
    final static int EXIT_SUCCESSFUL_REWIND = 1;
    
    final static int CONTINUE_AND_REWIND = 2;
    /**
     * the operation was completely unsuccessful (no suitable paths were found 
     * for the string, so it was impossible to parse it.)
     * 
     * This happens when an unexpected character occurs in a place where only 
     * "normal" characters are expected.
     */
    final static int EXIT_UNSUCCESSFUL = 25;
    
    /**
     * Continues to the next character, and record the given character
     */
    final static int ERROR_AND_RECORD = 150;
    final static int CONTINUE_AND_RECORD = 151;
    
    /**
     * Continues to the next character without recording
     */
    final static int CONTINUE = 122;
    
    final static int UNKNOWN_CHAR = 55;
    final static int INDIVIDUAL_CHAR = 12347;
}
