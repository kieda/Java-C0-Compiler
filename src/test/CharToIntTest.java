

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//import java.util.Scanner;


/**
 *
 * @author kieda
 */
public class CharToIntTest {
    final static  String WORD = "\\w*[,]";
    final static String COMMA_WS = "\\s*|,";
    static Scanner s = new Scanner("hello,my_name,is,homer\n");
    static void next(String pat){
        if(s.hasNext()){
            System.out.println(s.next());
        }
    }
    public static void main(String[] args){
//        while(s.hasNext()){
//            System.out.println(s.next());
//        }
//        next(WORD);
//        next(COMMA_WS);
        for(int i = 0; i < 128; i++){
            char c = (char)i;
            System.out.println(i + " : \"" + c + "\"");
            
        }
    }
}
class NumbersConsole {

    private static String ttyConfig;

    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        if(true)return;
            try {
                    setTerminalToCBreak();

                    int i=0;
                    while (true) {

//                            System.out.println( ""+ i++ );

//                            if ( System.in.available() != 0 ) {
                                    int c = System.in.read();
                                    if ( c == 0x1B ) {
                                            break;
                                    }
//                            }

                    } // end while
            }
            catch (IOException e) {
                    System.err.println("IOException");
            }
            catch (InterruptedException e) {
                    System.err.println("InterruptedException");
            }
            finally {
                try {
                    stty( ttyConfig.trim() );
                 }
                 catch (Exception e) {
                     System.err.println("Exception restoring tty config");
                 }
            }

    }

    private static void setTerminalToCBreak() throws IOException, InterruptedException {

        ttyConfig = stty("-g");

        // set the console to be character-buffered instead of line-buffered
        stty("-icanon min 1");

        // disable character echoing
        stty("-echo");
    }

    /**
     *  Execute the stty command with the specified arguments
     *  against the current active terminal.
     */
    private static String stty(final String args)
                    throws IOException, InterruptedException {
        String cmd = "stty " + args + " < /dev/tty";

        return exec(new String[] {
                    "sh",
                    "-c",
                    cmd
                });
    }

    /**
     *  Execute the specified command and return the output
     *  (both stdout and stderr).
     */
    private static String exec(final String[] cmd)
                    throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in = p.getInputStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        in = p.getErrorStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        p.waitFor();

        String result = new String(bout.toByteArray());
        return result;
    }

}