/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

/**
 *
 * @author kieda
 */
public class Variables{
    private static compl.etc.C0Data line(compl.etc.C0Data data){
        if(data instanceof Pointable){
            return compl.etc.Types.POINTER(line(((compl.etc.Pointable)data).getPointerType()));
        } else if(data instanceof compl.etc.Arrayable){
            return compl.etc.Types.ARRAY(line(((compl.etc.Arrayable)data).getArrayType()));
        } else{
            switch(data.representation){
                case BOOL:
                    return compl.etc.Types.BOOL;
                case CHAR:
                    return compl.etc.Types.CHAR;
                case INT:
                    return compl.etc.Types.INT;
                default:
                    if(data instanceof C0Struct){
                        return data;
                    } else if(data instanceof Void){
                        throw new AssertionError("void cannot be a variable.");
                    } 
                    throw new AssertionError("unknown data type.");
            }
        }
    }
    static compl.etc.IntVar INT(String var_name){
        return new compl.etc.IntVar(var_name);
    }
    static compl.etc.BoolVar BOOL(String var_name){
        return new compl.etc.BoolVar(var_name);
    }
    static compl.etc.CharVar CHAR(String var_name){
        return new compl.etc.CharVar(var_name);
    }
    static compl.etc.ArraVar ARRAY(String var_name, compl.etc.C0Data array_type){
        return new compl.etc.ArraVar(var_name, array_type);
    }
    static compl.etc.PointVar POINT(String var_name, compl.etc.C0Data array_type){
        return new compl.etc.PointVar(var_name, array_type);
    }
    static compl.etc.StriVar STRING(String var_name){
        return new compl.etc.StriVar(var_name);
    }
    
    /**
     * creates a new C0 variable under the name "var_name" and of the type 
     * "var_type"
     */
    static compl.etc.C0Variable create(compl.etc.C0Data var_type, String var_name){
        if(var_type instanceof compl.etc.Pointable){
            return new compl.etc.PointVar(var_name, line(((compl.etc.Pointable)var_type).getPointerType()));
        } else if(var_type instanceof compl.etc.Arrayable){
            return new compl.etc.ArraVar(var_name, ((compl.etc.Arrayable)var_type).getArrayType());
        } else{
            switch(var_type.representation){
                case BOOL:
                    return new compl.etc.BoolVar(var_name);
                case CHAR:
                    return new compl.etc.CharVar(var_name);
                case INT:
                    return new compl.etc.IntVar(var_name);
                default:
                    if(var_type instanceof C0Struct){
                        throw new AssertionError("a struct cannot be a variable. Hint: pass structs using pointers.");
                    } else if(var_type instanceof Void){
                        throw new AssertionError("void cannot be a variable.");
                    }
                    throw new AssertionError("unknown data type.");
            }
        }
    }
}
