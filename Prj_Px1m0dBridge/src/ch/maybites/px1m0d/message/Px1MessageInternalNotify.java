package ch.maybites.px1m0d.message;

import ch.maybites.px1m0d.plug.Plug;


/*
 * This Message is used to notify all the plugs on the board on certain 
 * important events, defined by the final NOTIFY_TYPE_ list
 */
public class Px1MessageInternalNotify extends Message{	
	public static final int NOTIFY_TYPE_Undefined = 0;	
	public static final int NOTIFY_TYPE_Plugged = 1;
	public static final int NOTIFY_TYPE_UnPlugged = 2;
	public static final int NOTIFY_TYPE_Pressed = 3;
	public static final int NOTIFY_TYPE_StartListeningForFunction = 4;
	public static final int NOTIFY_TYPE_StopListeningForFunction = 5;
	public static final int NOTIFY_TYPE_StartListeningForGroup = 6;
	public static final int NOTIFY_TYPE_StopListeningForGroup = 7;
	public static final int NOTIFY_TYPE_UnPluggNoticeForLinkedPlug = 8;
	public static final int NOTIFY_TYPE_PluggNoticeForLinkedPlug = 9;
	
	private int _myNotifyType = NOTIFY_TYPE_Undefined;

	public Px1MessageInternalNotify(){
		super.myType = Message.TYPE_NOTIFY;
	}

	public void createUnPluggNoticeForLinkedPlug(){
		_myNotifyType = NOTIFY_TYPE_UnPluggNoticeForLinkedPlug;
	}
	
	public boolean isUnPluggNoticeForLinkedPlug(){
		return(_myNotifyType == NOTIFY_TYPE_UnPluggNoticeForLinkedPlug)? true:false;
	}	

	public void createPluggNoticeForLinkedPlug(){
		_myNotifyType = NOTIFY_TYPE_PluggNoticeForLinkedPlug;
	}
	
	public boolean isPluggNoticeForLinkedPlug(){
		return(_myNotifyType == NOTIFY_TYPE_PluggNoticeForLinkedPlug)? true:false;
	}	

	public void createNotifyOfType(int notifyType){
		_myNotifyType = notifyType;
	}
	
	public boolean isNotifyType(int notifyType){
		return (_myNotifyType == notifyType)? true: false;
	}
		
	public String printLine(){
		return " Int: ";
	}
	
	public String print(){
		String 	s  = ">Internal Message START<\n";
		s += " - Type is " + this.myType + "\n";
		s += " - Receiver is " + this.myReceiver + "\n";
		s += ">Internal Message END<\n";
		return s;
	}
	
}
