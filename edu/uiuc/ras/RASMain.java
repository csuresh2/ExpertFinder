package edu.uiuc.ras;
import java.io.IOException;


public class RASMain 
{
	public static void main(String args[]) throws IOException, InterruptedException
	{
		SegmentDataReader readData = new SegmentDataReader("/home/adarshms/academics/cs410/project/crawl_data/", 
				"/home/adarshms/academics/cs410/project/read_data/", "/home/adarshms/academics" +
				"/cs410/project/nutch/");
		readData.readContent();
		SegmentDataParser parseData = new SegmentDataParser("/home/adarshms/academics/cs410/project/read_data/", 
				"/home/adarshms/academics/cs410/project/parse_data/");
		parseData.parseContent();
		NamedEntityTagger tagData = new NamedEntityTagger("/home/adarshms/academics/cs410/project/parse_data/", 
				"/home/adarshms/academics/cs410/project/tagged_data/");;
		tagData.tagData();
		NamedEntityExtractor extractData = new NamedEntityExtractor("/home/adarshms/academics/cs410/project/" +
				"tagged_data/", "/home/adarshms/academics/cs410/project/extract_data/");
		extractData.createEntityFiles();
		ConceptTagger tagConcepts = new ConceptTagger("/home/adarshms/academics/cs410/project/" +
				"extract_data/","/home/adarshms/academics/cs410/project/tagged_data/");
		tagConcepts.tagConcepts();
		extractData.enrichEntityFiles();
	}
}
