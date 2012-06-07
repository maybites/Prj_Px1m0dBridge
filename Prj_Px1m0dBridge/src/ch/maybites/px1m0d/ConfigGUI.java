package ch.maybites.px1m0d;
import ch.maybites.px1m0d.connection.Connector;
import controlP5.*;
import processing.core.*;
import sun.tools.tree.ThisExpression;
import java.util.*;


public class ConfigGUI implements ControlListener{

	private ControlP5 controlP5;
	private ControlWindow controlWindow;
	private Connector connector;
	private PApplet applet;
	private FileParameters parameters;
	private PlugFactory plugFactory;
	private Dispatcher dispatcher;

	Tab plugTab;
	Tab connectionTab;
	Tab messageLogTab;
	Tab sceneTab;
	Button[] plugIdMapping;
	Slider sceneLatency;

	private static String SLIDER_NAME_SCENELATENCY = "Set Scene Latency";

	private static String RADIO_NAME_LISTEN = "Listen";
	private static String BUTTON_NAME_CONNECTOSC = "Refresh";
	private static String BUTTON_NAME_CLEAR = "Clear";
	private static String BUTTON_NAME_RESTART = "Restart";
	private static String BUTTON_NAME_REFRESH_CONFIG = "Refresh Configuration";

	private static String BUTTON_NAME_LOADPLUG = "load map..";
	private static String BUTTON_NAME_SAVEPLUG = "save map..";

	private static String BUTTON_NAME_SOCKETMANAGER_LOAD = "load scene..";
	private static String BUTTON_NAME_SOCKETMANAGER_SAVE = "save scene..";

	private static String BUTTON_NAME_PLUDID_BETWEEN = " -> ";
	private static String BUTTON_NAME_PLUDID_PREPEND = "   ";

	private static String BUTTON_NAME_EDITPLUGFILE = "Edit...";

	private static String BUTTON_NAME_SAVEPREF_CONNECTIONS = "save as preferences";

	private static String TEXT_NAME_OSCSERVERURL = "osc server";
	private static String TEXT_NAME_OSCSERVERPORT = "server port";
	private static String TEXT_NAME_OSCLISTENERPORT = "listener port";

	private static String TEXT_NAME_NATEBU_FRAME_RATE = "natebu frame rate";

	private static String TEXTAREA_NAME_PX1M0D_INPUTWIN = "px1m0dInputWin";
	private static String TEXTAREA_NAME_PX1M0D_OUTPUTWIN = "px1m0dOutputWin";
	private static String TEXTAREA_NAME_WORLD_INPUTWIN = "worldInputWin";
	private static String TEXTAREA_NAME_WORLD_OUTPUTWIN = "worldOutputWin";

	private static String MULTILIST_PLUGTYPE_ROOT = "PlugListRoot";
	private static String MULTILIST_PLUGTYPE = "myPlugList";

	private Textarea px1m0dInput, px1m0dOutput, worldInput, worldOutput;

	private static String TAB_SCENE = "Scenes";
	private static String TAB_MESSAGELOG = "MessageLog";
	private static String TAB_CONNECTION = "Connections";
	private static String TAB_PLUGS = "Plugs";

	// controller ids
	private static int CID_PLUGTYPE_MULTILIST_ROOT = 1000; 
	private static int CID_PLUGTYPE_MULTILIST = 1100; // until 1199
	private static int CID_PLUGTYPE_BUTTON = 1200; // until 1299 

	private static int CID_PLUGSAVE_BUTTON = 1300; 
	private static int CID_PLUGLOAD_BUTTON = 1301; 

	private static int CID_PLUGEDIT_BUTTON = 1500; 

	private static int CID_SOCKETMANAGER_SAVE_BUTTON = 1400; 
	private static int CID_SOCKETMANAGER_LOAD_BUTTON = 1401; 

	private String px1m0dOutputMessage = "";
	private String worldOutputMessage = "";
	private String px1m0dInputMessage = "";
	private String worldInputMessage = "";

	private Textfield oscServerUrl;
	private Textfield oscServerPort;
	private Textfield oscListenerPort;

	private Textfield natebuFrameRate;

	private String selectedPlugType;

	public ConfigGUI(PApplet a, Connector c, FileParameters p, PlugFactory pf, Dispatcher d){
		applet = a;
		connector = c;
		parameters = p;
		plugFactory = pf;
		selectedPlugType = pf.getPlugName(0);
		dispatcher = d;
		c.registerListener(this);
	}

