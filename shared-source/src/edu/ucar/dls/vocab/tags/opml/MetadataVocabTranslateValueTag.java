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
package edu.ucar.dls.vocab.tags.opml;

import edu.ucar.dls.vocab.MetadataVocab;
import edu.ucar.dls.util.strings.StringUtil;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.*;

/**
 *  Tag handler for retreiving a metadata vocabulary encoded ID tranlation
 *  to/from its metadata name
 *
 * @author    Ryan Deardorff
 */
public class MetadataVocabTranslateValueTag extends MetadataVocabTag {
	String field = "";
	String value = "";

	/**
	 *  Sets the system attribute of the MetadataVocabGetFieldValueUiLabel object
	 *
	 * @param  field  The new fieldId value
	 */
	public void setField( String field ) {
		this.field = field;
	}

	/**
	 *  Sets the system attribute of the MetadataVocabGetFieldValueUiLabel object
	 *
	 * @param  value  The new value value
	 */
	public void setValue( String value ) {
		this.value = value;
	}

	/**
	 *  Start tag
	 *
	 * @return
	 * @exception  JspException
	 */
	public int doStartTag() throws JspException {
		setupTag( pageContext );
		StringBuffer outStr = new StringBuffer();
		try {
			pageContext.getOut().print(
				vocab.getTranslatedValue( metaFormat, metaVersion, field, value ) );
		}
		catch ( Exception e ) {
			System.out.println( "Exception in MetadataVocabTranslateValueTag: " + e.getMessage() );
			try {
				pageContext.getOut().print( "<!-- Exception in MetadataVocabTranslateValueTag: " + e.getMessage()
					 + " -->" );
			}
			catch ( Exception ex ) {}
		}
		return SKIP_BODY;
	}
}

