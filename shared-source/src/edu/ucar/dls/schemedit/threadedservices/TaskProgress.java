package edu.ucar.dls.schemedit.threadedservices;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.XMLUtils;

/**
 *  Class to report progress of a {@link MonitoredTask} via a json object.<p>
 *
 *  The monitoredTask updates the TaskProgress object as it runs, and the
 *  TaskProgress can also query the Task for other properties, such as whether
 *  the task is actually running.
 *
 * @author    ostwald
 */
public class TaskProgress {

	protected static boolean debug = false;

	MonitoredTask task = null;
	int total = 0;
	int done = 0;
	boolean error = false;
	String msg = null;


	/**
	 *  Constructor for the TaskProgress object
	 *
	 * @param  task  the task for which we report progress
	 */
	public TaskProgress(MonitoredTask task) {
		this.task = task;
	}


	/**
	 *  Initialize with total items to be processed
	 *
	 * @param  total  number of items to be processed
	 */
	public void init(int total) {
		this.init(total, null);
	}


	/**
	 *  Initialize with total items to be processed and a message that is part of
	 *  progress reports
	 *
	 * @param  total  number of items to be processed
	 * @param  msg    message used in reporting
	 */
	public void init(int total, String msg) {
		this.total = total;
		this.msg = msg;
		this.done = 0;
	}


	/**
	 *  Reports whether this TaskProgress' task is processing.
	 *
	 * @return    The active value
	 */
	public boolean isActive() {
		return this.task.getIsProcessing();
	}


	/**
	 *  Gets the msg attribute of the TaskProgress object
	 *
	 * @return    The msg value
	 */
	public String getMsg() {
		return this.msg;
	}


	/**
	 *  Sets the msg attribute of the TaskProgress object
	 *
	 * @param  msg  The new msg value
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}


	/**
	 *  Gets the done attribute of the TaskProgress object
	 *
	 * @return    The done value
	 */
	public int getDone() {
		return this.done;
	}


	/**
	 *  Sets the done attribute of the TaskProgress object
	 *
	 * @param  done  The new done value
	 */
	public void setDone(int done) {
		this.done = done;
	}


	/**
	 *  Gets the total attribute of the TaskProgress object
	 *
	 * @return    The total value
	 */
	public int getTotal() {
		return this.total;
	}


	/**
	 *  Sets the total attribute of the TaskProgress object
	 *
	 * @param  total  The new total value
	 */
	public void setTotal(int total) {
		this.total = total;
	}


	/**
	 *  Gets the error attribute of the TaskProgress object
	 *
	 * @return    The error value
	 */
	public boolean getError() {
		return this.error;
	}


	/**
	 *  Sets the error attribute of the TaskProgress object
	 *
	 * @param  error  The new error value
	 */
	public void setError(boolean error) {
		this.error = error;
	}


	/**
	 *  Percentage of task completed<p>
	 *
	 *  Can be calculated by client!?
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public float getPercentComplete() {
		return (float) this.done / this.total;
	}


	/**
	 *  Reports the current progress as a JSON string
	 *
	 * @return    The progressReport value
	 */
	public String getProgressReport() {

		String escapedMsg = "";
		try {
			escapedMsg = XMLUtils.escapeXml(getMsg());
		} catch (Throwable t) {
			prtln(t.getMessage());
			t.printStackTrace();
		}
		prtln("msg: " + escapedMsg);

		float percentComplete = getPercentComplete();
		prtln("percentComplete: " + percentComplete);
		String status = (isActive() ? "active" : "inactive");
		String reportXml = "<progress>" +
			"<status>" + status + "</status>" +
			"<msg>" + escapedMsg + "</msg>" +
			"<total>" + getTotal() + "</total>" +
			"<done>" + getDone() + "</done>" +
			"<error>" + getError() + "</error>" +
			"<percentComplete>" + Float.toString(percentComplete) + "</percentComplete>" +
			"</progress>";

		try {
			org.json.JSONObject json = org.json.XML.toJSONObject(reportXml);
			return json.toString();
		} catch (Exception e) {
			return "{'progressReportError': " + e.getMessage() + "}";
		}

	}


	/**  NOT YET DOCUMENTED */
	public void reset() {
		this.total = 0;
		this.done = 0;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "TaskProgress");
		}
	}

}

