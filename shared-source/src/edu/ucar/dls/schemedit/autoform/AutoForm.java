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
import edu.ucar.dls.schemedit.display.VirtualPageList;
import edu.ucar.dls.schemedit.display.VirtualPageConfig;
import edu.ucar.dls.schemedit.display.PageList;
import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import java.net.URL;
import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.io.OutputFormat;
// import org.dom4j.io.XMLWriter;

/**
 *  Class to automatically generate jsp pages (using a Renderer class such as
 *  {@link edu.ucar.dls.schemedit.autoform.DleseEditorRenderer}) for editing
 *  and viewing of schemedit-based xml documents.<p>
 *
 *  Called from command line for debugging as well as from {@link
 *  edu.ucar.dls.schemedit.MetaDataFramework} at start-up time and after
 *  run-time reconfiguration.
 *
 * @author    ostwald
 */
public class AutoForm {
	private static boolean debug = false;
	private static boolean verbose = false;

	/**  instance doc for this framework */
	protected Document instanceDocument = null;
	/**  SchemaHelper for this framework */
	protected SchemaHelper sh;
	/**  the framework */
	protected MetaDataFramework framework;
	/**  form bean that backs metadata editor */
	protected String formBeanName = "sef";


	/**
	 *  Constructor for the AutoForm object
	 *
	 * @param  framework  Description of the Parameter
	 */
	public AutoForm(MetaDataFramework framework) {
		this.framework = framework;
		this.sh = framework.getSchemaHelper();
		instanceDocument = sh.getInstanceDocument();
		if (instanceDocument == null) {
			prtln("instanceDocument not initialized in schemaHelper");
		}
	}


	/**
	 *  Returns true if the framework for this AutoForm defines a {@link
	 *  VirtualPageList}.
	 *
	 * @return    true if framework defines a VirtualPageList
	 */
	protected boolean hasVirtualPageList() {
		PageList pageList = framework.getPageList();
		if (pageList != null && (pageList instanceof VirtualPageList))
			return true;
		return false;
	}


	/**
	 *  Gets the virtualPageList attribute of the AutoForm object
	 *
	 * @return    The virtualPageList value or null.
	 */
	protected VirtualPageList getVirtualPageList() {
		if (this.hasVirtualPageList())
			return (VirtualPageList) framework.getPageList();
		return null;
	}


	/**
	 *  Constructor for the Stand-along AutoForm object, meaning it is created from
	 *  command line rather than via schemedit.
	 *
	 * @param  xmlFormat                  Description of the Parameter
	 * @exception  SchemaHelperException  Description of the Exception
	 * @exception  Exception              NOT YET DOCUMENTED
	 */
	public AutoForm(String xmlFormat)
		 throws Exception, SchemaHelperException {

		framework = TesterUtils.getFramework(xmlFormat);
		sh = framework.getSchemaHelper();

		instanceDocument = sh.getInstanceDocument();
		if (instanceDocument == null) {
			prtln("instanceDocument not initialized in schemaHelper");
		}
	}


