/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

import compl.data.FileManip;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author kieda
 */


/**a method that a C0 file holds.*/
class C0Interface{
    final C0Data[] inputs;//input variables
    
    final C0Data return_type; //the return type
    
    
    //non-final, so we can add multiple requires/ensures over multiple interface
    //declarations
    
    ArrayList<C0Interface.C0Requires> requires;
    ArrayList<C0Interface.C0Ensures> ensures;
    
    final String name;
    
    public C0Interface(C0Data[] inputs, C0Data return_type, ArrayList<C0Interface.C0Requires> requires, ArrayList<C0Interface.C0Ensures> ensures, String name){
        this.inputs      = inputs;
        this.return_type = return_type;
        this.requires    = requires;
        this.ensures     = ensures;
        this.name        = name;
    }
    public C0Interface(C0Data[] inputs, C0Data return_type, String name){
        this.inputs      = inputs;
        this.return_type = return_type;
        this.name        = name;
    }
    
    /**a requires statement that dictates the inputs of a C0 method*/
    class C0Requires extends C0Interface.C0Assert{}
    
    /**an ensures statement that dictates the outputs of a C0 method*/
    class C0Ensures extends C0Interface.C0Assert{}
    
    /**
     * a loop invariant that dictates whether the execution of a loop is proper.
     * used in the middle of a C0 method
     */
    class C0LoopInvar extends C0Interface.C0Assert{}
    
    /**
     * a basic invariant used to check whether or not a method is executing 
     * properly. Used in the middle of a C0 method.
     */
    class C0Assert{FileManip data;}
    @Override public String toString(){
        return return_type + " " + name  + Arrays.toString(inputs);
    }
    @Override public boolean equals(Object other){
        if(other instanceof String)
            return name.equals(other);
        else if(other instanceof C0Interface)
            return name.equals(((C0Interface)other).name);
        return false;
    }
    @Override public int hashCode(){
        return name.hashCode();
    }
}
