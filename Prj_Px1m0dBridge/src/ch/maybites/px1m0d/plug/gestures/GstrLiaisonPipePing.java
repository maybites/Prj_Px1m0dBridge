package ch.maybites.px1m0d.plug.gestures;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.plug.Plug;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.message.*;

import java.util.*;

/*
 * "GstrLiaisonPipePing" simply sends a Pipe Liaison Ping back in order to
 * acknoledge, that this plug is willing to accept pipe connections without
 * user gesture. it is ususally used to allow another plug in close proximity
 * to create automatically a pipeconnection.
 */
public class GstrLiaisonPipePing implements Gesture{
	private static final long serialVersionUID = 1L;
	private Plug myPlug;

	public GstrLiaisonPipePing(Plug p){
		myPlug = p;
	}
	
	public void incomming(Message msg){
		if(msg.myType == (Message.TYPE_LIAISON)){
			Px1MessageInternalLiaison ml = (Px1MessageInternalLiaison)msg;
			if(ml.isLiaisonType(Px1MessageInternalLiaison.LIAISON_TYPE_PingForPipeRequest)){
				//make sure it doesnt ping itself:
				if(myPlug.parameter.getSocketID() != ml.socketID_FROM){
					Px1MessageInternalLiaison mlNew = new Px1MessageInternalLiaison();
					mlNew.createLiaison(Px1MessageInternalLiaison.LIAISON_TYPE_PingForPipeResponse);
					mlNew.routeInternalyToSocketID(ml.socketID_FROM);
					myPlug.outgoing(mlNew);
				}
			}
		}
	}

	public void reSurrect(){;}

	public void destroy(){;}

	public void comatize(){;}

	public void reAnimate(){;}

}
