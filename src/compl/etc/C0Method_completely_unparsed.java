/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

import compl.data.FileManip;
import compl.data.FileManipScanner;
import java.util.List;

/**
 * a holder of shit to be parsed later.
 * @author kieda
 */
public class C0Method_completely_unparsed extends C0Interface_completely_unparsed{
    FileManip body;
    public C0Method_completely_unparsed(FileManip name_and_types, FileManip params, List<FileManipScanner.C0Token> assertions, FileManip body){
        super(name_and_types, params, assertions);
        this.body = body;
    }
}
