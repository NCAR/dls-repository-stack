package edu.ucar.dls.harvest.processors.record.containers;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Replacement")
public class Replacement {
	private String target = null;
	private String replacement = "";
	
	@XmlElement(name = "replacement")
	public String getReplacement() {
		return replacement;
	}
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
	
	@XmlElement(name = "target")
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
}
