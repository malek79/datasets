package com.tcb.ES;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.model.CkanTag;

public class WriteDataES {

	public static void writeES(RestHighLevelClient client, String id, String name, String title, List<String> groups, String organization,
			List<String> tags, String licensetitle, String author, List<Map<String, String>> resources)
			throws IOException {

		IndexRequest request = new IndexRequest("datasets", "data", id);

		// Using Map
		Map<String, Object> jsonMap = new HashMap<String, Object>();

		jsonMap.put("name", name);
		jsonMap.put("title", title);
		jsonMap.put("groups", groups);
		jsonMap.put("organization", organization);
		jsonMap.put("tags", tags);
		jsonMap.put("licensetitle", licensetitle);
		jsonMap.put("author", author);
		jsonMap.put("resources", resources);

		// create the source
		request.source(jsonMap, XContentType.JSON);

		IndexResponse indexResponse = client.index(request);
		System.out.println(indexResponse.getType());

	}

	public static List<String> groupsToList(List<CkanGroup> listgroups) {
		List<String> result = new ArrayList<>();
		if (!listgroups.isEmpty()) {
			for (int i = 0; i < listgroups.size(); i++) {
				result.add(listgroups.get(i).getDisplayName());
			}
		}
		return result;
	}

	public static List<String> tagsToList(List<CkanTag> listgroups) {
		List<String> result = new ArrayList<>();
		if (!listgroups.isEmpty()) {
			for (int i = 0; i < listgroups.size(); i++) {
				result.add(listgroups.get(i).getDisplayName());
			}
		}
		return result;
	}

	public static void main(String[] args) {

		String csvFile = "/home/malek/workspace/Project2/src/main/resources/urls.txt";
		BufferedReader br = null;
		String line = "";

		RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();

		RestHighLevelClient client = new RestHighLevelClient(restClient);
		
		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				try {
					System.out.println("-----------------------" + line + "-----------------------");
					CkanClient cc = new CkanClient(line);

					List<String> ds = cc.getDatasetList(1, 0);

					for (String s : ds) {

						List<Map<String, String>> arrayRes = new ArrayList<>();

						System.out.println("DATASET: " + s);
						CkanDataset d = cc.getDataset(s);
						System.out.println(" Title: " + d.getTitle());

						System.out.println("  RESOURCES:");
						for (CkanResource r : d.getResources()) {

							System.out.println("    " + r.getDescription());
							System.out.println("    FORMAT: " + r.getFormat());
							System.out.println("       URL: " + r.getUrl());

							Map<String, String> resources = new HashMap<String, String>();
							resources.put("description", r.getDescription());
							resources.put("format", r.getFormat());
							resources.put("url", r.getUrl());
							arrayRes.add(resources);

						}
						String organization = "";

						if (d.getOrganization() != null) {
							organization = d.getOrganization().getDisplayName();
							System.out.println(d.getOrganization().getName());
						}

						String title = d.getTitle().replaceAll("\\r|\'", " ").replaceAll("\\r|\"", " ");

						writeES(client, d.getId(), d.getName(), title, groupsToList(d.getGroups()), organization,
								tagsToList(d.getTags()), d.getLicenseTitle(), d.getAuthor(), arrayRes);
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
