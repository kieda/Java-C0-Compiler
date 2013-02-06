/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

import compl.data.FileManip;

/**
 *
 * @author kieda
 */
class C0TypeDef_parsed implements C0TypeDef{
    //we cannot define a type more than once. 
    final String name;//the name that is being used in place
    final C0Data type;//the type that is being replaced by name
    public C0TypeDef_parsed(String name, C0Data type){
        this.name = name;
        this.type = type;
    }
    @Override public int hashCode(){
        return name.hashCode();
    }
    @Override public boolean equals(Object other){
        if(other instanceof String){
            return name.equals(other);
        } else if(other instanceof C0TypeDef_parsed){
            return name.equals(((C0TypeDef_parsed)other).name);
        }
        return false;
    }
    @Override public String toString(){
        return "typedef " + type + " " + name;
    }
}
class C0TypeDef_unparsed implements C0TypeDef{
    String name;
    FileManip data;
    C0TypeDef_unparsed(String name, FileManip data){
        this.name = name;
        this.data = data;
    }
    public String toString(){
        return "typedef " + data;
    }
}