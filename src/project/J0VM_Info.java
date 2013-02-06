package project;

/**
 * the info that belongs to this project.
 * @author kieda
 */
public class J0VM_Info extends ProjectInfo{
    public static void open(){new J0VM_Info();}
    @Override protected String project_version() {return "v0.85";} 
    @Override protected String project_name()    {return "J0VM";}
    @Override protected String project_author()  {return "Kieda";}
}