/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;
/**
 *
 * @author kieda
 */

class PointVar extends compl.etc.SmallTypeVar implements compl.etc.Pointable{
    compl.etc.C0Data content;
    public PointVar(String var_name, C0Data pointer_to){super(compl.etc.C0Data.SmallType.POINTER, var_name);content = pointer_to;}
    @Override public compl.etc.C0Data getPointerType(){
        return content;
    }
}