package ch.maybites.px1m0d.plug.behaviours;

import ch.maybites.px1m0d.message.*;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.*;

public class BhaveSymbolGestures implements Behaviour, GstrLiaisonSymbolLstnr, GstrLiaisonFunctionLstnr, GstrMultipleClickLstnr, GstrButtonLstnr {
	private static final long serialVersionUID = 1L;

	Plug myPlug;

	int symbolClientLiaison; //contains the serverSocketID
	int symbolServerLiaison; //contains the clientSocketID

	int functionClientLiaison; //contains the functionServerSocketID
	int functionServerLiaison; //contains the functionClientSocketID

	int functionSocketIDReceiver;

	/*
	 * the difference between the following two SymbolFunction:
	 * the clientMessage is only created and sent back if the behaviour is beeing called
	 * by the method createdClientLiaisonForSymbol().
	 * the functionMessage is created by the button that establishes a functionLiaison
	 * and passes on the SymbolFuntion to its liaison partner. 
	 */
	SymbolFunction clientMessage;
	SymbolFunction functionMessage;

	boolean openForLiaison;

	int currentClicks;

	Px1MessageInternalLiaison postPoneLiaisonClosure;

	Px1MessageInternalLiaison postPoneFunctionLiaisonClosure;

	Px1MessageInternalPipe pipeMessage;

	public BhaveSymbolGestures(Plug p){
		myPlug = p;
		symbolClientLiaison = -1;
		symbolServerLiaison = -1;
		functionClientLiaison = -1;
		functionServerLiaison = -1; 

		functionSocketIDReceiver = -1;

		clientMessage = null;
		functionMessage = null;

		postPoneLiaisonClosure = null;
		postPoneFunctionLiaisonClosure = null;
		pipeMessage = null;

		openForLiaison = false;

		currentClicks = 0;
	}

	public void createdClientLiaisonForSymbol(int socketID_Server) {
		symbolClientLiaison = socketID_Server;
		clientMessage = new SymbolFunction(myPlug.parameter.getSocketID(), currentClicks);
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:createdClientLiaisonForSymbol("+socketID_Server+")");
	}

	public void createdServerLiaisonForSymbol(int socketID_Client) {
		symbolServerLiaison = socketID_Client;
		/*
		 * if a server liaison has been created without having a clientLiaison, the 
		 * functionMessage will be set. this means, this button is the base of the 
		 * function tree, even if in a later moment it will become a client of the
		 * 2nd order.
		 */
		if(clientMessage == null)
			functionMessage = new SymbolFunction(myPlug.parameter.getSocketID(), currentClicks);
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:createdServerLiaisonForSymbol("+socketID_Client+")");
	}

	public void closingLiaisonForSymbol(Px1MessageInternalLiaison msg) {
		/* if this plug is still maintaining a serverLiaison, it postpones the 
		 * closure of its own clientLiaison
		 * 
		 * REASON:
		 * 
		 * this allows the following gestures:
		 * 
		 * assume we have the following simplyfied board with socketIDs 0....15:
		 * 
		 * 	0	1	2	3	
		 * 	4	5	6	7
		 * 	8	9	10	11
		 * 	12	13	14	15
		 * 
		 * 1. dc (doubleclick) on 9 - hold
		 * 2. tc (trippleclick) on 6 -	hold - establishes a clientLiaison with 9
		 * 3. sc (singleclick) on 7 - hold - establishes a clientLiaison with 6
		 * 4. 9 release -> nothing happpens, since still a serverLiaison with 9 exists
		 * 5. 6 release -> nothing happpens, since still a serverLiaison with 7 exists
		 * 6. 7 release -> 
		 * 6.a		sends SymbolGesture to 6, closes clientLiaison with 6
		 * 6.b		6 sends SymbolGesture to 9, closes clientLiaison with 9
		 * 6.c		9 sends SymbolGesture to ....
		 * 
		 * this algorithm allows other sequential events too, like
		 * 
		 * 1. dc  on 9 - hold
		 * 2. tc  on 6 - hold - establishes a clientLiaison with 9
		 * 3. 9 release -> nothing happpens, since still a serverLiaison with 6 exists
		 * 4. sc  on 7 - hold - establishes a clientLiaison with 6
		 * 5. 7 release -> sends SymbolGesture to 6, closes clientLiaison with 6
		 * 6. 6 release -> 
		 * 6.a		sends SymbolGesture to 9, closes clientLiaison with 9
		 * 6.b		9 sends SymbolGesture to ....
		 * 
		 */
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:closingLiaisonForSymbol("+symbolClientLiaison+")");
		if(symbolServerLiaison == -1 || functionMessage != null){ // if no server liaison has been established OR if it is the base of the function tree
			sendClientMessage(msg);
		}else{ //otherwise postpone the closure of the client liaison until the server liaison has been closed...
			postPoneLiaisonClosure = msg;
		}
	}

