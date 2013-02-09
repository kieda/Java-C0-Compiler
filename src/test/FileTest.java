/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import org.kieda.data_structures.Stack;
import compl.data.FileManip;
import compl.data.FileManipScanner;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

/**
 *
 * @author kieda
 */
public class FileTest {
    public static void fileCreationTest(){
        File f = new File("./hello.class");
        System.out.println(f.getPath());
        System.out.println(f.getAbsoluteFile());
        System.out.println(f.getParentFile().getPath());
        
        //a quick work-around for the fact that you can't create a file nested
        //in many folders.
        File highest_parent = f.getParentFile();
        //lightweight Stack courtesy of the Kieda Framework!
        Stack<File> files = new Stack<File>();
        while(!highest_parent.exists()){
            files.push(highest_parent);
            highest_parent = highest_parent.getParentFile();
        }
        f.canRead();
        
        while(!files.isEmpty()){
            File ff = files.pop();
            ff.mkdir();
        }
//        if(!f.getParentFile().exists()){
//            f.getParentFile().mkdir();
//        }
        System.out.println(f.getParentFile().exists());
    }
    public static void readFileTest(){
        //we want to test what a file that contains "\n" will actually return.
        File f = new File("./src/readFileTest");
        String s = org.kieda.util.ReadFile.read(f);
        String[] ss = s.split("\n");
        System.out.println(Arrays.toString(ss));
    }
    public static void main(String[] args){
        
        FileManip fm = FileManip.generate("a", "sadkjfsa dfksa dfa");
        FileManipScanner fs = new FileManipScanner(fm);
        //Pattern.print(Pattern.DECIMAL_NUMBER);
        Pattern p = Pattern.HEX_NUMBER;
        Pattern.print(p);
        System.out.println("\n"+p.valid("0XA"));
        System.exit(0);
        
        BufferedImage bi = new BufferedImage(30, 40, BufferedImage.TYPE_INT_ARGB);
        bi.getSubimage(20, 20, 1, 1);
        System.exit(0);
//        fileCreationTest();
        readFileTest();
    }
}
