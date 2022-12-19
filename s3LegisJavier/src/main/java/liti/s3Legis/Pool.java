package liti.s3Legis;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

public class Pool {

	public DataSource ds;

	public Pool() {
		inicializarDataSource();
	}

	private void inicializarDataSource(){
		org.apache.commons.dbcp.BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(ClassPropertiesOracle.getInstance().dbDriverOracle);
		bds.setUsername(ClassPropertiesOracle.getInstance().usr);
		bds.setPassword(ClassPropertiesOracle.getInstance().pwd);
		bds.setUrl(ClassPropertiesOracle.getInstance().getConeccionOra());
		bds.setMaxActive(ClassPropertiesOracle.getInstance().cantidadSesiones);
		ds = bds;
	}

	public static void main(String[] args){
		UtilMensaje.getInstance().setNameFileAdmin("pool_" + ReadSystemProperties.getInstance().getPropertiesComputerName() + "_");
		if (!UtilMensaje.getInstance().preparaLogFile())
			System.exit(0);

		Pool pool = new Pool();
		Connection conn = null;
		try {
			conn = pool.ds.getConnection();

			if (conn!=null)
				UtilMensaje.getInstance().mensaje("Conn ok");
			else
				UtilMensaje.getInstance().mensaje("Conn err");


		} catch (SQLException e) {
			UtilMensaje.getInstance().mensaje(e.getMessage() + " " + ClassPropertiesOracle.getInstance().getParametrosOracle());
			//e.printStackTrace();
        }finally{
    		try {
    			conn.close();
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
        }
	}
}
