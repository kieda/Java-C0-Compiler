/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

import compl.data.FileManip;
import compl.data.FileManipScanner;
import java.util.List;

/**
 *
 * @author kieda
 */
public class C0Interface_completely_unparsed {
    //struct * asdf[]         (int i1, int i2)  /@requires i1>i2;                       @/ return NULL;
    FileManip name_and_types; FileManip params; List<FileManipScanner.C0Token> assertions;
    public C0Interface_completely_unparsed(FileManip name_and_types, FileManip params, List<FileManipScanner.C0Token> assertions){
        this.assertions = assertions;
        this.name_and_types = name_and_types;
        this.params = params;
    }
}
