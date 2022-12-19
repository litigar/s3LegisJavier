package liti.s3Legis;

import java.io.Serializable;
import java.util.ArrayList;

public class DatosActuacionInsert implements Serializable {
	private static final long serialVersionUID = 1L;
	private String Oficina; //Nombre de la oficina
	private String Caso; //Identificador del caso. Guid S� Identificador �nico del caso provisto por LegisOffice
	//private String nombre; //nombre de la oficina a la cual	pertenece el caso (M�ximo 128 caracteres)
	private String Titulo; //T�tulo de la actuaci�n. String S� M�ximo 200 caracteres
	private String TituloHomologado; //T�tulo de la actuaci�n HOMOLOGADO. String S� M�ximo 200 caracteres
	private String Descripcion; //Descripci�n de la actuaci�n. String S� M�ximo 4000 caracteres
	private String Fecha; // Fecha de la actuaci�n. Date S� Formato: YYYY-MM-DD
	private String Radicado; // N�mero de radicado. String S� M�ximo 50 caracteres
	//private int Juzgado; //Identificador de juzgado o entidad homologado Se cambia a string el 05/05/2022
	private String Juzgado; //1000 caracteres
	private String TpProcesoId;
	private ArrayList<VencimientoTerminos> VencimientoTerminos = new ArrayList<VencimientoTerminos>(); //	Listado con la informaci�n de vencimiento de t�rminos (tipo: VencimientoTerminoModel). NO Requerido porque no se maneja en litigar
	private ArrayList<Audiencia> Audiencias = new ArrayList<Audiencia>(); //Listado con la informaci�n de audiencias (tipo: AudienciaModel). Opcional, Si se maneja en litigar
	private Boolean FinalizaServicio=null; //	Indica si con esta actuaci�n se est� finalizado el servicio de Vigilancia Judicial para este caso.
	private String CodigoExterno; // C�digo con el cual el proveedor tiene registrado el caso o actuaci�n en su sistema, tama�o 150 crteres

	public DatosActuacionInsert() {
	}

	/*public void obtieneJson(String[] datos) {
		this.setDescripcion(datos[0]);
	}*/

	public String getOficina() {
		return Oficina;
	}

	public void setOficina(String oficina) {
		this.Oficina = oficina;
	}

	/*public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}*/

	public String getCaso() {
		return Caso;
	}

	public void setCaso(String caso) {
		Caso = caso;
	}

	public String getTpProcesoId() {
		return TpProcesoId;
	}

	public void setTpProcesoId(String tpProcesoId) {
		TpProcesoId = tpProcesoId;
	}

	public String getTitulo() {
		return Titulo;
	}

	public void setTitulo(String titulo) {
		this.Titulo = titulo;
	}

	public String getTituloHomologado() {
		return TituloHomologado;
	}

	public void setTituloHomologado(String tituloHomologado) {
		this.TituloHomologado = tituloHomologado;
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
		this.Fecha = fecha;
	}

	public String getRadicado() {
		return Radicado;
	}

	public void setRadicado(String radicado) {
		this.Radicado = radicado;
	}

	public String getJuzgado() {
		return Juzgado;
	}

	public void setJuzgado(String juzgado) {
		this.Juzgado = juzgado;
	}

	public ArrayList<VencimientoTerminos> getVencimientoTerminos() {
		return VencimientoTerminos;
	}

	public void setVencimientoTerminos(ArrayList<VencimientoTerminos> vencimientoTerminos) {
		this.VencimientoTerminos = vencimientoTerminos;
	}

	public ArrayList<Audiencia> getAudiencias() {
		return Audiencias;
	}

	public void setAudiencias(ArrayList<Audiencia> audiencias) {
		this.Audiencias = audiencias;
	}
	public Boolean isFinalizaServicio() {
		return FinalizaServicio;
	}

	public void setFinalizaServicio(Boolean finalizaServicio) {
		this.FinalizaServicio = finalizaServicio;
	}

	public String getCodigoExterno() {
		return Oficina;
	}

	public void setCodigoExterno(String CodigoExterno) {
		this.CodigoExterno = CodigoExterno;
	}

}
