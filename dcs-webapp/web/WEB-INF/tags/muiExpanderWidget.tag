<%--  muiExpanderWidget
   
--%> 

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="elementPath" required="true" %>

<div>
( <a href="#" onclick="muiExpander.expandAll('${sf:pathToId(elementPath)}');return false;">expand all</a> | 
<a href="#" onclick="muiExpander.collapseAll('${sf:pathToId(elementPath)}');return false;">collapse all</a> )
</div>
