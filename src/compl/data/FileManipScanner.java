/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.data;

import java.awt.event.KeyEvent;
import java.io.File;

public class FileManipScanner {
    private FileManip.TextPosition curr_pos;
    private FileManip fm;
    public FileManipScanner(FileManip fm){
        this.fm = fm;
        curr_pos = new FileManip.TextPosition(fm.getBeginPosition().getLine(), fm.getBeginPosition().getColumn());
    }
    public void reset(){
        skipTo(fm.getBeginPosition());
    }
    public FileManip getFileManip(){
        return fm;
    }
    public FileManip.TextPosition finalPos(){
        int line_num = fm.getEndPosition().getLine();
        FileManip.TextPosition final_pos = null;
        int col_num = fm.getLine(line_num).length();
        syn:for(;line_num >= 0; line_num-- ){
            String line = fm.getLine(line_num);
//            Character.isSpaceChar(ch)chr(13);
            char c;
            col_num = line.length()-1;
            for(;col_num >= 0;col_num--){
                
                c = line.charAt(col_num);
                if(c != '\t' && c != ' '){
                    break syn;
                }
            }
        }
        final_pos = new FileManip.TextPosition(line_num, col_num);
        return final_pos;
    }
    /**
     * returns the next non-whitespace string of characters.
     */
    public Word next(){
        if(!hasNext()){
            throw new AssertionError("no more words left.");
        }
        //returns the next word in FileManip format
        int line_num = curr_pos.getLine();
        int col_num = curr_pos.getColumn();
        String word = "";
        FileManip.TextPosition word_begin = null;
        int cols = 0;
        syn:for(;line_num <= fm.getEndPosition().getLine(); line_num++ ){
            String line = fm.getLine(line_num);
//            Character.isSpaceChar(ch)chr(13);
            char c;
            for(;col_num < line.length();col_num++){
                
                c = line.charAt(col_num);
                if(c != '\t' && c != ' '){
                    
                    cols = col_num;
                    w:while(c != '\t' && c != ' '){
                        
                        word+=c;
                        cols++;
                        if(cols >= line.length()) break w;
                        c = line.charAt(cols);
                    }
                    break syn;
                }
            }
            col_num = 0;
        }
        word_begin = new FileManip.TextPosition(line_num, col_num);
        curr_pos = new FileManip.TextPosition(line_num, col_num + word.length());
        return new Word(word, word_begin);
    }
    private Word next(FileManip.TextPosition curr_pos){
        if(!hasNext()){
            throw new AssertionError("no more words left.");
        }
        //returns the next word in FileManip format
        int line_num = curr_pos.getLine();
//        System.out.println("1.. "+line_num);
        int col_num = curr_pos.getColumn();
        String word = "";
        FileManip.TextPosition word_begin = null;
        syn:for(;line_num <= fm.getEndPosition().getLine(); line_num++ ){
            String line = fm.getLine(line_num);
//            Character.isSpaceChar(ch)chr(13);
            char c;
            for(;col_num < line.length();col_num++){
                
                c = line.charAt(col_num);
                if(c != '\t' && c != ' '){
                    
                    int cols = col_num;
                    w:while(c != '\t' && c != ' '){
                        
                        word+=c;
                        cols++;
                        if(cols >= line.length()) break w;
                        c = line.charAt(cols);
                    }
                    break syn;
                }
            }
            col_num = 0;
        }
        word_begin = new FileManip.TextPosition(line_num, col_num);
        this.curr_pos = new FileManip.TextPosition(line_num, col_num+word.length());
//        System.out.println("2.. "+line_num);
        //might take this out.
        return new Word(word, word_begin);
    }
    public boolean hasNext(){
        return curr_pos.compareTo(finalPos())<0;
    }
    /**
     * we go till we find the next instance of the given string in this 
     * FileManip. Also returns the text position at which it occurred, as part 
     * of the Word.
     * returns null if the sequence is not present.
     */
    public Word next(String sequence){
        int line_num = curr_pos.getLine();      
        for(; line_num <= fm.getEndPosition().getLine(); line_num++ ){
            int idx;
            if(line_num == curr_pos.getLine()){
//                System.out.println(fm.getLine(line_num).substring(curr_pos.getColumn(), fm.getLine(line_num).length()));
                
                
                idx = fm.getLine(line_num).substring(curr_pos.getColumn(), fm.getLine(line_num).length()).indexOf(sequence);
                if(idx!=-1){
                    return next(new FileManip.TextPosition(line_num, idx+curr_pos.getColumn()));
                }
            }
            else{
                idx = fm.getLine(line_num).indexOf(sequence);
                if(idx!=-1){
//                    System.out.println(idx);
                    //we found it!
                    return next(new FileManip.TextPosition(line_num, idx));
                }
            }
        }
        return null;
    }
    
