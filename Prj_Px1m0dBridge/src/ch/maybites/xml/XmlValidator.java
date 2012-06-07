package ch.maybites.xml;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import ch.maybites.px1m0d.Debugger;

public class XmlValidator {

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;

	private XmlValidator() throws ParserConfigurationException {

		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");

		// Specify our own schema - this overrides the schemaLocation in the xml file
		//		factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", "file:./data/schemas/plugTypes.xsd");

		builder = factory.newDocumentBuilder();
		builder.setErrorHandler( new SimpleErrorHandler() );
	}

	static private XmlValidator _instance;

	static public XmlValidator getInstance(){
		if (_instance == null) {
			synchronized(XmlValidator.class) {
				if (_instance == null){
					try{
						_instance = new XmlValidator();
					} catch (ParserConfigurationException e){
						Debugger.getInstance().fatalMessage(null, "unable to create XmlValidator-Instance. System Exit. " + e.getMessage());
						System.exit(0);
					}
				}
			}
		}
		return _instance;
	}

	public void validate(String xmlFile){
		Debugger.getInstance().debugMessage(getClass(), "validate("+xmlFile+")...");
		try{
			Document document = builder.parse(xmlFile);
			Node rootNode  = document.getFirstChild();
			Debugger.getInstance().debugMessage(getClass(), "Root node: "+ rootNode.getNodeName());
		} catch (SAXException e){
			Debugger.getInstance().fatalMessage(getClass(), "XML Validation of file = "+xmlFile+" caused Parsing Error. System Exit. " + e.getMessage());
			System.exit(0);
		} catch (IOException e){
			Debugger.getInstance().fatalMessage(getClass(), "XML Validation of file = "+xmlFile+" caused IO Error. System Exit. " + e.getMessage());
			System.exit(0);
		}
		Debugger.getInstance().debugMessage(getClass(), "...validated");
	}
}
