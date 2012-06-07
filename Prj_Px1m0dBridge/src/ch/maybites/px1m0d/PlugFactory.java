package ch.maybites.px1m0d;

import proxml.*;
import processing.core.*;
import ch.maybites.px1m0d.message.Px1MessageSocket;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.config.PlugConfig;
import ch.maybites.px1m0d.plug.config.exception.PlugConfigurationException;
import ch.maybites.xml.*;
import java.io.*;

import java.util.*;

/**
 * @author martin terence froehlich (2009)
 * 
 *
 */
public class PlugFactory{
	private XMLInOut xmlPlugMappings;
	private XMLInOut xmlPlugTree;
	private XMLElement plugTreeFile;
	private XMLElement plugMappingElement;

	protected Vector<Plug> myPlugComaBeds;

	protected Vector<PlugType> myPlugCollection;

	private XmlValidator validator;

	private PApplet applet;

	protected Thread plugComaWard = null;

	private final String FILENAME_PLUGMAPPING = "default";
	private final String FILENAME_PLUGMAPPING_EXTENSION = "xml";

	private final String RELATIVE_PATH_TO_PLUGTYPES = "plug/types/";
	private final String RELATIVE_PATH_TO_PLUGMAPPINGS = "plug/mappings/";

	PlugFactory(PApplet a){
		applet = a;
		myPlugComaBeds = new Vector<Plug>(2);
		validator = XmlValidator.getInstance();
		xmlPlugTree = new XMLInOut(applet, this);
		xmlPlugMappings = new XMLInOut(applet, this);
		loadPlugTypes();
		loadMappings(RELATIVE_PATH_TO_PLUGMAPPINGS + FILENAME_PLUGMAPPING + "." + FILENAME_PLUGMAPPING_EXTENSION);
	}

	//reloads the plug type configurations
	public void refresh(){
		loadPlugTypes();
	}

	//updates the plug config data without changing its state.
	public void refreshPlug(Plug plug){
		if(!isDefaultClass(plug)){
			int plugID = plug.parameter.getPlugID();
			try{
				plug.refreshConfiguration(getPlugConfiguration(getPlugName(plugID)));
			} catch (PlugConfigurationException e){
				Debugger.getInstance().fatalMessage(this.getClass(), "Serious Plug Creation Exception in: " + getPlugConfigFilePath(getPlugName(plugID)));
				Debugger.getInstance().fatalMessage(this.getClass(), "  " + e.getMessage());
			}
		}
	}

	public void loadMappings(String plugMappingsFilepath){
		Debugger.getInstance().debugMessage(getClass(), "loadMappings("+plugMappingsFilepath+")");
		plugMappingElement = null;
		//validator.validate("data/" + plugMappingsFilepath);
		xmlPlugMappings.loadElement(plugMappingsFilepath);
		while(plugMappingElement == null){
			try{
				Thread.sleep(10); // do nothing for 100 miliseconds
			} catch(InterruptedException e){
				e.printStackTrace();
			} 
		}
	}

	public void saveMappings(String plugMappingsFilepath){
		xmlPlugMappings.saveElement(plugMappingElement, "../../../../../../../../../../../../../../.."+plugMappingsFilepath + "." + FILENAME_PLUGMAPPING_EXTENSION);
	}

	private void loadPlugTypes(){
		myPlugCollection = new Vector<PlugType>(10);
		Vector<String> list = getFileHirarchy(applet.dataPath(RELATIVE_PATH_TO_PLUGTYPES));
		for(int i = 0; i < list.size(); i++){
			String path = list.elementAt(i);
			//validator.validate("data/" + path);
			xmlPlugTree.loadElement(path);
			while(plugTreeFile == null){
				try{
					Thread.sleep(10); // do nothing for 100 miliseconds
				} catch(InterruptedException e){
					e.printStackTrace();
				} 
			}
			String name = path.substring(RELATIVE_PATH_TO_PLUGTYPES.length(), path.indexOf(".xml"));
			try{
				PlugType type = new PlugType(plugTreeFile, name);
				myPlugCollection.add(type);
				Debugger.getInstance().infoMessage(getClass(), "plugTree sucessfully loaded: " + name);
			} catch (PlugConfigurationException e){
				Debugger.getInstance().fatalMessage(this.getClass(), "Serious Plug Creation Exception in: " + path);
				Debugger.getInstance().fatalMessage(this.getClass(), "  " + e.getMessage());
			}
			plugTreeFile = null;
		}
	}

