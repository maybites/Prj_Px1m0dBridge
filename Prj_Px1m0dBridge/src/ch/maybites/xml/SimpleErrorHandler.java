package ch.maybites.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import ch.maybites.px1m0d.Debugger;

public class SimpleErrorHandler implements ErrorHandler {
       
    public void error(SAXParseException exception) {
    	Debugger.getInstance().errorMessage(getClass(), exception.getMessage());
    }
         
    public void fatalError(SAXParseException exception) {
    	Debugger.getInstance().fatalMessage(getClass(), exception.getMessage());
    }
         
    public void warning(SAXParseException exception) {
    	Debugger.getInstance().warningMessage(getClass(), exception.getMessage());
    }
}
