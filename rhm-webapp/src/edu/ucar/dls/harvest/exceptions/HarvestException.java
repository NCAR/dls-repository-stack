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
package edu.ucar.dls.harvest.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Custom exception class used for holding more information and handling throughout the harvesting process
 * A harvest exception throw is an automatic stoppage of the harvest.
**/

public class HarvestException extends Exception {
	private String errorCode = null;
	private String errorMsg = null;
	private Exception orgException = null;
	private String context = null;
	
	/**
	 * Constructor that takes the error code and error message but also a string
	 * representing where the error happened. 
	 * @param errorCode
	 * @param errorMsg
	 * @param context
	 */
	public HarvestException(String errorCode, String errorMsg, String context)
	{
		super();
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.context = context;
	}
	
	/**
	 * Constructor that takes errorCode and errorMsg like the other constructor but this
	 * one takes a origin exception. In this case HarvestException will be a wrapper for
	 * it
	 * @param errorCode
	 * @param errorMsg
	 * @param orgException
	 */
	public HarvestException(String errorCode, String errorMsg, Exception orgException)
	{
		super();
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.orgException = orgException;
		this.context = this.createContext(orgException);
	}
	
	/**
	 * Get the errror from the exception. This will concat the error message with the
	 * origin exception if applicable
	 * @return
	 */
	public String getError()
	{
		String msg = this.errorMsg;
		if(this.orgException!=null)
			msg = String.format("%s; \n\nException: %s\n\n%s ", this.errorMsg, 
					this.orgException.getMessage(), 
					ExceptionUtils.getStackTrace(this.orgException));
		return msg;
	}
	
	/**
	 * Figures out from an exception where it originated from.
	 * @param e
	 * @return
	 */
	private String createContext(Exception e)
	{
		String context = "HarvestException";
		for (StackTraceElement stackTrace: e.getStackTrace())
		{
			if(stackTrace.getClassName().contains("edu.ucar.dls.harvest"))
			{
				context = String.format("%s.%s()", stackTrace.getClassName(), 
														stackTrace.getMethodName());
				break;
			}
		}
		return context;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getContext() {
		return context;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getErrorCode() {
		return this.errorCode;
	}
}
