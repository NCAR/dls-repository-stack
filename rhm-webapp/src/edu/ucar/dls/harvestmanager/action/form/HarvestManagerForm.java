package edu.ucar.dls.harvestmanager.action.form;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;
import java.text.*;
import java.net.URLEncoder;

/**
 *  A Struts ActionForm for the Harvest Repository Manager.
 *
 * @author    John Weatherley
 */
public final class HarvestManagerForm extends ActionForm implements Serializable {
	private static boolean debug = false;

	private String errMsg = null;
	private String statusMsg = null;
	
	/**  Constructor for the HarvestManagerForm object */
	public HarvestManagerForm() { }

	
	public void setErrorMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
	public String getErrorMsg () {
		return errMsg;
	}
	
	public void setStatusNotificationMsg(String msg) {
		statusMsg = msg;	
	}

	public String getStatusNotificationMsg() {
		return statusMsg;	
	}	
	
	//================================================================

	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + "HarvestManagerForm: " + s);
		}
	}


	/**
	 *  Return a string for the current time and date, sutiable for display in log files and
	 *  output to standout:
	 *
	 * @return    The dateStamp value
	 */
	private final static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
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



