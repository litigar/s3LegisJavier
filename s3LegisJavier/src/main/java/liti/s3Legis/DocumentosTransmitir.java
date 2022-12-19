package liti.s3Legis;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DocumentosTransmitir {
	public void transmitirDocumentos(String sProcesoEnEjecucion) {
        ArrayList<HiloDatosDocumento> listaDocs  = new ArrayList<HiloDatosDocumento>();
        
		int encolados= 0;	
		int contador = 0; 
		//int cantidadRegistros = 100; /*Para el select de pendientes*/
		String cantidadRegistros = ClassPropertiesLegis.getInstance().cantidadDocumentosTransmitir;
		//int cantidadRegistros = 1000; /*Para el select de pendientes*/
		//int topePdfs = 25;
		int topeDocumentos = ClassPropertiesLegis.getInstance().cantidadDocumentosLista;
		//int topePdfs = 20;
		int numeroCola = 1;
		
		long init = System.currentTimeMillis();
		
		//int cantidadPool = 5;/*Conexiones del pool*/
		int cantidadPool = ClassPropertiesLegis.getInstance().cantidadPool;
		
		ExecutorService executor = Executors.newFixedThreadPool(cantidadPool);
		while (listaDocs.size() == 0 && contador < 3 ){
			UtilMensaje.getInstance().mensaje("===== Ejecucion [" + ( contador + 1 )+ "]========");
			contador++;
	        listaDocs = ConnectOracle.getInstance().getListaDocumentosTx(sProcesoEnEjecucion, cantidadRegistros); 
			if (listaDocs.size()==0){
				UtilMensaje.getInstance().mensaje(" No existen registros pendientes de procesar");
			}else{
				UtilMensaje.getInstance().mensaje("Se van a procesar (" + listaDocs.size() + ") documentos");
				ArrayList<HiloDatosDocumento> listaHilo = new ArrayList<HiloDatosDocumento>();
				//contador=0;
				int cantidadPdfs = 0;
				for(int i=0; i< listaDocs.size();i++){
					encolados++;
					UtilMensaje.getInstance().mensaje("\nEncolando (" + numeroCola  + ")--------");
					if (cantidadPdfs < topeDocumentos){
						HiloDatosDocumento da = listaDocs.get(i);
						UtilMensaje.getInstance().mensaje("Cola: "+ numeroCola + " posicion (" + (i + 1) + ") -> documentoId: " + da.getDocumentoId() + " novedad_id: " + da.getNovedadId() + " actProcId: " + da.getActuacionProcesalId());
						listaHilo.add(da); 
						cantidadPdfs++;
					}else{
						cantidadPdfs=0;
						numeroCola++;
						listaHilo.add(listaDocs.get(i));
						UtilMensaje.getInstance().mensaje("Cola "+ numeroCola + " posicion (" + (i + 1) + ") -> documentoId " + listaDocs.get(i).getDocumentoId() + " novedad_id " + listaDocs.get(i).getNovedadId() + " actProcId: " + listaDocs.get(i).getActuacionProcesalId());
						UtilMensaje.getInstance().mensaje("\nEjecutar (" + (numeroCola) + ") ---- tamanio (" + listaHilo.size() + ")--------");
						Runnable hilo = new HiloDocumentosEjecutar (sProcesoEnEjecucion, listaHilo);
				        executor.execute(hilo);
				        listaHilo = new ArrayList<HiloDatosDocumento>();
					}
					
				}
				if (listaHilo.size()>0){
					numeroCola++;
					UtilMensaje.getInstance().mensaje("\nEjecutar ultimo ---- tamanio (" + listaHilo.size() + ")--------");
					Runnable hilo = new HiloDocumentosEjecutar (sProcesoEnEjecucion, listaHilo);
			        executor.execute(hilo);
				}
		        listaHilo = new ArrayList<HiloDatosDocumento>();
		        listaDocs = new ArrayList<HiloDatosDocumento>();	
			}
			esperarXsegundos(20);
			UtilMensaje.getInstance().mensaje("encolados: " + encolados + " *********************");
		}
		/*---------------------------*/
	    executor.shutdown();	// Cierro el Executor
	    while (!executor.isTerminated()) {
	    	// Espero a que terminen de ejecutarse todos los procesos 
	    	// para pasar a las siguientes instrucciones 
	   }

		long fin = System.currentTimeMillis();
		UtilMensaje.getInstance().mensaje("FIN HILOS CON EXITO. Con duración: " +(fin-init)/1000+" Segundos " + ((fin-init)/1000)/60+" Minutos" );
		/*---------------------------*/
		//correo_id, destinatario, asunto, mensaje, adjuntos_ruta, adjuntos_cantidad
		//return true; 
    }

	
	
	private static void esperarXsegundos(int segundos) {
		try {
			Thread.sleep(segundos * 1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}		

}
