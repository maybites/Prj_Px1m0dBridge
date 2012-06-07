package ch.maybites.px1m0d.connection;
import javax.sound.midi.*;

public class Connection {
	private boolean isOpen = false;
	String myID;
	
	private Receiver midiReceiver;
	
	public Connection(String id, Receiver r){
		reset();
		isOpen = true;
        midiReceiver = r;
        myID = id;
	}
	
	public Connection(String id){
		reset();
		isOpen = true;
		myID = id;
	}
	
	public String getID(){
		return myID;
	}
	
	private void reset(){
		isOpen = false;
		midiReceiver = null;
	}
		
	public void send(MidiMessage m){
		midiReceiver.send(m, -1L);
	}
	
	public boolean isOpen(){
		return isOpen;
	}
	
	public void close(){
		reset();
	}
		
}
