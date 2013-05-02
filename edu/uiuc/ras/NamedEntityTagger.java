package edu.uiuc.ras;
import org.w3c.dom.Document;

import com.alchemyapi.api.AlchemyAPI;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;

/**
 * This class tags the entities and concepts in the raw text documents obtained from the crawler
 * 
 * Output: Creates two documents per raw text input file. The first output file contains the tagged entities
 * Second contains the tagged concepts
 * 
 * @author rucha
 * 
 */
public class NamedEntityTagger
{

	String parsedDatafolder;
	String taggeddatafolder;


	public NamedEntityTagger(String inputfolder, String outputfolder)
	{
		parsedDatafolder=inputfolder;
		taggeddatafolder=outputfolder;

	}
	
	/*
	 * Wrapper method which generates the tagged output files for each input file
	 */
	public void tagData()
	{
		File[] files = new File(parsedDatafolder).listFiles();
		for(File ipfile: files)
			generateTaggedOutput(ipfile);
	}
	
	/*
	 * This method returns the String representation of an XML document.
	 * 
	 */
	public String getStringFromDocument(Document doc)
	{
	    try
	    {
	       DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       transformer.transform(domSource, result);
	       return writer.toString();
	    }
	    catch(TransformerException ex)
	    {
	       ex.printStackTrace();
	       return null;
	    }
	} 

	/*
	 * This method generates the tagged output using the Alchemy api.
	 * @input: Raw text file
	 * @output: Two output files per input file: 1) Tagged entities file 2)Tagged concepts file
	 */
	public void generateTaggedOutput(File ipfile)
	{
		try
		{
			String ipfilename=ipfile.getName();
			
			BufferedReader br=new BufferedReader(new FileReader(new File(parsedDatafolder+ipfilename)));

			String text=br.readLine();
			//System.out.println(text);
			
			AlchemyAPI a=AlchemyAPI.GetInstanceFromString("ce3494152ad3be9e4ec0322576a48efd06111040");

			Document xmldoc1=a.TextGetRankedNamedEntities(text);
			Document xmldoc2=a.TextGetRankedConcepts(text);

			String xmltext1=getStringFromDocument(xmldoc1);
			//System.out.println("Text:\n"+xmltext1);
			String xmltext2=getStringFromDocument(xmldoc2);
			//System.out.println("Text:\n"+xmltext2);

			File opfile1=new File(taggeddatafolder+ipfilename+".xml");

			File opfile2=new File(taggeddatafolder+ipfilename.replace("content","concept")+".xml");
			
			if(!opfile1.exists())
				opfile1.createNewFile();

			if(!opfile2.exists())
				opfile2.createNewFile();

			FileWriter fw1=new FileWriter(opfile1.getAbsoluteFile());
			BufferedWriter bw1=new BufferedWriter(fw1);
			bw1.write(xmltext1);

			FileWriter fw2=new FileWriter(opfile2.getAbsoluteFile());
			BufferedWriter bw2=new BufferedWriter(fw2);
			bw2.write(xmltext2);

			br.close();
			bw1.close();
			bw2.close();
			
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		try
		{
			NamedEntityTagger td=new NamedEntityTagger("/home/adarshms/academics/cs410/project/parse_data/","/home/adarshms/academics/cs410/project/tagged_data/");
			td.tagData();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
}
