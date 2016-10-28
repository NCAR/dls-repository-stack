package edu.ucar.dls.standards.asn.util;

import java.util.*;
import org.dom4j.*;

/**
 *
 * @author    Jonathan Ostwald
 */
public interface AsnCatalogInterface {


	/**
	 *  Gets the item attribute of the AsnCatalog object
	 *
	 * @param  asnId  NOT YET DOCUMENTED
	 * @return        The item value
	 */
	public AsnCatItemInterface getItem(String asnId);


	/**
	 *  Gets the status attribute of the AsnCatalog object
	 *
	 * @param  asnId  NOT YET DOCUMENTED
	 * @return        The status value
	 */
	public String getStatus(String asnId);

	/**
	 *  Gets the subjects attribute of the AsnCatalog object
	 *
	 * @return    The subjects value
	 */
	public List getSubjects();

	/**
	 *  Gets the subjectItems attribute of the AsnCatalog object
	 *
	 * @return    The subjectItems value
	 */
	public Map getSubjectItemsMap();

}

