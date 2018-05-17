package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OracleTable {
	private static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
	private static final String DATABASE_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
	private static final String DATABASE_USER = "wepull";
	private static final String DATABASE_PASSWORD = "wepull";
	private static Connection con = null;

	public static Connection getConnection() {
		try {
			Class.forName(DRIVER_CLASS);
			con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
			return con;
		} catch (Exception ex) {
			System.out.println("2:" + ex.getMessage());
		}
		return con;
	}

	/***
	 * 打印test
	 * 
	 * @throws SQLException
	 */
	public static List<?> sysoutStrTablePdmCloumns(String Table, String Owner) throws SQLException {
		getConnection();

		List<HashMap<String, String>> columns = new ArrayList<HashMap<String, String>>();

		try {
			Statement stmt = con.createStatement();

			String sql = "select " + "         comments as \"Name\"," + "         a.column_name \"Code\","
					+ "         a.DATA_TYPE as \"DataType\"," + "         b.comments as \"Comment\","
					+ "         decode(c.column_name,null,'FALSE','TRUE') as \"Primary\","
					+ "         decode(a.NULLABLE,'N','TRUE','Y','FALSE','') as \"Mandatory\","
					+ "         '' \"sequence\"" + "   from " + "       all_tab_columns a, "
					+ "       all_col_comments b," + "       (" + "        select a.constraint_name, a.column_name"
					+ "          from user_cons_columns a, user_constraints b"
					+ "         where a.constraint_name = b.constraint_name"
					+ "               and b.constraint_type = 'P'" + "               and a.table_name = '" + Table + "'"
					+ "       ) c" + "   where " + "     a.Table_Name=b.table_Name "
					+ "     and a.column_name=b.column_name" + "     and a.Table_Name='" + Table + "'"
					+ "     and a.owner=b.owner " + "     and a.owner='" + Owner + "'"
					+ "     and a.COLUMN_NAME = c.column_name(+)" + "  order by a.COLUMN_ID";
			System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Name", rs.getString("Name"));
				map.put("Code", rs.getString("Code"));
				map.put("DataType", rs.getString("DataType"));
				map.put("Comment", rs.getString("Comment"));
				map.put("Primary", rs.getString("Primary"));
				map.put("Mandatory", rs.getString("Mandatory"));
				columns.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return columns;
	}

	public static void main(String[] args) throws SQLException {

		List<?> columns = sysoutStrTablePdmCloumns("CT_INFO_CONTRACT_BORROW", "DEV");
		for (Object object : columns) {
			System.err.println(object);
		}

	}

}