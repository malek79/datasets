package com.spark.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class test {

	public static void main(String[] args) throws InterruptedException, IOException {

		
//		URL url = new URL("https://demo.ckan.org/api/3/action/package_search?q=spending");
	URL url = new URL("https://africaopendata.org/api/3/action/package_list?limit=1000&offset=0");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setConnectTimeout(5000);// 5 secs
		connection.setReadTimeout(5000);// 5 secs
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/json");


		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

		String line = null;
		
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		connection.disconnect();

	}
}
