/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

import compl.data.FileManip;
import java.util.ArrayList;

/**
 * a holder where the method inputs, outputs, and name are parsed; but the body
 * itself is not parsed.
 * represents the basic wrapper for a c0 method that does not have its contentes
 * parsed.
 * @author kieda
 */
class C0Method_unparsed extends C0Interface{
    class C0MethodBody{public C0MethodBody(FileManip data){this.data = data;}FileManip data;}
    C0Method_unparsed.C0MethodBody body;
    final String[] c0_names;
    public C0Method_unparsed(C0Variable[] inputs, C0Data return_type, ArrayList<C0Interface.C0Requires> requires, ArrayList<C0Interface.C0Ensures> ensures, String name, C0Method_unparsed.C0MethodBody body){
        super(inputs, return_type, requires, ensures, name);
        String[] names = new String[inputs.length];
        for(int i = 0; i < inputs.length; i++){
            names[i] = inputs[i].c0_name;
        }
        this.body = body;
        this.c0_names = names;
    }
    public C0Method_unparsed(C0Variable[] inputs, C0Data return_type, String name, C0Method_unparsed.C0MethodBody body){
        super(inputs, return_type, name);
        this.body = body;
        String[] names = new String[inputs.length];
        for(int i = 0; i < inputs.length; i++){
            names[i] = inputs[i].c0_name;
        }
        this.c0_names = names;
    }
    public C0Method_unparsed(C0Variable[] inputs, C0Data return_type, String name, FileManip body){
        super(inputs, return_type, name);
        this.body = new C0MethodBody(body);
        String[] names = new String[inputs.length];
        for(int i = 0; i < inputs.length; i++){
            names[i] = inputs[i].c0_name;
        }
        this.c0_names = names;
    }
}