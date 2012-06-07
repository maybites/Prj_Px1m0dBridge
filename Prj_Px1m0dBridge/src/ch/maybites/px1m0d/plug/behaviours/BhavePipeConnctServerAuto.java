package ch.maybites.px1m0d.plug.behaviours;

import ch.maybites.px1m0d.Debugger;
import ch.maybites.px1m0d.message.Message;
import ch.maybites.px1m0d.message.Px1MessageInternalPipe;
import ch.maybites.px1m0d.plug.gestures.listeners.*;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.drawing.Painter;
import ch.maybites.px1m0d.plug.drawing.effects.*;
import gestalt.shape.Color;

import java.util.*;

/*	BhavePipeConnctServerAuto deals with messages from the GstrPipeConnctReceiverLstnr
 *  and GstrPipeConnctManagerLstnr in a couple of fundamental ways different to 
 *  its parent BhavePipeConnctServer. while listening to the same interfaces, it
 *  A) needs only one pipe connection to a plug in order to send multiple pipe
 *  	messages to its receiving end.
 *  B) instead of taking the first pipe job from the plug-setup-file and sending it to
 *  	all its connections, it sends all the pipe messages to each of its connection.
 * 
 */
public class BhavePipeConnctServerAuto extends BhavePipeConnctServer{
	private static final long serialVersionUID = 1L;

	public BhavePipeConnctServerAuto(Plug p, Painter pt){
		super(p, pt);
		Color c = p.parameter.getIconColor();
		myPipeVisuals = new EffctPipes(p, c);
		pt.addEffect(myPipeVisuals);
	}

	public void action(){
		Vector<Message> messages = super.myPlug.parameter.MessageFactory();
		for(int i = 0; i < messages.size(); i++){
			// the first defined message must be a pipe message.
			// it will be sent to all connections
			if(messages.get(i).myType == Message.TYPE_PIPE){
				if(myPlug.parameter.switchIsOn()){
					for(int j = 0; j < super.myConnections.size(); j++){
						PipeConnection pc = super.myConnections.elementAt(j);
						Px1MessageInternalPipe pipeMessage = ((Px1MessageInternalPipe)messages.get(i)).clone();
						pipeMessage.routeInternalyToSocketID(pc.getSocketID_TO());
						super.myPlug.outgoing(pipeMessage);
						myPipeVisuals.valueChange(pc.getSocketID_TO(), pc.getChannel(), pipeMessage.getAbsoluteValue());
						//Debugger.getInstance().debugMessage(getClass(),"Plug with mySocketID: "+ myPlug.parameter.getSocketID() +" sends pipe value Message to " + pc.getSocketID_TO());
					}
				}
			} else {
				//Debugger.getInstance().debugMessage(this.getClass(), "plug is sending midi message: " + myPlug.parameter.getPlugID() + " values: "  + ((javax.sound.midi.ShortMessage)((Message)messages.get(i)).getValue(Message.KEY_MidiMessage)).getData2());
				super.myPlug.outgoing(messages.get(i));
			}
		}
		super.pipeChannelHasChanged = false;
	}
}
