package liti.s3Legis;

import java.io.File;

public class DatosArchivo {
	private final static double fB = 1024.0;
	//private static double totalSize=0;
	String sFile = "";
	String tipoTamannio = "No definido";
	File fFile;
	double totalSize=0;
	String sEstado = "";


	public DatosArchivo(String sElFile){
		double megasEnBytes20=20971520;//Esta variable tambien esta en ArchivosUtil
		//double megasEnBytes60=62914560;
		sFile = sElFile;
		fFile = new File(sFile);

		try {
			if (!fFile.exists()) {
				sEstado="No existe";
			}else if (fFile.length() == 0){
				sEstado="Archivo vacio";
			}else{
				String datos[] = getFileSizeV (fFile.length());
				totalSize = Double.parseDouble(datos[0]);
				tipoTamannio = datos[1];

				if (fFile.length() > megasEnBytes20)
					sEstado="Supera 20 megas";
				else if (!sFile.toLowerCase().endsWith(".pdf"))
					sEstado="extension no es pdf";
				else
					sEstado="ok";
			}

			UtilMensaje.getInstance().mensaje("sElFile: " + sElFile + " sEstado: " + sEstado);
			//System.out.println("Destino: " + fileDestino.getName());

		} catch (Exception e) {
			sEstado = "Error: " + e;
		}
	}

    public String getTotalSize () {
        return getFileSize (totalSize);
    }


    public String getFileSize (double fL) {
            if (fL <= fB) {
                return String.valueOf(fL).concat(" bytes");
            } else {
                double sizeKB = getFileSizeInKB(fL);
                if(getFileSizeInKB(fL) <= fB)
                    return String.valueOf(sizeKB).concat(" KB");
                else {
                    double sizeMB = getFileSizeInMB(fL);
                    if(sizeMB <= fB)
                        return String.valueOf(sizeMB).concat(" MB");
                    else {
                        double sizeGB = getFileSizeInGB(fL);
                        if(sizeGB <= fB)
                            return String.valueOf(sizeGB).concat(" GB");
                        else {
                            double sizeTB = getFileSizeInTB(fL);
                            //if(sizeTB <= fB)
                                return String.valueOf(sizeTB).concat(" TB");
                        }
                    }
                }
            }
    }


    private String [] getFileSizeV (double fL) {
		String dato []=  new String [2];

        if (fL <= fB) {
        	dato [0] = String.valueOf(fL);
        	dato [1]= "Bytes";
            return dato;
        } else {
            double sizeKB = getFileSizeInKB(fL);
            if(getFileSizeInKB(fL) <= fB){
            	dato [0] = String.valueOf(sizeKB);
            	dato [1]= "KB";
                return dato;
            }
            else {
                double sizeMB = getFileSizeInMB(fL);
                if(sizeMB <= fB){
                	dato [0] = String.valueOf(sizeMB);
                	dato [1]= "MB";
                    return dato;
                }
                else {
                    double sizeGB = getFileSizeInGB(fL);
                    if(sizeGB <= fB){
                    	dato [0] = String.valueOf(sizeGB);
                    	dato [1]= "GB";
                        return dato;
                    }
                    else {
                        double sizeTB = getFileSizeInTB(fL);
                        //if(sizeTB <= fB)
                    	dato [0] = String.valueOf(sizeTB);
                    	dato [1]= "TB";
                        return dato;
                    }
                }
            }
        }
    }


    private static double getFileSizeInKB (double f) {
        f = (f/fB);
        int fs = (int) Math.pow(10,2);
        return Math.rint(f*fs)/fs;
    }

    private static double getFileSizeInMB (double f) {
        f = f / Math.pow(fB,2);
        int fs = (int) Math.pow(10,2);
        return Math.rint(f*fs)/fs;
    }

    private static double getFileSizeInGB (double f) {
        f = f / Math.pow(fB,3);
        int fs = (int) Math.pow(10,2);
        return Math.rint(f*fs)/fs;
    }

    private static double getFileSizeInTB (double f) {
        f = f / Math.pow(fB,4);
        int fs = (int) Math.pow(10,2);
        return Math.rint(f*fs)/fs;
    }
}
