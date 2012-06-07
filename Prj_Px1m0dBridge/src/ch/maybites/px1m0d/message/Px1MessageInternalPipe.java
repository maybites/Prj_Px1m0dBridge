package ch.maybites.px1m0d.message;

/*
 */
public class Px1MessageInternalPipe extends Message{	
	private static final long serialVersionUID = 1L;
	private static final int PIPE_TYPE_Undefined = 0;
	private static final int PIPE_TYPE_RelativeMessage = 1;
	private static final int PIPE_TYPE_AbsoluteMessage = 2;

	private int _myPipeType = PIPE_TYPE_Undefined;
	
	private int myChannel = -1;
	private float myPipeRelValue;
	private float myPipeAbsValue;
	
	public Px1MessageInternalPipe(){
		super.myType = 	Message.TYPE_PIPE;
	}

	public void createRelativeMessage(int channel, int diff){
		_myPipeType = PIPE_TYPE_RelativeMessage;
		myChannel = channel;
		if(diff < -4)
			myPipeRelValue = -10f;
		else if(diff == -4)
			myPipeRelValue = -2f;
		else if(diff == -3)
			myPipeRelValue = -1f;
		else if(diff == -2)
			myPipeRelValue = -0.5f;
		else if(diff == 2)
			myPipeRelValue = 0.5f;
		else if(diff == 3)
			myPipeRelValue = 1f;
		else if(diff == 4)
			myPipeRelValue = 2f;
		else if(diff > 4)
			myPipeRelValue = 10f;
	}

	public void createRelativeMessage(int channel, float value){
		_myPipeType = PIPE_TYPE_RelativeMessage;
		myChannel = channel;
		myPipeRelValue = value;
	}

	public void createAbsoluteMessage(int channel, float value){
		_myPipeType = PIPE_TYPE_AbsoluteMessage;
		myChannel = channel;
		myPipeAbsValue = value;
	}

	public void setChannel(int channel){
		myChannel = channel;
	}
	
	public boolean isRelativeMessage(){
		return (_myPipeType == PIPE_TYPE_RelativeMessage)? true: false;
	}

	public boolean isAbsoluteMessage(){
		return (_myPipeType == PIPE_TYPE_AbsoluteMessage)? true: false;
	}
	
	public int getChannel(){
		return myChannel;
	}
	
	public void copyMyselfTo(Px1MessageInternalPipe message){
		message.myChannel = this.myChannel;
		message.myPipeAbsValue = this.myPipeAbsValue;
		message.myPipeRelValue = this.myPipeRelValue;
		message._myPipeType = this._myPipeType;
	}
	
	public Px1MessageInternalPipe clone(){
		Px1MessageInternalPipe clone = new Px1MessageInternalPipe();
		super.copyMyselfTo(clone);
		this.copyMyselfTo(clone);
		return clone;
	}
		
	public float getAbsoluteValue(){
		return myPipeAbsValue;
	}

	public float getRelativeValue(){
		return myPipeRelValue;
	}

}
