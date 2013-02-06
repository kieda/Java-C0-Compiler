/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

/**
 *
 * @author kieda
 */
public class Arra extends compl.etc.SmallT implements compl.etc.Arrayable{
    final compl.etc.C0Data type;
//    public Arra(){super(SmallType.ARRAY);}
    public Arra(compl.etc.C0Data type){
        super(compl.etc.C0Data.SmallType.ARRAY);
        this.type = type;
    }
    @Override public compl.etc.C0Data getArrayType(){
        return type;
    }
}