package edu.uiuc.ras;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class SegmentDataReader 
{
	private String crawlDataFolder;
	private String readDataFolder;
	private String nutchHome;
	
	public SegmentDataReader(String crawlDataFolder, String readDataFolder, String nutchHome)
	{
		this.crawlDataFolder = crawlDataFolder;
		this.readDataFolder = readDataFolder;
		this.nutchHome = nutchHome;
	}
	
	void readContent() throws IOException, InterruptedException
	{
		String rawSegmentDataFolder = crawlDataFolder + "/segments/";
		File[] rawSegmentFolders = new File(rawSegmentDataFolder).listFiles();
		for(File folder: rawSegmentFolders)
			readSegment(folder.getName());
	}
	
	void readSegment(String rawSegmentFolderName)
	{
		try
		{
			String rawSegmentFolderPath = crawlDataFolder + "segments/" + rawSegmentFolderName;
			String segmentFolderPath = readDataFolder + "segments/" + rawSegmentFolderName;
			String readSegmentCommand = nutchHome + "bin/nutch readseg -dump " + rawSegmentFolderPath + " " + segmentFolderPath + " -nocontent -nofetch -nogenerate -noparse -noparsedata";
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(readSegmentCommand);
			System.out.println(readSegmentCommand);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line=null;
            while((line=input.readLine()) != null) 
            {
                 System.out.println(line);
            }
			int exitValue = pr.waitFor();
			System.out.println("Exit value : " + exitValue);
		}
		catch(Exception e)
		{
			System.out.println("Exception in readSegment " + e);
		}
	}
	
	public static void main(String args[]) throws IOException, InterruptedException
	{
		SegmentDataReader read = new SegmentDataReader("/home/adarshms/academics/cs410/project/crawl_data/", "/home/adarshms/academics/cs410/project/read_data/", "/home/adarshms/academics/cs410/project/nutch/");
		read.readContent();
	}
}