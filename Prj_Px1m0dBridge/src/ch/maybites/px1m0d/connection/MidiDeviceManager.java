package ch.maybites.px1m0d.connection;
import javax.sound.midi.*;

import ch.maybites.px1m0d.Debugger;

import java.util.*;

public class MidiDeviceManager {
	
    Hashtable<String, MidiDevice> devices = new Hashtable<String, MidiDevice>();
    
    MidiDeviceManager(){
    	;
    }
    		
	public Receiver requestReceiver(MidiDevice.Info d) throws MidiUnavailableException{
		MidiDevice m = (MidiDevice)devices.get(d.getName());

	    if (m != null) {
			List<Receiver> listReceivers = m.getReceivers();
			if(listReceivers.size() > 0)
				return (Receiver)listReceivers.get(0);
	    }
	    MidiDevice md = MidiSystem.getMidiDevice(d);
	    md.open();
	    devices.put(d.getName(), md);
	    return md.getReceiver();
	}
	
	public void setTransmitter(MidiDevice.Info d, Receiver r) throws MidiUnavailableException{
		Debugger.getInstance().infoMessage(this.getClass(),"setTransmitter for " + d.getName());
		MidiDevice m = (MidiDevice)devices.get(d.getName());

	    if (m != null) {
	    	Debugger.getInstance().infoMessage(this.getClass(),"get existing Transmitter...");
			List<Transmitter> listTransmitters = m.getTransmitters();
			if(listTransmitters.size() > 0)
				((Transmitter)listTransmitters.get(0)).setReceiver(r);
	    } else {
	    	Debugger.getInstance().infoMessage(this.getClass(),"get new device and Transmitter...");
	    	MidiDevice md = MidiSystem.getMidiDevice(d);
	    	md.open();
	    	Transmitter t = md.getTransmitter();
	    	t.setReceiver(r);
	    	devices.put(d.getName(), md);
	    }
	}	
	
	public void stop(){
	    for (Enumeration<MidiDevice> e = devices.elements() ; e.hasMoreElements() ;) {
	    	MidiDevice md = (MidiDevice)e.nextElement();
			List<Transmitter> listTransmitters = md.getTransmitters();
            for(Iterator<Transmitter> i$ = listTransmitters.iterator(); i$.hasNext();)
            {
                ((Transmitter)i$.next()).close();
            }
            md.close();
	    }
	}

}
