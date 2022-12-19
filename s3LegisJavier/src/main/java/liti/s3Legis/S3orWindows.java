package liti.s3Legis;

public class S3orWindows {
	private String sY = "Y:/";
	
	S3orWindows(){
		
	}
	
	public String copiarArchivo(String origen, String destino){
		
		if (origen.contains(sY)){
			if (destino.contains(sY)){
				//Origen y destino son unidad Y
				UtilMensaje.getInstance().mensaje("S3orWindows copiarArchivo-copyFileAws origenY: " + origen + " to destinoY " + destino);
				if ( AmazonS3ObjectOperation.getInstance().copyFileAws(origen.replaceAll(sY, ""), destino.replaceAll(sY, ""))){
					return "ok";
				}
			}else{
				//Origen es unidad Y, destino es unidad Windows
				UtilMensaje.getInstance().mensaje("S3orWindows copiarArchivo-downloadFileAwsToWindows: origenY " + origen + " to destinoW " + destino);
				return AmazonS3ObjectOperation.getInstance().downloadFileAwsToWindows(origen.replaceAll(sY, ""), destino, origen);
			}
		}else{
			if (destino.contains(sY)){
				//Origen es windows, destino es unidad Y
				UtilMensaje.getInstance().mensaje("S3orWindows copiarArchivo-uploadFileWindowsToAws: origenW " + origen + " to destinoY " + destino);
				if (AmazonS3ObjectOperation.getInstance().uploadFileWindowsToAws(origen, destino.replaceAll(sY, ""), destino)){
					return "ok";
				}
			}else{
				//Origen y destino son unidad windows
				UtilMensaje.getInstance().mensaje("S3orWindows copiarArchivo-UtilcopiarArchivo: origenW " + origen + " to destinoW " + destino);
				return ArchivosUtil.getInstance().copiarArchivo(origen, destino);
			}
			
		}
		return "S3orWindows - Error al copiar " + origen + " en " + destino;
	}

}
