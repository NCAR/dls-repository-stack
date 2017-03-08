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
package edu.ucar.dls.schemedit.test;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.compositor.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.util.strings.*;

import java.io.*;
import java.util.*;
import java.text.*;
import java.util.regex.*;

import java.net.*;
import org.dom4j.Node;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.Namespace;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.sun.msv.datatype.xsd.*;
import org.relaxng.datatype.*;

/**
 *  Description of the Class
 *
 * @author    ostwald
 */
public class SimpleSchemaHelperTester {
	public GlobalDefMap globalDefMap = null;
	public Document instanceDocument = null;
	public Document sampleDocument = null;
	public XSDatatypeManager xsdDatatypeManager = null;
	public SchemaNodeMap schemaNodeMap = null;
	public DefinitionMiner definitionMiner;
	public XMLWriter writer;
	public SchemaHelper sh;
	// Map schemaMap = null;
	public String schemaName;
	String xmlFormat;

	public SimpleSchemaHelperTester (String schemaName) {
		this.schemaName = schemaName;
		SchemaRegistry sr = new SchemaRegistry();

		SchemaRegistry.Schema schema = (SchemaRegistry.Schema)sr.getSchema(schemaName);
		if (schema == null) {
			prtln ("Schema not found for \"" + schemaName + "\"");
			System.exit(1);
		}

		String path = schema.path;
		String rootElementName = schema.rootElementName;
		SimpleSchemaHelperTester t = null;
		try {
			if (path.indexOf ("http:") == 0) {
				URL	schemaUrl = new URL (path);
				sh = new SchemaHelper(schemaUrl, rootElementName);
				// sh = new SchemaHelper(schemaUrl);
			}
			else {
				prtln ("path: " + path);
				sh = new SchemaHelper (new File (path), rootElementName);
			}

			if (sh == null) {
				throw new Exception ("\n\n ** schemaHelper not instantiated **");
			}
			else {
				prtln("SchemaHelper instantiated");
			}
			schemaNodeMap = sh.getSchemaNodeMap();
			globalDefMap = sh.getGlobalDefMap();
			instanceDocument = sh.getInstanceDocument();
			writer = Dom4jUtils.getXMLWriter();

			xsdDatatypeManager = sh.getXSDatatypeManager();

		} catch (Exception e) {
			prtln ("failed to instantiate SimpleSchemaHelperTester: " + e.getMessage());
			// e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 *  The main program for the SimpleSchemaHelperTester class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) throws Exception {
		boolean truncated = true;

		String schemaName = "cow_item";

		if (args.length > 0)
			schemaName = args[0];

		prtln ("\n-------------------------------------------------");
		prtln ("SimpleSchemaHelperTester (" + schemaName + ")\n");

		SimpleSchemaHelperTester t = new SimpleSchemaHelperTester(schemaName);
		SchemaNodeMap.setDebug(false);
		SchemaUtils.showInstanceDoc (t.sh);
 		
		// SchemaUtils.showSchemaNodeMap(t.sh);
		// SchemaUtils.showGlobalDefs(t.sh);

		// SchemaUtils.showMinimalDocument(t.sh);
		// t.testSampleDoc();

		// t.recursiveNodes();

		if (t.schemaName.equals("nsdl_anno"))
			t.ndslAnnoTester();

		if (t.schemaName.equals("msp2"))
			t.msp2Tester();

		if (t.schemaName.equals ("sif_activity"))
			t.activitiesTester();

		if (t.schemaName.equals ("concepts")) {

		}

		if (t.schemaName.equals ("smile_item")) {
			t.smileTester();
		}

		if (t.schemaName.equals ("res_qual")) {

		}

		if (t.schemaName.equals ("osm")) {
			t.osmTester();
		}

		if (t.schemaName.equals("dlese_anno")) {
			t.annoTester();
		}

		if (t.schemaName.equals("ncs_collect")) {
			t.ncsCollectTester();
		}

		if (t.schemaName.equals("adn"))
			t.adnTester();

		if (t.schemaName.equals("math_path"))
			t.mathPathTester();

		if (t.schemaName.equals("mets")) {
			try {
				t.metsTester();
			} catch (Throwable e) {
				prtln  ("metsTester error: " + e.getMessage());
			}
		}
		
		if (t.schemaName.equals("lar")) {
			try {
				t.larTester();
			} catch (Throwable e) {
				prtln  ("LAR Tester error: " + e.getMessage());
			}
		}
		
		if (t.schemaName.equals("cow_item")) {
			try {
				t.cowItemTester();
			} catch (Throwable e) {
				prtln  ("cow_item Tester error: " + e.getMessage());
			}
		}
		
		if (truncated) {
			prtln ("\nSimpleSchemaHelperTester truncated and exiting now ...");
			System.exit(1);
		}

		t.testSampleDoc();

	}
	
	void cowItemTester () throws Exception {
		String asnPath = "/record/educational/benchmarks/asnId";
		SchemaNode schemaNode = this.sh.getSchemaNode(asnPath);
/* 		prtln (schemaNode.toString());
		List attributeNames = schemaNode.getAttributeNames();
		prtln ("Attribute Names");
		for (Iterator i=attributeNames.iterator();i.hasNext();)
			prtln ("- " + (String)i.next()); */
/* 		GlobalDef typeDef = schemaNode.getTypeDef();
		prtln ("TypeDef");
		prtln (typeDef.toString()); */
/* 		Node instanceNode = this.sh.getInstanceDocNode(asnPath);
		if (instanceNode != null)
			pp (instanceNode);
		else
			prtln ("instance Node not found for " + asnPath); */
		List attributeNames = this.sh.getAttributeNames(asnPath);
		prtln ("Attribute Names");
		for (Iterator i=attributeNames.iterator();i.hasNext();)
			prtln ("- " + (String)i.next());
		
	}
	
	void larTester()  throws Exception {
		prtln ("LAR TESTER");
		String xpath = "/record/educationLevel";
		// String xpath = "/record/type";
		
		SchemaNodeMap schemaNodeMap = this.sh.getSchemaNodeMap();
/* 		prtln ("\nSCHEMA PATHS");
		for (Iterator i=schemaNodeMap.getKeys().iterator();i.hasNext();) {
			String path = (String)i.next();
			prtln ("- \"" + path + "\"");
		} */
		
		SchemaNode schemaNode = this.sh.getSchemaNode(xpath);
		if (schemaNode == null)
			throw new Exception ("schemaNode not found for xpath: " + xpath);
		prtln ("schemaNode FOUND for " + xpath);
		//boolean isRequiredBranch = this.sh.isRequiredBranch(schemaNode);
		// prtln ("isRequiredBranch: " + isRequiredBranch);
		
		boolean isRequiredContent = this.sh.isRequiredContentElement(schemaNode, this.xmlFormat);
		prtln ("isRequiredContent: " + isRequiredContent);
	}
	
	void mathPathTester() {
		prtln ("MATH PATH TESTER");
		String path = "C:/Documents and Settings/ostwald/devel/dcs-instance-data/local-ndr/records/math_path/math_path/MP-000-000-000-006.xml";
		Document doc = null;
		try {
			doc = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(new File (path)));
		} catch (Throwable t) {
			prtln ("ERROR: " + t);
			return;
		}
		DocMap docMap = new DocMap (doc, this.sh);
		// String xpath = "/record/educational/contentBundle[1]/@description";
		String xpath = "/record/educational/contentBundle[1]/@description";
		// xpath = XPathUtils.normalizeXPath(xpath);
		prtln ("xpath: " + xpath);
		if (!docMap.nodeExists(xpath)) {
			prtln ("Path doesn't exist!");
		}
		else
			prtln ("path EXISTS!");
	}

