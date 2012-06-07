package ch.maybites.px1m0d.plug.behaviours;

import java.util.Vector;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.message.Message;
import ch.maybites.px1m0d.message.Px1MessageInternalCommand;
import ch.maybites.px1m0d.message.Px1MessageInternalLiaison;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.drawing.*;
import ch.maybites.px1m0d.plug.drawing.effects.*;

public class BhaveLinkClient implements Behaviour, GstrLiaisonLinkLstnr{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;
	private Painter myPainter;
	
	private EffctLinkLine _myEffect;

	boolean _myTakeControl = false;
	
	public BhaveLinkClient(Plug p, Painter pt){
		myPlug = p;
		myPainter = pt;
		_myEffect = new EffctLinkLine(myPlug);
		myPainter.addEffect(_myEffect);
	}
	
	public void closingLiaisonForLink(Px1MessageInternalLiaison msg){myPlug.outgoing(msg);}

	public void createdClientLiaisonForLink(int socketID_Server, long uniqueID_Server) {;}

	public void createdServerLiaisonForLink(int socketID_Client, long uniqueID_Client) {
		Debugger.getInstance().debugMessage(getClass(), "createdServerLiaisonForLink("+socketID_Client+") SocketID = " + myPlug.parameter.getSocketID());
	}

	public void liaisonForLinkClosedByClient() {;}

	public void linkMessage(Px1MessageInternalCommand msg) {
		if(msg.isEstablishLinkMessage()){
			if(myPlug.parameter.plugIsUnLinked()){
				Debugger.getInstance().debugMessage(getClass(), "linkMessage():link SocketID = " + myPlug.parameter.getSocketID());
				myPlug.parameter.setLink(msg.getLinkPlug());
				_myEffect.link();
				_myTakeControl = true;
				createAddLinkEffect();
			}else if(!myPlug.parameter.getLink().equals(msg.getLinkPlug())){
				Debugger.getInstance().debugMessage(getClass(), "linkMessage():link SocketID = " + myPlug.parameter.getSocketID());
				myPlug.parameter.setLink(msg.getLinkPlug());
				_myEffect.link();
				_myTakeControl = true;
				createAddLinkEffect();
			}else if (myPlug.parameter.getLink().equals(msg.getLinkPlug())){
				Debugger.getInstance().debugMessage(getClass(), "linkMessage():unlink SocketID = " + myPlug.parameter.getSocketID());
				myPlug.parameter.unLink();
				_myEffect.unlink();
				_myTakeControl = true;
				createRemoveLinkEffect();
			}
		}
		if(msg.isReMoveLinkMessage() && myPlug.parameter.plugIsLinked()){
			if(myPlug.parameter.getLink().equals(msg.getLinkPlug())){
				Debugger.getInstance().debugMessage(getClass(), "linkMessage():unlink SocketID = " + myPlug.parameter.getSocketID());
				myPlug.parameter.unLink();
				_myEffect.unlink();
				_myTakeControl = true;
				createRemoveLinkEffect();
			}
		}
		if(msg.isSuspendLinkMessage() && myPlug.parameter.plugIsLinked()){
			if(myPlug.parameter.getLink().equals(msg.getLinkPlug())){
				Debugger.getInstance().debugMessage(getClass(), "linkMessage():suspend SocketID = " + myPlug.parameter.getSocketID());
				_myEffect.unlink();
				_myTakeControl = true;
			}
		}
		if(msg.isReWireLinkMessage() && myPlug.parameter.plugIsLinked()){
			if(myPlug.parameter.getLink().equals(msg.getLinkPlug())){
				Debugger.getInstance().debugMessage(getClass(), "linkMessage():rewire SocketID = " + myPlug.parameter.getSocketID());
				_myEffect.link();
				_myTakeControl = true;
			}
		}
	}
	
	private void createRemoveLinkEffect(){
		int[] sequence = {0, 5, 10, 15, 20, 25, 30};
		myPlug.painter.addEffect(new EffctLampBlinker(50, sequence, false));
	}
	
	private void createAddLinkEffect(){
		int[] sequence = {0, 5, 10, 15, 20};
		myPlug.painter.addEffect(new EffctLampBlinker(50, sequence, false));
	}

	public void action(){;}
	
	public boolean takeControl(){return false;}
	
	public void reSurrect(){;}

	public void destroy(){
		Debugger.getInstance().debugMessage(getClass(), "linkMessage():unlink SocketID = " + myPlug.parameter.getSocketID());
		myPlug.parameter.unLink();
	}

	public void comatize() {;}

	public void reAnimate() { 
		if(myPlug.parameter.plugIsLinked()){
			// if there is a link-plug and it has been distroyed, a new message is generated that unlinks
			// this plug
			if(myPlug.parameter.getLink().isDestroyed){
				Debugger.getInstance().debugMessage(getClass(), "reAnimate() unlink own Link SocketID = " + myPlug.parameter.getSocketID());
				Px1MessageInternalCommand link = new Px1MessageInternalCommand();
				link.routeInternalyToSocketID(myPlug.parameter.getSocketID());
				link.createReMoveLinkMessage(myPlug.parameter.getLink());
				myPlug.outgoing(link);
			}
		}
	}

}
