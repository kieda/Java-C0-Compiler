package compl.data;

/**
 *
 * @author kieda
 */
public interface ManipulableList<T> extends IterableList<T> {
    //deletes from where the list begins till where it ends
    public void sew_inclusive(Object begin, Object end);
    public void sew(Object begin, Object end);
    public void sew_before(Object idx);
    public void sew_after(Object idx);
    public void sew_before_inclusive(Object idx);
    public void sew_after_inclusive(Object idx);
    public void cut_before(Object idx);

    public void cut_after(Object idx);
    public void cut_before();
    public void cut_after();
    /**
     * marks the current element
     */
    public Object mark()
            //@ensures mark is unique for a unique T
    ;
    public void insert(T elem);
    public void append(Object list);
    public void insert_list(Object list);
    
    public T get(Object idx);
}
