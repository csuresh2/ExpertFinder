package edu.uiuc.ras.extract;
import java.io.File;
import java.util.ArrayList;

/**
 * This class provides utility methods for getting a list of all xml files in the 
 * directory "folder".
 * 
 * @author chethans
 */
public class XMLListBuilder
{
	/*
	 * This method is for getting a list of all xml files in the 
	 * directory "folder".
	 */
	public static ArrayList<File> getXMLFiles(File folder)
	{
	    ArrayList<File> aList = new ArrayList<File>();

	    File[] files = folder.listFiles();
	    for (File pf : files)
	    {
	    	if (pf.isFile() && isXMLFile(pf).indexOf("xml") != -1)
	    	{
	    		aList.add(pf);
	    	}
	    }

	    return aList;
	}

	/*
	 * This is a helper method that identifies if the file 'f'
	 * is a xml file.
	 */
	public static String isXMLFile(File f)
	{
		if (f.getName().indexOf(".") == -1)
		{
			return "";
		}
		else
		{
			return f.getName().substring(f.getName().length() - 3, f.getName().length());
		}
	}
}	
