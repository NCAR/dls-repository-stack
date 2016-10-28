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