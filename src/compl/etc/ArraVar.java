/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;
/**
 *
 * @author kieda
 */
class ArraVar extends compl.etc.SmallTypeVar implements compl.etc.Arrayable{
    compl.etc.C0Data type;
    public ArraVar(String var_name, compl.etc.C0Data array_type){super(compl.etc.C0Data.SmallType.ARRAY, var_name); this.type = array_type;}
    @Override public compl.etc.C0Data getArrayType(){
        return type;
    }
}
