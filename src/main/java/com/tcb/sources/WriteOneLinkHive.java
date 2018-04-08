package com.tcb.sources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.model.CkanTag;

public class WriteOneLinkHive {

	
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";
	
	public static String groupsToString(List<CkanGroup> listgroups){
		String result ="";
		if (!listgroups.isEmpty()){
		for (int i = 0; i < listgroups.size()-1 ; i++) {
			result = result + listgroups.get(i).getDisplayName() +",";
		}
		result = result + listgroups.get(listgroups.size()-1).getDisplayName() ;
		}
		return result;
	}
	
	public static String tagsToString(List<CkanTag> listgroups){
		String result ="";
		if (!listgroups.isEmpty()){
		for (int i = 0; i < listgroups.size()-1 ; i++) {
			result = result + listgroups.get(i).getDisplayName() +",";
		}
		result = result + listgroups.get(listgroups.size()-1).getDisplayName() ;
		}
		return result;
	}
	
	
	
	public static void main(String[] args) throws SQLException {
		
		Statement stmt = null;
		ResultSet res1 = null;
		String resources ;
		String descriptions ;
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
		String dummytable = "onelinkdummy";
		// Create dummy table 
		   
		String sql = "CREATE TABLE IF NOT EXISTS " + dummytable + " (id string,name string,title string, groups string, organization string,tags string, licenseTitle string, author string,resources string, descriptions string) "
				+ " STORED AS TEXTFILE";
		stmt.execute(sql);
		
		//Create table 
		sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (id string,name string,title string, groups array<string>, organization string,tags array<string>, licenseTitle string, author string,resources map<string,string>, description string) "
				+ "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'" 
				+ " collection items terminated by ',' map keys terminated by ' ' lines terminated by '\n'"
				+" STORED AS TEXTFILE";
		
		stmt.execute(sql);
		// Describe table 
		sql = "describe " + tableName;
		System.out.println("\nGetting a description of the table:");
		res1 = stmt.executeQuery(sql);
		while (res1.next()) {
			System.out.println(res1.getString(1) + "\t" + res1.getString(2));
		}
		CkanClient cc = new CkanClient("https://africaopendata.org/");

		List<String> ds = cc.getDatasetList(5, 0);
	
		for (String s : ds) {
			
			resources = "";
			descriptions = "";
			System.out.println("DATASET: " + s);
			CkanDataset d = cc.getDataset(s);
			System.out.println(" Title: " + d.getTitle());
		
			System.out.println("  RESOURCES:");
			int i = 1 ;
			for (CkanResource r : d.getResources()) {
				
				System.out.println("    " + r.getDescription());
				System.out.println("    FORMAT: " + r.getFormat());
				System.out.println("       URL: " + r.getUrl());
				if (i < d.getResources().size()){
					resources = resources + r.getFormat() +" "+ r.getUrl()+",";
					descriptions = descriptions + r.getDescription() + ",";
				}
				if (i == d.getResources().size()){
					resources = resources + r.getFormat() +" "+ r.getUrl();
					descriptions = descriptions + r.getDescription();
				}
				i++;
			
			}
			String organization = "";
			
			if (d.getOrganization() != null){
				organization = d.getOrganization().getDisplayName();
				System.out.println(d.getOrganization().getName());
			}
			descriptions = descriptions.replaceAll("\\r|\\n", " ").replaceAll("\\r| \"", " ");
			
			
			String insert_sql = "INSERT INTO TABLE "+ dummytable +" VALUES"
                    +"("
                    +"\""+d.getId()+"\""
                    +","+"\""+d.getName()+"\""
                    +","+"\""+d.getTitle()+"\""
                    +","+ "\""+groupsToString(d.getGroups())+"\""
                    +","+"\""+  organization +"\""
                    +","+ "\""+tagsToString(d.getTags())+"\""
                    +","+"\""+d.getLicenseTitle()+"\""
                    +","+"\""+d.getAuthor()+"\""
                    +","+ "\""+resources +"\""        
                    +","+ "\""+descriptions+"\""
                    +")";
			System.out.println("--------------Request--------------------------");
System.out.println(insert_sql);			
			System.out.println("--------------Request--------------------------");

            stmt.execute(insert_sql);
		}
		      

		String insert_table = "INSERT INTO TABLE "+ tableName +" select"
                +" id, name, title, split(groups,','), organization, split(tags,','), licenseTitle,author , str_to_map(resources,',',' '), split(descriptions,',') "
                +"from "+ dummytable;
		System.out.println("--------------Request--------------------------");
System.out.println(insert_table);			
		System.out.println("--------------Request--------------------------");

        stmt.execute(insert_table);

	}

}
