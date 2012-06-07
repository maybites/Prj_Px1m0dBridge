package ch.maybites.px1m0d.connection;
import netP5.*;
import oscP5.*;
import ch.maybites.px1m0d.tools.*;

import javax.sound.midi.*;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.FileParameters;
import ch.maybites.px1m0d.Px1m0d;
import ch.maybites.px1m0d.ConfigGUI;
import ch.maybites.px1m0d.Dispatcher;
import ch.maybites.px1m0d.message.*;
import java.util.*;


/* Makes the Connections to the outside world possible
 * 
 * Author: Martin Fršhlich 2008
 * 
 */
import processing.serial.*;

public class Connector {
	private Dispatcher dispatcher;
	private boolean oscready = false;
	private boolean isListening = false;
	private OscP5 osc;
	private NetAddress myOscWorldServer;
	private NetAddress myOscNatebuServer;
	ConfigGUI messageListener;
	private Serial mySerial;
	private Px1m0d myApplet;
	private FileParameters myParameters;
	private long frameRateTimer;
	private int frameCounter;
	private NatebuDataReceiver myNatebuReceiver;
	
	public AppleScriptConnector appleScript;


	private javax.sound.midi.MidiDevice.Info info[];
	private javax.sound.midi.MidiDevice.Info inputInfo[];
	private javax.sound.midi.MidiDevice.Info outputInfo[];

	private Connection pxlm0dOutConnect;
	private Connection pxlm0dInConnect;
	private Connection worldOutConnect;
	private Connection worldInConnect;
	private Connection serialConnect;

	private MidiDeviceManager midiDevManager;

	private static String UNDEF_CONN = "<undefined>";
	private static int LF = 10;
	
	private Hashtable<String, NetAddress> oscAddressCollection;

	public Connector(FileParameters p, Dispatcher d, Px1m0d applet){
		oscAddressCollection = new Hashtable<String, NetAddress>();
		myApplet = applet;
		myParameters = p;
		this.dispatcher = d;
		midiDevManager = new MidiDeviceManager();
		info = MidiSystem.getMidiDeviceInfo();
		inputInfo = filterInputDevices(info, true);
		outputInfo = filterOutputDevices(info, true);
		this.dispatcher.connect(this);
		myNatebuReceiver = new NatebuDataReceiver();
		appleScript = new AppleScriptConnector();
	}

	public void enableOSCWorld(String serverurl, int serverport, int listenerport){
		osc = new OscP5(this, listenerport);
		myOscWorldServer = new NetAddress(serverurl,serverport);
		oscready = true;
	}

	private NetAddress getNetAddress(String IPAddress, int port){
		String key = IPAddress + port;
		if(!oscAddressCollection.containsKey(key))
			oscAddressCollection.put(key, new NetAddress(IPAddress, port));
		return oscAddressCollection.get(key);
	}
	
	public void oscEvent(OscMessage theOscMessage) {
		if(theOscMessage.checkAddrPattern("/natebu")){
			int[] values = new int[99];
			for( int i=0; i < values.length; i++) {
				int value = theOscMessage.get(i).intValue();
				//data correction from natebu
				/*
				if(value > 50){
					value = (int)(PApplet.map((float)value, 51f, 200f, 0f, 127f));
					value = (int)(PApplet.constrain((float)value, 0f, 127f));
				} else {
					value = 0;
				}  
				*/    
				values[i] = value;

			}
			Message m = new Px1MessageNatebu(values);
			receiveFromWorld(m);
		} else {
			Message m = new Px1MessageOsc(Message.ROUTE_BOARD, Message.RECEIVER_PLUG, theOscMessage);
			receiveFromWorld(m);
		}
	}

	// needs to be set after initialization because of the serial connection
	public void setDefaultConnections(){
		try{
			for(int i = 0; i < outputInfo.length; i++){
				if(outputInfo[i].getName().contains(myParameters.getPreferenceValue(myParameters.PARAM_DefaultOutputPxlmodMidiConnection))){
					pxlm0dOutConnect = new Connection(outputInfo[i].getName(), midiDevManager.requestReceiver(outputInfo[i]));
				}
				if(outputInfo[i].getName().contains(myParameters.getPreferenceValue(myParameters.PARAM_DefaultOutputWorldMidiConnection))){
					worldOutConnect = new Connection(outputInfo[i].getName(), midiDevManager.requestReceiver(outputInfo[i]));
				}
			}
			for(int i = 0; i < inputInfo.length; i++){
				if(inputInfo[i].getName().contains(myParameters.getPreferenceValue(myParameters.PARAM_DefaultInputPxlmodMidiConnection))){
					midiDevManager.setTransmitter(inputInfo[i], new Px1m0dReceiver(this));
					pxlm0dInConnect = new Connection(inputInfo[i].getName());
				}
				if(inputInfo[i].getName().contains(myParameters.getPreferenceValue(myParameters.PARAM_DefaultInputWorldMidiConnection))){
					midiDevManager.setTransmitter(inputInfo[i], new WorldReceiver(this));
					worldInConnect = new Connection(inputInfo[i].getName());
				}
			}
			//set serial connection
			for(int i = 0; i < getNOfSerialConn(); i++){
				if(myParameters.getPreferenceValue(myParameters.PARAM_DefaultNatebuSerialConnection).equals(getSerialConnLabel(i))){
					setSerialConnection(getSerialConnLabel(i));
				}
			}
			enableOSCWorld(myParameters.getPreferenceValue(myParameters.PARAM_oscServerUrl),
					(new Integer(myParameters.getPreferenceValue(myParameters.PARAM_oscServerPort))).intValue(),
					(new Integer(myParameters.getPreferenceValue(myParameters.PARAM_oscListenerPort))).intValue());
		} catch(MidiUnavailableException e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Caught unexpected MIDI Exception. Check your Midi Connections. System Exits...");
			Debugger.getInstance().fatalMessage(this.getClass(), e.getMessage());
			System.exit(0);
		}
	}

