package compl.data;

import compl.data.FileManipScanner.C0Token;

/**
 * A list of tokens from a FileManip. The idea behind this list is that you will
 * not have to create multiple scanners, or parse through the same materials 
 * multiple times while scanning c0 syntax. This provides a nice, higher level,
 * interface for dealing with scanning and manipulating c0 tokens within a main 
 * c0 file (like the removal of token ranges; and the addition and insertion of 
 * tokens), except as the list is processing these operations at a higher level
 * (it can skip passing around and analyzing strings; we only need to pass 
 * around tokens) the operations are much faster then their previous version.
 * 
 * The structure of token list is a linked list with nodes, and is optimized for
 * the use of sequential access, quick removal and addition of nodes (unlike an
 * array; which often has to perform in O(n) time for basic deletion)
 * 
 * The downside to using this is that there is no immediate access for elements,
 * so the way we work around that is by sending the user "Markers", which are 
 * objects that point to a position on the list. So, a user 
 * 
 * 
 * 
 * Note: adding and removing elements does not change the internal structure of 
 * the C0 tokens. 
 */
public class TokenList implements ManipulableList<FileManipScanner.C0Token> {

    /**
     * the head of the linked list
     */
    private Node head;
    
    /**
     * the current node in the list
     */
    private Node current;
    
    /**
     * the end of the list. 
     */
    private Node end;
    
    public TokenList(FileManip file){
        FileManipScanner fms = new FileManipScanner(file);
        
        //we scan through all of the elements that will 
        FileManipScanner.C0Token cur = fms.nextToken();
        //in the case of an empty file
        if(cur == null)return;
        //the head will be null if the list is of size zero (so if an empty file 
        //is appended, we will just make the next node null, for consistancy)
        
        //we want to traverse across the tree. We dlo this by node traversal and
        //by the token scanner.
        Node traversal = head = new Node(cur);
        while((cur = fms.nextToken()) != null){
            traversal = traversal.next(cur);
        }
        current = head;
        end = traversal;//
    }
    
    /**
     * A reference to a node in the linked list, and passed to the client via
     * mark()
     * 
     * this is a reference that is used, so the client doesn't fuck up the 
     * internal structure of the list.
     */
    private class Reference{
        private Reference(Node curr){
            this.point = curr;
        }
        private Node point;
        @Override public String toString(){
            return point.data.text;
        }
    }
    
    /**
     * an individual node in the linked list.
     */
    private class Node{
        private Node(FileManipScanner.C0Token data){this.data = data;}
        private FileManipScanner.C0Token data;
        private Node next(FileManipScanner.C0Token data){
            next = new Node(data);
            return next;
        }
        private void set(Node n){
            next = n.next;
            data = n.data;
        }
        private Node next;
    }
    
    /**
     * sews together two nodes; useful for deleting within two points in a 
     * single chain, or appending one chain to another.
     * 
     * sets the element after begin to the end
     * 
     * i.e.
     *      begin -> ........ -> end -> ........<br>
     *      begin -> end -> ........<br>
     * 
     * note - this method does not depend on this object, and does not (currently)
     * check whether or not the input args are actually from this TokenList.
     * So, theoretically, you can put in two marked references to nodes from 
     * completely different data structures into this structure and still have 
     * it work.
     */
    @Override public void sew(Object begin, Object end) {
        check(begin); check(end);
        //make sure that both nodes are cheesy
        
        //the beginning node.
        Node be = ((Reference)begin).point;
        //the ending node (what node we want to end up at)
        Node en = ((Reference)end).point;
        
        be.next = en;
    }
    
    /**
     * begin -> ........ -> current -> ........<br>
     * begin -> current -> ........<br>
     */
    @Override public void sew_before(Object begin) {
        check(begin);
        //make sure that the node is cheesy
        
        //the beginning node.
        Node be = ((Reference)begin).point;
        //the ending node (what node we want to end up at)
        
        be.next = current;
    }
    /**
     * ........ -> current -> ........ -> end -> ........<br>
     * ........ -> current -> end -> ........<br>
     */
    @Override public void sew_after(Object end) {
        check(end);
        //make sure that the node is cheesy
        
        //the beginning node.
        Node en = ((Reference)end).point;
        //the ending node (what node we want to end up at)
        
        current.next = en;
    }
    
    /**
     * begin -> ........ -> current -> next-> ........<br>
     * begin -> next -> ........<br>
     */
    @Override public void sew_before_inclusive(Object begin) {
        check(begin);
        //make sure that the node is cheesy
        
        
        ((Reference)begin).point.set(current);
    }
    /**
     * ........ -> current -> ........ -> end -> ........<br>
     * ........ -> end -> ........<br>
     */
    @Override public void sew_after_inclusive(Object end) {
        check(end);
        //make sure that the node is cheesy
        
        //the beginning node.
        Node en = ((Reference)end).point;
        //the ending node (what node we want to end up at)
        
        current = en;
    }
    
