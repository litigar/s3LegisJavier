package liti.s3Legis;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.DatatypeConverter;

public class CodeBase64 {
	String encoded;
	String token;
	String sRuta;


	public CodeBase64() {
	}
	public CodeBase64(String sToken) {
		token = sToken;
	}

	public String getEncoded() {
		return encoded;
	}

	public String serializarToken() {
		// Serializar el token
		byte [] tokenBin = this.getToken().getBytes();
		this.setEncoded( DatatypeConverter.printBase64Binary(tokenBin) );
		//System.out.println("Serializado: "+this.getEncoded());
		return this.getEncoded();
	}

	public String serializarPDF() {
		// Serializar docuemnto en formato PDF
		byte[] docum = convertPDFToByteArray(this.getsRuta());//Este ya funciono

		if (docum == new byte[1024]){
			return "";
		}

		this.setEncoded( DatatypeConverter.printBase64Binary(docum) );
		//UtilMensaje.getInstance().mensaje("Archivo " + sRuta + " Serializado: "+this.getEncoded());
		return this.getEncoded();
	}

    private static byte[] convertPDFToByteArray(String sourcePath) {
    	String sError="";

    	InputStream inputStream = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            inputStream = new FileInputStream(sourcePath);

            byte[] buffer = new byte[1024];
            baos = new ByteArrayOutputStream();

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            sError="ok";
        } catch (FileNotFoundException e) {
        	UtilMensaje.getInstance().mensaje("convertPDFToByteArray " + e.getMessage());
            e.printStackTrace();
            sError="error";
        } catch (IOException e) {
        	UtilMensaje.getInstance().mensaje("convertPDFToByteArray " + e.getMessage());
            e.printStackTrace();
            sError="error";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (sError.equals("ok"))
        	return baos.toByteArray();
        return new byte[1024];
}

	public void setEncoded(String encoded) {
		this.encoded = encoded;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getsRuta() {
		return sRuta;
	}

	public void setsRuta(String sRuta) {
		this.sRuta = sRuta;
	}

	public static void main(String[] args) {
		CodeBase64 base = new  CodeBase64();
		base.setToken("4ce482fc-bfb1-48e3-865a-af9961aea7d0");
		base.serializarToken();
		// Serializar el PDF
		base.setsRuta("L:\\legis\\prueba.pdf");
		base.serializarPDF();

	}
}