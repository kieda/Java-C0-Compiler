package compl.etc;

import com.kieda.util.error.OutOfMasksError;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class, coffee, is used to interface with the imports. You should use the
 * final integer flags included in this class, but you may also use your own.
 * There is a maximum number of 32 flags, as the user just specifies the flags
 * needed by a binary mask (only one integer). Luckily for me, there are few
 * enough c0 libraries that this method should hold for a while. If not, this 
 * will be modified to a long mask for a new temporary fix! (actually, more 
 * likely BigInts..)
 * 
 * This class gives the c0 representation of structs and 
 * 
 * Also, all of these functions are (literally) copied directly from the 
 * into the c0 file
 * @author kieda
 */
public class coffee {
    public static void brew(){}
    private static HashMap<String, Integer> rev = new HashMap<String, Integer>();
    private static HashMap<Integer, String> imports = new HashMap<Integer, String>();
    public static int makeImport(String import_name){
        Integer gg = rev.get(import_name);
        if(gg != null)
            return gg.intValue();
        else{
            if(max_shift == 32) throw new OutOfMasksError();
            int ret = 1<<max_shift;
            max_val |= ret;
            imports.put(ret, import_name);
            rev.put(import_name, ret);
            max_shift++;
            return ret;
        }
    }
    /**the include masks. Just bitwise OR the ones you want.*/
    public static final int ARGS   = makeImport("args");    //0b000001;
    public static final int CONIO  = makeImport("conio");   //0b000010;
    public static final int FILE   = makeImport("file");    //0b000100;
    public static final int IMG    = makeImport("img");     //0b001000;
    public static final int PARSE  = makeImport("parse");   //0b010000;
    public static final int STRING = makeImport("string");  //0b100000;
        //should be the imports associated with the standatrd C0 library.
        //addotional masks can be made 
    
    //CURSES is currently not available, because there is no documentation on 
    //it.
    
    private static File NATIVE_FILE; 
        // the native file we're invesitigating
    private static int max_shift = 0;
    private static int max_val = 0;
    public static String getImportName(int mask){
        return imports.get(mask);
    }
    
    
    /**
     * returns the import if the name is known, and returns -1 otherwise
     */
    public static int getImport(String import_name){
        Integer gg = rev.get(import_name);
        if(gg != null)
            return gg.intValue();
        return -1;
    }
    
    /**
     * Opens the files that contain the native functions.
     * Returns true iff the file was found and is readable
     */
    public static boolean open_native(String file){
        File f = new File(file);
        if(! f.canRead()) return false;
        NATIVE_FILE = f;
        return true;
    }
//    /**
//     * Finds by an enumeration search by
//     */
//    public static boolean find_and_open(){
//        //find the file by an enumeration search the file that contains the 
//        //native_fx code at the top.
//    }
    public static compl.etc.ImportParse.ImportPackage include(int[] mask){
        int main = 0;
        for(int m: mask){
            assert (m & main) == 0;//we don't want a mask that takes up more than one position (no overlap)
            main |=m;
        }
        return include(main);
    }
    //works for lists, collections, etc.
    public static compl.etc.ImportParse.ImportPackage include(Iterable<Integer> mask){
        int main = 0;
        for(int m: mask){
            assert (m & main) == 0;//we don't want a mask that takes up more than one position (no overlap)
            main |=m;
        }
        return include(main);
    }
    /**
     * include by OR-ing several masks together, like
     * ARGS | CONIO | FILE
     * 
     * order does not matter.
     */
    public static compl.etc.ImportParse.ImportPackage include(int mask){
        assert (mask & max_val) == 0 : "bad mask.";
        int maskn = mask;
        int mask_num = 0;
        List<String> includes = new ArrayList<String>();
        while(mask_num < max_shift){
            int mm = 1 << mask_num;
            if(((maskn)& mm) != 0){
                includes.add(imports.get(mm));
            }
            mask_num++;
        }
        String[] arr = new String[includes.size()];
        for(int i = 0; i < arr.length; i++){
            arr[i] = includes.get(i);
        }
        return compl.etc.ImportParse.parseIML(NATIVE_FILE.getPath(), arr);
    }
}