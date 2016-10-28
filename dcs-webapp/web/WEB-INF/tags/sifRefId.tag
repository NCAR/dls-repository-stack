<%--  mdvMultiBox
 
 - this is a modified version of resourceTag1.
		
 - it requires a parameter named elementPath. e.g. here is a call for this tag:
 		<st:mdvMultiBox elementPath="enumerationValuesOf(/itemRecord/educational/resourceTypes/resourceType)"/>
 	
 - this tag produces a static muli-level display. 
 - The very top level is not displayed, only the lower levels. 
   e.g., in the case of "resourceType" there is no header for "resourceType". The header is displayed by
   Renderer.

 - this tag will be produced by Renderer.getMultiBoxInput for the cases when a MVS mapping can be found for a 
   particular xpath
   
--%> 

<%@ tag isELIgnored ="false" 
%><%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" 
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ attribute name="property" required="true" %>
<%@ attribute name="styleId" required="true" %>
<%@ attribute name="sifType" required="true" %>


<html:text property="${property}" size="50" styleId="${styleId}"/>

<%-- Ref Handler Icon --%>
<a href="#" onclick="return (sifRefHandler('${styleId}', '${sifType}'));"><img 
	hspace="10" title="Find or create object to populate this field" src="../images/sif_finder.gif" border="0"/></a>
	
<%-- New Object Creator --%>
<%-- <a href="#" onclick="return (sifObjCreator('${styleId}', '${sifType}'));"><img 
	hspace="10" title="Create Object" src="../images/new.png" border="0"/> --%>

