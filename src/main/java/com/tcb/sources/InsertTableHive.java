package com.tcb.sources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertTableHive {

	public static void main(String[] args) throws SQLException {
		
		String driverName = "org.apache.hive.jdbc.HiveDriver";
		
		Statement stmt = null;
	
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		Connection con = DriverManager.getConnection("jdbc:hive2://192.168.1.112:10000/default", "", "");
		stmt = con.createStatement();
		String tableName = "onelink";
		String dummytable = "onelinkdummy1";
		
		String insert_table = "INSERT INTO TABLE " + tableName + " select"
				+ " id, name, title, nvl(split(groups,','), ''), organization, nvl(split(tags,','), ''), licenseTitle,author , nvl(str_to_map(resources,',',' '), ''), nvl(split(descriptions,','), '') "
				+ "from " + dummytable + " where resources != 'null' ";
		System.out.println("--------------Request--------------------------");
		System.out.println(insert_table);
		System.out.println("--------------Request--------------------------");

		stmt.execute(insert_table);
	}

}
