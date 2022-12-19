package liti.s3Legis;

import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
//import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.net.ContentHandlerFactory;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
//import java.sql.Timestamp;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;

import com.google.gson.Gson;


public class ConexionWebService {
	private static ConexionWebService instance;
	private int codError;
	private String resource;
	private String estadoEnvio;
	private String token;
	private String tokenSerializado;
	private int idActuacion;
	private boolean archivoEnviado;
	private String sJson;
	private HttpURLConnection conn;


    public static ConexionWebService getInstance(){
        if (instance==null)
    		instance = new ConexionWebService();
    	return instance;
    }


    private ConexionWebService() {
    	newToken();
	}

    private void newToken(){
    	//401 -- Acceso no autorizado.
    	//403 -- El Token no existe o ha caducado. Es necesario que gener� un nuevo	Token.
    	if (instance == null || getCodError() == 401 || getCodError() == 403){
    		this.setResource("");
    		this.setCodError(0);
    		this.setToken("");
    		getValidarCredenciales();
    	}
    }


    private void openConn(String sTipo, String sMetodo) {
    	String msg="";
		try{
			this.setResource(sTipo);

			//String servicio = "https://vj.legisoffice.com/LOServiceVJ/VJ/" + getResource();
			String servicio = ClassPropertiesLegis.getInstance().url + getResource();
			//UtilMensaje.getInstance().mensaje(servicio);
			//UtilMensaje.getInstance().mensaje("getTokenSerializado " + this.getTokenSerializado());
			URL url = new URL(servicio);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Token " + this.getTokenSerializado());
			conn.setRequestProperty("Encoding", "UTF-8");
			//conn.setRequestMethod("POST");
			conn.setRequestMethod(sMetodo);
			conn.setDoOutput(true);
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			msg = e.getMessage();
	    	conn.disconnect();
		} catch (IOException e) {
			//e.printStackTrace();
			msg = e.getMessage();
	    	conn.disconnect();
		}

		setEstadoEnvio(msg.replace("\n", ""));
	}



