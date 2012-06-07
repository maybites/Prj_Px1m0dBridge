package ch.maybites.px1m0d.message;

import oscP5.*;

/*
 * OSC Message for Sending Osc Message to the outside world
 * 
 * Not suitable for receiving OSC Messages!! use different Message instead
 */
public class Px1MessageOsc extends Message{		
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_IP_ADDRESS = "world";
	private OscMessage _oscMessage;
	private String _ipAddress;
	private int _ipAddressPort;

	public Px1MessageOsc(int gate, int receiver){
		super.myRoute = gate;
		super.myType = Message.TYPE_OSC;
		super.myReceiver = receiver;
	}

	public Px1MessageOsc(OscMessage m, String ipaddress, int port){
		this(Message.ROUTE_OUTSIDE, Message.RECEIVER_WORLD);
		_oscMessage = m;
		_ipAddress = ipaddress;
		_ipAddressPort = port;
	}

	public Px1MessageOsc(int gate, int receiver, String theAddrPattern){
		this(gate, receiver);
		_oscMessage = new OscMessage(theAddrPattern);
	}
	
	public Px1MessageOsc(int gate, int receiver, OscMessage m){
		this(gate, receiver);
		_oscMessage = m;
	}
	
	public OscMessage getOscMessage(){
		return _oscMessage;
	}
	
	public boolean isDefaultIPAddress(){
		return (_ipAddress.equals(DEFAULT_IP_ADDRESS))?true:false;
	}
	
	public String getIPAddress(){
		return _ipAddress;
	}
	
	public int getPort(){
		return _ipAddressPort;
	}
	
	public String printLine(){
		return " Osc: " + _oscMessage.address();
	}
	
	public String print(){
		String 	s  = ">Osc Message START<\n";
		s += " - Type is " + this.myType + "\n";
		s += " - Receiver is " + this.myReceiver + "\n";
		s += " - OscAdress is " + _oscMessage.address() + "\n";
		s += ">Osc Message END<\n";
		return s;
	}
	
}
