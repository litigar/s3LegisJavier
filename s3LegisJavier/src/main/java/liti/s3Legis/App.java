package liti.s3Legis;

//import Liti.Legis.ReadSystemProperties;
import java.util.Properties;

public class App {

	static String sProcesoEnEjecucion = "";//Para que se ejecute una sola vez el proceso y no se pisen las ejecuciones
	static Properties prop = new Properties();


	/*
	 * @autor Jairo Vega
	 * @param arg[0] nombre del proceso a ser ejecutado
	 * @param arg[1] fecha de la novedad
	 * @return void
	 * @version 18/12/2016
	 */
	public static void main(String[] args) {

		String pchanged_time = "";
		UtilMensaje.getInstance().setNameFileAdmin("legis_s3_jav_" + ReadSystemProperties.getInstance().getPropertiesComputerName()  + "_" + System.getenv().get("USERNAME") + "_" + args[0] + "_");
		if (!UtilMensaje.getInstance().preparaLogFile())
			System.exit(0);

		pchanged_time = ConnectOracle.getInstance().obtieneFecha();

		UtilMensaje.getInstance().mensaje("========================================================================");
		UtilMensaje.getInstance().mensaje("=============== 20210817 - PARAMETRIZACION MASIVA LEGIS  ===============");
		UtilMensaje.getInstance().mensaje("=== 20220508 - NUEVA PARAMETRIZACION LEGIS TODOS LOS TIPOS PROCESOS ====");
		UtilMensaje.getInstance().mensaje("=== 20220708 - TITULO HOMOLOGADO 10 PALABRAS ====");
		UtilMensaje.getInstance().mensaje("=== 20221128 - TITULO HOMOLOGADO FIDUPREVISORA ====");
		UtilMensaje.getInstance().mensaje("=== 20221209 - Se toma ver de prod y se quita TNT ====");
		UtilMensaje.getInstance().mensaje("=== 20221216 - Se envía correo de alerta para novedades - Hoy ====");
		UtilMensaje.getInstance().mensaje("=== 20221219 - Se envía correo de alerta para novedades - Listado 8 dias ====");
		UtilMensaje.getInstance().mensaje("========================================================================");


		UtilMensaje.getInstance().mensaje("Inicio Proceso Legis [" + args[0] + "]");


		//LEGIS_NOVEDADES se inicia por:
		//1 - Trigger de procesos_clientes
		//2 - seleccionarActuaciones

		//SELECCIONAR_ACTUACIONES AM

		sProcesoEnEjecucion = "SOLO_SELECCIONAR";// 010 - Selecciona actuaciones a transmitir
		if (args[0].equals(sProcesoEnEjecucion)){
			UtilMensaje.getInstance().mensaje("Inicio Proceso Legis [" + args[0] + "] horario [" + args[1] + "]");
			ConnectOracle.getInstance().verificarProcesoEnEjecucion(sProcesoEnEjecucion);//Si lleva mas de 45 en ejecucion, cambia el indicador a No ejecucion
			if(ConnectOracle.getInstance().isProcesoEnEjecucion(sProcesoEnEjecucion).equals("N")){
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");

				UtilMensaje.getInstance().mensaje("============================= seleccionarActuaciones " + args[1] + "===================================");
			    ConnectOracle.getInstance().seleccionarActuaciones(sProcesoEnEjecucion, pchanged_time, args[1]); //Inserta actuaciones en legis_novedades

				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "N");
			}
		}

