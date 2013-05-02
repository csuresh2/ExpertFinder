package edu.uiuc.ras;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alchemyapi.api.AlchemyAPI;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

/**
 * This class is used to perform two tasks
 * 1) Incorporate feedback from Google for each named entity
 * 2) Tag concepts in the feedback documents returned by google, in order to enrich the profile for each entity
 * 
 * Output: Adds concept keywords to the profile document for each named entity
 * 
 * 
 * @author rucha
 * 
 */

public class ConceptTagger {

	String persondatafolder;
	String taggedconceptdatafolder;
	
	/*
	 * Constructor that takes in input and output folder names as arguments 
	 * to instantiate an object.
	 */
	public ConceptTagger(String inputfolder, String outputfolder)
	{
		persondatafolder=inputfolder;
		taggedconceptdatafolder=outputfolder;
	}
	
	/*
	 * This method returns the top 10 URLS from google. The query string is the name of the entity file 
	 * which corresponds to the name of the person entity
	 */
	public List<String> getURLS(File ipfile)
	{
		String query_text=ipfile.getName();
		//System.out.println(query_text);
		List<String> urls=new ArrayList<String>();
		
		Customsearch customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), null);

	    try {
	        com.google.api.services.customsearch.Customsearch.Cse.List list = customsearch.cse().list(query_text);
	        list.setKey("AIzaSyCF2MoqZXlsa96eBy7G_ZY7dz6KtNXbMmI");
	        list.setCx("012838771652296013735:scgi1baa_tk");
	        Search results = list.execute();
	        List<Result> items = results.getItems();
	        System.out.println("Size isssssssssssss " + results.toPrettyString());
	        for(Result result:items)
	        	urls.add(result.getLink().toString());
	        
	        for(String s:urls)
	        	System.out.println(s);
	        
	      //  System.out.println(items.get(1).getLink());
	        

	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    
	    return urls;
	}
	
	/*
	 * This method adds concept keywords to the profile document for each named entity, using Alchemy API
	 * @input: 1) List of feedback URLS from Google 2) Name of the person entity
	 * @output: Concept keywords tagged from the text of the feedback documents
	 */
	public void generateConceptFiles(List<String> urls, String person_name)
	{
		int counter=1;
		try
		{
		for(String url: urls)
		{
			/*if(url.equals("http://charuaggarwal.net/text-content.pdf"))
			{
				System.out.println("pdf");
				continue;
			}*/
			//String url=urls.get(0);
			//System.out.println("url"+url);
			
			Document doc = Jsoup.connect(url).ignoreContentType(true).get();
			
			String text=doc.body().text();
			
			AlchemyAPI a=AlchemyAPI.GetInstanceFromString("d467516bddf39b258993c26ec1115984cf15e0cd");
			
			org.w3c.dom.Document d=a.TextGetRankedConcepts(text);
			
			String concept_doc=getStringFromDocument(d);
			
			
			File file = new File(taggedconceptdatafolder + ""+ person_name);
			if (!file.exists()) {
				if (file.mkdir()) {
				//	System.out.println("Directory is created!");
				} else {
					System.out.println("Failed to create directory!");
				}
			}
			
			//System.out.println("Dir name:"+file.getAbsolutePath());
			
			String folder_name=file.getAbsolutePath();
			
			//System.out.println("Folder_Name" + folder_name);		
					

			File concept_file=new File(folder_name +"/"+ person_name + counter +".concept.xml");
			counter++;
			
			if(!concept_file.exists())
				concept_file.createNewFile();

			FileWriter fw=new FileWriter(concept_file);
			BufferedWriter bw=new BufferedWriter(fw);
			bw.write(concept_doc);

			bw.close();
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * This method returns the String representation of an XML document.
	 * 
	 */
		public String getStringFromDocument(org.w3c.dom.Document doc)
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
		 * Wrapper method which generates the tagged concepts from Google for each named entity
	 	*/
		public void tagConcepts()
		{
			File[] files = new File(persondatafolder).listFiles();
			
			for(File ipfile: files)
			{
				String person_name=ipfile.getName();
				System.out.println(person_name);
				List<String> urls=getURLS(ipfile);
				generateConceptFiles(urls, person_name);
			}
		}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try
		{
			ConceptTagger tc=new ConceptTagger("/home/adarshms/academics/cs410/project/extract_data/","/home/adarshms/academics/cs410/project/tagged_data/");
			tc.tagConcepts();
			
			/*File f=new File("ChengXiang Zhai");
			String person_name=f.getName();
			
			List<String> urls=tc.getURLS(f);
			tc.generateConceptFiles(urls, person_name);*/
			
			System.out.println("Works");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


	}

}
