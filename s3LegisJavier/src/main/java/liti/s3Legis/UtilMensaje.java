package liti.s3Legis;

import java.io.*;
import java.sql.Timestamp;
import java.util.Date;

public class UtilMensaje {
	private static UtilMensaje instance;

	private String nameFile="";
	private String fLog="";
	private FileWriter fw = null;
	private PrintWriter pw = null;

    public static UtilMensaje getInstance(){
    	if (instance==null)
    		instance = new UtilMensaje();
    	return instance;
    }

    public void setNameFileHHMM(String sNameFile){
    	nameFile = sNameFile;
		fLog=ClassPropertiesUtil.getInstance().pathLogsBatch + nameFile+getFecha()+ "_" + getHHMM() + ".log";
    }

    public void setNameFileAdmin(String sNameFile){
    	nameFile = sNameFile;
		fLog=ClassPropertiesUtil.getInstance().pathLogsBatch + nameFile+getFecha() + ".log";
		System.out.println(fLog);
    }

    public String getNameFile(){
    	return "File Name : (" + nameFile + ") fLog (" + fLog + ")";
    }

    public String getHHMM(){
		Timestamp ts = new Timestamp(new Date().getTime());
		return ts.toString().substring(11, 13) + "-" + ts.toString().substring(14, 16);
	}

	public boolean preparaLogFile(){
    	if (nameFile.equals("")||fLog.equals("")){
    		System.out.println("No se esta colocando el nombre del archivo log");
            closeAndExit();
    	}
        try {
			fw = new FileWriter(fLog, true);
	        pw = new PrintWriter(fw);
	        return true;
		} catch (IOException e1) {
			e1.printStackTrace();
        } catch (Exception e2) {
	        e2.printStackTrace();
	    }
        closeAndExit();
        return false;
	}

	public void closeAndExit(){
		UtilMensaje.getInstance().mensaje("Fin Proceso");
        try {
        // Nuevamente aprovechamos el finally para
        // asegurarnos que se cierra el fichero.
        	if (null != fw)
        		fw.close();
        } catch (Exception e3) {
           e3.printStackTrace();
        }
		System.exit(0);
	}

	public void close(){
        try {
        	if (null != fw)
        		fw.close();
        } catch (Exception e3) {
           e3.printStackTrace();
        }
	}
	private String getFecha (){
		Timestamp ts = new Timestamp(new Date().getTime());
		return ts.toString().substring(0, 10);
	}
	private String getFechaHH (){
		Timestamp ts = new Timestamp(new Date().getTime());
		return ts.toString();
	}
	public void mensaje(String msg){
		System.out.println(getFechaHH() + " " + nameFile + " " + msg);
		mensajeW(msg);
	}

	private void mensajeW(String msg){
		preparaLogFile();
		pw.println(getFechaHH() + " " + msg);
		close();
	}
	public static void main(String[] args) {
		UtilMensaje.getInstance().nameFile="prueba";
		UtilMensaje.getInstance().preparaLogFile();
		UtilMensaje.getInstance().mensaje("esta es una prueba de archivos");
		UtilMensaje.getInstance().mensaje("fin de prueba ");
		UtilMensaje.getInstance().closeAndExit();
	}


}
