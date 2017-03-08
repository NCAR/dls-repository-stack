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
package edu.ucar.dls.schemedit.ccs;

import java.util.regex.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.text.*;
import org.dom4j.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;

import edu.ucar.dls.webapps.tools.GeneralServletTools;
import edu.ucar.dls.xml.Dom4jUtils;

/**
 *  Servlet that implements the citableUrl scheme, in which citableURLs are
 *  either redirected to NLDR landingPages (views) or, when the citableURL
 *  refers to a digital asset, binary content is streamed to the requestor.
 *
 * @author    Jonathan Ostwald
 */
public final class PrimaryContentServlet extends HttpServlet {
	private static boolean debug = true;
	
	/* private String ncsDDSWSBaseUrl; */
	private File protectedDir;


	/**  Constructor for the PrimaryContentServlet object */
	public PrimaryContentServlet() { }


	/**
	 *  Initialize the servlet with values for primaryContentPath, which refers to
	 *  the location of digital assets, and landingPageBaseUrl, which is used to
	 *  construct URLs to resource landing pages.
	 *
	 * @param  config                the servlet config
	 * @exception  ServletException  if required config param is not found
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext servletContext = getServletContext();

		String primaryContentPath = (String) servletContext.getInitParameter("primaryContentPathOnServer");
		if (primaryContentPath != null && primaryContentPath.trim().length() > 0) {
			this.protectedDir = new File (primaryContentPath);
			if (!this.protectedDir.exists())
				throw new ServletException("protectedDir not found at " + primaryContentPath);
			if (!this.protectedDir.canWrite())
				throw new ServletException("protectedDir is not writable");
			prtln ("PrimaryContentServlet initialized");
		}
		else {
			prtln ("WARN: initParamter \"primaryContentPathOnServer\" not found. PrimaryContentServlet not initialized");
		}
	}

	private void showRequestInfo (HttpServletRequest request) {
		prtln ("path info");
		prtln ("\trequestURI: " + request.getRequestURI());
		prtln ("\tpathInfo: " + request.getPathInfo());
		prtln ("\tservletPath: " + request.getServletPath());
		prtln ("\trequestURL: " + request.getRequestURL().toString());
/* 		prtln ("paramater names");
		Enumeration pnames = request.getParameterNames();
		while (pnames.hasMoreElements()){
			prtln ((String)pnames.nextElement());
		}
		
		prtln ("attribute names");
		Enumeration anames = request.getAttributeNames();
		while (anames.hasMoreElements()){
			prtln ((String)anames.nextElement());
		} */
	}
	

	/**
	 *  Serve the asset content or forward to NLDR View for handling, depending on
	 *  parameters present in request.<P>
	 *
	 *  NOTE: eventually, the foward to NLDRs will be done by apache rewrite rule,
	 *  so this servlet will receive asset requests only.
	 *
	 * @param  request
	 * @param  response
	 * @exception  ServletException
	 * @exception  IOException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		prtln("doGet()");

		ServletContext servletContext = getServletContext();
		HttpSession session = request.getSession(true);

		showRequestInfo (request);
		
		try {

			/*
			legal paths:
				asset:
					assets/{collId}/{assetId}
			*/
			String path = request.getPathInfo();
			if (path == null)
				path = "";
			prtln("path: " + path);

			String[] pathSegments = path.split("/");
			prtln(pathSegments.length + " path segments");
			// first seg is blank from leading slash

			if (pathSegments.length != 3) {
				// no path - treat as 404
 				forwardToErrorPage (request, response);
/*				throw new ServletException("params not supplied"); 
				response.sendError(407, "Need authentication!!!" );*/
			}
			
			String collection = pathSegments[1];
			String fileName = pathSegments[2];

			doServeContent (collection, fileName, request, response);
			
		} catch (NullPointerException e) {
			prtlnErr("PrimaryContentServlet caught unknown exception. \t\n(remoteAddr: " + request.getRemoteAddr() + ")");
			e.printStackTrace();
			forwardToErrorPage (request, response);
			return;
		} catch (Throwable e) {
			prtlnErr(e.getMessage() + " \n\t(remoteAddr: " + request.getRemoteAddr() + ")");
			e.printStackTrace();
			forwardToErrorPage (request, response);
			return;
		}
	}

	/*
	http://localhost:8070/curricula/content/bogus-collection
	http://localhost:8070/curricula/content/bogus-colection/bogus-asset.pdf
	http://localhost:8070/curricula/content/bogus-colection/bogus-asset.pdf/extra
	http://localhost:8070/curricula/content/dps_tips/6_1_Instructional_Task_Rocks.pdf
	

	*/

	/** Serve binary content or forward to approprate JSP for handling. */
	private void doServeContent(String collection, String fileName,
	                            HttpServletRequest request,
	                            HttpServletResponse response) throws ServletException, IOException {

		prtln("doServeContent!");
		ServletContext servletContext = getServletContext();
		
		File fileToRead = new File(protectedDir, collection + "/" + fileName);
		prtln("- fileToRead: " + fileToRead);

		if (!fileToRead.exists() || fileToRead.isDirectory()) {
			prtlnErr("- Resource file (" + fileName + ") not found in protectedDir for " + collection);
			
			forwardToErrorPage(request, response);
			return;
		}

		// get fileName and size from file on disk
		fileName = fileToRead.getName();
		prtln("- fileName: " + fileName);
		
		String sizeBytes = String.valueOf(fileToRead.length());
		prtln("sizeBytes: " + sizeBytes);

		// Get the mime type from the servlet config (defined in global or webapp web.xml). If not available, use default.
		String contentType = servletContext.getMimeType(fileName.toLowerCase());

		// The default contentType to use if unknown, per rfc1341:
		if (contentType == null)
			contentType = "application/octet-stream";
		prtln("- contentType: " + contentType);

		// Set the headers:
		try {
			response.reset();
		} catch (Throwable t) {
			prtln ("WARNING: could not reset: " + t.getMessage());
		}

		response.setContentType(contentType);

		// pdf, image, text are not setAsAttachment
		if (setAsAttachment(contentType, fileName)) {
			prtln("- Attachment");
			response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		}
		else {
			prtln ("- NOT attachment");
			response.addHeader("Content-Disposition", "filename=\"" + fileName + "\"");
		}
		if (sizeBytes != null)
			response.addHeader("Content-Length", sizeBytes);

		// Generate Last-Modified header in HTTP-date format rfc1123-date
		Date lastModifiedDate = new Date(fileToRead.lastModified());

		SimpleDateFormat df = new SimpleDateFormat("E', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' GMT'");
		df.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
		String datestg = df.format(lastModifiedDate);
		response.addHeader("Last-Modified", datestg);

		// Stream the binary content:
		OutputStream responseOutputStream = response.getOutputStream();
		BufferedInputStream contentInputStream = new BufferedInputStream(new FileInputStream(fileToRead));
		ByteArrayOutputStream contentOutputStream = new ByteArrayOutputStream(2048);
		int contentByte;
		while ((contentByte = contentInputStream.read()) != -1)
			contentOutputStream.write(contentByte);
		contentInputStream.close();
		contentOutputStream.writeTo(responseOutputStream);
		return;
	}

	/* Determine if the content should be set as an attachment for download or inline for display inthe browser */
	private boolean setAsAttachment(String contentType, String fileName) {
		boolean isAttachment = true;

		// prtln("setAsAttachment");
		// prtln("  contentType: " + contentType);

		// image, text and pdf are NOT delivered as attachment
		if (contentType.startsWith("image") || contentType.startsWith("text") || contentType.startsWith("application/pdf"))
			isAttachment = false;

		// These may be redundent, but for good measure:
		else if (fileName != null) {
			fileName = fileName.toLowerCase();
			if (fileName.endsWith(".pdf") || fileName.endsWith(".jpg") || 
				fileName.endsWith(".gif") || fileName.endsWith(".xml") || fileName.endsWith(".png"))
				isAttachment = false;
		}

		// prtln("setAsAttachment(contentType:'" + contentType + "', fileName:'" + fileName + "'): " + isAttachment);
		return isAttachment;
	}


	private void forwardToErrorPage (HttpServletRequest request, HttpServletResponse response) {
		// String views404Address = landingPageBaseUrl + "404.php";
		prtln ("forwardToErrorPage()");
		// redirectToAddress (views404Address, request, response);
/* 		try {
			prtln ("sending 400 error ...");
			response.sendError(400, "Need authentication!!!" );
			return;
		} catch (IOException e) {
			e.printStackTrace();
			prtlnErr ("forwardToErrorPage ERROR: " + e.getMessage());
		} */
		
		try {
			prtln ("redirecting to address for 400 error ...");
			redirectToAddress ("/WEB-INF/error_pages/error_page_400.jsp", request, response);
			// redirectToAddress ("http://localhost:8070/curriculum/error_page_400.jsp", request, response);
			return;
		} catch (Exception e) {
			prtlnErr ("redirectToAddress ERROR: " + e.getMessage());
		}	
		
		
	}
	
	private void redirectToAddress (String address, HttpServletRequest request, HttpServletResponse response) {
		prtln ("redirectToAddress is OFF");
		return;
/* 		try {
			prtln("redirectToAddress(): " + address);
			response.sendRedirect(address);
		} catch (IllegalStateException ise) {
			prtlnErr("Got IllegalStateException trying to forward request to " + address);
			if (response.isCommitted())
				prtln ("response has already been committed (this should not be the case!");
			ise.printStackTrace();
		} catch (Throwable t) {
			prtlnErr("Unable to forward request to " + address + ": " + t);
		} */
	}
	
	// ---------------------- Debug info --------------------

	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	protected final static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " PrimaryContentServlet Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug) {
			// System.out.println(getDateStamp() + " PrimaryContentServlet: " + s);
			System.out.println("PrimaryContentServlet: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}
}

