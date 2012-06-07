package ch.maybites.px1m0d.plug.collection.liveA;

import ch.maybites.px1m0d.message.Message;
import ch.maybites.px1m0d.message.Px1MessageNatebu;
import ch.maybites.px1m0d.message.Px1MessageSocket;
import ch.maybites.px1m0d.plug.*;
import gestalt.shape.*;
import ch.maybites.px1m0d.plug.config.PlugConfig;
import ch.maybites.px1m0d.plug.drawing.Painter;
import ch.maybites.px1m0d.plug.drawing.effects.*;
import ch.maybites.px1m0d.plug.drawing.effects.icons.IconBasic;
import ch.maybites.px1m0d.plug.behaviours.*;
import ch.maybites.px1m0d.plug.gestures.*;

public class PlgChainLink extends Plug{
	private static final long serialVersionUID = 1L;

	public PlgChainLink(Socket sm, PlugConfig param, Px1MessageSocket msg){
		super(sm, param, msg);
		painter = new Painter(this);
		agent = new Agent();
		watcher = new Watcher(this);
		param.print();

		//IconBasic basicPlug = new IconBasic(new Color(1.0f, 1.0f, 0.3f, 0.3f));
		painter.addEffect(parameter.getIconEffect());

		EffctLampSwitch lampSwitch = new EffctLampSwitch();
		painter.addEffect(lampSwitch);
		
		//--		//--
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
		// LINKS
		//********
		// handles Link liaisons 
		GstrLiaisonLink links = new GstrLiaisonLink(this);
		button.registerListener(links);
		//********
		// manages links to other plugs. it can be linked TO another plug, but not linked
		// AT - hence a client
		BhaveLinkClient linkClient = new BhaveLinkClient(this, painter);
		links.registerListener(linkClient);		
		// manages the links to this plug. it can be linked AT, but not linked
		// TO another plug - hence a server
		BhaveLinkServer linkServer = new BhaveLinkServer(this, painter);
		links.registerListener(linkServer);
		plugging.registerListener(linkServer);
		//********
		// PIPES
		//********
		// GstrLiaisonPipeReceiver handles pipe-liaisons, beeing a receiver 
		// this means it can only be on the receiving end of a pipe connection
		// carefull: a plug can also receiving pipe messages without beeing part of
		// a pipe connection -> see for example GstrPipeGlobalListening
		GstrLiaisonPipeReceiver pipeConnection = new GstrLiaisonPipeReceiver(this);
		// needs to listen to button events
		button.registerListener(pipeConnection);
		// GstrLiaisonPipePing pings back any Liaison pipe pings and acknoleges this way
		// the willingness of this plug to accept automated pipe connections
		GstrLiaisonPipePing pipePing = new GstrLiaisonPipePing(this);
		//********
		// is helping listening to RELATIVE pipe messages. it actually is listening for
		// the relevant function-messages that switch the listening mode on/off
		GstrPipeGlobalListening globalPipeListnr = new GstrPipeGlobalListening(this);
		//********
		// GstrPipeConnctReceiver helps handling the pipe connections AND
		// handles the different pipe messages. 
		GstrPipeConnctReceiver pipeMsgReceiver = new GstrPipeConnctReceiver(this);
		// or a pipe message 
		// or a global relative pipe message by listing to the pipe-global-message-listener
		// that switches the listening to those messages on/off
		globalPipeListnr.registerListener(pipeMsgReceiver);
		// it also listen to switch on/off events
		switsch.registerListener(pipeMsgReceiver);
		//********
		// JOBS
		//********
		// handles the assigned jobs
		BhaveJobManager jobManager = new BhaveJobManager(this, painter);
		button.registerListener(jobManager);
		plugging.registerListener(jobManager);
		switsch.registerListener(jobManager);
		natebu.registerListener(jobManager);
		pipeMsgReceiver.registerListener(jobManager);
		metronome.registerListener(jobManager);
		links.registerListener(jobManager);
		//--

		watcher.register(plugging);
		watcher.register(switsch);
		watcher.register(button);
		watcher.register(metronome);
		watcher.register(functions);
		watcher.register(links);
		watcher.register(pipeConnection);
		watcher.register(globalPipeListnr);
		watcher.register(pipeMsgReceiver);
		watcher.register(natebu);
		watcher.register(pipePing);
		
		agent.register(fLiaisonStub);
		agent.register(linkServer);
		agent.register(jobManager);//should be registered last in order to get the link notifications

		
	}

}
