/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import compl.data.FileManip;
import compl.data.FileManipScanner;
import java.io.File;

/**
 *
 * @author kieda
 */
public class FileManipulationTest {
    public static void main(String[] args){
        FileManip this_file = new FileManip(new File("./src/include/string"));
        System.out.println(this_file + "\n");
        FileManip.TextPosition ttp = new FileManip.TextPosition(2, 1);
        if(ttp.getColumn() == 0){
            this_file.delete(ttp.getLine());
        } else
            this_file.delete(new FileManip.Range(ttp, new FileManip.TextPosition(ttp.getLine(), this_file.getLine(ttp.getLine()).length())));
        
        System.out.println(this_file);
        System.exit(0);
        FileManip fm1 = new FileManip(new File("./src/Test.c0"));
        FileManipScanner fms = new FileManipScanner(fm1);
        FileManipScanner.Word w;
        while((w=fms.nextToken()) != null){
            System.out.println(w.text);
        }
//        {
        System.exit(0);
            FileManip fm2 = new FileManip(new File("./src/idea"));
            FileManip fmm = new FileManip(new File("./src/Test.java"));
    //        System.out.println(fm1);
    //        System.out.println(fm2);
            fmm.append(fm1);
//        fmm.splitline(new FileManip.TextPosition(4, 1));
        
        fmm = fmm.subRange(new FileManip.Range(0, 2, 5, 1));
//        fmm = fmm.subRange(new FileManip.Range(5, 0, 5, 6));
//            fmm.delete(new FileManip.Range(0, 2, 2, 20));
//            fm1.attach(fmm).attach(fm2);
//        FileManip.TextPosition tp1 = new FileManip.TextPosition(11, 10);
//        FileManip.TextPosition tp2 = fm1.getEndPosition();
//        FileManip fm3 = fm1.subRange(new FileManip.Range(tp1,
//                tp2));
//        }//gc fm2
        fmm.delete(5);
        System.out.println(fmm);
        
        fmm.joinLinePrev(4);
        System.out.println(fmm);
        
    }
}
