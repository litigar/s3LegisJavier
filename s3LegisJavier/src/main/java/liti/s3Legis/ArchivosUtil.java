package liti.s3Legis;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class ArchivosUtil {
	private static ArchivosUtil instance;
	private double megasEnBytes20=20971520;//Esta variable tambien esta en DatosArchivo

	public ArchivosUtil() {
		// TODO Auto-generated constructor stub
	}

    public static ArchivosUtil getInstance(){
    	if (instance==null)
    		instance = new ArchivosUtil();
    	return instance;
    }

	public String moverArchivo(String  sOrigen,String sDestino){
		String mensaje = "";
		UtilMensaje.getInstance().mensaje("Moviendo " +  sOrigen + " to " + sDestino);
		File fOrigen = new File(sOrigen);
		File fDestino = new File(sDestino);
		UtilMensaje.getInstance().mensaje("Origen tama�o " + fOrigen.length());
		UtilMensaje.getInstance().mensaje("Destino tama�o " + fDestino.length());
		if (fDestino.length()> 0){
			UtilMensaje.getInstance().mensaje("Existe archivo en el destino con un tama�o " + fDestino.length());
			if (fDestino.length()>= fOrigen.length()){
				UtilMensaje.getInstance().mensaje("Se borra el origen por ser el destino de mayor o igual tama�o");
				return borrarArchivo(sOrigen);
			}else{
			   UtilMensaje.getInstance().mensaje("por lo cual se borra el destino por ser de menor tama�o");
			   borrarArchivo(sDestino);
			}
		}
		if (fDestino.exists() && fDestino.length()==0){
			UtilMensaje.getInstance().mensaje("Existe archivo en el destino con un tama�o " + fDestino.length() + " por lo cual ser� borrado, para que sea nuevamente copiado");
			borrarArchivo(sDestino);
		}
		if (!fOrigen.exists() ){
			UtilMensaje.getInstance().mensaje("Error, no existe el archivo origen " + sOrigen);
			return "Error archivo " + sOrigen + " vacio";
		}
		if ( fOrigen.length()==0){
			UtilMensaje.getInstance().mensaje("Existe archivo en el origen con un tama�o " + fOrigen.length());
			return "Error archivo " + sOrigen + " vacio";
		}
		try {
			fOrigen.setWritable(true);
			fDestino.setWritable(true);
			FileUtils.moveFile(fOrigen, fDestino);
			File verificarDestino = new File(sDestino);
			if (verificarDestino.length()==0){
				borrarArchivo(sDestino);
				mensaje = "Error, destino quedo en cero";
			}
			else{
				borrarArchivo(sOrigen);
				mensaje = "ok";
			}
		} catch (IOException e) {
				//e.printStackTrace();
				mensaje = "Error moverArchivo " + sOrigen + " a " + sDestino + ". " + e.getMessage();
				if (e.getMessage().toLowerCase().contains("already exists")){
					mensaje = "ok";
				}
		}
		mensaje = mensaje.replace("'", "");
		UtilMensaje.getInstance().mensaje("Mover Archivo " + mensaje);
		return mensaje;
	}

	public String copiarArchivo(String  origen,String destino){
		String mensaje = "";
		UtilMensaje.getInstance().mensaje("Preparandose para traer a servidores liti " +  origen + " to " + destino);
		File antiguo = new File(origen);
		File nuevo = new File(destino);
		UtilMensaje.getInstance().mensaje("Origen " +  origen + " (" + antiguo.length() + ")");
		UtilMensaje.getInstance().mensaje("Destino " +  destino + " (" + nuevo.length() + ")");
		if (!antiguo.exists() || antiguo.length() == 0){
			mensaje = "Origen " +  origen + " (" + antiguo.length() + ") Invalido";
			UtilMensaje.getInstance().mensaje(mensaje);
			return mensaje;
		}

		if (nuevo.exists()){
			if  (nuevo.length() > 0){
				if (antiguo.length() == nuevo.length()){
					mensaje = "El origen " +  origen + " (" + antiguo.length() + ") y el destino " + destino + " (" + nuevo.length() + "), son del mismo tama�o. ";
					UtilMensaje.getInstance().mensaje(mensaje);
					mensaje = "ok";
					if (nuevo.length() > megasEnBytes20) {
						mensaje= destino + " Supera 20 megas";
						UtilMensaje.getInstance().mensaje(mensaje);
					}
					return mensaje;
				}else{
					mensaje = "El origen " +  origen + " (" + antiguo.length() + ") y el destino " + destino + " (" + nuevo.length() + ") son de diferente tama�o, se sobreescribe el destino. ";
					UtilMensaje.getInstance().mensaje(mensaje);
					borrarArchivo(destino);
				}
			}else{
				mensaje = "El destino " + destino + " (" + nuevo.length() + "), tiene longitud cero. Se sobre escribe eldetino.";
				UtilMensaje.getInstance().mensaje(mensaje);
				borrarArchivo(destino);
			}
		}


		try {
			antiguo.setWritable(true);
			nuevo.setWritable(true);
			for (int intentos = 0;  intentos < 3 ; intentos++){
				FileUtils.copyFile(antiguo, nuevo);
				File verificarDestino = new File(destino);
				if (verificarDestino.length()==0){
					borrarArchivo(destino);
					mensaje = "Error, destino quedo en cero. Intentos de copia: " + intentos + 1;
				}else{
					if ( antiguo.length() == verificarDestino.length()){
						mensaje = "ok";
						intentos = 3;
						if (nuevo.length() > megasEnBytes20) {
							mensaje= verificarDestino + " Supera 20 megas";
							UtilMensaje.getInstance().mensaje(mensaje);
						}
					}
					else{
						borrarArchivo(destino);
						mensaje = "Origen "+ antiguo.length() + " y destino " + verificarDestino.length() + " con tama�os diferentes";

					}
				}

			}

		} catch (IOException e) {
				//e.printStackTrace();
				mensaje = "Error copiarArchivo " + origen + " a " + destino + ". " + e.getMessage();
				if (e.getMessage().toLowerCase().contains("already exists"))
					mensaje = "ok";
		}
		mensaje = mensaje.replace("'", "");
		UtilMensaje.getInstance().mensaje(mensaje);
		return mensaje;
	}


	public String borrarArchivo(String  sFile){
		UtilMensaje.getInstance().mensaje("Borrando " +  sFile);
		File sFileBorrar = new File(sFile);
		String mensaje = "";

		if (sFileBorrar.exists()) {
			try {
				FileUtils.forceDelete(sFileBorrar);
				mensaje = "ok";
			} catch (IOException e) {
				mensaje = "Error borrarArchivo " + e.getMessage();
			}
		}else
			mensaje = "ok";

		mensaje = mensaje.replace("'", "");
		UtilMensaje.getInstance().mensaje("Borrado archivo " + mensaje);
		return mensaje;
	}

	public boolean existeArchivo(String  origen){
		File antiguo = new File(origen);
		
		if (!antiguo.exists() || antiguo.length() == 0){
			String mensaje = "Error, Archivo: " +  origen + " tamannio (" + antiguo.length() + ") No existe";
			UtilMensaje.getInstance().mensaje(mensaje);
			return false;
		}
		UtilMensaje.getInstance().mensaje("Archivo: " + origen + "(" + antiguo.length() + ")");
		return true;
	}
	
	
	public String quitarCaracteres (String cadena ){
		String sCadena = cadena.replace(":", "");
		return sCadena;
	}
}
