package edu.uiuc.ras;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.uiuc.ras.extract.EntityDescriptor;
import edu.uiuc.ras.extract.XMLDriver;
import edu.uiuc.ras.extract.XMLListBuilder;

/**
 * This class extracts the Named entities from the output files of the IE tagging 
 * engine.
 * 
 * Output: Creates a document per entity, each containing all the IE tagged values
 * 		   along with the list of organizations it belongs to.
 * 
 * @author chethans
 * 
 */
public class NamedEntityExtractor
{
	public static String INPUT_FILE_NAME_PREFIX ;
	public static String OUTPUT_FILE_NAME_PREFIX;
	HashMap<String, ArrayList<EntityDescriptor>> entitiesMap;
	HashMap<String, ArrayList<File>> entityXMLsMap;
	public String inputFileNamePrefix;
	public ArrayList<File> xmlFilesList;
	public ArrayList<File> contentXMLFilesList;
	public ArrayList<File> conceptXMLFilesList;
	public ArrayList<File> entityFoldersList;

	public NamedEntityExtractor(String inputFolder, String outputFolder)
	{
		INPUT_FILE_NAME_PREFIX = inputFolder;
		OUTPUT_FILE_NAME_PREFIX = outputFolder;
		entitiesMap = new HashMap<String, ArrayList<EntityDescriptor>>();
	}

	void buildContentAndConceptXMLFilesList()
	{
		contentXMLFilesList = new ArrayList<File>();
		conceptXMLFilesList = new ArrayList<File>();

		for(int i=0; i < xmlFilesList.size(); i++)
		{
			if(xmlFilesList.get(i).getName().contains("content"))
			{
				contentXMLFilesList.add(xmlFilesList.get(i));
			}
			else if(xmlFilesList.get(i).getName().contains("concept"))
			{
				conceptXMLFilesList.add(xmlFilesList.get(i));
			}
		}

		Collections.sort(contentXMLFilesList, new FileComparator());
		Collections.sort(conceptXMLFilesList, new FileComparator());
	}

