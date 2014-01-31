package jchat;
import java.rmi.*;
import java.util.*;

interface Server extends Remote {
	void registerListener(String id, Listener obj) throws RemoteException;
	void unregisterListener(String id) throws RemoteException;
	void writeMessage(String id, String s) throws RemoteException;
	Enumeration getListeners() throws RemoteException;
	//  String[] getAllRecord() throws RemoteException;
}
