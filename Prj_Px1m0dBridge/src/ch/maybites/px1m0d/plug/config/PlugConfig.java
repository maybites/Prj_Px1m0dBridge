package ch.maybites.px1m0d.plug.config;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.message.Message;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.drawing.*;
import ch.maybites.px1m0d.plug.drawing.effects.icons.*;
import ch.maybites.px1m0d.plug.config.exception.MessageCreationException;
import ch.maybites.px1m0d.plug.config.exception.PlugConfigurationException;
import de.cnc.expression.AbstractRuntimeEnvironment;
import de.cnc.expression.exceptions.ExpressionEvaluationException;
import de.cnc.expression.exceptions.ExpressionParseException;
import proxml.XMLElement;
import java.util.*;
import gestalt.shape.Color;
import java.io.*;

public class PlugConfig implements Serializable{
	private static final long serialVersionUID = 1L;

	public String Path;
	public String Name;
	int numberOfJobs;
	public Effect displayIcon;
	public Color displayIconColor;
	public String displayIconLabel;
	public String[] displayIconLabelPipes;
	public String displayIconPath;

	private JobConfig[] _myJobs;

	private final static String UNDEFINED = "undefined";

	private final static String TAG_PLUG = "plug";
	private final static String TAG_PARAMETERS = "parameter";
	private final static String TAG_JOBS = "jobs";
	private final static String TAG_JOB = "job";
	private final static String TAG_DISPLAY = "display";
	private final static String TAG_ICON = "icon";
	private final static String TAG_COLOR = "color";
	private final static String TAG_LABEL = "label";
	private final static String[] TAG_LABEL_PIPE = {
		"labelPipe0",
		"labelPipe1",
		"labelPipe2",
		"labelPipe3",
		"labelPipe4",
		"labelPipe5",
		"labelPipe6",
		"labelPipe7",
		"labelPipe8"	
	};

	private final static String ATTR_PLUG_NAME = "name";
	private final static String ATTR_PLUG_PATH = "path";

	private final static String ATTR_ICON_PATH = "path";

	private final static String ATTR_COLOR_RED = "red";
	private final static String ATTR_COLOR_GREEN = "green";
	private final static String ATTR_COLOR_BLUE = "blue";
	private final static String ATTR_COLOR_ALPHA = "alpha";

	transient private XMLElement myPlugElement;
	
	private final static String DEFAULT_ICON_PATH = "ch.maybites.px1m0d.plug.drawing.effects.icons.IconBasic";
	private final static String DEFAULT_ICON_LABEL = "xxx";
	private final static String DEFAULT_ICON_LABELPIPE = "---";
	private Color defaultC = new Color(1.0f, 0f, 0f, 1f);

	public PlugConfig(){
		myPlugElement = null;
		Path = UNDEFINED;
		Name = UNDEFINED;
	}

	public PlugConfig(XMLElement data) throws PlugConfigurationException{
		try{
			myPlugElement = data;
			Path = getPlugPath();
			Name = getPlugName();
			numberOfJobs = getNumberOfJobs();
			displayIconLabelPipes = new String[9];

			displayIconColor = this.getIconColor();
			displayIconLabel = this.getIconLabel();
			displayIconPath = this.getIconPath();
			displayIcon = iconFactory(displayIconPath, displayIconColor, displayIconLabel);
			
			for(int i = 0; i < 9; i++){
				displayIconLabelPipes[i] = this.getIconLabelPipe(i);
			}
			
			if(numberOfJobs > 0){
				_myJobs = new JobConfig[numberOfJobs];
				for(int i = 0; i < numberOfJobs; i++){
					_myJobs[i] = new JobConfig(this.getJob(i));
				}
			}
		} catch (proxml.InvalidAttributeException e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Parsing Error in XML-Tree: ");
			data.printElementTree();
			throw new PlugConfigurationException(e.getMessage());
		}
	}

	/*
	 * It can easely used inside a for loop:
	 * 
	 * for(int i = 0; Job(i) != null; i++){
	 * 	do your thing here
	 * }
	 * 
	 * @param 		index of job
	 * @return		null if no job at the specified index exists.
	 */
	public JobConfig Job(int index){
		if(_myJobs != null && _myJobs.length > index){
			return _myJobs[index];
		}
		return null;
	}

