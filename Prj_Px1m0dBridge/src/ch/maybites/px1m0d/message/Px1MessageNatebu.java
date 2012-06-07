package ch.maybites.px1m0d.message;

import ch.maybites.px1m0d.Debugger;

public class Px1MessageNatebu extends Message{
	private static final long serialVersionUID = 1L;

	private int[] _myArray;
	
	private int[] _myBottomSwitches;
	private int[] _myRightSwitches;
		
	public int leftSelector;
	public int rightSelector;
	public int leftPoti;
	public int rightPoti;
	public boolean centralButton;
	public boolean plugAInput1;
	public boolean plugAInput2;
	public boolean plugBInput1;
	public boolean plugBInput2;
	
	public float xPosWeight;
	public float yPosWeight;
	public float weight;
	
	private boolean _myUIDataHasChanged;

	public boolean bottomSwitchHasChanged;
	public boolean rightSwitchHasChanged;
	public boolean rightSelectorHasChanged;
	public boolean leftSelectorHasChanged;
	public boolean rightPotiHasChanged;
	public boolean leftPotiHasChanged;
	public boolean AInputHasChanged;
	public boolean BInputHasChanged;
	public boolean centralButtonHasChanged;

	public Px1MessageNatebu(){
		super.myRoute = Message.ROUTE_BOARD;
		super.myType = Message.TYPE_NATEBU;
		super.myReceiver = Message.RECEIVER_PLUG;
		super.socketID_FROM = -1; // makes sure the message is sent to the whole board
		int[] values = new int[99];
		for(int i = 0; i < values.length; i++){
			values[i] = 0;
		}
		setData(values);
	}

	public Px1MessageNatebu(int[] values){
		super.myRoute = Message.ROUTE_BOARD;
		super.myType = Message.TYPE_NATEBU;
		super.myReceiver = Message.RECEIVER_PLUG;
		super.socketID_FROM = -1; // makes sure the message is sent to the whole board
		setData(values);
		
		xPosWeight = getXCenter();
		yPosWeight = getYCenter();
		weight = weight();
	}

	private float weight(){
		float min = 0;
		float max = 255;
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				min = (min > _myArray[i + 8 * j])? _myArray[i + 8 * j]: min;
				max = (max < _myArray[i + 8 * j])? _myArray[i + 8 * j]: max;
			}
		}
		return (max - min);
	}

	private float getXCenter(){
		float sum2 = 0;
		float sum1 = 0;
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				sum1 += _myArray[i + 8 * j] * (j + 1);
				sum2 += _myArray[i + 8 * j];
			}
		}
		return (sum1 / sum2 - 1 ) * 255 / 8;
	}

	private float getYCenter(){
		float sum2 = 0;
		float sum1 = 0;
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				sum1 += _myArray[i + 8 * j] * (i + 1);
				sum2 += _myArray[i + 8 * j];
			}
		}
		return (sum1 / sum2 - 1 ) * 255 / 8;
	}

	public int getArrayValue(int socketID){
		return _myArray[socketID];
	}
	
	public int getBottomSwitch(int switchID){
		if(switchID < _myBottomSwitches.length)
			return _myBottomSwitches[switchID];
		return 0;
	}

	public int getRightSwitch(int switchID){
		if(switchID < _myRightSwitches.length)
			return _myRightSwitches[switchID];
		return 0;
	}

	public boolean uiDataHasChanged(){
		return _myUIDataHasChanged;
	}
	
	private void setData(int[] values){
		_myArray = new int[64];
		for(int i = 0; i < 64; i++){
			_myArray[i] = values[i];
		}
		_myBottomSwitches = new int[8];
		for (int i = 64 ; i < 72 ; i++) {
			_myBottomSwitches[i - 64] = values[i];
		}
		_myRightSwitches = new int[8];
		for (int i = 72 ; i < 80 ; i++) {
			_myRightSwitches[i - 72] = values[i];
		}
		leftSelector = values[80];
		rightSelector = values[81];
		leftPoti = values[82];
		rightPoti = values[83];
		centralButton = (values[84] == 1)?true:false;
		plugAInput1 = (values[85] == 1)?true:false;
		plugAInput2 = (values[86] == 1)?true:false;
		plugBInput1 = (values[87] == 1)?true:false;
		plugBInput2 = (values[88] == 1)?true:false;
		
		_myUIDataHasChanged = (values[89] == 0)? false: true;
		
		rightSwitchHasChanged = (values[90] == 0)? false: true;
		bottomSwitchHasChanged = (values[91] == 0)? false: true;
		rightSelectorHasChanged = (values[92] == 0)? false: true;
		leftSelectorHasChanged = (values[93] == 0)? false: true;
		rightPotiHasChanged = (values[94] == 0)? false: true;
		leftPotiHasChanged = (values[95] == 0)? false: true;
		AInputHasChanged = (values[96] == 0)? false: true;
		BInputHasChanged = (values[97] == 0)? false: true;
		centralButtonHasChanged = (values[98] == 0)? false: true;
	}

	public String printLine(){
		return "Natebu Message";
	}

}