	public int getNOfSerialConn(){
		return Serial.list().length;
	}

	public String getSerialConnLabel(int p){
		return Serial.list()[p];
	}

	public int getNOfInputs(){
		return inputInfo.length;
	}

	public String getInputLabel(int p){
		return inputInfo[p].getName();
	}

	public int getNOfOutputs(){
		return outputInfo.length;
	}

	public String getOutputLabel(int p){
		return outputInfo[p].getName();
	}

	public String getPxlm0dOutputConnection(){
		if(pxlm0dOutConnect != null)
			return pxlm0dOutConnect.getID();
		return UNDEF_CONN;
	}

	public String getPxlm0dInputConnection(){
		if(pxlm0dInConnect != null)
			return pxlm0dInConnect.getID();
		return UNDEF_CONN;
	}

	public String getWorldOutputConnection(){
		if(worldOutConnect != null)
			return worldOutConnect.getID();
		return UNDEF_CONN;
	}

	public String getWorldInputConnection(){
		if(worldInConnect != null)
			return worldInConnect.getID();
		return UNDEF_CONN;
	}

	public String getSerialConnection(){
		if(serialConnect != null)
			return serialConnect.getID();
		return UNDEF_CONN;
	}

	public void setPxlm0dOutputConnection(String id){
		try{
			for(int i = 0; i < outputInfo.length; i++){
				if(outputInfo[i].getName().equals(id)){
					pxlm0dOutConnect = new Connection(id, midiDevManager.requestReceiver(outputInfo[i]));
				}
			}
		} catch(MidiUnavailableException e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Caught unexpected MIDI Exception. Check your Midi Connections. System Exits...");
			Debugger.getInstance().fatalMessage(this.getClass(), e.getMessage());
			System.exit(0);
		}
	}

	public void setPxlm0dInputConnection(String id){
		try{
			for(int i = 0; i < inputInfo.length; i++){
				if(inputInfo[i].getName().equals(id)){
					midiDevManager.setTransmitter(inputInfo[i], new Px1m0dReceiver(this));
					pxlm0dInConnect = new Connection(id);
				}
			}
		} catch(MidiUnavailableException e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Caught unexpected MIDI Exception. Check your Midi Connections. System Exits...");
			Debugger.getInstance().fatalMessage(this.getClass(), e.getMessage());
			System.exit(0);
		}
	}

	public void setWorldOutputConnection(String id){
		try{
			for(int i = 0; i < outputInfo.length; i++){
				if(outputInfo[i].getName().equals(id)){
					worldOutConnect = new Connection(id, midiDevManager.requestReceiver(outputInfo[i]));
				}
			}
		} catch(MidiUnavailableException e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Caught unexpected MIDI Exception. Check your Midi Connections. System Exits...");
			Debugger.getInstance().fatalMessage(this.getClass(), e.getMessage());
			System.exit(0);
		}
	}

	public void setWorldInputConnection(String id){
		try{
			for(int i = 0; i < inputInfo.length; i++){
				if(inputInfo[i].getName().equals(id)){
					midiDevManager.setTransmitter(inputInfo[i], new WorldReceiver(this));
					worldInConnect = new Connection(id);
				}
			}
		} catch(MidiUnavailableException e){
			Debugger.getInstance().fatalMessage(this.getClass(), "Caught unexpected MIDI Exception. Check your Midi Connections. System Exits...");
			Debugger.getInstance().fatalMessage(this.getClass(), e.getMessage());
			System.exit(0);
		}
	}

	public void setSerialConnection(String id){
		for(int i = 0; i < Serial.list().length; i++){
			if(Serial.list()[i].equals(id)){
				mySerial = myApplet.startSerialConnection(id);
				mySerial.clear();
				mySerial.readStringUntil(LF);
				serialConnect = new Connection(id);
			}
		}
		frameRateTimer = System.currentTimeMillis();
		frameCounter = 0;
	}

