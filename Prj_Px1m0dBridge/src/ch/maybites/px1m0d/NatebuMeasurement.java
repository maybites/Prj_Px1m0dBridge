package ch.maybites.px1m0d;

import processing.serial.*;
import oscP5.*;
import netP5.*;
import controlP5.*;
import processing.core.*; 

public class NatebuMeasurement extends PApplet{
	private static final long serialVersionUID = 1L;

	Balken[] Balken;
	int[] values;
	byte[] bvalues;

	DataTransmitter store;

	Measurement measure;

	int measurementDistance = 30;

	// serail pars variables
	int lf = 10;    // linefeed in ASCII TODO '!' dosent work reliable, why?
	String myString = null;
	Serial myPort;  // The serial port
	int VarN[] = {
	};

	ControlP5 controlP5;
	ControlWindow controlWindow;

	public void setup() {
		size(600, 600, P3D);

		controlP5 = new ControlP5(this);
		controlP5.setAutoDraw(false);
		controlWindow = controlP5.addControlWindow("controlP5window",100,100,400,400);
		controlWindow.setBackground(color(40));
		Button b = controlP5.addButton("measureNow",10,160,160,80,20);
		b.moveTo(controlWindow);
		Button l = controlP5.addButton("load",10,160,120,80,20);
		l.moveTo(controlWindow);
		Button s = controlP5.addButton("save",10,160,200,80,20);
		s.moveTo(controlWindow);
		RadioButton r = controlP5.addRadio("distance",100,20);
		r.deactivateAll(); // use deactiveAll to not make the first radio button active.
		r.addItem("69",690);
		r.addItem("66",660);
		r.addItem("63",630);
		r.addItem("60",600);
		r.addItem("57",570);
		r.addItem("54",540);
		r.addItem("51",510);
		r.addItem("48",480);
		r.addItem("45",450);
		r.addItem("42",420);
		r.addItem("39",390);
		r.addItem("36",360);
		r.addItem("33",330);
		r.addItem("30",300);
		r.addItem("27",270);
		r.addItem("24",240);
		r.addItem("21",210);
		r.addItem("18",180);
		r.addItem("15",150);
		r.addItem("12",120);
		r.addItem("9",90);
		r.addItem("6",60);
		r.addItem("3",30);
		r.moveTo(controlWindow);


		measure = new Measurement(69/3, 100);

		noStroke();
		println(Serial.list());

		store = new DataTransmitter(new OscP5(this,12001), new NetAddress("127.0.0.1",12000));

		myPort = new Serial(this, Serial.list()[0], 115200);
		myPort.clear();
		myString = myPort.readStringUntil(lf);
		myString = null;

		values = new int[64];
		bvalues = new byte[values.length];
		Balken = new Balken[64];
		for( int i=0; i<Balken.length; i++) {
			Balken[i] = new Balken((i%8)*50+50, (i/8)*50+50, i);
		}

		camera(300f, 300f, 400.0f, // eyeX, eyeY, eyeZ
				0.0f, 0.0f, 0.0f, // centerX, centerY, centerZ
				1.0f, 1.0f, 0.0f); // upX, upY, upZ

	}

	public void draw() {
		lights();
		background(0); 

		translate(230, 230, 0);
		for( int i=0; i<Balken.length; i++) {
			Balken[i].render();
		}
		translate(-230, -230, 0);

		translate(0, 0, -10);
		fill(204);
		box(410, 410, 10);
	}

	public void load(float theValue) {
		println("load data file");
		measure.load("measure.store");
	}

	public void save(float theValue) {
		println("save data file");
		measure.save("measure.store");
	}

	public void measureNow(float theValue) {
		println("start measuring distance: "+ measurementDistance);
		measure.start(measurementDistance);
	}

	public void distance(float theValue) {
		println("new distance is set: "+theValue);
		measurementDistance = (int)theValue;
	}

	public int getR(int val) {
		if (val >= 0 && val <= 128) {
			val = 255 - val*2;
		} 
		else {
			val = 0;
		}
		val = constrain(val, 0, 255);
		return val;
	} 

	public int getG(int val) {
		if (val >= 128 && val <= 255) {
			val = 255 - ((val-128)*2);
		} 
		else {
			val = val*2;
		}
		val = constrain(val, 0, 255);
		return val;
	}

	public int getB(int val) {
		if (val >= 128 && val <= 255) {
			val = val*2 - 255;
		} 
		else {
			val = 0;
		}
		val = constrain(val, 0, 255);
		return val;
	} 

	public void serialEvent(Serial sp) { 
		//		if (myPort.available() > 0) {
		//		  println("Serial Data: ");
		//		} 
		char inByte = sp.readChar();
		int intByte = inByte;
		intByte -= 129;
		if(intByte < 0)
			intByte += 256;
		//print(" - " + intByte);
		int[] vals = store.addData(intByte);
		if(vals != null){
			measure.nextDataSet(vals);
			values = vals;
		}
	}