	/** must be called just after declaration of object, but not during calling of constructor.
	 * 
	 */
	public void setup(){
		controlP5 = new ControlP5(applet);
		controlP5.setAutoDraw(false);
		controlWindow = controlP5.addControlWindow("controlP5window",10,100,400,300);
		controlWindow.setBackground(applet.color(40));


		connectionTab = controlWindow.getCurrentTab();
		connectionTab.setLabel(TAB_CONNECTION);
		setupConnectionTab();

		messageLogTab = controlP5.addTab(controlWindow, TAB_MESSAGELOG);
		setupLogTab();

		sceneTab = controlP5.addTab(controlWindow, TAB_SCENE);
		setupSceneTab();

		setupPlugTab2();
		//setupPlugTab();

	}

	private void setupSceneTab(){

		Button button = controlP5.addButton(BUTTON_NAME_SOCKETMANAGER_LOAD,CID_SOCKETMANAGER_LOAD_BUTTON,100,50,60,20);
		button.moveTo(sceneTab);
		button = controlP5.addButton(BUTTON_NAME_SOCKETMANAGER_SAVE,CID_SOCKETMANAGER_SAVE_BUTTON,200,50,60,20);
		button.moveTo(sceneTab);

	}

	private void setupLogTab(){
		RadioButton r = controlP5.addRadio(RADIO_NAME_LISTEN, 360, 5);
		r.addItem("off", 0);
		r.addItem("on", 1);
		r.moveTo(controlWindow);
		r.setTab(TAB_MESSAGELOG);

		Button button3 = controlP5.addButton(BUTTON_NAME_CLEAR,100,300,5,50,10);
		button3.moveTo(messageLogTab);

		Textlabel tlabel = controlP5.addTextlabel("label1", "Px1m0d input messages", 10, 22);
		tlabel.moveTo(messageLogTab);
		px1m0dInput = controlP5.addTextarea(TEXTAREA_NAME_PX1M0D_INPUTWIN, "waiting for output ...\n\n\n\n\n\n\n\n\n\n\n\n\n",10,30,180,120);
		px1m0dInput.setColor(0xff00ddff);
		px1m0dInput.moveTo(messageLogTab, controlWindow);
		tlabel = controlP5.addTextlabel("label2", "Px1m0d output messages", 190, 22);
		tlabel.moveTo(messageLogTab);
		//px1m0dOutput = controlP5.addTextarea(TEXTAREA_NAME_PX1M0D_OUTPUTWIN, "waiting for output ...\n",200,30,160,120);

		px1m0dOutput = controlP5.addTextarea(TEXTAREA_NAME_PX1M0D_OUTPUTWIN, "waiting for output ...\n\n\n\n\n\n\n\n\n\n\n\n\n",200,30,180,120);

		px1m0dOutput.setColor(0xff00ffdd);
		px1m0dOutput.moveTo(messageLogTab, controlWindow);
		tlabel = controlP5.addTextlabel("label3", "World input messages", 10, 152);
		tlabel.moveTo(messageLogTab);
		worldInput = controlP5.addTextarea(TEXTAREA_NAME_WORLD_INPUTWIN, "waiting for output ...\n\n\n\n\n\n\n\n\n\n\n\n\n",10,160,180,120);
		worldInput.setColor(0xff00eeff);
		worldInput.moveTo(messageLogTab, controlWindow);
		tlabel = controlP5.addTextlabel("label4", "World output messages", 190, 152);
		tlabel.moveTo(messageLogTab);
		worldOutput = controlP5.addTextarea(TEXTAREA_NAME_WORLD_OUTPUTWIN, "waiting for output ...\n\n\n\n\n\n\n\n\n\n\n\n\n",200,160,180,120);
		worldOutput.setColor(0xff00ffee);
		worldOutput.moveTo(messageLogTab, controlWindow);

	}

