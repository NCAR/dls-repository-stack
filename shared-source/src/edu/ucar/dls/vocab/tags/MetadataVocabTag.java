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

package edu.ucar.dls.vocab.tags;

import edu.ucar.dls.vocab.MetadataVocab;
import edu.ucar.dls.util.strings.StringUtil;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.PageContext;

/**
 *  Default tag handler for rendering controlled vocabularies. All other vocab
 *  tags should extend this!
 *
 * @author    Ryan Deardorff
 */
public class MetadataVocabTag extends TagSupport {
	/**
	 *  Description of the Field
	 */
	protected String system;
	/**
	 *  Description of the Field
	 */
	protected String interfce;
	/**
	 *  Description of the Field
	 */
	protected String language;
	/**
	 *  Description of the Field
	 */
	protected String group;
	/**
	 *  Description of the Field
	 */
	protected MetadataVocab vocab;
	/**
	 *  Description of the Field
	 */
	protected StringUtil stringUtil = new StringUtil();

	/**
	 *  System is the identifier of a particular app, i.e. "dds", or "dcs"
	 *
	 * @param  system  The new system value
	 */
	public void setSystem( String system ) {
		this.system = system;
	}

	/**
	 *  Interface is a particular UI within a given system. All systems should
	 *  define an interface named "descr" (description). If a different "view" is
	 *  needed for a spot within the app, another interface (such as "banner") can
	 *  be referenced, provided that the corresponding XML files are loaded
	 *  successfully.
	 *
	 * @param  interfce  The new interface value
	 */
	public void setInterface( String interfce ) {
		this.interfce = interfce;
	}

	/**
	 *  Language will allow for support of internationalization. "en-us" indicates
	 *  the United States version of English.
	 *
	 * @param  language  The new language value
	 */
	public void setLanguage( String language ) {
		this.language = language;
	}

	/**
	 *  Group is used to reference a particular spot WITHIN the vocabulary
	 *  hierarchy. Setting to "resourceType" would cause a return of ALL values
	 *  within the "Resource type" vocabulary, whereas "resourceType:Visual" would
	 *  return only those values within the "Visual" sub-group.
	 *
	 * @param  group  The new group value
	 */
	public void setGroup( String group ) {
		this.group = group;
	}

	/**
	 *  Get the vocab object from the page context and expand system to be a
	 *  concatenation of system, interface, and language
	 *
	 * @param  pageContext
	 * @exception  JspException
	 */
	public void setupTag( PageContext pageContext ) throws JspException {
		vocab = (MetadataVocab)pageContext.findAttribute( "MetadataVocab" );
		if ( vocab == null ) {
			throw new JspException( "Vocabulary not found" );
		}
		else {
			if ( group != null ) {
				group = stringUtil.replace( group, " ", "_", false );
			}
			else {
				group = "";
			}
			system = system + "." + interfce + "." + language;
		}
	}

	/**
	 *  Description of the Method
	 */
	public void release() {
		super.release();
	}
}