	public class Measurement{
		String[] fileRecords;
		float[][] records;
		float[][] current;
		int distance;
		int numberOfMeasurements;
		int countMeasurements;

		Measurement(int numberOfRecords, int nuOfMeasurements){
			records = new float[numberOfRecords][64];
			numberOfMeasurements = nuOfMeasurements;
		}

		public void start(int distnce){
			distance = distnce;
			current = new float[64][numberOfMeasurements];
			countMeasurements = 0;
		}

		public boolean nextDataSet(int[] vals){
			if(current != null && vals.length == current.length){
				if(countMeasurements < numberOfMeasurements){
					print("Measuring set:");
					for(int i = 0; i < vals.length; i++){
						current[i][countMeasurements] = vals[i];
						print(" " + vals[i]);
					}
					println(" : end");
					countMeasurements++;
				} else if (current != null){
					print("Measurement Average:");
					for(int i = 0; i < current.length; i++){
						for(int t = 0; t < numberOfMeasurements; t++){
							records[(distance / 30) - 1][i] += current[i][t];
						}
						records[(distance / 30) - 1][i] /= numberOfMeasurements;
						print(" " + records[(distance / 30) - 1][i]);
					}
					println(" : end");
					current = null;
					return true;
				} 
			}
			return false;
		}

		public void load(String filename){
			fileRecords = loadStrings(filename);
			for(int i = 0; i < fileRecords.length; i++) {
				String[] pieces = split(fileRecords[i], '\t');
				if(pieces.length == 65){
					int position = ((new Float(pieces[0])).intValue() / 30) - 1;
					for(int t = 1; t < 65; t++){
						records[position][t-1] = (new Float(pieces[t])).floatValue();
					}
				}
			}
		}

		public void save(String filename){
			fileRecords = new String[records.length];
			for (int i = 0; i < records.length; i++) {
				fileRecords[i] = "" + (i + 1) * 30;
				for(int t = 0; t < 64; t++){
					fileRecords[i] += "\t" + records[i][t];
				}
			}
			saveStrings(filename, fileRecords);			
		}

	}


	//***********************************************
	//***********************************************

	public class Balken{
		// Datenfelder
		int xPos;
		int yPos;
		int nummer;
		int value;

		// Konstruktor
		Balken ( int x, int y, int num) {
			xPos = x;
			yPos = y;
			nummer = num;
		}

		// Methoden
		void render() {
			value = values[nummer];
			value = constrain(value, 0, 255);
			fill(getB(value), getG(value), getR(value));
			translate(-xPos, -yPos, value/4);
			box(40, 40, value/2);
			translate(xPos, yPos, -value/4);
		}

	}



	//***********************************************
	//***********************************************



	public class DataTransmitter{
		int[] bigStorage;
		int storagePosition;
		NetAddress myRemoteLocation;
		OscP5 oscP5;

		int[] PATTERN = {
				255, 127, 63, 31, 15, 7, 3, 1, 0};

		DataTransmitter(OscP5 osc, NetAddress remoteLoc){
			myRemoteLocation = remoteLoc;
			oscP5 = osc;
			bigStorage = new int[256];
			storagePosition = 0;
		}

		void oscSender(int[] pack) {
			/* in the following different ways of creating osc messages are shown by example */
			OscMessage myMessage = new OscMessage("/natebu");

			for(int i = 0; i < pack.length; i++){
				myMessage.add(pack[i]); /* add the values array to the osc message */
			}

			/* send the message */
			oscP5.send(myMessage, myRemoteLocation); 
			//println("current millis: " + millis());
		}

		int[] addData(int newbees){
			if(storagePosition < bigStorage.length){
				bigStorage[storagePosition] = newbees;
				storagePosition++;
			} 
			else {
				storagePosition = 0;
				bigStorage[storagePosition] = newbees;
			}
			if(findEndOfPackPattern()){
				int[] pack = getDataPackage();
				if(pack.length == 90){
					//oscSender(pack);
					int[] vals = new int[64];
					for(int i = 0; i < vals.length; i++){
						vals[i] = pack[i];
					}
					return vals;
				} 
				else {
					println("Dropped Serial Data Pack");
				}
			}
			return null;
		}


		int[] getDataPackage(){
			int[] newPackage = new int[storagePosition - PATTERN.length];
			for(int i = 0; i < storagePosition - PATTERN.length; i++){
				newPackage[i] = bigStorage[i];
			}
			storagePosition = 0;
			return newPackage;
		}


		boolean findEndOfPackPattern(){
			if((storagePosition - PATTERN.length) >= 0){
				for(int i = 0; i < PATTERN.length; i++){
					if(bigStorage[storagePosition - PATTERN.length + i] != PATTERN[i]){
						return false;
					}
				}
				return true;
			}
			return false;
		}


	}

	static public void main(String args[]) {     
		PApplet.main(new String[] { "ch.maybites.px1m0d.NatebuMeasurement" });  
	}

}
