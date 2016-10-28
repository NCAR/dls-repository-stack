<%-- tool-help-link.jsp
	- requires that a helpTopic param is set in calling page 
	- displays link that will access tool help page
	
	--%>
<%@ page isELIgnored ="false" %>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

 <a href="#" onClick="return (doToolHelp ('${param.topic}'))"><img 
	src="../images/questionMarkWhite.gif" border="0"  width="7" Height="10" alt="click for help"/></a>

