package liti.s3Legis;

//import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.OutputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
//import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
//import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class AmazonS3ObjectOperation
{
	private static AmazonS3ObjectOperation instance;
    private String bucketName;
    private String accessKey;
    private String secretAccessKey;
    final AmazonS3 s3Client;

    public static AmazonS3ObjectOperation getInstance(){
    	if (instance==null)
    		instance = new AmazonS3ObjectOperation();
    	return instance;
    }
    
    private AmazonS3ObjectOperation() {
        this.bucketName = ClassPropertiesAmazon.getInstance().bucketName;
        this.accessKey = ClassPropertiesAmazon.getInstance().access_key_id;
        this.secretAccessKey = ClassPropertiesAmazon.getInstance().secret_access_key;
        this.s3Client = generateS3Client();
    }
    
    private AmazonS3 generateS3Client() {
    	BasicAWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretAccessKey);
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
        return s3client;
    }    
    
    private InputStream downloadFileFromS3Bucket(final String key) throws AmazonClientException {
        try {
    	
            UtilMensaje.getInstance().mensaje("Downloading an object " + key);
            final S3Object s3object = s3Client.getObject(new GetObjectRequest(this.bucketName, key));
            //final S3Object s3object = s3Client.getObject(this.bucketName, key);
            //UtilMensaje.getInstance().mensaje("Content-Type: " + s3object.getObjectMetadata().getContentType());
            return (InputStream)s3object.getObjectContent();
        }
        catch (AmazonServiceException ase) {
            UtilMensaje.getInstance().mensaje("AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
            UtilMensaje.getInstance().mensaje("Error Message:    " + ase.getMessage());
            UtilMensaje.getInstance().mensaje("HTTP Status Code: " + ase.getStatusCode());
            UtilMensaje.getInstance().mensaje("AWS Error Code:   " + ase.getErrorCode());
            UtilMensaje.getInstance().mensaje("Error Type:       " + ase.getErrorType());
            UtilMensaje.getInstance().mensaje("Request ID:       " + ase.getRequestId());
            throw ase;
        }
        catch (AmazonClientException ace) {
            UtilMensaje.getInstance().mensaje("AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
            UtilMensaje.getInstance().mensaje("Error Message: " + ace.getMessage());
            throw ace;
        }
    }
    
	public String downloadFileAwsToWindows(String sfAmazon, String sfWindows, String sfEnY) {
		OutputStream osD=null;
		InputStream in=null;
		String ok = "";
		String mensaje = "";
		
		//rutaAmazon = "radicaciones/adjuntos/2022/01/";
		//sFile= "2_ACRILICOS_DE_COLOMBIA_LIMITADA.pdf";
		UtilMensaje.getInstance().mensaje("downloadFileAwsToWindows " + sfAmazon + " se descargara en " + sfWindows);
		
		File fDestino = new File (sfWindows);
		if (fDestino.exists()){
			if (fDestino.length()>0){
				File fY = new File (sfEnY);
				if (fY.exists()){
					if (fY.length()>0){
						if (fDestino.length() == fY.length()){
							UtilMensaje.getInstance().mensaje("downloadFileAwsToWindows " + sfAmazon + " ya existia en el destino " + sfWindows + ", tamanio: " + fDestino.length());
							return "ok";
						}
					}
				}
				UtilMensaje.getInstance().mensaje("downloadFileAwsToWindows Archivo Y: " + sfEnY + "("+ fY.length() + ") ya existe en el destino " + sfWindows + "(" + fDestino.length() + ") tamanio diferentes");
				//return "ok"; Se ba a bajar de nuevo porque se pudo caer en el primer descargue
				ArchivosUtil.getInstance().borrarArchivo(sfWindows);
			}
		}
		
		try {
			in = downloadFileFromS3Bucket(sfAmazon);
			osD = new FileOutputStream(sfWindows);
			int b = 0;
			while (b != -1) {
			  b = in.read();
			  if (b != -1){
				osD.write((char) b);
			  }
			}
			if (ArchivosUtil.getInstance().existeArchivo(sfWindows)){
				ok = "ok"; 
			}else{
				ok = "Error: No se descargo el archivo " + sfWindows; 
			}
		} catch (FileNotFoundException e) {
			mensaje = " downloadFileAwsToWindows. " + sfWindows + " .Error FileNotFoundException : " + e.getMessage();
			//UtilMensaje.getInstance().mensajeW(mensaje);
			ok += mensaje; 
		}catch (IOException z) {
			mensaje = " downloadFileAwsToWindows. " + sfWindows + " .Error IOException : " + z.getMessage();
			//UtilMensaje.getInstance().mensajeW(mensaje);
			ok += mensaje; 
		}catch (Exception e) {
			mensaje = " downloadFileAwsToWindows. " + sfWindows + " .Error Exception : " + e.getMessage();
			//UtilMensaje.getInstance().mensajeW(mensaje);
			ok += mensaje; 
		}finally{
			try {
				in.close();
				osD.close();
			} catch (IOException ie) {
				mensaje = "downloadFileAwsToWindows Error IOException : " + ie.getMessage();
				//UtilMensaje.getInstance().mensajeW(mensaje);
				ok += mensaje; 
			}catch (Exception e) {
				mensaje = "downloadFileAwsToWindows Error  Exception: " + e.getMessage();
				//UtilMensaje.getInstance().mensajeW(mensaje);
				ok += mensaje; 
			} 
		}
		if (ok.equals("ok")){
			UtilMensaje.getInstance().mensaje("downloadFileAwsToWindows " + sfAmazon + " se descargo en " + sfWindows + " correctamente");
			return "ok";
		}
		UtilMensaje.getInstance().mensaje(ok);
		UtilMensaje.getInstance().mensaje("downloadFileAwsToWindows " + sfAmazon + " no fue posible la descarga");
		return ok;
	}

	public boolean uploadFileWindowsToAws(String origenWindows, String destinoEnAws, String sDestinoEnY) {
		//Y:/-->Apunta a s3 con ayuda del TNT
		//origenWindows->PathWinsows/ArchivoName.xxx ej: (H:/BASE/RAMA/2022_1205_rama_501_5279_23_2951720_0_MEDELLIN_JUZGADO LABORAL DEL CIRCUITO No 21_ESTADO.pdf)
		//destinoEnAws->PathAws, no contiene unidad Y:/ ej: (2022/501/5279/23/1205-1.pdf)
		//sDestinoEnY-> PathY/ArchivoName.xxx (Y:/2022/501/5279/23/1205-1.pdf)
		//String key_name = pathAws + Paths.get(sfWindows).getFileName().toString();
        try {
        	UtilMensaje.getInstance().mensaje("uploadFileWindowsToAws - Copiando el archivo (" + origenWindows + ") a s3 en (" + sDestinoEnY + ")");
        	s3Client.putObject(bucketName, destinoEnAws, new File(origenWindows));
        	return true;
			/*
        	if (ArchivosUtil.getInstance().existeArchivo(sDestinoEnY)){
				//Verifica que el archivo si haya sido cargado en Y, o sea en S3
				return true;
			}
			return false;
			*/
      	
        }         
        catch (AmazonServiceException ase) {
            UtilMensaje.getInstance().mensaje("Upload, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
            UtilMensaje.getInstance().mensaje("Error Message:    " + ase.getMessage());
            UtilMensaje.getInstance().mensaje("HTTP Status Code: " + ase.getStatusCode());
            UtilMensaje.getInstance().mensaje("AWS Error Code:   " + ase.getErrorCode());
            UtilMensaje.getInstance().mensaje("Error Type:       " + ase.getErrorType());
            UtilMensaje.getInstance().mensaje("Request ID:       " + ase.getRequestId());
        }
		catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client  
            // couldn't parse the response from Amazon S3.
            UtilMensaje.getInstance().mensaje("DeleteObjectRequest, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
            UtilMensaje.getInstance().mensaje("Error Message: " + e.getMessage());
        }
		return false;
	}	
	
	public boolean moveFileAws(String sourceKey, String destinationKey) {
		UtilMensaje.getInstance().mensaje("Inicio MOVER(Aws) archivo origen " + sourceKey + " destino " + destinationKey );
		if (copyFileAws( sourceKey,  destinationKey)){
			//El delete se hace, pero una politica de bakcup recupera los archivos borrados
			if (deleteFileAws(sourceKey )){
				UtilMensaje.getInstance().mensaje("Fin mover archivo origen " + sourceKey + " destino " + destinationKey + ". OK");
				return true;
			}
		}
		UtilMensaje.getInstance().mensaje("Fin mover archivo origen " + sourceKey + " destino " + destinationKey + ". ERROR" );
		return false;
	}
	
	public boolean copyFileAws(String sourceKey, String destinationKey) {
		UtilMensaje.getInstance().mensaje("Inicio COPIAR(aws) archivo origen " + sourceKey + " destino " + destinationKey );
		try {
	        CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName, sourceKey, bucketName, destinationKey);
	        s3Client.copyObject(copyObjRequest);
			UtilMensaje.getInstance().mensaje("Fin COPIAR(aws) archivo origen " + sourceKey + " destino " + destinationKey + ". OK");
	        return true;
		} 
        catch (AmazonServiceException ase) {
            UtilMensaje.getInstance().mensaje("DeleteObjectRequest, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
            UtilMensaje.getInstance().mensaje("Error Message:    " + ase.getMessage());
            UtilMensaje.getInstance().mensaje("HTTP Status Code: " + ase.getStatusCode());
            UtilMensaje.getInstance().mensaje("AWS Error Code:   " + ase.getErrorCode());
            UtilMensaje.getInstance().mensaje("Error Type:       " + ase.getErrorType());
            UtilMensaje.getInstance().mensaje("Request ID:       " + ase.getRequestId());
        }
		catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client  
            // couldn't parse the response from Amazon S3.
            UtilMensaje.getInstance().mensaje("DeleteObjectRequest, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
            UtilMensaje.getInstance().mensaje("Error Message: " + e.getMessage());
        }
		UtilMensaje.getInstance().mensaje("Fin COPIAR(aws) archivo origen " + sourceKey + " destination " + destinationKey + ". ERROR");
		return false;
	}

	public boolean deleteFileAws(String sfAmazon){
		UtilMensaje.getInstance().mensaje("DELETE(Aws) archivo " + sfAmazon + " -Simulado ");
		return true;
	}
	public boolean deleteFileAws_v1(String sfAmazon){
		//El delete se hace, pero una politica de bakcup recupera los archivos borrados
		UtilMensaje.getInstance().mensaje("Inicio DELETE(Aws) archivo " + sfAmazon + " -Simulado ");
		try {
			s3Client.deleteObject(new DeleteObjectRequest(bucketName, sfAmazon));
			UtilMensaje.getInstance().mensaje("Inicio DELETE(Aws) archivo " + sfAmazon + ". OK");
			return true;
		}
        catch (AmazonServiceException ase) {
            UtilMensaje.getInstance().mensaje("DeleteObjectRequest, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
            UtilMensaje.getInstance().mensaje("Error Message:    " + ase.getMessage());
            UtilMensaje.getInstance().mensaje("HTTP Status Code: " + ase.getStatusCode());
            UtilMensaje.getInstance().mensaje("AWS Error Code:   " + ase.getErrorCode());
            UtilMensaje.getInstance().mensaje("Error Type:       " + ase.getErrorType());
            UtilMensaje.getInstance().mensaje("Request ID:       " + ase.getRequestId());
        }
        catch (AmazonClientException ace) {
            UtilMensaje.getInstance().mensaje("DeleteObjectRequest, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
            UtilMensaje.getInstance().mensaje("Error Message: " + ace.getMessage());
        }
		UtilMensaje.getInstance().mensaje("Inicio DELETE(Aws) archivo " + sfAmazon + ". ERROR");
		return false;
	}
	
}
