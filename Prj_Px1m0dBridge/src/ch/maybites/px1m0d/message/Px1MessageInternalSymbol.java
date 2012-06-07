package ch.maybites.px1m0d.message;

import ch.maybites.px1m0d.plug.*;

/*
 * The Gesture Messages are only sent within the neighbourhood.
 */
public class Px1MessageInternalSymbol extends Message{	
	
	public static final int SYMBOL_TYPE_Undefined = 0;
	public static final int SYMBOL_TYPE_Message = 1;
	public static final int SYMBOL_TYPE_ButtonPressed = 2;
	public static final int SYMBOL_TYPE_ButtonReleased = 3;
	
	private int _mySymbolType = SYMBOL_TYPE_Undefined;
	
	private SymbolFunction _myFunction;
	
	public Px1MessageInternalSymbol(){
		super.myType = Message.TYPE_SYMBOL;
	}

	public void createSymbolFunctionMessage(SymbolFunction function){
		_myFunction = function;
		_mySymbolType = SYMBOL_TYPE_Message;
	}
	
	public void createButtonPressedMessage(){
		_mySymbolType = SYMBOL_TYPE_ButtonPressed;
	}

	public void createButtonReleasedMessage(){
		_mySymbolType = SYMBOL_TYPE_ButtonReleased;
	}

	public boolean isSymbolFunctionMessage(){
		return (_mySymbolType == SYMBOL_TYPE_Message)? true: false;
	}
	
	public boolean isButtonPresseMessage(){
		return (_mySymbolType == SYMBOL_TYPE_ButtonPressed)? true: false;
	}
	
	public boolean isButtonReleasedMessage(){
		return (_mySymbolType == SYMBOL_TYPE_ButtonReleased)? true: false;
	}
	
	public SymbolFunction getSymbolFunction(){
		return _myFunction;
	}
	
	public String printLine(){
		return " Int: ";
	}
	
	public String print(){
		String 	s  = ">Internal Gesture Message START<\n";
		s += " - Type is " + this.myType + "\n";
		s += " - Receiver is " + this.myReceiver + "\n";
		s += ">Internal Gesture Message END<\n";
		return s;
	}
	
}