	public String getTest() {
		String retorno = "";
		try {
			setResource("Test");
			URL url = new URL(ClassPropertiesLegis.getInstance().url + getResource());
			//URL url = new URL("http://vj.legisoffice.com/LOServiceVJ/VJ/" + getResource());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");

			conn.setRequestMethod("GET");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Fallo conexi�n : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			UtilMensaje.getInstance().mensaje("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				UtilMensaje.getInstance().mensaje(output);
				if (output.toString().trim() != "") {
					this.setEstadoEnvio(output.toString().trim());
					retorno = "OK";
				}
			}

			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retorno;
	}

	public boolean getArchivoEnviado(){
		return archivoEnviado;
	}

	public void setArchivoEnviado(boolean bArchivoEnviado){
		archivoEnviado = bArchivoEnviado;
	}

	public String getJson(){
		return sJson;
	}

	public void setJson(String js){
		sJson = js;
	}

	public String getValidarCredenciales() {
		String retorno = "";
		try {

    		this.setResource("ValidarCredenciales");

			//String sUrl = "https://vj.legisoffice.com/LOServiceVJ/VJ/" + getResource();
			String sUrl = ClassPropertiesLegis.getInstance().url + getResource();

			UtilMensaje.getInstance().mensaje(sUrl);

			URL url = new URL(sUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			String sjson = "";

			conn.setRequestMethod("POST");
			sjson = "{"
					+ "\"Usuario\": \""+ ClassPropertiesLegis.getInstance().usuario +"\","
					+ "\"Clave\": \""+ ClassPropertiesLegis.getInstance().password +"\","
					+ "\"Proveedor\": \""+ ClassPropertiesLegis.getInstance().proveedor +"\""
					+ "}";

			String input = sjson;

			//UtilMensaje.getInstance().mensaje(input);

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Fallo conexi�n : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			//UtilMensaje.getInstance().mensaje("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				//UtilMensaje.getInstance().mensaje("Token: "+output);
				if (output.toString().trim() != "") {
					this.setToken(output.toString().replace("\"", ""));
					//UtilMensaje.getInstance().mensaje("Token: "+output.toString().replace("\"", ""));
					retorno = "OK";
				}
				//UtilMensaje.getInstance().mensaje("Token: " + getToken());
			}
			CodeBase64 cb = new CodeBase64(getToken()) ;
			String sTokenCodeBase64 = cb.serializarToken();
			this.setTokenSerializado(sTokenCodeBase64);
			//UtilMensaje.getInstance().mensaje("Token Base64: " + getTokenSerializado());
			//conn.disconnect();

		} catch (MalformedURLException e) {
			UtilMensaje.getInstance().mensaje("getValidarCredenciales MalformedURLException: " + e);
			retorno = "getValidarCredenciales MalformedURLException: " + e;
			e.printStackTrace();
		} catch (IOException e) {
			UtilMensaje.getInstance().mensaje("getValidarCredenciales IOException: " + e);
			retorno = "getValidarCredenciales IOException: " + e;
			e.printStackTrace();
		}
		return retorno;
	}

	public void transmitirActuacionRetornaActIdLegis(DatosActuacionInsert regAct) {
		String msg = "";
		long inicio = System.currentTimeMillis();

		try {
			newToken();//Generar token
			this.openConn("RegistrarActuacion", "POST");

			// Armar el json con Gjson
			final Gson gson = new Gson();
			String sJson = gson.toJson(regAct);
			setJson(sJson);
	        //UtilMensaje.getInstance().mensaje(sJson);


			conn.getOutputStream().write(sJson.getBytes("UTF-8"));
	        UtilMensaje.getInstance().mensaje(">>>>>Inicio Respuesta RegistrarActuacion: " + conn.getResponseCode());
	        this.setCodError(conn.getResponseCode());

	        try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		        StringBuilder builder = new StringBuilder();

		        for (String line = null; (line = reader.readLine()) != null;)
		        {
		            builder.append(line).append("\n");
		        }

		        reader.close();

		        UtilMensaje.getInstance().mensaje(">>>>>Fin Respuesta OK: " + conn.getResponseCode());
		        UtilMensaje.getInstance().mensaje(">>>>>ActuacionID: " + builder.toString());
		        msg = conn.getResponseCode() + " - " + builder.toString().replace("\n", "");
		        this.setCodError(conn.getResponseCode());

				this.setIdActuacion(0);
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					UtilMensaje.getInstance().mensaje("transmitirActuacionInsert : " + conn.getResponseCode());
					throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
				}

				String sIdActuacion = builder.toString().replace("\n", "");
				this.setIdActuacion( ( new Integer(sIdActuacion) ).intValue());
				conn.disconnect();

	        }catch (IOException e) {
				BufferedReader readerError = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
		        StringBuilder builderError = new StringBuilder();

		        for (String line = null; (line = readerError.readLine()) != null;)
		        {
		        	builderError.append(line).append("\n");
		        }
		        readerError.close();
		        msg = builderError.toString();
		        UtilMensaje.getInstance().mensaje(">>>>>Fin Respuesta RegistrarActuacion Error: " + builderError.toString());
		    	conn.disconnect();
	        }

		} catch (MalformedURLException e) {
			//e.printStackTrace();
			msg = e.getMessage();
	    	conn.disconnect();
		} catch (IOException e) {
			//e.printStackTrace();
			msg = e.getMessage();
	    	conn.disconnect();
		}

		setEstadoEnvio(msg.replace("\n", ""));

