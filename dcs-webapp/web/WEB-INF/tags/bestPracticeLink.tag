<%-- bestPracticeLink.tag
- NOT CURRENTLY USED??
	- requires that a helpTopic param is set in calling page 
	- displays link that will access tool help page
	
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="reader" required="true" type="edu.ucar.dls.index.reader.XMLDocReader"%>

 <a href="#" onClick="return (doBestPractice ('${topic}'))"><st:questionMarkImg alt="click for best practices"/></a>


<%--  <a href="#" onClick="return (doBestPractice ('${topic}'))"><img 
	src="../images/questionMarkWhite.gif" border="0"  width="7" Height="10" alt="click for best practices"/></a>
 --%>
