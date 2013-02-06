/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;

/**
 *
 * @author kieda
 */
public class ExtendingArrayTest {
    
    public static void main(String[] args){
        
        
        C0Method cm = new C0Method(new C0Variable[]
            {
                new C0Variable(C0Data.SmallType.INT, "i"),
                new C0Variable(C0Data.SmallType.BOOL, "b"),
                new C0Variable(C0Data.SmallType.POINTER, "point")
            });
        System.out.println(cm);
    }
}
class C0Data{    
    final SmallType representation;
        //the representation of the C0 data. A LargeType (Struct) is represented
        //by a pointer
    public C0Data(SmallType representation){
        this.representation = representation;
    
    }
    enum SmallType{
        INT("int"), CHAR("char"), BOOL("boolean"), STRING("string"), ARRAY("array"), POINTER("pointer");
        final String name;
        private SmallType(String name) {
            this.name = name;
        }
    }
    enum LargeType{
        STRUCT
    }
    
}
class C0Variable extends C0Data{
    final String c0_name;//the name of the variable. does not change.
    
    public C0Variable(SmallType representation, String c0_name){
        super(representation);
        this.c0_name = c0_name;
    }
}
class C0Interface{
    final C0Data[] inputs;//input variables
    
    public C0Interface(C0Data[] inputs){
        this.inputs = inputs;
    }
    @Override public String toString(){
        String ret = "[";
        for(int i = 0; i < inputs.length; i++){
            ret += inputs[i].representation.name +", ";
        }
        if(inputs.length != 0) ret += "\b\b";
        ret += "]";
        return ret;
    }
}
class C0Method extends C0Interface{
    final String[] c0_names;
    public C0Method(C0Variable[] inputs){
        super(inputs);
        String[] names = new String[inputs.length];
        for(int i = 0; i < inputs.length; i++){
            names[i] = inputs[i].c0_name;
        }
        this.c0_names = names;
    }
    @Override public String toString(){
        String ret = "[";
        for(int i = 0; i < c0_names.length; i++){
            ret += inputs[i].representation.name + " " + c0_names[i]+", ";
        }
        if(c0_names.length != 0) ret += "\b\b";
        ret += "]";
        return ret;
    }
}