	private void setupConnectionTab(){
		oscServerUrl = controlP5.addTextfield(TEXT_NAME_OSCSERVERURL,10,20,140,20);
		oscServerUrl.moveTo(connectionTab);
		oscServerUrl.setValue(parameters.getPreferenceValue(parameters.PARAM_oscServerUrl));

		oscServerPort = controlP5.addTextfield(TEXT_NAME_OSCSERVERPORT,160,20,40,20);
		oscServerPort.moveTo(connectionTab);
		oscServerPort.setValue(parameters.getPreferenceValue(parameters.PARAM_oscServerPort));

		oscListenerPort = controlP5.addTextfield(TEXT_NAME_OSCLISTENERPORT,250,20,40,20);
		oscListenerPort.moveTo(connectionTab);
		oscListenerPort.setValue(parameters.getPreferenceValue(parameters.PARAM_oscListenerPort));

		Button button2 = controlP5.addButton(BUTTON_NAME_CONNECTOSC,100,340,20,50,20);
		button2.moveTo(connectionTab);

		/**
		oscNatebuUrl = controlP5.addTextfield(TEXT_NAME_OSCNATEBUURL,10,55,140,20);
		oscNatebuUrl.moveTo(connectionTab);
		oscNatebuUrl.setValue(parameters.getPreferenceValue(parameters.PARAM_oscNatebuUrl));

		oscNatebuPort = controlP5.addTextfield(TEXT_NAME_OSCNATEBUPORT,160,55,40,20);
		oscNatebuPort.moveTo(connectionTab);
		oscNatebuPort.setValue(parameters.getPreferenceValue(parameters.PARAM_oscNatebuPort));

		 **/

		natebuFrameRate = controlP5.addTextfield(TEXT_NAME_NATEBU_FRAME_RATE,250,55,40,20);
		natebuFrameRate.moveTo(connectionTab);
		natebuFrameRate.setValue("---");

		// This code is for selecting serial port
		MultiList mySerial = controlP5.addMultiList("mySerial",10,55,190,20);
		MultiListButton d;
		d = mySerial.add("Natebu",4);
		d.setColorBackground(applet.color(0, 64 + 20,0,255));
		d.setValueLabel("Serial <-> "+connector.getSerialConnection());
		for(int i=0; i < connector.getNOfSerialConn(); i++){
			MultiListButton c = d.add("Natebu serial" + (61 + i),61 + i);
			c.setValueLabel(connector.getSerialConnLabel(i));
			c.setColorBackground(applet.color(0, 64 + 20*i,(64 + 20*i)/2,255));
		}
		mySerial.moveTo(connectionTab);

		MultiList myList = controlP5.addMultiList("myList",10,90,190,20);
		MultiListButton b;
		b = myList.add("Device1",1);
		b.setColorBackground(applet.color(64 + 20,0,0,255));
		b.setValueLabel("Px1m0d <- "+connector.getPxlm0dOutputConnection());
		for(int i=0; i < connector.getNOfOutputs(); i++){
			MultiListButton c = b.add("Device1 item" + (11 + i),11 + i);
			c.setValueLabel(connector.getOutputLabel(i));
			c.setColorBackground(applet.color(64 + 20*i,(64 + 20*i)/2,0,255));
		}
		b = myList.add("Device2",2);
		b.setColorBackground(applet.color(64 + 40,0,0,255));
		b.setValueLabel("Px1m0d -> "+connector.getPxlm0dInputConnection());
		for(int i=0; i < connector.getNOfInputs(); i++){
			MultiListButton c = b.add("Device2 item" + (21 + i),21 + i);
			c.setValueLabel(connector.getInputLabel(i));
			c.setColorBackground(applet.color(64 + 20*i,(64 + 20*i)/2,0,255));
		}
		b = myList.add("Device3",3);
		b.setColorBackground(applet.color(64 + 60,0,0,255));
		b.setValueLabel("World <- "+connector.getWorldOutputConnection());
		for(int i=0; i < connector.getNOfOutputs(); i++){
			MultiListButton c = b.add("Device3 item" + (31 + i),31 + i);
			c.setValueLabel(connector.getOutputLabel(i));
			c.setColorBackground(applet.color(64 + 20*i,(64 + 20*i)/2,0,255));
		}
		b = myList.add("Device4",4);
		b.setColorBackground(applet.color(64 + 80,0,0,255));
		b.setValueLabel("World -> "+connector.getWorldInputConnection());
		for(int i=0; i < connector.getNOfInputs(); i++){
			MultiListButton c = b.add("Device4 item" + (41 + i),41 + i);
			c.setValueLabel(connector.getInputLabel(i));
			c.setColorBackground(applet.color(64 + 20*i,(64 + 20*i)/2,0,255));
		}		
		myList.moveTo(connectionTab);

		float latency = GlobalPreferences.getInstance().getLatencyFloat();
		sceneLatency = controlP5.addSlider(SLIDER_NAME_SCENELATENCY,0,20,latency,10,200,100,10);
		sceneLatency.moveTo(connectionTab);

		Button button = controlP5.addButton(BUTTON_NAME_RESTART,100,10,250,190,20);
		button.moveTo(connectionTab);

		Button button4 = controlP5.addButton(BUTTON_NAME_REFRESH_CONFIG,100,210,250,180,20);
		button4.moveTo(connectionTab);

		Button button3 = controlP5.addButton(BUTTON_NAME_SAVEPREF_CONNECTIONS,100,10,280,190,10);
		button3.moveTo(connectionTab);
	}

