package edu.ucar.dls.ldap;

import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

/**
 *  This Trust Manager is "naive" because it trusts everyone. 
 *
 * @author    Anh Nguyen
 */
public class NaiveTrustManager implements X509TrustManager {

	/**
	 *  Approves a certificate by not throwing an exception.
	 *
	 */
	public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		// do nothing
	}


	/**
	 *  Approves a certificate by not throwing an exception.
	 *
	 */
	public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		// do nothing
	}

	/**
	 *  Approves a certificate by not throwing an exception.
	 *
	 */
	public void checkClientTrusted(java.security.cert.X509Certificate[] arg0,
	                               String arg1) throws java.security.cert.CertificateException {
	}


	/**
	 *  Approves a certificate by not throwing an exception.
	 *
	 */
	public void checkServerTrusted(java.security.cert.X509Certificate[] arg0,
	                               String arg1) throws java.security.cert.CertificateException {
	}


	/**
   * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
   **/
	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	}

}