    private C0TokenParser cos = new C0TokenParser();
    
    public Character nextChar(){
        if(!hasNextCharPosition()) return null;
        setNextCharPosition();
        if(fm.isOutOfBounds(curr_pos)) return '\n';
        return fm.getCharAt(curr_pos);
    }
    
    boolean first = true;
    /**
     * returns the next C0 token.
     * will log an error if there is invalid C0 syntax.
     */
    public C0Token nextToken(){
        
        boolean to_rewind = true;
       /**
        * let's work out what exactly the algorithm should be doing.
        *
        * 
        * first, we'll prove all of the sub-parts to show that the whole 
        * will work.
        * 
        * 
        * first sub-part : setNextCharPosition().
        * 
        * Checked, this part is working correctly.
        */
        cos.next = cos.head;
        
        FileManip.TextPosition beginning_position = new FileManip.TextPosition(curr_pos.getLine(), curr_pos.getColumn());
        
        //get the chars
//         = s.toCharArray();
        String n = "";

//        fm
        //line by line
//        int x = curr_pos.getColumn();
//        char[] line = null;
        boolean b;
        bra: while(b=hasNextCharPosition()){
        //instead: (fm.getLine+'\n').toCharArray()
//        for(int y = curr_pos.getLine(); y <= fm.getEndPosition().getLine(); ){
//            line = fm.getLine(y).toCharArray();
//            for(; x < line.length; x++){
//            System.out.println(curr_pos + " " + fm.getCharAt(curr_pos));
//              if(curr_pos.getColumn()==0&&first){ FileManip.setTextPositionCol(curr_pos, -1);first = false;}
              if(to_rewind){ 
                  setPreCharPosition();
//                  FileManip.setTextPositionCol(curr_pos, -1);nnn = false;
                  to_rewind = false;
              }
              
//              if(curr_pos.getColumn()==-1){ 
//                  System.out.println("$$$$$$$$$$");
//                  FileManip.setTextPositionCol(curr_pos, -1);
//              }
              char c;
              setNextCharPosition();
              if(curr_pos.getColumn() == fm.getLine(curr_pos.getLine()).length()){
                c = '\n';
//                setNextCharPosition();
              } else{
                  
                  c = fm.getCharAt(curr_pos);
              }
//              System.out.println(c);
////                System.out.println(x +" : " +y + ": "+c);
                int action = cos.next.action(c);
                if(action < 0){
                    beginning_position = new FileManip.TextPosition(curr_pos.getLine(), curr_pos.getColumn());
                    action = -action;
                }
                switch(action){
                    case C0TokenParser.EXIT_SUCCESSFUL_REWIND:
//                        System.out.println("EXIT SUCCESS REWIND : " + n + " "+c);
//                        FileManip.setTextPositionCol(curr_pos, x-1);
                        break bra;
                    case C0TokenParser.ERROR_AND_RECORD:
//                        System.out.println("ERROR AND RECORD");
                    case C0TokenParser.CONTINUE_AND_RECORD:
//                        System.out.println("CONT AND RECORD");
                        
                        n+=c;
//                        System.out.println(c);
//                        setNextCharPosition();
                        break;
                    
                    case C0TokenParser.EXIT_SUCCESSFUL:
//                        System.out.println(c);
                        n+=c;
                        setNextCharPosition();
                        
                        
                        break bra;
                    case C0TokenParser.EXIT_UNSUCCESSFUL: break bra;
                    case C0TokenParser.UNKNOWN_CHAR:
//                        retnexChar = true;
                        FileManip.TextPosition tp = new FileManip.TextPosition(curr_pos.getLine(), curr_pos.getColumn());
//                        setNextCharPosition();
                        //(the char from an unsucessful exit)
                        setNextCharPosition();
                        n = fm.getCharAt(tp) + ""; 
                        
                        //setPreCharPosition();
//                        return new Word(n, tp);
                        return new C0Token(n, beginning_position, cos.head.type);
//                        setPreCharPosition();
//                        FileManip.setTextPositionCol(curr_pos, x-1);
//                        break bra;
//                        return null;
//                    case C0Scanner.SUCCESS_SINGLE_CHAR:
//                        break;
                    case C0TokenParser.CONTINUE: //setNextCharPosition();
                        break;
                    case C0TokenParser.CONTINUE_AND_REWIND:
//                        System.out.println("CONTINUE AND REEEEWIND");
                        setPreCharPosition();
                        break;
                }
                
            }
            if(b == false) 
            {return null;}  
            
//            if(x == line.length) {System.err.println("sadf");x = 0; y++;}
//            FileManip.setTextPosition(curr_pos,y, x);
//        }
//        return new Word(n, curr_pos);
        return new C0Token(n, beginning_position, cos.head.type);
    }
    private boolean hasNextCharPosition(){
        return !(fm.getLine(curr_pos.getLine()).length() == curr_pos.getColumn()
            && curr_pos.getLine() == fm.getEndPosition().getLine());
    }
    private void setNextCharPosition(){
        if(fm.getLine(curr_pos.getLine()).length() == curr_pos.getColumn()){
            if(curr_pos.getLine() == fm.getEndPosition().getLine()){
                return;
            }else{
                FileManip.setTextPosition(curr_pos, curr_pos.getLine()+1, 0);
            }
        } else {FileManip.setTextPosition(curr_pos, curr_pos.getLine(), curr_pos.getColumn()+1);}
    }
    private void setPreCharPosition(){
        if(0 == curr_pos.getColumn()){
            if(curr_pos.getLine() == 0){
                FileManip.setTextPosition(curr_pos, 0, -1);
                return;
            }else{
//                FileManip.setTextPosition(curr_pos, curr_pos.getLine()-1, fm.getLine(curr_pos.getLine()-1).length()-1);
                FileManip.setTextPosition(curr_pos, curr_pos.getLine(), -1);
            }
        } else {FileManip.setTextPosition(curr_pos, curr_pos.getLine(), curr_pos.getColumn()-1);}
    }
    