	private void updatePlugTabButtons(){
		for(int i = 1; i < plugIdMapping.length; i++){
			plugIdMapping[i].setValueLabel(BUTTON_NAME_PLUDID_PREPEND + i + BUTTON_NAME_PLUDID_BETWEEN + this.plugFactory.getPlugName(i));
		}
	}

	private String getLabel(String[] plugList, int index, int level){
		String token = null;
		if(index >= 0 && index < plugList.length){
			StringTokenizer st = new StringTokenizer(plugList[index], ".");
			for(int i = 0; i <= level; i++){
				if(st.hasMoreTokens())
					token = st.nextToken();
				else 
					token = null;
			}
		}
		return token;
	}

	private boolean isLastLevel(String[] plugList, int index, int level){
		return (getLabel(plugList, index, level+1) == null)? true: false;
	}

	private String getLastLabel(String[] plugList, int index, int level){
		return getLabel(plugList, index - 1, level);
	}

	private boolean isEqualLastLabel(String[] plugList, int index, int level){
		return (getLabel(plugList, index, level) != null && getLabel(plugList, index, level).equals(getLastLabel(plugList, index, level)))? true: false;
	}

	//returns the count of unique labels of this level so far
	private int getLevelIndex(String[] plugList, int index, int level){
		int count = 0;
		for(int i = 1; i <= index; i++){
			if(!isEqualLastLabel(plugList, i, level) && isEqualLastLabel(plugList, i, level-1)){
				count++;
			}
		}
		return count;
	}

