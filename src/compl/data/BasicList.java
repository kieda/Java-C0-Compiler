package compl.data;

/**
 *
 * @author kieda
 */
public interface BasicList<T> {
    public boolean empty();
    public int size();
//    public T head();
    //adds an elem at the end
    public void add(T elem);
}
