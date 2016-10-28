package edu.ucar.dls.harvest.processors;

import java.util.ArrayList;
import java.util.List;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * The super class for all processors. All processors created should extend from
 * this class. No method is required to be implemented  since the subclasses overload
 * the run method with their own. This super class enables processors to be able to 
 * add errors, warning and infos which will be later used for reporting. This class
 * also keeps track of all the counts so the ingestor processor knows when to throw
 * max error exceptions
 *
 */
public abstract class Processor {
	// Name should be overridden to be used for end users
	protected String name = null;
	protected Workspace workspace = null;
	protected int errorCount = 0;
	protected int warningCount = 0;
	protected int infoCount = 0;
	protected List<ProcessorIssue> errors = new ArrayList<ProcessorIssue>();
	protected List<ProcessorIssue> warnings = new ArrayList<ProcessorIssue>();
	protected List<ProcessorIssue> infos = new ArrayList<ProcessorIssue>();
	public Processor()
	{
		// by default the name is the class name
		this.name = this.getClass().getName();
	}
	
	/**
	 * Add a error to the processor
	 * @param documentId
	 * @param msg
	 */
	public void addError(String documentId, String msg)
	{
		this.errors.add(new ProcessorIssue(documentId, msg, ProcessorIssue.ERROR_TYPE));
		this.errorCount++;
	}
	
	/**
	 * Add a warning to the processor
	 * @param documentId
	 * @param msg
	 */
	public void addWarning(String documentId, String msg)
	{
		this.warnings.add(new ProcessorIssue(documentId, msg, ProcessorIssue.WARNING_TYPE));
		this.warningCount++;
	}
	
	/**
	 * Add a info to the processor
	 * @param documentId
	 * @param msg
	 */
	public void addInfo(String documentId, String msg)
	{
		this.infos.add(new ProcessorIssue(documentId, msg, ProcessorIssue.INFO_TYPE));
		this.infoCount++;
	}
	
	/**
	 * Get the name of the processor. if the subclass doesn't change it it will
	 * be just the name of the class
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the error count
	 * @return
	 */
	public int getErrorCount() {
		return this.errorCount;
	}
	
	/**
	 * Get the warning count
	 * @return
	 */
	public int getWarningCount() {
		return warningCount;
	}
	
	/**
	 * Get the info count
	 * @return
	 */
	public int getInfoCount() {
		return infoCount;
	}
	
	/**
	 * Get the list of errors for the processor
	 * @return
	 */
	public List<ProcessorIssue> getErrors() {
		return this.errors;
	}
	
	/**
	 * Get the list of warnings for the processor
	 * @return
	 */
	public List<ProcessorIssue> getWarnings() {
		return this.warnings;
	}
	
	/**
	 * Get the list of infos for the processor
	 * @return
	 */
	public List<ProcessorIssue> getInfos() {
		return this.infos;
	}
	
	/**
	 * initialize the processor, note all sub classes that override 
	 * this should call super.initialize() too
	 * @return
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		this.workspace = workspace;
		this.clear();
	}
	
	/**
	 * Clear all counts and attributes that hold persistant data
	 */
	public void clear()
	{
		this.errorCount = 0;
		this.warningCount = 0;
		this.infoCount = 0;
		this.errors.clear();
		this.warnings.clear();
		this.infos.clear();
	}
	
	/** 
	 * Main run method that is called to execute processor, this method
	 * should be either overridden or overloaded depended on wether or not its
	 * been used as a repository processor, harvest processor or repository processor 
	 * @throws HarvestException
	 */
	public void run() throws HarvestException{

	}
	
	public static void initializeProcessors(
			List<? extends Processor> processors, Workspace workspace) throws HarvestException {
		for(Processor processor: processors)
			processor.initialize(workspace);
	}
	
}