	public void liaisonForSymbolClosedByClient() {
		/* the client of this plug has closed the so far 
		 * maintained server connection
		 */
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:liaisonForSymbolClosedByClient("+symbolServerLiaison+") ");
		if(postPoneLiaisonClosure != null){ // if a client liaison has been postponed, it is being closed now.
			sendClientMessage(postPoneLiaisonClosure);
			postPoneLiaisonClosure = null;
		}
		symbolServerLiaison = -1;
	}

	private void sendClientMessage(Px1MessageInternalLiaison liaisonClosure){
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:sendSymbol()");
		Px1MessageInternalSymbol sm = new Px1MessageInternalSymbol();
		sm.routeInternalyToSocketID(symbolClientLiaison);
		sm.createSymbolFunctionMessage(clientMessage);
		myPlug.outgoing(sm);
		myPlug.outgoing(liaisonClosure);

		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:clientMessage.getDepth() = " + clientMessage.getDepth());
		Debugger.getInstance().verboseMessage(this.getClass(),"                             getChannel() = " + clientMessage.getChannel());
		Debugger.getInstance().verboseMessage(this.getClass(),"                             getFunction() = " + clientMessage.getFunction());
		Debugger.getInstance().verboseMessage(this.getClass(),"                             getSubFunction() = " + clientMessage.getSubFunction());

		clientMessage = null;
		postPoneLiaisonClosure = null;
		symbolClientLiaison = -1;
	}

	public synchronized void symbolMessage(Px1MessageInternalSymbol msg) {
		Px1MessageInternalSymbol ism = (Px1MessageInternalSymbol)msg;
		if(ism.isSymbolFunctionMessage()){
			Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:SymbolFunctionMessage() from SocketID= " + ism.socketID_FROM);
			if(clientMessage != null){
				Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:addFunctionToClientMessage");
				clientMessage.addFunctionGesture(ism.getSymbolFunction());
			}else{
				Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:addFunctionToFunctionMessage");
				functionMessage.addFunctionGesture(ism.getSymbolFunction());
				sendFunction();
			}
		} else if(this.openForLiaison){
			SymbolFunction sFunction = functionMessage;
			if(sFunction == null){
				sFunction = new SymbolFunction(myPlug.parameter.getSocketID(), currentClicks);
			}
			if (ism.isButtonPresseMessage()){
				int socketIDFrom = ism.socketID_FROM;
				int diff = this.socketRowDiff(myPlug.parameter.getSocketID(), socketIDFrom);
				if(diff < -1 || diff > 1){
					pipeMessage = new Px1MessageInternalPipe();
					pipeMessage.routeInternalyToBoard();
					if(sFunction.getFunction() == -1){
						pipeMessage.createRelativeMessage(0, diff);
					} else {
						pipeMessage.createRelativeMessage(sFunction.getFunction() % 10, diff);
					}
				}
			} else if (ism.isButtonReleasedMessage()){
				pipeMessage = null;
			}
		}
	}

	private void sendFunction(){
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:sendFunction:functionMessage.getDepth() = " + functionMessage.getDepth());
		Debugger.getInstance().verboseMessage(this.getClass(),"                               getChannel() = " + functionMessage.getChannel());
		Debugger.getInstance().verboseMessage(this.getClass(),"                               getFunction() = " + functionMessage.getFunction());
		Debugger.getInstance().verboseMessage(this.getClass(),"                               getSubFunction() = " + functionMessage.getSubFunction());

		if(functionClientLiaison != -1 && functionMessage != null){
			Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:createSymbolFromClientMessage");
			Px1MessageInternalFunction fnct = new Px1MessageInternalFunction();
			fnct.createSymbolFromClientMessage(functionMessage);
			fnct.routeInternalyToSocketID(functionClientLiaison);
			myPlug.outgoing(fnct);
			functionClientLiaison = -1;
			functionMessage = null;
		} 
		if(functionServerLiaison != -1 && functionMessage != null){
			Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:createSymbolFromServerMessage");
			Px1MessageInternalFunction fnct = new Px1MessageInternalFunction();
			fnct.createSymbolFromServerMessage(functionMessage);
			fnct.routeInternalyToSocketID(functionServerLiaison);
			myPlug.outgoing(fnct);
			functionServerLiaison = -1;
			functionMessage = null;
		}

		if(postPoneFunctionLiaisonClosure != null){
			Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:sendpostPonedFunctionLiaisonClosure");
			myPlug.outgoing(postPoneFunctionLiaisonClosure);
			postPoneFunctionLiaisonClosure = null;
		}
	}

