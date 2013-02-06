/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

/**
 *
 * @author kieda
 */
/**a struct that a C0 file holds.*/
class C0Struct extends LargeT {
    final String struct_name;//by definition, a struct has a name.
    public C0Struct(String struct_name){
        super();
        this.struct_name = struct_name;
    }
    @Override public boolean equals(Object other){
        if(other instanceof C0Struct)
            return struct_name.equals(((C0Struct)other).struct_name);
        else if(other instanceof String)
            return struct_name.equals(other);
        else return false;
    }
    @Override public int hashCode(){
        return struct_name.hashCode();
    }
    @Override public String toString(){
        return "struct "+struct_name;
    }
}
