package test;



import com.kieda.search.Search;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import com.kieda.typesafe.AssertionMethod;

class FMTest{
    public static FileManip2.TextPosition pos(int l, int c){
        return new FileManip2.TextPosition(l, c);
    }
    public static void main(String[] args){
        FileManip2 hello = FileManip2.generate("hello", "hello, world!\nYou are looking great\ntoday!");
        FileManip2 goodbye = FileManip2.generate("goodbye", "goodbye, universe!\nI'm not smelling disgusting\ntomorrow!");
        FileManip2 edit = hello.copy().insert(goodbye, pos(2, 4));//.subRange(new FileManip2.Range(1, 2, 4, 4));
        edit.setVerbose(true);
        System.out.println(edit);
        int l = 1; int c = 4;
        System.out.println(edit.getOriginalPosition(pos(l,c)));
        System.out.println(hello.getCharAt(edit.getOriginalPosition(pos(l,c))) + "\n\n");
                //copy().append(goodbye);//edit is acopy of hello (as to preserve the original file)
//        System.exit(0);
        /**
         * assert that the edited one has the same equality of the original 
         * parts, when converted back to its original pos.
         */
        for(int line = 0; line <= edit.getEndPosition().getLine(); line++){
            for(int col = 0; col < edit.getLine(line).length(); col++){
                FileManip2.TextPosition pos = new FileManip2.TextPosition(line, col);
                char ch = edit.getCharAt(pos);
                FileManip2.TextPosition old_pos = edit.getOriginalPosition(pos);
                FileManip2 check;
                switch(edit.getTextSource(pos)){
                    case "hello":
                        check = hello;
                        break;
                    case "goodbye":
                        check = goodbye;
                        break;
                    default: return;
                }
                System.out.println(ch + "  " + check.getCharAt(old_pos) + "  [" + edit.getTextSource(pos) + "]" + "  " + pos + "   " + old_pos);
                assert ch == check.getCharAt(old_pos);
                
            }
        }
//        hello.getOriginalPosition(null)
//        hello.printPos(2, 6);
//        hello.printPos(2, 7);
//        hello.printPos(2, 8);
//        hello.printPos(2, 22);
//        hello.printPos(2, 23);
//        goodbye.printPos(0, 16);
//        goodbye.printPos(0, 17);
//        System.out.println(goodbye);
    }
}
/**
 * A object + library for some nice/advanced file manipulations
 * @author kieda
 */
public class FileManip2 {
    /**************************************************************************\
    |*                                variables                               *|
    \**************************************************************************/
    //might change the access to protected, in the case we want some sort of
    //C0FileManip class.
    private ArrayList<String> lines_of_code;
    //the list of the ranges of where the code has been edited
    private ArrayList<EditedRange> code_edits;
    
    private final TextPosition begin_position = new TextPosition(0, 0);
    private TextPosition end_position;
    private boolean verbose = false;
    /**************************************************************************\
    |*                               assertions                               *|
    \**************************************************************************/
    @AssertionMethod
    private boolean checkRange(Range range){
        //we want the range to be correct at both the begining and the end.
        //in addition, we want to ensure that the begin position precedes the
        //end position.
        if(range.begin_position.compareTo(range.end_position) == 1) return false;
            //the begin position can't be after the end position
        
        return checkPos(range.begin_position) && checkPos(range.end_position);
        //the begin positon and end position must be real coordinates.
    }
    @AssertionMethod
    private boolean checkPos(TextPosition position){
        //we check three things
            //1. if either the line number or the column number is less than 
            //   zero
            //2. if the line number is in range
            //3. if the column number is in range for the given range
        
        if(position.char_number<0 || position.line_number <0) return false;
        
        //we know that the line_number and char_number are greater than zero by
        //here.
        
        if(position.line_number >= lines_of_code.size()) return false;
        
        //we know that, by here, the line_number is less than the lines of code,
        //and thus are in the range.
        
        if(position.char_number > lines_of_code.get(position.line_number).length()) return false;
        
        //we can insert at the end of the line, and delete till the end of the
        //line, so it is possible for char_number to equal the length of the 
        //string
        
        return true;
    }
    /**
     * checks the edited ranges and ensures that they have the following 
     * properties:
     *     1. they are non-null, and their components are non-null
     *     2. their ranges are valid
     *     3. no ranges overlap
     *     4. all of the ranges sum up to the lines_of_code.
     *     5. the ranges must be in order
     */
    @AssertionMethod
    private boolean checkEditRanges(){
        
        if(code_edits.isEmpty()) return false;
        //there has to be at least one range by the file specification. Should 
        //not happen.
        
        if(!code_edits.get(0).range.begin_position.equals(begin_position)) return false;
        //the beginning must start at the beginning of the file.
        
        
        for(int i = 0; i < code_edits.size(); i++){
            EditedRange edit1 = code_edits.get(i);//the current edit
            if(edit1 == null || edit1.name == null || edit1.range == null) return false;
            checkRange(edit1.range);//the range must be in range.
            if(i+1 < code_edits.size()){
                
                //we can check the next range for consistancy. The next range's
                //beginning should be where this range ends. We also know that
                //all ranges are ascending by the ascertion we made while 
                //creating them. If all of the ranges are in range, and all of 
                //the ranges end where the next one ends, and the first range 
                //starts at the beginning of the file, and the last range ends 
                //at the end of the file, all ranges should be covered with no 
                //overlap.
                if(!edit1.range.end_position.equals(code_edits.get(i+1).range.begin_position)){
                    System.err.println(
                            "end position at " 
                                + edit1.range.end_position 
                            + " and corresponding begin position at "
                                + code_edits.get(i+1).range.begin_position);
                    return false;
                }
                    //(not) the current ranges end is equal to the next ranges
                    //beginning.
            } else{
                //we're at the end of the ranges. So, the final range's end 
                //position should be at the end of the file.
                if(!code_edits.get(i).range.end_position.equals(end_position)){ return false;}
                //i is the last index, as (not) i+1<code_edits.size(), so
                //i+1>=code_edits.size();
                //but because we were counting up
                
                //i + 1 == code_edits.size()
                
                //subtracting one, we see that i is the last index
                
                //i == code_edits.size() - 1
            }
        }
        return true;
    }
    /**************************************************************************\
    |*                              constructors                              *|
    \**************************************************************************/
    public FileManip2(File f){
        //we are allowed to throw an assertion error, as we already checked that
        //the user input correct paths. Incorrect paths in here will be the
        //fault of the person managing FileManips
        assert (f.canRead()) : ("File " + f.getPath()+ "cannot be read.");
        
        //initialize our lines of code
        lines_of_code = new ArrayList<String>();
        
        //initialize code edits
        code_edits = new ArrayList<EditedRange>();
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String c;
            while((c=br.readLine())!=null){
                lines_of_code.add(c);//add line-by-line.
            }
        } catch (Exception ex) {}
        //the last position
        end_position = new TextPosition(
                //is the last line
                lines_of_code.size()-1,
                //and the length of the last line.
                lines_of_code.get(lines_of_code.size()-1).length());
        
