/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;
/**
 * large types cannot be variables, so there is no LargeTypeVar. Instead,
 * we have C0Struct which has a struct_name given by the programmer.
 */


/**Large-Types*/
class LargeT extends C0Data implements LType{
    public LargeT(){
        super(C0Data.SmallType.POINTER);
            //large types MUST be represented by a pointer
    }
}