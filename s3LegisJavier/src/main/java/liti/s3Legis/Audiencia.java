package liti.s3Legis;

public class Audiencia {
	private String Nombre; // Nombre de la audiencia. String Si M�ximo 200 caracteres
	private String Descripcion; // Descripci�n de la audiencia. String Si M�ximo 500 caracteres
	private String Fecha; // Fecha de la audiencia. Date Si Formato: YYYY-MM-DD

	public Audiencia() {
		Nombre="";
		Descripcion="";
		Fecha="";
	}

	public String getNombre() {
		return Nombre;
	}

	public void setNombre(String nombre) {
		this.Nombre = nombre;
	}

	public String getDescripcion() {
		return Descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.Descripcion = descripcion;
	}

	public String getFecha() {
		return Fecha;
	}

	public void setFecha(String fecha) {
		Fecha = fecha;
	}

}
