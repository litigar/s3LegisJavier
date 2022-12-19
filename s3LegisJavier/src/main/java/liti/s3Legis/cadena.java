package liti.s3Legis;

public class cadena {
	String permitidos = "0123456789ABCDEFGHIJKLMN�OPQRSTUVWXYZabcdefghijklmn�opqrstuvwxyz( ).:,/|<>";
	String tildes = "������������������������������������������������������";

	public String quitarEspeciales(String palabra){
		String permitidos = this.permitidos + tildes;
	    char[] caracteres = palabra.toCharArray();
	    char[] char_permitidos = permitidos.toCharArray();
	    String cadena = "";

	    for (int i = 0; i < caracteres.length; i++) {
	        //System.out.println("El caracter " + i + " es " + caracteres[i]);
	        for (int j = 0; j < char_permitidos.length; j++) {
	        	if (caracteres[i] == char_permitidos[j]){
	        		cadena += caracteres[i];
	        		j=char_permitidos.length;
	        	}
	        }


	    }
		return cadena;
	}

	public String getCadenaPorLimite(String palabra, int limite){
	    char[] caracteres = palabra.toCharArray();
	    String cadena = "";

	    if (caracteres.length <= limite)
	    	return palabra;
	    for (int i = 0; i < caracteres.length; i++) {
	        //System.out.println("El caracter " + i + " es " + caracteres[i]);
    		cadena += caracteres[i];
	    }
		return cadena;
	}

	public String obtenerPalabras(String palabras,int cantidad ){

		String qtyPalabras = "";

			String parte1[] = palabras.split("<");
			palabras = parte1[0];

			if (palabras == null || palabras.equals(" ") || palabras.equals("  ") || palabras.equals("   ")|| palabras.equals(".")){
				qtyPalabras = "AUTO";
			}else{
				String separar[] = palabras.split(" ");
				for(int i = 0; i < separar.length & i < cantidad ; i++){
					qtyPalabras += separar[i]+" ";
				}
			}
		return qtyPalabras;
	}


}