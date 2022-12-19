package liti.s3Legis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClassPropertiesLegis {
	private static ClassPropertiesLegis instance;
	public Properties prop = new Properties();
	String usuario = "";
	String password = "";
	String proveedor = "";
	String sFechaProceso = "";
	String url = "";
	String rutaLegis = "";

	int cantidadPool=0;

	String cantidadDocumentosTransmitir="";
	int cantidadDocumentosLista=0;

	String cantidadActuacionesEliminarTransmitir="";
	int cantidadActuacionesEliminarLista=0;


    public static ClassPropertiesLegis getInstance(){
    	if (instance==null)
    		instance = new ClassPropertiesLegis();
    	return instance;
    }

    private ClassPropertiesLegis(){
    	InputStream is = null;
		try {
			String propert="";
			
			String sPathProperties = ReadSystemProperties.getInstance().getPropertiesPath(); 

			propert=sPathProperties + "legis.properties";
			
			is=new FileInputStream(propert);
			prop.load(is);

			usuario = prop.getProperty("usuario");
			password = prop.getProperty("password");
			proveedor = prop.getProperty("proveedor");
			sFechaProceso = prop.getProperty("sFechaProceso");
			url = prop.getProperty("url");
			rutaLegis = prop.getProperty("rutaLegis");//Para traer archivos de amazon
			
			cantidadPool = Integer. parseInt(prop.getProperty("cantidadPool"));//Cantidad de conexiones
			cantidadDocumentosTransmitir = prop.getProperty("cantidadDocumentosTransmitir");//Para traer archivos de amazon
			cantidadDocumentosLista = Integer. parseInt(prop.getProperty("cantidadDocumentosLista"));//Cantidad de documentos por lista

			cantidadActuacionesEliminarTransmitir = prop.getProperty("cantidadActuacionesEliminarTransmitir");//Para enviar actuaciones a la api de legis
			cantidadActuacionesEliminarLista = Integer. parseInt(prop.getProperty("cantidadActuacionesEliminarLista"));//Cantidad de actuaciones por lista
            
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
    }

}
