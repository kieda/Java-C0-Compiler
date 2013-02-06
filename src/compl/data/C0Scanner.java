/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.data;

import com.kieda.data_structures.Queue;
import com.kieda.data_structures.character.CharStack;
import java.util.Iterator;

/**
 *
 * @author kieda
 */
class Test{
    public static void main(String[] args){
        C0Scanner cos = new C0Scanner("hello <world> saf sadf nn/****/ sadf \"\"\" kjasd");
        for(String s: cos){
            System.out.println(s);
        }
    }
}
public class C0Scanner implements Iterable<String>, Iterator<String> {
    private CharStack cs = new CharStack();
    private C0TokenParser top = new C0TokenParser();
    private Character curr_char = null;
        //used to iterate in the reverse order that we put them on.
    public C0Scanner(String s){
        char[] cc = s.toCharArray();
        for(int i = cc.length-1; i >= 0; i--)
            cs.push(cc[i]);
        if(!cs.isEmpty()){
            curr_char = cs.pop();
        }
        //abcd
        //[a, b, c, d]
        
        //dcba
    } @Override public Iterator<String> iterator() {
        return this;
    } @Override public boolean hasNext() {
        return !cs.isEmpty() || curr_char != null;
    } @Override public String next() {
//        if(cs.isEmpty()) return null;
        return nextToken();
        
    } @Override public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    private String nextToken(){
        boolean to_rewind = true;
        
        top.next = top.head;        
        String n = null;
        boolean b;
        boolean todo;
        bra: while(b= hasNext()){
                todo = !cs.isEmpty();
                if(n == null) n = "";
                int action = top.next.action(curr_char);
                if(action < 0){
                    action = -action;
                }
                switch(action){
                    case C0TokenParser.EXIT_SUCCESSFUL_REWIND:
                        break bra;
                    case C0TokenParser.ERROR_AND_RECORD:
                    case C0TokenParser.CONTINUE_AND_RECORD:
                        n+=curr_char; if(todo)curr_char = cs.pop();else curr_char = null; break;
                    case C0TokenParser.EXIT_SUCCESSFUL:
                        n+=curr_char; if(todo)curr_char = cs.pop(); else curr_char = null; break bra;
                    case C0TokenParser.EXIT_UNSUCCESSFUL: break bra;
                    case C0TokenParser.UNKNOWN_CHAR:
                        n = curr_char + ""; 
                        if(todo)curr_char = cs.pop();else curr_char = null;
                        return n;
                    case C0TokenParser.CONTINUE:
                        if(todo)curr_char = cs.pop();else curr_char = null;
                        break;
                    case C0TokenParser.CONTINUE_AND_REWIND:
                        break;
                }
            }
            if(b == false) 
            {   
                if(n!= null){
                    String g = n;
                    n = null;
                    return g;
                }
//                if(curr_char != null){
//                    char c = curr_char;
//                    curr_char = null;
//                    return ""+c;
//                } 
                else
                    return null;
            }
        return n;
    }
}
