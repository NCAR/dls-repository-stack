package edu.ucar.dls.standards.asn.util;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.standards.asn.AsnDocKey;
import org.dom4j.*;
import java.io.*;

/**
 *  Represents a single ASN Catalog item (an ASN Document) with attributes
 *  obtained from mapping file
 *
 * @author    Jonathan Ostwald
 */
public class AsnCatItemNew implements AsnCatItemInterface {

	private static boolean debug = false;
	
	Element element = null;
	String id;
	String title;
	String created;
	String topic;
	String author;
	String authorName;
	String key;
	String uid;
	String status;


	/**
	 *  Constructor for the AsnCatItemNew object
	 *
	 * @param  element  NOT YET DOCUMENTED
	 */
	public AsnCatItemNew(Element element) {
		this.element = element;
		this.id = this.element.attributeValue("id");
		this.title = this.getSubElementValue("title");
		this.created = this.getSubElementValue("created");
		this.topic = this.getSubElementValue("topic");
		this.author = this.getSubElementValue("author");
		this.authorName = this.getSubElementValue("authorName");
		this.status = this.getSubElementValue("status");
	}


	/**
	 *  hack to get the status out of the acsrInfo branch, which is a sibling to
	 *  this.element
	 *
	 * @return    The status value
	 */
	public String getStatus() {
		return this.status;
	}


	/**
	 *  Gets the uid attribute of the AsnCatItemNew object
	 *
	 * @return    The uid value
	 */
	public String getUid() {

		if (this.uid == null) {
			try {
				this.uid = new File(this.id).getName();
			} catch (Throwable t) {
				prtln("makeAsnDocKey error (" + t.getMessage() + ") - will use full purl id for uid");
				this.uid = "???";
			}
		}
		return this.uid;
	}


	/**
	 *  Gets the key attribute of the AsnCatItemNew object
	 *
	 * @return    The key value
	 */
	public String getKey() {
		if (this.key == null) {
			AsnDocKey docKey = new AsnDocKey(this.author, this.topic, this.created, this.getUid());
			this.key = docKey.toString();
		}
		return this.key;
	}


	/**
	 *  Gets the id attribute of the AsnCatItemNew object
	 *
	 * @return    The id value
	 */
	public String getId() {
		return this.id;
	}


	/**
	 *  Gets the title attribute of the AsnCatItemNew object
	 *
	 * @return    The title value
	 */
	public String getTitle() {
		return this.title;
	}


	/**
	 *  Gets the created attribute of the AsnCatItemNew object
	 *
	 * @return    The created value
	 */
	public String getCreated() {
		return this.created;
	}


	/**
	 *  Gets the topic attribute of the AsnCatItemNew object
	 *
	 * @return    The topic value
	 */
	public String getTopic() {
		return this.topic;
	}


	/**
	 *  Gets the author attribute of the AsnCatItemNew object
	 *
	 * @return    The author value
	 */
	public String getAuthor() {
		return this.author;
	}

	public String getAuthorName() {
		if (this.authorName != null)
			return this.authorName;
		return this.getAuthor();
	}

	/**
	 *  Gets the subElementValue attribute of the AsnCatItemNew object
	 *
	 * @param  tag  NOT YET DOCUMENTED
	 * @return      The subElementValue value
	 */
	String getSubElementValue(String tag) {
		try {
			return this.element.element(tag).getTextTrim();
		} catch (Exception e) {
			prtln("getSubElementValue error: " + e.getMessage());
		}
		return "";
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toString() {
		String NL = "\n\t";
		String s = this.id;
		s += NL + "title: " + this.title;
		s += NL + "created: " + this.created;
		s += NL + "topic: " + this.topic;
		s += NL + "author: " + this.author;
		s += NL + "status: " + this.status;
		return s;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "AsnCatItemNew");
		}
	}

}


