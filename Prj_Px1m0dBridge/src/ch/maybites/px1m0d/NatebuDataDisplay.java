package ch.maybites.px1m0d;

import netP5.*;
import controlP5.*;
import processing.core.*; 

public class NatebuDataDisplay extends PApplet{
	private static final long serialVersionUID = 1L;

	int[] values;

	Measurement measure;
	
	Diagram diagram;

	int measurementDistance = 30;

	ControlP5 controlP5;
	Textfield mySensorNumberField;

	public void setup() {
		size(600, 600, P2D);

		controlP5 = new ControlP5(this);
		controlP5.setAutoDraw(false);
		Button l = controlP5.addButton("load",10,20,20,80,20);
		Slider s = controlP5.addSlider("sensorNumber",0,63,0,20,50,500,10);
		controlP5.addSlider("valueA", 0, 1, 0.3f,20,70,500,10);
		controlP5.addSlider("valueB", 0, 60,30,20,90,500,10);
		controlP5.addSlider("valueC", 0, 60,0,20,110,500,10);
		controlP5.addSlider("valueD", 0, 100,0,20,130,500,10);
		controlP5.addSlider("valueF", 0, 500,0,20,150,500,10);
		mySensorNumberField = controlP5.addTextfield("sensorNo",120,20,20,20);

		measure = new Measurement(69/3, 100);
		measure.load("measure.store");
		
		diagram = new Diagram();
		diagram.setValues(measure.getRecord(0));

		values = new int[64];
	}

	public void draw() {
		background(128);
		controlP5.draw();
		diagram.draw(20, 200, 500, 200);
	}

	public void load(float theValue) {
		println("load data file");
		measure.load("measure.store");
	}

	public void sensorNumber(float theValue) {
		mySensorNumberField.setText("" + (int)theValue);
		diagram.setValues(measure.getRecord((int)theValue));
	}

	public void valueA(float theValue) {
		println("Change Value A: " + theValue);
		diagram.setA(theValue);
	}
	
	public void valueB(float theValue) {
		println("Change Value B: " + theValue);
		diagram.setB(theValue);
	}
	
	public void valueC(float theValue) {
		println("Change Value C: " + theValue);
		diagram.setC(theValue);
	}
	
	public void valueD(float theValue) {
		println("Change Value D: " + theValue);
		diagram.setD(theValue);
	}
	
	public void valueF(float theValue) {
		println("Change Value F: " + theValue);
		diagram.setF(theValue);
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

	
	public class Diagram{

		float[] myValues;
		float maxVal;
		
		float valA, valB, valC, valD, valF;
		
		Diagram(){
			valA = 0;
			valB = 1;
			valC = 1;
			valD = 0;
		}
		
		public void setValues(float[] values){
			myValues = values;
			maxVal = max(myValues);
		}
		
		public void draw(int x, int y, int w, int h){
			stroke(0);
			rect(x, y, w, h);
			int step = w / (myValues.length - 1);
			for(int i = 0; i < (myValues.length - 1); i++){
				stroke(0, i * 20, 255);
				line(x + (step * i), (y + h) - map(myValues[i], 0, 255, 0, h), x + (step * (i+1)), (y + h) - map(myValues[(i+1)], 0, 255, 0, h));				
				stroke(255, i * 20, 0);
				line(x + (step * i), (y + h) - map(myValues[i], 0, 255, 0, h), x + calcX(map(myValues[i], 0, 255, 0, h)), y + h);				
			}
			for(int i = 0; i < (myValues.length - 1); i++){
				stroke(i * 20, 255, 0);
				line(x + (step * i), (y + h) - calcY(step * i), x + (step * (i+1)), (y + h) - calcY(step * (i + 1)));				
				stroke(255, i * 20, 255);
				line(x + (step * i), (y + h) - calcY(step * i), x + calcX(calcY(step * i)), y + h);				
			}
		}
		
		
		// funktion der kurve: y= a ^( x / b - c / d) * d + f
		// berechnung von x : x = (b * c * log(a) + b * d * log(-(f-x)/d))/(d * log(a))
		
		private float calcX(float y){
			return (valB * valC * log(valA) + valB * valD * log (- (valF - y) / valD))/(valD * log(valA));
		}
		
		private float calcY(float x){
			return pow(valA,((x / valB) - (valC / valD))) * valD + valF;
		}
		
		public void setA(float a){
			valA = a;
		}

		public void setB(float b){
			valB = b;
		}
		
		public void setC(float c){
			valC = c;
		}
		
		public void setD(float d){
			valD = d;
		}

		public void setF(float f){
			valF = f;
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

		public float[] getRecord(int index){
			float[] rec = new float[records.length];
			for (int i = 0; i < rec.length; i++) {
				rec[i] += records[i][index];
			}
			return rec;
		}
		
	}

	static public void main(String args[]) {     
		PApplet.main(new String[] { "ch.maybites.px1m0d.NatebuDataDisplay" });  
	}

}
