package ch.maybites.px1m0d.plug;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.config.exception.PlugConfigurationException;
import proxml.XMLElement;

public class PlugType {

	private XMLElement _myElement;
	private String _myName, _myFielName;
	private String _myPath;

	public PlugType(XMLElement element, String name) throws PlugConfigurationException{
		try{
			_myElement = element;
			_myFielName = name;
			_myName = name.replace('/', '.');
			_myPath = _myElement.getChild(0).getAttribute("path");
		} catch (proxml.InvalidAttributeException e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Parsing Error in XML-Tree: ");
			element.printElementTree();
			throw new PlugConfigurationException(e.getMessage());
		}
	}

	public String getFileName(){
		return _myFielName;
	}

	public String getName(){
		return _myName;
	}

	public String getPlugPath(){
		return _myPath;
	}

	public XMLElement getParamaters(){
		return _myElement.getChild(0);
	}

}
