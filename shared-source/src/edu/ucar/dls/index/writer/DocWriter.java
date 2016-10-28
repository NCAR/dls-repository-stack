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

package edu.ucar.dls.index.writer;



import java.io.*;

import edu.ucar.dls.xml.*;

import org.apache.lucene.document.*;



/**

 *  Abstract class for creating a typed Lucene {@link org.apache.lucene.document.Document}. A {@link

 *  edu.ucar.dls.index.reader.DocReader} should be implemented to read {@link

 *  org.apache.lucene.document.Document}s of this type.

 *

 * @author     John Weatherley

 * @see        edu.ucar.dls.index.reader.DocReader

 * @see        edu.ucar.dls.index.ResultDoc

 */

public interface DocWriter {



	/**

	 *  Returns a unique document type key for this kind of document, corresponding to the format type. For

	 *  example "dleseims". The string is parsed using the Lucene {@link

	 *  org.apache.lucene.analysis.standard.StandardAnalyzer} so it must be lowercase and should not contain any

	 *  stop words.

	 *

	 * @return                The docType String

	 * @exception  Exception  This method should throw and Exception with appropriate error message if an error

	 *      occurs.

	 */

	public abstract String getDocType() throws Exception;





	/**

	 *  Gets the name of the concrete {@link edu.ucar.dls.index.reader.DocReader} class that is used to read

	 *  this type of {@link org.apache.lucene.document.Document}, for example

	 *  "edu.ucar.dls.index.reader.ItemDocReader". The class name is used by the {@link

	 *  edu.ucar.dls.index.ResultDoc} factory to return the appropriate DocReader.

	 *

	 * @return    The name of the {@link edu.ucar.dls.index.reader.DocReader}

	 */

	public abstract String getReaderClass();



}



