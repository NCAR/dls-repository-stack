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
package edu.ucar.dls.xml.schema.action;

import edu.ucar.dls.schemedit.*;

import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.action.form.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.index.writer.*;
import edu.ucar.dls.index.queryParser.*;
import edu.ucar.dls.serviceclients.remotesearch.*;

import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.QueryParser;

import edu.ucar.dls.vocab.MetadataVocab;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Locale;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

import java.net.URLEncoder;
import java.net.URLDecoder;

/**
 *  Just a minimal action that will set up a form bean and then forward to a jsp
 *  page this action depends upon an action-mapping in the struts-config file!
 *
 *@author     ostwald
 */
public final class AdvancedQueryAction extends Action {

	private static boolean debug = true;
	private FieldBean fieldBean = null;

	// --------------------------------------------------------- Public Methods

	/**
	 *  Processes the specified HTTP request and creates the corresponding HTTP
	 *  response by forwarding to a JSP that will create it. Returns an {@link
	 *  org.apache.struts.action.ActionForward} instance that maps to the Struts
	 *  forwarding name "xxx.xxx," which must be configured in struts-config.xml to
	 *  forward to the JSP page that will handle the request.
	 *
	 *@param  mapping               Description of the Parameter
	 *@param  form                  Description of the Parameter
	 *@param  request               Description of the Parameter
	 *@param  response              Description of the Parameter
	 *@return                       Description of the Return Value
	 *@exception  IOException       Description of the Exception
	 *@exception  ServletException  Description of the Exception
	 */
	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
		throws IOException, ServletException {
		/*
		 *  Design note:
		 *  Only one instance of this class gets created for the app and shared by
		 *  all threads. To be thread-safe, use only local variables, not instance
		 *  variables (the JVM will handle these properly using the stack). Pass
		 *  all variables via method signatures rather than instance vars.
		 */
		 
		prtln ("\nexecuting");

		ActionErrors errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();
		
		AdvancedQueryForm aqf;
		MetaDataFramework framework = null;

		try {
			aqf = (AdvancedQueryForm) form;
		} catch (Throwable e) {
			prtln("AdvancedQueryAction caught exception. " + e);
			return null;
		}

		String errorMsg;

		FrameworkRegistry frameworkRegistry = getRegistry();
		List xmlFormats = frameworkRegistry.getAllFormats();
		aqf.setFrameworks(xmlFormats);
		try {
			fieldBean = new FieldBean (getFrameworks(xmlFormats));
		} catch (Exception e) {
			throw new ServletException (e.getMessage());
		}

		// RemoteSearcher rs = (RemoteSearcher) servlet.getServletContext().getAttribute("RemoteSearcher");

		// Query Args
		String command = request.getParameter("command");
		
		SchemEditUtils.showRequestParameters(request);	
		
		if (command == null) {
			return mapping.findForward("advanced.query");
		}
		
		if (command.equals("search")) {
			return doSearch (mapping, form, request, response);
		}
		
		if (command.equals("getChoices")) {
			String level = request.getParameter("level");
			String parent = request.getParameter("parent");
			aqf.setChoices (fieldBean.getChoices (Integer.parseInt(level), parent));
			return mapping.findForward ("advanced.query.choices");
		}
		
		return mapping.findForward("advanced.query");
	}

	private ActionForward doSearch(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) {
				
		prtln ("doSearch");
		AdvancedQueryForm aqf = (AdvancedQueryForm) form;
		RepositoryManager rm =
				(RepositoryManager) servlet.getServletContext().getAttribute("repositoryManager");
		ResultDocList results = ddsStandardQuery (request, rm, servlet.getServletContext());
		prtln (results.size() + " results found");
		aqf.setResults(results);
		return mapping.findForward("advanced.query");
	}
	
