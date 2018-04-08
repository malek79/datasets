package com.spark.hive;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import scala.Tuple2;

public class JavaSparkHiveExample {

	public static void main(String[] args) {

		SparkSession spark = SparkSession.builder().appName("Java Spark Hive Example").enableHiveSupport()
				.config("hive.metastore.uris", "thrift://192.168.1.112:9083").master("local[*]").getOrCreate();

		Dataset<Row> onelink = spark.sql("SELECT * FROM onelink");

		// Data in tuple
		Dataset<Tuple2<String, String>> resources = onelink.select("resources")
				.map((MapFunction<Row, Tuple2<String, String>>) row -> new Tuple2<String, String>(
						row.getJavaMap(0).keySet().iterator().next().toString(),
						row.getJavaMap(0).entrySet().iterator().next().toString()),
						Encoders.tuple(Encoders.STRING(), Encoders.STRING()));

		// Get number of resources per type method
		Dataset<String> formats = onelink.select("resources").flatMap((FlatMapFunction<Row, String>) row -> {
			Iterator<Object> it = row.getJavaMap(0).keySet().iterator();
			List<String> result = new ArrayList<>();
			while (it.hasNext()) {
				result.add((String) it.next());
			}
			return result.iterator();

		}, Encoders.STRING());

		List<Row> p = formats.groupBy("value").count().collectAsList();
		// for (Row k : p) {
		// System.out.println(k);
		// }
		// Get authors

		Dataset<String> authors = spark.sql("select author,count(*) from onelink GROUP by author").map(
				(MapFunction<Row, String>) row -> "Key: " + row.get(0) + ",Value: " + row.get(1), Encoders.STRING());

		// onelink.groupBy("author").count().show();

		// Get datasets by country
		Dataset<String> urls = onelink.select("resources").flatMap((FlatMapFunction<Row, String>) row -> {
			Iterator<Object> it = row.getJavaMap(0).values().iterator();
			List<String> result = new ArrayList<>();
			while (it.hasNext()) {
				result.add((String) it.next());
			}
			return result.iterator();

		}, Encoders.STRING());
		Pattern pattern = Pattern.compile("(https?://)([^:^/]*)(:\\d*)?(.*)?");
	
		Dataset<String> countries = urls.map((MapFunction<String, String>) url -> {
			
	        if(url !=null ){
	        	
	            Matcher matcher = pattern.matcher(url);
	            
	            if(matcher.find()){
	            //System.out.println(matcher.group(2));
	                    Locale obj = new Locale("", matcher.group(2).split("\\.")[matcher.group(2).split("\\.").length-1]);
	                 //  return matcher.group(2).split("\\.")[matcher.group(2).split("\\.").length-1];
	                     return obj.getDisplayCountry();
	                
	            }
	            else return "";
	        }else return "";
			
		}, Encoders.STRING());
		countries.groupBy("value").count().show();
		
		spark.stop();
	}
}