	/**
	 *  The main program for the AutoForm class. The first argument is command, the
	 *  second is arg (if nec);
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		setVerbose(true);
		debug = true;
		VirtualPageConfig.setSourceURI("http://ns.nsdl.org/ncs/lar/1.00/docs/virtualPageConfig.xml");
		verbose = true;
		String xmlFormat = "comm_anno";
		String command = "renderAndWrite";
		String xpath = ""; // "/collectionRecord/general";

		if (args.length == 0) {
			prtln("Usage: \n\tframework (adn)\n\t command (renderAndWrite or batchRender) \n\txpath");
			return;
		}

		try {
			xmlFormat = args[0];
			command = args[1];
			xpath = args[2];
		} catch (Exception e) {}

		prtln("--------------------------------------------------------------------------");
		prtln("AutoForm:");
		prtln("\tframework: " + xmlFormat);
		prtln("\tcommand: " + command);
		prtln("\txpath: " + xpath);

		// RendererHelper.setVerbose (true);

		AutoForm autoForm = null;
		try {
			autoForm = new AutoForm(xmlFormat);
		} catch (Exception e) {
			prtln(e.getMessage());
			return;
		}

		if (command.equals("batchRender")) {
			autoForm.batchRenderAndWrite();
			return;
		}

		if (command.equals("renderAndWrite")) {
			prtln("autoForm for " + xpath);
			/* autoForm.setRendererClassName ("CollapsibleJspRenderer"); */
			autoForm.renderAndWrite(xpath);
		}
	}


	/**
	 *  Renders a metadata editor form for the specified xpath, and writes the
	 *  rendered form to disk as JSP.
	 *
	 * @param  xpath          root element of rendered form
	 * @exception  Exception  if not able to render and write form
	 */
	public void renderAndWrite(String xpath) throws Exception {

		File dest = getJspDest(XPathUtils.getNodeName(xpath));
		renderAndWrite(xpath, dest);
	}


	/**
	 *  Renders a metadata editor form for the specified xpath, and writes the
	 *  rendered form to disk as JSP.
	 *
	 * @param  xpath          root element of rendered form
	 * @param  dest           path at which to write JSP
	 * @exception  Exception  if not able to render and write form
	 */
	public void renderAndWrite(String xpath, File dest) throws Exception {
		Element e = render(xpath);

		if (!writeJsp(e, dest, getMasterJspHeader())) {
			throw new Exception("failed to write jsp to " + dest);
		}
	}


	/**
	 *  Create a jsp file for each top-level element of the schema and write to
	 *  proper location in webapp (i.e., "webapps/${app_name}/editor/${xmlFormat}/${page_name}.jsp"
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void batchRenderAndWrite() throws Exception {
		prtln("\nbatchRenderAndWrite()");

		if (this.hasVirtualPageList()) {
			batchRenderAndWriteVirtualPages();
			return;
		}
		List elements = instanceDocument.getRootElement().elements();
		// prtln("batchRenderAndWrite found " + elements.size() + " elements");
		for (Iterator i = elements.iterator(); i.hasNext(); ) {
			Element child = (Element) i.next();
			String xpath = child.getPath();
			String pageName = XPathUtils.getNodeName(xpath);
			// prtln(pageName + " (" + xpath + ")");

			Element e = render(xpath);
			if (e != null &&
				!writeJsp(e, getBatchJspDest(pageName), this.getComponentJspHeader())) {
				throw new Exception("batchRenderAndWrite could not write jsp for " + pageName);
			}
		}
	}


	/**
	 *  Render and write metadata pages as defined by a VirtualPageList
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void batchRenderAndWriteVirtualPages() throws Exception {

		PageList virtualPages = framework.getPageList();

		Iterator virtualPagesIter = virtualPages.getPages().iterator();
		while (virtualPagesIter.hasNext()) {

			VirtualPageList.VirtualPage page = (VirtualPageList.VirtualPage) virtualPagesIter.next();

			for (Iterator i = page.getFields().iterator(); i.hasNext(); ) {
				String field = (String) i.next();
				prtln(" - " + field);
			}

			Element e = renderVirtualPage(page);
			String pageName = page.getMapping();

			if (e != null &&
				!writeJsp(e, getBatchJspDest(pageName), this.getComponentJspHeader())) {
				throw new Exception("batchRenderAndWrite could not write jsp for " + pageName);
			}
		}
	}


	/**
	 *  Render a virtual page, which consists of a list of editor fields. Each field is rendered
	 individually, and the result for each field is appended to a root element.
	 *
	 * @param  page  A virtual page from a VirtualPageList
	 * @return       rendered JSP as a single XML element.
	 */
	public Element renderVirtualPage(VirtualPageList.VirtualPage page) {
		Element root = DocumentHelper.createElement("div");
		Element label = root.addElement("div");
		// prtln("\n***\nrenderVirtualPage - mapping: " + page.getMapping());
		label.setText(page.getMapping());
		label.setAttributeValue("class", "main-element-label");
		RendererHelper rhelper = new RendererHelper(root, framework, formBeanName, getRendererClassName());
		// prtln("renderVirtualPage with " + page.getFields().size() + " fields");
		Iterator fieldIter = page.getFields().iterator();
		RendererImpl renderer = null;

		while (fieldIter.hasNext()) {
			String xpath = (String) fieldIter.next();

			try {
				renderer = rhelper.getRenderer(xpath, root);
				if (renderer == null)
					throw new Exception("renderer is null");
			} catch (Exception e) {
				prtln("Unable to create renderer: " + e.getMessage());
				e.printStackTrace();
				return null;
			}
			renderer.renderNode();
			rhelper.destroy();
		}

		return root;
	}


	/**
	 *  Gets the rendererClassName attribute of the AutoForm object
	 *
	 * @return    The rendererClassName value
	 */
	protected String getRendererClassName() {
		return framework.getRenderer();
	}

	/**
	 *  Produce a {@link org.dom4j.Element} representing an editor for the node at
	 *  xpath. The Element returned by render is eventually converted to JSP.
	 *
	 * @param  xpath  XPath to a Node in the Schema
	 * @return        An Element representing an editor for the given schema node
	 */
	public Element render(String xpath) {
		Element instanceDocElement = (Element) sh.getInstanceDocNode(RendererHelper.normalizeXPath(xpath));
		if (instanceDocElement == null) {
			prtln("renderer couldn't find node for " + xpath);
			return null;
		}
		Element root = DocumentHelper.createElement("div");
		RendererHelper rhelper = new RendererHelper(root, framework, formBeanName, getRendererClassName());
		RendererImpl renderer = null;
		try {
			renderer = rhelper.getRenderer(xpath, root);
		} catch (Exception e) {
			prtln("Unable to create renderer: " + e.getMessage());
			return null;
		}

		renderer.renderNode();
		rhelper.destroy();
		return root;
	}


	/**
	 *  Perform any modifications to the XML to create legal JSP. Some strings,
	 *  such as tag-like notation (e.g., "c:set") are not convenient in XML
	 *  processing, since they are interpreted as namespaces, so they are encoded
	 *  by the renderer using a convention (e.g., "c__set") and then decoded here
	 *  to convert to JSP
	 *
	 * @param  e  Element produced by renderer
	 * @return    JSP representation of Element
	 */
	public static String elementToJsp(Element e) {
		String s = Dom4jUtils.prettyPrint(e);
		Pattern p = Pattern.compile("__");
		Matcher m = p.matcher(s);
		s = m.replaceAll(":");

		// replace ^V^ pattern with single quotes
		p = Pattern.compile("\\^v\\^");
		m = p.matcher(s);
		s = m.replaceAll("'");

		s = replaceDirectives(s);

		return s;
	}


	/**
	 *  Unescape JSP page directives that were encoded to avoid XML errors.
	 *
	 * @param  s  the string containing directives to be replaced
	 * @return    string containing page directives
	 */
	static String replaceDirectives(String s) {
		// pattern to detect page directives (e.g., includes) that were inserted
		// into Document as text and therefore have the tags escaped.
		Pattern p = Pattern.compile("&lt;(%@.*?%)&gt;");
		Matcher m = null;
		while (true) {
			m = p.matcher(s);
			if (m.find()) {
				String repl = "<" + m.group(1) + ">";
				s = m.replaceFirst(repl);
			}
			else
				break;
		}
		return s;
	}


	/**
	 *  Path for writing component jsp pages.
	 *
	 * @param  pageName       NOT YET DOCUMENTED
	 * @return                The batchJspDest value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	protected File getBatchJspDest(String pageName) throws Exception {
		return getJspDest(pageName);
	}


	/**
	 *  Path for writing master jsp files.
	 *
	 * @param  pageName       NOT YET DOCUMENTED
	 * @return                The jspDest value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	protected File getJspDest(String pageName) throws Exception {
		String fileName = pageName + ".jsp";
		File autoFormDir = new File(framework.getAutoFormDir());
		return new File(autoFormDir, Files.encode(fileName));
	}


	/**
	 *  JSP code to insert at the top of master jsp files.
	 *
	 * @return    The masterJspHeader value
	 */
	protected String getMasterJspHeader() {
		String header = "<%@ include file=\"/lib/includes.jspf\" %>\n\n";
		header += "<%@ page import=\"edu.ucar.dls.schemedit.display.CollapseBean\" %>\n\n";
		header +=
			"<bean:define id=\"collapseBean\" name=\"" + formBeanName + "\" property=\"collapseBean\" type=\"CollapseBean\" />\n\n";
		return header;
	}


	/**
	 *  JSP code to insert at the top of component jsp files. In the case of
	 *  AutoForm, where the components are on separate JSP pages, the component and
	 *  master headers are the same. In subclasses, the component JSP pages maybe
	 *  included (via jsp:include) in the master, and therefore they may require a
	 *  different header.
	 *
	 * @return    The componentJspHeader value
	 */
	protected String getComponentJspHeader() {
		return getMasterJspHeader();
	}


	/**
	 *  Writes Element to disk as JSP page to be included in a master page at run
	 *  time.
	 *
	 * @param  element    Element representing editor page
	 * @param  dest       NOT YET DOCUMENTED
	 * @param  jspHeader  NOT YET DOCUMENTED
	 * @return            true if JSP was successfully written
	 */
	protected boolean writeJsp(Element element, File dest, String jspHeader) {

		String autoJsp = jspHeader + elementToJsp(element);
		String errorMsg;

		try {

			File destDir = dest.getParentFile();
			if (!destDir.exists() && !destDir.mkdirs()) {
				errorMsg = "couldn't find or make a directory at " + destDir;
				throw new Exception(errorMsg);
			}

			Files.writeFile(autoJsp, dest);
			if (verbose)
				System.out.println("jspf written to " + dest.toString());
		} catch (Exception e) {
			prtln("writeJspFragment couldn't write to disk: " + e);
			return false;
		}
		return true;
	}

	/**
	 *  Sets the logging attribute of the AutoForm class
	 *
	 * @param  verbose  The new logging value
	 */
	public static void setLogging(boolean verbose) {
		try {
			/* RendererHelper.setDebug (verbose); */
			edu.ucar.dls.schemedit.autoform.mde.MdeNode.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeAttribute.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeSimpleType.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeComplexType.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeRepeatingComplexType.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeRepeatingSimpleType.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.mde.MdeSequence.setDebug(verbose);

			/* 			edu.ucar.dls.schemedit.autoform.RendererHelper.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.Renderer.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.RendererImpl.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.ViewerRenderer.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.EditorRenderer.setDebug(verbose);
			edu.ucar.dls.schemedit.autoform.DleseEditorRenderer.setDebug(verbose); */
		} catch (Throwable t) {
			prtln("setLogging ERROR: " + t.getMessage());
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "");
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	protected static void prtlnErr(String s) {
		System.out.println(s);
	}


	/**
	 *  Sets the verbose attribute of the AutoForm class
	 *
	 * @param  verbosity  The new verbose value
	 */
	public static void setVerbose(boolean verbosity) {
		verbose = verbosity;
	}
}