	/*
	 * This method parses the output file of IE tagger and constructs the entitiesMap 
	 * by building a key value relationship between the entity (key) and 
	 * EntityDescriptors (values).
	 */
	void parseInput()
	{
		// Parse it based on the output file of IE tagger.
		try
		{
			if(conceptXMLFilesList.size() != contentXMLFilesList.size())
			{
				throw new Exception("Number of contentXMLFiles and conceptXMLFiles " +
						"are not the same");
			}

			// Parse the XML file(s) and update the parsedObject.
			for(int i=0; i < contentXMLFilesList.size(); i++)
			{
				XMLDriver driver = new XMLDriver(contentXMLFilesList.get(i), 
						conceptXMLFilesList.get(i));
				ArrayList<String> keys = driver.getParsedObject().getKeys();
				EntityDescriptor value = driver.getParsedObject().getDescriptor();

				// Update the hashMap with the keys and values.
				for(int j = 0; j < keys.size(); j++)
				{
					if(!entitiesMap.containsKey(keys.get(j)))
					{
						ArrayList<EntityDescriptor> list = new ArrayList<EntityDescriptor>();
						list.add(value);
						entitiesMap.put(keys.get(j), list);
					}
					else
					{
						ArrayList<EntityDescriptor> list = entitiesMap.get(keys.get(j));
						list.add(value);
						entitiesMap.put(keys.get(j), list);
					}
				}
			}
		}
		// Must handle each exception individually - refactor later.
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Eliminate irrelevant entities and its descriptors.
	 */
	void eliminateEntityDescriptors()
	{
		// Implement it based on a probabilistic Unigram model or alike.
		
	}

	/*
	 * This method resolves all the entity based conflicts; for example same 
	 * person but identified as different entities or different persons but 
	 * was tagged as same person (reason being the same last name).
	 */
	void resolveEntityConflicts()
	{
		
	}

	/*
	 * Flush the entitiesMap on to the output files such that each entity forms 
	 * a document and contains its values and organization it belongs to.
	 */
	void flushEntitiesMap()
	{
		for (Map.Entry<String, ArrayList<EntityDescriptor>> entry : entitiesMap.entrySet())
		{
			String key = entry.getKey();
			ArrayList<EntityDescriptor> values = entry.getValue();

			for(int i = 0; i < values.size(); i++)
			{
				if(!(new File(OUTPUT_FILE_NAME_PREFIX + key)).exists() && 
						isValidFileName(OUTPUT_FILE_NAME_PREFIX + key))
				{
					values.get(i).flushToFile(OUTPUT_FILE_NAME_PREFIX + key);
				}
				else if(isValidFileName(OUTPUT_FILE_NAME_PREFIX + key))
				{
					values.get(i).flushToFile(OUTPUT_FILE_NAME_PREFIX + key);
				}
			}
		}
	}

	public boolean isValidFileName(final String aFileName) {
	    final File aFile = new File(aFileName);
	    boolean isValid = true;
	    try {
	        if (aFile.createNewFile()) {
	            aFile.delete();
	        }
	    } catch (IOException e) {
	        isValid = false;
	    }
	    return isValid;
	}

	/*
	 * This method finds all xml files in the directory inputFileNamePrefix.
	 */
	void findAllXMLFiles()
	{
		xmlFilesList = XMLListBuilder.getXMLFiles(new File(inputFileNamePrefix));
	}

	/*
	 * Get the list of all xml files the need to be parsed.
	 */
	public ArrayList<File> getXMLFilesList()
	{
		return xmlFilesList;
	}

	void populateEntityDirectoryList()
	{
		entityFoldersList = new ArrayList<File>();

	    File[] files = new File(inputFileNamePrefix).listFiles();

	    for (File pf : files)
	    {
	    	if (pf.isDirectory())
	    	{
	    		entityFoldersList.add(pf);
	    	}
	    }
	}

	void populateEntityXMLsMap()
	{
		for(int i=0; i < entityFoldersList.size(); i++)
		{
			String key = entityFoldersList.get(i).getName();

			if(!entityXMLsMap.containsKey(key))
			{
				entityXMLsMap.put(key, XMLListBuilder.getXMLFiles(
						new File(inputFileNamePrefix + key + "/")));
			}
			else
			{
				entityXMLsMap.put(key, XMLListBuilder.getXMLFiles(
						new File(inputFileNamePrefix + key + "/")));
			}
		}
	}

	void parseEntityXMLList()
	{
		populateEntityXMLsMap();

		// Parse it based on the output file of IE tagger.
		try
		{
			for (Map.Entry<String, ArrayList<File>> entry : entityXMLsMap.entrySet())
			{
				ArrayList<File> xmlFilesList = entry.getValue();

				// Parse the XML file(s) and update the parsedObject.
				for(int i=0; i < xmlFilesList.size(); i++)
				{
					XMLDriver driver = new XMLDriver(xmlFilesList.get(i), true);
					EntityDescriptor value = driver.getParsedObject().getDescriptor();

					// Update the hashMap with the key and value.
					if(!entitiesMap.containsKey(entry.getKey()))
					{
						ArrayList<EntityDescriptor> list = new ArrayList<EntityDescriptor>();
						list.add(value);
						entitiesMap.put(entry.getKey(), list);
					}
					else
					{
						ArrayList<EntityDescriptor> list = entitiesMap.get(entry.getKey());
						list.add(value);
						entitiesMap.put(entry.getKey(), list);
					}
				}
			}
		}
		// Must handle each exception individually - refactor later.
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Creates the entity files, where each file consists of the key values that 
	 * defines or describes that person.
	 */
	public void createEntityFiles()
	{
		this.inputFileNamePrefix = INPUT_FILE_NAME_PREFIX;
		findAllXMLFiles();
		buildContentAndConceptXMLFilesList();
		parseInput();
		flushEntitiesMap();
	}

	public void enrichEntityFiles()
	{
		entitiesMap = new HashMap<String, ArrayList<EntityDescriptor>>();
		entityXMLsMap = new HashMap<String, ArrayList<File>>();

		populateEntityDirectoryList();
		parseEntityXMLList();
		flushEntitiesMap();
	}

	public static void main(String[] args)
	{
		NamedEntityExtractor extractor = new NamedEntityExtractor("/home/adarshms/academics/cs410/project/tagged_data/", "/home/adarshms/academics/cs410/project/extract_data/");
		extractor.createEntityFiles();
		extractor.enrichEntityFiles();
	}

	private class FileComparator implements Comparator<File>
	{
		public int compare(File f1, File f2)
		{
			return f1.getName().compareTo(f2.getName());
		}
	}
}
