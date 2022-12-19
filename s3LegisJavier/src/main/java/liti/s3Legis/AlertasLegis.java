package liti.s3Legis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AlertasLegis {
	
	String sqlQuery = "";

	
	public void generarAlertaNovedades(){
		AlertaLegisDato dato = getNovedadesHoy();
	
		ArrayList<AlertaLegisDato> lista = getListNovedades();
		String tablaDatos = "";
		if (lista.size()>0){
			tablaDatos = convertirTableHtml(lista);
		}
		
		if (dato.iCantidad == 0){
			crearCorreo("Legis ***Urgente *** - No se han transmitido novedades " + dato.sFechaAhora, ". Las Novedades son creadas y transmitidas hoy. <p style=\"color: red;\">Informe al administrador.<p>" + tablaDatos, "Novedades");
		}else{
			crearCorreo("Legis - Se han transmitido " + dato.iCantidad + " novedades "  + dato.sFechaAhora, ". Las Novedades son creadas y transmitidas hoy." + tablaDatos, "Novedades");
		}
	
	}

	
	private String convertirTableHtml(ArrayList<AlertaLegisDato> lista) {
		String sHtml = "<table style=\"max-width: 550px; padding: 10px; margin:0 auto; border-collapse: collapse;\">"
		+ "<thead>"
		+ "	<tr>"
		+ "	 <th>Estado</th>"
		+ "	 <th>F Transmite</th>"
		+ "	 <th>Dia</th>"
		+ "	 <th>Cantidad</th>"
		+ "	 <th>Observaciones</th>"
		+ "	</tr>"
		+ "<thead>"
		+ "<tbody>";
		
		for (int i = 0; i < lista.size();i++){
			AlertaLegisDato dato = lista.get(i);
			sHtml +=  "	<tr>"
					+ "	<td>" + dato.sEstado + "</td>"
					+ "	<td>" + dato.sFechaTransmite + "</td>"
					+ "	<td>" + dato.sDia + "</td>"
					+ "	<td>" + dato.iCantidad + "</td>"
					+ "	<td>" + dato.sObservaciones + "</td>"
					+ "	</tr>";
		}
		sHtml += "</tbody>"
		+ "</table>";
		return sHtml;
	}

	private void crearCorreo(String sAsunto, String textoAdicional, String sTipo) {
		String sqCorreo = ConnectOracle.getInstance().getSecuencia("SQ_CORREOS_PENDIENTES");
		
		//String sConCopia=ClassPropertiesEmail.getInstance().copiaEmailLitiViejo;
		
		String sEmailDestinatarios = getEmailsDestinatarios();
		
		sqlQuery = "insert into correos_pendientes (correo_id"
				+ " , cliente_id, PROCESO_ID, INCIDENTE_ID, usuario_id "
				+ " , alias_remitente, destinatario, asunto "
				+ " , mensaje, origen, adjuntos_ruta "
				+ " , adjuntos_nombre "
				+ " , ID_SEGUN_ORIGEN "
				+ " )values(" + sqCorreo
				+ " , null, null, null , 'ALERTA_LEGIS_" + sTipo.toUpperCase() + "'"
				+ "	, 'Alerta legis " + sTipo.toLowerCase() +"', '" + sEmailDestinatarios+ "' , '" + sAsunto + "'"
				+ "	, '" + sAsunto + " - " + textoAdicional + "' , 'ALERTA_LEGIS_" + sTipo.toUpperCase() + "', null" 
				+ "	, null"
				+ " , null " 
				+ ")";		
		
		//UtilMensaje.getInstance().mensaje(sqlQuery);
		String resultado = ConnectOracle.getInstance().ejecutarQuery("correos_pendientes","crearCorreoPendiente",sqlQuery);
		
		UtilMensaje.getInstance().mensaje("Se crear correo " + sqCorreo + " - " + sAsunto + " - " + textoAdicional);
		
	}
	
	public String getEmailsDestinatarios(){
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;
		sqlQuery = "SELECT VALOR "
		+ " FROM CONFIGURACION_SYS "
		+ " WHERE PROCESO = 'LEGIS_ALERTAS' "
		+ " AND PARAMETRO = 'CORREOS'"; 
		
    	String correos = "";
		try {
            conn = ConnectOracle.getInstance().pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            if (rsLeer.next()) {
            	correos = rsLeer.getString("VALOR"); 
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error getActuacionHomologadaNewTitulo: " + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return correos;	
	}	

	public AlertaLegisDato getNovedadesHoy(){
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;
		sqlQuery = " SELECT COUNT (*) CANTIDAD, SYSDATE AS FECHA "
             + " FROM LEGIS_NOVEDADES LN "
             + " WHERE COD_ERROR_WS = 200 "
             + "   AND TRUNC (FECHA_CREACION) = TRUNC(SYSDATE)";
		
    	AlertaLegisDato dato = new AlertaLegisDato();
		try {
            conn = ConnectOracle.getInstance().pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            if (rsLeer.next()) {
            	//dato.sEstado = rsLeer.getString("ESTADO"); 
            	dato.sFechaTransmite = rsLeer.getString("FECHA"); 
              	//dato.sDia = rsLeer.getString("DIA"); 
            	dato.iCantidad = rsLeer.getInt("CANTIDAD"); 
               	//dato.sObservaciones = rsLeer.getString("OBSERVACIONES"); 
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error getActuacionHomologadaNewTitulo: " + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dato;	
	}

	public void generarAlertaDocumentos(){
		AlertaLegisDato dato = getDocumentosHoy();
		ArrayList<AlertaLegisDato> lista = getListDocuments();
		String tablaDatos = "";
		if (lista.size()>0){
			tablaDatos = convertirTableHtml(lista);
		}
		if (dato.iCantidad == 0){
			crearCorreo("Legis +++Urgente+++ - No se han transmitido pdfs " + dato.sFechaAhora, ". Los Documentos son creados y transmitidos hoy. <p style=\"color: red;\">Informe al administrador.<p>"+ tablaDatos, "Documentos");
		}else{
			crearCorreo("Legis * Se han transmitido " + dato.iCantidad + " pdfs asociados a las actuaciones "  + dato.sFechaAhora, ". Los Documentos son creados y transmitidos hoy."+ tablaDatos, "Documentos");
		}

		/*ArrayList<AlertaLegisDato> lista = getListNovedades();
		if (lista.size()==0){
			
		}*/
	}

	
	public AlertaLegisDato getDocumentosHoy(){
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;
		sqlQuery = " SELECT COUNT (*) CANTIDAD, SYSDATE AS FECHA "
             + " FROM LEGIS_DOCUMENTOS LN "
             + " WHERE COD_ERROR = 200 "
             + "   AND TRUNC (FECHA_CREACION) = TRUNC(SYSDATE)";
		
    	AlertaLegisDato dato = new AlertaLegisDato();
		try {
            conn = ConnectOracle.getInstance().pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            if (rsLeer.next()) {
            	//dato.sEstado = rsLeer.getString("ESTADO"); 
            	dato.sFechaTransmite = rsLeer.getString("FECHA"); 
              	//dato.sDia = rsLeer.getString("DIA"); 
            	dato.iCantidad = rsLeer.getInt("CANTIDAD"); 
               	//dato.sObservaciones = rsLeer.getString("OBSERVACIONES"); 
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error getActuacionHomologadaNewTitulo: " + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dato;	
	}

	public ArrayList<AlertaLegisDato> getListNovedades(){
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

	    ArrayList<AlertaLegisDato> lista = new ArrayList<AlertaLegisDato>();

		sqlQuery = " SELECT ESTADO, to_char(FECHA_TRANSMITE,'YYYY-MM-DD') as FECHA_TRANSMITE, DIA, CANTIDAD, nvl(OBSERVACIONES,'') as OBSERVACIONES "
	    + " FROM (  " 
	    + " SELECT ESTADO, NULL AS FECHA_TRANSMITE, NULL AS DIA, COUNT (*) CANTIDAD, OBSERVACIONES "
	    + "   FROM LEGIS_NOVEDADES LN "
	    + "  WHERE ESTADO in ('ENVIAR') "
	    + "    AND LN.NOVEDAD_ID IN ( "
	    + "        SELECT novedad_id "
	    + "          FROM legis_novedades "
	    + "         WHERE TRUNC (fecha_creacion) >= trunc(sysdate - 8)) "
	    + "         GROUP BY ESTADO, null, null, OBSERVACIONES "
	    + "       UNION "
	    + "         SELECT 'TRANSMITIDO', TRUNC (FECHA_TRANSMITE) AS FECHA_TRANSMITE "
	    + "         , TO_CHAR (TRUNC (FECHA_TRANSMITE), 'DAY', 'NLS_DATE_LANGUAGE=SPANISH') DIA, COUNT (*) CANTIDAD, NULL AS OBSERVACIONES "
	    + "           FROM LEGIS_NOVEDADES LN "
	    + "          WHERE COD_ERROR_WS = 200 "
	    + "            AND LN.NOVEDAD_ID IN "
	    + "                       (SELECT novedad_id "
	    + "                          FROM legis_novedades "
	    + "                         WHERE TRUNC (fecha_creacion) >= trunc(sysdate - 8))  "
	    + "       GROUP BY 'TRANSMITIDO', TRUNC (FECHA_TRANSMITE), TO_CHAR (TRUNC (FECHA_TRANSMITE)) "
	    + "       ) "
	    + " ORDER BY ESTADO, FECHA_TRANSMITE DESC ";
		try {
            conn = ConnectOracle.getInstance().pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			UtilMensaje.getInstance().mensaje(sqlQuery);

            while (rsLeer.next()) {
            	AlertaLegisDato dato = new AlertaLegisDato();
            	dato.sEstado = rsLeer.getString("ESTADO"); 
            	dato.sFechaTransmite = rsLeer.getString("FECHA_TRANSMITE");
            	if (dato.sFechaTransmite == null){
            		dato.sFechaTransmite = "";
            	}
              	dato.sDia = rsLeer.getString("DIA"); 
            	if (dato.sDia==null){
            		dato.sDia = "";
            	}
            	dato.iCantidad = rsLeer.getInt("CANTIDAD"); 
               	dato.sObservaciones = rsLeer.getString("OBSERVACIONES"); 
            	if (dato.sObservaciones==null){
            		dato.sObservaciones = "";
            	}
            	lista.add(dato);
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error getListNovedades: " + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}catch(Exception exception){
			UtilMensaje.getInstance().mensaje("Error getListNovedades: " +  exception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return lista;	
	}
	
	public ArrayList<AlertaLegisDato> getListDocuments(){
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

	    ArrayList<AlertaLegisDato> lista = new ArrayList<AlertaLegisDato>();

		sqlQuery = "SELECT ESTADO, TO_CHAR(FECHA_TRANSMITE,'YYYY-MM-DD') AS FECHA_TRANSMITE, DIA, CANTIDAD FROM ("
				+ "     SELECT ESTADO, TRUNC(FECHA_TRANSMITE) AS FECHA_TRANSMITE  "
				 + "      , TO_CHAR(TRUNC(FECHA_TRANSMITE) , 'DAY', 'NLS_DATE_LANGUAGE=SPANISH') DIA "
				 + "      , COUNT(*) CANTIDAD "
				 + "      FROM LEGIS_DOCUMENTOS LD "
				 + "      WHERE ESTADO IN ('TRANSFERIDO','ENVIAR') "
				 + "      AND TRUNC (fecha_creacion) >= trunc(sysdate - 8) "
				 + "      GROUP BY ESTADO, TRUNC(FECHA_TRANSMITE), TO_CHAR(TRUNC(FECHA_TRANSMITE)) "
				 + ")"
				 + " ORDER BY FECHA_TRANSMITE DESC, ESTADO   ";
		try {
            conn = ConnectOracle.getInstance().pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			UtilMensaje.getInstance().mensaje(sqlQuery);

            while (rsLeer.next()) {
            	AlertaLegisDato dato = new AlertaLegisDato();
            	dato.sEstado = rsLeer.getString("ESTADO"); 
            	dato.sFechaTransmite = rsLeer.getString("FECHA_TRANSMITE");
            	if (dato.sFechaTransmite == null){
            		dato.sFechaTransmite = "";
            	}
              	dato.sDia = rsLeer.getString("DIA"); 
            	if (dato.sDia==null){
            		dato.sDia = "";
            	}
            	dato.iCantidad = rsLeer.getInt("CANTIDAD"); 
               	/*dato.sObservaciones = rsLeer.getString("OBSERVACIONES"); 
            	if (dato.sObservaciones==null){
            		dato.sObservaciones = "";
            	}*/
            	lista.add(dato);
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error getListDocuments: sqlexception -" + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}catch(Exception exception){
			UtilMensaje.getInstance().mensaje("Error getListDocuments: exception - " +  exception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return lista;	
	}	
}
