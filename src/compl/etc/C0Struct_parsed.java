/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

import java.util.HashMap;

class C0Struct_parsed extends C0Struct{
    
    /**The data that the struct holds */
    final C0Variable[] types;//the types that go in a C0Struct
    private final HashMap<String, C0Variable> vars;
    public C0Struct_parsed(C0Variable[] types, String struct_name){
        super(struct_name);
        this.types = types;
        HashMap<String, C0Variable> vars = new HashMap<String, C0Variable>();
        for(C0Variable cv : types){
            vars.put(cv.c0_name, cv);
        }
        this.vars = vars;
    }
    public C0Variable getVariable(String name){
        return vars.get(name);
    }
}