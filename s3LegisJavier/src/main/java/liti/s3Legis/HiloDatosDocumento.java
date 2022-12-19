package liti.s3Legis;

public class HiloDatosDocumento {
	private String documentoId; // Identificador del documento
	private String novedadId; // Identificador de la novedad
	private String actuacionProcesalId; // Identificador de la actuacion liti
	private int actuacionLegisId; // Identificador de la actuacion legis
	private String oficina; // Nombre de la oficina. 
	private String caso; //Identificador del caso provisto por LegisOffice
	private String archivoId; // Identificador del archivo
	private String archivoRuta; // Identificador del archivo
	private String archivoNombre; // Nombre del archivo, tamannio 256 crteres
	
	public HiloDatosDocumento() {
	}

	public String getDocumentoId() {
		return documentoId;
	}

	public void setDocumentoId(String documento) {
		this.documentoId = documento;
	}
	
	public String getNovedadId() {
		return novedadId;
	}

	public void setNovedadId(String novedadId) {
		this.novedadId = novedadId;
	}

	public String getActuacionProcesalId() {
		return actuacionProcesalId;
	}
	
	public void setActuacionProcesalId(String actuacionId) {
		this.actuacionProcesalId = actuacionId;
	}

	public int getActuacionLegisId() {
		return actuacionLegisId;
	}

	public void setActuacionLegisId(int idActuacion) {
		this.actuacionLegisId = idActuacion;
	}

	
	public String getOficina() {
		return oficina;
	}

	public void setOficina(String oficina) {
		this.oficina = oficina;
	}


	public String getCaso() {
		return caso;
	}

	public void setCaso(String caso) {
		this.caso = caso;
	}	

	public String getArchivoId() {
		return archivoId;
	}

	public void setArchivoId(String archivoId) {
		this.archivoId = archivoId;
	}	

	public String getArchivoRutaEnY() {
    	String sFile = archivoRuta.replace("\\", "/");
    	sFile = sFile.replace("D:/FOTOS", "Y:");            	
		return sFile;
	}

	public String getArchivoRutaEnAws() {
		return getArchivoRutaEnY().replace("Y:/", "");
	}
	
	public void setArchivoRutaEnY(String archivoRuta) {
		this.archivoRuta = archivoRuta;
	}	
	
	public String getArchivoNombre() {
		return archivoNombre;
	}

	public void setArchivoNombre(String nombre) {
		archivoNombre = nombre;
	}	


	
}
