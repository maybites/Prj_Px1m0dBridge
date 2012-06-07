package ch.maybites.px1m0d.plug;
import ch.maybites.px1m0d.SceneManager;
import ch.maybites.px1m0d.message.*;
import processing.core.*;
import java.io.*;

public class Socket implements Serializable{
	private static final long serialVersionUID = 1L;
	private MessageQueue myIncommingMessageQueue;
	private MessageQueue myOutgoingMessageQueue;
	private SceneManager mySceneManager;

	private Px1MessageNatebu _myLastNatebuMessage;
	
	public Plug myPlug;
	
	public int myXpos, myYpos, myId;
	
	public Socket(SceneManager sm, int x, int y){
		myIncommingMessageQueue = new MessageQueue();
		myOutgoingMessageQueue = new MessageQueue();
		mySceneManager = sm;
		myXpos = x;
		myYpos = y;
		myId = x + y * 8;
		myPlug = mySceneManager.plugFactory.getPlugDefault(this, null);
	}

	public void refreshPlug(){
		mySceneManager.plugFactory.refreshPlug(myPlug);
	}
	
	protected void outgoing(Message m){
		myOutgoingMessageQueue.addMessage(m);
	}

	public void synchronize(){
		while(this.myOutgoingMessageQueue.hasMoreMessages()){
			mySceneManager.send(this.myOutgoingMessageQueue.getNextMessage());	
		}
		myPlug.synchronize();
	}

	public void incomming(Message m){
		myIncommingMessageQueue.addMessage(m);
	}

	public void update(){
		while(myIncommingMessageQueue.hasMoreMessages()){
			Message m = myIncommingMessageQueue.getNextMessage();
			if(m.myReceiver == (Message.RECEIVER_SOCKET)){
				if(m.myType == (Message.TYPE_SOCKET)){
					Px1MessageSocket msg = (Px1MessageSocket)m;
					if(msg.getIsPlugged() != myPlug.parameter.plugIsPlugged()){
						myPlug.socketUnplug(msg);
						myPlug.incomming(m); //this makes sure the plugs Gestures 
						/* can recognize the unplugging before the plug
						 * gets enbalmed, and subsequently destroyed.
						 */

						mySceneManager.plugFactory.returnPlugObject(myPlug); // returns plug to
						/*	plugfactory for enbalment. if the plug is replugged bevore
						 *  the time To Kill has been reached, it will be reused with all
						 *  the settings intact. 
						 */
						if(mySceneManager.plugFactory.isDefaultClass(myPlug)){
							myPlug = mySceneManager.plugFactory.getPlugObject(this, msg);
							if(_myLastNatebuMessage != null)
								myPlug.resetParameter(_myLastNatebuMessage);
						} else {
							myPlug = mySceneManager.plugFactory.getPlugDefault(this, msg);
						}
					} else {
						myPlug.socketUpdate(msg); 
						/* this method has to be called if a socketMessage arrives.
						 * the plugFactory is calling it as well, thats why it doesnt has to be called
						 * again for all newly plugged in plugs
						 */
						
					}
					myPlug.incomming(m);
				}
			} else if (m.myReceiver == (Message.RECEIVER_PLUG)){
				if(m.myType == Message.TYPE_NATEBU)
					_myLastNatebuMessage = (Px1MessageNatebu)m;
				myPlug.incomming(m);
			}
		}
		myPlug.update();
	}

	public void reSurrect(){
		myPlug.reSurrect();
	}
	
	public void draw(PApplet canvas){
		myPlug.draw(canvas);       		
	}

	public void destroy(){
		myIncommingMessageQueue.clear();
		myOutgoingMessageQueue.clear();
		mySceneManager = null;
		myPlug.destroy();
	}
	
}
