package edu.ucar.dls.ldap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 *  a SSL Factory instance that accepts all server certificates.
 *
 *	env.put(Context.SECURITY_PROTOCOL, "ssl");
 *	env.put("java.naming.ldap.factory.socket", "edu.ucar.dls.ldap.NaiveSSLSocketFactory");
 *
 */
public class NaiveSSLSocketFactory extends SSLSocketFactory {

	private SSLSocketFactory socketFactory;


	/**  Constructor for the NaiveSSLSocketFactory object */
	public NaiveSSLSocketFactory() {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[]{new NaiveTrustManager()}, new SecureRandom());
			socketFactory = ctx.getSocketFactory();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			/* handle exception */
		}
	}


	/**
	 *  Gets the default attribute of the NaiveSSLSocketFactory class
	 *
	 * @return    The default value
	 */
	public static SocketFactory getDefault() {
		return new NaiveSSLSocketFactory();
	}


	/**
	 *  Gets the defaultCipherSuites attribute of the NaiveSSLSocketFactory object
	 *
	 * @return    The defaultCipherSuites value
	 */
	public String[] getDefaultCipherSuites() {
		return socketFactory.getDefaultCipherSuites();
	}


	/**
	 *  Gets the supportedCipherSuites attribute of the NaiveSSLSocketFactory
	 *  object
	 *
	 * @return    The supportedCipherSuites value
	 */
	public String[] getSupportedCipherSuites() {
		return socketFactory.getSupportedCipherSuites();
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  socket           NOT YET DOCUMENTED
	 * @param  string           NOT YET DOCUMENTED
	 * @param  i                NOT YET DOCUMENTED
	 * @param  bln              NOT YET DOCUMENTED
	 * @return                  NOT YET DOCUMENTED
	 * @exception  IOException  NOT YET DOCUMENTED
	 */
	public Socket createSocket(Socket socket, String string, int i, boolean bln) throws IOException {
		return socketFactory.createSocket(socket, string, i, bln);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  string                    NOT YET DOCUMENTED
	 * @param  i                         NOT YET DOCUMENTED
	 * @return                           NOT YET DOCUMENTED
	 * @exception  IOException           NOT YET DOCUMENTED
	 * @exception  UnknownHostException  NOT YET DOCUMENTED
	 */
	public Socket createSocket(String string, int i) throws IOException, UnknownHostException {
		return socketFactory.createSocket(string, i);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  string                    NOT YET DOCUMENTED
	 * @param  i                         NOT YET DOCUMENTED
	 * @param  ia                        NOT YET DOCUMENTED
	 * @param  i1                        NOT YET DOCUMENTED
	 * @return                           NOT YET DOCUMENTED
	 * @exception  IOException           NOT YET DOCUMENTED
	 * @exception  UnknownHostException  NOT YET DOCUMENTED
	 */
	public Socket createSocket(String string, int i, InetAddress ia, int i1) throws IOException, UnknownHostException {
		return socketFactory.createSocket(string, i, ia, i1);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  ia               NOT YET DOCUMENTED
	 * @param  i                NOT YET DOCUMENTED
	 * @return                  NOT YET DOCUMENTED
	 * @exception  IOException  NOT YET DOCUMENTED
	 */
	public Socket createSocket(InetAddress ia, int i) throws IOException {
		return socketFactory.createSocket(ia, i);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  ia               NOT YET DOCUMENTED
	 * @param  i                NOT YET DOCUMENTED
	 * @param  ia1              NOT YET DOCUMENTED
	 * @param  i1               NOT YET DOCUMENTED
	 * @return                  NOT YET DOCUMENTED
	 * @exception  IOException  NOT YET DOCUMENTED
	 */
	public Socket createSocket(InetAddress ia, int i, InetAddress ia1, int i1) throws IOException {
		return socketFactory.createSocket(ia, i, ia1, i1);
	}

}

