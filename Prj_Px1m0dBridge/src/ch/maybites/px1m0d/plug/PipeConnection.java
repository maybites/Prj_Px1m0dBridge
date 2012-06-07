package ch.maybites.px1m0d.plug;

import java.io.*;

public class PipeConnection implements Serializable{
	private static final long serialVersionUID = 1L;

	private int _myChannel;
	private SymbolFunction _mySymbolFunction;
	private int _mySocketID_TO;
	private long _myUniqueID_TO, _myUniqueID_FROM;
	
	public PipeConnection(SymbolFunction sf, int socketID_TO, long uniqueID_TO, long uniqueID_FROM){
		_mySymbolFunction = sf;
		_mySocketID_TO = socketID_TO;
		_myUniqueID_TO = uniqueID_TO;
		_myUniqueID_FROM = uniqueID_FROM;
		_myChannel = (_mySymbolFunction.getDepth() == 0)? 0: _mySymbolFunction.getFunction() % 10;
	}

	public PipeConnection(int channel, long uniqueID_FROM, int socketID_TO, long uniqueID_TO){
		_mySymbolFunction = null;
		_mySocketID_TO = socketID_TO;
		_myUniqueID_TO = uniqueID_TO;
		_myUniqueID_FROM = uniqueID_FROM;
		_myChannel = channel;
	}

	public boolean hasSymbol(){
		return (_mySymbolFunction != null)? true: false;
	}
	
	public int getChannel(){
		return _myChannel;
	}

	public long getUniqueID_TO(){
		return _myUniqueID_TO;
	}

	public long getUniqueID_FROM(){
		return _myUniqueID_FROM;
	}

	public int getSocketID_TO(){
		return _mySocketID_TO;
	}
	
	public void rewireSocketID(int socketID){
		_mySocketID_TO = socketID;
	}
	
	public boolean equals(Object pc){
		if(pc instanceof  PipeConnection)
			if(getUniqueID_TO() == ((PipeConnection)pc).getUniqueID_TO() && 
					getChannel() == ((PipeConnection)pc).getChannel() &&
					getUniqueID_FROM() == ((PipeConnection)pc).getUniqueID_FROM())
				return true;
		return false;
	}
	
}
