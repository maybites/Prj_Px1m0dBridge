package ch.maybites.px1m0d;

public class GlobalPreferences {

	// methods and attributes for Singleton pattern
	private GlobalPreferences() {}

	static private GlobalPreferences _instance;

	static public GlobalPreferences getInstance() {
		if (_instance == null) {
			synchronized(GlobalPreferences.class) {
				if (_instance == null)
					_instance = new GlobalPreferences();
			}
		}
		return _instance;
	}

	private String _latency = "10.0";
	
	private String _dataPath = "";
	
	public void setDataPath(String path){
		_dataPath = path;
	}
	
	/*
	 * returns the absolute path. the specified addPath, which is relative to the 
	 * data folder, is being added to the absolute path of the data folder
	 */
	public String getAbsolutePath(String addPath){
		return _dataPath + addPath;
	}
	
	public void setLatency(String millis){	
		_latency = millis;
	}

	public void setLatency(float millis){	
		_latency = "" + millis;
	}
	
	public float getLatencyFloat(){
		return (new Float(_latency)).floatValue();
	}
	
	public String getLatencyString(){
		return _latency;
	}
}
