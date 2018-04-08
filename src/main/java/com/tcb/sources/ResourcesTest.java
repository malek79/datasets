package com.tcb.sources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ResourcesTest {

	public static void main(String[] args) {

		final String FILENAME = "/home/malek/Documents/links1.txt";

		BufferedWriter bw = null;
		FileWriter fw = null;

		String csvFile = "/home/malek/ckan.txt";
		BufferedReader br = null;
		String line = "";
//		String cvsSplitBy = ",";

		try {
			fw = new FileWriter(FILENAME);
			bw = new BufferedWriter(fw);

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
//				String[] country = line.split(cvsSplitBy);
//				if (country.length > 2) {
					if (line.contains("http")) {
						bw.write(line+"\n");
						System.out.println(line);
					}

//				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {

				if (bw != null)
					bw.close();
				System.out.println("finshed bw");
				if (fw != null)
					fw.close();
				System.out.println("finshed fw");
			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}

	}

}