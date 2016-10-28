<%-- editRecordLink.tag
	- requires that a propertyId param is set in calling page 
	- diplays error messages relevant to a particular field (identified by propertyId)
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="reader" required="true" type="edu.ucar.dls.index.reader.XMLDocReader" %>
<%@ attribute name="label" %>

<a href="/schemedit/${reader.metadataPrefix}/${reader.metadataPrefix}.do?src=dcs&collection=${reader.collection}&command=edit&recId=${reader.id}">${not empty label ? label : 'edit'}</a>