	private void setupPlugTab2(){
		if(plugTab != null){
			controlP5.remove(plugTab.name());
		}
		plugTab = controlP5.addTab(controlWindow, TAB_PLUGS);
		plugTab.setLabel(TAB_PLUGS);


		plugIdMapping = new Button[35];
		for(int i = 1; i < 18; i++){
			plugIdMapping[i] = controlP5.addButton(BUTTON_NAME_PLUDID_PREPEND + i + BUTTON_NAME_PLUDID_BETWEEN + this.plugFactory.getPlugName(i),CID_PLUGTYPE_BUTTON + i,10, 25 + i *15,10,10);
			plugIdMapping[i].moveTo(plugTab);
			plugIdMapping[i+17] = controlP5.addButton(BUTTON_NAME_PLUDID_PREPEND + (i+17) + BUTTON_NAME_PLUDID_BETWEEN + this.plugFactory.getPlugName(i+17),CID_PLUGTYPE_BUTTON + (i+17),210, 25 + i *15,10,10);
			plugIdMapping[i+17].moveTo(plugTab);
		}

		Button editbutton = controlP5.addButton(BUTTON_NAME_EDITPLUGFILE,CID_PLUGEDIT_BUTTON,360,20,30,15);
		editbutton.moveTo(plugTab);

		MultiList myList = controlP5.addMultiList(MULTILIST_PLUGTYPE,10,20,75,15);
		MultiListButton levelX = null;
		MultiListButton level0 = null;
		MultiListButton level1 = null;
		MultiListButton level2 = null;
		MultiListButton level3 = null;
		levelX = myList.add(MULTILIST_PLUGTYPE_ROOT,CID_PLUGTYPE_MULTILIST_ROOT);
		levelX.setColorBackground(applet.color(64 + 20,0,0,0));
		levelX.setValueLabel("Select:");
		String[] plugs = plugFactory.getPlugList();
		for(int i=0; i < plugs.length; i++){
			// if the new label is different than the last one:
			if(!isEqualLastLabel(plugs, i, 0) && getLabel(plugs, i, 0) != null){
				if(isLastLevel(plugs, i, 0)){
					level0 = levelX.add(plugs[i], CID_PLUGTYPE_MULTILIST + i);
					level0.setValueLabel(getLabel(plugs, i, 0));
					level0.setColorBackground(applet.color(120 + 10 * getLevelIndex(plugs, i, 0),(120)/2, 200, 255));
				} else {
					level0 = levelX.add(getLabel(plugs, i, 0), CID_PLUGTYPE_MULTILIST_ROOT + i);
					level0.setValueLabel(getLabel(plugs, i, 0));
					level0.setColorBackground(applet.color(120 + 10 * getLevelIndex(plugs, i, 0),(120)/2, 0, 255));
				}
			}

			if(level0 != null && !isEqualLastLabel(plugs, i, 1) && getLabel(plugs, i, 1) != null){
				if(isLastLevel(plugs, i, 1)){
					level1 = level0.add(plugs[i], CID_PLUGTYPE_MULTILIST + i);
					level1.setValueLabel(getLabel(plugs, i, 1));
					level1.setColorBackground(applet.color(120,(120 + 10 * getLevelIndex(plugs, i, 1))/2, 200, 255));
				} else {
					level1 = level0.add(getLabel(plugs, i, 1), CID_PLUGTYPE_MULTILIST_ROOT + i);
					level1.setValueLabel(getLabel(plugs, i, 1));
					level1.setColorBackground(applet.color(120,(120 + 10 * getLevelIndex(plugs, i, 1))/2, 0, 255));
				}
			}

			if(level1 != null && !isEqualLastLabel(plugs, i, 2) && getLabel(plugs, i, 2) != null){
				if(isLastLevel(plugs, i, 2)){
					level2 = level1.add(plugs[i], CID_PLUGTYPE_MULTILIST + i);
					level2.setValueLabel(getLabel(plugs, i, 2));
					level2.setColorBackground(applet.color(120 + 10 * getLevelIndex(plugs, i, 2),(120 + 10 * getLevelIndex(plugs, i, 2))/2, 200,255));
				} else {
					level2 = level1.add(getLabel(plugs, i, 2), CID_PLUGTYPE_MULTILIST_ROOT + i);
					level2.setValueLabel(getLabel(plugs, i, 2));
					level2.setColorBackground(applet.color(120 + 10 * getLevelIndex(plugs, i, 2),(120 + 10 * getLevelIndex(plugs, i, 2))/2, 0,255));
				}
			}

			if(level2 != null && !isEqualLastLabel(plugs, i, 3) && getLabel(plugs, i, 3) != null){
				if(isLastLevel(plugs, i, 3)){
					level3 = level2.add(plugs[i], CID_PLUGTYPE_MULTILIST + i);
					level3.setValueLabel(getLabel(plugs, i, 3));
					level3.setColorBackground(applet.color(120 + 10 * getLevelIndex(plugs, i, 3),(120 + 10 * getLevelIndex(plugs, i, 3))/2, 200, 255));
				} else {
					level3 = level2.add(getLabel(plugs, i, 3), CID_PLUGTYPE_MULTILIST_ROOT + 1);
					level3.setValueLabel(getLabel(plugs, i, 3));
					level3.setColorBackground(applet.color(120 + 10 * getLevelIndex(plugs, i, 3),(120 + 10 * getLevelIndex(plugs, i, 3))/2, 0, 255));
				}
			}
		}
		myList.moveTo(plugTab);

		Button button = controlP5.addButton(BUTTON_NAME_LOADPLUG,CID_PLUGLOAD_BUTTON,280,0,58,17);
		button.moveTo(plugTab);
		button = controlP5.addButton(BUTTON_NAME_SAVEPLUG,CID_PLUGSAVE_BUTTON,340,0,58,17);
		button.moveTo(plugTab);

	}

