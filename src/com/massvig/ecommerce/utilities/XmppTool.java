package com.massvig.ecommerce.utilities;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * @author Sam.Io
 * @time 2011/11/18
 * @project AdXmpp
 */
public class XmppTool {

	private static XMPPConnection con = null;
	
	private static void openConnection(String ip, int port, String service) {
		try {
	        ConnectionConfiguration connConfig = new ConnectionConfiguration(ip, port, service);
			con = new XMPPConnection(connConfig);
			con.connect();
		} catch (XMPPException xe) {
			xe.printStackTrace();
		}
	}

	public static XMPPConnection getConnection(String ip, int port, String service) {
		if (con == null) {
			openConnection(ip, port, service);
		}
		return con;
	}

	public static void closeConnection() {
		con.disconnect();
		con = null;
	}
}
