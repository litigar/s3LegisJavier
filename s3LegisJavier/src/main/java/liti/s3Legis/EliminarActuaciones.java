package liti.s3Legis;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class EliminarActuaciones {
	public void eliminarActuaciones(String sProcesoEnEjecucion) {
        ArrayList<HiloDatosActuacionEliminar> listaActs  = new ArrayList<HiloDatosActuacionEliminar>();
        
		int encolados= 0;	
		int contador = 0; 
		//int cantidadRegistros = 100; /*Para el select de pendientes*/
		String cantidadRegistros = ClassPropertiesLegis.getInstance().cantidadActuacionesEliminarTransmitir;
		//int cantidadRegistros = 1000; /*Para el select de pendientes*/
		//int topePdfs = 25;
		int topeActuaciones = ClassPropertiesLegis.getInstance().cantidadActuacionesEliminarLista;
		//int topePdfs = 20;
		int numeroCola = 1;
		
		long init = System.currentTimeMillis();
		
		//int cantidadPool = 5;/*Conexiones del pool*/
		int cantidadPool = ClassPropertiesLegis.getInstance().cantidadPool;
		
		ExecutorService executor = Executors.newFixedThreadPool(cantidadPool);
		while (listaActs.size() == 0 && contador < 3 ){
			UtilMensaje.getInstance().mensaje("===== Ejecucion [" + ( contador + 1 )+ "]========");
			contador++;
	        //listaActs = ConnectOracle.getInstance().getListaDocumentosTx(sProcesoEnEjecucion, cantidadRegistros); 
	        listaActs = ConnectOracle.getInstance().getListaActuacionesEliminar(sProcesoEnEjecucion, cantidadRegistros); 
			if (listaActs.size()==0){
				UtilMensaje.getInstance().mensaje(" No existen registros pendientes de procesar");
			}else{
				UtilMensaje.getInstance().mensaje("Se van a procesar (" + listaActs.size() + ") documentos");
				ArrayList<HiloDatosActuacionEliminar> listaHilo = new ArrayList<HiloDatosActuacionEliminar>();
				//contador=0;
				int cantidadPdfs = 0;
				for(int i=0; i< listaActs.size();i++){
					encolados++;
					UtilMensaje.getInstance().mensaje("\nEncolando (" + numeroCola  + ")--------");
					if (cantidadPdfs < topeActuaciones){
						HiloDatosActuacionEliminar da = listaActs.get(i);
						UtilMensaje.getInstance().mensaje("Cola: "+ numeroCola + " posicion (" + (i + 1) + ") -> novedad_id: " + da.getNovedadId() + " actProcLegisId: " + da.getActuacionLegisId());
						listaHilo.add(da); 
						cantidadPdfs++;
					}else{
						cantidadPdfs=0;
						numeroCola++;
						listaHilo.add(listaActs.get(i));
						UtilMensaje.getInstance().mensaje("Cola "+ numeroCola + " posicion (" + (i + 1) + ") -> novedad_id: " + listaActs.get(i).getNovedadId() + " actProcLegisId: " + listaActs.get(i).getActuacionLegisId());
						UtilMensaje.getInstance().mensaje("\nEjecutar (" + (numeroCola) + ") ---- tamanio (" + listaHilo.size() + ")--------");
						Runnable hilo = new HiloEliminarActuacionesEjecutar (sProcesoEnEjecucion, listaHilo);
				        executor.execute(hilo);
				        listaHilo = new ArrayList<HiloDatosActuacionEliminar>();
					}
					
				}
				if (listaHilo.size()>0){
					numeroCola++;
					UtilMensaje.getInstance().mensaje("\nEjecutar ultimo ---- tamanio (" + listaHilo.size() + ")--------");
					Runnable hilo = new HiloEliminarActuacionesEjecutar (sProcesoEnEjecucion, listaHilo);
			        executor.execute(hilo);
				}
		        listaHilo = new ArrayList<HiloDatosActuacionEliminar>();
		        listaActs = new ArrayList<HiloDatosActuacionEliminar>();	
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
