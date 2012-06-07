package ch.maybites.px1m0d.message;

import ch.maybites.px1m0d.plug.*;

/*
 * The Command Message is used for Message between different kind of 
 * Gestures and Behaviours. Usually it is used to establish certain kind
 * of relationships between plugs, like links or pipes
 */
public class Px1MessageInternalCommand extends Message{	
	private static final long serialVersionUID = 1L;

	private static final int COMMAND_TYPE_Undefined = 0;
	private static final int COMMAND_TYPE_EstablishLink = 1;
	private static final int COMMAND_TYPE_ReMoveLink = 2;
	private static final int COMMAND_TYPE_SuspendLink = 3;
	private static final int COMMAND_TYPE_ReWireLink = 4;
	private static final int COMMAND_TYPE_PluggingLink = 5;
	private static final int COMMAND_TYPE_EstablishPipe = 6;
	private static final int COMMAND_TYPE_SuspendPipe = 7;
	private static final int COMMAND_TYPE_ReWirePipe = 8;
	private static final int COMMAND_TYPE_ReMovePipe = 9;
	
	private int myCommandType = COMMAND_TYPE_Undefined;
	
	private PipeConnection _myPipeConnection;
	
	private Plug _link;

	public Px1MessageInternalCommand(){
		super.myType = 	Message.TYPE_COMMAND;
	}
	
	public void createEstablishLinkMessage(Plug link){
		myCommandType = COMMAND_TYPE_EstablishLink;
		_link = link;
	}
	
	public boolean isEstablishLinkMessage(){
		return(myCommandType == COMMAND_TYPE_EstablishLink)? true:false;
	}	

	public void createReMoveLinkMessage(Plug link){
		myCommandType = COMMAND_TYPE_ReMoveLink;
		_link = link;
	}
	
	public boolean isReMoveLinkMessage(){
		return(myCommandType == COMMAND_TYPE_ReMoveLink)? true:false;
	}	

	public void createSuspendLinkMessage(Plug link){
		myCommandType = COMMAND_TYPE_SuspendLink;
		_link = link;
	}
	
	public boolean isSuspendLinkMessage(){
		return(myCommandType == COMMAND_TYPE_SuspendLink)? true:false;
	}	

	public void createReWireLinkMessage(Plug link){
		myCommandType = COMMAND_TYPE_ReWireLink;
		_link = link;
	}
	
	public boolean isReWireLinkMessage(){
		return(myCommandType == COMMAND_TYPE_ReWireLink)? true:false;
	}	
	
	public void createPluggingLinkMessage(Plug link){
		myCommandType = COMMAND_TYPE_PluggingLink;
		_link = link;
	}
	
	public boolean isPluggingLinkMessage(){
		return(myCommandType == COMMAND_TYPE_PluggingLink)? true:false;
	}	

	public Plug getLinkPlug(){
		return _link;
	}

	public long getUniqueID(){
		return this.plugUniqueID_FROM;
	}

	public void createReMovePipeMessage(PipeConnection connection){
		myCommandType = COMMAND_TYPE_ReMovePipe;
		_myPipeConnection = connection;
	}
	
	public boolean isReMovePipeMessage(){
		return(myCommandType == COMMAND_TYPE_ReMovePipe)? true:false;
	}	

	public void createReWirePipeMessage(PipeConnection connection){
		myCommandType = COMMAND_TYPE_ReWirePipe;
		_myPipeConnection = connection;
	}
	
	public boolean isReWirePipeMessage(){
		return(myCommandType == COMMAND_TYPE_ReWirePipe)? true:false;
	}	

	public void createEstablishPipeMessage(PipeConnection connection){
		myCommandType = COMMAND_TYPE_EstablishPipe;
		_myPipeConnection = connection;
	}
	
	public boolean isEstablishPipeMessage(){
		return(myCommandType == COMMAND_TYPE_EstablishPipe)? true:false;
	}	
	
	public PipeConnection getPipeConnection(){
		return _myPipeConnection;
	}	

	public void createSuspendPipeMessage(PipeConnection connection){
		myCommandType = COMMAND_TYPE_SuspendPipe;
		_myPipeConnection = connection;
	}
	
	public boolean isSuspendPipeMessage(){
		return(myCommandType == COMMAND_TYPE_SuspendPipe)? true:false;
	}	

}
