package liti.s3Legis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gson.Gson;

public class ConnectOracle {
	private static ConnectOracle instance;
	Pool pool;
    public boolean encontreRadicado = false;
    String auditorText = "";
    float val = 1.0f;
    public String dbServer = "";
    public String usr = "";
    public String pwd = "";
    public String sid = "";
    public String puerto = "";
    public String servidor= "";
    String error = "";

    public static ConnectOracle getInstance(){
    	if (instance==null)
    		instance = new ConnectOracle();
    	return instance;
    }

    private ConnectOracle(){
		pool = new Pool();
    }

    public boolean isConnect(){
    	boolean valida = false;
	    Connection conn=null;
		try {
			conn = pool.ds.getConnection();

			if (conn == null || !conn.isValid(1) ||  conn.isClosed()){
				//UtilMensaje.getInstance().mensaje("Conn err");
				valida = false;
			}
			else{
				//UtilMensaje.getInstance().mensaje("Conn ok");
				valida = true;
			}
		} catch (SQLException e) {
			UtilMensaje.getInstance().mensaje(e.getMessage() + " " + ClassPropertiesOracle.getInstance().getParametrosOracle());
			//e.printStackTrace();
        }finally{
    		try {
    			conn.close();
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
        }
		return valida;

    }

    //---------------------------------
    // ---- Fin Administrador de colas
    //---------------------------------

	public String ejecutarQuery(String tabla, String sPrograma, String sqlQuery){
	    Connection conn=null;
	    Statement stUpd=null;
		String sMensaje="";
		try{
            conn = pool.ds.getConnection();
            stUpd = conn.createStatement();

            stUpd.executeUpdate(sqlQuery);
			//sMensaje= "" + stUpd.getUpdateCount();
			//conn.commit();
			//UtilMensaje.getInstance().mensaje("Procesados  " + sMensaje +" registros. Tabla: " + tabla + ". " + sPrograma + " " + new Timestamp(new Date().getTime()));
			sMensaje = "ok";
		}
		catch(SQLException sqlexception)
		{
			sMensaje+= "Error sqlexception: Programa " + sPrograma + " " + sqlexception.getErrorCode() + " - " + sqlexception.getMessage() + " sqlQuery " + sqlQuery ;
			UtilMensaje.getInstance().mensaje(sMensaje);
		}
		catch(Exception exception)
		{
			sMensaje+= "Error exception: Programa " + sPrograma + " " + exception + " sqlQuery " + sqlQuery ;
			UtilMensaje.getInstance().mensaje(sMensaje);
		}finally{
			try {
    			conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return sMensaje;

	}
	//--------------------------------------------------------

	public String isProcesoEnEjecucion(String sProceso){
        String sqlQuery = "";
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

		String sActivo = "N";

		try {
            sqlQuery = "select nvl(VALOR,'N') ACTIVO"
            + " FROM CONFIGURACION_SYS "
        	+ " where proceso = '" + sProceso + "'";

            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            while (rsLeer.next()) {
            	sActivo = rsLeer.getString("ACTIVO");
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error isProcesoEstaEnEjecucion: " + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		UtilMensaje.getInstance().mensaje("isProcesoEstaEnEjecucion EnEjecucion: " + sActivo);
		return sActivo;
	}

	public void	actualizaEstadoProcesoEnEjecucion(String sProceso, String sEjecucion){
    	String sqlQuery = "";

    	sqlQuery = "update CONFIGURACION_SYS "
    			+ " set VALOR = '" + sEjecucion + "'"
    			+ " , fecha = sysdate"
    	+ " where PROCESO = '" + sProceso + "'";


    	ejecutarQuery("actualizaEstadoProcesoEnEjecucion", "CONFIGURACION_SYS", sqlQuery);
	}

	public void	verificarProcesoEnEjecucion(String sProceso){
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;

		//Si existe proceso en ejecucion mayor a 45 minutos, lo pasa a N, para quie se ejecute otro evento
        String sqlQuery = "";

		String sActivo = "N";

		try {
            sqlQuery = "select VALOR"
            + " FROM CONFIGURACION_SYS "
        	+ " where PROCESO = '" + sProceso + "'"
            + " and VALOR = 'S'"
        	+ " and (sysdate - fecha) * 24 >= 1 ";

            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            while (rsLeer.next()) {
        		actualizaEstadoProcesoEnEjecucion(sProceso, "N");
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error verificarProcesoEnEjecucion: " + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		UtilMensaje.getInstance().mensaje("verificarProcesoEnEjecucion. Proceso: " + sProceso + ". En ejecucion: " + sActivo);
	}
//--------------------------------------------------------

	public String obtieneFecha(){
		String sFecha = "";
		String sTipo="";
		Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";

		if (ClassPropertiesLegis.getInstance().sFechaProceso.length() == 10 ){
			sFecha = ClassPropertiesLegis.getInstance().sFechaProceso;
			sTipo="Parametro";
		}
		else{
			try{

				sqlQuery ="select TO_CHAR(SYSDATE-8,'DD/MM/YYYY') laFecha from dual";

				conn = pool.ds.getConnection();
				stLeer = conn.createStatement();
				rsLeer = stLeer.executeQuery(sqlQuery);
				rsLeer.next();
				sTipo="Sistema";
				sFecha = rsLeer.getString("laFecha");
				//sFecha = "2015-07-30";
			}catch(SQLException sqlexception){
				UtilMensaje.getInstance().mensaje("Error: obtieneFecha." + sqlexception.getMessage()  + "QUery: " + sqlQuery);
				return "error";
			}
			catch(Exception exception){
				UtilMensaje.getInstance().mensaje("Error: obtieneFecha." + exception.getMessage() + "QUery: " + sqlQuery);
				exception.printStackTrace();
				return "error";
			}finally{
				try {
	    			conn.close();
					rsLeer.close();
					stLeer.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		UtilMensaje.getInstance().mensaje("Fecha de Proceso (" + sFecha + ") obtenido por " + sTipo);
		return sFecha;
	}


	/*
     * @autor Jairo Vega
     * @version 29/12/2016
     */
    public void prepararNovedadesYactuaciones(String sProcesoEnEjecucion) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
        int counter=0;

        //La tabla LEGIS_NOVEDADES se llena por:
        //1-un trigger de procesos_clientes
        //2-En la funcion de prepararActuaciones

        try {



            //Si CHANGE_TYPE = 'U', se le debe enviar a legis el ACTUACION_ID_LEGIS, para que actualice datos.
            sqlQuery = " select DISTINCT LN.NOVEDAD_ID, LN.PROCESO_ID, LN.NOVEDAD, LN.DESPACHO_ID "
            + " , LN.OFICINA_ID, LN.CASO, LN.RADICADO "
            + ", LN.ACTUACION_PROCESAL_ID, NVL(VA.ACTUACION_ID_LEGIS,'0') AS ACTUACION_ID_LEGIS "
            + " , nvl(CHANGE_TYPE,'I') as CHANGE_TYPE "
            + " , LN.FECHA_CREACION "
            + " , AP.ACTUACION_ID, N.NOTIFICACION_NOMBRE "
            + " , LN.TITULO "
            + " , VM.TP_PROCESO_ID "
           // + " , OBSERVACIONES "
            + " from LEGIS_NOVEDADES LN, V_ACTUACION_LEGIS_ULTIMA VA, ACTUACIONES_PROCESALES AP, NOTIFICACIONES N, VM_PROCESOS VM "
            + " where LN.estado LIKE 'PENDIENTE%' "
            + " and LN.FECHA_CREACION >= sysdate - 8 "
            //+ " AND LN.NOVEDAD = 'ACTUACION' "
            //+ " and novedad = 'RETIRO' "
            + " AND LN.ACTUACION_PROCESAL_ID = VA.ACTUACION_PROCESAL_ID (+) "
            + " AND LN.ACTUACION_PROCESAL_ID = AP.ACTUACION_PROCESAL_ID "
            + " AND AP.NOTIFICACION_ID = N.NOTIFICACION_ID "
            + " AND VM.PROCESO_ID = LN.PROCESO_ID "
            + " AND VM.PROCESO_ID = AP.PROCESO_ID "
            //+ " AND LN.PROCESO_ID IN (6401484, 6491439, 4935004, 4877376, 4855541) "
        	//+ " and ln.novedad_id in(2516797,2516672,2477685,2477502,2383922, 2380318)"
            // + " and ln.NOVEDAD_ID in (1923877) "
            //+ " AND LN.ACTUACION_PROCESAL_ID IN (25653992,	25654215,	25654560,	25653882,	25653730,	25653731,	25654997,	25654049,	25654998,	25654156,	25654148,	25654151,	25654999,	25653688,	25653708,	25653719,	25653767,	25653746,	25654183,	25654147,	25654210,	25653947,	25653948,	25653958,	25653834,	25653962,	25653505,	25653724,	25653452,	25653700,	25653953,	25653747,	25654033,	25653815,	25654078,	25654077,	25653968,	25653804,	25654146,	25653629,	25653888,	25653965,	25653823,	25654096,	25653964,	25653465,	25653917,	25653857,	25653720,	25654168,	25654184,	25653769,	25654164,	25653598,	25653597,	25653830,	25653825,	25653868,	25653870,	25654212,	25653682,	25653838,	25653928,	25653980,	25654149,	25653738,	25654035,	25653514,	25653863,	25653923,	25653925,	25653703,	25654120,	25653643,	25653600,	25653725,	25653644,	25653667,	25654097,	25653726,	25653973,	25654264,	25653970,	25653972,	25654008,	25653971,	25653836,	25653690,	25654098,	25654171,	25653772,	25653861,	25653860,	25654138,	25654080,	25653828,	25653594,	25653637,	25661105,	25653907,	25653649,	25653602,	25653657,	25653664,	25653663,	25653603,	25654174,	25654213,	25653646,	25654173,	25654172,	25653591,	25653645,	25654406,	25653639,	25653623,	25653634,	25653661,	25653650,	25653585,	25653809,	25653763,	25653993,	25653994,	25653877,	25653694,	25653764,	25653908,	25653909,	25654050,	25653588,	25653765,	25653876,	25653792,	25653874,	25653589,	25654082,	25653910,	25653875,	25653790,	25653735,	25653586,	25653487,	25653488,	25653810,	25653622,	25654045,	25653811,	25653812,	25654104,	25653743,	25653620,	25653483,	25653482,	25653592,	25654018,	25653866,	25654116,	25654099,	25653903,	25653610,	25654595,	25654596,	25654594,	25654606,	25654607,	25655002,	25655003,	25655005,	25653789,	25653755,	25653732,	25653443,	25654991,	25653728,	25653946,	25653444,	25653714,	25653704,	25654034,	25653712,	25653748,	25654158,	25653468,	25653593,	25653739,	25653821,	25653740,	25655062,	25655063,	25653758,	25653756,	25653757,	25654599,	25653454,	25654163,	25655018,	25655048,	25654575,	25654576,	25654577,	25653737,	25654001,	25654002,	25654003,	25654004,	25653493,	25653458,	25653478,	25653479,	25654587,	25655049,	25654175,	25654729,	25653635,	25654992,	25654993,	25654994,	25654995,	25654996,	25655000,	25655001,	25655006,	25654839,	25654891,	25654578,	25654005,	25654724,	25654726,	25654725,	25654727,	25654728,	25653718,	25654628,	25654968,	25654561,	25654562,	25654563,	25654564,	25654565,	25654653,	25655059,	25655060,	25655061,	25654605,	25654604,	25654734,	25654735,	25654737,	25654736,	25654738,	25654740,	25653893,	25654739,	25655028,	25655015,	25654590,	25654598,	25655058,	25654683,	25654669,	25654730,	25654731,	25655010,	25655027,	25654678,	25655033,	25655035,	25653736,	25654030,	25654031,	25654032,	25653437,	25653745,	25653751,	25653752,	25653753,	25653754,	25654751,	25654752,	25654753,	25654754,	25654755,	25655009,	25655008,	25655007,	25654610,	25654613,	25655032,	25655037,	25655038,	25655039,	25655040,	25655041,	25655042,	25654677,	25654593,	25654742,	25655036,	25654016,	25655016,	25655017,	25654637,	25654638,	25654639,	25655022,	25654760,	25654756,	25654757,	25654758,	25654759,	25654761,	25654762,	25654658,	25654659,	25654660,	25654661,	25654662,	25654663,	25654664,	25654665,	25654591,	25655025,	25655024,	25654679,	25654680,	25654681,	25654682,	25654581,	25654582,	25654588,	25654589,	25654615,	25654616,	25654623,	25654624,	25654629,	25654630,	25654791,	25654792,	25654631,	25654793,	25654632,	25654795,	25654797,	25654633,	25654798,	25654799,	25654650,	25654651,	25654800,	25654801,	25654654,	25654803,	25654666,	25654804,	25654806,	25654805,	25654808,	25654810,	25654672,	25654812,	25654674,	25654815,	25654817,	25654818,	25654820,	25654822,	25654824,	25654826,	25654829,	25654831,	25654833,	25654836,	25654837,	25654838,	25654840,	25654841,	25654842,	25654843,	25654845,	25654847,	25654850,	25654848,	25654851,	25654852,	25654853,	25654854,	25654855,	25654857,	25654856,	25654858,	25654859,	25654861,	25654863,	25654865,	25654864,	25654866,	25654869,	25654873,	25654871,	25654874,	25654876,	25654878,	25654879,	25654881,	25654882,	25654884,	25654888,	25654885,	25654887,	25654889,	25654890,	25654893,	25654895,	25654894,	25654898,	25654897,	25654899,	25654900,	25654901,	25654904,	25654902,	25654905,	25654903,	25654907,	25654906,	25654909,	25654908,	25654910,	25654912,	25654911,	25654915,	25654914,	25654916,	25654917,	25654393,	25654918,	25654919,	25654921,	25654922,	25654920,	25654925,	25654924,	25654923,	25654929,	25654928,	25654926,	25654927,	25654931,	25654930,	25654935,	25654932,	25654934,	25654933,	25654937,	25654936,	25654941,	25654939,	25654938,	25654942,	25654944,	25654943,	25654945,	25654946,	25654948,	25654947,	25654950,	25654949,	25654951,	25654953,	25654952,	25654956,	25654954,	25654955,	25654959,	25654957,	25654961,	25654960,	25654958,	25654963,	25654962,	25654965,	25654964,	25654967,	25654966,	25654972,	25654970,	25654969,	25654973,	25654971,	25654974,	25654975,	25654976,	25654977,	25654978,	25654981,	25654980,	25654979,	25654982,	25654984,	25654983,	25654986,	25654985,	25654988,	25654987,	25654989,	25654990,	25654597,	25655064,	25655054,	25654570,	25654568,	25654687,	25654684,	25655057,	25655055,	25655053,	25654732,	25654733,	25654090,	25654089 ) "
            + " AND LN.OFICINA_ID = VA.OFICINA_ID (+) "
            + " AND LN.PROCESO_ID = VA.PROCESO_ID (+) "
            + " AND LN.CASO = VA.CASO (+) "
            + " AND LN.CLIENTE_ID = VA.CLIENTE_ID(+) "
            + "  and exists ("
            + "     SELECT   * "
            + "     FROM procesos P "
            + "     WHERE P.proceso_id  = ln.proceso_id "
            + "  )"
            + " ORDER BY LN.NOVEDAD_ID ";


            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);

			UtilMensaje.getInstance().mensaje(sqlQuery);
            while (rsLeer.next()) {
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");
            	UtilMensaje.getInstance().mensaje("NOVEDAD_ID " + rsLeer.getString("NOVEDAD_ID") + " PROCESO_ID " + rsLeer.getString("PROCESO_ID") + " DESPACHO_ID " + rsLeer.getString("DESPACHO_ID") + " " + rsLeer.getString("NOVEDAD") + " ACTUACION_ID_LEGIS " +  rsLeer.getString("ACTUACION_ID_LEGIS") + " CHANGE_TYPE " + rsLeer.getString("CHANGE_TYPE"));
            	//--Inicio juzgado
            	String sDespachoId="";
            	String sJuzgado = "";
            	if (rsLeer.getString("NOVEDAD").equals("INGRESO") || rsLeer.getString("NOVEDAD").equals("RETIRO")){
                	sDespachoId = getDespachoIdVm(rsLeer.getString("PROCESO_ID")); // Busca en vm_procesos
            		if (sDespachoId.equals(""))
            			sDespachoId=getDespachoIdInstancia(rsLeer.getString("PROCESO_ID"));
            	}else if (rsLeer.getString("NOVEDAD").equals("ACTUACION")){
            		sDespachoId=rsLeer.getString("DESPACHO_ID");
            	}else{
            		UtilMensaje.getInstance().mensaje("Error en el tipo de novedad");
            	}
        		//sJuzgado = getJuzgadoLegis(rsLeer.getString("NOVEDAD"), sDespachoId);
        		sJuzgado = getJuzgado(sDespachoId);
            	//--Fin juzgado

				DatosActuacionInsert regAct = new DatosActuacionInsert();
				regAct.setOficina(rsLeer.getString("OFICINA_ID"));
				regAct.setCaso(rsLeer.getString("CASO"));
				regAct.setRadicado(rsLeer.getString("RADICADO"));
				regAct.setTpProcesoId(rsLeer.getString("TP_PROCESO_ID"));
				//regAct.setJuzgado(new Integer(sJuzgado).intValue());
				regAct.setJuzgado(sJuzgado);


				String sValidaChangeType = rsLeer.getString("CHANGE_TYPE");
				if (rsLeer.getString("CHANGE_TYPE").equals("U")){
					if(rsLeer.getString("ACTUACION_ID_LEGIS").equals("0")){
						sValidaChangeType = "La novedad " + rsLeer.getString("NOVEDAD_ID") + " es de actualizaci�n U, y no tiene ACTUACION_ID_LEGIS, se reemplazara por I ";
						UtilMensaje.getInstance().mensaje(sValidaChangeType);
					}else{
						UtilMensaje.getInstance().mensaje("ACTUACION_ID_LEGIS " + rsLeer.getString("ACTUACION_ID_LEGIS"));
					}

				}


				actualizarEstadoPreparaNovedad(rsLeer.getString("NOVEDAD_ID"), regAct, sDespachoId, sValidaChangeType, rsLeer.getString("ACTUACION_ID_LEGIS"), rsLeer.getString("ACTUACION_PROCESAL_ID"));

        		counter += 1;
                //conn.commit();

            }
        }
        catch (SQLException sqlexception) {
            this.error = "prepararNovedadesYactuaciones " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
        }
        catch (Exception exception) {
            this.error = "prepararNovedadesYactuaciones " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        UtilMensaje.getInstance().mensaje("Registros procesados: " + counter);
    }




    /*
     * @autor Jairo Vega
     * @return void
     * @version 29/12/2016
     */



	private  ArrayList<String> getActuacionHomologadaNewTituloConciliacion(String sActuacionProcesalId) {

		String sqlQuery = "";
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

	    ArrayList<String> lista = new ArrayList<String>();

		try {
            sqlQuery = " SELECT DISTINCT LA.ACTUACION_ID_HOMOLOGA, LN.DESCRIPCION "
            		+ "  FROM ACTUACIONES_PROCESALES AP "
            		+ "     , NOTIFICACIONES N "
            		+ "     , LEGIS_ACTUACION LA "
            		+ "     , LEGIS_NOVEDADES LN "
            		+ " WHERE AP.ACTUACION_ID = LA.ACTUACION_ID "
            		+ "   AND AP.NOTIFICACION_ID = N.NOTIFICACION_ID "
            		+ "   AND LN.ACTUACION_PROCESAL_ID = AP.ACTUACION_PROCESAL_ID"
            		+ "   AND LN.estado LIKE 'PENDIENTE%' "
            		+ "   AND LA.TP_PROCESO_ID = 338 "
            		+ "   AND AP.ACTUACION_PROCESAL_ID = " + sActuacionProcesalId;

            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            if (rsLeer.next()) {
            	lista.add(rsLeer.getString("ACTUACION_ID_HOMOLOGA"));
            	String titulo = new cadena().obtenerPalabras(rsLeer.getString("DESCRIPCION"),11) ;
            	lista.add(rsLeer.getString("ACTUACION_ID_HOMOLOGA")+" - "+titulo) ;
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
		return lista;
   	}

	private  ArrayList<String> getActuacionHomologadaNewTituloManualConciliacion(String sActuacionProcesalId) {

		String sqlQuery = "";
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

	    ArrayList<String> lista = new ArrayList<String>();

		try {
			sqlQuery = " SELECT DISTINCT LA.ACTUACION_ID_HOMOLOGA, LN.DESCRIPCION "
            		+ "  FROM ACTUACIONES_PROCESALES AP "
            		+ "     , NOTIFICACIONES N "
            		+ "     , LEGIS_ACTUACION LA "
            		+ "     , LEGIS_NOVEDADES LN "
            		+ " WHERE LN.ACTUACION_ID_HOMOLOGA = LA.ACTUACION_ID_HOMOLOGA "
            		+ "   AND LN.ACTUACION_PROCESAL_ID = AP.ACTUACION_PROCESAL_ID "
            		+ "   AND AP.NOTIFICACION_ID = N.NOTIFICACION_ID "
            		+ "   AND LN.estado LIKE 'PENDIENTE%' "
            		+ "   AND LA.TP_PROCESO_ID = 338 "
            		+ "   AND AP.ACTUACION_PROCESAL_ID = " + sActuacionProcesalId;

            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

			if (rsLeer.next()) {
            	lista.add(rsLeer.getString("ACTUACION_ID_HOMOLOGA"));
            	String titulo = new cadena().obtenerPalabras(rsLeer.getString("DESCRIPCION"),11) ;
            	lista.add(rsLeer.getString("ACTUACION_ID_HOMOLOGA")+" - "+titulo) ;
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
		return lista;
   	}


	private  ArrayList<String> getActuacionHomologadaNewTitulo(String sActuacionProcesalId) {

		String sqlQuery = "";
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

	    ArrayList<String> lista = new ArrayList<String>();

		try {
            sqlQuery = "SELECT DISTINCT LA.ACTUACION_ID_HOMOLOGA, LN.DESCRIPCION "
            		+ "  FROM ACTUACIONES_PROCESALES AP "
            		+ "     , NOTIFICACIONES N "
            		+ "     , LEGIS_ACTUACION LA "
            		+ "     , LEGIS_NOVEDADES LN "
            		+ " WHERE AP.ACTUACION_ID = LA.ACTUACION_ID "
            		+ "   AND AP.NOTIFICACION_ID = N.NOTIFICACION_ID "
            		+ "   AND LN.ACTUACION_PROCESAL_ID = AP.ACTUACION_PROCESAL_ID"
            		+ "   AND LN.estado LIKE 'PENDIENTE%' "
            		+ "   AND AP.ACTUACION_PROCESAL_ID = " + sActuacionProcesalId;

            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            if (rsLeer.next()) {
            	lista.add(rsLeer.getString("ACTUACION_ID_HOMOLOGA"));
            	String titulo = new cadena().obtenerPalabras(rsLeer.getString("DESCRIPCION"),11) ;
            	lista.add(rsLeer.getString("ACTUACION_ID_HOMOLOGA")+" - "+titulo) ;
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
		return lista;
   	}

	private  ArrayList<String> getActuacionHomologadaNewTituloManual(String sActuacionProcesalId) {

		String sqlQuery = "";
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

	    ArrayList<String> lista = new ArrayList<String>();

		try {
			sqlQuery = " SELECT DISTINCT LA.ACTUACION_ID_HOMOLOGA, LN.DESCRIPCION "
            		+ "  FROM ACTUACIONES_PROCESALES AP "
            		+ "     , NOTIFICACIONES N "
            		+ "     , LEGIS_ACTUACION LA "
            		+ "     , LEGIS_NOVEDADES LN "
            		+ " WHERE LN.ACTUACION_ID_HOMOLOGA = LA.ACTUACION_ID_HOMOLOGA "
            		+ "   AND LN.ACTUACION_PROCESAL_ID = AP.ACTUACION_PROCESAL_ID "
            		+ "   AND AP.NOTIFICACION_ID = N.NOTIFICACION_ID "
            		+ "   AND LN.estado LIKE 'PENDIENTE%' "
            		+ "   AND AP.ACTUACION_PROCESAL_ID = " + sActuacionProcesalId;

            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

			if (rsLeer.next()) {
            	lista.add(rsLeer.getString("ACTUACION_ID_HOMOLOGA"));
            	String titulo = new cadena().obtenerPalabras(rsLeer.getString("DESCRIPCION"),11) ;
            	lista.add(rsLeer.getString("ACTUACION_ID_HOMOLOGA")+" - "+titulo) ;
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
		return lista;
   	}

	/*
     * @autor Jairo Vega
     * @return void
     * @version 29/12/2016
     */
    private String getJuzgadoLegis(String sNovedad, String sDespachoId){
		String sqlQuery = "";
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

		String sDato = "1"; //Si no existe despacho, se env�a despacho legis 1

		if (sDespachoId.equals(""))
			return sDato;

		try {
            sqlQuery = "select ld.DESPACHO_ID_LEGIS "
            + " from legis_despachos ld "
            + " where ld.DESPACHO_ID = " + sDespachoId;

            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            if (rsLeer.next()) {
            	sDato = rsLeer.getString("DESPACHO_ID_LEGIS");
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error getJuzgadoLegis: " + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		UtilMensaje.getInstance().mensaje("DESPACHO_ID_LEGIS " + sDato);
		return sDato;

    }

    private String getJuzgado(String sDespachoId){
		String sqlQuery = "";
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

		String sDato = "0"; //Si no existe despacho, se env�a despacho legis 1

		if (sDespachoId.equals(""))
			return sDato;

		try {
            sqlQuery = "select d.DESPACHO_NOMBRE || ' - ' || L.LOCALIDAD_NOMBRE as DESPACHO "
            		+ "  from despachos d, localidades l "
            		+ " where D.LOCALIDAD_ID = l.LOCALIDAD_ID "
            		+ "   and d.despacho_id =  " + sDespachoId;

            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            if (rsLeer.next()) {
            	sDato = rsLeer.getString("DESPACHO");
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error getJuzgado: " + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//UtilMensaje.getInstance().mensaje("sDespachoId " + sDato);
		return sDato;

    }

    private String getDespachoIdVm(String sProcesoId){
        String sqlQuery = "";
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

		String sDato = "";

		try {
            sqlQuery = "select DESPACHO_ID from ("
            + " select vm.DESPACHO_ID, ap.ACTUACION_FECHA, ap.ACTUACION_PROCESAL_ID"
            + " from vm_procesos vm, actuaciones_procesales ap"
            + " where vm.proceso_id = " + sProcesoId
            + " and vm.PROCESO_ID = ap.PROCESO_ID "
            + " and vm.DESPACHO_ID = ap.DESPACHO_ID "
            + " union "
            + " select vm.DESPACHO_ID, ap.ACTUACION_FECHA, ap.ACTUACION_PROCESAL_ID"
            + " from duplica.vm_procesos vm, duplica.actuaciones_procesales ap"
            + " where vm.proceso_id = " + sProcesoId
            + " and vm.PROCESO_ID = ap.PROCESO_ID "
            + " and vm.DESPACHO_ID = ap.DESPACHO_ID "
            + " union "
            + " select vm.DESPACHO_ID, ap.ACTUACION_FECHA, ap.ACTUACION_PROCESAL_ID "
            + "   from vm_procesos vm, actuaciones_procesales ap "
            + "  where vm.PROCESO_ID = ap.PROCESO_ID "
            + "    and vm.DESPACHO_ID = ap.DESPACHO_ID "
            + "    and vm.proceso_id in ( "
            + "    select destino "
            + "      from PROCESAR_DUPLICADOS "
            + "     where origen =  " + sProcesoId
            + "    ) "
            + " ) "
            + " order by ACTUACION_FECHA desc, ACTUACION_PROCESAL_ID desc ";

            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            if (rsLeer.next()) {
            	sDato = rsLeer.getString("DESPACHO_ID");
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error getJuzgadoVm: " + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		//UtilMensaje.getInstance().mensaje("getJuzgadoVm: " + sDato);
		return sDato;

    }

    private String getDespachoIdInstancia(String sProcesoId){
        String sqlQuery = "";
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

		String sDato = "";

		try {

            sqlQuery = "select DESPACHO_ID "
           	+ " from procesos_instancias "
            + " where proceso_id = " + sProcesoId
            + " and DESPACHO_ID is not null"
            + " union "
            + " select DESPACHO_ID "
           	+ " from duplica.procesos_instancias "
            + " where proceso_id = " + sProcesoId
            + " and DESPACHO_ID is not null"
            + " union "
            + " SELECT DESPACHO_ID "
            + "   FROM procesos_instancias "
            + "  WHERE DESPACHO_ID IS NOT NULL "
            + "    and proceso_id in ( "
            + "      select destino  "
            + "        from PROCESAR_DUPLICADOS "
            + "       where origen = " + sProcesoId
            + "    )"
            ;

            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            if (rsLeer.next()) {
            	sDato = rsLeer.getString("DESPACHO_ID");
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error getDatosProceso: " + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		//UtilMensaje.getInstance().mensaje("getJuzgadoInstancia: " + sDato);
		return sDato;

    }


    /*
     * @autor Jairo Vega
     * @version 02/01/2017
    //La tabla LEGIS_NOVEDADES se llena por:
    //1-un trigger de procesos_clientes
    //2-En la funcion de prepararActuaciones
     */
    public void seleccionarActuaciones(String sProcesoEnEjecucion, String fechaProceso, String sAmPm) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
        int counter=0;

    	String[] registroF = new String[10];
    	registroF = getFIniFFin(sAmPm) ;

    	String fechaProcesoIni = registroF[0];
    	String fechaProcesoFin = registroF[1];

        try {

            sqlQuery =
            		" select DISTINCT ACTUACION_PROCESAL_ID, PROCESO_ID , NOVEDAD, CHANGE_TYPE "
            	//	+ " , TP_AP,  TP_DAP, F_AP,  F_DAP  "
            		+ " , TITULO, DESCRIPCION, FECHA, CLIENTE_ID, DESPACHO_ID "
            		+ " , OFICINA_ID_LEGIS, PROCESO_ID_LEGIS, RADICADO  "
            		+ " , ACTUACION_ID "
            		+ "  FROM(  "
					+ " SELECT AP.ACTUACION_PROCESAL_ID AS ACTUACION_PROCESAL_ID "
					+ " , LG.PROCESO_ID  "
                    + " , 'ACTUACION' AS NOVEDAD, nvl(VA.CHANGE_TYPE,'I') as CHANGE_TYPE "
               //     + " , 'A' AS TP_AP, 'A' AS TP_DAP , null as F_AP , null as F_DAP "
                    + " , REPLACE (A.ACTUACION_NOMBRE || ' - ' || N.NOTIFICACION_NOMBRE, '-- - -- -',NULL) AS TITULO " //Para fomag se cambia la forma de armar el t�tulo en preparar
                    //     + " , REPLACE (A.ACTUACION_ID || ' - ' || A.ACTUACION_NOMBRE || ' - ' || N.NOTIFICACION_NOMBRE, '-- - -- -',NULL) AS TITULO " //Para fomag se cambia la forma de armar el t�tulo en preparar
                    + " , DAP.DATO_VALOR AS DESCRIPCION  "
                    + " , to_char(AP.ACTUACION_FECHA,'yyyy-mm-dd') AS FECHA "
                    + " , 23957 as CLIENTE_ID  "
                    + " , AP.DESPACHO_ID AS DESPACHO_ID "
                    + " , LG.OFICINA_ID_LEGIS, LG.PROCESO_ID_LEGIS, LG.RADICADO "
                    + " , A.ACTUACION_ID "
                    + "  FROM ACTUACIONES A   "
                    + " , ACTUACIONES_PROCESALES AP "
                    + " , DATOS_ACTUACION_PROCESAL DAP "
                    + " , NOTIFICACIONES N "
                    + " , LEGIS_PROCESOS LG"
                    + " , V_LEGIS_ACTUACION VA"
                    + "  WHERE A.ACTUACION_ID = AP.ACTUACION_ID "
                    + "  AND AP.NOTIFICACION_ID = N.NOTIFICACION_ID "
                    + "  AND AP.ACTUACION_PROCESAL_ID = DAP.ACTUACION_PROCESAL_ID "
                    + "  AND AP.ACTUACION_PROCESAL_ID = AP.ACTUACION_PROCESAL_ID + 0"
                    + "  AND LG.PROCESO_ID = AP.PROCESO_ID "
                    + "  AND lg.proceso_id = lg.proceso_id + 0 "
                    + "  AND DAP.DATO_NOMBRE = 'RESUELVE' "
                    //+ "  and AP.ACTUACION_FECHA >= to_date('2021-04-01','yyyy-mm-dd')"
                    + "  AND AP.ACTUACION_PROCESAL_ID = VA.ACTUACION_PROCESAL_ID (+)";
                    //+ "  and OFICINA_ID_LEGIS in('GENERALESYVIDA', 'SED', 'ALLIANZSEGUROS' )";
                   // + "  AND AP.ACTUACION_PROCESAL_ID in (18497818, 18145978, 17782666)"
                    if (sAmPm.equals("AM") || sAmPm.equals("PM")){
                    	sqlQuery += " and VA.CHANGED_TIME between TO_DATE('" + fechaProcesoIni + "','DD/MM/YYYY HH24:MI') and TO_DATE('" + fechaProcesoFin + "','DD/MM/YYYY HH24:MI') ";
                    }else {
                    	sqlQuery += " and VA.CHANGED_TIME (+) between TO_DATE('" + fechaProcesoIni + "','DD/MM/YYYY HH24:MI') and TO_DATE('" + fechaProcesoFin + "','DD/MM/YYYY HH24:MI') ";
                    }
                    sqlQuery += " ) ACTUA ";
            if (sAmPm.equals("AM") || sAmPm.equals("PM")){
            	sqlQuery += "  WHERE NOT EXISTS ( "
	            		+ "  SELECT 1   "
	            		+ "  FROM LEGIS_NOVEDADES LN "
	            		+ "  WHERE LN.ACTUACION_PROCESAL_ID = ACTUA.ACTUACION_PROCESAL_ID "
	            		+ "  and ln.DESPACHO_ID= ACTUA.DESPACHO_ID  "
	            		+ "  and LN.OFICINA_ID = ACTUA.OFICINA_ID_LEGIS "
	            		+ "  and LN.PROCESO_ID = ACTUA.PROCESO_ID "
	            		+ "  and LN.CASO = ACTUA.PROCESO_ID_LEGIS "
	            		+ "  and LN.CLIENTE_ID = ACTUA.CLIENTE_ID "
	            		+ "  and LN.RADICADO = ACTUA.RADICADO "
	            		+ "  and ln.TITULO = ACTUA.TITULO "
	            		+ "  and ln.DESCRIPCION= ACTUA.DESCRIPCION "
	            		+ "  and trunc(ln.FECHA) = TO_date(ACTUA.FECHA,'YYYY-MM-DD') "
            		+ "  )"
            		+ " ORDER BY ACTUACION_PROCESAL_ID DESC ";
            }else{
            	sqlQuery += "  WHERE NOT EXISTS ( "
	            		+ "  SELECT 1   "
	            		+ "  FROM LEGIS_NOVEDADES LN "
	            		+ "  WHERE LN.ACTUACION_PROCESAL_ID = ACTUA.ACTUACION_PROCESAL_ID "
	            		+ "  and ln.DESPACHO_ID= ACTUA.DESPACHO_ID  "
	            		+ "  and LN.OFICINA_ID = ACTUA.OFICINA_ID_LEGIS "
	            		+ "  and LN.PROCESO_ID = ACTUA.PROCESO_ID "
	            		+ "  and LN.CASO = ACTUA.PROCESO_ID_LEGIS "
	            		+ "  and LN.CLIENTE_ID = ACTUA.CLIENTE_ID "
	            		+ "  and LN.RADICADO = ACTUA.RADICADO "
	            		//+ "  and ln.TITULO = ACTUA.TITULO "
	            		//+ "  and ln.DESCRIPCION= ACTUA.DESCRIPCION "
	            		//+ "  and trunc(ln.FECHA) = TO_date(ACTUA.FECHA,'YYYY-MM-DD') "
            		+ "  )"
            		//+ "and rownum < 3"
            		+ " ORDER BY ACTUACION_PROCESAL_ID DESC ";
            }


			UtilMensaje.getInstance().mensaje(sqlQuery);

			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);

            while (rsLeer.next()) {
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");
            	UtilMensaje.getInstance().mensaje("ACTUACION_PROCESAL_ID " + rsLeer.getString("ACTUACION_PROCESAL_ID") + " PROCESO_ID " + rsLeer.getString("PROCESO_ID") + " CHANGE_TYPE " + rsLeer.getString("CHANGE_TYPE"));

            	String descripcion = new cadena().quitarEspeciales(rsLeer.getString("DESCRIPCION"));

            	sqlQuery = "insert into LEGIS_NOVEDADES ("
            	+ " NOVEDAD_ID, PROCESO_ID, CLIENTE_ID, NOVEDAD, TITULO, DESCRIPCION, FECHA, DESPACHO_ID, ACTUACION_PROCESAL_ID, OFICINA_ID, CASO, RADICADO, ESTADO, ACTUACION_ID, CHANGE_TYPE) "
                + " values (SEC_LEGIS_NOVEDAD.NEXTVAL "
            	+ " , " + rsLeer.getString("PROCESO_ID")
            	+ " , " + rsLeer.getString("CLIENTE_ID")
            	+ " , '" + rsLeer.getString("NOVEDAD") + "'"
            	+ " , '" + rsLeer.getString("TITULO") + "'"
            	+ " , '" + descripcion + "'"
            	+ " , TO_DATE('" + rsLeer.getString("FECHA") + "','YYYY-MM-DD')"
            	+ " , " + rsLeer.getString("DESPACHO_ID")
            	+ " , " + rsLeer.getString("ACTUACION_PROCESAL_ID")
            	+ " , '" + rsLeer.getString("OFICINA_ID_LEGIS") + "'"
            	+ " , '" + rsLeer.getString("PROCESO_ID_LEGIS") + "'"
            	+ " , '" + rsLeer.getString("RADICADO") + "'"
            	+ " , 'PENDIENTE' "
            	+ " , " + rsLeer.getString("ACTUACION_ID")
            	+ " , '" + rsLeer.getString("CHANGE_TYPE") + "'"
            	+ ")";

               	ejecutarQuery("seleccionarActuaciones", "LEGIS_NOVEDADES", sqlQuery);

            	counter += 1;
                ////conn.commit();

            }
        }
        catch (SQLException sqlexception) {
            this.error = "prepararNovedades " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
        }
        catch (Exception exception) {
            this.error = "prepararNovedades " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        UtilMensaje.getInstance().mensaje("Registros procesados: " + counter);
    }


    public String[] getFIniFFin(String sAmPm){
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String[] registro = new String[10];
        registro[0] = ".";
        registro[1] = ".";


		String sqlQuery = "select "
		+ " to_char(to_date(to_char (trunc(sysdate-1),'dd/mm/yyyy') || '19:00', 'dd/mm/yyyy HH24:MI'), 'dd/mm/yyyy HH24:MI') f_ini_am_19 "
		+ " , to_char(to_date(to_char (trunc(sysdate),'dd/mm/yyyy') || '08:00', 'dd/mm/yyyy HH24:MI'), 'dd/mm/yyyy HH24:MI') f_fin_am_08 "
		+ " , to_char(to_date(to_char (trunc(sysdate),'dd/mm/yyyy') || '08:01', 'dd/mm/yyyy HH24:MI'), 'dd/mm/yyyy HH24:MI') f_ini_pm_08 "
		+ " , to_char(to_date(to_char (trunc(sysdate),'dd/mm/yyyy') || '18:59', 'dd/mm/yyyy HH24:MI'), 'dd/mm/yyyy HH24:MI') f_fin_pm_19 "
		+ " , to_char(to_date(to_char (trunc(sysdate-7),'dd/mm/yyyy') || '19:00', 'dd/mm/yyyy HH24:MI'), 'dd/mm/yyyy HH24:MI') f_ini_ot_19 "
		+ " , to_char(to_date(to_char (trunc(sysdate-1),'dd/mm/yyyy') || '18:59', 'dd/mm/yyyy HH24:MI'), 'dd/mm/yyyy HH24:MI') f_fin_ot_19 "
		+ " from dual ";
		try {
			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			if (rsLeer.next()){
				if (sAmPm.equals("AM")){
	                registro[0] = rsLeer.getString(1);
	                registro[1] = rsLeer.getString(2);
				}else if (sAmPm.equals("PM")){
	                registro[0] = rsLeer.getString(3);
	                registro[1] = rsLeer.getString(4);
				}else{
	                registro[0] = rsLeer.getString(5);
	                registro[1] = rsLeer.getString(6);
				}
    			//UtilMensaje.getInstance().mensaje("Estado " + rs.getString(1) + " notificacionIdPws " + rs.getString(2));
			}
			/*else
    			UtilMensaje.getInstance().mensaje("No existe coindiencia en postress");
    		*/
		} catch (SQLException e) {
			UtilMensaje.getInstance().mensaje("getFIniFFin SQLException: " + e.getMessage() + ". " + sqlQuery);
		}

		return registro;

    }

    public void seleccionarActuacionesAnt(String fechaProceso) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
        int counter=0;

        //La tabla LEGIS_NOVEDADES se llena por:
        //1-un trigger de procesos_clientes
        //2-En la funcion de prepararActuaciones

        try {

            sqlQuery =
				"SELECT AP.ACTUACION_PROCESAL_ID"
				+ ", PC.PROCESO_ID AS PROCESO_ID"
				+ ", 'ACTUACION' AS NOVEDAD"
				+ ", A.ACTUACION_NOMBRE || ' - ' || E.ETAPA_NOMBRE || ' - ' || N.NOTIFICACION_NOMBRE AS TITULO"
				+ ", DAP.DATO_VALOR AS DESCRIPCION"
				+ ", to_char(AP.ACTUACION_FECHA,'yyyy-mm-dd') AS FECHA"
				+ ", LG.CLIENTE_ID"
				+ ", AP.DESPACHO_ID AS DESPACHO_ID"
				+ " FROM ACTUACIONES A"
				+ ", ACTUACIONES_PROCESALES AP"
				+ ", DATOS_ACTUACION_PROCESAL DAP"
				+ ", PROCESOS P"
				+ ", PROCESOS_CLIENTES PC"
				+ ", ETAPAS E "
				+ ", NOTIFICACIONES N"
    	    	+ ", LEGIS_CLIENTES LG "
				+ " WHERE A.ACTUACION_ID = AP.ACTUACION_ID "
				+ " AND AP.NOTIFICACION_ID = N.NOTIFICACION_ID "
				+ " AND AP.ACTUACION_PROCESAL_ID = DAP.ACTUACION_PROCESAL_ID "
				+ " AND P.PROCESO_ID = AP.PROCESO_ID "
				+ " AND P.PROCESO_ID = PC.PROCESO_ID "
				+ " AND DAP.DATO_NOMBRE = 'RESUELVE' "
				+ " AND AP.ETAPA_ID = E.ETAPA_ID "
    	    	+ " AND LG.CLIENTE_id = PC.CLIENTE_id "
    	    	+ " AND TRUNC(AP.ACTUACION_FECHA_CARGA) >= TRUNC(TO_DATE('"+fechaProceso+"','DD/MM/YYYY'))"
				+ " AND NOT EXISTS ("
				+ "   SELECT 1 "
				+ "   FROM LEGIS_NOVEDADES LN "
				+ "   WHERE LN.ACTUACION_PROCESAL_ID = AP.ACTUACION_PROCESAL_ID "
				+ ")";



			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);

			// UtilMensaje.getInstance().mensaje(sqlQuery);
            while (rsLeer.next()) {
            	UtilMensaje.getInstance().mensaje("ACTUACION_PROCESAL_ID " + rsLeer.getString("ACTUACION_PROCESAL_ID") + " PROCESO_ID " + rsLeer.getString("PROCESO_ID"));

            	sqlQuery = "insert into LEGIS_NOVEDADES (NOVEDAD_ID, PROCESO_ID, CLIENTE_ID, NOVEDAD, TITULO, DESCRIPCION, FECHA, DESPACHO_ID, ACTUACION_PROCESAL_ID) "
                + " values (SEC_LEGIS_NOVEDAD.NEXTVAL "
            	+ " , " + rsLeer.getString("PROCESO_ID")
            	+ " , " + rsLeer.getString("CLIENTE_ID")
            	+ " , '" + rsLeer.getString("NOVEDAD") + "'"
            	+ " , '" + rsLeer.getString("TITULO") + "'"
            	+ " , '" + rsLeer.getString("DESCRIPCION") + "'"
            	+ " , TO_DATE('" + rsLeer.getString("FECHA") + "','YYYY-MM-DD')"
            	+ " , " + rsLeer.getString("DESPACHO_ID")
            	+ " , " + rsLeer.getString("ACTUACION_PROCESAL_ID")
            	+ ")";

               	ejecutarQuery("seleccionarActuaciones", "LEGIS_NOVEDADES", sqlQuery);

            	counter += 1;
                //conn.commit();

            }
        }
        catch (SQLException sqlexception) {
            this.error = "prepararNovedades " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
        }
        catch (Exception exception) {
            this.error = "prepararNovedades " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        UtilMensaje.getInstance().mensaje("Registros procesados: " + counter);
    }

    /*
     * @autor C�sar Vega
     * @version 18/12/2016
     */
    public void transmitirNovedadesYactuaciones(String sProcesoEnEjecucion) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
        int counter=0;


        try {

            sqlQuery = " select NOVEDAD_ID, PROCESO_ID, NOVEDAD, nvl(CHANGE_TYPE,'I') as CHANGE_TYPE"
            + ", OFICINA_ID, CASO, TITULO, NVL(TITULO_HOMOLOGADO,'X') TITULO_HOMOLOGADO,  DESCRIPCION, to_char(FECHA,'yyyy-mm-dd') AS FECHA"
            + ", RADICADO, DESPACHO_ID, DESPACHO, ACTUACION_PROCESAL_ID, nvl(ACTUACION_ID_LEGIS,0) as ACTUACION_ID_LEGIS"
        	+ " from LEGIS_NOVEDADES "
        	+ " where "
        	+ " TITULO_HOMOLOGADO IS NOT NULL "
        	+ " AND estado = 'ENVIAR' "
        	+ " AND OFICINA_ID in ('FOMAG','DPJA') "
        	//+ " and novedad_id in(2516797,2516672,2477685,2477502,2383922, 2380318)";
        	+ " UNION"
        	+ " select NOVEDAD_ID, PROCESO_ID, NOVEDAD, nvl(CHANGE_TYPE,'I') as CHANGE_TYPE "
        	+ " , OFICINA_ID, CASO, TITULO, NVL(TITULO_HOMOLOGADO,'X') TITULO_HOMOLOGADO,  DESCRIPCION, to_char(FECHA,'yyyy-mm-dd') AS FECHA "
        	+ " , RADICADO, DESPACHO_ID, DESPACHO, ACTUACION_PROCESAL_ID, nvl(ACTUACION_ID_LEGIS,0) as ACTUACION_ID_LEGIS "
        	+ " from LEGIS_NOVEDADES "
        	+ " where estado = 'ENVIAR' "
        	+ " AND OFICINA_ID NOT in ('FOMAG','DPJA') "
        	+ " AND TRUNC (FECHA_CREACION) >= TRUNC (SYSDATE-8) "
        	+ " order by NOVEDAD_ID"
        	;

			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);

			UtilMensaje.getInstance().mensaje(sqlQuery);
            while (rsLeer.next()) {
                boolean conProcesoActivo = true;

				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");

				UtilMensaje.getInstance().mensaje("NOVEDAD_ID " + rsLeer.getString("NOVEDAD_ID") + " NOVEDAD " + rsLeer.getString("NOVEDAD") + " ACTUACION_PROCESAL_ID " + rsLeer.getString("ACTUACION_PROCESAL_ID") + " PROCESO_ID " + rsLeer.getString("PROCESO_ID") + " CHANGE_TYPE (" + rsLeer.getString("CHANGE_TYPE") + ")");

				DatosActuacionInsert regActInsert = new DatosActuacionInsert();

				if (rsLeer.getString("TITULO_HOMOLOGADO").equals("X") ){
					regActInsert.setTitulo(rsLeer.getString("TITULO"));
					} else {
					regActInsert.setTitulo(rsLeer.getString("TITULO_HOMOLOGADO"));
					}

				regActInsert.setOficina(rsLeer.getString("OFICINA_ID"));
				regActInsert.setCaso(rsLeer.getString("CASO"));
				//regActInsert.setTitulo(rsLeer.getString("TITULO"));
				//regActInsert.setTituloHomologado(rsLeer.getString("TITULO_HOMOLOGADO"));
				regActInsert.setDescripcion(rsLeer.getString("DESCRIPCION"));
				regActInsert.setFecha(rsLeer.getString("FECHA"));
				regActInsert.setRadicado(rsLeer.getString("RADICADO"));
				//regActInsert.setJuzgado(new Integer(rsLeer.getString("DESPACHO_ID_LEGIS")).intValue());

				String sJuzgado = "";
				if (rsLeer.getString("DESPACHO")==null)
					sJuzgado = getJuzgado(rsLeer.getString("DESPACHO_ID"));
				else
					sJuzgado = rsLeer.getString("DESPACHO");

				regActInsert.setJuzgado(sJuzgado);
				regActInsert.setCodigoExterno(rsLeer.getString("ACTUACION_PROCESAL_ID"));

            	if (rsLeer.getString("NOVEDAD").equals("ACTUACION")){
            		regActInsert.setVencimientoTerminos(ConnectOracle.getInstance().getVencimientoTerminos(rsLeer.getString("ACTUACION_PROCESAL_ID")));
    				//Este parche en este try es por error de mascara en el formato fecha
    				try{
    					regActInsert.setAudiencias(ConnectOracle.getInstance().getListaAudiencias(rsLeer.getString("ACTUACION_PROCESAL_ID"),0));
    				}catch (Exception exception) {
			            this.error = "transmitirNovedadesYactuaciones pasada 0" + exception.getMessage() + "   Query: " + sqlQuery;
			            UtilMensaje.getInstance().mensaje("Error: " + this.error);
			            exception.printStackTrace();
	    				try{
	    					regActInsert.setAudiencias(ConnectOracle.getInstance().getListaAudiencias(rsLeer.getString("ACTUACION_PROCESAL_ID"),1));
	    				}catch (Exception exception1) {
				            this.error = "transmitirNovedadesYactuaciones pasada 1" + exception.getMessage() + "   Query: " + sqlQuery;
				            UtilMensaje.getInstance().mensaje("Error: " + this.error);
				            exception.printStackTrace();
	    				}
    				}
            	}

                Boolean bFinaliza = false;
	            if (rsLeer.getString("NOVEDAD").equals("RETIRO")) {
		             bFinaliza = true;
		             conProcesoActivo = tieneProcesoActivo(rsLeer.getString("NOVEDAD_ID"));
		             //Si es una novedad de retiro, pero tiene proceso legis (caso) activo,
		             //no se env�a la novedad de retiro
		             if (conProcesoActivo) {
		            	 actualizarEstadoyObservacionesNovedad(rsLeer.getString("NOVEDAD_ID"), "SI EXISTE EN LEGIS_PROCESOS","NO SE TRANSMITE LA NOVEDAD DE RETIRO");
		            	 conProcesoActivo=false;
		             }

	            }

	            if (conProcesoActivo) {
		            regActInsert.setFinalizaServicio(bFinaliza);
		            int intentos = 0;
		            while (intentos < 2){
		            	if (rsLeer.getString("CHANGE_TYPE").equals("I")) //I- Insercion
		            		ConexionWebService.getInstance().transmitirActuacionRetornaActIdLegis(regActInsert);
		            	else{ //U- Actualizacion
		            		UtilMensaje.getInstance().mensaje("ACTUACION_ID_LEGIS " + rsLeer.getString("ACTUACION_ID_LEGIS"));
		    				DatosActuacionUpdate regActUpdate = new DatosActuacionUpdate(regActInsert, rsLeer.getString("ACTUACION_ID_LEGIS"));
		            		ConexionWebService.getInstance().transmitirActuacionEnviaActIdLegis(regActUpdate);
		            	}
			            if (ConexionWebService.getInstance().getCodError() == 401 || ConexionWebService.getInstance().getCodError() == 403){
			            	intentos++;
			            }else{
			            	//if (ConexionWebService.getInstance().getCodError() == 200)
			            	intentos=2;
			            }
		            }
				    actualizarEstadoTransmiteNovedad(rsLeer.getString("NOVEDAD_ID"), rsLeer.getString("NOVEDAD"));
		            ////conn.commit();
	            }else {
		            actualizarEstadoyObservacionesNovedad(rsLeer.getString("NOVEDAD_ID"), "NO EXISTE EN LEGIS_PROCESOS","");
	            }

        		counter += 1;

            }//Fin while (rsLeer.next())
        }
        catch (SQLException sqlexception) {
            this.error = "transmitirNovedadesYactuaciones " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("ErrorSql: " + this.error);
        }
        catch (Exception exception) {
            this.error = "transmitirNovedadesYactuaciones " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        UtilMensaje.getInstance().mensaje("Registros procesados: " + counter);
    }

    /*
     * @autor C�sar Vega
     * @param fechaProceso es la fecha que viene por par�metro
     * @return lista
     * @version 29/12/2016
     */
    public void prepararDocumentos(String sProcesoEnEjecucion, String sDias) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
        int counter=0;

        try {

            sqlQuery =
              "SELECT DISTINCT LN.NOVEDAD_ID, LN.CASO, LN.ACTUACION_PROCESAL_ID , LN.ACTUACION_ID_LEGIS "
            		+ " , TO_CHAR(LN.FECHA_CREACION,'YYYY-MM-DD') FECHA_CREACION "
            		+ " , REPLACE(ln.RADICADO || '_' || to_char(ln.fecha,'YYYY-MM-DD') || '_' || a.ACTUACION_NOMBRE || '.pdf' , ' ', '_') AS NOMBRE_ARCHIVO"
            		+ " , LN.TITULO, UPPER(LN.OFICINA_ID) OFICINA_ID "
            + " FROM LEGIS_NOVEDADES LN, actuaciones a "
            + " WHERE LN.ESTADO IN ('DOCUMENTOS_PENDIENTES','FINALIZA_DOCUMENTOS')"
            + " and ln.actuacion_id = a.actuacion_id "
        	+ " and FECHA_CREACION >= sysdate - " + sDias
        	+ " order by NOVEDAD_ID ";


			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);

            while (rsLeer.next()) {
            	counter++;
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");
				String name_file = "";

				if (rsLeer.getString("OFICINA_ID").equals("FOMAG")||rsLeer.getString("OFICINA_ID").equals("DPJA")){
					name_file = rsLeer.getString("TITULO");
				}else{
					name_file = rsLeer.getString("NOMBRE_ARCHIVO");
				}

            	insertarDocumentos(rsLeer.getString("NOVEDAD_ID"), rsLeer.getString("ACTUACION_PROCESAL_ID"), rsLeer.getString("ACTUACION_ID_LEGIS"), rsLeer.getString("CASO"), rsLeer.getString("FECHA_CREACION"), name_file);
                //conn.commit();
            }
        }
        catch (SQLException sqlexception) {
            this.error = "prepararDocumentos: " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
        }
        catch (Exception exception) {
            this.error = "prepararDocumentos: " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        UtilMensaje.getInstance().mensaje("Registros procesados: " + counter);
    }

    public void verificarDocumentosEnError(String sProcesoEnEjecucion) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
        int counter=0;

        try {

            sqlQuery = "SELECT LD.DOCUMENTO_ID, LD.ARCHIVO, LD.OBSERVACIONES, LD.NOVEDAD_ID"
            + " FROM LEGIS_DOCUMENTOS LD "
            + " WHERE LD.ESTADO = 'ERROR'"
            + " and FECHA_CREACION >= sysdate - 30 "
            //+ " and LD.DOCUMENTO_ID = 495647"
            + "ORDER BY LD.DOCUMENTO_ID";

			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);

            while (rsLeer.next()) {

				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");
            	UtilMensaje.getInstance().mensaje("DOCUMENTO_ID " + rsLeer.getString("DOCUMENTO_ID"));

            	String sFile = rsLeer.getString("ARCHIVO");
            	DatosArchivo da = new DatosArchivo(sFile);

            	//tieneActuacionNovedadDeEliminacion(rsLeer.getString("NOVEDAD_ID"));
            			
            	sqlQuery = "UPDATE LEGIS_DOCUMENTOS "
            			+ " SET TAMANNIO = " + da.totalSize
            			+ ", TIPO_TAMANNIO = '" + da.tipoTamannio + "'";
            	if(da.sEstado.equals("ok")){
            		if (rsLeer.getString("OBSERVACIONES").contains("El identificador de la actuación no es válido.")){
            			if (tieneActuacionNovedadDeEliminacion(rsLeer.getString("NOVEDAD_ID"))){
                    		sqlQuery += ", ESTADO = 'CON_NOVEDAD_DE_ELIMINACION' ";
            			}else{
                    		sqlQuery += ", ESTADO = 'ENVIAR' ";
                		}
            		}else{
                		sqlQuery += ", ESTADO = 'ENVIAR' ";
            		}
        			//+ ", OBSERVACIONES = null";
            	}else{
            		sqlQuery += ", OBSERVACIONES = '" + da.sEstado + "'";
            	}
          		sqlQuery += " WHERE DOCUMENTO_ID = " + rsLeer.getString("DOCUMENTO_ID");

            	ejecutarQuery("LEGIS_DOCUMENTOS", "verificarDocumentosEnError", sqlQuery);

            }
        }
        catch (SQLException sqlexception) {
            this.error = "verificarDocumentosEnError: " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
        }
        catch (Exception exception) {
            this.error = "verificarDocumentosEnError: " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        UtilMensaje.getInstance().mensaje("Registros procesados: " + counter);
    }

    private boolean tieneActuacionNovedadDeEliminacion(String novedad_id) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
        int cantidad = 0; 
        try {

            sqlQuery =
              "SELECT COUNT(*) AS Q "
			+ " FROM LEGIS_NOVEDADES LP "
			+ " WHERE NOVEDAD = 'ELIMINA_ACTUACION' " 
			+ " AND (ACTUACION_PROCESAL_ID, CASO) IN( " 
			+ "     SELECT ACTUACION_PROCESAL_ID, CASO  "
			+ "     FROM LEGIS_NOVEDADES LS "
			+ "     WHERE NOVEDAD_ID =  " + novedad_id
			+ " )";

			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);

			
            while (rsLeer.next()) {
            	cantidad = rsLeer.getInt("Q");
            }
        }
        catch (SQLException sqlexception) {
            this.error = "tieneActuacionNovedadDeEliminacion: " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
        }
        catch (Exception exception) {
            this.error = "tieneActuacionNovedadDeEliminacion: " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	//UtilMensaje.getInstance().mensaje(" cantidad (" + cantidad + ")");
    	if (cantidad == 0)
            return false;
        return true;
	}

	public void insertarDocumentos(String sNovedadId, String sActProcId, String sActLegis, String sCaso, String sFecha, String nombre_archivo ) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
		String sOk="";
		String sErrorDoc="ok";
		int qArchivosOk = 0;
		int qArchivosError = 0;

        try {
            sqlQuery =
            "Select DISTINCT ARCHIVO_ID, ARCHIVO_RUTA "
            + "from ("
            		+ " SELECT DISTINCT A.ARCHIVO_ID, A.ARCHIVO_RUTA, 'ACTUACION' AS TIPO"
            		+ "  FROM archivos a "
            		+ "   WHERE EXISTS ( "
            		+ "            SELECT da.dato_valor "
            		+ "              FROM actuaciones_procesales ap, "
            		+ "                   datos_actuacion_procesal da, "
            		+ "                   datos dat "
            		+ "             WHERE dat.dato_tipo_dato = 'ARCHIVO' "
            		+ "               AND da.dato_nombre = dat.dato_nombre "
            		+ "               AND ap.actuacion_procesal_id = da.actuacion_procesal_id "
            		+ "               AND ap.actuacion_procesal_id = " + sActProcId
            		+ "               AND da.dato_valor = a.archivo_id) "
            + " UNION "
	    		+ "	SELECT DISTINCT A.ARCHIVO_ID, A.ARCHIVO_RUTA, 'AUTO' AS TIPO "
	    		+ "	FROM ACTUACIONES_PROCESALES AP "
	    		+ "		 , DATOS_ACTUACION_PROCESAL DAP "
	    		+ "		 , ARCHIVOS A "
	    		+ "	WHERE DAP.DATO_NOMBRE like 'ARCHIVO_RELACIONADO%'"
	    		+ "		 AND DAP.ACTUACION_PROCESAL_ID = AP.ACTUACION_PROCESAL_ID "
	    		+ "		 AND DAP.ACTUACION_ID = AP.ACTUACION_ID "
	    		+ "		 AND DAP.DATO_VALOR = A.ARCHIVO_ID "
	    		+ "		AND A.ARCHIVO_ACCESO = 'P' "
	    		+ "		AND A.ARCHIVO_ABSTRACT = TO_CHAR(AP.ACTUACION_PROCESAL_ID) "
	    		+ "		AND A.ARCHIVO_PALABRAS_CLAVE = TO_CHAR(AP.PROCESO_ID)"
	    		//+ "		AND A.ARCHIVO_CARGADO_POR = 'amazonAutos'"
	    		+ "  AND AP.ACTUACION_PROCESAL_ID = " + sActProcId
        + ") losArchivos "
        + "where not exists ( "
        + "		select 1 "
        + "		from legis_documentos ld "
        + "		where ld.archivo_id = losarchivos.archivo_id "
        + "     and ld.caso = '" + sCaso + "'"
   //     + "		and ld.NOVEDAD_ID = " + sNovedadId
        + ")" ;

			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);

            while (rsLeer.next()) {
            	String sFile = rsLeer.getString("ARCHIVO_RUTA");
            	DatosArchivo da = new DatosArchivo(sFile);

            	sqlQuery = "INSERT INTO LEGIS_DOCUMENTOS (DOCUMENTO_ID, NOVEDAD_ID, ARCHIVO_ID, CASO, TIPO_FILE, ARCHIVO, TAMANNIO, TIPO_TAMANNIO, ESTADO, OBSERVACIONES, nombre_archivo) ";
            	if(da.sEstado.equals("ok")){//Indicador de Archivo valido
            		//sqlQuery += "VALUES (SEC_LEGIS_DOCUMENTOS.NEXTVAL, " + sNovedadId + ", "+ rsLeer.getString("ARCHIVO_ID") + ",'" + rsLeer.getString("TIPO") + "', '" + sFile + "', " + da.totalSize + ", '" + da.tipoTamannio  + "', 'ENVIAR', null)";
            		sqlQuery += "VALUES (SEC_LEGIS_DOCUMENTOS.NEXTVAL, " + sNovedadId + ", "+ rsLeer.getString("ARCHIVO_ID") + ",'" + sCaso + "','NA', '" + sFile + "', " + da.totalSize + ", '" + da.tipoTamannio  + "', 'ENVIAR', null, '"+nombre_archivo+"')";
            		qArchivosOk++;
            	}else{
            		sqlQuery += "VALUES (SEC_LEGIS_DOCUMENTOS.NEXTVAL, " + sNovedadId + ", "+ rsLeer.getString("ARCHIVO_ID") + ",'" + sCaso + "','NA', '" + sFile + "', " + da.totalSize + ", '" + da.tipoTamannio  + "', 'ERROR','" + da.sEstado + "', '"+nombre_archivo+"')";
            		qArchivosError++;
            	}

            	String sError = ejecutarQuery("LEGIS_DOCUMENTOS", "insertarDocumentos", sqlQuery);

            	if (!sError.equals("ok"))
            		sErrorDoc = " Error al insertar LEGIS_DOCUMENTOS";


            }
            sOk="sNovedadId " + sNovedadId + " sFecha " + sFecha + " Archivos a enviar: Ok " + qArchivosOk + " Error " + qArchivosError;
        	if (!sErrorDoc.equals("ok"))
        		sOk += sErrorDoc;
        }
        catch (SQLException sqlexception) {
            this.error = "insertarDocumentos Error: " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            sOk += sqlexception.getMessage();
            sErrorDoc = "Error";
        }
        catch (Exception exception) {
            this.error = "insertarDocumentos Error : " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
            sOk += exception.getMessage();
            sErrorDoc = "Error";
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        if (qArchivosOk + qArchivosError > 0){
        	UtilMensaje.getInstance().mensaje(sOk);
        	finalizarActuacionPorEnvioDoc(sNovedadId, sOk, sErrorDoc);
        }
    }

	public ArrayList<HiloDatosDocumento> getListaDocumentosTx(String sProcesoEnEjecucion, String cantidadRegistros) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
        
        int counter = 0;
        ArrayList<HiloDatosDocumento> listaDocs  = new ArrayList<HiloDatosDocumento>();
		ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");
        try {

            sqlQuery = "select * from ("
    		+ "SELECT /*+ INDEX(LD IDX_LD_FID)*/ "
    		+ "  ROW_NUMBER() OVER (ORDER BY LD.DOCUMENTO_ID DESC) AS FILA "
            + ", LD.DOCUMENTO_ID, NVL(LN.NOVEDAD_ID,-998) NOVEDAD_ID " 
            + ", LN.ACTUACION_PROCESAL_ID, LN.ACTUACION_ID_LEGIS, LN.OFICINA_ID, LN.CASO "
            + ", LD.ARCHIVO_ID, LD.ARCHIVO, LD.NOMBRE_ARCHIVO "
            //+ " , REPLACE(ln.RADICADO || '_' || to_char(ln.fecha,'YYYY-MM-DD') || '_' || a.actuacion_nombre  || '.pdf' , ' ', '_') AS NOMBRE_ARCHIVO_2"
            + " FROM LEGIS_DOCUMENTOS LD "
            + " , LEGIS_NOVEDADES LN, actuaciones a "
            + " WHERE LD.NOVEDAD_ID = LN.NOVEDAD_ID (+)"
            + " AND LD.ESTADO = 'ENVIAR'"
            + " and ln.actuacion_id = a.actuacion_id "
            + " and LD.NOMBRE_ARCHIVO is not null"
            + " and LD.DOCUMENTO_ID = 471831 "
            + ")where fila <  " + cantidadRegistros
        	//+ " and LD.FECHA_CREACION >= sysdate - 30 " 
           	//+ " order by ld.FECHA_CREACION DESC";
            //+ " order by LD.DOCUMENTO_ID DESC"
            ;
			UtilMensaje.getInstance().mensaje(sqlQuery);
			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			

            while (rsLeer.next()) {
            	counter++;
				HiloDatosDocumento dd = new HiloDatosDocumento();
				dd.setDocumentoId(rsLeer.getString("DOCUMENTO_ID"));
				dd.setNovedadId(rsLeer.getString("NOVEDAD_ID"));
				dd.setActuacionProcesalId(rsLeer.getString("ACTUACION_PROCESAL_ID"));
				dd.setActuacionLegisId(rsLeer.getInt("ACTUACION_ID_LEGIS"));
            	dd.setOficina(rsLeer.getString("OFICINA_ID"));
            	dd.setCaso(rsLeer.getString("CASO"));
            	dd.setArchivoId(rsLeer.getString("ARCHIVO_ID"));
            	dd.setArchivoRutaEnY(rsLeer.getString("ARCHIVO"));
            	dd.setArchivoNombre(rsLeer.getString("NOMBRE_ARCHIVO"));
            	
            	listaDocs.add(dd);
				
            	UtilMensaje.getInstance().mensaje("DOCUMENTO_ID " + rsLeer.getString("DOCUMENTO_ID") + " NOVEDAD_ID "+ rsLeer.getString("NOVEDAD_ID") + " ("+ counter +")");
        	}
        }
        catch (SQLException sqlexception) {
            this.error = "getListaDocumentosTx: " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
        }
        catch (Exception exception) {
            this.error = "getListaDocumentosTx: " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
		}finally{		
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	  
        UtilMensaje.getInstance().mensaje("Registros a procesar: " + counter );
		return listaDocs;
	}	    

	public ArrayList<HiloDatosActuacionEliminar> getListaActuacionesEliminar(String sProcesoEnEjecucion, String cantidadRegistros) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
        
        int counter = 0;
        ArrayList<HiloDatosActuacionEliminar> listaDocs  = new ArrayList<HiloDatosActuacionEliminar>();
		ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");
        try {

            sqlQuery = "SELECT NOVEDAD_ID, CASO, OFICINA_ID, ACTUACION_ID_LEGIS "
            		+ " FROM LEGIS_NOVEDADES "
            		+ " WHERE NOVEDAD = 'ELIMINA_ACTUACION' "
            		+ " AND ESTADO in ('ELIMINAR_ACTUACION','ERROR_ELIMINA_ACTUACION') "
            		+ " AND TRUNC(FECHA_CREACION) >= TRUNC(SYSDATE) - 30 "
            		;
            
			UtilMensaje.getInstance().mensaje(sqlQuery);
			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			

            while (rsLeer.next()) {
            	counter++;
            	HiloDatosActuacionEliminar dd = new HiloDatosActuacionEliminar();
				dd.setNovedadId(rsLeer.getString("NOVEDAD_ID"));
				dd.setActuacionLegisId(rsLeer.getInt("ACTUACION_ID_LEGIS"));
            	dd.setOficina(rsLeer.getString("OFICINA_ID"));
            	dd.setCaso(rsLeer.getString("CASO"));
           	
            	listaDocs.add(dd);
				
            	UtilMensaje.getInstance().mensaje("NOVEDAD_ID " + rsLeer.getString("NOVEDAD_ID") + " ACTUACION_ID_LEGIS "+ rsLeer.getString("ACTUACION_ID_LEGIS") + " ("+ counter +")");
        	}
        }
        catch (SQLException sqlexception) {
            this.error = "getListaActuacionesEliminar: " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
        }
        catch (Exception exception) {
            this.error = "getListaActuacionesEliminar: " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
		}finally{		
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	  
        UtilMensaje.getInstance().mensaje("Registros a procesar: " + counter );
		return listaDocs;
	}		
    /*
     * @autor C�sar Vega
     * @param fechaProceso es la fecha que viene por par�metro
     * @return lista
     * @version 29/12/2016
     */

    public void transmitirDocumentos(String sProcesoEnEjecucion) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
        int counter=0;
        int cuantos=150;
        cuantos=Integer. parseInt(ClassPropertiesLegis.getInstance().cantidadDocumentosTransmitir);
        int qOk=0;
        int qError=0;

        String sDocId="0";
        String sJson = "0";
        try {

            sqlQuery = "select * from ("
    		+ "SELECT /*+ INDEX(LD IDX_LD_FID)*/ "
    		+ "  ROW_NUMBER() OVER (ORDER BY LD.DOCUMENTO_ID DESC) AS FILA "
            + ", LD.DOCUMENTO_ID, NVL(LN.NOVEDAD_ID,-998) NOVEDAD_ID "
            + ", LN.ACTUACION_PROCESAL_ID, LN.ACTUACION_ID_LEGIS, LN.OFICINA_ID, LN.CASO "
            + ", LD.ARCHIVO_ID, LD.ARCHIVO, LD.NOMBRE_ARCHIVO "
            + " , REPLACE(ln.RADICADO || '_' || to_char(ln.fecha,'YYYY-MM-DD') || '_' || a.actuacion_nombre  || '.pdf' , ' ', '_') AS NOMBRE_ARCHIVO_2"
            + " FROM LEGIS_DOCUMENTOS LD "
            + " , LEGIS_NOVEDADES LN, actuaciones a "
            + " WHERE LD.NOVEDAD_ID = LN.NOVEDAD_ID (+)"
            + " AND LD.ESTADO = 'ENVIAR'"
            + " and ln.actuacion_id = a.actuacion_id "
            + " and nvl(LN.ACTUACION_ID_LEGIS,0) > 0"
            //+ " and LD.DOCUMENTO_ID = 497206"
            + ")where fila <  " + cuantos
            //+ " and DOCUMENTO_ID = 421099 "
        	//+ " and LD.FECHA_CREACION >= sysdate - 30 "
           	//+ " order by ld.FECHA_CREACION DESC";
            //+ " order by LD.DOCUMENTO_ID DESC"
            ;

            UtilMensaje.getInstance().mensaje(sqlQuery);
			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);

            while (rsLeer.next()) {
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");
				sJson = "0";

            	UtilMensaje.getInstance().mensaje("DOCUMENTO_ID " + rsLeer.getString("DOCUMENTO_ID") + " NOVEDAD_ID "+ rsLeer.getString("NOVEDAD_ID"));
            	sDocId = rsLeer.getString("DOCUMENTO_ID");
            	String sEstado="";
            	if (tieneProcesoActivo(rsLeer.getString("NOVEDAD_ID"))){
	            	String sFile = rsLeer.getString("ARCHIVO");
	            	sFile = sFile.replace("\\", "/");
	            	sFile = sFile.replace("D:/FOTOS", "Y:");
	            	String sNameDestino = "DOCUMENTO_ID_" + rsLeer.getString("DOCUMENTO_ID") + "_NOVEDAD_ID_"+ rsLeer.getString("NOVEDAD_ID") + ".pdf";
	            	String sDestino = ClassPropertiesLegis.getInstance().rutaLegis + sNameDestino;
	            	//String sDestino = ClassPropertiesLegis.getInstance().rutaLegis + "DOCUMENTO_ID_" + rsLeer.getString("DOCUMENTO_ID") + "_NOVEDAD_ID_"+ rsLeer.getString("NOVEDAD_ID") + ".pdf";

	            	//Inicio tiempo de copia
	        		long inicio = System.currentTimeMillis();

	            	//sEstado = ArchivosUtil.getInstance().copiarArchivo(sFile, sDestino);
	                sEstado = "error";
	                //String sDestinoWindows = ClassPropertiesLegis.getInstance().rutaLegis + sNameDestino;
	                String sDestinoWindows = sDestino;
	                //String sOrigenAws = dd.getArchivoRutaEnAws();
	                String sOrigenAws = sFile.replace("Y:/", "");
	                sEstado = AmazonS3ObjectOperation.getInstance().downloadFileAwsToWindows(sOrigenAws, sDestinoWindows, sFile);

	            	long fin = System.currentTimeMillis();
	        		double tiempo = (double) ((fin - inicio)/1000);
	        		UtilMensaje.getInstance().mensaje("DOCUMENTO_ID_" + rsLeer.getString("DOCUMENTO_ID") + " Duraci�n de la copia: minutos [" + tiempo/60 + "] segundos [" + tiempo + "]");
	            	//Fin tiempo de copia
	    			if (sEstado.equals("ok")){
	                	//Inicio
	        			DatosDocumentos regDocAct = new DatosDocumentos();
	        			regDocAct.setOficina(rsLeer.getString("OFICINA_ID"));
	        			regDocAct.setIdActuacion(new Integer(rsLeer.getString("ACTUACION_ID_LEGIS")).intValue());
	        			regDocAct.setCaso(rsLeer.getString("CASO"));
	        			String sNameFile = "";
	        			if (rsLeer.getString("NOMBRE_ARCHIVO") == null)
	        				sNameFile = rsLeer.getString("NOMBRE_ARCHIVO_2");
        				else
	        				sNameFile = rsLeer.getString("NOMBRE_ARCHIVO");
	        			regDocAct.setNombre(sNameFile);
	        			regDocAct.setCodigoExterno(rsLeer.getString("ACTUACION_PROCESAL_ID"));

	        			// Armar el json con Gison - En esta funcion se usa para grabarlo en la tabla de documentos.
	        			// - No se incluye el pdf para poder guardar la estructura del json en la tabla
	        			final Gson gson = new Gson();
	        			regDocAct.setDocumento(sDestino);
	        			sJson = gson.toJson(regDocAct);

	        			// Serializar el PDF
	        			CodeBase64 base = new  CodeBase64();
	        			//base.setsRuta(sFile);
	        			base.setsRuta(sDestino);

	        			regDocAct.setDocumento(base.serializarPDF());
	        			if (regDocAct.getDocumento().length() != 0){
	                    	ConexionWebService.getInstance().setCodError(-100);//200 es ok, se coloca para hacer 3 intentos
	                    	ConexionWebService.getInstance().setEstadoEnvio("Limpia estado antes de tx");
        					sEstado = "Logra serializar";
	        				for (int i = 0; ConexionWebService.getInstance().getCodError()!=200 && i < 3 ; i++){
	        					UtilMensaje.getInstance().mensaje("Intento de transmicion (" + i + ") del archivo " + sDestino);
	        					ConexionWebService.getInstance().transmitirDocumento(regDocAct, rsLeer.getString("DOCUMENTO_ID"));
	        					if (ConexionWebService.getInstance().getCodError()==200)
	        						i = 3;
	        					else{
	        						if (!ConexionWebService.getInstance().getEstadoEnvio().contains("El Token no existe")
	        						&& !ConexionWebService.getInstance().getEstadoEnvio().contains("Access is denied"))
		        						i = 3;
	        					}
	        				}
	        			}else{
	        				sEstado = "Error al serializar";
	        				qError++;
	        			}

	        				//Fin
	                	ArchivosUtil.getInstance().borrarArchivo(sDestino);
	            	}else{
	    				sEstado += "Error al copiar archivo para serializar. " + sEstado;
	    				qError++;
	            	}//if (sEstado.equals("ok")){
            	}else{
    				sEstado = "Error novedad no tiene proceso activo";
    				qError++;
            	}//if tieneProcesoActivo

    		    // Se va a actualizar por cada sFile exitoso!!!
    		    // Se deber�a grabar s�lo el priemro y los dem�s no? Si es as�, hay que colocar switch para que lo haga 1 vez
            	actualizarEstadoTransmiteDocumento(rsLeer.getString("DOCUMENTO_ID"),  sEstado, sJson);

            	counter += 1;
                //conn.commit();
                UtilMensaje.getInstance().mensaje("DOCUMENTO " + rsLeer.getString("DOCUMENTO_ID") + "(" + counter + "/" + cuantos + ")\n");
            }
        }
        catch (SQLException sqlexception) {
            this.error = "sDocId " + sDocId + " transmitirDocumentos: " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
        }
        catch (Exception exception) {
            this.error = "sDocId " + sDocId + " transmitirDocumentos: " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        UtilMensaje.getInstance().mensaje("Registros procesados: " + counter + " ok " + qOk + " Error " + qError);
    }    
    

    public void transmitirActuacionesEliminarLegis(String sProcesoEnEjecucion, HiloDatosActuacionEliminar dd) {
        
        String sJson = "0";
    	UtilMensaje.getInstance().mensaje("NOVEDAD_ID "+ dd.getNovedadId() + " actProcLegisId " + dd.getActuacionLegisId() + " Inicio");
    	//String sEstado="";
    	//if (tieneProcesoActivo(dd.getNovedadId())){
    		DatosEliminaActuacion regActEliminar = new DatosEliminaActuacion();
    		regActEliminar.setOficina(dd.getOficina());
    		regActEliminar.setIdActuacion(dd.getActuacionLegisId());
    		regActEliminar.setCaso(dd.getCaso());
			
			// Armar el json con Gison - En esta funcion se usa para grabarlo en la tabla de novedades.
			// - No se incluye el pdf para poder guardar la estructura del json en la tabla
			final Gson gson = new Gson();
			sJson = gson.toJson(regActEliminar);
    			
        	ConexionWebService.getInstance().setCodError(-100);//200 es ok, se coloca para hacer 3 intentos
        	ConexionWebService.getInstance().setEstadoEnvio("Limpia estado antes de tx");
			for (int i = 0; ConexionWebService.getInstance().getCodError()!=200 && i < 3 ; i++){
				UtilMensaje.getInstance().mensaje("Intento de transmicion (" + i + ") de la novedad: " + dd.getNovedadId()); 
				ConexionWebService.getInstance().transmitirActuacionEliminar(regActEliminar, dd.getNovedadId());
				if (ConexionWebService.getInstance().getCodError()==200)
					i = 3;
				else{
					if (!ConexionWebService.getInstance().getEstadoEnvio().contains("El Token no existe")
					&& !ConexionWebService.getInstance().getEstadoEnvio().contains("Access is denied"))
						i = 3;
				}
			}
    	//}else{
		//	sEstado = "Error novedad " + dd.getNovedadId() + " no tiene proceso activo";
    	//}//if tieneProcesoActivo
    	
	    // Se va a actualizar por cada sFile exitoso!!!
    	actualizarEstadoTransmiteEliminaActuacion(dd.getNovedadId(), sJson);
    	UtilMensaje.getInstance().mensaje("NOVEDAD_ID "+ dd.getNovedadId() + " actProcLegisId " + dd.getActuacionLegisId() + " Fin");
 
    }   	

    
    public void envioManualDeDocumentos(String sProcesoEnEjecucion) {
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";
        int counter=0;
        int cuantos=100;
        int qOk=0;
        int qError=0;

        String sDocId="0";
        String sJson = "0";
        try {

            sqlQuery = "select * from ("
    		+ "SELECT /*+ INDEX(LD IDX_LD_FID)*/ "
    		+ "  ROW_NUMBER() OVER (ORDER BY LD.DOCUMENTO_ID DESC) AS FILA "
            + ", LD.DOCUMENTO_ID, NVL(LN.NOVEDAD_ID,-998) NOVEDAD_ID "
            + ", LN.ACTUACION_PROCESAL_ID, LN.ACTUACION_ID_LEGIS, LN.OFICINA_ID, LN.CASO "
            + ", LD.ARCHIVO_ID, LD.ARCHIVO, LD.NOMBRE_ARCHIVO "
            + " FROM LEGIS_DOCUMENTOS LD "
            + " , LEGIS_NOVEDADES LN "
            + " WHERE LD.NOVEDAD_ID = LN.NOVEDAD_ID (+)"
           // + " AND LD.ESTADO = 'ENVIAR'"
            +  " and ld.DOCUMENTO_ID in(	199189	, "
            +  " 	199792	, "
            +  " 	200089	, "
            +  " 	200482	, "
            +  " 	200534	, "
            +  " 	201246	, "
            +  " 	202049	, "
            +  " 	202058	, "
            +  " 	202100	, "
            +  " 	202672	, "
            +  " 	202811	, "
            +  " 	203455	, "
            +  " 	203526	, "
            +  " 	203745	, "
            +  " 	204218	, "
            +  " 	204839	, "
            +  " 	206156	, "
            +  " 	206159	, "
            +  " 	206160	, "
            +  " 	206161	, "
            +  " 	207153	, "
            +  " 	208055	, "
            +  " 	208057	, "
            +  " 	208779	, "
            +  " 	209275	, "
            +  " 	209277	, "
            +  " 	209279	, "
            +  " 	209282	, "
            +  " 	209283	, "
            +  " 	210238	, "
            +  " 	211092	, "
            +  " 	211160	, "
            +  " 	211161	, "
            +  " 	211226	, "
            +  " 	212123	, "
            +  " 	212404	, "
            +  " 	212421	, "
            +  " 	213299	, "
            +  " 	213320	, "
            +  " 	213321	, "
            +  " 	213324	, "
            +  " 	213325	, "
            +  " 	213326	, "
            +  " 	213689	, "
            +  " 	213697	, "
            +  " 	216158	, "
            +  " 	216232	, "
            +  " 	216861	, "
            +  " 	217222	, "
            +  " 	217223	, "
            +  " 	217798	, "
            +  " 	218406	, "
            +  " 	219093	, "
            +  " 	219382	, "
            +  " 	220267	, "
            +  " 	220293	, "
            +  " 	220298	, "
            +  " 	220305	, "
            +  " 	220419	, "
            +  " 	220420	, "
            +  " 	220422	, "
            +  " 	220423	, "
            +  " 	220424	, "
            +  " 	220425	, "
            +  " 	221059	, "
            +  " 	222465	, "
            +  " 	223149	, "
            +  " 	223151	, "
            +  " 	223152	, "
            +  " 	223153	, "
            +  " 	223154	, "
            +  " 	223155	, "
            +  " 	223156	, "
            +  " 	223158	, "
            +  " 	223159	, "
            +  " 	223160	, "
            +  " 	223161	, "
            +  " 	223164	, "
            +  " 	223165	, "
            +  " 	223167	, "
            +  " 	223166	, "
            +  " 	223168	, "
            +  " 	223169	, "
            +  " 	223170	, "
            +  " 	223174	, "
            +  " 	223176	, "
            +  " 	223177	, "
            +  " 	223178	, "
            +  " 	223179	, "
            +  " 	223182	, "
            +  " 	223451	, "
            +  " 	223454	, "
            +  " 	223690	)"
            + ")where fila <  " + cuantos;
            //+ " and LD.DOCUMENTO_ID = 234560 "
        	//+ " and LD.FECHA_CREACION >= sysdate - 30 "
           	//+ " order by ld.FECHA_CREACION DESC";
            //+ " order by LD.DOCUMENTO_ID DESC";


			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);

            while (rsLeer.next()) {
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");
				sJson = "0";

            	UtilMensaje.getInstance().mensaje("DOCUMENTO_ID " + rsLeer.getString("DOCUMENTO_ID") + " NOVEDAD_ID "+ rsLeer.getString("NOVEDAD_ID"));
            	sDocId = rsLeer.getString("DOCUMENTO_ID");
            	String sEstado="";
            	if (tieneProcesoActivo(rsLeer.getString("NOVEDAD_ID"))){
	            	String sFile = rsLeer.getString("ARCHIVO");
	            	sFile = sFile.replace("\\", "/");
	            	sFile = sFile.replace("D:/FOTOS", "Y:");
	            	String sNameDestino = "DOCUMENTO_ID_" + rsLeer.getString("DOCUMENTO_ID") + "_NOVEDAD_ID_"+ rsLeer.getString("NOVEDAD_ID") + ".pdf";
	            	String sDestino = ClassPropertiesLegis.getInstance().rutaLegis + sNameDestino;

	            	//Inicio tiempo de copia
	        		long inicio = System.currentTimeMillis();

	            	sEstado = ArchivosUtil.getInstance().copiarArchivo(sFile, sDestino);

	            	long fin = System.currentTimeMillis();
	        		double tiempo = (double) ((fin - inicio)/1000);
	        		UtilMensaje.getInstance().mensaje("DOCUMENTO_ID_" + rsLeer.getString("DOCUMENTO_ID") + " Duraci�n de la copia: minutos [" + tiempo/60 + "] segundos [" + tiempo + "]");
	            	//Fin tiempo de copia
	    			if (sEstado.equals("ok")){
	                	//Inicio
	        			DatosDocumentos regDocAct = new DatosDocumentos();
	        			regDocAct.setOficina(rsLeer.getString("OFICINA_ID"));
	        			regDocAct.setIdActuacion(new Integer(rsLeer.getString("ACTUACION_ID_LEGIS")).intValue());
	        			regDocAct.setCaso(rsLeer.getString("CASO"));
	        			regDocAct.setNombre(rsLeer.getString("NOMBRE_ARCHIVO"));
	        			regDocAct.setCodigoExterno(rsLeer.getString("ACTUACION_PROCESAL_ID"));

	        			// Armar el json con Gison - En esta funcion se usa para grabarlo en la tabla de documentos.
	        			// - No se incluye el pdf para poder guardar la estructura del json en la tabla
	        			final Gson gson = new Gson();
	        			regDocAct.setDocumento(sDestino);
	        			sJson = gson.toJson(regDocAct);

	            	}else{
	    				sEstado = "Error al copiar archivo para serializar";
	    				qError++;
	            	}//if (sEstado.equals("ok")){
            	}else{
    				sEstado = "Error novedad no tiene proceso activo";
    				qError++;
            	}//if tieneProcesoActivo

    		    // Se va a actualizar por cada sFile exitoso!!!
    		    // Se deber�a grabar s�lo el priemro y los dem�s no? Si es as�, hay que colocar switch para que lo haga 1 vez
            	actualizarEstadoEnvioManualDocumento(rsLeer.getString("DOCUMENTO_ID"),  sEstado, sJson);

            	counter += 1;
                //conn.commit();
                UtilMensaje.getInstance().mensaje("DOCUMENTO " + rsLeer.getString("DOCUMENTO_ID") + "(" + counter + "/" + cuantos + ")\n");
            }
        }
        catch (SQLException sqlexception) {
            this.error = "sDocId " + sDocId + " transmitirDocumentos: " + sqlexception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
        }
        catch (Exception exception) {
            this.error = "sDocId " + sDocId + " transmitirDocumentos: " + exception.getMessage() + "   Query: " + sqlQuery;
            UtilMensaje.getInstance().mensaje("Error: " + this.error);
            exception.printStackTrace();
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        UtilMensaje.getInstance().mensaje("Registros procesados: " + counter + " ok " + qOk + " Error " + qError);
    }

    /*
     * @autor C�sar Vega
     * @param Datos para actualizar la tabla RegistroActuacion (token)
     * @return void
     * @version 18/12/2016
     */
    private void actualizarEstadoTransmiteDocumento(String sDocId, String sError, String sJson){
    	String sqlQuery = "";

    	String sMensaje = "";
    	String sEstado = "";

    	if (sError.contains("Error")){
    		sMensaje = sError;
        	sqlQuery = " update LEGIS_DOCUMENTOS "
        	    	+ " set  OBSERVACIONES = '" + sError + "'";

        	if (sError.equals("Error novedad no tiene proceso activo")){
	    		sqlQuery += ", ESTADO = 'RETIRADO'";
	    		sEstado = "RETIRADO";
        	}else{
	    		sqlQuery += ", ESTADO = 'ERROR'";
	    		sEstado = "ERROR";
        	}

    	}else{
    		String sObservaciones = "";
    		try{
    			sObservaciones = ConexionWebService.getInstance().getEstadoEnvio();
    		}catch(Exception exception ){
                this.error = "actualizarEstadoTransmiteDocumento: " + exception.getMessage() + "   Query: " + sqlQuery;
                UtilMensaje.getInstance().mensaje("Error: " + this.error);
                exception.printStackTrace();
    		}
        	sMensaje = sObservaciones;
        	int maxLength = (sJson.length() < 4000)?sJson.length():4000;
        	String newJson = sJson.substring(0, maxLength);
        	sqlQuery = " update LEGIS_DOCUMENTOS "
        	    	+ " set  OBSERVACIONES = SUBSTR('" + sObservaciones + "',1,800)"
        	        + " , COD_ERROR = " + ConexionWebService.getInstance().getCodError()
        			+ " , SJSON = '" + newJson + "'";


        	    	if (ConexionWebService.getInstance().getCodError()==200){
        	    		sqlQuery += ", ESTADO = 'TRANSFERIDO'";
        	    		sEstado = "TRANSFERIDO";
        	    	}
        	    	else{
        	    		if (sObservaciones.contains("El documento no es un PDF")
        	    				|| sObservaciones.contains("extension no es pdf")){
            	    		sqlQuery += ", ESTADO = 'NO_ES_PDF'";
            	    		sEstado = "NO_ES_PDF";
        	    		}else{
	        	    		if (sObservaciones.contains("tama�o m�nimo")
	        	    				|| sObservaciones.contains("Archivo vacio")
	        	    				|| sObservaciones.contains("Supera 20 megas")
	        	    				|| sObservaciones.contains("documento excede")){
	            	    		sqlQuery += ", ESTADO = 'TAMA�O_NO_PERMITIDO'";
	            	    		sEstado = "TAMA�O_NO_PERMITIDO";
	        	    		}else{
	        	    			sqlQuery += ", ESTADO = 'ERROR'";
	            	    		sEstado = "ERROR";
	        	    		}
        	    		}
        	    	}
    	}

    	sqlQuery += " , FECHA_TRANSMITE = sysdate ";
    	sqlQuery += " where DOCUMENTO_ID = " + sDocId;

    	UtilMensaje.getInstance().mensaje("DOCUMENTO_ID: " + sDocId + " Estado: " + sEstado + " Mensaje: " + sMensaje);

       	ejecutarQuery("actualizarEstadoTransmiteDocumento", "LEGIS_DOCUMENTOS", sqlQuery);

    }


    private void actualizarEstadoEnvioManualDocumento(String sDocId, String sEstado, String sJson){
    	String sqlQuery = "";

    	String sMensaje = "";

        	sqlQuery = " update LEGIS_DOCUMENTOS "
        	    	+ " set ESTADO = ESTADO || ' " + sEstado + "'"
        			+ " , token = '" + sJson + "'"
    	            + " where DOCUMENTO_ID = " + sDocId;

    	UtilMensaje.getInstance().mensaje("DOCUMENTO_ID: " + sDocId + " Estado: " + sEstado + " Mensaje: " + sMensaje);

       	ejecutarQuery("actualizarEstadoEnvioManualDocumento", "LEGIS_DOCUMENTOS", sqlQuery);

    }

    /*
     * @autor Jairo Vega
     * @version 29/12/2016
     */
    private void actualizarEstadoPreparaNovedad(String sNovedadId, DatosActuacionInsert da, String sDespachoId, String sValidaChangeType, String sAcuatcionIdLegis, String sActProcId){
    	String sqlQuery = "";
    	String sResumen = "";

    	sqlQuery = " update LEGIS_NOVEDADES set ";


    	if (da.getJuzgado().equals("0"))
    		sResumen += "SinJuzgado ";
   		else
   			sqlQuery += " DESPACHO = '" + da.getJuzgado() + "', ";

    	if (sDespachoId.equals("0") || sDespachoId.equals(""))
    		sResumen += "SinDespacho ";
   		else
   			sqlQuery += " DESPACHO_ID = " + sDespachoId + ", ";


    	if (!sValidaChangeType.equals("I")){
        	if (sValidaChangeType.equals("U"))
        		sqlQuery += " ACTUACION_ID_LEGIS = " + sAcuatcionIdLegis + ", ";
        	else
        		sqlQuery += " CHANGE_TYPE = 'I', ";
    	}

    	//System.out.println("Oficina: " + da.getOficina());
   	UtilMensaje.getInstance().mensaje("TIPO_PROCESO: (" + da.getTpProcesoId() + ")") ;
		if ((da.getOficina().equals("FOMAG")||da.getOficina().equals("DPJA")) && (da.getTpProcesoId().equals("338"))) {
			ArrayList<String> lista_actuacion_homologada_conciliacion = getActuacionHomologadaNewTituloConciliacion(sActProcId);
			ArrayList<String> lista_actuacion_homologadaManual_conciliacion = getActuacionHomologadaNewTituloManualConciliacion(sActProcId);
	    	if (lista_actuacion_homologada_conciliacion.size()>0) {
	    		String sActIdLegis = lista_actuacion_homologada_conciliacion.get(0);
	    		String sTitulo = lista_actuacion_homologada_conciliacion.get(1);
	    		sqlQuery += " ACTUACION_ID_HOMOLOGA = " + sActIdLegis + " ,";
	    		sqlQuery += " TITULO_HOMOLOGADO = '" + sTitulo + "' ,";
	    		} else if (lista_actuacion_homologadaManual_conciliacion.size()>0) {
		    		String sActIdLegis = lista_actuacion_homologadaManual_conciliacion.get(0);
		    		String sTitulo = lista_actuacion_homologadaManual_conciliacion.get(1);
		    		sqlQuery += " ACTUACION_ID_HOMOLOGA = " + sActIdLegis + " ,";
		    		sqlQuery += " TITULO_HOMOLOGADO = '" + sTitulo + "' ,";
	    		}else {
		    		sResumen = "SinActuacionLegis";
		    	}
		}

		if ((da.getOficina().equals("FOMAG")||da.getOficina().equals("DPJA")) && !(da.getTpProcesoId().equals("338"))) {
			ArrayList<String> lista_actuacion_homologada = getActuacionHomologadaNewTitulo(sActProcId);
			ArrayList<String> lista_actuacion_homologadaManual = getActuacionHomologadaNewTituloManual(sActProcId);
	    	if (lista_actuacion_homologada.size()>0) {
	    		String sActIdLegis = lista_actuacion_homologada.get(0);
	    		String sTitulo = lista_actuacion_homologada.get(1);
	    		sqlQuery += " ACTUACION_ID_HOMOLOGA = " + sActIdLegis + " ,";
	    		sqlQuery += " TITULO_HOMOLOGADO = '" + sTitulo + "' ,";
	    		} else if (lista_actuacion_homologadaManual.size()>0) {
		    		String sActIdLegis = lista_actuacion_homologadaManual.get(0);
		    		String sTitulo = lista_actuacion_homologadaManual.get(1);
		    		sqlQuery += " ACTUACION_ID_HOMOLOGA = " + sActIdLegis + " ,";
		    		sqlQuery += " TITULO_HOMOLOGADO = '" + sTitulo + "' ,";
	    		}else {
		    		sResumen = "SinActuacionLegis";
		    	}
		}

		sqlQuery += " FECHA_PREPARAR = SYSDATE, ";
    	if (sResumen.equals("")){
        	sqlQuery += " ESTADO = 'ENVIAR',";
        	sqlQuery += " OBSERVACIONES = null";
    	}else{
        	//sqlQuery += " ESTADO = 'PENDIENTE " + sResumen + "',";
        	sqlQuery += " OBSERVACIONES = '" + sResumen + "'";
    	}

		sqlQuery += " where NOVEDAD_ID = " + sNovedadId;

		UtilMensaje.getInstance().mensaje("sNovedadId " +  sNovedadId + " Resumen (" + sResumen + ")");

       	ejecutarQuery("LEGIS_NOVEDADES", "actualizarEstadoPreparaNovedad", sqlQuery);
    }

    private void actualizarEstadoyObservacionesNovedad(String sNovedadId, String sEstado, String sObs){
    	String sqlQuery = "";

    	sqlQuery =  " update LEGIS_NOVEDADES set ";
		sqlQuery += " FECHA_TRANSMITE = SYSDATE, ";
      	sqlQuery += " ESTADO = '" + sEstado + "',";
       	sqlQuery += " OBSERVACIONES = '" + sObs + "'";
		sqlQuery += " where NOVEDAD_ID = " + sNovedadId;

		UtilMensaje.getInstance().mensaje("sNovedadId " +  sNovedadId + " Resumen (" + sEstado + " - " + sObs + ")");

       	ejecutarQuery("LEGIS_NOVEDADES", "actualizarEstadoyObservacionesNovedad", sqlQuery);
    }

    /*
     * @autor Jairo Vega
     * @version 29/12/2016
-----------------------
9 de mayo de 2017
Debido a todos los inconvenientes que hemos tenido con los clientes de LegisOffice debido a la  trasmisi�n de actuaciones, se realizaron algunos filtros que bloquearan actuaciones que generen malestar o confusi�n  en los clientes.
Los filtros son los siguientes:
Las actuaciones con Fecha de actuaci�n inferior a la fecha de solicitud de vigilancia judicial del caso ser�n bloqueadas.
Las actuaciones con Fecha de actuaci�n con m�s de dos meses de retraso del caso ser�n bloqueadas.
Las actuaciones con t�tulo �Sale proceso de Vigilancia� y que no tengan el flagFinalizaServicio=1, ser�n bloqueadas.
Las actuaciones que tengan el flagFinalizaServicio=1, y no tenga una novedad de retiro ser�n bloqueadas.
Las actuaciones enviadas para casos que nunca han tenido vigilancia judicial ser�n bloqueadas
Todas las actuaciones bloqueadas el identificador que devolver� el servicio es un �0�.
     */
    private void actualizarEstadoTransmiteNovedad(String sNovedadId, String sNovedad){
    	String sqlQuery = "";
    	String sEstado = "";

    	if (sNovedad.equals("ACTUACION"))
   			sEstado="DOCUMENTOS_PENDIENTES";//Pendiente de enviar documentos
    	else
   			sEstado="TRANSMITIDA";//Fin de Novedades

    	sqlQuery = " update LEGIS_NOVEDADES "
        + " set ACTUACION_ID_LEGIS = " + ConexionWebService.getInstance().getIdActuacion()
        + " , COD_ERROR_WS = " + ConexionWebService.getInstance().getCodError()
    	+ " , FECHA_TRANSMITE = sysdate ";
    	if (ConexionWebService.getInstance().getCodError()==200){
    		if (ConexionWebService.getInstance().getIdActuacion()==0 && sNovedad.equals("ACTUACION")){
        		sqlQuery += ", ESTADO = '" + sEstado + " BloqueadaPorLegis'";
        		sqlQuery += " , OBSERVACIONES = 'BloqueadaPorLegis' ";
    		}
    		else{
    			sqlQuery += ", ESTADO = '" + sEstado + "'";
    			sqlQuery += " , OBSERVACIONES = NULL ";
    		}
    	}else{
    		String sObservaciones = ConexionWebService.getInstance().getEstadoEnvio();
    		sqlQuery += " , OBSERVACIONES = SUBSTR('" + sObservaciones + "',1,1000)";
    		if (!tieneProcesoActivo(sNovedadId))
    			sqlQuery += ", ESTADO = 'NO EXISTE EN LEGIS_PROCESOS'";
    		else if (sObservaciones.contains("El identificador de la actuaci�n no es v�lido."))
    			sqlQuery += ", ESTADO = 'ACTUACION_LEGIS_NO_EXISTE'";
    	}

    	String sJson = ConexionWebService.getInstance().getJson();
    	int maxLength = (sJson.length() < 4000)?sJson.length():4000;
    	String newJson = sJson.substring(0, maxLength);

    	sqlQuery += " , SJSON = '" + newJson + "'";

		sqlQuery += " where NOVEDAD_ID = " + sNovedadId;

       	ejecutarQuery("LEGIS_NOVEDADES", "actualizarEstadoTransmiteNovedad", sqlQuery);
    }

    private void actualizarEstadoTransmiteEliminaActuacion(String sNovedadId, String sJson){
    	String sqlQuery = "";
    	sqlQuery = " update LEGIS_NOVEDADES "
        + " set COD_ERROR_WS = " + ConexionWebService.getInstance().getCodError()
    	+ " , FECHA_TRANSMITE = sysdate ";
    	if (ConexionWebService.getInstance().getCodError()==200){
			sqlQuery += ", ESTADO = 'ACTUACION_ELIMINADA'";    
			sqlQuery += " , OBSERVACIONES = NULL ";
    	}else{
    		String sObservaciones = ConexionWebService.getInstance().getEstadoEnvio();
    		sqlQuery += " , OBSERVACIONES = SUBSTR('" + sObservaciones + "',1,1000)";
    		if (sObservaciones.contains("El identificador de la actuación no es válido."))
    			sqlQuery += ", ESTADO = 'ACTUACION_ELIMINAR_NO_EXISTE'";
    		else{
    			sqlQuery += ", ESTADO = 'ERROR_ELIMINA_ACTUACION'";
    		}
    	}
    	
    	int maxLength = (sJson.length() < 4000)?sJson.length():4000;
    	String newJson = sJson.substring(0, maxLength); 

    	sqlQuery += " , SJSON = '" + newJson + "'";
    		
		sqlQuery += " where NOVEDAD_ID = " + sNovedadId;   

       	ejecutarQuery("LEGIS_NOVEDADES", "actualizarEstadoTransmiteEliminaActuacion", sqlQuery);
    }
    
    private boolean tieneProcesoActivo(String sNovedadId) {

        String sqlQuery = "";
		Statement stLeer=null;
		ResultSet rsLeer=null;
	    Connection conn=null;

		try {
            sqlQuery = "select count(*) as q "
            		+ " from legis_novedades ln "
					+" where exists ("
					+"     select 1 from legis_procesos lp "
					+"     where LN.PROCESO_ID = Lp.PROCESO_ID "
					+ "      and Lp.PROCESO_ID_LEGIS = LN.CASO "
					+" )and ln.novedad_id = " + sNovedadId;


            conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);
			//UtilMensaje.getInstance().mensaje(sqlQuery);

            if (rsLeer.next()) {
            	int tieneProceso = rsLeer.getInt("q");
            	if (tieneProceso > 0)
            		return true;
            	return false;
            }

		}catch(SQLException sqlexception){
			UtilMensaje.getInstance().mensaje("Error tieneProcesoActivo: " + sqlexception.getErrorCode() + " - " +  sqlexception.getMessage()  + "   QUery: " + sqlQuery);
		}finally{
			try {
    			conn.close();
				rsLeer.close();
				stLeer.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		UtilMensaje.getInstance().mensaje("sinProcesoActivo: sNovedadId " + sNovedadId );
    	return false;
	}

	/*
     * @autor Jairo Vega
     * @version 02/01/2017
     */
    private void finalizarActuacionPorEnvioDoc(String sNovedadId, String sResumen, String sEstado){
    	String sqlQuery = "";

    	sqlQuery = " update LEGIS_NOVEDADES "
        + " set OBSERVACIONES = '" + sResumen + "'";
    	if (sEstado.equals("ok"))
    		sqlQuery += " , ESTADO = 'FINALIZA_DOCUMENTOS'";
    	sqlQuery += " where NOVEDAD_ID = " + sNovedadId;

       	ejecutarQuery("LEGIS_NOVEDADES", "finalizarActuacionPorEnvioDoc", sqlQuery);
    }

    /*
     * @autor C�sar Vega
     * Obtiene lista de Audiencias
     * @return lista
     * @version 29/12/2016
     */
	public ArrayList<Audiencia> getListaAudiencias(String sFiltrarPor, int pasada){
		ArrayList <Audiencia> lista = new ArrayList <Audiencia>();
	    Connection conn=null;
		Statement stLeer=null;
		ResultSet rsLeer=null;
        String sqlQuery = "";

        //sFiltrarPor = "15353087";

      //Este parche en este pasada es por error de marcara en el formato fecha
        UtilMensaje.getInstance().mensaje("ACTUACION_PROCESAL_ID " + sFiltrarPor + " pasada " + pasada);

		try{

			sqlQuery =
				  "SELECT A.ACTUACION_NOMBRE "
				+ " , DAP.DATO_NOMBRE DESCRIPCION";
			if (pasada == 0)
				sqlQuery += " , TO_CHAR (TO_date (dap.dato_valor, 'yyyy-mm-dd'),'yyyy-mm-dd') ";
			else
				sqlQuery += " , TO_CHAR (TO_date (dap.dato_valor, 'dd-mm-yyyy'),'yyyy-mm-dd') ";
			sqlQuery += " FROM datos_actuacion_procesal DAP"
				+ "   , actuaciones A "
				+ " WHERE dap.dato_nombre like '%FECHA%AUD%' "
				+ " AND A.ACTUACION_ID = DAP.ACTUACION_ID "
				+ " AND DAP.DATO_VALOR like '%-%'"
				+ " AND DAP.ACTUACION_PROCESAL_ID = " + sFiltrarPor
		+ " UNION "
				+  "SELECT A.ACTUACION_NOMBRE "
				+ " , DAP.DATO_NOMBRE DESCRIPCION"
				+ " , to_char(to_date(DAP.DATO_VALOR,'dd/mm/yyyy'),'yyyy-mm-dd') "
				+ " FROM datos_actuacion_procesal DAP"
				+ "   , actuaciones A "
				+ " WHERE dap.dato_nombre like '%FECHA%AUD%' "
				+ " AND A.ACTUACION_ID = DAP.ACTUACION_ID "
				+ " AND DAP.DATO_VALOR like '%/%' "
				+ " AND DAP.ACTUACION_PROCESAL_ID = " + sFiltrarPor;

			conn = pool.ds.getConnection();
			stLeer = conn.createStatement();
			rsLeer = stLeer.executeQuery(sqlQuery);

			while(rsLeer.next())
			{

				Audiencia audiencia =  new Audiencia();
				audiencia.setNombre(rsLeer.getString(1)); //ACTUACION_NOMBRE
				audiencia.setDescripcion(rsLeer.getString(2)); //DESCRIPCION
				audiencia.setFecha(rsLeer.getString(3)); //FECHA
				lista.add(audiencia);
			}
			/*if (lista.size()==0){
				Audiencia audiencia =  new Audiencia();
				lista.add(audiencia);
			}*/
			UtilMensaje.getInstance().mensaje("Total audiencias:  " + lista.size() + " ACTUACION_PROCESAL_ID " + sFiltrarPor);
		}
		catch(SQLException sqlexception)
		{
			error = "getListaAudiencias: " + sqlexception.getMessage()  + "   QUery: " + sqlQuery;
			UtilMensaje.getInstance().mensaje("Error: " + error);
		}
		catch(Exception exception)
		{
			error = "getListaAudiencias: " + exception.getMessage() + "   QUery: " + sqlQuery;
			UtilMensaje.getInstance().mensaje("Error: " + error);
			exception.printStackTrace();
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
    /*
     * @autor C�sar Vega
     * Obtiene lista de Audiencias
     * @return lista
     * @version 29/12/2016
     */
	public ArrayList<VencimientoTerminos> getVencimientoTerminos(String sFiltrarPor){
		ArrayList <VencimientoTerminos> lista = new ArrayList <VencimientoTerminos>();

			/*if (lista.size()==0){
				VencimientoTerminos vencimientos =  new VencimientoTerminos();
				lista.add(vencimientos);
			}*/

		return lista;
	}
	
	   public String getSecuencia(String sSecuencia)
	   {
		    Connection conn=null;
			Statement stLeer=null;
			ResultSet rsLeer=null;
			String sqlQuery="";
			String secuencia ="";
		   try{
		       sqlQuery = "SELECT " + sSecuencia + ".NEXTVAL SECUENCIA FROM DUAL";

				conn = pool.ds.getConnection();
				stLeer = conn.createStatement();
				rsLeer = stLeer.executeQuery(sqlQuery);

		       secuencia="";
		       if(rsLeer.next())
		           secuencia = rsLeer.getString("SECUENCIA");
		   }catch(SQLException sqlexception){
		   	UtilMensaje.getInstance().mensaje("getSecuencia " + sqlexception.getErrorCode() + " - " + sqlexception.getMessage() + " sqlQuery " + sqlQuery );
		   	secuencia = "error";
			}finally{
				try {
					rsLeer.close();
					stLeer.close();
				} catch (SQLException e) {
					UtilMensaje.getInstance().mensaje("getSecuencia: " + e.getMessage());
					e.printStackTrace();
				}
			}
		   UtilMensaje.getInstance().mensaje(sSecuencia + " " + secuencia);
		   return secuencia;
		}
	
}