	/**
	 *  Performs textual and field-based searches limited to discoverable items only and using pre-defined search
	 *  logic. Used by DDS search and DDSWebServices search.
	 *
	 * @param  request                      The HTTP request
	 * @param  rm                           The RepositoryManager
	 * @param  vocab                        The MetadataVocab
	 * @param  context                      The ServletContext
	 * @param  searchType                   The searchType that gets logged
	 * @param  additionalQueryOrConstraint  If the q parameter is empty, this value will be used as the search
	 *      query, if q is not empty this value will be ANDed with it
	 * @return                              A DDSStandardSearchResult that contains the search results and a
	 *      String indicating which page to forward to for presentataion.
	 */
	public static ResultDocList ddsStandardQuery(
	                                                       HttpServletRequest request,
	                                                       RepositoryManager rm,
	                                                       ServletContext context) {
		String s = request.getParameter("s");
															   
		ResultDocList resultDocs = null;
		SimpleLuceneIndex index = rm.getIndex();

		BooleanQuery booleanQuery = new BooleanQuery();

		QueryParser qp = index.getQueryParser();
		
		Enumeration paramEnum = request.getParameterNames();
		
		while (paramEnum.hasMoreElements()) {
			String paramName = (String)paramEnum.nextElement();
			prtln ("param: " + paramName);
			if (paramName.startsWith("field_")) {
				String num = paramName.substring("field_".length());
				prtln ("num: " + num);
				String field = request.getParameter(paramName);
				String value = request.getParameter("value_"+num);
				if (value == null || value.trim().length() == 0)
					continue;
				//HARDCODE to do key matching for now
				
				// What kind of match are we doing??
				String match = request.getParameter("match_"+num);
				if (match == null)
					match = "exact";
				String fieldType = ("exact".equals(match) ? "key" : "text");
				
				String clause = null;
				BooleanClause.Occur occur = BooleanClause.Occur.MUST;
				if (value.equals("ANY")) {
					clause = "indexedXpaths:\""+field+"\"";
				} else if (value.equals("NONE")) {
					clause = "indexedXpaths:\""+field+"\"";
					occur = BooleanClause.Occur.MUST_NOT;
				} else {
					clause = "/"+fieldType+"/"+field+":\""+value+"\"";
				}
				try {
					booleanQuery.add(qp.parse(clause), occur);
				} catch (Exception e) {
					prtln ("Parse Error: " + e.getMessage());
				}
			}
		}
		
		
		prtln ("query: " + booleanQuery);
														   
		resultDocs = index.searchDocs(booleanQuery);

		int numSearchResults = (resultDocs == null) ? 0 : resultDocs.size();
		

		return resultDocs;
	}

	
	private List getFrameworks (List xmlFormats) throws Exception {
		FrameworkRegistry frameworkRegistry = getRegistry();
		List frameworks = new ArrayList();
		for (Iterator i=xmlFormats.iterator();i.hasNext();) {
			String xmlFormat = (String)i.next();
			try {
				frameworks.add (frameworkRegistry.getFramework(xmlFormat));
			} catch (Exception e) {
				prtln ("ERROR: could not get framework for \"" + xmlFormat + "\":" + e.getMessage());
			}
		}
		return frameworks;
	}
	
	private FrameworkRegistry getRegistry () throws ServletException {
		FrameworkRegistry reg = (FrameworkRegistry)servlet.getServletContext()
			.getAttribute("frameworkRegistry");
		if (reg == null) {
			throw new ServletException ("frameworkRegistry not found in servletContext");
		}
		else {
			return reg;
		}
	}
	
	private ActionErrors setGlobalDef (String path, SchemaHelper schemaHelper, AdvancedQueryForm aqf) {
		ActionErrors errors = new ActionErrors ();
		GlobalDef globalDef = schemaHelper.getGlobalDefFromXPath(path);
		if (globalDef == null) {
			errors.add(errors.GLOBAL_ERROR,
				new ActionError("schemaviewer.def.notfound.error", path));
		}
		else {
			aqf.setGlobalDef(globalDef);
			// aqf.setTypeName(globalDef.getName());
		}
		return errors;
	}
	
	private MetaDataFramework getMetaDataFramework(String xmlFormat) throws ServletException {
		return getRegistry().getFramework (xmlFormat);
	}
	
		
	private void prtParam (String name, String value) {
		if (value == null)
			prtln(name + " is null");
		else
			prtln(name + " is " + value);
	}

	/**
	 *  Print a line to standard out.
	 *
	 *@param  s  The String to print.
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("AdvancedQueryAction: " + s);
			SchemEditUtils.prtln (s, "AdvancedQueryAction");
		}
	}

}

