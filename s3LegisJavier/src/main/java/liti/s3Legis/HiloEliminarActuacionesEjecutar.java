package liti.s3Legis;

import java.util.ArrayList;

public class HiloEliminarActuacionesEjecutar implements Runnable{
	ArrayList<HiloDatosActuacionEliminar> listaDocumentos  = new ArrayList<HiloDatosActuacionEliminar>();
	String sProcesoEnEjecucion = "" ;



	public HiloEliminarActuacionesEjecutar(String sProcesoEnEjecucion, ArrayList<HiloDatosActuacionEliminar> listaHilo) {
		listaDocumentos = listaHilo;
		this.sProcesoEnEjecucion = sProcesoEnEjecucion;
	}


	public void run() {
		long init = System.currentTimeMillis();
		UtilMensaje.getInstance().mensaje("Inicia Hilo " + Thread.currentThread().getName());
		int procesados = 0;
		for (int i = 0; i < listaDocumentos.size(); i++) {
			procesados++;
			UtilMensaje.getInstance().mensaje("(" + listaDocumentos.size() + "/" +procesados+ ") Hilo " + Thread.currentThread().getName() );
			ConnectOracle.getInstance().actualizaEstadoProcesoEnEjecucion(this.sProcesoEnEjecucion, "S");
			HiloDatosActuacionEliminar dd = listaDocumentos.get(i);
       		ConnectOracle.getInstance().transmitirActuacionesEliminarLegis(this.sProcesoEnEjecucion, dd);
            //UtilMensaje.getInstance().mensaje("linea - 6");            
        	
        	//conn.commit();

		}
		UtilMensaje.getInstance().mensaje("Procesados: " + procesados);
		

		long fin = System.currentTimeMillis();
		UtilMensaje.getInstance().mensaje("Termina Hilo " + Thread.currentThread().getName()
				+ " Duraci�n: " + (fin - init) / 1000 + " Segundos " 
				+ ((fin - init) / 1000) / 60 + " Minutos ");
	}
}
