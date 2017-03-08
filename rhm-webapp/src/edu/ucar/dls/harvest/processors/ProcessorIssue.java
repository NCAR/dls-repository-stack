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
package edu.ucar.dls.harvest.processors;

/**
 * Issue class that hold information about what issue a processor finds.
 * Note that elementId can be anything and is only used for reporting to
 * identify what had the issue. In the case for record processors it also 
 * uses to it to give a sample of the record
 *
 */
public class ProcessorIssue {
	public static final String ERROR_TYPE = "Error";
	public static final String WARNING_TYPE = "Warning";
	public static final String INFO_TYPE = "Info";
	
	private String elementId = null;
	private String message = null;
	private String type = null;
	
	/**
	 * Constructor method that takes the element identifier, message and type 
	 * that must be on of the constants ERROR_TYPE, WARNING_TYPE, INFO_TYPE
	 * These are used for groupings in the reporting
	 * 
	 * @param aDocumentId
	 * @param aMessage
	 * @param type
	 */
	public ProcessorIssue(String aElementId, String aMessage, String type)
	{
		this.elementId = aElementId;
		this.message = aMessage;
		this.type = type;
	}

	/**
	 * Get the element id from the issue
	 * @return
	 */
	public String getElementId() {
		return this.elementId;
	}

	/**
	 * Gets the message
	 * @return
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Get the type of issue this is
	 * @return
	 */
	public String getType() {
		return this.type;
	}
}