	public void controlEvent(ControlEvent theEvent) {
		try{
			Debugger.getInstance().verboseMessage(this.getClass(),"event name: " + theEvent.getController().getValue());
			Debugger.getInstance().verboseMessage(this.getClass(),"event value: " + theEvent.getController().getValue());

			/*
			 * Scene Tab Event
			 */
			if(theEvent.getController().getValue() == CID_SOCKETMANAGER_LOAD_BUTTON){
				String path = this.applet.selectInput("*.pxs");
				Debugger.getInstance().verboseMessage(this.getClass(), "loading scene from: " + path);
				this.dispatcher.loadScene(path);
			}

			if(theEvent.getController().getValue() == CID_SOCKETMANAGER_SAVE_BUTTON){
				String path = this.applet.selectOutput("*.pxs");
				Debugger.getInstance().verboseMessage(this.getClass(), "saving scene to: " + path);
				this.dispatcher.saveScene(path);
			}


			/*
			 * Plug Tab Event
			 */
			if(theEvent.getController().getValue() == CID_PLUGEDIT_BUTTON){
				Debugger.getInstance().verboseMessage(this.getClass(), "edit xml file " + controlP5.getController(MULTILIST_PLUGTYPE_ROOT).getLabel());
				String path = this.plugFactory.getPlugConfigFilePath(controlP5.getController(MULTILIST_PLUGTYPE_ROOT).getLabel());
				if(path.length() > 0){
					openXMLFileEditor(path);			
				}
				//updatePlugTabButtons();
			}

			if(theEvent.getController().getValue() == CID_PLUGLOAD_BUTTON){
				String path = this.applet.selectInput("*.xml");
				Debugger.getInstance().verboseMessage(this.getClass(), "loading plugingmapping to: " + path);
				this.plugFactory.loadMappings(path);
				updatePlugTabButtons();
			}

			if(theEvent.getController().getValue() == CID_PLUGSAVE_BUTTON){
				String path = this.applet.selectOutput("*.xml");
				Debugger.getInstance().verboseMessage(this.getClass(), "saving plugingmapping to: " + path);
				this.plugFactory.saveMappings(path);
			}


			//find PlugType Button events
			if(theEvent.getController().getValue() >= CID_PLUGTYPE_BUTTON && theEvent.getController().getValue() < (CID_PLUGTYPE_BUTTON+100)){
				int plugID = (int)theEvent.getController().getValue() - CID_PLUGTYPE_BUTTON;
				Debugger.getInstance().verboseMessage(this.getClass(), "event plugID: " + plugID);
				this.plugFactory.setPlugMapping(plugID, selectedPlugType);
				theEvent.getController().setCaptionLabel(BUTTON_NAME_PLUDID_PREPEND + plugID + BUTTON_NAME_PLUDID_BETWEEN + selectedPlugType);
			}

			//find PlugType Multilist events
			if(theEvent.getController().getValue() >= CID_PLUGTYPE_MULTILIST && theEvent.getController().getValue() < (CID_PLUGTYPE_MULTILIST+100)){
				controlP5.getController(MULTILIST_PLUGTYPE_ROOT).setCaptionLabel(theEvent.getController().getName());
				selectedPlugType = theEvent.getController().getName();
			}

			//Serial connection chosen
			if(theEvent.getController().getName().indexOf("serial") != -1){
				Debugger.getInstance().verboseMessage(this.getClass(), "parent label: " + controlP5.getController("level" + (int)(theEvent.getValue()/10)).getLabel());
				String label = controlP5.getController("Natebu").getLabel();
				String name = controlP5.getController("Natebu").getName();
				if(label.indexOf(" <-> ") != -1){
					label = label.substring(0, label.indexOf(" <-> "))+ " <-> " + theEvent.getController().getLabel();
				}
				controlP5.getController("Natebu").setCaptionLabel(label);
				if(name.equals("Natebu")){
					connector.setSerialConnection(theEvent.getLabel());
					Debugger.getInstance().verboseMessage(this.getClass(), "change SerialConnection to: " + theEvent.getController().getLabel());
				}
			}

			/*
			 * updates the list-lables 
			 */
			if(theEvent.getController().getName().indexOf("item") != -1){
				String label = controlP5.getController("Device" + (int)(theEvent.getValue()/10)).getName();
				String name = controlP5.getController("Device" + (int)(theEvent.getValue()/10)).getName();
				if(label.indexOf(" -> ") != -1){
					label = label.substring(0, label.indexOf(" -> "))+ " -> " + theEvent.getController().getName();
				}
				if(label.indexOf(" <- ") != -1){
					label = label.substring(0, label.indexOf(" <- "))+ " <- " + theEvent.getController().getName();
				}
				controlP5.getController("Device" + (int)(theEvent.getValue()/10)).setCaptionLabel(label);
				if(name.equals("Device1")){
					connector.setPxlm0dOutputConnection(theEvent.getName());
				}
				else if(name.equals("Device2")){
					connector.setPxlm0dInputConnection(theEvent.getName());
				}
				else if(name.equals("Device3")){
					connector.setWorldOutputConnection(theEvent.getName());
				}
				else if(name.equals("Device4")){
					connector.setWorldInputConnection(theEvent.getName());
				}
			}
			/*
			 * clears messages windows
			 */
			if(theEvent.getController().getName().equals(BUTTON_NAME_CLEAR)){
				px1m0dOutputMessage = "";
				worldOutputMessage = "";
				px1m0dInputMessage = "";
				worldInputMessage = "";
				px1m0dOutput.setText(px1m0dOutputMessage);
				px1m0dInput.setText(px1m0dInputMessage);
				worldOutput.setText(worldOutputMessage);
				worldInput.setText(worldInputMessage);
			}
			/*
			 * connects osc
			 */
			if(theEvent.getController().getName().equals(BUTTON_NAME_CONNECTOSC)){
				connector.enableOSCWorld(oscServerUrl.getText(), 
						new Integer(oscServerPort.getText()).intValue(), 
						new Integer(oscListenerPort.getText()).intValue());
			}
			/*
			 * restart button pressed
			 */
			if(theEvent.getController().getName().equals(BUTTON_NAME_RESTART)){
				dispatcher.restartSimulation();
			}
			/*
			 * refresh config button pressed
			 */
			if(theEvent.getController().getName().equals(BUTTON_NAME_REFRESH_CONFIG)){
				dispatcher.refresh();
				setupPlugTab2();
			}
			/*
			 * radio buttons to listen to messages
			 */
			if(theEvent.getController().getName().equals(RADIO_NAME_LISTEN)){
				switch((int)theEvent.getController().getValue()) {
				case(0):
					this.connector.stopListenToMessages();
				controlWindow.setUpdateMode(ControlWindow.ECONOMIC);
				break;  
				case(1):
					this.connector.listenToMessages();
				controlWindow.setUpdateMode(ControlWindow.NORMAL);
				break;  
				}
			}
			/*
			 * if the SceneLatency Slider has been changed, the global values have to be changed as well
			 */
			if(theEvent.getController().getName().equals(SLIDER_NAME_SCENELATENCY)){
				GlobalPreferences.getInstance().setLatency(sceneLatency.getValue());
			}


			/*
			 * Saves the preferences
			 */
			if(theEvent.getController().getName().equals(BUTTON_NAME_SAVEPREF_CONNECTIONS)){
				parameters.setPreferenceValue(parameters.PARAM_DefaultInputPxlmodMidiConnection, connector.getPxlm0dInputConnection());
				parameters.setPreferenceValue(parameters.PARAM_DefaultInputWorldMidiConnection, connector.getWorldInputConnection());
				parameters.setPreferenceValue(parameters.PARAM_DefaultOutputPxlmodMidiConnection, connector.getPxlm0dOutputConnection());
				parameters.setPreferenceValue(parameters.PARAM_DefaultOutputWorldMidiConnection, connector.getWorldOutputConnection());
				parameters.setPreferenceValue(parameters.PARAM_DefaultNatebuSerialConnection, connector.getSerialConnection());
				parameters.setPreferenceValue(parameters.PARAM_oscListenerPort, oscListenerPort.getText());
				parameters.setPreferenceValue(parameters.PARAM_oscServerPort, oscServerPort.getText());
				parameters.setPreferenceValue(parameters.PARAM_oscServerUrl, oscServerUrl.getText());
				parameters.setPreferenceValue(parameters.PARAM_sceneLatency, ""+ sceneLatency.getValue());
				parameters.savePreferences();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void addP1xlmodOutputMessage(String message){
		px1m0dOutputMessage = message + "\n" + px1m0dOutputMessage;
		px1m0dOutput.setText(px1m0dOutputMessage);
	}

	public void addPx1m0dInputMessage(String message){
		px1m0dInputMessage = message + "\n" + px1m0dInputMessage;
		px1m0dInput.setText(px1m0dInputMessage);
	}

	public void addWorldOutputMessage(String message){
		worldOutputMessage = message + "\n" + worldOutputMessage;
		worldOutput.setText(worldOutputMessage);
	}

	public void addWorldInputMessage(String message){
		worldInputMessage = message + "\n" + worldInputMessage;
		worldInput.setText(worldInputMessage);
	}

	public void setNatebuFrameRate(int framerate){
		natebuFrameRate.setText(framerate+ " Hz");
	}

	public void openXMLFileEditor(String filePath){
		connector.appleScript.openXMLFile("/Applications/editix-free-2009sp1.app", filePath);
	}

	public void draw(){
		//controlP5.draw();
	}

}
