<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<%@ page import="java.util.Date" %>

<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-nested.tld" prefix="nested" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/response.tld" prefix="resp" %>
<%@ taglib uri="/WEB-INF/tlds/request.tld" prefix="req" %>
<%@ taglib uri="/WEB-INF/tlds/datetime.tld" prefix="dt" %>
<%@ taglib uri='/WEB-INF/tlds/utils.tld' prefix='util' %>
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<html:html>

<head>
	<title>Repository Analytics</title>
	<meta charset="utf-8">
	<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>
	<%@ include file="/nav/head.jsp" %>	
	<%@ include file="/analytics/head.jsp" %>
	
</head>

<body text="#000000" bgcolor="#ffffff">
	<!-- Sub-title just underneath logo -->
	<div class="dlese_sectionTitle" id="dlese_sectionTitle">
	   Repository<br/> Analytics
	</div>
	
	<%@ include file="/nav/top.jsp" %>
	
	<html:errors />
	
	<%@ include file="/analytics/body.jsp" %>
	
	<%@ include file="/nav/bottom.jsp" %>
</body>
</html:html>