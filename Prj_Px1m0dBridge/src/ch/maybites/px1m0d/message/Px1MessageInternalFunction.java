package ch.maybites.px1m0d.message;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.*;

/*
 * The Gesture Messages are only sent within the neighbourhood.
 */
public class Px1MessageInternalFunction extends Message{	
	private static final long serialVersionUID = 1L;

	private static final int FUNCTION_TYPE_Undefined = 0;
	private static final int FUNCTION_TYPE_SymbolFromClient = 1;
	private static final int FUNCTION_TYPE_SymbolFromServer = 2;
	
	private int myFunctionType = FUNCTION_TYPE_Undefined;
	
	private SymbolFunction _function;

	public Px1MessageInternalFunction(){
		super.myType = 	Message.TYPE_FUNCTION;
	}
	
	public void createSymbolFromClientMessage(SymbolFunction message){
		myFunctionType = FUNCTION_TYPE_SymbolFromClient;
		_function = message;
	}
	
	public boolean isSymbolFromClientMessage(){
		return(myFunctionType == FUNCTION_TYPE_SymbolFromClient)? true:false;
	}

	public void createSymbolFromServerMessage(SymbolFunction message){
		myFunctionType = FUNCTION_TYPE_SymbolFromServer;
		_function = message;
		if(message == null){
			Debugger.getInstance().debugMessage(getClass(), "Message is empty!!");
		}
	}
	
	public boolean isSymbolFromServerMessage(){
		return(myFunctionType == FUNCTION_TYPE_SymbolFromServer)? true:false;
	}

	public SymbolFunction getSymbolMessage(){
		return _function;
	}

}
