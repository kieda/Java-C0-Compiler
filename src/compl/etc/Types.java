package compl.etc;

/**
 * Final static Small-Types for quick use.
 * @author kieda
 */
class Types{
    public static final compl.etc.Int    INT     = new Int();
    public static final compl.etc.Char   CHAR    = new Char();
    public static final compl.etc.Bool   BOOL    = new Bool();
    public static final compl.etc.Stri   STRI    = new Stri();
    public static final compl.etc.Void   VOID    = new Void();
    
    //void arrays and void pointers are not to be used as an actual C0 or type.
    //instead, these are used internally to represent a pointer to an anytype,
    //or an array of anytype.
  //  public static final Point  POINTER = new Point(VOID);
   // public static final Arra   ARRAY   = new Arra(VOID);
    //I should not use this unless if I REALLY have to.
    
        //no representation for a COTYPE, as it is not passed around as a 
        //variable
    public static Arra ARRAY(C0Data type){
        return new Arra(type);
    }
    public static compl.etc.Point POINTER(C0Data content){
        return new compl.etc.Point(content);
    }
}
