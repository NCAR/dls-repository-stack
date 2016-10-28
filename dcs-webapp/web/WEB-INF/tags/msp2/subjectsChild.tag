<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ tag import="edu.ucar.dls.schemedit.display.CollapseBean" %>
<%@ taglib tagdir="/WEB-INF/tags/vocabLayout" prefix="vl" %>

<bean:define id="collapseBean" name="sef" property="collapseBean" type="CollapseBean" />

<%@ attribute name="elementName" required="true" %>

<c:set var="elementPath" value="/record/general/subjects/${elementName}" />
<c:set var="id" value="${sf:pathToId(elementPath)}"/>
<jsp:setProperty name="collapseBean" property="id" value="${id}"/>
<input type="hidden" id="${id}_displayState" name="${id}_displayState" value="${sef.collapseBean.displayState}" />
<input type="hidden" value="" name="enumerationValuesOf(${elementPath})" />

<div id="${id}_box" class="level-1">
<table class="input-field-table">
  <tr class="form-row">
	<td class="label-cell">
	  <div>
		<div class="field-label optional ">
			<a href="javascript:toggleDisplayState ('${id}');">
			  <c:choose>
				<c:when test="${sef.collapseBean.isOpen}">
				  <img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/opened.gif" />
				</c:when>
				<c:otherwise>
				  <img id="${id}_img" border="0" hspace="3" height="12" width="12" src="../images/closed.gif" />
				</c:otherwise>
			  </c:choose>${elementName}
			</a>
			<div>
				<st:bestPracticesLink pathArg="${elementPath}" fieldName="${elementName}"/>
			</div>
		</div>
	  </div>
	</td>
	<td class="input-cell">
		<st:fieldPrompt pathArg="${elementPath}" />
	  <st:elementMessages propertyId="enumerationValuesOf(${elementPath})"/>
	  <div>
		<table style="display:${sef.collapseBean.displayState};" width="100%" id="${id}">
		  <tr valign="top">
			<td>
				<vl:vocabLayoutMultiBox elementPath="enumerationValuesOf(${elementPath})"/>
			</td>
		  </tr>
		</table>
	  </div>
	</td>
  </tr>
</table>
</div>
