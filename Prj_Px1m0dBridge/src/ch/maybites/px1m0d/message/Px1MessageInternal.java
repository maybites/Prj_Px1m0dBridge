package ch.maybites.px1m0d.message;


public class Px1MessageInternal extends Message{	
	
	public Px1MessageInternal(int plugID){
		this.plugID_TO = plugID;
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
