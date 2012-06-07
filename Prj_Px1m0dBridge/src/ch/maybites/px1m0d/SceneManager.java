package ch.maybites.px1m0d;
import ch.maybites.px1m0d.message.*;
import ch.maybites.px1m0d.plug.Socket;
import processing.core.*;
import java.io.*;

/*
 * All about Serialization: http://oreilly.com/catalog/javarmi/chapter/ch10.html
 */
public class SceneManager implements Serializable{
	private static final long serialVersionUID = 1L;
	public transient PlugFactory plugFactory;
	private transient Dispatcher myDispatcher;
	protected transient Thread manager = null;

	private transient Socket[][] sockets;
	
	private transient boolean isBeingSerialized = false;
	protected transient boolean managerIsRunning = false;
	
	protected int sceneLatency = 10;
		
	public SceneManager(){
		;
	}
		
	public void reset(Dispatcher d, PlugFactory pf){
		plugFactory = pf;
		myDispatcher = d;
		sockets = new Socket[8][8];

		for(int i=0; i < 8; i++){
			for(int j=0; j < 8; j++){
				sockets[i][j] = new Socket(this, i, j);
			}
		}	
		startManager();
	}

	/*
	 * This function has to be called after the new scene has been loaded.
	 */
	public void reSurrect(Dispatcher d, PlugFactory pf){
		plugFactory = pf;
		myDispatcher = d;
		for(int j = 0; j < 8; j++){
			for(int i = 0; i < 8; i++){
				sockets[i][j].reSurrect();
			}
		}
		startManager();
	}

	public void send(Message m){
		// During serializing of this object, all the messages will be disregarded
		if(!isBeingSerialized){
			myDispatcher.send(m);
		}
	}

	public void sendToSocket(Message m){
		// During serializing of this object, all the messages will be disregarded
		if(!isBeingSerialized){
			/**
			Debugger.getInstance().verboseMessage(this.getClass(), "Message Type sent: " + m.myType);
			for(int i = 0; i < m.getKeys().size(); i++){
				debug.debugMessage(getClass(), " has key: " + (String)m.getKeys().toArray()[i]);
			}
			**/
			int socketID = m.socketID_TO;
			//debug.debugMessage(getClass(), " socketID: " + socketID);
			sockets[column(socketID)][row(socketID)].incomming(m);
		}
	}

	public void sendToRow(Message m){
		// During serializing of this object, all the messages will be disregarded
		if(!isBeingSerialized){
			int socketID = m.socketID_TO;
			for(int i = 0; i < 8; i++){
				sockets[column(socketID)][i].incomming(m);
			}
		}
	}

	public void sendToColumn(Message m){
		// During serializing of this object, all the messages will be disregarded
		if(!isBeingSerialized){
			int socketID = m.socketID_TO;
			for(int i = 0; i < 8; i++){
				sockets[i][row(socketID)].incomming(m);
			}
		}
	}

	public void sendToBoard(Message m){
		// During serializing of this object, all the messages will be disregarded
		if(!isBeingSerialized){
			int socketID_FROM = m.socketID_FROM;
			for(int j = 0; j < 8; j++){
				for(int i = 0; i < 8; i++){
					if(socketID_FROM != (i + j * 8))
						sockets[i][j].incomming(m);
				}
			}
		}
	}

	public void sendToBoardHood(Message m){
		// During serializing of this object, all the messages will be disregarded
		if(!isBeingSerialized){
			int socketID = m.socketID_TO;
			int row = row(socketID);
			int col = column(socketID);
			for(int j = (8 + row -1); j < (8 + row + 2); j++){
				for(int i = (8 + col -1); i < (8 + col + 2); i++){
					if(!((j % 8) == row && (i % 8) == col)){
						sockets[i % 8][j % 8].incomming(m);
					}
				}
			}
		}
	}

	public void sendToPlug(Message m){
		// During serializing of this object, all the messages will be disregarded
		if(!isBeingSerialized){
			int plugID = m.plugID_TO;
			for(int j = 0; j < 8; j++){
				for(int i = 0; i < 8; i++){
					if(sockets[i][j].myPlug.parameter.getPlugID() == plugID){
						sockets[i][j].incomming(m);
					}
				}
			}
		}
	}

