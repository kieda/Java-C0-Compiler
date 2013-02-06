package compl.data;

public interface TokenParsingOperations {
    
    /**
     * constant to delete just the current token
     */
    public static int DELETE_TOKEN      = 00;
    /**
     * constant to delete just the current token
     */
    public static int KEEP_TOKEN        = 69;
    
    /**
     * return this constant to begin some sort of syntax block. From this point,
     * we set a marker till an END is called
     */
    public static int BEGIN             = 01;
    
    /**
     * return this statement when we should end our group of syntax, and to 
     * delete everything, including the final token
     */
    public static int END_DELETE        = 02;
    
    /**
     * return this statement when we should end our group of syntax, and to 
     * delete everything except for the final token
     */
    public static int END_DELETE_REWIND = 03;
    
    /**
     * return this statement when we should end our group of syntax, and to 
     * keep everything, including the final token
     */
    public static int END_KEEP          = 04;
    
    /**
     * return this statement when we should end our group of syntax, and to 
     * keep everything, except for the final token. We start scanning at the
     * previous token.
     */
    public static int END_KEEP_REWIND   = 05;
    
    /**
     * return this statement when we should end our group of syntax, and to 
     * keep everything, except for the final token. We start scanning at the
     * previous token.
     */
    public static int CONTINUE          = 06;
}
