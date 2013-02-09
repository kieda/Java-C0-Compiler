/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import org.kieda.data_structures.StackSet;

/**
 *
 * @author kieda
 */
public class StackSetTest {
    public static void main(String[] args){
        StackSet<String> set = new StackSet<String>();
        /*{*/set.pushQueue();
                /*int hello1;*/
                set.queueElement("hello1");
                /*int hello2;*/
                set.queueElement("hello2");
                
                /*{*/set.pushQueue();
                        /*int no_way;*/
                        set.queueElement("no_way");
                        /*int bro;*/
                        set.queueElement("bro");
                        /*{*/set.pushQueue();
                                //nothing
                        /*}*/set.dump();
                /*}*/set.dump();
                /*{*/set.pushQueue();
                        /*int bro;*/
                        set.queueElement("bro");
                        /*int no_way;*/
                        set.queueElement("no_way");
                /*}*/set.dump();
        /*}*/set.dump();
    }
}
