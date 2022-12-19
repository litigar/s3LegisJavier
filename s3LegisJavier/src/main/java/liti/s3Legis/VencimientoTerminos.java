package liti.s3Legis;

public class VencimientoTerminos {

	private String Actividad; // Descripci�n vencimiento de t�rminos. String S� M�ximo 500 caracteres
	private String Descripcion; // Descripci�n vencimiento de t�rminos. String S� M�ximo 500 caracteres
	//private Date FechaInicio; // Fecha inicio vencimiento de t�rminos. Date S� Formato: YYYY-MM-DD
	private String FechaInicio; // Fecha inicio vencimiento de t�rminos. Date S� Formato: YYYY-MM-DD
	//private Date FechaFin;  // Fecha fin vencimiento de t�rminos. Date S� Formato: YYYY-MM-DD
	private String FechaFin;  // Fecha fin vencimiento de t�rminos. Date S� Formato: YYYY-MM-DD

	public VencimientoTerminos() {
		Actividad = "";
		Descripcion = "";
		FechaInicio = "";
		FechaFin = "";
	}

	public String getActividad() {
		return Actividad;
	}

	public void setActividad(String actividad) {
		this.Actividad = actividad;
	}

	public String getDescripcion() {
		return Descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.Descripcion = descripcion;
	}

	public String getFechaInicio() {
	//public Date getFechaInicio() {
		return FechaInicio;
	}

	//public void setFechaInicio(Date fechaInicio) {
	public void setFechaInicio(String fechaInicio) {
		this.FechaInicio = fechaInicio;
	}

	//public Date getFechaFin() {
	public String getFechaFin() {
		return FechaFin;
	}

	//public void setFechaFin(Date fechaFin) {
	public void setFechaFin(String fechaFin) {
		this.FechaFin = fechaFin;
	}
}
