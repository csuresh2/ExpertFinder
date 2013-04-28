package edu.uiuc.ras;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.commons.io.FileUtils;

public class ContentFilter 
{
	private String parseDataFolder;
	private String filterDataFolder;
	private String filterSchema;
	private String[] contentKeyWords = {"faculty", "professor"};
	private String[] urlKeyWords = {"user", "faculty", "people", "~"};
	
	public ContentFilter(String parseDataFolder, String filterDataFolder, String filterSchema)
	{
		this.parseDataFolder = parseDataFolder;
		this.filterDataFolder = filterDataFolder;
		this.filterSchema = filterSchema;
		loadFilterSchema();
	}
	
	void loadFilterSchema()
	{
		
	}
	
	void filterData()
	{
		try
		{
			BufferedReader br=new BufferedReader(new FileReader(parseDataFolder + "urls.txt"));
			BufferedWriter bwUrl=new BufferedWriter(new FileWriter(filterDataFolder + "urls.txt", true));
			String url;
			while((url=br.readLine())!=null)
			{
				if(filterByUrl(url))
				{
					moveToFilteredData(url);
					System.out.println(url);
					bwUrl.append(url + "\n");
					continue;
				}
				if(filterByContent(url))
					moveToFilteredData(url);
			}
			br.close();
			bwUrl.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception in filterData() - " + e);
		}
	}
	
	void moveToFilteredData(String url)
	{
		try
		{
			MessageDigest md=MessageDigest.getInstance("MD5");
			md.update(url.getBytes(), 0, url.length());
			String digest = new BigInteger(1, md.digest()).toString(16);
			File srcFile = new File(parseDataFolder + digest + ".content");
			File destFile = new File(filterDataFolder + digest + ".content");
			FileUtils.copyFile(srcFile, destFile);
		}
		catch(Exception e)
		{
			System.out.println("Exception in moveToFilteredData() - " + e);
		}
	}
	
	Boolean filterByUrl(String url)
	{
		for(String keyword : urlKeyWords)
		{
			if(url.contains(keyword))
				return true;
		}
		return false;
	}
	
	Boolean filterByContent(String url)
	{
		try
		{
			MessageDigest md=MessageDigest.getInstance("MD5");
			md.update(url.getBytes(), 0, url.length());
			String digest = new BigInteger(1, md.digest()).toString(16);
			BufferedReader br=new BufferedReader(new FileReader(parseDataFolder + digest + ".content"));
			String line;
			while((line=br.readLine())!=null)
			{
				for(String keyword : contentKeyWords)
				{
					if(line.contains(keyword))
						return true;
				}
			}
		
		}
		catch(Exception e)
		{
			System.out.println("Exception in filterByContent() - " + e);
		}
		return false;
	}
	
	public static void main(String args[])
	{
		ContentFilter filter = new ContentFilter("/home/adarshms/academics/cs410/project/parse_data/", "/home/adarshms/academics/cs410/project/filter_data/", "/home/adarshms/academics/cs410/project/filter_schema.xml/");
		filter.filterData();
	}
}
