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