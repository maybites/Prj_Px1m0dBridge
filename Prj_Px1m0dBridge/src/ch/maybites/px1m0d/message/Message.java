package ch.maybites.px1m0d.message;

import java.io.*;

public abstract class Message implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final int ROUTE_UNDEFINED = 0;		
	public static final int ROUTE_SOCKETID = 1;		//sends the message directly to a certain socketid
	public static final int ROUTE_PLUGID = 2;		//sends the message directly to a certain plugid
	public static final int ROUTE_BOARD = 3;			//sends the message to all the sockets of the board
	public static final int ROUTE_BOARD_ROW = 4;		//sends the message to all the sockets of the row (indicated by the key)
	public static final int ROUTE_BOARD_COL = 5;		//sends the message to all the sockets of the column (indicated by the key)
	public static final int ROUTE_BOARD_HOOD = 6;		//sends the message to all the sockets of the neighbourhood around the indicated plugid
	public static final int ROUTE_OUTSIDE = 7;		//sends the message to the outside world
	public static final int ROUTE_BRIDGE = 8;		//sends the message to the bridge
	public static final int ROUTE_POSTPONE = 9;		//sends the message to the bridge for postponing
	public static final int ROUTE_PLUGUNIQUEID = 10;		//sends the message to a plug with the spec uniqueID
	
	public static final int TYPE_UNDEFINED = 0;
	public static final int TYPE_SOCKET = 1;
	public static final int TYPE_NATEBU = 2;
	public static final int TYPE_PLUG = 3;
	public static final int TYPE_MIDI = 4;
	public static final int TYPE_OSC = 5;
	public static final int TYPE_SYMBOL = 6;
	public static final int TYPE_COMMAND = 7;
	public static final int TYPE_PX1M0DRESETED = 8;
	public static final int TYPE_NOTIFY = 9;
	public static final int TYPE_LIAISON = 10;
	public static final int TYPE_PIPE = 11;
	public static final int TYPE_FUNCTION = 12;
	public static final int TYPE_METRONOME = 13;
	
	public static final int RECEIVER_UNDEFINED = 0;
	public static final int RECEIVER_BRIDGE = 1;
	public static final int RECEIVER_SOCKET = 2;
	public static final int RECEIVER_PLUG = 3;
	public static final int RECEIVER_NATEBU = 4;
	public static final int RECEIVER_WORLD = 5;
	public static final int RECEIVER_PX1M0D = 6;
	
	public int socketID_TO, socketID_FROM;
	public long plugUniqueID_TO, plugUniqueID_FROM;
	public int plugID_TO, plugID_FROM;
	
	public int myRoute, myType, myReceiver;
	
	private int myPostponedRoute = ROUTE_UNDEFINED;
	private long myPostponedTime = 0;
	
	// copies all the data into the provided object reference
	protected void copyMyselfTo(Message message){
		message.myPostponedRoute = this.myPostponedRoute;
		message.myPostponedTime = this.myPostponedTime;
		message.myReceiver = this.myReceiver;
		message.myRoute = this.myRoute;
		message.myType = this.myType;
		message.plugID_FROM = this.plugID_FROM;
		message.plugID_TO = this.plugID_TO;
		message.plugUniqueID_FROM = this.plugUniqueID_FROM;
		message.plugUniqueID_TO = this.plugUniqueID_TO;
		message.socketID_FROM = this.socketID_FROM;
		message.socketID_TO = this.socketID_TO;
	}
	
	/*
	 * is called by the socket when beeing sent to the dispatcher
	 */
	public Message setSenderID(int socketIDFROM, int plugIDFROM, long uniqueIDFrom){
		socketID_FROM = (socketID_FROM != -1)?socketIDFROM : -1;
		plugID_FROM = plugIDFROM;
		plugUniqueID_FROM = uniqueIDFrom;
		return this;
	}
		
	public void routeInternalyToSocketID(int socketIDTO){
		myRoute =	Message.ROUTE_SOCKETID;
		myReceiver =	Message.RECEIVER_PLUG;
		socketID_TO = socketIDTO;
	}

	public void routeInternalyToUID(long uniqueIDTo){
		myRoute =	Message.ROUTE_PLUGUNIQUEID;
		myReceiver =	Message.RECEIVER_PLUG;
		plugUniqueID_TO = uniqueIDTo;
	}

	public void routeInternalyToPlugID(int plugIDTO){
		myRoute =	Message.ROUTE_PLUGID;
		myReceiver =	Message.RECEIVER_PLUG;
		plugID_TO = plugIDTO;
	}

	public void routeInternalyToRow(int socketIDTO){
		myRoute =	Message.ROUTE_BOARD_ROW;
		myReceiver =	Message.RECEIVER_PLUG;
		socketID_TO = socketIDTO;
	}

	public void routeInternalyToCol(int socketIDTO){
		myRoute =	Message.ROUTE_BOARD_COL;
		myReceiver =	Message.RECEIVER_PLUG;
		socketID_TO = socketIDTO;
	}

	public void routeInternalyToNeighbour(int socketIDTO){
		myRoute =	Message.ROUTE_BOARD_HOOD;
		myReceiver =	Message.RECEIVER_PLUG;
		socketID_TO = socketIDTO;
	}

	/*
	 * sends this message to the entire board, except the sender
	 */
	public void routeInternalyToBoard(){
		myRoute =	Message.ROUTE_BOARD;
		myReceiver =	Message.RECEIVER_PLUG;
	}
	
	/*
	 * sends this message to the entire board, except the specified socketID
	 * if socketIDFROM = -1 it will be sent to all the sockets, including the sender
	 */
	public void routeInternalyToBoard(int socketIDFROM){
		myRoute =	Message.ROUTE_BOARD;
		myReceiver =	Message.RECEIVER_PLUG;
		socketID_FROM = socketIDFROM;
	}

	public void routeExternalyToWorld(){
		myRoute =	Message.ROUTE_OUTSIDE;
		myReceiver = Message.RECEIVER_WORLD;
	}

	/*
	 * this method is used by the dispatcher only in order to send the message through
	 * the correct channels
	 */
	public boolean isRoutedOver(int gate){
		return (myRoute == gate)?true:false;
	}
	
	public void postponeFor(int milliseconds){
		myPostponedRoute = myRoute;
		myPostponedTime = System.currentTimeMillis() + milliseconds;
		myRoute = ROUTE_POSTPONE;
	}
	
	public boolean postponeHasEnded(){
		if(System.currentTimeMillis() > myPostponedTime){
			myRoute = myPostponedRoute;
			return true;
		}
		return false;
	}
	
	public String printLine(){
		return "called printLine() function: not defined yet";
	}
	
	public String print(){
		return "called print() function: not defined yet";
	}
}
