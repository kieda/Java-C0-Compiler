/**a basic test of the native java compiler*/
public class Test{
    public static void main(String[] args){
        String s = "hello world";
        System.out.println(s);
        int tot = 0;
        for(int i = 0; s.length(); i++){
            tot += i;
        }
        System.out.println("and the total is: "+tot);
    }
}