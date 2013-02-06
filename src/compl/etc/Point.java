/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

/**
 * represents a pointer
 * @author kieda
 */
public class Point extends SmallT implements Pointable{
    final C0Data content;
//    public Point(){super(SmallType.POINTER);}
    public Point(C0Data content){super(SmallType.POINTER);this.content = content;}
    @Override public C0Data getPointerType(){
        return content;
    }
}
