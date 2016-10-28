<%@ page language="java" %>
<%@ page contentType="text/csv; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%@ taglib uri='/WEB-INF/tlds/vocabulary.tld' prefix='vocab' %>
<%@ taglib uri='/WEB-INF/tlds/utils.tld' prefix='util' %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>

<% response.addHeader("Content-Disposition", "filename=histogram_data.csv;"); %>

<%-- GetURL bean used in place of IO taglib: --%>
<jsp:useBean id="io" class="edu.ucar.dls.util.GetURL" scope="application"/>
<bean:define id="thisServer" value='<%= "http://" + request.getServerName() + ":" + request.getServerPort() %>' />

<jsp:useBean id="histogramForm" class="edu.ucar.dls.dds.action.form.HistogramForm" scope="session"/>


<%-- Need to set which interface (system.interface.language) to use as controlled by the vocab XML files: --%>
<jsp:setProperty name="histogramForm" property="vocabInterface" value="dds.descr.en-us" />

Generated on: <%= new java.util.Date().toString() %>

Category, Number of resources		

<logic:iterate id="collection" name="histogramForm" property="collections" length="1">
	<logic:equal name="collection" property="vocabNode.noDisplay" value="false">
			
		
Collection: <bean:write name="collection" property="vocabNode.label" filter="false" />,<bean:write name="collection" property="libraryTotal" filter="false" />

Grade level
			<logic:iterate id="gradeLevel" name="histogramForm" property="gradeRange">
				<logic:equal name="gradeLevel" property="vocabNode.noDisplay" value="false">			
					<logic:greaterThan name="gradeLevel" property="libraryTotal" value="0">	
<bean:write name="gradeLevel" property="vocabNode.label" filter="false" />,<bean:write name="gradeLevel" property="libraryTotal" filter="false" />
					</logic:greaterThan>
				</logic:equal>
			</logic:iterate>
			<logic:notEmpty name="histogramForm" property="resourceType">

Resource type
			<logic:iterate id="resourceType" name="histogramForm" property="resourceType">
				<logic:equal name="resourceType" property="vocabNode.noDisplay" value="false">
					<logic:greaterThan name="resourceType" property="libraryTotal" value="0">	
<bean:write name="resourceType" property="vocabNode.label" filter="false"  />,<bean:write name="resourceType" property="libraryTotal" filter="false" />	
					</logic:greaterThan>
				</logic:equal>
			</logic:iterate>
			</logic:notEmpty>

Subject
			<logic:iterate id="subject" name="histogramForm" property="subject">
				<logic:equal name="subject" property="vocabNode.noDisplay" value="false">			
					<logic:greaterThan name="subject" property="libraryTotal" value="0">	
<bean:write name="subject" property="vocabNode.label" filter="false" />,<bean:write name="subject" property="libraryTotal" filter="false" />		
					</logic:greaterThan>
				</logic:equal>
			</logic:iterate>
	</logic:equal>
</logic:iterate>
