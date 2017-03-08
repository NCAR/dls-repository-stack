/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
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

