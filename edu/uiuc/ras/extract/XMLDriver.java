package edu.uiuc.ras.extract;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * This is a xml driver class that parses the xml file and returns its values 
 * in a format desirable to the NamedEntityExtractor.
 * 
 * @author chethans
 */
public class XMLDriver
{
	ParsedXMLObject parsedObject;

	public XMLDriver() throws SAXException, IOException, 
		ParserConfigurationException
	{
		File xmlFile = new File("/home/chethans/Downloads/abc.content.xml");
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();

		parsedObject = new ParsedXMLObject();
		XMLHandler xmlHandler = new XMLHandler(parsedObject);
		xmlHandler.handleResultTag(doc);
	}

	public XMLDriver(File xmlFile, boolean isConceptFile) throws SAXException, IOException, 
		ParserConfigurationException
	{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();

		parsedObject = new ParsedXMLObject();
		XMLHandler xmlHandler = new XMLHandler(parsedObject);
		
		if(isConceptFile)
		{
			xmlHandler.handleConceptResultTag(doc);
		}
		else
		{
			xmlHandler.handleResultTag(doc);
		}
	}

	public XMLDriver(File contentXMLFile, File conceptXMLFile) throws SAXException, IOException, 
		ParserConfigurationException
	{
		// Parse content file.
		DocumentBuilderFactory contentDocBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder contentDocBuilder = contentDocBuilderFactory.newDocumentBuilder();
		Document contentDoc = contentDocBuilder.parse(contentXMLFile);
		contentDoc.getDocumentElement().normalize();

		// Parse concept file.
		DocumentBuilderFactory conceptDocBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder conceptDocBuilder = conceptDocBuilderFactory.newDocumentBuilder();
		Document conceptDoc = conceptDocBuilder.parse(conceptXMLFile);
		conceptDoc.getDocumentElement().normalize();

		// Build a single parsed object for both content and concept xml files.
		parsedObject = new ParsedXMLObject();
		XMLHandler xmlHandler = new XMLHandler(parsedObject);
		System.out.println("Handling contents xml:\n");
		xmlHandler.handleResultTag(contentDoc);
		System.out.println("\nHandling concepts xml:\n");
		xmlHandler.handleConceptResultTag(conceptDoc);
	}

	public ParsedXMLObject getParsedObject()
	{
		return parsedObject;
	}

	/*public static void main(String[] args) throws SAXException, IOException, 
		ParserConfigurationException
	{
		XMLDriver driver = new XMLDriver();
	}*/
}
