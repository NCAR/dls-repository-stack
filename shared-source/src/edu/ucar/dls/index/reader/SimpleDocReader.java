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

package edu.ucar.dls.index.reader;



import org.apache.lucene.document.*;

import edu.ucar.dls.index.writer.*;

import edu.ucar.dls.index.*;

import edu.ucar.dls.xml.*;

import edu.ucar.dls.webapps.tools.*;

import edu.ucar.dls.util.*;



import javax.servlet.*;

import java.io.*;

import java.text.*;

import java.util.*;



/**

 *  A bean that provides a simple implementataion of the {@link edu.ucar.dls.index.reader.DocReader}

 *  interface. This DocReader is used when no other is available.

 *

 * @author     John Weatherley

 */

public class SimpleDocReader extends DocReader {



	/**  Constructor for the SimpleDocReader object */

	public SimpleDocReader() { }





	/**  Init method does nothing. */

	public void init() { }





	/**

	 *  Constructor that may be used programatically to wrap a reader around a Lucene {@link

	 *  org.apache.lucene.document.Document} created by a {@link edu.ucar.dls.index.writer.DocWriter}. Sets the

	 *  score to 0.

	 *

	 * @param  doc  A Lucene {@link org.apache.lucene.document.Document} created by a {@link

	 *      edu.ucar.dls.index.writer.DocWriter}.

	 */

	public SimpleDocReader(Document doc) {

		super(doc);

	}





	/**

	 *  Gets a String describing the reader type. This may be used in (Struts) beans to determine which type of

	 *  reader is available for a given search result and thus what data is available for display in the UI. The

	 *  reader type implies which getter methods are available.

	 *

	 * @return    The readerType value.

	 */

	public String getReaderType() {

		return "SimpleDocReader";

	}

}



