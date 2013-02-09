/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.data;

/**
 *
 * @author kieda
 */
public class IncorrectListElementException extends RuntimeException {

    /**
     * Creates a new instance of
     * <code>IncorrectListElementException</code> without detail message.
     */
    public IncorrectListElementException() {
    }

    /**
     * Constructs an instance of
     * <code>IncorrectListElementException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public IncorrectListElementException(String msg) {
        super(msg);
    }
}