        //the range of this file
        EditedRange er = new EditedRange(new Range(begin_position, end_position), f.getPath(), begin_position);
        code_edits.add(er);
    }
    private FileManip2(String s){
        this(s, "edited");
    }
    //only for use within this class!
    private FileManip2(String s, String source){
        //initialize our lines of code
        lines_of_code = new ArrayList<String>();
        
        //initialize code edits
        code_edits = new ArrayList<EditedRange>();
        
        try {
            BufferedReader br = new BufferedReader(new StringReader(s));
            String c;
            while((c=br.readLine())!=null){
                lines_of_code.add(c);//add line-by-line.
            }
        } catch (Exception ex) {}
        
        //the last position
        end_position = new TextPosition(
                //is the last line
                lines_of_code.size()-1,
                //and the length of the last line.
                lines_of_code.get(lines_of_code.size()-1).length());
        
        //the range of this file
        EditedRange er = new EditedRange(new Range(begin_position, end_position), source, begin_position);//the source starts at (0,0)
//        er.name = source;
//        er.range = 
        code_edits.add(er);
    }
    
    /**************************************************************************\
    |*                              manipulations                             *|
    \**************************************************************************/
    
    /**
     * inserts all of the text of another FileManip2 class at a given insertion
     * position into this FileManip2. Inserts on the exact line that is 
     * specified, and does not add on a new line unless if a new line is 
     * specified in the other file. This method returns this FileManip2, so the 
     * client can do multiple operations on a FileManip2.
     * 
     * marks the range for which the other FileManip2 is inserted, and adjusts 
     * all other ranges if necessary.
     */
    public FileManip2 insert(FileManip2 another_file, TextPosition insertion_position){
        assert (checkPos(insertion_position)) : ("bad insertion position: " + insertion_position);
        
        //the things we edit:
        //    the lines of code (add another_file's contents to the end)
        //    the code edits (we append the code edits of the another_file and
        //        shift it over by the length of this file)
        //    the last index of the file
        
        //this one just copied the pointers instead of adding on the strings
//        lines_of_code.addAll(another_file.lines_of_code);
        for(String line : another_file.lines_of_code){
            lines_of_code.add(new String(line));
        }
        final int new_code_begin = code_edits.size();
            //we know that the new code will begin at this index in the 
            //code_edits
        
        for(int i = 0; i < another_file.code_edits.size(); i++) {//EditedRange range : another_file.code_edits){
            EditedRange range = another_file.code_edits.get(i);
            code_edits.add(
                    new EditedRange(
                        new Range(range.range.begin_position, range.range.end_position),
                        new String(range.name),
                        new TextPosition(range.original_beginning.line_number, range.original_beginning.char_number)
                    )
                );
        }
            //adds all of the code edits to the end of the file.
        
        //the place where the new code will begin is on a new line after the 
        //current end of the file.
        
        final int line_addition = end_position.line_number + 1;//the last line 
        
        //we know a FileManip2 begins at (0,0), so we can just add the 
        //line_addition to all of the ranges at and after new_code_begin
        
        //we run through all of the new edited lines to be modified
        for(int i = 0; i < another_file.code_edits.size(); i++){
            code_edits.get(new_code_begin + i).range.begin_position.line_number += line_addition;
            code_edits.get(new_code_begin + i).range.end_position.line_number += line_addition;
        }
        
        //we have to set the end position of the old file equal to the begin 
        //position of the new file
        code_edits.get(new_code_begin-1).range.end_position.char_number =
                code_edits.get(new_code_begin).range.begin_position.char_number;
        code_edits.get(new_code_begin-1).range.end_position.line_number =
                code_edits.get(new_code_begin).range.begin_position.line_number;
        
        end_position.line_number = lines_of_code.size()-1;
        end_position.char_number = lines_of_code.get(end_position.line_number).length();
        
        
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: attach's range changes");
        return this;
    }
    
    /**
     * inserts all of the text of a string at a given insertion
     * position into this FileManip2. Inserts on the exact line that is 
     * specified, and does not add on a new line unless if a new line is 
     * specified in the other file. This method returns this FileManip2, so the 
     * client can do multiple operations on a FileManip2.
     * 
     * marks the range for which the String is inserted as "edited", and adjusts 
     * all other ranges if necessary.
     */
    public FileManip2 insert(String text, TextPosition insertion_position){
        assert (checkPos(insertion_position)) : ("bad insertion position: " + insertion_position);
        FileManip2 inserted_manip = new FileManip2(text);
        insert(inserted_manip, insertion_position);
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: attach's range changes");
        return this;
    }
    
    /**
     * inserts all of the text of a string at the end of this file from 
     * another_file. Inserts on exactly the last line, and does not add on a new
     * line unless if a new line is specified in the other file; unlike attach.
     * This method returns this FileManip2, so the client can do multiple 
     * operations on a FileManip2.
     * 
     * marks the range for which the String is inserted as "edited", and adjusts 
     * all other ranges if necessary.
     */
    public FileManip2 append(FileManip2 another_file){
        assert another_file != null : "another_file cannot be null!";
        assert another_file.lines_of_code != null : "lines of code cannot be null!";
        assert another_file.code_edits != null : "code edits cannot be null!";
        assert !another_file.lines_of_code.isEmpty(): "lines_of_code cannot be empty!";
        assert !another_file.code_edits.isEmpty(): "code_edits cannot be empty!";
        
        final int last_line = lines_of_code.size()-1;
        final int chare = lines_of_code.get(last_line).length();
        {
            //add the very first line of another_file to the last line of this file
            String first_line = new String(another_file.lines_of_code.get(0));
                //we can grab this element as we asserted that lines_of_code is 
                //non-null and not empty.
            
            lines_of_code.set(last_line, lines_of_code.get(last_line) + first_line);
                //set the last line equal to the last line plus the first line
        }//destroy last_index; gc first_line
        
        for(int i = 1; i < another_file.lines_of_code.size(); i++){
            //go through all of the next indices and add them to the file.
            lines_of_code.add(new String(
                     another_file.lines_of_code.get(i)
                    ));
        }
        
        for(int i = 0; i < another_file.code_edits.size(); i++) {//EditedRange range : another_file.code_edits){
            EditedRange edit = another_file.code_edits.get(i);
            int char_begin;
            int char_end;
            
            if(edit.range.begin_position.line_number == 0){
                char_begin = edit.range.begin_position.char_number + chare;
            } else char_begin = edit.range.begin_position.char_number;
            if(edit.range.end_position.line_number == 0){
                char_end = edit.range.end_position.char_number + chare;
            } else char_end = edit.range.end_position.char_number;
            
            code_edits.add(
                    new EditedRange(
                        new Range(edit.range.begin_position.line_number+last_line,
                                  char_begin,
                                  edit.range.end_position.line_number+last_line,
                                  char_end),
                        new String(edit.name),
                        new TextPosition(edit.original_beginning.line_number, edit.original_beginning.char_number)
                    ));
        }
        end_position.line_number = lines_of_code.size()-1;
        end_position.char_number = lines_of_code.get(end_position.line_number).length();
        
//        System.out.println(code_edits);    
            //for testing.
        
        //ensures
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: append's range changes");
        return this;
    }
    
    /**
     * attaches all of the text from another FileManip2 class to the end of this
     * FileManip2. Note: attaches on a new line, unlike insert. This method 
     * returns this FileManip2 after the attachment, so the client can do 
     * multiple operations on a FileManip2.
     * 
     * marks the range for which the other FileManip2 is attached.
     */
    public FileManip2 attach(FileManip2 another_file) {
        //the things we edit:
        //    the lines of code (add another_file's contents to the end)
        //    the code edits (we append the code edits of the another_file and
        //        shift it over by the length of this file)
        //    the last index of the file
        
        //this one just copied the pointers instead of adding on the strings
//        lines_of_code.addAll(another_file.lines_of_code);
        for(String line : another_file.lines_of_code){
            lines_of_code.add(new String(line));
        }
        
        final int new_code_begin = code_edits.size();
            //we know that the new code will begin at this index in the 
            //code_edits
        
        //this one just copied the pointers instead of adding on the strings
//        code_edits.addAll(another_file.code_edits);
        for(int i = 0; i < another_file.code_edits.size(); i++) {//EditedRange range : another_file.code_edits){
            EditedRange range = another_file.code_edits.get(i);
            code_edits.add(
                    new EditedRange(
                        new Range(range.range.begin_position, range.range.end_position),
                        new String(range.name),
                        new TextPosition(range.original_beginning.line_number, range.original_beginning.char_number)
                    )
                );
        }
            //adds all of the code edits to the end of the file.
        
        //the place where the new code will begin is on a new line after the 
        //current end of the file.
        
        final int line_addition = end_position.line_number + 1;//the last line 
        
        //we know a FileManip2 begins at (0,0), so we can just add the 
        //line_addition to all of the ranges at and after new_code_begin
        
        //we run through all of the new edited lines to be modified
        for(int i = 0; i < another_file.code_edits.size(); i++){
            code_edits.get(new_code_begin + i).range.begin_position.line_number += line_addition;
            code_edits.get(new_code_begin + i).range.end_position.line_number += line_addition;
        }
        
        //we have to set the end position of the old file equal to the begin 
        //position of the new file
        code_edits.get(new_code_begin-1).range.end_position.char_number =
                code_edits.get(new_code_begin).range.begin_position.char_number;
        code_edits.get(new_code_begin-1).range.end_position.line_number =
                code_edits.get(new_code_begin).range.begin_position.line_number;
        
        end_position.line_number = lines_of_code.size()-1;
        end_position.char_number = lines_of_code.get(end_position.line_number).length();
        
        
        //ensures
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: attach's range changes");
        return this;
    }
    
    /**
     * attaches all of the text from a string to the end of this
     * FileManip2. Note: attaches on a new line, unlike insert. This method 
     * returns this FileManip2 after the attachment, so the client can do 
     * multiple operations on a FileManip2.
     * 
     * marks the range for which the String is attached as "edited"
     */
    public FileManip2 attach(String text) {
        //create a new manipulation
        FileManip2 new_manip = new FileManip2(text); //hope this works
        attach(new_manip); //attach the new manip
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: attach's range changes");
        return this;
    }
    
    /**
     * deletes the range from this FileManip2. Note: deleting from the same 
     * position to the same position deletes nothing. Returns this FileManip2 
     * after the attachment, so the client can do multiple operations on a 
     * FileManip2.
     * 
     * adjusts the ranges if necessary.
     */
    public FileManip2 delete(Range deletion_range){
        assert (checkRange(deletion_range)) : ("bad range: " + deletion_range);
        //we can "cheat" by getting a subrange up to the beginning of the 
        //deletion range, and a subrange immediately after the deleletion range.
        //We then can then append the latter one to the first one to get a final 
        //result without the range.
        
        FileManip2 fm 
            = subRange(new Range(begin_position, deletion_range.begin_position))
              .append(subRange(new Range(deletion_range.end_position, end_position)));
              //holy shit this is beautiful.
        
        //we don't need to manually copy (I think) because using append and 
        //subRange already copied it into new objects
        
        code_edits = fm.code_edits;
        end_position = fm.end_position;
        lines_of_code = fm.lines_of_code;
        
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: delete's range changes");
        return this;
    }
    
    /**
     * deletes a line from this FileManip2. Not only deletes the text on the 
     * line, but also the line itself.  Returns this FileManip2 after the 
     * attachment, so the client can do multiple operations on a FileManip2.
     * 
     * adjusts the ranges if necessary.
     */
    public FileManip2 delete(int line){
        assert (line >= 0 && line < lines_of_code.size()) : ("line out of bounds: " + line);
        
        FileManip2 fm;
        if(lines_of_code.size()-1 == 0)
            fm = subRange(new Range(0,0,0,0));
        else if(line == lines_of_code.size()-1)
            fm = subRange(new Range(
                        begin_position.line_number, 
                        begin_position.char_number, 
                        line-1,
                        lines_of_code.get(line-1).length()
                    ));
        else
              fm = subRange(new Range(
                        begin_position.line_number, 
                        begin_position.char_number, 
                        line,
                        0
                    )).append(subRange(
                new Range(
                    line+1, 
                    0,
                    end_position.line_number,
                    end_position.char_number
                )));
              
        
        //we don't need to manually copy (I think) because using append and 
        //subRange already copied it into new objects
        
        code_edits = fm.code_edits;
        end_position = fm.end_position;
        lines_of_code = fm.lines_of_code;
        
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: deletes's range changes");
        return this;
    }
    
    /**
     * splits a line into two lines at the given point.
     * 
     * adjusts the ranges if necessary.
     */
    public FileManip2 splitline(TextPosition split_position){
        assert (checkPos(split_position)) : ("bad position: " + split_position);
        //we can split by getting a substring from the beginning of the file 
        //till the split_position, then getting a substring from split_position
        //till the end of the file, then attaching the former to the latter.
        
        FileManip2 fm = subRange(new Range(begin_position, split_position))
                       .attach(subRange(new Range(split_position, end_position)));
              //holy shit wow so beautiful.
        
        //we don't need to manually copy (I think) because using append and 
        //subRange already copied it into new objects
        
        code_edits = fm.code_edits;
        end_position = fm.end_position;
        lines_of_code = fm.lines_of_code;
        
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: splitline's range changes");
        return this;
    }
    
    /**
     * joins a line with its next line.
     * 
     * cannot join the last line.
     * 
     * adjusts the ranges if necessary.
     * 
     * Example: join at line (0) 
     * 0. "hello"
     * 1. " world"
     * 2. "floop"
     * goes to: 
     * 0. "hello world"
     * 1. "floop"
     */
    public FileManip2 joinLineNext(int line){
        assert (line >= 0 && line < lines_of_code.size()) : ("line cannot be joined: " + line);
        
        
        FileManip2 fm 
            = subRange(new Range(
                    begin_position.line_number,
                    begin_position.char_number,
                    line,
                    lines_of_code.get(line).length()
                    ))
              .append(subRange(
                    new Range(
                        line+1,
                        0,
                        end_position.line_number,
                        end_position.char_number
                )));
              //holy shit this is beautiful.
        
        //we don't need to manually copy (I think) because using append and 
        //subRange already copied it into new objects
        
        code_edits = fm.code_edits;
        end_position = fm.end_position;
        lines_of_code = fm.lines_of_code;
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: splitline's range changes");
        return this;
    }
    
    /**
     * joins a line with its previous line.
     * 
     * cannot join the first line.
     * 
     * adjusts the ranges if necessary.
     * 
     * Example: join at line (1) 
     * 0. "hello"
     * 1. " world"
     * 
     * goes to: 
     * 0. "hello world"
     */
    public FileManip2 joinLinePrev(int line){
        assert (line > 0 && line <= lines_of_code.size()) : ("line cannot be joined: " + line);
        
        FileManip2 fm 
            = subRange(new Range(
                    begin_position.line_number,
                    begin_position.char_number,
                    line-1,
                    lines_of_code.get(line-1).length()
                    ))
              .append(subRange(
                    new Range(
                        line,
                        0,
                        end_position.line_number,
                        end_position.char_number
                )));
              //holy shit this is beautiful.
        
        //we don't need to manually copy (I think) because using append and 
        //subRange already copied it into new objects
        
        code_edits = fm.code_edits;
        end_position = fm.end_position;
        lines_of_code = fm.lines_of_code;
        
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: splitline's range changes");
        return this;
    }
    
    /**
     * replaces the range of this FileManip2 with the text in the other FileManip2
     * 
     * adjusts the ranges if necessary.
     */
    public FileManip2 replace(FileManip2 new_text, Range replaced_range){
        assert (checkRange(replaced_range)) : ("bad range: " + replaced_range);
        //can be done by just by deleting the replaced_ranged, and inserting the 
        //new_text at the beginning of the replaced_range.
        
        delete(replaced_range);
        insert(new_text, replaced_range.begin_position);
            //that simple!
        
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: replace's range changes");
        return this;
    }
    
    /**
     * replaces the range of this FileManip2 with the text in the string
     * 
     * adjusts the ranges if necessary. Range for the new text is under "edited"
     */
    public FileManip2 replace(String new_text, Range replaced_range){
        assert (checkRange(replaced_range)) : ("bad range: " + replaced_range);
        
        FileManip2 new_manip = new FileManip2(new_text); //create a new manip
        
        replace(new_manip, replaced_range);//replace
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: replace's range changes");
        return this;
    }
    
    /**
     * replaces the line at the line number with the given string of text.
     * 
     * Doesn't have to be a single line.
     * 
     * adjusts the ranges if necessary. Range for the new line is under "edited"
     */
    public FileManip2 replace(String line, int line_number){
        assert (line_number >= 0 && line_number <= lines_of_code.size()) : ("line out of bounds: " + line_number);
        
        //goes from the beginning of the line (zero) to the end of the line 
        //(the string's length)
        Range line_replaced_range = new Range(
                new TextPosition(line_number, 0),
                new TextPosition(line_number, lines_of_code.get(line_number).length()));
        replace(line, line_replaced_range); //e-z!
        
        assert (checkEditRanges()) : ("Ranges are invalid. Problem: replace's range changes");
        return this;
    }
    
    /**************************************************************************\
    |*                                methods                                 *|
    \**************************************************************************/
    
    /**
     * returns the text on the given range.
     */
    public String getText(Range text_range){
        assert (checkRange(text_range)) : ("bad range: " + text_range);
        String ret = "";
        if(text_range.onSameLine()){
            //we just copy one line from the beginning position to the end 
            //position
            //we can assume that the positions are in bounds, and are correct.
            //we only need to check one value for the line number, as we know 
            //that they are on the same line.
            
            //the line
            ret = lines_of_code.get(text_range.begin_position.line_number)
                    //substring from
                    .substring(
                        //the beginning column
                        text_range.begin_position.char_number,
                        //to the end column
                        text_range.end_position.char_number
                    );
        } else{
            //we know that they are on separate lines, and because we know that
            //the range is correct by assertions, we can assume that we can copy
            //to the end of the line for the first line, and from the beginning
            //of the line to the stop position for the last line. We copy whole
            //lines for everything in between.
            
            //copy the first line from the column to the end of the line
            ret += lines_of_code.get(text_range.begin_position.line_number)
                    .substring(text_range.begin_position.char_number);
            ret += "\n";//new line.
            
            for(int i = text_range.begin_position.line_number+1; i < text_range.end_position.line_number; i++){
                ret += lines_of_code.get(i) + "\n";
            }//copy lines till we're one less than the line number of the end position
            
            //copy the last line from the beginning of the line to the column
            ret += lines_of_code.get(text_range.end_position.line_number)
                    .substring(0, text_range.end_position.char_number);
        }
        //text edit range should not change.
        //if it did we're totally f***ed.        
        return ret;
    }
    /**returns the string at the given line*/
    public String getLine(int line_number){
        return lines_of_code.get(line_number);
    }
    
    /**
     * returns the source of the text at the given position.
     * 
     * Useful if there's an error in the code, then we can say that the error
     * came from a specific file.
     */
    public String getTextSource(TextPosition position){
        assert(checkPos(position)):("bad position: " + position);
        //text edit range should not change.
        //if it did we're totally f***ed.
        int index = Search.BinarySearch(code_edits, position);
        assert(index != -1) : ("position not found in the range! Something is wrong with the edits or the text position!");
        return code_edits.get(index).name;
    }
    
    /**
     * returns the edited range of the text at the given position.
     * 
     * Useful if there's an error in the code, then we can say that the error
     * came from a specific file.
     */
    private int getEditPos(TextPosition position){
        assert(checkPos(position)):("bad position: " + position);
        //text edit range should not change.
        //if it did we're totally f***ed.
        int index = Search.BinarySearch(code_edits, position);
        assert(index != -1) : ("position not found in the range! Something is wrong with the edits or the text position!");
        return index;
    }
    
    /**
     * returns the beginning position of the file
     */
    public TextPosition getBeginPosition(){
        return begin_position;
    }
    /**
     * returns the last position of the file.
     */
    public TextPosition getEndPosition(){
        return end_position;
    }
    
    /**
     * returns the last position of the file.
     */
    public Range getRange(){
        return new Range(begin_position, end_position);
    }
    
    /**
     * returns a new FileManip2 that is a sub-section of this FileManip2.
     * a subrange from the beginning to the end of this FileManip2 will return
     * a copy of this FileManip2.
     * 
     * Subrange is very useful, and is used for higher-level operations like
     * deletion, replication, replacing, and more, while combined with other
     * operations.
     */
    public FileManip2 subRange(Range sub_range){
        assert (checkRange(sub_range)) : ("bad range: " + sub_range);
        assert checkEditRanges() : "bad input edit ranges: " + code_edits;
            //this method assumes that the input ranges are correct.
        FileManip2 fm = new FileManip2(" ");
        
        fm.code_edits.clear();
        fm.lines_of_code.clear();
            //to ensure we're starting off with a new slate.
        
        /***********************************************************************
          copy the lines of code first into lines_of_code.
         **********************************************************************/
        
        //basically stolen from getText
        if(sub_range.onSameLine()){
            //we just copy one line from the beginning position to the end 
            //position
            //we can assume that the positions are in bounds, and are correct.
            //we only need to check one value for the line number, as we know 
            //that they are on the same line.
            
            //add the single line
            fm.lines_of_code.add(lines_of_code.get(sub_range.begin_position.line_number)
                    //substring from
                    .substring(//returns a new string so we're A-OK
                        //the beginning column
                        sub_range.begin_position.char_number,
                        //to the end column
                        sub_range.end_position.char_number
                    ));
        } else{
            //we know that they are on separate lines, and because we know that
            //the range is correct by assertions, we can assume that we can copy
            //to the end of the line for the first line, and from the beginning
            //of the line to the stop position for the last line. We copy whole
            //lines for everything in between.
            
            //copy the first line from the column to the end of the line
            
            //add the first line
            fm.lines_of_code.add(lines_of_code.get(sub_range.begin_position.line_number)
                    .substring(sub_range.begin_position.char_number));
            
            for(int i = sub_range.begin_position.line_number+1; i < sub_range.end_position.line_number; i++){
                fm.lines_of_code.add(new String(lines_of_code.get(i)));
            }//copy lines till we're one less than the line number of the end position
            
            //copy the last line from the beginning of the line to the column
            fm.lines_of_code.add(lines_of_code.get(sub_range.end_position.line_number)
                    .substring(0, sub_range.end_position.char_number));
        }
        //assume this code works.
        
        /***********************************************************************
          next, copy and adjust the edits into the code_edits.
         **********************************************************************/
                
        int start_edit = getEditPos(sub_range.begin_position);
            //the edit that this sub-range starts in
        
        //begin at (0,0), and go to when the range ends
        
        //keep going till we run out of edits, or according to additional breaks
        //in the for loop
        legs:for(int i = start_edit;i < code_edits.size(); i++){ 
            //if we copy ALL of the code_edits without our legs being broken,
            //then we will still copy till the end, as we know that the range 
            //will not be out of the range of this FileManip2.
            
            EditedRange edit = code_edits.get(i);
//            System.out.println(edit); //a routine check
            String s = new String(edit.name);
            int begin_line;
            int begin_char;
            int end_line;
            int end_char;
            
            int orig_line;
            int orig_char;
            
            if(i == start_edit){
                begin_char = 0;
                begin_line = 0;
                orig_line = edit.original_beginning.line_number + sub_range.begin_position.line_number;
                orig_char = (edit.range.begin_position.line_number == sub_range.begin_position.line_number)?edit.original_beginning.char_number + sub_range.begin_position.char_number: sub_range.begin_position.char_number;
            }
            else{
                begin_char = fm.code_edits.get(i-1-start_edit).range.end_position.char_number;
                begin_line = fm.code_edits.get(i-1-start_edit).range.end_position.line_number;
                orig_line =  edit.original_beginning.line_number;
                orig_char =  edit.original_beginning.char_number;
            }
            //split up this way because the positions on the same line are 
            //shifted over columns
            
            if(sub_range.begin_position.line_number == edit.range.end_position.line_number){
                //in the case that they are on the same line, the ranges of the
                //columns are effected from their original
                
//                begin_line = 0; 
                end_line   = 0; 
                    //they begin and end on the same line, and the sub-range's 
                    //first line must be at 0.
                
                //there is a problem if the edit range is before the 
                //beginning of the sub range. this is fixed by checking if the 
                //result is negative, and if so, setting the beginning char to 0
                //(such that we start off at zero)
//                begin_char = edit.range.begin_position.char_number - sub_range.begin_position.char_number;
//                if(begin_char < 0) begin_char = 0;
                    //subtract the edit's beginning position from the 
                    //sub_range's beginning position.
                
                if(edit.range.end_position.compareTo(sub_range.end_position)>0){
                    //the end position is greater than the range, so we cut the
                    //end position short of the edit range. We also know that
                    //this end position is the last one, so we can exit and set the
                    //final position.
                    
                    end_char = sub_range.end_position.char_number - sub_range.begin_position.char_number;
                    
                    fm.code_edits.add(new EditedRange(
                            new Range(begin_line, begin_char, end_line, end_char), 
                            s,
                            new TextPosition(orig_line, orig_char)
                            ));
                        //goes from the range's beginning to the end of the 
                        //sub-range

                    break legs;//we don't want the algo to run on any further!
                }
                
                //we know that by the break statement this statement will not 
                //execute, and that we are still in our sub_range. Because
                //of the if statement, we are on the same line, so the end char
                //is bumped back
                end_char = edit.range.end_position.char_number - sub_range.begin_position.char_number;
            } else{
                //in the case that they are not on the same line, only the
                //rows are effected.
                
                //we also know that the beginning position of our edit cannot be
                //out of the sub_range, as we know that all of the ranges are 
                //ordered, and are rising. We would have caught and broke the 
                //loop by a end position, which by definition are greater than
                //or equal to the beginning position.
                
                //by this, we know that the beginning char must be equivalent to
                //the original beginning char position.
                
                //we look at the end_char later. The end char depends if the 
                //current range is out of the range.
                
                
                //we know that the line number will be the difference of the 
                //line number of the edit and the beginning line number.                
                
                
                if(edit.range.end_position.compareTo(sub_range.end_position)>0){
                    //the end position is greater than the range, so we cut the
                    //end position short of the edit range. We also know that
                    //this end position is the last one, so we can exit and set the
                    //final position.
                    
                    //we cut off the end_char at where the sub_range is supposed 
                    //to stop. No shifting is required, as it starts at the 
                    //beginning of the line.
                    if(sub_range.onSameLine())
                        end_char = sub_range.end_position.char_number - sub_range.begin_position.char_number;
                    else 
                        end_char = sub_range.end_position.char_number;
                    
                    end_line = sub_range.end_position.line_number - 
                        sub_range.begin_position.line_number;
                    
                    fm.code_edits.add(new EditedRange(
                            new Range(begin_line, begin_char, end_line, end_char), 
                            s, new TextPosition(orig_line, orig_char)
                            ));
                        //goes from the range's beginning to the end of the 
                        //sub-range
                    break legs;//we don't want the algo to run on any further!
                }
                
                //in this case, due to the break statement, we know that we have
                //not exited yet, and we're in the middle of the text of the 
                //sub_range. So, the end char should be equialent to the edit's
                //end char.
                end_char = edit.range.end_position.char_number;
                
                //we know that the last line must be the difference
                //of the edit's last line number to the beginning.
                end_line = edit.range.end_position.line_number - 
                        sub_range.begin_position.line_number;
            }
//            System.out.println("{["+begin_line + ", "+ begin_char + "], ["+ end_line+", "+ end_char+"]}");
            fm.code_edits.add(new EditedRange(
                        new Range(begin_line, begin_char, end_line, end_char), 
                        s,  new TextPosition(orig_line, orig_char)
                        ));
        }
        TextPosition last_position = fm.code_edits.get(fm.code_edits.size()-1).range.end_position;
        fm.end_position.char_number = last_position.char_number;
        fm.end_position.line_number = last_position.line_number;
        
//        System.out.println(fm.code_edits);
//        System.out.println(fm.end_position);
            //a routine check.
        
        
        //check the edit ranges on fm
        assert fm.checkEditRanges() : "bad edit ranges in subRange, subrange: " + fm;
        return fm;
    }
    
    //get a text position from this file translated into the text position of 
    //the original file.
    public TextPosition getOriginalPosition(TextPosition this_position){
        EditedRange e  = code_edits.get(getEditPos(this_position));
        return new TextPosition((this_position.line_number-e.range.begin_position.line_number) + e.original_beginning.line_number,//ok.
                                (this_position.line_number == e.range.begin_position.line_number)?(this_position.char_number-e.range.begin_position.char_number) +e.original_beginning.char_number:
                this_position.char_number
                );
    }
    public char getCharAt(TextPosition tp){
        assert tp.line_number < lines_of_code.size() && tp.line_number >= 0 : "bad range: " + tp +" (line index out of bounds)";
        assert tp.char_number < lines_of_code.get(tp.line_number).length() && tp.char_number >= 0 : "bad range: " + tp + " (char index out of bounds)";
        return lines_of_code.get(tp.line_number).charAt(tp.char_number);
    }
    public FileManip2 copy(){
        return subRange(getRange());
    }
    /**
     * destroys this FileManip2 (prepares for GC)
     */
    public void destroy(){
        end_position = null;
        code_edits.clear();
        code_edits = null;
        lines_of_code.clear();
        lines_of_code = null;
    }
    /**
     * sets the printing of this class to be verbose or non-verbose.
     * Verbose just states the beginning and ending of each block of text.
     * 
     * false by default
     */
    public void setVerbose(boolean verbose){
        this.verbose = verbose;
    }
    public boolean getVerbose(){
        return verbose;
    }
    
    //bullshit methods
    private static String begin(EditedRange er){
        return "<BEGIN " + er.name + ">";
    }
    private static String end(EditedRange er){
        return "<END " + er.name + ">";
    }
    public static FileManip2 generate(String file_source, String text){
        return new FileManip2(text, file_source);
    }
    /**************************************************************************\
    |*                            internal classes                            *|
    \**************************************************************************/
    
    /**
     * represents a range of lines in the code. used for selection or deletion.
     */
    public static class Range{
        public Range(TextPosition begin, TextPosition end){
            assert begin.compareTo(end) <= 0 : "the beginning must precede or be equal to the end";
            
            end_position = new TextPosition(end.line_number, end.char_number);
            begin_position = new TextPosition(begin.line_number, begin.char_number);
        }
        public Range(int begin_line, int begin_col, int end_line, int end_col){
            end_position = new TextPosition(end_line, end_col);
            begin_position = new TextPosition(begin_line, begin_col);
            assert begin_position.compareTo(end_position) <= 0 : "the beginning must precede or be equal to the end";
        }
        public Range(Range r){
            assert(r.begin_position.compareTo(r.end_position) <= 0): ("the beginning must precede or be equal to the end");
            end_position = new TextPosition(r.end_position.line_number, r.end_position.char_number);
            begin_position = new TextPosition(r.begin_position.line_number, r.begin_position.char_number);
        }
        public TextPosition getBeginning(){
            return begin_position;
        }
        public TextPosition getEnd(){
            return end_position;
        }
        private TextPosition begin_position;
        private TextPosition end_position;
        @Override public String toString(){
            return "{" + begin_position + ", " + end_position + "}";
        }
        @Override public boolean equals(Object other){
            if(!(other instanceof Range)) return false;
            return ((Range)other).begin_position.equals(begin_position) && ((Range)other).end_position.equals(end_position);
        }
        /**
         * returns true iff the beginning position and the ending position are 
         * on the same line.
         */
        public boolean onSameLine(){
            return begin_position.line_number==end_position.line_number;
        }
    }
    public static void setTextPosition(TextPosition tp, int line, int col){
        tp.line_number = line;
        tp.char_number = col;
    }
    public static void setTextPositionCol(TextPosition tp, int col){
        tp.char_number = col;
    }
    public static void setTextPositionLine(TextPosition tp, int line){
        tp.char_number = line;
    }
    /**represents a position in the text*/
    public static class TextPosition implements Comparable<TextPosition> {
        public TextPosition(int line, int col){
            this.line_number = line;
            this.char_number = col;
        }
        /**
         * represents the line number in this FileManip2. 
         * line 0 is the first line
         */
        private int line_number;
        
        /**the "column" of text. Char 0 is before the line begins.*/
        private int char_number;
        public int getLine(){
            return line_number;
        }
        public int getColumn(){
            return char_number;
        }
        /**compares if one text position precedes another*/
        @Override public int compareTo(TextPosition other) {
            if(line_number != other.line_number){
                //the line numbers aren't the same, so we only need to look at 
                //the line numbers
                
                //either this object is after the other object (greater than) 
                //or this object precedes the other object (less than).
                //these are the only two options, as we know that the line 
                //numbers are not equal.
                return (line_number > other.line_number)?1 : -1;
            }
            else{
                //the line numbers are the same, so we have to look at the 
                //columns
                
                //there are three options, either this object is on a 
                //previous column than the other object (less than), this object 
                //is on a later column compared to the other object 
                //(greater than), or they are in the same column (equal to)
                if(char_number > other.char_number)
                    return 1;
                else if(char_number < other.char_number)
                    return -1;
                return 0;
            }
            
        }
        @Override public boolean equals(Object other){
            if(!(other instanceof TextPosition)) return false;
            //same char pos and line pos
            return ((TextPosition)other).char_number == char_number 
                    && ((TextPosition)other).line_number == line_number;
        }
        @Override public String toString(){
            return "[" + line_number + "," + char_number + "]";
        }
    }
    /**
     * keeps track of what sections have been edited and from which file each 
     * range comes from.
     */
    private class EditedRange implements Comparable<TextPosition> {
        //where the range began in the original file.
        TextPosition original_beginning;
        
        //the range of the file that belongs to this edited range
        Range range;
        //the name of the edited range. If it came from a file, it would be 
        //called the name of the filename. If the range was edited by the
        //client, the edited range's name is "edited"
        String name;
//        EditedRange(){}
        EditedRange(Range range, String name, TextPosition original_beginning){this.name = name; this.range = range; 
            this.original_beginning = original_beginning;
        }
        /**
         * tests whether a given text position is 
         *     in the range    (equal to)
         *     after this object's range (less than)
         *     before this object's range (greater than)
         */
        @Override public int compareTo(TextPosition o) {
            int i1 = o.compareTo(range.begin_position);
            int i2 = o.compareTo(range.end_position);
            
            if(i1!=i2) return 0; 
                //the two comparing statements are pointing in the opposite 
                //direction, and as we are comparing to a real point on the 
                //line, we know that this point must be greater than the minimum
                //and less than the max, and thus between the two points.
            
            //in this case, the two compareto statements are equivalent. So we 
            //only need to test the one.
            
            if(i1 > 0) return -1;
                //the text position is after (greater than) both the ranges.
                //so, this range is less than the textposition.
            if(i1 < 0) return 1;
                //the text position is before (less than) both the ranges.
                //so, this range is greater than the textposition.
            
            return 0;
                //if the range is a single element, and the position is pointing
                //directly to that element.
        }
        @Override public String toString(){
            return "{" + range.begin_position + " " + name + " " + range.end_position + "}";
        }
    }
    
    /**************************************************************************\
    |*                           overridden methods                           *|
    \**************************************************************************/
    @Override public String toString(){
        String ret = "";
        //run through the segments of code
        for(int i = 0; i<code_edits.size(); i++){
            EditedRange edit = code_edits.get(i);
            if(verbose) ret += begin(edit);
                ret += getText(edit.range);
            if(verbose) ret += end(edit);
        }
        return ret;
    }
}
