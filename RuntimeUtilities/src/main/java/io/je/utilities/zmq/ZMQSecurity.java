package io.je.utilities.zmq;

import org.zeromq.ZMQ.Curve.KeyPair;

public class ZMQSecurity {
	
	private static boolean isSecure = true;
	private static KeyPair serverPair = new KeyPair("P7*N&z+Nd#:h}bHBM0pjAQx}}>-G:g]vx$T=yiQr","}r!y</A*iP[iAE-1feWqiJL@/NfKy6.eN?o@Pn{L" );
	public static boolean isSecure() {
		return isSecure;
	}
	public static void setSecure(boolean isSecure) {
		ZMQSecurity.isSecure = isSecure;
	}
	public static KeyPair getServerPair() {
		return serverPair;
	}
	public static void setServerPair(KeyPair serverPair) {
		ZMQSecurity.serverPair = serverPair;
	}



	

}
