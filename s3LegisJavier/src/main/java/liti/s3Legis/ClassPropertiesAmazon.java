package liti.s3Legis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClassPropertiesAmazon {
	private static ClassPropertiesAmazon instance;
	private static Properties prop = new Properties();

	//Amazon
	public String bucketName = "";
	public String access_key_id = "";
	public String secret_access_key = "";

    public static ClassPropertiesAmazon getInstance(){
    	if (instance==null)
    		instance = new ClassPropertiesAmazon();
    	return instance;
    }

    private ClassPropertiesAmazon(){
    	InputStream is = null;
		try {
			String propert="";
			String sPathProperties = ReadSystemProperties.getInstance().getPropertiesPath(); 
			propert=sPathProperties + "amazon.properties";

			is=new FileInputStream(propert);
			prop.load(is);

		    //Amazon
			bucketName = prop.getProperty("bucketName");
			access_key_id = prop.getProperty("access_key_id");
			secret_access_key = prop.getProperty("secret_access_key");

		    UtilMensaje.getInstance().mensaje("Amazon: "+bucketName);
		    //UtilMensaje.getInstance().mensaje("key: "+access_key_id);
		    //UtilMensaje.getInstance().mensaje("access: "+secret_access_key);

		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
    }

    public String getParametrosAmazon(){
    	String mensaje= " bucketName (" + bucketName  + ")\n"
	    + " access_key_id (" + access_key_id  + ")\n"
	    + " secret_access_key (" + secret_access_key  + ")\n"
	    ;
	    return mensaje;
    }

}