		sProcesoEnEjecucion = "SELECCIONAR_ACTUACIONES";// 010 - Selecciona actuaciones a transmitir
		if (args[0].equals(sProcesoEnEjecucion)){
			UtilMensaje.getInstance().mensaje("Inicio Proceso Legis [" + args[0] + "] horario [" + args[1] + "]");
			ConnectOracle.getInstance().verificarProcesoEnEjecucion(sProcesoEnEjecucion);//Si lleva mas de 45 en ejecucion, cambia el indicador a No ejecucion
			if(ConnectOracle.getInstance().isProcesoEnEjecucion(sProcesoEnEjecucion).equals("N")){
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");

				UtilMensaje.getInstance().mensaje("============================= seleccionarActuaciones " + args[1] + "===================================");
			    ConnectOracle.getInstance().seleccionarActuaciones(sProcesoEnEjecucion, pchanged_time, args[1]); //Inserta actuaciones en legis_novedades
				UtilMensaje.getInstance().mensaje("============================= prepararNovedadesYactuaciones " + args[1] + "===================================");
		        ConnectOracle.getInstance().prepararNovedadesYactuaciones(sProcesoEnEjecucion); //Llena datos faltantes LEGIS_NOVEDADES
				UtilMensaje.getInstance().mensaje("============================= transmitirNovedadesYactuaciones " + args[1] + "===================================");
			    ConnectOracle.getInstance().transmitirNovedadesYactuaciones(sProcesoEnEjecucion); //Envia novedad a legis

				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "N");
			}
		}


		sProcesoEnEjecucion = "PREPARAR_ACTUACIONES";//011 - Complementa datos que rquiere legis -> Ingreso, Retiros, Actuaciones
		if (args[0].equals(sProcesoEnEjecucion)){
			ConnectOracle.getInstance().verificarProcesoEnEjecucion(sProcesoEnEjecucion);//Si lleva mas de 45 en ejecucion, cambia el indicador a No ejecucion
			if(ConnectOracle.getInstance().isProcesoEnEjecucion(sProcesoEnEjecucion).equals("N")){
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");

				ConnectOracle.getInstance().prepararNovedadesYactuaciones(sProcesoEnEjecucion); //Llena datos faltantes LEGIS_NOVEDADES

				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "N");
			}
		}

		sProcesoEnEjecucion = "TRANSMITIR_ACTUACIONES";// 012 - transmite actuaciones y actuaciones
		if (args[0].equals(sProcesoEnEjecucion)){
			ConnectOracle.getInstance().verificarProcesoEnEjecucion(sProcesoEnEjecucion);//Si lleva mas de 45 en ejecucion, cambia el indicador a No ejecucion
			if(ConnectOracle.getInstance().isProcesoEnEjecucion(sProcesoEnEjecucion).equals("N")){
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");

				ConnectOracle.getInstance().transmitirNovedadesYactuaciones(sProcesoEnEjecucion); //Envia novedad a legis

				/*UtilMensaje.getInstance().mensaje("================================================================");
				UtilMensaje.getInstance().mensaje("Inicio Proceso Legis [TRANSMITIR_DOCUMENTOS");

				ConnectOracle.getInstance().transmitirDocumentos(sProcesoEnEjecucion); //Envia novedad a legis
				*/
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "N");
			}
		}

		sProcesoEnEjecucion = "PREPARAR_DOCUMENTOS";// 020 - Prepara documentos pdf (autos y audiencias) de actuaciones
		if (args[0].equals(sProcesoEnEjecucion)){
			UtilMensaje.getInstance().mensaje("Inicio Proceso Legis [" + args[0] + "] dias [" + args[1] + "]");
			ConnectOracle.getInstance().verificarProcesoEnEjecucion(sProcesoEnEjecucion);//Si lleva mas de 45 en ejecucion, cambia el indicador a No ejecucion
			if(ConnectOracle.getInstance().isProcesoEnEjecucion(sProcesoEnEjecucion).equals("N")){
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");

				ConnectOracle.getInstance().prepararDocumentos(sProcesoEnEjecucion, args[1]); //Inserta en tabla legis_documentos

				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "N");
			}
		}

		sProcesoEnEjecucion = "ERROR_DOCUMENTOS";// 021 - Prepara documentos pdf (autos y audiencias) de actuaciones
		if (args[0].equals(sProcesoEnEjecucion)){
			ConnectOracle.getInstance().verificarProcesoEnEjecucion(sProcesoEnEjecucion);//Si lleva mas de 45 en ejecucion, cambia el indicador a No ejecucion
			if(ConnectOracle.getInstance().isProcesoEnEjecucion(sProcesoEnEjecucion).equals("N")){
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");

				ConnectOracle.getInstance().verificarDocumentosEnError(sProcesoEnEjecucion); //Inserta en tabla legis_documentos

				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "N");
			}
		}
		sProcesoEnEjecucion = "TRANSMITIR_DOCUMENTOS";// 022 - transmite documentos pdf a legis
		
		//ConnectOracle.getInstance().transmitirDocumentos(sProcesoEnEjecucion); //Envia documentos a legis
		
		if (args[0].equals(sProcesoEnEjecucion)){
			ConnectOracle.getInstance().verificarProcesoEnEjecucion(sProcesoEnEjecucion);//Si lleva mas de 45 en ejecucion, cambia el indicador a No ejecucion
			if(ConnectOracle.getInstance().isProcesoEnEjecucion(sProcesoEnEjecucion).equals("N")){
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");

				ConnectOracle.getInstance().transmitirDocumentos(sProcesoEnEjecucion); //Envia documentos a legis

				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "N");
			}
		}

		sProcesoEnEjecucion = "ENVIO_MANUAL_DOCUMENTOS";// 023 - Genera documentos con novedad_id.pdf para enviarlos a legis
		if (args[0].equals(sProcesoEnEjecucion)){
			ConnectOracle.getInstance().verificarProcesoEnEjecucion(sProcesoEnEjecucion);//Si lleva mas de 45 en ejecucion, cambia el indicador a No ejecucion
			if(ConnectOracle.getInstance().isProcesoEnEjecucion(sProcesoEnEjecucion).equals("N")){
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");

				ConnectOracle.getInstance().envioManualDeDocumentos(sProcesoEnEjecucion); //Envia documentos a legis

				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "N");
			}
		}

		sProcesoEnEjecucion = "ELIMINAR_ACTUACIONES";// 030 - EliminarActuaciones
		if (args[0].equals(sProcesoEnEjecucion)){
			UtilMensaje.getInstance().mensaje("Inicio Proceso Legis [" + args[0] + "]");
			ConnectOracle.getInstance().verificarProcesoEnEjecucion(sProcesoEnEjecucion);//Si lleva mas de 45 en ejecucion, cambia el indicador a No ejecucion
			if(ConnectOracle.getInstance().isProcesoEnEjecucion(sProcesoEnEjecucion).equals("N")){
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "S");
				
				UtilMensaje.getInstance().mensaje("============================= eliminarActuaciones " + args[0] + "===================================");
				EliminarActuaciones dt = new EliminarActuaciones();
				dt.eliminarActuaciones(sProcesoEnEjecucion); //Envia actuaciones a eliminar a la api de legis
					
				ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(sProcesoEnEjecucion, "N");
			}
		}

		sProcesoEnEjecucion = "ALERTA_NOVEDADES";// 040 - 
		if (args[0].equals(sProcesoEnEjecucion)){
			UtilMensaje.getInstance().mensaje("============================= Alertas " + args[0] + "===================================");
			AlertasLegis al = new AlertasLegis();
			al.generarAlertaNovedades(); //
					
		}

		sProcesoEnEjecucion = "ALERTA_DOCUMENTOS";// 041 - 
		if (args[0].equals(sProcesoEnEjecucion)){
			UtilMensaje.getInstance().mensaje("============================= Alertas " + args[0] + "===================================");
			AlertasLegis al = new AlertasLegis();
			al.generarAlertaDocumentos(); //
		}
					
		UtilMensaje.getInstance().mensaje("Proceso " + args[0] + " terminado.");
		UtilMensaje.getInstance().closeAndExit();
	}
}
