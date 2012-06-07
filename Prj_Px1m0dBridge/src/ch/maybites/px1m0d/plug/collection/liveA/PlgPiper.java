package ch.maybites.px1m0d.plug.collection.liveA;

import ch.maybites.px1m0d.message.*;
import ch.maybites.px1m0d.plug.*;
import ch.maybites.px1m0d.plug.behaviours.*;
import ch.maybites.px1m0d.plug.config.PlugConfig;
import ch.maybites.px1m0d.plug.drawing.*;
import ch.maybites.px1m0d.plug.drawing.effects.*;
import ch.maybites.px1m0d.plug.drawing.effects.icons.IconBasic;
import ch.maybites.px1m0d.plug.gestures.*;
import gestalt.shape.*;

/**
  * Switch Plug is used to select a sample within a track and mutes/unmutes the track
  * Usage: pluggin in selects the sample within the track (sends CCC1, socketID, 127)
  *        Pressing the button and switching it on this way unmutes the track (sends CCC10, socket.posX, 127)
  *
  */
public class PlgPiper extends Plug {
	private static final long serialVersionUID = 1L;

	public PlgPiper(Socket sm, PlugConfig param, Px1MessageSocket msg){
		super(sm, param, msg);
		painter = new Painter(this);
		agent = new Agent();
		watcher = new Watcher(this);
		param.print();
		
		//IconBasic basicPlug = new IconBasic(new Color(1.0f, 1.0f, 0.3f, 0.3f));
		painter.addEffect(parameter.getIconEffect());

		EffctLampSwitch lampSwitch = new EffctLampSwitch();
		painter.addEffect(lampSwitch);
		
		//--
		GstrPlugging plugging = new GstrPlugging(this);
		GstrButton button = new GstrButton(this);
		GstrSwitch switsch = new GstrSwitch(this);
		switsch.registerListener(lampSwitch);
		plugging.registerListener(lampSwitch);
		GstrNatebu natebu = new GstrNatebu(this);
		GstrMetronome metronome = new GstrMetronome(this);
		//************
		// FUNCTIONS
		//************
		// Functions are Gestures on "naked" buttons. These Objects are used to open
		// Liaisons with buttons so the buttons can send infos about the pressed 
		// gestures to this plug. they are called functions because they shouldnt be 
		// mixed up with the Gesture Objects of the bridge architecture.
		// Functions are used to set the plug into a special condition, like beeing 
		// able to open or close a pipe liaison, or listening to global pipe messages		
		//********
		// handles the function-liaisons 
		GstrLiaisonFunctionForPlugs functions = new GstrLiaisonFunctionForPlugs(this);
		button.registerListener(functions);
		//********
		BhaveLiaisonFunctionStub fLiaisonStub = new BhaveLiaisonFunctionStub(this);
		functions.registerListener(fLiaisonStub);
		//********
		// PIPES
		//********
		// handles pipe-liaisons, beeing a server 
		// this means it can only be on the sending end of a pipe connection
		// carefull: a plug can also receiving pipe messages without beeing part of
		// a pipe connection -> see for example GstrPipeConnctReceiver
		GstrLiaisonPipeServer pipeLiasonServer = new GstrLiaisonPipeServer(this);
		// needs to listen to button events
		button.registerListener(pipeLiasonServer);
		// GstrLiaisonPipePing pings back any Liaison pipe pings and acknowledges this way
		// the willingness of this plug to accept automated pipe connections
		GstrLiaisonPipePing pipePing = new GstrLiaisonPipePing(this);
		//********
		// is helping listening to relative pipe messages. it actually is listening for
		// the relevant function-messages that switch the listening mode on/off
		GstrPipeGlobalListening pipeGlobalListnr = new GstrPipeGlobalListening(this);
		//********
		// handles the pipe connections on the sending end and passes their condition 
		// onto its listeners
		GstrPipeConnctServer pipeServer = new GstrPipeConnctServer(this);
		// listens to the pipe-liaison-server
		pipeLiasonServer.registerListener(pipeServer);
		// listens to button events
		button.registerListener(pipeServer);
		//********
		// GstrPipeConnctReceiver helps handling the pipe connections on the receiving end AND
		// handles the different pipe messages - for what it is beeing used here, since it
		// will not handle any pipe connections because no receiving liaison is possible. 
		GstrPipeConnctReceiver pipeMsgReciever = new GstrPipeConnctReceiver(this);
		// or a ABSOLTE pipe message 
		// or a global RELATIVE pipe message by listing to the pipe-global-message-listener
		// that switches the listening to those messages on/off
		pipeGlobalListnr.registerListener(pipeMsgReciever);
		// it also listens to switch on/off events
		switsch.registerListener(pipeMsgReciever);
		//********
		// this behaviour serves the abolute pipe-messages to all its pipe-connections
		BhavePipeConnctServer piper = new BhavePipeConnctServer(this, painter);
		// it listens to the pipe-connection-server for the conditions of the pipe connections
		pipeServer.registerListener(piper);
		// it listens to the pipe connection receiver for change of pipe events
		pipeMsgReciever.registerListener(piper);
		// it listens to natebu events
		natebu.registerListener(piper);
		// it listens to switch events
		switsch.registerListener(piper);
		// it also listens to metronome clicks
		metronome.registerListener(piper);

		watcher.register(plugging);
		watcher.register(switsch);
		watcher.register(button);
		watcher.register(metronome);
		watcher.register(functions);
		watcher.register(pipeLiasonServer);
		watcher.register(pipeGlobalListnr);
		watcher.register(pipeServer);
		watcher.register(pipeMsgReciever);
		watcher.register(natebu);
		watcher.register(pipePing);
		
		agent.register(piper);
		agent.register(fLiaisonStub);
	}

}
