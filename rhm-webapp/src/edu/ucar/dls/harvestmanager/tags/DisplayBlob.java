package edu.ucar.dls.harvestmanager.tags;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import edu.ucar.dls.harvest.Config;

import java.io.*;

/**
 * Custom tag that displays a blob that is defined by a byte[]
 * @author dfinke
 *
 */
public class DisplayBlob  extends SimpleTagSupport{
	private byte[] blob = null;
	
	/**
	 * execute the tag
	 */
	public void doTag() throws JspException, IOException {
	    JspWriter out = getJspContext().getOut();
	    out.println(new String(this.blob, Config.ENCODING));
	  }
	
	public void setBlob(byte[] blob) {
		this.blob = blob;
	}

}
