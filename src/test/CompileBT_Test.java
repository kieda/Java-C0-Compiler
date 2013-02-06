/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package test;

/**
 *
 * @author kieda
 */
class CompileBT_Test {
    private void printint(int a){
        System.out.print(a);
    }
    
    private int main(){
        
        struct1 s1 = new struct1();
        s1.a = 3;
        s1.b = -3;
        printint(s1.a);
        s1 = flip(s1);
        
        return s1.a;
    }
    private class struct1{
        int a;
        int b;
    };
    private struct1 flip(struct1 s1){
        struct1 s2 = new struct1();
        s2.a = s1.b;
        s2.b = s1.a;
        return s2;
    }
public static void main(String[] args){ new CompileBT_Test();} 
private CompileBT_Test(){System.out.println(main());}
}