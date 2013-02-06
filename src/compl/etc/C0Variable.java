/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

/**
 *
 * @author kieda
 */
class C0Variable extends C0Data{
    final String c0_name;//the name of the variable. does not change.
    
    public C0Variable(C0Data.SmallType representation, String c0_name){
        super(representation);
        this.c0_name = c0_name;
    }
    @Override public String toString(){
        return super.toString() +" "+ c0_name;
    }
}