	public void closingLiaisonForFunction(Px1MessageInternalLiaison msg) {
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:closingLiaisonForFunction()");
		/*
		if(functionMessage == null){
			functionClientLiaison = -1;
			myPlug.outgoing(msg);
		} else {
			postPoneFunctionLiaisonClosure = msg;
		}
		 */
		postPoneFunctionLiaisonClosure = msg;
	}

	public void liaisonForFunctionClosedByClient() {
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:liaisonForFunctionClosedByClient()");
		/*
		if(functionMessage == null){
			functionServerLiaison = -1;
		} 
		 */
	}

	public void createdClientLiaisonForFunction(int socketID_Server) {
		functionClientLiaison = socketID_Server;
		if(functionMessage == null)
			functionMessage = new SymbolFunction(myPlug.parameter.getSocketID(), currentClicks);
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:createdClientLiaisonForFunction("+socketID_Server+")");
	}

	public void createdServerLiaisonForFunction(int socketID_Client) {
		functionServerLiaison = socketID_Client; 
		if(functionMessage == null)
			functionMessage = new SymbolFunction(myPlug.parameter.getSocketID(), currentClicks);
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:createdServerLiaisonForSymbol("+socketID_Client+")");
	}

	public void multiplePressed(int clicks) {
		currentClicks = clicks;
		if(clientMessage == null){
			openForLiaison = true;
			Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:open()");
		} else {
			Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:tried to open()");
		}
	}

	public synchronized void multipleReleased(int clicks) {
		openForLiaison = false;
		// if it is (the root of the symbolTree) && (there are no more branches [i.e. client connections] open) then..
		if(clientMessage == null && functionMessage != null && symbolServerLiaison == -1){
			sendFunction();
			functionMessage = null;
		}

		pipeMessage = null;
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:close()");
	}

	public synchronized void action() {
		if(pipeMessage != null){
			myPlug.outgoing(pipeMessage);	
			Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:action() sending pipeMessage");
			Debugger.getInstance().verboseMessage(this.getClass(),"           pipeMessage:channel = " + pipeMessage.getChannel());
			Debugger.getInstance().verboseMessage(this.getClass(),"           pipeMessage:value = " + pipeMessage.getRelativeValue());
		}
	}

	public void destroy() {;}

	public void reSurrect() {;}

	public void comatize() {;}

	public void reAnimate() {;}

	public boolean takeControl() {
		return (pipeMessage != null)?true:false;
	}

	public void buttonPressed() {
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:buttonPressed() sending Px1MessageInternalSymbol");
		Px1MessageInternalSymbol sm = new Px1MessageInternalSymbol();
		sm.routeInternalyToBoard(this.myPlug.parameter.getSocketID());
		sm.createButtonPressedMessage();
		myPlug.outgoing(sm);
	}

	public void buttonReleased() {
		Debugger.getInstance().verboseMessage(this.getClass(),"[SocketID=" + this.myPlug.parameter.getSocketID() + "]:buttonReleased() sending Px1MessageInternalSymbol");
		Px1MessageInternalSymbol sm = new Px1MessageInternalSymbol();
		sm.routeInternalyToBoard(this.myPlug.parameter.getSocketID());
		sm.createButtonReleasedMessage();
		myPlug.outgoing(sm);
	}

	/*
	 * returns the y (row) - distance between two socketID's
	 */
	private int socketRowDiff(int socketID_LowerLevel, int socketID_HigherLevel){
		int rowDiff = getRow(socketID_LowerLevel) - getRow(socketID_HigherLevel);
		//		int colDiff = getCol(socketID_LowerLevel) - getCol(socketID_HigherLevel);
		return rowDiff;
	}

	private int getRow(int socketID){
		return socketID / 8;
	}

	private int getCol(int socketID){
		return socketID % 8;
	}

}
