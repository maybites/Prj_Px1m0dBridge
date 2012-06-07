package ch.maybites.px1m0d.plug.gestures.listeners;

import ch.maybites.px1m0d.plug.PipeConnection;

public interface GstrPipeConnctManagerLstnr {
	
	/*
	 * this method is beeing called when a new pipe connection has been
	 * established. it passes on the new PipeConnection
	 */
	public void createdPipeConnection(PipeConnection con);

	/*
	 * this method is beeing called when an old pipe connection has been
	 * removed. it passes on the removed PipeConnection
	 */
	public void removedPipeConnection(PipeConnection con);
	
	/*
	 * this method is beeing called when a pipe connection has been
	 * suspended, which means that the client plug has been unplugged
	 * it passes on the suspended PipeConnection
	 */
	public void suspendPipeConnection(PipeConnection con);

	/*
	 * this method is beeing called when a pipe connection has been
	 * rewired, which means that the plug has been replugged
	 * it passes on the reWired PipeConnection
	 */
	public void reWiredPipeConnection(PipeConnection con);
	
}
