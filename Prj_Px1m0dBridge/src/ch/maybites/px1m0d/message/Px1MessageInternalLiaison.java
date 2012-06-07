package ch.maybites.px1m0d.message;


/*
 * This Message is used to notify all the plugs on the board on certain 
 * important events, defined by the final NOTIFY_TYPE_ list
 */
public class Px1MessageInternalLiaison extends Message{	
	public static final int LIAISON_TYPE_Undefined = 0;	
	public static final int LIAISON_TYPE_RequestForSymbol = 1;
	public static final int LIAISON_TYPE_AcknolegedForSymbol = 2;
	public static final int LIAISON_TYPE_ClosedForSymbol = 3;
	public static final int LIAISON_TYPE_RequestForFunctionByPlug = 4;
	public static final int LIAISON_TYPE_RequestForFunctionByButton = 5;
	public static final int LIAISON_TYPE_AcknolegedForFunction = 6;
	public static final int LIAISON_TYPE_ClosedForFunction = 7;
	public static final int LIAISON_TYPE_RequestForLink = 8;
	public static final int LIAISON_TYPE_AcknolegedForLink = 9;
	public static final int LIAISON_TYPE_ClosedForLink = 10;
	public static final int LIAISON_TYPE_RequestForPipe = 11;
	public static final int LIAISON_TYPE_AcknolegedForPipe = 12;
	public static final int LIAISON_TYPE_ClosedForPipe = 13;
	public static final int LIAISON_TYPE_PingForPipeRequest = 14;
	public static final int LIAISON_TYPE_PingForPipeResponse = 15;
	
	private int _myLiaisonType = LIAISON_TYPE_Undefined;

	public Px1MessageInternalLiaison(){
		super.myType = Message.TYPE_LIAISON;
	}

	public void createLiaison(int liaisonType){
		_myLiaisonType = liaisonType;
	}
	
	public boolean isLiaisonType(int liaisonType){
		return (_myLiaisonType == liaisonType)? true: false;
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