    /**
     * sew together two nodes; useful for deleting within two points in a 
     * single chain, or appending one chain to another.
     * 
     * i.e.
     *      begin -> ........ -> end -> next -> ........<br>
     *      begin -> next -> ........<br>
     * 
     * end's next node may be null.
     */
    @Override public void sew_inclusive(Object begin, Object end) {
        check(begin); check(end);
        //make sure that both nodes are cheesy
        
        //the beginning node.
        Node be = ((Reference)begin).point;
        //the ending node (what node we want to end up at)
        Node en = ((Reference)end).point;
        if(en == null) throw new IncorrectListElementException(end + " references to null");
        be.next = en.next;
    }
    
    
    
    /**
     * removes all nodes that follows this given node
     */
    @Override public void cut_after(Object idx) {
        check(idx);
        ((Reference)idx).point.next = null;
        //sets the next node to null
    }
    
    /**
     * deletes from the after the current index till the end of the file.
     */
    @Override public void cut_after() {
        current.next = null;
    }
    
    /**
     * deletes from the beginning of the file till the given index
     */
    @Override public void cut_before(Object idx) {
        check(idx);
        head = ((Reference)idx).point;
    }
    
    /**
     * deletes from the beginning of the file till the current index
     * (links the head to the current)
     */
    @Override public void cut_before() {
        head = current;
    }
    
    /**
     * marks the position with the returned obkect. Return the marker at the 
     * current index.
     */
    @Override public Object mark() {
        return new Reference(current);
    }
    
    /**
     * inserts an token after the current element. Does not increment the 
     * current element
     */
    @Override public void insert(C0Token elem) {
        Node next = new Node(elem);
        Node temp = current.next;
        current.next = next;
        next.next = temp;
    }
    
    /**
     * inserts an token after the current element.
     * 
     * @requires list does not have cycles (I'm assuming that the client will 
     * not be stupid in handling these)
     */
    @Override public void insert_list(Object list) {
        Node next = ((Reference)list).point;
        Node temp = current.next;
        current.next = next;
        for(;;){
            if(next.next == null){
                next.next = temp;
                return;
            } else{next = next.next;}
        }
    }
    
    /**
     * returns the this token and moves to the next element.
     * If there are no elements left, returns null.
     */
    @Override public C0Token next() {
        if(current == null)return null;
        C0Token temp = current.data;
        current = current.next;
        return temp;
    }
    
    /**
     * rewinds the position of the linked list, back to the beginning
     */
    @Override public void rewind() {
        current = head;
    }
    
    /**
     * O(n) operation. Use empty() if you want a faster operation
     * Use empty() if you want an insta-fast BS
     */
    @Override public int size() {
        Node n = head;
        int size = 0;
        while(n.data != null){
            size++;
            n = n.next;
        } return size;
    }
    
    /**
     * O(1) operation.
     */
    @Override public boolean empty(){
        return head == null;
    }
    
    /**
     * adds a vertex to the end of the list.
     */
    @Override public void add(C0Token elem) {
        set_end();
        end = end.next = new Node(elem);
    }
    
    /**
     * moves the position of the 'end' node to the end of the linked list
     */
    private void set_end(){
        if(end.next != null){
            //set the new end. We let the line grow in length without 
            //incrementing the end index. We only do this here to save time
            
            while((end = end.next).next != null);
            //traverse the tree and see if the next elem is null. Will break 
            //when we are at the last elem (which points to null)
        }
        assert end.next == null;//assert we are at the last node
    }
    
    /**
     * appends a list to the end of this file.
     */
    @Override public void append(Object list) {
        check(list);
        set_end();
        end.next = ((Reference)list).point;
    }
    /**
     * returns a token for a given reference
     */
    @Override public C0Token get(Object idx) {
        check(idx);
        return ((Reference)idx).point.data;
    }
    /**
     * checks if the reference is of the correct type
     */
    private static void check(Object a){
        if(a == null || a.getClass() != Reference.class)
            throw new IncorrectListElementException("input \""+ a + "\" is not of expected type " + Reference.class);
    }
    @Override public String toString(){
        StringBuilder sb = new StringBuilder();
        Node hh = head;
        if(hh == null || hh.data == null) return "";
        sb.append(hh.data.text).append(" ");
        while((hh = hh.next) != null){
            sb.append(hh.data.text).append(" ");
        }
        return sb.toString();
    }
    public Object peek(Object index){
        check(index);
        return new Reference(((Reference)index).point.next);
    }
}
class Ttest{
    public static void main(String[] args){
        TokenList tl = new TokenList(FileManip.generate("saf", "hello world!{ }"));
        System.out.println(tl);
        Object hello = tl.mark();
        tl.next();
        
        System.out.println("1 "+tl.next());
        System.out.println("2 "+tl.next());
        
        System.out.println(hello);
        
        //world is removed
        tl.cut_before(tl.mark());
        System.out.println(tl.peek(null)
        );
    }
}