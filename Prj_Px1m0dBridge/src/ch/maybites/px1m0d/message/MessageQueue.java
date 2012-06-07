package ch.maybites.px1m0d.message;
import java.util.Vector;
import java.io.*;

/*
 *	Used to Store the MessageQueue 
 *
 * @author      maf
 * @version     1.0
 */

public class MessageQueue implements Serializable{
	Vector myStorage;

	public MessageQueue(){
		myStorage = new Vector(10);
	}

	/*
	 * constructor
	 * 
	 * @param	size	size of the initial vector (internal storage object)
	 */
	public MessageQueue(int size){
		myStorage = new Vector(size);
	}

	/*
	 * add a message to the queue
	 * 
	 * @param	msg	Message to be stored
	 */
	synchronized public void addMessage(Message msg){
		myStorage.add(msg);
	}

	/*
	 * method to see if any messages are left in the queue
	 * 
	 * @return	boolean	true if there are more messages
	 */
	public boolean hasMoreMessages(){
		try{
			myStorage.firstElement();
			return true;
		} catch(Exception e){
			;
		}
		return false;
	}

	/*
	 * metod to get next Message
	 * 
	 * before calling this method, make sure that hasMoreMessages() return true,
	 *  otherwise you run into troubles...
	 *  
	 *  @return	Message	Message Object
	 */
	public Message getNextMessage(){
		Message msg = (Message)myStorage.firstElement();
		myStorage.remove(msg);
		return msg;
	}

	/*
	 * method to get queue size
	 * 
	 * @return	int	queue size
	 */
	public int getQueueSize(){
		return myStorage.size();
	}
	
	/*
	 * method to empty the queue
	 * 
	 */
	public void clear(){
		myStorage.clear();
	}
}
