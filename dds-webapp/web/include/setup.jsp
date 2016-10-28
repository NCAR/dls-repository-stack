<%@ page language="java" 
%><%@ page import="org.apache.lucene.document.Document" 
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" 
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" 
%><%@ taglib uri="/WEB-INF/tlds/utils.tld" prefix="util" 
%><%@ taglib uri="/WEB-INF/tlds/dds.tld" prefix="dds" 
%><%@ taglib uri="/WEB-INF/tlds/response.tld" prefix="resp" 
%><%@ taglib uri="http://dls.ucar.edu/tags/dlsELFunctions" prefix="f" %><%-- Fields/Groups OPML-based vocabs: --%><%@
	taglib uri='/WEB-INF/tlds/vocabulary_opml.tld' prefix='vocabs' 
%><%@ page isELIgnored="false" 
%><%-- Set ${domain} so as to reference a *local* copy of /dlese_shared/ (or anything else) --%><c:set 
	var="domain"><%= "http://" + request.getServerName() + ":" + request.getServerPort() %></c:set>
<%-- Location of the JavaScript vocabs service --%>
<c:set var="jsVocabsUrl">${ domain }/dds/services/vocab-menus-1-0</c:set>
<jsp:useBean id="ddsQueryForm" class="edu.ucar.dls.dds.action.form.DDSQueryForm" scope="session"/>
<resp:setContentType>text/html</resp:setContentType>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">