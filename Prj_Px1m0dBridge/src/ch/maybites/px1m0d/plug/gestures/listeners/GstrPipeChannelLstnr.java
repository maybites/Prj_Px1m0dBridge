package ch.maybites.px1m0d.plug.gestures.listeners;

public interface GstrPipeChannelLstnr {

	/*
	 * returns -1 if its listing on all channels or the channel number
	 */
	public void startListeningForPipe(int channel);
	
	public void stopListeningForPipe();
}
