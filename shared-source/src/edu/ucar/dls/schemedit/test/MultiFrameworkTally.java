
package edu.ucar.dls.schemedit.test;

import java.util.*;

import edu.ucar.dls.schemedit.test.SchemaHelperTester;
import edu.ucar.dls.xml.schema.*;

/**
 *  Class for comaparing schemaHelpers accross mulitple frameworks. For example,
 *  to collect the "derivedContentModel" elements for each of a list of
 *  frameworks and then display them.
 *
 * @author    ostwald<p>
 *
 *
 */
public class MultiFrameworkTally {

	private static boolean debug = true;
	private Map schemaHelpers = null;


	/**
	 *  Constructor for the MultiFrameworkTally object
	 *
	 * @param  formats        NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	MultiFrameworkTally(List formats) throws Exception {
		schemaHelpers = new HashMap();
		for (Iterator i = formats.iterator(); i.hasNext(); ) {
			String xmlFormat = (String) i.next();
			try {
				prtln("loading " + xmlFormat);
				SchemaHelperTester sht = new SchemaHelperTester(xmlFormat);
				if (sht == null)
					throw new Exception("tester is null");
				schemaHelpers.put(xmlFormat, sht.sh);
			} catch (Throwable e) {
				prtln("error: " + e.getMessage());
			}
		}

		prtln(schemaHelpers.size() + " frameworks loaded");
	}


	/**
	 *  Gets the schemaHelper attribute of the MultiFrameworkTally object
	 *
	 * @param  xmlFormat  NOT YET DOCUMENTED
	 * @return            The schemaHelper value
	 */
	SchemaHelper getSchemaHelper(String xmlFormat) {
		return (SchemaHelper) this.schemaHelpers.get(xmlFormat);
	}


	/**
	 *  Gets the xmlFormats attribute of the MultiFrameworkTally object
	 *
	 * @return    The xmlFormats value
	 */
	Set getXmlFormats() {
		return this.schemaHelpers.keySet();
	}

	/**  NOT YET DOCUMENTED */
	void showModelGroups() {
		for (Iterator i = this.getXmlFormats().iterator(); i.hasNext(); ) {
			String xmlFormat = (String) i.next();
			SchemaHelper sh = this.getSchemaHelper(xmlFormat);
			List groups = SchemaUtils.getModelGroups(sh);
			prtln("\n *** " + xmlFormat + "(" + groups.size() + ") ***");
			for (Iterator g=groups.iterator();g.hasNext();) {
				GlobalDef globalDef = (GlobalDef)g.next();
				prtln ("\n" + globalDef.toString());
/* 				ModelGroup group = (ModelGroup)g.next();
				prtln ("\t name: " + group.getName()); */
			}
		}
	}

	/**  NOT YET DOCUMENTED */
	void showDerivedContentModelNodes() {
		for (Iterator i = this.getXmlFormats().iterator(); i.hasNext(); ) {
			String xmlFormat = (String) i.next();
			SchemaHelper sh = this.getSchemaHelper(xmlFormat);
			prtln("\n *** " + xmlFormat + "***");
			SchemaUtils.showDerivedContentModelNodes(sh);
		}
	}

	void showDerivedTextOnlyModelNodes() {
		for (Iterator i = this.getXmlFormats().iterator(); i.hasNext(); ) {
			String xmlFormat = (String) i.next();
			SchemaHelper sh = this.getSchemaHelper(xmlFormat);
			prtln("\n *** " + xmlFormat + "***");
			SchemaUtils.showDerivedTextOnlyModelNodes(sh);
		}
	}
	
	void showChoices() {
		for (Iterator i = this.getXmlFormats().iterator(); i.hasNext(); ) {
			String xmlFormat = (String) i.next();
			SchemaHelper sh = this.getSchemaHelper(xmlFormat);
			prtln("\n *** " + xmlFormat + "***");
			SchemaUtils.showChoiceNodes(sh);
		}
	}

	/**
	 *  The main program for the MultiFrameworkTally class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		StructureWalker.setDebug(false);
		List formats = new ArrayList();
		/*
		formats.add("sif_activity");
		formats.add("extension-tester");
		formats.add("restriction-tester");
		*/
		
 		formats.add("adn");
		formats.add("ncs_item");
		formats.add("concepts");
		formats.add("mast_demo");
		formats.add("dlese_anno"); 
		formats.add("msp2"); 
/* 		formats.add ("mets");
		formats.add ("choice_tester");*/

		MultiFrameworkTally tally = new MultiFrameworkTally(formats);
		// tally.showDerivedContentModelNodes();
		tally.showChoices();
		// tally.showModelGroups();
		// tally.showDerivedTextOnlyModelNodes();
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("MultiFrameworkTally: " + s);
			System.out.println(s);
		}
	}

}