	void metsTester() {
		SchemaUtils.showDerivedTextOnlyModelNodes (this.sh);
/* 		List nodes = SchemaUtils.getDerivedTextOnlyNodes(this.sh);
		prtln ("Derived Text Only Nodes");
		for (Iterator i=nodes.iterator();i.hasNext();) {
			SchemaNode schemaNode = (SchemaNode)i.next();
			// prtln (schemaNode.getXpath());
			prtln (schemaNode.toString());
		} */
	}

	void ncsCollectTester () throws Exception {
		String path = "/record/collection/ingest";
		prtln ("\nPATH: " + path);
		SchemaNode schemaNode = this.sh.getSchemaNode(path);
		boolean isChoiceElement = this.sh.isChoiceElement(schemaNode);
		prtln ("is choice element? " + isChoiceElement);

		boolean hasChoiceCompositor = this.sh.hasChoiceCompositor(schemaNode);
		prtln ("has choice compositor? " + hasChoiceCompositor);

		Choice choiceCompositor = (Choice)schemaNode.getCompositor();
		boolean isRequiredChoice = choiceCompositor.isRequiredChoice();
		prtln ("is REQUIRED choice? " + isRequiredChoice);
	}

	void ndslAnnoTester () throws Exception {
		prtln ("minny");

		Document minDoc = this.sh.getMinimalDocument();
		DocMap docMap = new DocMap (minDoc, this.sh);
		pp (minDoc);
 		String path = "/nsdl_anno/structuredOutline";
		Element newElement = this.sh.getNewElement(path);
		if (newElement == null) {
			throw new Exception("getNewElement failed");
		}
		else {
			// prtln (" ... calling docMap.addElement()");
			if (!docMap.addElement(newElement, path)) {
				throw new Exception("docMap.addElement failed");
			}
		}
		pp (minDoc);

	}

