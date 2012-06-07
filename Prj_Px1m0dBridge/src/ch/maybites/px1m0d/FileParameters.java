package ch.maybites.px1m0d;

import ch.maybites.xml.XmlValidator;
import proxml.*;
import processing.core.*;

public class FileParameters {
	private XMLInOut xmlPreferences;
	private XMLInOut xmlsetup;
	private XMLElement setupElement;
	private XMLElement preferenceElement;
	
	private PApplet applet;
	private XmlValidator validator;
	
	private final String FILEPATH_SETUP = "settings.xml";
	private final String FILEPATH_PREFERENCES = "preferences.xml";
	
	public final String PARAM_DefaultOutputPxlmodMidiConnection = "DefaultOutputPxlmodMidiConnection";
	public final String PARAM_DefaultOutputWorldMidiConnection = "DefaultOutputWorldMidiConnection";
	public final String PARAM_DefaultInputPxlmodMidiConnection = "DefaultInputPxlmodMidiConnection";
	public final String PARAM_DefaultInputWorldMidiConnection = "DefaultInputWorldMidiConnection";

	public final String PARAM_DefaultNatebuSerialConnection = "DefaultNatebuSerialConnection";
	
	public final String PARAM_oscServerUrl = "oscServerUrl";
	public final String PARAM_oscServerPort = "oscServerPort";
	public final String PARAM_oscListenerPort = "oscListenerPort";
	public final String PARAM_oscNatebuUrl = "oscNatebuUrl";
	public final String PARAM_oscNatebuPort = "oscNatebuPort";

	public final String PARAM_sceneLatency = "sceneLatency";
	
	public final String PARAM_ = "";

	FileParameters(PApplet a){
		applet = a;
		validator = XmlValidator.getInstance();
		xmlsetup = new XMLInOut(applet, this);
		xmlPreferences = new XMLInOut(applet, this);
		loadSetup(FILEPATH_SETUP);
		loadPreferences(FILEPATH_PREFERENCES);
		while(setupElement == null || preferenceElement == null){
			try{
				Thread.sleep(10); // do nothing for 100 miliseconds
			} catch(InterruptedException e){
				e.printStackTrace();
			} 
		}
		GlobalPreferences.getInstance().setLatency(this.getPreferenceValue(PARAM_sceneLatency));
	}
	
	public void savePreferences(){
		xmlPreferences.saveElement(preferenceElement, FILEPATH_PREFERENCES);
	}
	
	private void loadPreferences(String preferencesFilepath){
		//validator.validate("data/" + preferencesFilepath);
		xmlPreferences.loadElement(preferencesFilepath);
	}

	private void loadSetup(String setupFilepath){
		//validator.validate("data/" + setupFilepath);
		xmlsetup.loadElement(setupFilepath);
	}

	public String getSetupValue(String name){
		XMLElement[] children = setupElement.getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getAttribute("name").equals(name)){
				return translateFromEscapeChars(children[i].getAttribute("value"));
			}
		}
		return "";
	}

	public String getPreferenceValue(String name){
		XMLElement[] children = preferenceElement.getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getAttribute("name").equals(name)){
				return translateFromEscapeChars(children[i].getAttribute("value"));
			}
		}
		return "";
	}

	public void setPreferenceValue(String name, String value){
		value = translateToEscapeChars(value);
		XMLElement[] children = preferenceElement.getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getAttribute("name").equals(name)){
				children[i].addAttribute("value", value);
				return;
			}
		}
		//if the code reaches this lines, no child element has been found, therefore:
		XMLElement child = new XMLElement("property");
		child.addAttribute("name", name);
		child.addAttribute("value", value);
		preferenceElement.addChild(child);
	}

	public void xmlEvent(XMLElement element){
		if(element.getElement().equals("setup")){
			setupElement = element;
			Debugger.getInstance().debugMessage(this.getClass(),"setup loaded");
		} else if(element.getElement().equals("preferences")){
			preferenceElement = element;
			Debugger.getInstance().debugMessage(this.getClass(),"preferences loaded");
		}
	}

	private String translateToEscapeChars(String source){
		if(source.indexOf("<") != -1){
			source = (replace(source, "<", "+lt"));
		}
		if(source.indexOf(">") != -1){
			source = (replace(source, ">", "+gt"));
		}
		return source;
	}
	
	private String translateFromEscapeChars(String source){
		if(source.indexOf("+gt") != -1){
			source = (replace(source, "+gt", ">"));
		}
		if(source.indexOf("+lt") != -1){
			source = (replace(source, "+lt", "<"));
		}
		return source;
	}
	
	private String replace(String source, String find, String replace){
		return (source.substring(0, source.indexOf(find)) + replace + source.substring(source.indexOf(find) + find.length()));
	}
	
}