	public void parse(AbstractRuntimeEnvironment runEnv) throws ExpressionParseException{
		for(int i = 0; i < numberOfJobs; i++){
			_myJobs[i].parse(runEnv);
		}
	}

	public Vector<Message> evaluate(AbstractRuntimeEnvironment runEnv, PlugParameter parameter, boolean enforceTrigger){
		Vector<Message> msgs = new Vector<Message>();
		for(int i = 0; i < numberOfJobs; i++){
			try{
				Message[] retrn = _myJobs[i].evaluate(runEnv, parameter, enforceTrigger);
				if(retrn != null){
					for(int j = 0; j < retrn.length; j++)
						msgs.add(retrn[j]);
				}
			} catch (MessageCreationException e){
				Debugger.getInstance().errorMessage(getClass(), "Message Creation Error in Plug: " + this.Name);
				Debugger.getInstance().errorMessage(getClass(), " JobName: " + Job(i).Name);
				Debugger.getInstance().errorMessage(getClass(), " Message: " + e.getMessage());
			} catch (ExpressionEvaluationException e){
				Debugger.getInstance().errorMessage(getClass(), "Expression Error in Plug: " + this.Name);
				Debugger.getInstance().errorMessage(getClass(), " JobName: " + Job(i).Name);
				Debugger.getInstance().errorMessage(getClass(), " Message: " + e.getMessage());
			}
		}
		return msgs;
	}

	@SuppressWarnings("unchecked")
	private Effect iconFactory(String iconPath, Color color, String label){
		Effect icon = null;
		try{
			Class[] constructorParameter = {color.getClass()};
			Object[] instanceParameter = {color};
			java.lang.reflect.Constructor c = Class.forName(iconPath).getConstructor(constructorParameter);
			icon = (Effect)c.newInstance(instanceParameter);
		}catch(Exception e){
			Debugger.getInstance().errorMessage(getClass(), "effectFactory("+iconPath+"): unable to create specified icon");
			Debugger.getInstance().errorMessage(getClass(), "  most likely cause: check plug configuration file for missspellings");
			Debugger.getInstance().errorMessage(getClass(), "  check constructors..: " + e.getClass().getName());
			for(int i = 0; i < e.getStackTrace().length; i++){
				Debugger.getInstance().errorMessage(getClass(), e.getStackTrace()[i].toString());
			}
			Debugger.getInstance().errorMessage(getClass(), "Returning default Icon instead");
			return new IconBasic(color);
		}
		return icon;
	}
	
	private boolean hasPlug(){
		return (getPlug() != null);
	}

	private XMLElement getPlug(){
		if(myPlugElement != null && myPlugElement.getName().equals(TAG_PLUG)){
			return myPlugElement;
		}
		return null;
	}

	private String getPlugPath(){
		if(hasPlug())
			return getPlug().getAttribute(ATTR_PLUG_PATH);
		return UNDEFINED;
	}

	private String getPlugName(){
		if(hasPlug())
			return getPlug().getAttribute(ATTR_PLUG_NAME);
		return UNDEFINED;
	}

	private boolean hasParameters(){
		return (getParameters() != null);
	}

	private XMLElement getParameters(){
		if(hasPlug()){
			for(int i = 0; i < myPlugElement.getChildren().length; i++){
				if(myPlugElement.getChildren()[i].getName().equals(TAG_PARAMETERS)){
					return myPlugElement.getChildren()[i];
				}
			}
		}
		return null;
	}

	private boolean hasDisplay(){
		return (getDisplay() != null);
	}

	private XMLElement getDisplay(){
		if(hasParameters()){
			XMLElement[] children = getParameters().getChildren();
			for(int i = 0; i < children.length; i++){
				if(children[i].getName().equals(TAG_DISPLAY)){
					return children[i];
				}
			}
		}
		return null;
	}
	
	private String getIconPath(){
		if(hasIcon())
			return getIcon().getAttribute(ATTR_ICON_PATH);
		return DEFAULT_ICON_PATH;
	}

