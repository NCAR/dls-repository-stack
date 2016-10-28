<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>
<%@ taglib tagdir="/WEB-INF/tags/vocabLayout" prefix="vl" %>

<%@ attribute name="elementPath" required="true" %>
<%@ attribute name="vocabTerm" required="true" type="edu.ucar.dls.schemedit.vocab.layout.LayoutNode"%>
<%@ attribute name="inputMode" required="true"%>

<bean:define id="selectedValues" name="sef" property="${elementPath}"/>

<c:set var="cellWidth"><c:if test="${vocabTerm.numColumns gt 1}">width="${100 div vocabTerm.numColumns}%"</c:if></c:set>

<c:if test="${!vocabTerm.noDisplay}">

	<c:choose>
			<%-- vocab term --%>
			<c:when test="${ !vocabTerm.hasSubList }">
			
					<c:if test="${ vocabTerm.wrap }">
						<!-- start new cell for wrap -->
						</td><td width="${100 div vocabTerm.numColumns}%">
					</c:if>
			
				<vl:leaf elementPath="${elementPath}" vocabTerm="${vocabTerm}" inputMode="${inputMode}"/>

			</c:when>
			
			<c:when test="${ vocabTerm.hasSubList }">
			
					<c:if test="${ vocabTerm.wrap }">
						<!-- start new cell for wrap -->
						</td><td width="${100 div vocabTerm.numColumns}%">
					</c:if>
			
			<%-- group term --%>
				<vl:header elementPath="${elementPath}" vocabTerm="${vocabTerm}" inputMode="${inputMode}"/>
				
			</c:when>
		</c:choose>

</c:if>
