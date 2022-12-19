package liti.s3Legis;

public class HiloDatosActuacionEliminar {
	private String novedadId; // Identificador de la novedad
	private int actuacionLegisId; // Identificador de la actuacion legis
	private String oficina; // Nombre de la oficina. 
	private String caso; //Identificador del caso provisto por LegisOffice
	
	public HiloDatosActuacionEliminar() {
	}

	public String getNovedadId() {
		return novedadId;
	}

	public void setNovedadId(String novedadId) {
		this.novedadId = novedadId;
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

}
