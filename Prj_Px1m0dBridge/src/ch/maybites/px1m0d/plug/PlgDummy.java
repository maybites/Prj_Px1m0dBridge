package ch.maybites.px1m0d.plug;
import ch.maybites.px1m0d.message.Px1MessageSocket;
import ch.maybites.px1m0d.plug.behaviours.*;
import ch.maybites.px1m0d.plug.config.PlugConfig;
import gestalt.shape.*;
import ch.maybites.px1m0d.plug.drawing.Painter;
import mathematik.*;
import ch.maybites.px1m0d.plug.drawing.effects.*;
import ch.maybites.px1m0d.plug.drawing.effects.icons.EffctDummy;
import ch.maybites.px1m0d.plug.gestures.*;

public class PlgDummy extends Plug{
	
	private static final long serialVersionUID = 1L;

	public PlgDummy(Socket sm, PlugConfig config, Px1MessageSocket msg){
		super(sm, config, msg);
		painter = new Painter(this);
//		myPainter.addEffect(new EffctSocket(new Color(0.4f, 0.4f, 0.4f, 0.4f)));
		painter.addEffect(new EffctDummy(new Vector3f(.2f, .2f, .01f), new Color(0.4f, 0.4f, 0.4f, 0.4f)));
		watcher = new Watcher(this);
		agent = new Agent();
		timeToKill = 0;
	
		GstrButton button = new GstrButton(this);
		GstrMultipleClick multipleClicker = new GstrMultipleClick(this, 300);
		GstrLiaisonSymbol symbolLiaison = new GstrLiaisonSymbol(this);
		GstrLiaisonFunctionForButtons functionLiaison = new GstrLiaisonFunctionForButtons(this);
		
		BhaveSymbolGestures symbolGestures = new BhaveSymbolGestures(this);

		button.registerListener(symbolGestures);
		
		multipleClicker.registerListener(symbolLiaison);
		multipleClicker.registerListener(functionLiaison);
		multipleClicker.registerListener(symbolGestures);
		
		symbolLiaison.registerListener(symbolGestures);
		functionLiaison.registerListener(symbolGestures);
		
		watcher.register(button);
		watcher.register(multipleClicker);
		watcher.register(symbolLiaison);		
		watcher.register(functionLiaison);		
		
		agent.register(symbolGestures);
	}

}
