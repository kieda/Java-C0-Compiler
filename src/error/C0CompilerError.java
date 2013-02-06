package error;

import compl.data.FileManip;

/**
 * Errors dealing with actual compilation of C0 code.
 * @author kieda
 */
public abstract class C0CompilerError {
    static FileManip fmp;
    public static void open(FileManip file){
        fmp = file;
    }
    
    public static final int CRITICAL_FAILURE = -666;
    public static final int COMPILATION_ABORTED = 0;
    public static final int COMPILATION_FAILED = 1;
    public static final int MOST_EXCELLENT = 69;
    public static String exitCode(int number){
        switch(number){
            case CRITICAL_FAILURE: return "Critical failure";
            case COMPILATION_ABORTED: return "Compilation aborted";
            case COMPILATION_FAILED: return "Compilation failed";
            case MOST_EXCELLENT: return "Compilation successful";
            default: return null;
        }
    }
    public abstract String errorType();
    FileManip.Range range;
    String message;
    public C0CompilerError(FileManip.Range error_range, String message){
        assert fmp != null;
        this.range = error_range;
        this.message = message;
    }
}
