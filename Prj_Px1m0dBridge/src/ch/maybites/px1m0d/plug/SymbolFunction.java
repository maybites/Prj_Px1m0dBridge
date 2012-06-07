package ch.maybites.px1m0d.plug;

import java.io.*;

public class SymbolFunction implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final int FUNCTION_click1_undefined 	= 9;
	public static final int FUNCTION_click1_1			= 11;
	public static final int FUNCTION_click1_2			= 12;
	public static final int FUNCTION_click1_3			= 13;
	public static final int FUNCTION_click1_4			= 14;
	public static final int FUNCTION_click1_5			= 15;
	public static final int FUNCTION_click1_6			= 16;
	public static final int FUNCTION_click1_7			= 17;
	public static final int FUNCTION_click1_8			= 18;
	
	public static final int FUNCTION_click2_undefined	= 19;
	public static final int FUNCTION_click2_1			= 21;
	public static final int FUNCTION_click2_2			= 22;
	public static final int FUNCTION_click2_3			= 23;
	public static final int FUNCTION_click2_4			= 24;
	public static final int FUNCTION_click2_5			= 25;
	public static final int FUNCTION_click2_6			= 26;
	public static final int FUNCTION_click2_7			= 27;
	public static final int FUNCTION_click2_8			= 28;

	public static final int FUNCTION_click3_undefined	= 29;
	public static final int FUNCTION_click3_1			= 31;
	public static final int FUNCTION_click3_2			= 32;
	public static final int FUNCTION_click3_3			= 33;
	public static final int FUNCTION_click3_4			= 34;
	public static final int FUNCTION_click3_5			= 35;
	public static final int FUNCTION_click3_6			= 36;
	public static final int FUNCTION_click3_7			= 37;
	public static final int FUNCTION_click3_8			= 38;

	private int mySocketID, myClicks, myLevel;
	private SymbolFunction myInnerFunction = null; 

	public SymbolFunction(int socketID, int clicks){
		mySocketID = socketID;
		myClicks = clicks;	
		myLevel = 0;
	}

	public void addFunctionGesture(SymbolFunction fg){
		myInnerFunction = fg;
		myInnerFunction.setLevel(myLevel);
	}
	
	/*
	 * checks if it is a pipe connection function for creation
	 */
	public boolean isCreatePipeConnectionFunction(){
		return (myClicks == 2)? true: false;
	}
	
	/*
	 * checks if it is a pipe connection function for destruction
	 */
	public boolean isDestroyPipeConnectionFunction(){
		return (myClicks == 3)? true: false;
	}
	
	/*
	 * checks if it is a pipe connection function for destruction
	 */
	public boolean isGlobalPipeListeningFunction(){
		return (myClicks == 1)? true: false;
	}
	
	/*
	 * since only the most top object is reachable
	 */
	public int getChannel(){
		return myClicks;
	}
	
	/*
	 * returns the function number
	 * return -1 if no function is defined
	 */
	public int getFunction(){
		if(myInnerFunction != null){
			int functionSocket = getSocketID(myLevel + 1);
			int functionClick = getClicks(myLevel + 1);
			int functionNumber = clockNumber(mySocketID, functionSocket);
			return functionClick * 10 + functionNumber;
		} else
			return -1;
	}

	/*
	 * returns the subfunction number
	 * return -1 if no subfunction is defined
	 */
	public int getSubFunction(){
		if(myInnerFunction != null){
			return myInnerFunction.getFunction();
		} else
			return -1;
	}


	private int clockNumber(int socketID_LowerLevel, int socketID_HigherLevel){
		/*  (rowDiff,colDiff)					= (rowDiff * 3 + colDiff)
		 * 	(1,1)	(1,0)	(1,-1)		= (3 + 1)	(3 + 0)		(3 - 1)	=	4	3	2
		 * 	(0,1)	(0,0)	(0,-1)		= (0 + 1)	(0 + 0)		(0 - 1)	=	1	0	-1
		 * 	(-1,1)	(-1,0)	(-1,-1)		= (-3 + 1)	(-3 + 0)	(-3 -1) =	-2	-3	-4
		 */
		int rowDiff = getRow(socketID_LowerLevel) - getRow(socketID_HigherLevel);
		int colDiff = getCol(socketID_LowerLevel) - getCol(socketID_HigherLevel);
		int preNumber = rowDiff * 3 + colDiff;

		switch(preNumber){
		case 0:
			return 0;
		case 2:
			return 1;
		case -1:
			return 2;
		case -4:
			return 3;
		case -3:
			return 4;
		case -2:
			return 5;
		case 1:
			return 6;
		case 4:
			return 7;
		case 3:
			return 8;
		}

		/* old mapping:
		switch(preNumber){
		case 0:
			return 0;
		case 3:
			return 1;
		case 2:
			return 2;
		case -1:
			return 3;
		case -4:
			return 4;
		case -3:
			return 5;
		case -2:
			return 6;
		case 1:
			return 7;
		case 4:
			return 8;
		}
		*/
		return -1;
	}
	
	private int getRow(int socketID){
		return socketID / 8;
	}
	
	private int getCol(int socketID){
		return socketID % 8;
	}
	
	/*
	 * this makes sure, that the levels depth are propagated through the tree
	 * first level is 0, then 1,2,3 etc.
	 */
	private void setLevel(int l){
		myLevel = l + 1;
		if(myInnerFunction != null)
			myInnerFunction.setLevel(myLevel);
	}


	/*
	 * gets the socketID of the specified level
	 * top most level is 0
	 */
	private int getSocketID(int level){
		if(myLevel == level)
			return mySocketID;
		else if(myInnerFunction != null)
			return myInnerFunction.getSocketID(level);
		else 
			return -1;
	}

	/*
	 * gets the clicks of the specified level
	 * top most level is 0
	 */
	private int getClicks(int level){
		if(myLevel == level)
			return myClicks;
		else if(myInnerFunction != null)
			return myInnerFunction.getClicks(level);
		else 
			return -1;
	}

	/*
	 * returns inner depth of functions. starts with 0 as the most inner level
	 */
	public int getDepth(){
		if(myInnerFunction != null)
			return (myInnerFunction.getDepth() + 1);
		else
			return 0;
	}

}
