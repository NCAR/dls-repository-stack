<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="elementPath" required="true" %>
<%@ attribute name="orientation" required="false" %> <%-- vertical (default) | horizontal  --%>

<bean:define id="val" name="sef" property="valueOf(${elementPath})" />

<ul class="autoform-radiobuttons ${orientation}">
	<logic:iterate id="radioBean" name="sef" property="enumerationOptions(${elementPath})">
		<c:set var="radioId" value="${sf:pathToId(elementPath)}_${radioBean.value}_id" />
		<li><%-- label: ${radioBean.label} value: ${radioBean.value} --%>
		<input type="radio" 
			id="${radioId}" 
			name="valueOf(${elementPath})" 
			value="${radioBean.value}" 
			${val eq radioBean.value ? 'CHECKED' : ''} />
			<label for="${radioId}">${radioBean.label}</label>
		</li>
	</logic:iterate>
</ul>
