package edu.uiuc.ras;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.*;

public class SegmentDataParser 
{
	private String readDataFolder;
	private String parseDataFolder;
	//private String linkDataFile;
	
	public SegmentDataParser(String readDataFolder, String parseDataFolder)
	{
		this.readDataFolder = readDataFolder;
		this.parseDataFolder = parseDataFolder;
		//linkDataFile = "/home/adarshms/academics/cs498/project/linkData/links.txt";
	}
	
	/*void parseAnchorLinks()
	{
		try
		{
			BufferedReader bReader = new BufferedReader(new FileReader(linkDataFile));
			String line;
			String url = "";
			String anchorTextData  = "";
			while ((line = bReader.readLine()) != null)
			{
				if(line.startsWith("http://") && line.endsWith("Inlinks:"))
				{
					if(!url.isEmpty())
					{
						dumpAnchorTextData(url, anchorTextData);
					}
					url = (line.split("\\s+"))[0];
				}
				else if(line.startsWith(" fromUrl: "))
				{
					
					if(!url.isEmpty())
					{
						if((line.split(" anchor: ")).length > 1)
							anchorTextData += "\n" + (line.split(" anchor: "))[1];
					}
				}
			}
			bReader.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception in indexAnchorLinks() - " + e);
		}
	}*/
	
	void parseContent()
	{
		File[] segmentFolders = new File(readDataFolder + "segments/").listFiles();
		for(File folder: segmentFolders)
			parseSegment(folder.getName());
	}
	
	void parseSegment(String segmentFolderName)
	{
		try
		{
			BufferedReader br=new BufferedReader(new FileReader(readDataFolder + "segments/" + segmentFolderName + "/dump"));
			BufferedWriter bwUrl=new BufferedWriter(new FileWriter(parseDataFolder + "urls.txt", true));
			String curline;
			while((curline=br.readLine())!=null)
			{
				if(curline.contains("Recno::"))
				{
					String url=br.readLine();
					url = url.substring(url.indexOf("http://"));
					MessageDigest md=MessageDigest.getInstance("MD5");
					md.update(url.getBytes(), 0, url.length());
					
					String digest = new BigInteger(1, md.digest()).toString(16);
					curline=br.readLine(); //skip blank line
					curline=br.readLine(); //skip "ParseText"
					
					String parseText=br.readLine();
					File output=new File(parseDataFolder+digest+".content");
					
					if (!output.exists()) {
						output.createNewFile();
					}
					
					FileWriter fwContent = new FileWriter(output.getAbsoluteFile());
					BufferedWriter bwContent=new BufferedWriter(fwContent);
					bwContent.write(parseText);
					bwContent.close();
					bwUrl.append(url + "\n");
				}
			}
			bwUrl.close();
			br.close();
		}
		
		catch(Exception e)
		{
			System.out.println("Exception in parseSegment() " + e);
		}

		
	}
	
	/*void dumpAnchorTextData(String url, String anchorTextData)
	{
		try
		{
			MessageDigest md=MessageDigest.getInstance("MD5");
			md.update(url.getBytes(), 0, url.length());
			String urlHash = new BigInteger(1, md.digest()).toString(16);
			String fileName = parseDataFolder + urlHash + ".anchor";
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(fileName), true));
			bWriter.write(anchorTextData);
			bWriter.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception in dumpAnchorTextData() - " + e);
		}
	}*/
	
	public static void main(String args[])
	{
		SegmentDataParser index = new SegmentDataParser("/home/adarshms/academics/cs410/project/read_data/", "/home/adarshms/academics/cs410/project/parse_data/");
		//index.indexAnchorLinks();
		index.parseContent();
	}
}