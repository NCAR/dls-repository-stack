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
package edu.ucar.dls.harvestmanager.tags;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import edu.ucar.dls.harvest.Config;

import java.io.*;

/**
 * Custom tag that displays a blob that is defined by a byte[]
 * @author dfinke
 *
 */
public class DisplayBlob  extends SimpleTagSupport{
	private byte[] blob = null;
	
	/**
	 * execute the tag
	 */
	public void doTag() throws JspException, IOException {
	    JspWriter out = getJspContext().getOut();
	    out.println(new String(this.blob, Config.ENCODING));
	  }
	
	public void setBlob(byte[] blob) {
		this.blob = blob;
	}

}