	private boolean hasIcon(){
		return (getIcon() != null);
	}

	private XMLElement getIcon(){
		if(hasDisplay()){
			XMLElement[] children = getDisplay().getChildren();
			for(int i = 0; i < children.length; i++){
				if(children[i].getName().equals(TAG_ICON)){
					return children[i];
				}
			}
		}
		return null;
	}

	private Color getIconColor(){
		if(hasIcon()){
			XMLElement[] children = getIcon().getChildren();
			for(int i = 0; i < children.length; i++){
				if(children[i].getName().equals(TAG_COLOR)){
					return new Color(
							children[i].getFloatAttribute(ATTR_COLOR_RED) / 255,
							children[i].getFloatAttribute(ATTR_COLOR_GREEN) / 255,
							children[i].getFloatAttribute(ATTR_COLOR_BLUE) / 255,
							children[i].getFloatAttribute(ATTR_COLOR_ALPHA) / 255);
				}
			}
		}
		return defaultC;
	}

	private String getIconLabel(){
		if(hasIcon()){
			XMLElement[] children = getIcon().getChildren();
			for(int i = 0; i < children.length; i++){
				if(children[i].getName().equals(TAG_LABEL)){
					return children[i].getChild(0).getText();
				}
			}
		}
		return DEFAULT_ICON_LABEL;
	}

	private String getIconLabelPipe(int pipe){
		if(hasIcon()){
			XMLElement[] children = getIcon().getChildren();
			for(int i = 0; i < children.length; i++){
				if(children[i].getName().equals(TAG_LABEL_PIPE[pipe])){
					return children[i].getChild(0).getText();
				}
			}
		}
		return DEFAULT_ICON_LABELPIPE;
	}
	
	private boolean hasJobs(){
		return (getJobs() != null);
	}

	private XMLElement getJobs(){
		if(hasParameters()){
			XMLElement[] children = getParameters().getChildren();
			for(int i = 0; i < children.length; i++){
				if(children[i].getName().equals(TAG_JOBS)){
					return children[i];
				}
			}
		}
		return null;
	}

	private int getNumberOfJobs(){
		if(hasJobs()){
			XMLElement[] children = getJobs().getChildren();
			return children.length;
		}
		return 0;
	}

	private XMLElement getJob(int index){
		if(hasJobs()){
			XMLElement[] children = getJobs().getChildren();
			if(children[index].getName().equals(TAG_JOB)){
				return children[index];
			}
		}
		return null;
	}

	public static String translateBooleanChars(String source){
		if(source.indexOf(" GT ") != -1){
			source = (replace(source, " GT ", " > "));
		}
		if(source.indexOf(" LT ") != -1){
			source = (replace(source, " LT ", " < "));
		}
		if(source.indexOf(" AND ") != -1){
			source = (replace(source, " AND ", " && "));
		}
		if(source.indexOf(" OR ") != -1){
			source = (replace(source, " OR ", " || "));
		}
		return source;
	}

	public static String replace(String source, String find, String replace){
		return (source.indexOf(find) == -1)? source: replace(source.substring(0, source.indexOf(find)) + replace + source.substring(source.indexOf(find) + find.length()), find, replace);
	}

	public void print(){
		Debugger.getInstance().infoMessage(null, "Plug Data:");
		Debugger.getInstance().infoMessage(null, "           Path: " + Path);
		Debugger.getInstance().infoMessage(null, "           Name: " + Name);
		Debugger.getInstance().infoMessage(null, "           Icon:");
		Debugger.getInstance().infoMessage(null, "              Path: " + displayIconPath);
		Debugger.getInstance().infoMessage(null, "              Color: r:" + (displayIconColor.r * 255) + " g:" + (displayIconColor.g * 255) + " b:" + (displayIconColor.b * 255) + " a:" + (displayIconColor.a * 255));
		Debugger.getInstance().infoMessage(null, "              Label: " + displayIconLabel);

		for(int i = 0; i < getNumberOfJobs(); i++){
			_myJobs[i].print();
		}
	}

}