	public void receiveSerialMessage(Serial m){
	    char inByte = m.readChar();
	    int intByte = inByte;
	    intByte -= 129;
	    if(intByte < 0)
	      intByte += 256;
	    // returns a filled array once a dataset is complete
	    int[] vals = myNatebuReceiver.addData(intByte); 
	    if(vals != null){
			Message msg = new Px1MessageNatebu(vals);
			receiveFromWorld(msg);    	
	    	//update the GUI with the current framerate
			frameCounter++;
			if(frameRateTimer + 1000 < System.currentTimeMillis()){
				Debugger.getInstance().infoMessage(this.getClass(), "framerate: " + frameCounter);
				messageListener.setNatebuFrameRate(frameCounter);
				frameRateTimer = System.currentTimeMillis();
				frameCounter = 0;
			}
	    }
	}

	public void sendSerialMessage(Message m){
		;
	}

	public void receiveFromPxlm0d(Message m){
		dispatcher.send(m);
		if(isListening){
			messageListener.addPx1m0dInputMessage(m.printLine());
		}
	}

	public void receiveFromWorld(Message m){
		dispatcher.send(m);
		if(isListening){
			messageListener.addWorldInputMessage(m.printLine());
		}
	}

	public void sendToPxlm0d(Message m){
		if(m.myType == (Message.TYPE_MIDI)){
			pxlm0dOutConnect.send(((Px1MessageMidi)m).getMidiMessage());
			if(isListening){
				messageListener.addP1xlmodOutputMessage(m.printLine());
			}
		}
	}

	public void sendToWorld(Message m){
		if(m.myType == (Message.TYPE_MIDI))
			worldOutConnect.send(((Px1MessageMidi)m).getMidiMessage());
		if(m.myType == (Message.TYPE_OSC) && oscready){
			if(m.myReceiver == (Message.RECEIVER_WORLD)){
				Px1MessageOsc msg = (Px1MessageOsc)m;
				if(msg.isDefaultIPAddress()){
					osc.send(msg.getOscMessage(), this.myOscWorldServer); 
				} else {
					osc.send(msg.getOscMessage(), getNetAddress(msg.getIPAddress(), msg.getPort()));
				}
			}
		}
		if(isListening){
			messageListener.addWorldOutputMessage(m.printLine());
		}
	}

	public void refresh(){
		dispatcher.restartSimulation();
	}

	public void registerListener(ConfigGUI g){
		messageListener = g;
	}

	public void listenToMessages(){
		Debugger.getInstance().infoMessage(this.getClass(),"listening to messages");
		isListening = true;
	}

	public void stopListenToMessages(){
		Debugger.getInstance().verboseMessage(this.getClass(),"stoplistening to messages");
		isListening = false;
	}

	private javax.sound.midi.MidiDevice.Info[] filterInputDevices(javax.sound.midi.MidiDevice.Info infoList[], boolean select)
	{
		boolean copyElement[] = new boolean[infoList.length];
		int deviceCount = 0;
		for(int index = 0; index < infoList.length; index++)
			try
		{
				MidiDevice device = MidiSystem.getMidiDevice(infoList[index]);
				boolean hasTransmitter = device.getMaxTransmitters() != 0;
				if(hasTransmitter == select)
				{
					copyElement[index] = true;
					deviceCount++;
				}
		}
		catch(MidiUnavailableException e) { }

		javax.sound.midi.MidiDevice.Info outList[] = new javax.sound.midi.MidiDevice.Info[deviceCount];
		int outIndex = 0;
		for(int index = 0; index < infoList.length; index++)
			if(copyElement[index])
			{
				outList[outIndex] = infoList[index];
				outIndex++;
			}

		return outList;
	}

	private javax.sound.midi.MidiDevice.Info[] filterOutputDevices(javax.sound.midi.MidiDevice.Info infoList[], boolean select)
	{
		boolean copyElement[] = new boolean[infoList.length];
		int deviceCount = 0;
		for(int index = 0; index < infoList.length; index++)
			try
		{
				MidiDevice device = MidiSystem.getMidiDevice(infoList[index]);
				boolean hasReceiver = device.getMaxReceivers() != 0;
				if(hasReceiver == select)
				{
					copyElement[index] = true;
					deviceCount++;
				}
		}
		catch(MidiUnavailableException e) { }

		javax.sound.midi.MidiDevice.Info outList[] = new javax.sound.midi.MidiDevice.Info[deviceCount];
		int outIndex = 0;
		for(int index = 0; index < infoList.length; index++)
			if(copyElement[index])
			{
				outList[outIndex] = infoList[index];
				outIndex++;
			}

		return outList;
	}

	public void deconnect(){
		Debugger.getInstance().infoMessage(this.getClass(), "closing Connections...");
		if(osc != null)
			osc.stop();
		midiDevManager.stop();
		Debugger.getInstance().infoMessage(this.getClass(),"Connections closed");
	}
}
