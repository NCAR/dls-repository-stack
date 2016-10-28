<%-- Reference the JSP tag libraries that are used in these template pages --%>
<%@ page language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="dt" uri="http://www.dlese.org/dpc/dds/tags" %>
<%@ taglib prefix="f" uri="http://dls.ucar.edu/tags/dlsELFunctions" %>
<%@ page isELIgnored ="false" %>



<%-- The absolute URL to the directory in which this page resides. This is used to 
		create the URLs for the images and CSS. If you move this page to another 
		context, be sure to update the context portion of the path below! --%>
<c:set var="urlToMyDir" value="${f:contextUrl(pageContext.request)}/services/jshtml1-1"/>

<html>
<head>	
	<title>Error in the search service: </title>

	<%-- The CSS styles that format the colors, fonts and look-and-feel of this page --%>
	<link rel='stylesheet' type='text/css' href='${urlToMyDir}/styles.css'>

</head>

<%-- 	When the page loads, call the JavaScript function checkSelectedItems(),
		which selects the items in the checkbox menus that the user previously
		selected  --%>
<body class="dleseTmpl">


	There was an error: ${jsformv11.errorMsg}


</body>
</html>

