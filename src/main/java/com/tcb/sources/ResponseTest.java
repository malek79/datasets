package com.tcb.sources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;

public class ResponseTest {

	public static void main(String[] args) {

		String csvFile = "/home/malek/workspace/Project2/src/main/resources/links.txt";
		BufferedReader br = null;
		String line = "";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				try {

					CkanClient cc = new CkanClient(line);

					List<String> ds = cc.getDatasetList(1000, 0);

					for (String s : ds) {

						System.out.println("DATASET: " + s);
						CkanDataset d = cc.getDataset(s);

						System.out.println("  RESOURCES:");
						for (CkanResource r : d.getResources()) {
							System.out.println("    " + r.getName());
							System.out.println("    FORMAT: " + r.getFormat());
							System.out.println("       URL: " + r.getUrl());
						}
					}
				} catch (CkanException e) {
					// TODO: handle exception
				}

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

		}

	}

}