    /**
     * skips this FileManip's position to the given position. Also returns the 
     * next word.
     * Careful, skipTo's word position might not match your input position if you
     * skip to whitespace.
     * @param position
     * @return 
     */
    public Word skipTo(FileManip.TextPosition position){
        curr_pos = new FileManip.TextPosition(position.getLine(), position.getColumn());
        return next();
    }
    public void set(FileManip.TextPosition position){
        curr_pos = new FileManip.TextPosition(position.getLine(), position.getColumn());
    }
    public FileManip.TextPosition getCurrentPos(){
        return curr_pos;
    }
    public class Word{        
        /**
         * returns the original position of this word
         */
        public FileManip.TextPosition getOriginalPosition(){
            return fm.getOriginalPosition(position);
        }
        
        /**
         * returns the name of this edit (when the word started)
         */
        public String getEditName(){
            return fm.getTextSource(position);
        }
        /**
         * the text that is contained in a given word
         */
        public String text;
        
        /**
         * the starting index of the word. The word will NOT span across 
         * multiple lines, so it's safe to assume that the end position of the
         * word will be at
         * (row_end, col_end) = (row_begin, col_begin + text.length())
         */
        public FileManip.TextPosition position;
        
        //a string and a position. 'nuff said.
        private Word(String text, FileManip.TextPosition position){
            this.text = new String(text);
            this.position = new FileManip.TextPosition(position.getLine(), position.getColumn());
        }
        @Override public String toString(){
            return position + " : " + text;
        }
    }
    public class C0Token extends Word implements Types{
        private int RECOGNIZED_WORD_TYPE;
        public int getTokenType(){
            return RECOGNIZED_WORD_TYPE;
        }
        //may put in multiple masks by OR-ing each one.
        //returns true if the word is of type A or type B or type C...
        public boolean isOfType(int TYPE_MASK){
            return (RECOGNIZED_WORD_TYPE & TYPE_MASK) != 0;
        }
        //may put in multiple masks by AND-ing each one.
        //returns true if the word is of type A or type B or type C...
        public boolean isAllOfType(int TYPE_MASK){
            return (RECOGNIZED_WORD_TYPE & TYPE_MASK) == TYPE_MASK;
        }
        public boolean baseTypeEqual(int val){
            assert (val & 0x0000FFFF) != 0;
            return (val & 0x0000FFFF) == (RECOGNIZED_WORD_TYPE & 0x0000FFFF);
        }
        private C0Token(String text, FileManip.TextPosition position, int val){
            super(text, position);
            RECOGNIZED_WORD_TYPE = val;
        }
        private C0Token(String text, FileManip.TextPosition position){
            super(text, position);
            RECOGNIZED_WORD_TYPE = ANY | UNKNOWN_TYPE;;
        }
    }
}
class Blah{
    public static void main(String[] args){
        FileManip fm1 = new FileManip(new File("./src/Test.c0"));
        FileManipScanner fms = new FileManipScanner(fm1);
        FileManipScanner.Word w;
        String end = "";
        while((w = fms.nextToken()) != null){
            end += w.toString() + "\n";
        }
        System.out.println(end);
         System.exit(0);
       C0TokenParser c0s = new C0TokenParser();
        System.out.println(c0s.next("is_unique("));
        System.out.println(c0s.next(">>sjkfd"));
        System.out.println(c0s.next(">>=sdf"));
        System.out.println(c0s.next(" !=><>=sdf"));
    }
}