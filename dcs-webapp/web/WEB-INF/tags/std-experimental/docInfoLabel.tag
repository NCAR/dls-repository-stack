<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.asn.AsnDocInfo" %>

<%@ attribute name="docInfo" required="true" type="edu.ucar.dls.schemedit.standards.asn.AsnDocInfo"%>

<table cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td>
			<div class="std-doc-label">
				<c:if test="${not empty docInfo.author}">
					${docInfo.author}
				</c:if>
				<c:if test="${not empty docInfo.topic}">
					- ${docInfo.topic}
				</c:if>
				<c:if test="${not empty docInfo.created}">
					- ${docInfo.created}
				</c:if>
			</div>
		</td>
		<td align="right">
			<input type="button" class="use-doc-button stds-ui-button" id="${docInfo.key}"
					value="use" />
		</td>
	</tr>
	<c:if test="${not empty docInfo.title}">
	<tr>
		<td colspan="2">
			<div class="std-doc-title">${docInfo.title}</div>
		</td>
	</tr>
	</c:if>
</table>
