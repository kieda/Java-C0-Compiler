
//import java.util.HashMap;



/**All c0 methods have "private" at the beginning, as a modification. (also 
 * such that another class can't use this class's methods)**/

/**THE CLASS THAT HOLDS THE NAME**/
class ErrorTests {

    
    //old variables:    $var_name
    //special methods:  method_name$()
    //structs:          $struct_name$num_pointers
    //var names in native libraries: $$var_name$$
    //
    //    so           struct hi{int i1; inti2;};
    //    would be     private class hi$0{int i1; int i2;};
    //
    //    so           hi*  a  = alloc(...);
    //    would be     $hi$1 a  = new hi$1();
    //
    //    so           *a
    //    would be     a.p
    //
    //    so           (*a).i1
    //    would be     (a.p).i1
    //
    //    so           a->i1
    //    would be     a.p.i1
    //
    
    //pointers:        $pointer_type$num_pointers
    //    so           int** hi
    //    would be   
    //                 private class $int$1{int p;}
    //                 private class $int$2{int$1 p;}
    //                 $int$2 hi
    //
    //    so           *hi     = ___
    //    would be     hi.p    = ___
    //
    //    so           **hi    = ___
    //    would be     hi.p.p  = ___
    //
    
    //
    //array pointers:  array_type$$num_pointers
    //    so           int[]* hi
    //    would be
    //                 private class int$$1{int[] p;}
    //                 int$$1 hi
    //
    //    so           *hi     = ___
    //    would be     hi.p    = ___
    //    
    //    so           *hi[n]  = ___
    //    would be     hi.p[n] = ___
    
    
/**BEGIN included libraries for java**/
private int divide$(int i1, int i2){
    if(i1 == Integer.MIN_VALUE && i2 == -1) throw new ArithmeticException("Floating exception: INT_MIN divided by -1");
    else if(i2 == 0) throw new ArithmeticException("Floating exception: division by zero");
    return i1/i2;
}
private int mod$(int i1, int i2){
    if(i1 == Integer.MIN_VALUE && i2 == -1) throw new ArithmeticException("Floating exception: INT_MIN mod by -1");
    else if(i2 == 0) throw new ArithmeticException("Floating exception: mod zero");
    return i1%i2;
}
/**END included libraries for java**/
private class $args$1 {
    $args$0 p;
}
private class $args$0 {
    int argc;
    String[] argv;
}
private $args$1 $$$ARGS$$$;
private java.util.HashMap<String, Object> $$$args_pointers$$$;
/**Begin Java entry point*/
public static void main(String[] s){new ErrorTests(s);}
private ErrorTests(String[] s){
$$$args_pointers$$$ = new java.util.HashMap<String, Object>();
$$$ARGS$$$ = new $args$1(); $$$ARGS$$$.p.argc = s.length; $$$ARGS$$$.p.argv = s;
System.out.println(main());}

private class $int$1{
    int p;
}
private class $boolean$1{
    boolean p;
}
private class $String$1{
    String p;
}
/**End Java entry point*/

/**begin C0 main method*/
private int main(){/*@ensures \result == 0;*/try{
        something("1hello");
//        println(readline());
//        println(readline());
//        println(readline());
        /**
         * return 0
         *    changed to
         * {int NULL = 0; if(!(true)) {throw new AssertionError("ensures::user function::ErrorTests::main()::true");} return NULL;}
         */
//        return 0;
        
        //\result translates to NULL
        {int NULL = 0; 
            //assertion
            if(!(NULL == 0)) {throw new AssertionError("ensures::user function::ErrorTests::main()::NULL == 0");} 
        return NULL;}//return \result
        
}catch(AssertionError e){String[] data = e.getMessage().split("::"); String r= "";if(data[0].equals("prompt")) r += "error: " + data[4]; else r+="assertion failed:\n\t@"+data[0]+" "+data[4]+ ";\n";
if(data[1].equals("native function"))r+=("\tin native library "+data[2] + " : function " + data[3] + "\n"); 
else r+=("\tin user file "+data[2] + " : function \"" + data[3] + "\"\n");
System.err.println(r+"(core dumped)"); System.exit(0);throw new AssertionError();}

catch(Exception e){String m=e.getMessage();String r="";
if(e instanceof ArithmeticException)r+=(m==null)?"Unknown arithmetic exception":m;
else if(e instanceof ArrayIndexOutOfBoundsException)r+="Out of bounds array access: "+((m==null)?"Unknown array index":("index "+m))+"\nSegmentation fault"; 
else if(e instanceof NullPointerException)r+="Attempt to dereference null pointer\nSegmentation fault";else r+="Unknown Exception";
System.err.println(r+" (core dumped)"); System.exit(0);throw new AssertionError();} 
}
/**end C0 main method*/
private void hrekj$kjfsadf(){}
private void a$$(){}//this is something that we can expliot in java, as it is unusable in C0
/**BEGIN user-defined methods**/
private void something(String s){
    String $s = new String(s);
        //we copy the values of the old into the new. For Strings, we do new String(string);
    //@requires string_charat(s, 0) == '1';
    if(!(string_charat(s, 0) == '1')){ throw new AssertionError("requires::user function::ErrorTests::something(string s)::string_charat(s, 0) == '1'");}
    //@ensures string_equal(s, \old(s));
    
    try{
        /** BEGIN OF METHOD **/

            //string 
            //    converted to 
            //String
            
            //Integer.MIN_VALUE/-1 
            //     converted to 
            //mod(Integer.MIN_VALUE, -1)
//            int i = mod(Integer.MIN_VALUE, -1);
            //int[] r = alloc_array(int, 1) 
            //    converted to
            //int[] r = new int[1];
            int[] r = new int[1];
            //same.
//            r[-2] = 0;
            //same.
            printint(1);
            println(s);
            String g = "1hell";
            s = g;
        /** END OF METHOD **/
            //ensures statement, because this type is void AND has no return
            //statements. (if there are return statements, we add on the ensures
            //just before the return, and after the method exits.)
            
            //@ensures string_equal(s, \old(s));
            if(!(string_equal(s, $s))) throw new AssertionError("ensures::user function::ErrorTests::void something(string s)::string_equal(s, \\old(s))");
}catch(AssertionError e){String[] data = e.getMessage().split("::"); String r= "";if(data[0].equals("prompt")) r += "error: " + data[4]; else r+="assertion failed:\n\t@"+data[0]+" "+data[4]+ ";\n";
if(data[1].equals("native function"))r+=("\tin native library "+data[2] + " : function " + data[3] + "\n"); 
else r+=("\tin user file "+data[2] + " : function \"" + data[3] + "\"\n");
System.err.println(r+"(core dumped)"); System.exit(0);throw new AssertionError();}

catch(Exception e){String m=e.getMessage();String r="";
if(e instanceof ArithmeticException)r+=(m==null)?"Unknown arithmetic exception":m;
else if(e instanceof ArrayIndexOutOfBoundsException)r+="Out of bounds array access: "+((m==null)?"Unknown array index":("index "+m))+"\nSegmentation fault"; 
else if(e instanceof NullPointerException)r+="Attempt to dereference null pointer\nSegmentation fault";else r+="Unknown Exception";
System.err.println(r+" (core dumped)"); System.exit(0);throw new AssertionError();} 
}


}/*END of class that holds the file name*/
