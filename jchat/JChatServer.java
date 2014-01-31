package jchat;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;

public class JChatServer extends UnicastRemoteObject implements Server {
	Hashtable listeners;

	public static void main(String argv[]) {
		System.setSecurityManager(new RMISecurityManager());
		try {
			JChatServer obj = new JChatServer();
			/*      LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
							Registry registry = LocateRegistry.getRegistry();
							registry.rebind("//www.first.tsukuba.ac.jp/jc", obj);
							*/
			Naming.rebind("//www.first.tsukuba.ac.jp/jc", obj);
			System.out.println("JChatServer bound in registry");
		} catch (Exception e) {
			System.out.println("JChatServer Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	JChatServer() throws RemoteException {
		listeners = new Hashtable();
	}

	public synchronized void registerListener(String id, Listener obj) throws RemoteException {
		// identical ID string must be denied.
		listeners.put(id, obj);
	}

	public synchronized void unregisterListener(String id) throws RemoteException {
		listeners.remove(id);
	}

	public synchronized Enumeration getListeners() throws RemoteException {
		return listeners.keys();
	}

	public synchronized void writeMessage(String id, String s) throws RemoteException {
		System.out.println(id + " > " + s);
		castMessageToAllListeners(id, s);
	}

	private void castMessageToAllListeners(String id, String s) throws RemoteException {
		for (Enumeration enum = listeners.elements(); enum.hasMoreElements(); ) 
			((Listener)enum.nextElement()).listen(id, s);
	}

	public void finalize() {
		try {
			for (Enumeration enum = listeners.elements(); enum.hasMoreElements(); ) 
				((Listener)enum.nextElement()).serverHasDied();
		} catch (Exception e) {
			System.out.println("JCharServer Exception: " + e);
			e.printStackTrace();
		}
		// write a log file
		// close all resources
	}
}
