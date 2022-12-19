package liti.s3Legis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClassPropertiesOracle {
	private static ClassPropertiesOracle instance;
	public Properties propOracle = new Properties();

	public String ambiente = "";
	public String dbDriverOracle = "";
	public String dbServer = "";
	public String usr = "";
	public String pwd = "";
	public String sid = "";
	public String puerto = "";
	public String servidor= "";
	public int cantidadSesiones= 0;




    public static ClassPropertiesOracle getInstance(){
    	if (instance==null)
    		instance = new ClassPropertiesOracle();
    	return instance;
    }

    private ClassPropertiesOracle(){
    	InputStream isOracle = null;
		try {
			String propertOracle="";
			//String propertPostgressql="";

			String sPathProperties = ReadSystemProperties.getInstance().getPropertiesPath();

			propertOracle=sPathProperties + "oracle.properties";

			isOracle=new FileInputStream(propertOracle);
			propOracle.load(isOracle);

		    ambiente = propOracle.getProperty("dbAmbiente");

		    //Oracle
		    dbDriverOracle = propOracle.getProperty("dbDriverOracle");
			dbServer = propOracle.getProperty("dbServer");
		    usr = propOracle.getProperty("dbUser");
		    pwd = propOracle.getProperty("dbPwd");
		    sid = propOracle.getProperty("dbSid");
		    puerto = propOracle.getProperty("dbPuerto");

		    cantidadSesiones = Integer.parseInt(propOracle.getProperty("cantidadSesiones"));
            if (cantidadSesiones == 0)
            	cantidadSesiones = 10;
            UtilMensaje.getInstance().mensaje("cantidadSesiones Pool: " + cantidadSesiones);

            UtilMensaje.getInstance().mensaje("Ambiente Oracle: "+ambiente);
            UtilMensaje.getInstance().mensaje("Servidor: " + servidor);
    		UtilMensaje.getInstance().mensaje("Usuario Sistema Operativo: " + ReadSystemProperties.getInstance().getPropertiesUsername());

		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
    }

    public String getConeccionOra(){
    	return "jdbc:oracle:thin:@" + dbServer +":" + puerto + ":" + sid;
    }

    public String getParametrosOracle(){
    	String mensaje="ambiente (" + ambiente + ")\n"
	    + " dbDriverOracle (" + dbDriverOracle  + ")\n"
	    + " dbServer (" + dbServer  + ")\n"
	    //+ " usr (" + usr  + ")\n"
	    //+ " pwd (" + pwd  + ")\n"
	    + " sid (" + sid  + ")\n"
	    + " puerto (" + puerto  + ")\n";
	    return mensaje;
    }

}