	private Vector<String> getFileHirarchy(String path){
		Vector<String> filelist = new Vector<String>();
		File fileHelper = new File(path);
		File[] content = fileHelper.listFiles();
		for(int i = 0; i< content.length; i++){
			if(content[i].canRead()){
				if(content[i].isDirectory()){
					Collection<String> col = getFileHirarchy(content[i].getAbsolutePath());
					filelist.addAll(col);
				}else if(content[i].isFile() && content[i].getName().endsWith(".xml")){
					filelist.add(content[i].getPath().substring(content[i].getPath().indexOf(RELATIVE_PATH_TO_PLUGTYPES), content[i].getPath().length()));
				}
			}
		}
		return filelist;
	}

	public String[] getPlugList(){
		String[] plugs = new String[myPlugCollection.size()];
		for(int i = 0; i < myPlugCollection.size(); i++){
			plugs[i] = ((PlugType)myPlugCollection.get(i)).getName();
			Debugger.getInstance().infoMessage(this.getClass(),"Plug name: " + plugs[i]);
		}
		return plugs;
	}

	private String getPlugClassPath(String plugName){
		for(int i = 0; i < myPlugCollection.size(); i++){
			if(((PlugType)myPlugCollection.get(i)).getName().equals(plugName)){
				return ((PlugType)myPlugCollection.get(i)).getPlugPath();
			}
		}
		return "";
	}

	public String getPlugConfigFilePath(String plugName){
		for(int i = 0; i < myPlugCollection.size(); i++){
			if(((PlugType)myPlugCollection.get(i)).getName().equals(plugName)){
				return applet.dataPath(RELATIVE_PATH_TO_PLUGTYPES) + ((PlugType)myPlugCollection.get(i)).getFileName() + ".xml";
			}
		}
		return "";
	}

	private PlugConfig getPlugConfiguration(String plugName) throws PlugConfigurationException{
		for(int i = 0; i < myPlugCollection.size(); i++){
			if(((PlugType)myPlugCollection.get(i)).getName().equals(plugName)){
				return new PlugConfig(((PlugType)myPlugCollection.get(i)).getParamaters());
			}
		}
		return new PlugConfig();
	}

