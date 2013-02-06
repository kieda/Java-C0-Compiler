/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

/**
 *
 * @author kieda
 */
/*********************************Data in C0***********************************/
public class C0Data implements Method.ExpressionElement<C0Data, C0Data>{    
    final SmallType representation;
        //the representation of the C0 data. A LargeType (Struct) is represented
        //by a pointer
    public C0Data(SmallType representation){
        this.representation = representation;
    }

    @Override public int numArgs() {
        return 0;
    }
    @Override public C0Data relate(C0Data[] x) {
        assert x.length == numArgs();
        return this;
    }
    
    public enum SmallType{
        INT, CHAR, BOOL, STRING, ARRAY, POINTER
    }
    public enum LargeType{
        STRUCT
    }
    
    public String dataString(){
        switch(representation){
            case INT:    return "int";
            case CHAR:   return "char";
            case BOOL:   return "bool";
            case STRING: return "string";
            case ARRAY:  return ((Arrayable)this).getArrayType() +"[]";
            case POINTER:return ((Pointable)this).getPointerType() +"*";
        }
        return "";
    }
    @Override public String toString(){
        return dataString();
    }
    
    @Override public boolean equals(Object other){
        if(other instanceof String)
            return dataString().equals(other);
        else if(other instanceof C0Data)
            return ((C0Data)other).dataString().equals(dataString());
        else return false;
    }
    @Override public int hashCode(){
        return toString().hashCode();
    }
}