	public void sendToPlugUniqueID(Message m){
		// During serializing of this object, all the messages will be disregarded
		if(!isBeingSerialized){
			long plugUniqueID = m.plugUniqueID_TO;
//			debug.debugMessage(getClass(), " sendToPlugUniqueID() plugUniqueID: " + plugUniqueID);
			for(int j = 0; j < 8; j++){
				for(int i = 0; i < 8; i++){
					if(sockets[i][j].myPlug.myUniqueID == plugUniqueID){
//						debug.debugMessage(getClass(), " ...sent: ");
						sockets[i][j].incomming(m);
					}
				}
			}
		}
	}

	private int row(int socketID){
		return socketID / 8;
	}

	private int column(int socketID){
		return socketID % 8;
	}

	protected void synchronize(){
		for(int i=0; i < 8; i++){
			for(int j=0; j < 8; j++){
				sockets[i][j].synchronize();
			}
		}		
	}

	protected void update(){
		for(int i=0; i < 8; i++){
			for(int j=0; j < 8; j++){
				sockets[i][j].update();
			}
		}		
	}

	public void draw(PApplet canvas){
		// During serializing of this object, all the messages will be disregarded
		if(!isBeingSerialized){
			for(int i=0; i < 8; i++){
				for(int j=0; j < 8; j++){
					sockets[i][j].draw(canvas);
				}
			}		
		}
	}

	private synchronized void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
		isBeingSerialized = true;
		stopManager();
		stream.defaultWriteObject(); 
		for(int i=0; i < 8; i++){
			for(int j=0; j < 8; j++){
				stream.writeObject(sockets[i][j]); 
			}
		}	
		startManager();
		isBeingSerialized = false;
	}

	private  void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
		stream.defaultReadObject(); 
		sockets = new Socket[8][8];
		for(int i=0; i < 8; i++){
			for(int j=0; j < 8; j++){
				sockets[i][j] = (Socket)stream.readObject(); 
			}
		}	
	}
	
	private void startManager(){
		if(manager == null){
			try{
				sceneLatency = (int)GlobalPreferences.getInstance().getLatencyFloat();
				Debugger.getInstance().infoMessage(this.getClass(),"SocketManager: startManagerThread() with sceneLatency of " + sceneLatency + "[ms]");
				manager = new Thread(new UpdateManager());
				manager.start();
				manager.setPriority(Thread.NORM_PRIORITY);
			} catch(Exception e){
				Debugger.getInstance().errorMessage(this.getClass(), "Manager Exception");
				e.printStackTrace(System.out);
			}
		}
	}
	
	private void stopManager(){
		//gracefully stopping the thread
		while(managerIsRunning){
			manager = null;
			try{
				Thread.sleep(10); // do nothing for 10 miliseconds
			} catch(InterruptedException e){
				e.printStackTrace();
			} 
		}
	}
	
	public void destroy(){
		stopManager();
		for(int i=0; i < 8; i++){
			for(int j=0; j < 8; j++){
				sockets[i][j].destroy();
			}
		}		
	}

	//refreshes 
	//  1. the plug configurations
	public void refresh(){
		stopManager();
		for(int i=0; i < 8; i++){
			for(int j=0; j < 8; j++){
				sockets[i][j].refreshPlug();
			}
		}		
		startManager();
	}
	
	public void restart(){
		stopManager();
		startManager();
	}

	protected class UpdateManager implements Runnable{

		transient Thread thisThread;		
		public void run(){
			//int counter = 0;
			//long t = System.currentTimeMillis();
			thisThread = Thread.currentThread();
			managerIsRunning = true;
			
			while(manager == thisThread){
				/*
				counter++;
				if(counter > 100){
					Debugger.getInstance().verboseMessage(this.getClass(),"SocketUpdater 100 cycles in: " + (System.currentTimeMillis() - t) + " ms");
					t = System.currentTimeMillis();
					counter = 0;
				}
				 */

				try {
					thisThread.sleep(sceneLatency);
				} catch (InterruptedException e){;}

				synchronize();
				update();
			}
			managerIsRunning = false;
		}

	}

}
