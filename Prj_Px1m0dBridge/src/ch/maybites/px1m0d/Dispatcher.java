package ch.maybites.px1m0d;
import ch.maybites.px1m0d.connection.Connector;
import ch.maybites.px1m0d.message.*;
import ch.maybites.px1m0d.plug.drawing.Canvas;
import processing.core.*;
import gestalt.Gestalt;

import java.io.*;
import java.util.*;

public class Dispatcher{
	Connector connector;
	PlugFactory plugFactory;
	Thread myPostPoner = null;
	protected Vector<Message> posteRestante;

	SceneManager mySceneManager;

	Message postponeMessage;
	long postponeTimer;
	

	public Dispatcher(PlugFactory pf){
		mySceneManager = new SceneManager();
		mySceneManager.reset(this, pf);
		posteRestante = new Vector<Message>();
		plugFactory = pf;
	}

	public void loadScene(String path){
		try{
			FileInputStream underlyingStream = new FileInputStream(path);
			ObjectInputStream serializer = new ObjectInputStream(underlyingStream);
			try{
				Canvas.getInstance().getPlugin().bin(Gestalt.BIN_3D).clear();
				Canvas.getInstance().getPlugin().disable();
				mySceneManager.destroy(); // stops the managers thread
				SceneManager newSceneManager = (SceneManager)serializer.readObject();
				newSceneManager.reSurrect(this, plugFactory);
				mySceneManager = newSceneManager;
				Canvas.getInstance().getPlugin().enable();
			}catch(IOException e){
				Debugger.getInstance().errorMessage(this.getClass(),"Failed to load Scene: "+path);
				e.printStackTrace(System.out);
			}catch(ClassNotFoundException e){
				Debugger.getInstance().errorMessage(this.getClass(),"Failed to read Scene: "+path);
				e.printStackTrace(System.out);
			}				
			serializer.close();
			underlyingStream.close();
		}catch(IOException e){
			Debugger.getInstance().errorMessage(this.getClass(),"Failed to open file: "+path);
			e.printStackTrace(System.out);
		}catch(Exception e){
			Debugger.getInstance().errorMessage(this.getClass(),"Failed to open file: "+path);
			e.printStackTrace(System.out);
		}

	}

	public void saveScene(String path){
		try{
			FileOutputStream underlyingStream = new FileOutputStream(path);
			ObjectOutputStream serializer = new ObjectOutputStream(underlyingStream);
			try{
				serializer.writeObject(mySceneManager);
				serializer.flush();
			}catch(IOException e){
				Debugger.getInstance().errorMessage(this.getClass(),"Failed to write Scene: "+path);
				e.printStackTrace(System.out);
			}		
			serializer.close();
			underlyingStream.close();
		}catch(IOException e){
			Debugger.getInstance().errorMessage(this.getClass(),"Failed to save file: "+path);
			e.printStackTrace(System.out);
		}		
	}

	public void restartSimulation(){
		// creates a midi message that causes px1m0d to send its current state
		mySceneManager.restart();
		send(new Px1MessageRefresh());
	}
	
	public void refresh(){
		plugFactory.refresh();
		mySceneManager.refresh();
	}
	
	public void connect(Connector c){
		connector = c;
	}

	public void send(Message m){
		if(m.isRoutedOver(Message.ROUTE_POSTPONE)){
			postponeMessage(m);
		}
		if(m.isRoutedOver(Message.ROUTE_BRIDGE)){
			if(m.myReceiver == Message.RECEIVER_BRIDGE){
				if(m.myType == (Message.TYPE_PX1M0DRESETED)){
					Debugger.getInstance().infoMessage(this.getClass(),"SystemReset Received. Refresh Board...");
					Px1MessageRefresh msg = new Px1MessageRefresh();
					msg.postponeFor(2000);
					send(msg);
				}
			}
		}else if(m.isRoutedOver(Message.ROUTE_OUTSIDE)){
			if(m.myReceiver == (Message.RECEIVER_PX1M0D)){
				connector.sendToPxlm0d(m);
//				Debugger.getInstance().debugMessage(getClass(), " sent pxlm0d midi message to socket: " + m.getIntValue(Message.KEY_SocketID_FROM) + " at : " + System.currentTimeMillis() + " ms");
			} else if(m.myReceiver == (Message.RECEIVER_NATEBU)){
				connector.sendToPxlm0d(m);
			} else if(m.myReceiver == (Message.RECEIVER_WORLD)){
				connector.sendToWorld(m);
			}
		} else if(m.isRoutedOver(Message.ROUTE_SOCKETID)){
			mySceneManager.sendToSocket(m);
		} else if(m.isRoutedOver(Message.ROUTE_BOARD_ROW)){
			mySceneManager.sendToRow(m);
		} else if(m.isRoutedOver(Message.ROUTE_BOARD_COL)){
			mySceneManager.sendToColumn(m);
		} else if(m.isRoutedOver(Message.ROUTE_BOARD)){
			mySceneManager.sendToBoard(m);
		} else if(m.isRoutedOver(Message.ROUTE_BOARD_HOOD)){
			mySceneManager.sendToBoardHood(m);
		} else if(m.isRoutedOver(Message.ROUTE_PLUGID)){
			mySceneManager.sendToPlug(m);
		} else if(m.isRoutedOver(Message.ROUTE_PLUGUNIQUEID)){
			mySceneManager.sendToPlugUniqueID(m);
		}
	}

	public void draw(PApplet canvas){
		mySceneManager.draw(canvas);
	}

	private void postponeMessage(Message m){
		this.posteRestante.add(m);
		if(myPostPoner == null){
			try{
				Debugger.getInstance().debugMessage(this.getClass(),"PostPoner: start Thread");
				myPostPoner = new Thread(new PostPoner());
				myPostPoner.start();
				myPostPoner.setPriority(Thread.MIN_PRIORITY);
			} catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
	}
		
	protected class PostPoner implements Runnable{

		public void run(){
			Thread thisThread = Thread.currentThread();

			while(posteRestante.size() > 0){
				try {
					thisThread.sleep(5);
				} catch (InterruptedException e){;}

				for(int i = 0; i < posteRestante.size(); i++){
					if(((Message)posteRestante.get(i)).postponeHasEnded()){
						send((Message)posteRestante.get(i));
						posteRestante.remove(i);
					}
				}
			}
			Debugger.getInstance().debugMessage(getClass(), "PostPoner: end Thread");
			myPostPoner = null;
		}
	}


}
