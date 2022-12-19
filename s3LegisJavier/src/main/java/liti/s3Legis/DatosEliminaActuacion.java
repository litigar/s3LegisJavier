package liti.s3Legis;

public class DatosEliminaActuacion {
	private String Oficina; // Nombre de la oficina. 
	private int IdActuacion; // Identificador de la actuacion Int 
	private String Caso; //Identificador del caso. 
	
	public DatosEliminaActuacion() {
	}

	public String getOficina() {
		return Oficina;
	}

	public void setOficina(String oficina) {
		this.Oficina = oficina;
	}

	public int getIdActuacion() {
		return IdActuacion;
	}

	public void setIdActuacion(int idActuacion) {
		this.IdActuacion = idActuacion;
	}

	public String getCaso() {
		return Caso;
	}

	public void setCaso(String caso) {
		Caso = caso;
	}	
}
