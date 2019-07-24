package scannel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtility {

	public enum TYPE {
		MySQL,
		MSSQL,
	}


	private TYPE type;
	
	private String db_driver;
	private String db_url;
	private String db_server;
	private String db_username;
	private String db_password;
	
	public DBUtility() {
		// TODO Auto-generated constructor stub
		type = TYPE.MSSQL;
		
		db_driver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		db_server = "192.168.100.100:1433";
		db_username = "";
		db_password = "";
		db_url = "jdbc:microsoft:sqlserver://"+db_server+";DatabaseName=DATABASE";
		
		try {
			Class.forName(db_driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		try {
			Connection con = DriverManager.getConnection(db_url,db_username, db_password);
			Statement stmt = con.createStatement();
			
			String SQL = "SELECT * FROM RFID_TABLE ";
			ResultSet rs = stmt.executeQuery(SQL);
			
			while (rs.next()) {
                System.out.println(rs.getString("Column1") + " " + rs.getString("Column2"));
            }
			
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TYPE getDBType() {
		return type;
	}
	
}
