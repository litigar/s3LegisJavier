package liti.s3Legis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClassPropertiesUtil {
	private static ClassPropertiesUtil instance;
	public Properties prop = new Properties();
	String pathLogsBatch = "";


    public static ClassPropertiesUtil getInstance(){
    	if (instance==null)
    		instance = new ClassPropertiesUtil();
    	return instance;
    }

    private ClassPropertiesUtil(){
    	InputStream is = null;
		try {
			String propert="";

			String sPathProperties = ReadSystemProperties.getInstance().getPropertiesPath();

			propert=sPathProperties + "util.properties";

			is=new FileInputStream(propert);
			prop.load(is);

			pathLogsBatch = prop.getProperty("pathLogsBatch");


		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
    }

}
