package ch.maybites.px1m0d.tools;
import javax.script.*;

import ch.maybites.px1m0d.Debugger;

import java.util.*;
//import com.apple.cocoa.application.NSApplication;
//import com.apple.cocoa.foundation.*;


public class AppleScriptConnector {
	ScriptEngineManager mgr;
	ScriptEngine engine;

	public AppleScriptConnector(){
		mgr = new ScriptEngineManager();
		engine = mgr.getEngineByName("AppleScript");
	}
	
	private void simpleExecute(String script){
	    try{
//			Debugger.getInstance().debugMessage(this.getClass(), "execute script: " + script);
	    	engine.eval(script);
	    } catch (ScriptException e){
			Debugger.getInstance().errorMessage(this.getClass(), "Exception: " + e.getMessage());
	    }
	}

	public void runSimpleScript(){
		String script = "say \"Hello from Java\"";
		simpleExecute(script);
	}

	public void speakVoice(String text){
		String script = "say \""+text+"\"";
		simpleExecute(script);
	}
	
	public void openXMLFile(String applicationPath, String path){
		/*	tell application "Finder"
				activate
				open document file "Seq4.xml" of folder "Sample" of folder "ConVert" of folder "Live" of folder "types" of folder "plug" of folder "data" of folder "Px1m0dBridge" of folder "eclipse" of folder "code" of folder "Arbeiten" of folder "maf" of folder "Users" of startup disk using application file "editix-free-2009sp1.app" of folder "Applications" of startup disk
			end tell
		*/
		String script = 
			"tell application \"Finder\" \n" +
				"activate \n";
		StringTokenizer tokens = new StringTokenizer(path, "/");
		String newPath = " of startup disk";
		while(tokens.hasMoreTokens()){
			String token = tokens.nextToken();
			if(tokens.hasMoreTokens()){
				newPath = " of folder \""+token+"\"" + newPath;
			} else {
				newPath = "open document file \""+token+"\"" + newPath;
			}
		}
		tokens = new StringTokenizer(applicationPath, "/");
		String newApplicPath= " of startup disk";
		while(tokens.hasMoreTokens()){
			String token = tokens.nextToken();
			if(tokens.hasMoreTokens()){
				newApplicPath = " of folder \""+token+"\"" + newApplicPath;
			} else {
				newApplicPath = "using application file \""+token+"\"" + newApplicPath;
			}
		}
		String applic = " using application file \"" + applicationPath + "\" of folder \"Applications\" of startup disk";
		script = script + newPath + " " + newApplicPath + " \n" + 
			"end tell";

		simpleExecute(script);
	}
	
}