		long fin = System.currentTimeMillis();
		double tiempo = (double) ((fin - inicio)/1000);
		UtilMensaje.getInstance().mensaje("\nDuraci�n del env�o RegistrarActuacion: minutos [" + tiempo/60 + "] segundos [" + tiempo + "]");
	}

	public void transmitirActuacionEnviaActIdLegis(DatosActuacionUpdate regAct) {
		String msg = "";
		long inicio = System.currentTimeMillis();

		try {
			newToken();//Generar token
			this.openConn("ActualizarActuacion", "POST");

			// Armar el json con Gjson
			final Gson gson = new Gson();
			String sJson = gson.toJson(regAct);
			setJson(sJson);
	        //UtilMensaje.getInstance().mensaje(sJson);


			conn.getOutputStream().write(sJson.getBytes("UTF-8"));
	        UtilMensaje.getInstance().mensaje(">>>>>Inicio Respuesta ActualizarActuacion: " + conn.getResponseCode());
	        this.setCodError(conn.getResponseCode());

	        try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		        StringBuilder builder = new StringBuilder();

		        for (String line = null; (line = reader.readLine()) != null;)
		        {
		            builder.append(line).append("\n");
		        }

		        reader.close();

		        UtilMensaje.getInstance().mensaje(">>>>>Fin Respuesta OK: " + conn.getResponseCode());
		        UtilMensaje.getInstance().mensaje(">>>>>Estado transmici�n: " + builder.toString());
		        msg = conn.getResponseCode() + " - " + builder.toString().replace("\n", "");
		        this.setCodError(conn.getResponseCode());

				this.setIdActuacion(0);
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					UtilMensaje.getInstance().mensaje("transmitirActuacionUpdate : " + conn.getResponseCode());
					throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
				}
				msg = builder.toString().replace("\n", "");//Estado Transmicion - True / False
				conn.disconnect();

	        }catch (IOException e) {
				BufferedReader readerError = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
		        StringBuilder builderError = new StringBuilder();

		        for (String line = null; (line = readerError.readLine()) != null;)
		        {
		        	builderError.append(line).append("\n");
		        }
		        readerError.close();
		        msg = builderError.toString();
		        UtilMensaje.getInstance().mensaje(">>>>>Fin Respuesta ActualizarActuacion Error:  " + builderError.toString());
		        UtilMensaje.getInstance().mensaje(sJson);
		    	conn.disconnect();
	        }

		} catch (MalformedURLException e) {
			//e.printStackTrace();
			msg = e.getMessage();
	    	conn.disconnect();
		} catch (IOException e) {
			//e.printStackTrace();
			msg = e.getMessage();
	    	conn.disconnect();
		}

		ConexionWebService.getInstance().setIdActuacion(Integer.parseInt(regAct.getIdActuacion())) ;
		setEstadoEnvio(msg.replace("\n", ""));

		long fin = System.currentTimeMillis();
		double tiempo = (double) ((fin - inicio)/1000);
		UtilMensaje.getInstance().mensaje("\nDuraci�n del env�o: minutos [" + tiempo/60 + "] segundos [" + tiempo + "]");
	}
	//public void transmitirDocumento(DatosActuacion regAct) {
	public void transmitirDocumento(DatosDocumentos regDocAct, String sDocumentoId) {
		String msg = "";
		long inicio = System.currentTimeMillis();

		try {
			newToken();//Generar token
			this.openConn("RegistrarDocumentoActuacion", "POST");

			// Armar el json con Gison
			final Gson gson = new Gson();
			String sJson = gson.toJson(regDocAct);
			//setJson(sJson);
	        //UtilMensaje.getInstance().mensaje(sJson);

			//Para enviarle documentos json a legis y ellos puedan hacer pruebas.
			//El nombre del archivo queda en: D:/logs/Pdf-DocumentoId-mes-dia_hh-mm.log
			//Ej: D:/logs/Pdf-2020582021-03-16_18-08.log
			//UtilMensajeJsonPDF.getInstance().mensajeJson(sDocumentoId, sJson);


			conn.getOutputStream().write(sJson.getBytes("UTF-8"));
	        UtilMensaje.getInstance().mensaje("Documento " + sDocumentoId + " >>>>>Inicio Respuesta: " + conn.getResponseCode());
	        this.setCodError(conn.getResponseCode());

	        try{
		        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		        StringBuilder builder = new StringBuilder();

		        for (String line = null; (line = reader.readLine()) != null;)
		        {
		            builder.append(line).append("\n");
		        }

		        reader.close();

		        UtilMensaje.getInstance().mensaje("Documento " + sDocumentoId + " >>>>>Fin Respuesta: " + conn.getResponseCode());
		        UtilMensaje.getInstance().mensaje("Documento " + sDocumentoId + " >>>>>DocumentoEnviado: " + builder.toString());
		        msg = conn.getResponseCode() + " - " + builder.toString().replace("\n", "");
		        this.setCodError(conn.getResponseCode());

				this.setIdActuacion(0);
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					UtilMensaje.getInstance().mensaje("Documento " + sDocumentoId + " transmitirActuacion : " + conn.getResponseCode());
					throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
				}

				String sArchivoEnviado = builder.toString().replace("\n", "");
				this.setArchivoEnviado(new Boolean(sArchivoEnviado).booleanValue());
				conn.disconnect();

	        }catch (IOException e) {
				BufferedReader readerError = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
		        StringBuilder builderError = new StringBuilder();

		        for (String line = null; (line = readerError.readLine()) != null;)
		        {
		        	builderError.append(line).append("\n");
		        }
		        readerError.close();
		        msg = builderError.toString();
		        UtilMensaje.getInstance().mensaje("Documento " + sDocumentoId + " >>>>>Error: " + builderError.toString());
		    	conn.disconnect();
	        }

		} catch (MalformedURLException e) {
			e.printStackTrace();
			msg = e.getMessage();
	    	conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			msg = e.getMessage();
	    	conn.disconnect();
		}

		setEstadoEnvio(msg.replace("\n", ""));

		long fin = System.currentTimeMillis();
		double tiempo = (double) ((fin - inicio)/1000);
		UtilMensaje.getInstance().mensaje("Documento " + sDocumentoId + " Duraci�n del env�o: minutos [" + tiempo/60 + "] segundos [" + tiempo + "]");

	}

	public void transmitirActuacionEliminar(DatosEliminaActuacion regActEliminar, String sNovedadId) {
		String msg = "";
		long inicio = System.currentTimeMillis();

		try {
			newToken();//Generar token
			this.openConn("EliminarActuacion", "POST");
			
			// Armar el json con Gison
			final Gson gson = new Gson();
			String sJson = gson.toJson(regActEliminar);
			//setJson(sJson);
	        //UtilMensaje.getInstance().mensaje(sJson);

			conn.getOutputStream().write(sJson.getBytes("UTF-8"));
	        UtilMensaje.getInstance().mensaje("NOVEDAD_ID " + sNovedadId + " >>>>>Inicio Respuesta: " + conn.getResponseCode());
	        this.setCodError(conn.getResponseCode());

	        try{
		        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		        StringBuilder builder = new StringBuilder();
		        
		        for (String line = null; (line = reader.readLine()) != null;) 
		        {
		            builder.append(line).append("\n");
		        }
		        
		        reader.close();
		        
		        UtilMensaje.getInstance().mensaje("NOVEDAD_ID " + sNovedadId + " >>>>>Fin Respuesta: " + conn.getResponseCode());
		        UtilMensaje.getInstance().mensaje("NOVEDAD_ID " + sNovedadId + " >>>>>NovedadEnviada: " + builder.toString());
		        msg = conn.getResponseCode() + " - " + builder.toString().replace("\n", "");
		        this.setCodError(conn.getResponseCode());
	
				this.setIdActuacion(0);
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					UtilMensaje.getInstance().mensaje("NOVEDAD_ID " + sNovedadId + " transmitirActuacion : " + conn.getResponseCode());
					throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
				}
	
				conn.disconnect();
				
	        }catch (IOException e) {
				BufferedReader readerError = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
		        StringBuilder builderError = new StringBuilder();
		        
		        for (String line = null; (line = readerError.readLine()) != null;) 
		        {
		        	builderError.append(line).append("\n");
		        }
		        readerError.close();
		        msg = builderError.toString();
		        UtilMensaje.getInstance().mensaje("NOVEDAD_ID " + sNovedadId + " >>>>>Error: " + builderError.toString());
		    	conn.disconnect();
	        }

		} catch (MalformedURLException e) {
			e.printStackTrace();
			msg = e.getMessage();
	    	conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			msg = e.getMessage();
	    	conn.disconnect();
		}
		
		setEstadoEnvio(msg.replace("\n", ""));
		
		long fin = System.currentTimeMillis();
		double tiempo = (double) ((fin - inicio)/1000);
		UtilMensaje.getInstance().mensaje("NOVEDAD_ID " + sNovedadId + " Duracion del envio: minutos [" + tiempo/60 + "] segundos [" + tiempo + "]");
		
	}
	
	public int getCodError() {
		return codError;
	}

	public void setCodError(int codError) {
		this.codError = codError;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getToken() {
		return token;
	}

	public String getEstadoEnvio() {
		return estadoEnvio;
	}

	public String getTokenSerializado() {
		return tokenSerializado;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setEstadoEnvio(String estado) {
		this.estadoEnvio = estado;
	}

	public void setTokenSerializado(String stoken) {
		this.tokenSerializado = stoken;
	}

	public int getIdActuacion() {
		return idActuacion;
	}

	public void setIdActuacion(int idActuacion) {
		this.idActuacion = idActuacion;
	}

	public static void main(String[] args) {
		String codigo = "";

		ConexionWebService conn = new ConexionWebService();

		conn.setResource("Test");
		codigo = conn.getTest();
		UtilMensaje.getInstance().mensaje(codigo);
		if (codigo == "OK") {
			conn.setResource("ValidarCredenciales");
			codigo = conn.getValidarCredenciales();
			UtilMensaje.getInstance().mensaje(codigo);
			//UtilMensaje.getInstance().mensaje(conn.getToken());
		}
	}
}
