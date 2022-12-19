package liti.s3Legis;

import java.sql.Timestamp;
import java.util.Date;

public class AlertaLegisDato {
	String sEstado = "";
	String sFechaTransmite = ""; 
	String sDia = "";
	int iCantidad = 0;
	String sObservaciones = "";
	String sFechaAhora =getFechaHH ();
	
	
	private String getFechaHH (){
		Timestamp ts = new Timestamp(new Date().getTime());
		return ts.toString();
	}
	
}
