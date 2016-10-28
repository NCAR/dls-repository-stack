/*
 *  License and Copyright:
 *
 *  The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may
 *  not use this file except in compliance with the License. You should have received a copy of the License
 *  along with this software; if not, you may obtain a copy of the License at
 *  http://www.opensource.org/licenses/ecl1.php.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 *  either express or implied. See the License for the specific language governing rights and limitations
 *  under the License.
 *
 *  Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.schemedit.autoform;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.schemedit.display.PageList;
import edu.ucar.dls.schemedit.display.VirtualPageList;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import java.net.URL;
import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;

/**
 *  Displays an XML metadata record using the ViewerRenderer.
 *  DcsViewRecord provides the view of the entire record for the DCS
 *  DcsViewRecord page
 *
 *@author    ostwald
 *
 */
public class DcsViewRecord extends AutoForm {
	private static boolean debug = true;

	/**
	 *  Constructor for the DcsViewRecord object
	 *
	 *@param  framework  Description of the Parameter
	 */
	public DcsViewRecord(MetaDataFramework framework) {
		super(framework);
		formBeanName = "viewForm";
	}


	/**
	 *  Constructor for the Stand-alone DcsViewRecord object, meaning it is created
	 *  from command line rather than via schemedit.
	 *
	 *@param  xmlFormat                  Description of the Parameter
	 *@exception  SchemaHelperException  Description of the Exception
	 */
	public DcsViewRecord(String xmlFormat)
		throws Exception, SchemaHelperException {
		super(xmlFormat);
		formBeanName = "viewForm";
	}

	/**
	 *  The main program for the DcsViewRecord class. The first argument is
	 *  command, the second is arg (if nec);
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args) throws Exception {
		setVerbose(true);
		edu.ucar.dls.schemedit.display.VirtualPageConfig.setSourceURI(
			"http://ns.nsdl.org/ncs/lar/1.00/docs/virtualPageConfig.xml");
		prtln("\n==========================================================\nDCS View Record:");
		String xmlFormat = "framework_config";
		String command = "batchRender";
		String xpath = "/frameworkConfigRecord";

		try {
			xmlFormat = args[0];
			command = args[1];
			xpath = args[2];
		} catch (Exception e) {}

		prtln ("\t xmlFormat = " + xmlFormat);
		prtln ("\t command = " + command);
		prtln ("\t xpath = " + xpath);

		DcsViewRecord fullView = null;
		try {
			fullView = new DcsViewRecord(xmlFormat);
		} catch (Exception e) {
			prtln(e.getMessage());
			return;
		}
		fullView.setLogging(true);
		AutoForm.setVerbose(true);
		
		prtln(fullView.getVirtualPageList().toString());

		if (command.equals("batchRender")) {
			prtln("batchRenderAndWrite");
			fullView.batchRenderAndWrite();
			return;
		}

		if (command.equals("renderAndWrite")) {
			prtln("renderAndWrite");
			fullView.renderAndWrite(xpath);
		}
	}

	/* Render as a single JSP page */
	public void renderAndWrite() throws Exception {
		renderAndWrite ("/" + this.framework.getRootElementName());
	}

	/* Render the master file, which contains jsp:include directives to suck
	   in the component jsp pages
	*/
	protected void renderMaster () throws Exception {
		Element base = DocumentHelper.createElement("div");
		/* base.addAttribute ("class", "level--1"); */
		List elements = instanceDocument.getRootElement().elements();
		// prtln("batchRenderAndWrite found " + elements.size() + " elements");
		for (Iterator i = elements.iterator(); i.hasNext(); ) {
			Element child = (Element) i.next();
			String xpath = child.getPath();
			String pageName = XPathUtils.getNodeName(xpath);
			Element include = base.addElement("jsp__include");
			include.addAttribute("page", getMasterComponentPath (pageName));
		}

		File dest = getJspDest (null);
		if (!writeJsp(base, dest, this.getMasterJspHeader())) {
			throw new Exception ("renderMaster(): failed to write jsp to " + dest);
		}

	}

	protected void renderVirtualMaster () throws Exception {
		Element base = DocumentHelper.createElement("div");
		/* base.addAttribute ("class", "level--1"); */
		
		PageList virtualPages = this.getVirtualPageList();
		
		Iterator virtualPagesIter = virtualPages.getPages().iterator();
		while (virtualPagesIter.hasNext()) {
			
			VirtualPageList.VirtualPage page = (VirtualPageList.VirtualPage)virtualPagesIter.next();
			Element include = base.addElement("jsp__include");
			include.addAttribute("page", getMasterComponentPath (page.getName()));
		}
		
		// -----------------------------------
		
/* 		List elements = instanceDocument.getRootElement().elements();
		// prtln("batchRenderAndWrite found " + elements.size() + " elements");
		for (Iterator i = elements.iterator(); i.hasNext(); ) {
			Element child = (Element) i.next();
			String xpath = child.getPath();
			String pageName = XPathUtils.getNodeName(xpath);
			Element include = base.addElement("jsp__include");
			include.addAttribute("page", getMasterComponentPath (pageName));
		} */

		File dest = getJspDest (null);
		if (!writeJsp(base, dest, this.getMasterJspHeader())) {
			throw new Exception ("renderMaster(): failed to write jsp to " + dest);
		}

	}
	
	public void batchRenderAndWrite() throws Exception {
		
		// if (this.framework.getXmlFormat().equals("lar")) {
		if (this.hasVirtualPageList()) {
			batchRenderAndWriteVirtualPages();
			return;
		}
		
		renderMaster ();
		super.batchRenderAndWrite();
	}

	public void batchRenderAndWriteVirtualPages() throws Exception {
		renderVirtualMaster();
		super.batchRenderAndWriteVirtualPages();
	}

	protected String getRendererClassName () {
		return "ViewerRenderer";
	}

	protected File getBaseDest () {
		String basePath = framework.getDocRoot() + "/browse/viewing";
		return new File(basePath);
	}
	
	protected File getJspDest (String pageName) {
		String fileName = framework.getXmlFormat() + "_record.jsp";
		return new File(getBaseDest(), fileName);
	}

	protected File getBatchJspDest (String pageName) throws Exception {
		String fileName = pageName + ".jsp";
		File viewingDir = new File(getBaseDest(),framework.getXmlFormat());
		return new File(viewingDir, Files.encode(fileName));
	}

	protected String getMasterJspHeader () {
		String header = "<%@ include file=\"/lib/includes.jspf\" %>\n\n";
		return header;
	}
	
	/*
	** Path from Master jsp file to component jsp pages
	*/
	protected String getMasterComponentPath (String pageName) {
		return this.framework.getXmlFormat() + "/" + pageName + ".jsp";
	}

	public static void setLogging (boolean verbose) {
		try {
			/* RendererHelper.setDebug (verbose); */
			edu.ucar.dls.schemedit.autoform.mde.MdeNode.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeAttribute.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeSimpleType.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeComplexType.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeRepeatingComplexType.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeRepeatingSimpleType.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeSequence.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeDerivedTextOnlyModel.setDebug(verbose);

			/* edu.ucar.dls.schemedit.autoform.RendererHelper.setDebug(verbose); */
			edu.ucar.dls.schemedit.autoform.Renderer.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.RendererImpl.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.ViewerRenderer.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.EditorRenderer.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.DleseEditorRenderer.setDebug(verbose);
		} catch (Throwable t) {
			prtln ("setLogging ERROR: " + t.getMessage());
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
}
