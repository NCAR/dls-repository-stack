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
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.Config.DataFormats;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.tools.JsonUtil;
import edu.ucar.dls.harvest.workspaces.Workspace;


/**
 * Processor that enables one to convert from a format to another one. If other
 * formats are needed they must be added to this processor. This is not a everything
 * will work processor each coversion (source-> format) must be implemented. But if
 * so desired these may be chained. So if one implemented a JSON->XML and then a 
 * XML->HTML. You could chain them to create a JSON->HTML. Even though it would be
 * better to implement it here.
 */
@XmlRootElement(name="DataFormatTransform")
public class DataFormatTransform extends RecordProcessor{
	private String sourceDataFormat = null;
	private String targetDataFormat = null;
	private String transformation = null;
	private String ERROR_TRANSFORMING_DOC_MSG = "Error trying to transform from format %s "+
	"to format %s.";
	
	
	private static String TRANSFORMATION_STRING_FORMAT = "%s:%s";
	private static String JSON_TO_XML = String.format(TRANSFORMATION_STRING_FORMAT, 
			DataFormats.JSON, DataFormats.XML);
	
	// Valid transformations. After one is implented you must add it to this list so
	// initialize will not throw an error
	private static String [] VALID_TRANSFORMATIONS = {
		JSON_TO_XML};
	
	/**
	 * initialize method that makes sure that a source-data-format and a 
	 * target-data-format are specified. And that that conversion is implemented
	 * withing the processor via VALID_TRANSFORMATIONS
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		if(this.sourceDataFormat==null || this.targetDataFormat==null)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"source-data-format and target-data-format are required attributes for DataFormatTransform", 
					"DataFormatTransform.initialize()");
		
		this.transformation = String.format(
				TRANSFORMATION_STRING_FORMAT, this.sourceDataFormat, this.targetDataFormat);
		List<String> validTransformationsList = Arrays.asList(VALID_TRANSFORMATIONS);
		if(!validTransformationsList.contains(this.transformation))
		{
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					String.format("Ttransformation %s to %s has not been implemented yet.", 
							this.sourceDataFormat, this.targetDataFormat), 
					"DataFormatTransform.initialize()");
		}
	}
	
	/**
	 * Does the conversion, and returns the results
	 * @param recordId record id of the record being processed
	 * @param record record xml as a string
	 * @param workspace the workspace that is being used for harvesting
	 */
	public String run(String documentId, String record) throws HarvestException
	{
		
		String output = record;
		try {
			if(this.transformation.equals(JSON_TO_XML))
			{
				output = JsonUtil.convertToXML(record);
			}
		}
		catch(Exception e)
		{
			this.addError(documentId, String.format(ERROR_TRANSFORMING_DOC_MSG, 
					this.sourceDataFormat, this.targetDataFormat));
		}
		if(!output.equals(record))
			return output;
		else
			return null;
		
	}
	
	@XmlElement(name = "source-format")
	public String getSourceDataFormat() {
		return sourceDataFormat;
	}
	
	/**
	 * Specifies the source data format
	 * @param sourceDataFormat
	 */
	public void setSourceDataFormat(String sourceDataFormat) {
		this.sourceDataFormat = sourceDataFormat;
	}

	@XmlElement(name = "target-format")
	public String getTargetDataFormat() {
		return targetDataFormat;
	}

	/**
	 * Specifies the target data format
	 * @param targetDataFormat
	 */
	public void setTargetDataFormat(String targetDataFormat) {
		this.targetDataFormat = targetDataFormat;
	}

}
