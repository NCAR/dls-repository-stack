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
package edu.ucar.dls.harvest.processors.record;

import javax.xml.bind.annotation.XmlRootElement;

import edu.ucar.dls.harvest.workspaces.Workspace;


/**
 *  Class that fixes xml when the escaping was done wrong. For example
 *  &amp;gt; Pretty print will not decode this because &amp is special char
 *  in xml. In reality this should be &gt; This class takes care of some of the
 *  cases. &amp;amp;, &amp;apos;, &amp;gt;, &amp;lt; &ampl;&quot. This list
 *  was taken from the old ingest code. Might want to add some more if they 
 *  are found
 */

@XmlRootElement(name="EncodedCharsTransform")
public class EncodedCharsTransform extends RecordProcessor{
	private String[] ampChecks = {"amp", "apos", "gt", "lt", "quot", "#039"};

	
	/**
	 * Run method that takes the record and decodes mistakes in escaping
	 * ie &amp;gt; -> &gt;. Read class defintion for a more in depth 
	 * explanation.
	 * @param recordId record id of the record being processed
	 * @param record record xml as a string
	 * @param workspace the workspace that is being used for harvesting
	 */
	public String run(String recordId, String record)
	{
		String strCheckFor = null;
		String strReplaceTo = null;
		
		String formatedRecord = new String(record);
		for(String ampCheck : ampChecks)
		{
			strCheckFor = String.format("&amp;%s;", ampCheck);
			strReplaceTo = String.format("&%s;", ampCheck);
			formatedRecord = formatedRecord.replaceAll(strCheckFor, strReplaceTo);
		}
		// Only if it changed return it otherwise return null, so we don't waste
		// a update to the record
		if (!formatedRecord.equals(record))
			return formatedRecord;
		return null;
	}
}
