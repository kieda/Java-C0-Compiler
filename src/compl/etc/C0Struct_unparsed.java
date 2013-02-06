/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

import compl.data.FileManip;

class C0Struct_unparsed extends C0Struct{
    FileManip internals;
    public C0Struct_unparsed(FileManip internals, String struct_name){
        super(struct_name);
        this.internals = internals;
    }
    @Override public String toString(){
        return super.toString() + "{" + internals + "};";
    }
}
