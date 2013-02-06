package project;

/**
 * an abstract system used for this project
 * @author kieda
 */
public abstract class ProjectInfo {
   protected abstract String project_version();
   protected abstract String project_name();
   protected abstract String project_author();
   
   protected final static String NAME_BINDING    = "project_name";
   protected final static String VERSION_BINDING = "project_version";
   protected final static String AUTHOR_BINDING  = "project_author";
   
   

   {
       System.setProperty(VERSION_BINDING, project_version());
       System.setProperty(NAME_BINDING, project_name());
       System.setProperty(AUTHOR_BINDING, project_author());
   }
   
     public static String getName(){
       return System.getProperty(NAME_BINDING);
   } public static String getVersion(){
       return System.getProperty(VERSION_BINDING);
   } public static String getAuthor(){
       return System.getProperty(AUTHOR_BINDING);
   }
}
