package com.spark.test;

import java.util.List;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;

public class Main {

	public static void main(String[] args)  {

		CkanClient cc = new CkanClient("https://africaopendata.org/");

		List<String> ds = cc.getDatasetList(1000, 0);

		for (String s : ds) {

			System.out.println("DATASET: " + s);
			CkanDataset d = cc.getDataset(s);
			System.out.println(" Title: " + d.getTitle());
			System.out.println("    id: " + d.getId());
			System.out.println("    id: " + d.getOrganization());
			System.out.println(" group: " + d.getGroups());
			System.out.println("    id: " + d.getAuthor());
			System.out.println(" licen: " + d.getLicenseTitle());
			System.out.println(" tagss: " + d.getTags());
			System.out.println("  RESOURCES:");
			for (CkanResource r : d.getResources()) {
				System.out.println("    " + r.getDescription());
				System.out.println("    FORMAT: " + r.getFormat());

				System.out.println("       URL: " + r.getUrl());
			}
		}

	}
}
