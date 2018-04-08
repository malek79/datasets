package com.tcb.hive;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

public class HiveJdbcClient {
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";

	public static void main(String[] args) throws SQLException {

		Statement stmt = null;
		Statement stmt2 = null;
		ResultSet res1 = null;
		ResultSet res2 = null;

		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "", "");
		stmt = con.createStatement();
		String tableName = "testHiveDriverTable";
		String sql = "drop table if exists " + tableName;
		stmt.execute(sql);

		sql = "CREATE EXTERNAL TABLE " + tableName + " (name string, buyer string, units int)"
				+ "ROW FORMAT DELIMITED FIELDS TERMINATED BY ','" + "STORED AS TEXTFILE";
		stmt.execute(sql);

		// Retrieve and display a description of the table
		sql = "describe " + tableName;
		System.out.println("\nGetting a description of the table:");
		res1 = stmt.executeQuery(sql);
		while (res1.next()) {
			System.out.println(res1.getString(1) + "\t" + res1.getString(2));
		}

		// Insert data into the new table
		// sql = "INSERT overwrite TABLE cellnumbersderived "
		// + "SELECT name, age, cell from cellnumbers LIMIT 2";
		String filepath = "/home/malek/Downloads/Documents/productname.txt";
		sql = "load data local inpath '" + filepath + "' overwrite into table " + tableName;
		// + "SELECT name, age, cell from cellnumbers LIMIT 2";
		System.out.println("Running: " + sql);
		// System.out.println("\nInserting data into the table.");
		stmt.execute(sql);

		// Retrieve data from the table
		sql = "select * from " + tableName;
		stmt2 = con.createStatement();
		System.out.println("\nRetrieving inserted data:");
		System.out.println("Running: " + sql);
		res2 = stmt2.executeQuery(sql);

		while (res2.next()) {
			System.out.println(res2.getString(1) + "\t" + res2.getString(2) + "\t" + res2.getString(3));
		}
		System.out.println("\nHive queries completed successfully!");

		// regular hive query
		// sql = "select count(1) from " + tableName;
		// System.out.println("Running: " + sql);
		// res = stmt.executeQuery(sql);
		// while (res.next()) {
		// System.out.println(res.getString(1));
		// }
		
	}
}