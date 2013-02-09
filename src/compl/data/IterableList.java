/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.data;

/**
 *
 * @author kieda
 */
public interface IterableList<T> extends BasicList<T> {
    public T next();
    //goes back to the beginning of the list
    public void rewind();
//    public int mark(T elem);
}
