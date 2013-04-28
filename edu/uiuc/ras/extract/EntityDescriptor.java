package edu.uiuc.ras.extract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * This class encloses the tagged values of the current object/entity and 
 * the list of organizations this entity belongs to.
 * 
 * @author chethans
 */
public class EntityDescriptor
{
	ArrayList<String> keyValues;
	ArrayList<String> organizationsList;
	ArrayList<String> conceptsList;

	public EntityDescriptor()
	{
		keyValues = new ArrayList<String>();
		organizationsList = new ArrayList<String>();
		conceptsList = new ArrayList<String>();
	}

	public EntityDescriptor(ArrayList<String> keyValues, ArrayList<String> organizationsList, 
			ArrayList<String> conceptsList)
	{
		this.keyValues = keyValues;
		this.organizationsList = organizationsList;
		this.conceptsList = conceptsList;
	}

	public ArrayList<String> getKeyValues()
	{
		return keyValues;
	}

	public ArrayList<String> getOrganizationsList()
	{
		return organizationsList;
	}

	public ArrayList<String> getConceptsList()
	{
		return conceptsList;
	}

	public void addKeyValue(String value)
	{
		keyValues.add(value);
	}

	public void addOrganization(String organization)
	{
		organizationsList.add(organization);
	}

	public void addConcept(String concept)
	{
		conceptsList.add(concept);
	}

	/*
	 * Writes itself to an output file in a tabbed spaced fashion.
	 */
	public void flushToFile(String filename)
	{
		try
		{
			// Create file 
			FileWriter fstream = new FileWriter(filename, true);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("\n");

			// Write all tagged values.
			out.write("Tagged Values List:\n");
			for(int i=0; i < keyValues.size(); i++)
			{
				out.write(keyValues.get(i));
				out.write("\n");
			}

			out.write("\n");

			// Write all organizations list
			out.write("Organizations List:\n");
			for(int i=0; i < organizationsList.size(); i++)
			{
				out.write(organizationsList.get(i));
				out.write("\n");
			}

			out.write("\n");

			// Flush Concepts onto the file.
			out.write("Concepts:\n");
			for(int i=0; i < conceptsList.size(); i++)
			{
				out.write(conceptsList.get(i));
				out.write("\n");
			}

			// Close the output stream
			out.close();
		}
		catch (Exception e)
		{
			//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
