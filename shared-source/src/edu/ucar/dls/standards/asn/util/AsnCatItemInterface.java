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
public interface AsnCatItemInterface {

	/**
	 *  hack to get the status out of the acsrInfo branch, which is a sibling to
	 *  this.element
	 *
	 * @return    The status value
	 */
	public String getStatus();


	/**
	 *  Gets the uid attribute of the AsnCatItem object
	 *
	 * @return    The uid value
	 */
	public String getUid();

	/**
	 *  Gets the key attribute of the AsnCatItem object
	 *
	 * @return    The key value
	 */
	public String getKey();


	/**
	 *  Gets the id attribute of the AsnCatItem object
	 *
	 * @return    The id value
	 */
	public String getId();


	/**
	 *  Gets the title attribute of the AsnCatItem object
	 *
	 * @return    The title value
	 */
	public String getTitle();


	/**
	 *  Gets the created attribute of the AsnCatItem object
	 *
	 * @return    The created value
	 */
	public String getCreated();


	/**
	 *  Gets the topic attribute of the AsnCatItem object
	 *
	 * @return    The topic value
	 */
	public String getTopic();


	/**
	 *  Gets the author attribute of the AsnCatItem object
	 *
	 * @return    The author value
	 */
	public String getAuthor();
	public String getAuthorName();


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toString();

}