	void smileTester () throws Exception {
		prtln ("SmileTester");
		String myDocPath = "C:/tmp/SMILE-14-000-000-000-001.xml";
		// Document myDoc = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(new File (myDocPath)))
		Document myDoc = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(new File (myDocPath)));
		pp (myDoc);

		String xpath = "/smileItem/authorshipRights/copyright/license";
		SchemaNode schemaNode = sh.getSchemaNode(xpath);
		if (schemaNode == null)
			prtln ("schemaNode NOT found for " + xpath);
		else
			prtln ("schemaNode FOUND for " + xpath);
		Compositor compositor = sh.getCompositor(schemaNode);
		prtln (compositor.toString());

		if (sh.isMultiSelect(schemaNode)) {
			prtln (xpath + " is a multiSelect node!?");
		}
		else {
			prtln (xpath + " is NOT a multiSelect node");
		}

	}

	void osmTester () throws Exception {
		// String path = "/record/rights/publisherTermsOfUse";
		// String path = "/record/rights/access";
		String path = "/record/general/pubName";
		SchemaNode schemaNode = getSchemaNode (path);
		if (schemaNode == null)
			throw new Exception ("schemaNode not found for " + path);
		prtln (schemaNode.toString());
		prtln ("isRequiredBranch: " + sh.isRequiredBranch(schemaNode));
		prtln ("isRequiredContent: " + sh.isRequiredContentElement(schemaNode, this.xmlFormat));
		prtln ("is enumeration: " + this.isEnumerationType(path));
		prtln ("validating type: " + schemaNode.getValidatingType().getName());
	}

	void msp2Tester_1 () throws Exception {
		String path = "/record/general/subjects/educationalSubject";
		SchemaNode schemaNode = getSchemaNode (path);
		if (schemaNode == null)
			throw new Exception ("schemaNode not found for " + path);
		prtln (schemaNode.toString());

	}

	void msp2Tester () throws Exception {
		String path = "/record/lifecycle/rights";
		SchemaNode schemaNode = getSchemaNode (path);
		if (schemaNode == null)
			throw new Exception ("schemaNode not found for " + path);
		prtln (schemaNode.toString());
		if (schemaNode.isRequired())
			prtln ("REQUIRED");
		else
			prtln ("NOT required");
		if (this.xmlFormat.equals("adn") && sh.isRequiredContentElement(schemaNode, this.xmlFormat)) {
			prtln ("requiredContentElement");
		}
		else {
			prtln ("NOT requiredContentElement");
		}
	}

	void metsTesterOLD () throws Exception {
		String parentPath = "/this:mets/this:amdSec[1]";
		// SchemaNode schemaNode = this.sh.getSchemaNode (parentPath);
		// Sequence comp = (Sequence)schemaNode.getCompositor();
		Compositor comp = this.sh.getCompositor(parentPath);
		comp.printInstanceNames();
		comp.printLeafMemberNames();
		String docPath = "C:/Documents and Settings/ostwald/devel/dcs-instance-data/ndr/records/mets/1216839452217/METS-TEST-000-000-000-003.xml";
		Document instanceDoc = Dom4jUtils.getXmlDocument(new File(docPath));
		Element parent = (Element)instanceDoc.selectSingleNode (parentPath);
		if (parent == null) {
			throw new Exception ("parent not found");
		}
		else {
			boolean accepts = comp.acceptsNewMember(parent, "this:techMD", 1);
		}
	}


	void activitiesTester () {
		String typeName = "sif:SIF_ExtendedElementsType";
		GlobalDef globalDef = this.sh.getGlobalDef(typeName);
		pp (globalDef.getElement());
	}

	void annoTester1 () {
		prtln ("\n--------------------\nannoTester");
		Document sampleDoc = makeSampleDoc();
		DocMap docMap = new DocMap (sampleDoc, this.sh);
		String moreInfoPath = "/annotationRecord/moreInfo";
		SchemaNode moreInfoNode = this.sh.getSchemaNode(moreInfoPath);
		if (moreInfoNode == null) {
			prtln ("moreInfoNode not found");
			return;
		}
		GlobalDef moreInfoTypeDef = moreInfoNode.getTypeDef();
		// prtln (moreInfoTypeDef.toString());
		try {
			// docMap.createNewNode(moreInfoPath);
		} catch (Throwable t) {
			prtln (t.getMessage());
			return;
		}
		String path = "/annotationRecord/moreInfo/any";

		prtln ("\nSchemaNode for " + path);
		SchemaNode schemaNode = this.sh.getSchemaNode(path);
		if (schemaNode == null)
			prtln ("schemaNode not found");
		else
			prtln (schemaNode.toString());
		try {
			docMap.createNewNode("/annotationRecord/moreInfo/any");
		} catch (Throwable t) {
			prtln ("ERROR: " + t.getMessage());
			t.printStackTrace();
			return;
		}
	}

	void annoTester2 () {
		prtln ("\n--------------------\nannoTester");
		Document sampleDoc = makeSampleDoc();
		DocMap docMap = new DocMap (sampleDoc, this.sh);
		String moreInfoPath = "/annotationRecord/moreInfo";
		SchemaNode moreInfoNode = this.sh.getSchemaNode(moreInfoPath);
		if (moreInfoNode == null) {
			prtln ("moreInfoNode not found");
			return;
		}
		GlobalDef moreInfoTypeDef = moreInfoNode.getTypeDef();
		// prtln (moreInfoTypeDef.toString());
		Compositor compositor = moreInfoNode.getCompositor();
		// prtln (compositor.toString());
		prtln ("compositor has " + compositor.getMembers().size() + " members");
		prtln (compositor.getMemberAt(0).getElement().asXML());
		prtln (compositor.getMemberAt(0).getQualifiedName());
	}

	void annoTester () {
		prtln ("\n--------------------\nannoTester");
		Document sampleDoc = makeSampleDoc();
		DocMap docMap = new DocMap (sampleDoc, this.sh);
		String anyPath = "/annotationRecord/moreInfo/any";
		SchemaNode anyNode = this.sh.getSchemaNode(anyPath);
		if (anyNode == null) {
			prtln ("anyNode not found");
			return;
		}
		GlobalDef anyTypeDef = anyNode.getTypeDef();

		if (anyTypeDef.isBuiltIn()) {
			prtln ("BUILT_IN");
			if (anyTypeDef.isAnyType())
				prtln ("ANY TYPE");
			else
				prtln ("NOT any type (" + anyTypeDef.getName() + ")");
		}
		if (sh.isRepeatingElement(anyNode))
			prtln ("REPEATING");
		else
			prtln ("SINGLETON");
		// prtln (moreInfoTypeDef.toString());

	}

	SchemaNode getSchemaNode (String xpath) {
		return sh.getSchemaNode(xpath);
	}


	void enumTester () {
		String typeName = "roleAnnotationType";
		List valueList = this.sh.getEnumerationValues(typeName, false);
		if (valueList == null) {
			prtln ("no values found for " + typeName);
			return;
		}
		prtln (typeName + " values");
		for (Iterator i=valueList.iterator();i.hasNext();) {
			prtln ((String)i.next());
		}
	}

	void recursiveNodes () {
		prtln ("Recursive Nodes");
		List recursiveNodes = SchemaUtils.getRecursiveNodes(sh);
		for (Iterator i=recursiveNodes.iterator();i.hasNext();){
			SchemaNode schemaNode = (SchemaNode)i.next();
			String xpath = schemaNode.getXpath();
			prtln ("\n" + xpath);
			// prtln (choice.toString());
		}
	}


	void displayChoices () {
		prtln ("Choices");
		List choicePaths = SchemaUtils.getChoicePaths(sh);
		for (Iterator i=choicePaths.iterator();i.hasNext();){
			String xpath = (String)i.next();
			SchemaNode schemaNode = sh.getSchemaNode (xpath);
			Choice choice = (Choice) schemaNode.getCompositor();
			prtln ("\n" + xpath);
			prtln (schemaNode.getTypeDef().getName());
			if (choice.isRequiredChoice())
				prtln ("\tREQUIRED");
			else
				prtln ("\tOPTIONAL");
			prtln ("\tminOccurs: " + choice.getMinOccurs());
			prtln ("\tmaxOccurs: " + choice.getMaxOccurs());
			for (Iterator j = choice.getMembers().iterator();j.hasNext(); ) {
				CompositorMember member = (CompositorMember) j.next();
				String m = "\t - " + member.getInstanceQualifiedName();
				m += " -  maxOccurs: " + member.maxOccurs + "  minOccurs: " + member.minOccurs;
				prtln (m);
			}
			// prtln (choice.toString());
		}
	}

	void adnTester () {
		String xpath = "/itemRecord/temporalCoverages/timeAndPeriod_${timeAndPeriodIndex+1}_/periods/period";
		String normalized = edu.ucar.dls.schemedit.autoform.RendererHelper.normalizeXPath(xpath);
		prtln ("normalized: " + normalized);
		SchemaNode schemaNode = sh.getSchemaNode(xpath);
		if (schemaNode == null)
			prtln ("schemaNode not found for " + xpath);
		else
			prtln ("schemaNode found");
	}


	void getNewElementTester (String path) {
		prtln ("\n getNewElementTester with " + path);
		sh.getNewElement(path);
	}

	void schemaNodeTester (String path) {
		prtln ("\nschemaNodeTester with " + path);
		SchemaNode schemaNode = this.sh.getSchemaNode(path);
		if (schemaNode == null) {
			prtln ("\t schemaNode not found");
			return;
		}

		prtln ("isRepeatingElement: " + this.sh.isRepeatingElement(schemaNode));
	}

	/**
	* Assumptions
	* - RootElement has ComplexType
	* - If a globalElement has a member name of another globalElement, it CAN't be the root.

	* Algorithm
	* 1 - find the globalElements defined in the targetNameSpace (they must have a compositor?)
	* 2 - for each ComplexType in the GlobalDefMap, if the compositor contains a globalMember
	*     having the name of a globalElement, that globalElement is eliminated from consideration
	* 3 - if there is still more than one root candidate we are in trouble ...
	*
	* WHAT ABOUT global elements that are defined but never referenced ...?
	*/
	void findRootElement() {
		prtln (sh.getDefinitionMiner().getNamespaces().toString());
		List globalElements = this.sh.getGlobalDefMap().getDefsOfType(GlobalDef.GLOBAL_ELEMENT);
		prtln ("GlobalElements (" + globalElements.size() + " found)");

		// find the globalElements defined in the targetNamespace of ComplexType
		List candidates = new ArrayList();
		for (Iterator i=globalElements.iterator();i.hasNext();) {
			GlobalElement globalElement = (GlobalElement)i.next();
			String typeName = globalElement.getType();
			//prtln (globalElement.toString());
			prtln ("\n-------\nglobalElement: " + globalElement.getQualifiedInstanceName() +
				"typeName: " + typeName);
			Namespace ns = sh.getDefinitionMiner().getNamespaces().getTargetNamespace();
			GlobalDef globalDef = sh.getGlobalDefMap().getValue (typeName, ns);
			prtln ("\nglobalDef: " + globalDef.getQualifiedInstanceName());
			if (globalDef.isComplexType()) {
				candidates.add (globalElement.getQualifiedInstanceName());
			}
		}
		prtln (candidates.size() + " candidates found");
		prtln ("\nscanning complex types ...");
		for (Iterator i=sh.getGlobalDefMap().getDefsOfType(GlobalDef.COMPLEX_TYPE).iterator();i.hasNext();) {
			ComplexType cType = (ComplexType)i.next();
			prtln ("\n- cType: " + cType.getQualifiedInstanceName());
			try {
				List members = cType.getCompositor().getMemberNames();
				prtln ("members");
				for (Iterator m=members.iterator();m.hasNext();) {
					String member = (String)m.next();
					prtln ("\t" + member);
					if (candidates.contains(member)) {
						prtln ("\t ...candidate eliminated");
						candidates.remove(member);
					}
				}

			} catch (Throwable t) {
				prtln ("couldnt get member names: " + t.getMessage());
			}
		}
		prtln ("\n" + candidates.size() + " candidates remain");
	}

	void testSampleDoc() {
		Document sampleDoc = makeSampleDoc();
		prtln ("\nSAMPLE DOC\n" + Dom4jUtils.prettyPrint(sampleDoc));

		String validationMsg = edu.ucar.dls.xml.XMLValidator.validateString(sampleDoc.asXML(), true);
		if (validationMsg == null)
			prtln ("Sample Doc is VALID");
		else
			prtln (validationMsg);
	}

	boolean isEnumerationType (String path) {
		SchemaNode schemaNode = sh.getSchemaNode(path);
		if (schemaNode == null) {
			prtln ("schemaNode not found for " + path);
			return false;
		}
		GenericType typeDef = null;
		try {
			typeDef = (GenericType) schemaNode.getTypeDef();
			prtln (typeDef.getElementAsXml());
		} catch (Throwable t) {
			prtln ("ERROR: " + t.getMessage());
			return false;
		}

		return typeDef.isEnumerationType();
	}

	Document makeSampleDoc () {
		// DocMap docMap = new DocMap ((Document)sh.getInstanceDocument().clone());
		DocMap docMap = new DocMap (sh.getMinimalDocument());
		for (Iterator i=sh.getSchemaNodeMap().getKeys().iterator();i.hasNext();) {
			String path = (String)i.next();
			if (docMap.selectSingleNode(path) == null) {
				// prtln (" ... docMap node not found for " + path);
				continue;
			}
			SchemaNode schemaNode = sh.getSchemaNode(path);
			// String typeName = schemaNode.getDataTypeName();


			try {
				putSampleDocValue (docMap, schemaNode, path);
			} catch (Exception e) {
				prtln ("smartPut boo-boo: " + e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
		return docMap.getDocument();
	}

	void putSampleDocValue (DocMap docMap, SchemaNode schemaNode, String path) throws Exception {
		String typeName = schemaNode.getTypeDef().getName();
		// prtln ("putSampleDocValue for " + path + " (" + typeName + ")");
		GlobalDef globalDef = sh.getGlobalDef(schemaNode);

		if (typeName.equals("xs:string")) {
			docMap.smartPut(path, XPathUtils.getNodeName(path));
		}
		else if (typeName.equals("xs:date")) {
			docMap.smartPut(path, "2005-01-01");
		}
		else if (globalDef.isSimpleType() && ((SimpleType)globalDef).isEnumerationType()) {
			SimpleType simpleType = (SimpleType) globalDef;
			//prtln ("getting an enumeration value for " + path);
			String val = (String)simpleType.getEnumerationValues(false).get(0);
			docMap.smartPut(path, val);
		}
		else {
			//prtln (typeName + " (" + path + ")");
		}
	}


	void doStatusReportNodeProbes () {
		nodeProbe ("/statusReport");
		nodeProbe ("/statusReport/project");
		nodeProbe ("/statusReport/project/@urgency");
	}

	void doStatusReportNodeNSProbes () {
		nodeProbe ("/sr:statusReport");
		nodeProbe ("/sr:statusReport/sr:project");
		nodeProbe ("/sr:statusReport/sr:id");
		nodeProbe ("/sr:statusReport/sr:project/@urgency");
	}

	void doCdNodeProbes () {
		nodeProbe ("/cd:cd");
		nodeProbe ("/cd:cd/@nameTitle");
		nodeProbe ("/cd:cd/@tp:rating");
		nodeProbe ("/cd:cd/cd:id");
		nodeProbe ("/cd:cd/cd:media/sh:album");
		nodeProbe ("/cd:cd/cd:media/sh:album/@key");
		nodeProbe ("/cd:cd/cd:info");
		nodeProbe ("/cd:cd/@tp:rating");
/* 		nodeProbe ("/cd:cd/cd:tape/@newAttr");
		nodeProbe ("/cd:cd/cd:tape/@tp:newAttr");
		nodeProbe ("/cd:cd/cd:tape/@newAttr");
		nodeProbe ("/cd:cd/cd:tape/@tp:newAttr"); */
	}
	void nodeProbe (String path) {
		// Document doc = sh.getInstanceDocument();
		Document doc = sh.getMinimalDocument();
		Node n = doc.selectSingleNode (path);
		if (n == null)
			prtln("node NOT found at " + path);
		else
			prtln ("node FOUND at " + path);
	}

	void nodeTester (String path) {
		prtln ("\nnodeTester with " + path);
		SchemaNode schemaNode = sh.getSchemaNode(path);
		if (schemaNode == null) {
			prtln ("schemaNode not found for " + path);
			return;
		}
		prtln (schemaNode.toString());
	}

	void showEnumValues (SimpleType simpleType) {
		List enums = simpleType.getEnumerationValues();
		prtln ("\n enumerations");
		if (enums == null)
			prtln ("\t null!");
		else {
			prtln ("there are " + enums.size() + " enum values");
			for (Iterator i=enums.iterator();i.hasNext();)
				prtln ("\t" + (String)i.next());
		}
	}

	/**
	* find a GlobalDef and print out key attributes
	*/
	void typeTester (String path) {
		prtln ("\ntypeTester with " + path);
		SchemaNode schemaNode = sh.getSchemaNode(path);
		if (schemaNode == null) {
			prtln ("schemaNode not found for " + path);
			return;
		}
		GlobalDef def = null;
		try {
			def = (GlobalDef) schemaNode.getTypeDef();
		} catch (Throwable t) {
			prtln ("ERROR: " + t.getMessage());
			return;
		}
		String s = "";
		String [] pathSplits = def.getClass().getName().split("\\.");
		String className = def.getClass().getName();
		if (pathSplits.length > 0)
			className = pathSplits[pathSplits.length -1];
		s +=  "\n\t" + def.getName() + "  (" + className + ")";
		s += "\n\t\t qualified name: " + def.getQualifiedName() ;
		s += "\n\t\t qualified instance name: " + def.getQualifiedInstanceName() ;
		s += "\n\t\t namespaceURI: " + def.getNamespace().getURI();
		try {
			s += "\n\t\t location: " + def.getLocation();
			s += "\n\t\t schemaReader source: " + def.getSchemaReader().getLocation().toString();
		} catch (Throwable t) {}
		s += "\n" + def.getElementAsXml();
		prtln ( s );

		if (def.isSimpleType()) {
			SimpleType simpleType = (SimpleType)def;

			showEnumValues (simpleType);

			if (simpleType.isComboUnionType())
				prtln ("COMBO UNION TYPE!");
			else
				prtln ("NOT comboUnionType");
		}

		prtln ("Testing the following against \"isEnumerationType\"");
		prtln ("\t def: " + this.sh.isEnumerationType(def));
		prtln ("\t schemaNode.getValidatingType: " + sh.isEnumerationType(schemaNode.getValidatingType()));
		prtln ("\t xsiTypeType: " + this.sh.isEnumerationType("xsiTypeType"));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  o  Description of the Parameter
	 */
	public void write(Object o) {
		try {
			writer.write(o);
			prtln("");
		} catch (Exception e) {
			prtln("couldn write");
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  node  Description of the Parameter
	 */
	public static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	public static void prtln(String s) {
		// System.out.println("SimpleSchemaHelperTester: " + s);
		System.out.println(s);
	}


}
