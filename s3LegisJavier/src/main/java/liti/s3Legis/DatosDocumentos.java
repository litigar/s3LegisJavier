package liti.s3Legis;

public class DatosDocumentos {
	private String Oficina; // Nombre de la oficina. String S�
	private int IdActuacion; // Identificador de la actuaci�n Int S�
	private String Caso; //Identificador del caso. Guid S� Identificador �nico del caso provisto por LegisOffice
	private String Nombre; // Nombre del archivo, tama�o 256 crteres
	private String Documento; // Base64 del archivo PDF que se quiere asociar a la actuaci�n. String S� Base64, tama�o m�ximo 20 Mb
	private String CodigoExterno; // C�digo con el cual el proveedor tiene registrado el caso o actuaci�n en su sistema, tama�o 150 crteres

	public DatosDocumentos() {
		// TODO Auto-generated constructor stub
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

	public String getNombre() {
		return Nombre;
	}

	public void setNombre(String nombre) {
		Nombre = nombre;
	}

	public String getDocumento() {
		return Documento;
	}

	public void setDocumento(String documento) {
		this.Documento = documento;
	}

	public String getCodigoExterno() {
		return Oficina;
	}

	public void setCodigoExterno(String CodigoExterno) {
		this.CodigoExterno = CodigoExterno;
	}

}