	public String getPlugName(int plugID){
		XMLElement[] children = plugMappingElement.getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getAttribute("id").equals(""+plugID)){
				return children[i].getAttribute("name");
			}
		}
		//if the code reaches this lines, the default plug will be returned (if it is defined):
		for(int i = 0; i < children.length; i++){
			if(children[i].getAttribute("id").equals("default")){
				return children[i].getAttribute("name");
			}
		}
		Debugger.getInstance().errorMessage(getClass(), "getPlugName("+plugID+"): Cannot link PlugId to valid PlugClass..");
		return "";
	}

	public void setPlugMapping(int plugID, String plugName){
		XMLElement[] children = plugMappingElement.getChildren();
		for(int i = 0; i < children.length; i++){
			if(children[i].getAttribute("id").equals(""+plugID)){
				children[i].addAttribute("name", plugName);
				return;
			}
		}
		//if the code reaches this lines, no child element has been found, therefore:
		XMLElement child = new XMLElement("mapping");
		child.addAttribute("id", "" + plugID);
		child.addAttribute("name", plugName);
		plugMappingElement.addChild(child);
	}

	public Plug getPlugObject(Socket sm, Px1MessageSocket msg){
		int plugID = msg.getPlugID();
		// checks if the pludStorage has an Plug with the same plugID
		for(int i = 0; i < myPlugComaBeds.size(); i++){
			if(((Plug)myPlugComaBeds.get(i)).parameter.getPlugID().intValue() == plugID){ 
				Plug plg = (Plug)myPlugComaBeds.get(i);
				plg.socketUpdate(msg);
				plg.reAnimate(sm);
				myPlugComaBeds.remove(i);
				return plg;
			}
		}
		//if the plugComaWard has no plug of the same id the plugfactory will create a new one.
		Plug ret;
		try{
			ret = createPlugObject(getPlugClassPath(getPlugName(plugID)), getPlugConfiguration(getPlugName(plugID)), sm, msg);
		} catch (PlugConfigurationException e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Serious Plug Creation Exception in: " + getPlugConfigFilePath(getPlugName(plugID)));
			Debugger.getInstance().fatalMessage(this.getClass(), "  " + e.getMessage());
			ret = getPlugDefault(sm, msg);
		}
		return ret;
	}


	public Plug getPlugDefault(Socket sm, Px1MessageSocket msg){
		return new PlgDummy(sm, new PlugConfig(), msg);
	}


	public boolean isDefaultClass(Object o){
		return (o instanceof PlgDummy);
	}

	@SuppressWarnings("unchecked")
	private Plug createPlugObject(String plugPath, PlugConfig params, Socket sm, Px1MessageSocket msg){
		Plug plug = null;
		try{
			Class[] constructorParameter = {sm.getClass(), params.getClass(), msg.getClass()};
			Object[] instanceParameter = {sm, params, msg};
			java.lang.reflect.Constructor c = Class.forName(plugPath).getConstructor(constructorParameter);
			plug = (Plug)c.newInstance(instanceParameter);
		}catch(Exception e){
			Debugger.getInstance().fatalMessage(getClass(), "getPlugObject("+plugPath+"): unable to create specified plug");
			Debugger.getInstance().fatalMessage(getClass(), "  most likely cause: check plug configuration file for missspellings");
			Debugger.getInstance().fatalMessage(getClass(), "  check constructors..: " + e.getClass().getName());
			for(int i = 0; i < e.getStackTrace().length; i++){
				Debugger.getInstance().fatalMessage(getClass(), e.getStackTrace()[i].toString());
			}
			Debugger.getInstance().fatalMessage(getClass(), "Returning Dummy instead");
			return new PlgDummy(sm, new PlugConfig(), msg);
		}
		return (plug != null)? plug : new PlgDummy(sm, new PlugConfig(), msg);
	}

	public void xmlEvent(XMLElement element){
		if(element.getElement().equals("plugTree")){
			plugTreeFile = element;
			Debugger.getInstance().infoMessage(getClass(), "plugTree sucessfully parsed");
		} else if(element.getElement().equals("plugMapping")){
			plugMappingElement = element;
			Debugger.getInstance().infoMessage(getClass(), "plugMappings sucessfully parsed");
		}
	}

	/*
	 * an unused plug should be sent back to the PlugFactory (Default Class excepted)
	 * so it can be reused in case the plug is replugged within the set timeframe
	 */
	public void returnPlugObject(Plug p){
		if(!isDefaultClass(p)){
			Debugger.getInstance().debugMessage(getClass(), "returnPlugObject()");
			p.comatize();
			myPlugComaBeds.add(p);
			startComaJob();
		} else {
			p.destroy();
		}
	}

	private void startComaJob(){
		if(plugComaWard == null){
			try{
				Debugger.getInstance().debugMessage(getClass(), "start ComaJob Thread");
				plugComaWard = new Thread(new PlugComaWard());
				plugComaWard.start();
				plugComaWard.setPriority(Thread.MIN_PRIORITY);
			} catch(Exception e){
				e.printStackTrace(System.out);
			}
		}

	}


	/*
	 * this Thread helps cleaning up the plugStorage. it will only run while the
	 * plugStorage contains any objects.
	 */
	protected class PlugComaWard implements Runnable{

		@SuppressWarnings("static-access")
		public void run(){
			Thread thisThread = Thread.currentThread();

			while(myPlugComaBeds.size() > 0){

				//Debugger.getInstance().verboseMessage(this.getClass(),"PlugFactory: Thread is running, trying to kill a plug");
				try {
					thisThread.sleep(100);
				} catch (InterruptedException e){;}

				for(int i = 0; i < myPlugComaBeds.size(); i++){
					Plug plg = (Plug)myPlugComaBeds.get(i);
					if(plg.timeToDestroy()){
						plg.destroy();
						myPlugComaBeds.remove(i);
					}
				}
			}

			// when the job is done, stop the thread.
			plugComaWard = null;
			Debugger.getInstance().debugMessage(getClass(), "stop ComaJob Thread...");
		}
	}

}
