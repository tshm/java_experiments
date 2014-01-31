package jchat;
import java.rmi.*;

interface Listener extends Remote {
	void listen(String id, String s) throws RemoteException;
	void serverHasDied() throws RemoteException;
}
