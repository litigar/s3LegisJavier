package liti.s3Legis;

import java.util.Map;
import java.util.Set;



public class ReadSystemProperties {

	private static ReadSystemProperties instance;

    public static ReadSystemProperties getInstance(){
    	if (instance==null){
    		instance = new ReadSystemProperties();
    	}
    	return instance;
    }

    public String getPropertiesPath(){
    	return System.getenv().get("LITI_PATH") + "/";
    }

    public String getPropertiesUsername(){
    	return System.getenv().get("USERNAME");
    }

    public String getPropertiesComputerName(){
    	return System.getenv().get("COMPUTERNAME");
    }



   public static void main(String[] args) {
     String user = System.getProperty("user");
     String password = System.getProperty("password");

     System.out.println("user = "+user);
     System.out.println("password = "+password);

     String myVar = System.getenv().get("LITI_PATH");
     System.out.println("LITI_PATH="+myVar);

     Map<String,String> env = System.getenv();
     Set<String> keys = env.keySet();
     for (String key: keys){
        System.out.println(key + " = "+env.get(key));
     }
   }

}