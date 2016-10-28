<%-- workFlowStatusNote.tag
	- displays the current workFlowStatusNote, and a history of prior statusEntries
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="sessionBean" required="true" type="edu.ucar.dls.schemedit.SessionBean" %>
<%@ attribute name="result" required="true" type="edu.ucar.dls.index.ResultDoc" %>

<c:set var="indentAmount" value="0"/>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="statusNoteClass">${queryForm.searchMode == 'status_note' ? 'doHighlight' : ''}</c:set>

<bean:define id="dcsData" name="sessionBean" property="dcsDataRecord(${result.docReader.id})" />
<div id="statusNote_${result.docReader.id}" style="display:none;">
<div style="border:2px solid #E8ECF4;padding:3px;text-align:left">
<%-- 	<div style="white-space:nowrap">
		<a 	href="javascript:toggleVisibility('statusNote_${result.docReader.id}');" 
			title="Click to show/hide" 
			class="vocabHeading"><img src='${contextPath}/images/btnExpand.gif' 
			alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" />Status Note</a>
			&nbsp;
			<span class="dcs-data">[ 
				<a href="${contextPath}/record_op/status.do?command=edit&recId=${result.docReader.id}" 
				title="edit record status">edit</a> ]</span>						
	</div> --%>

	<div style="margin-left:${indentAmount}px">
			<div style="font-size:85%;font-weight:bold"><fmt:formatDate value="${dcsData.currentEntry.date}" 
				pattern="yyyy-MM-dd h:mm a"/> (${sf:getFullName (dcsData.currentEntry.editor, userManager)})&nbsp;-
				${sf:getStatusLabel(result.docMap['dcsstatus'], result.docReader.collection, sessionBean)}
			</div>
			<div class="searchResultValues" style="white-space:normal">
				<div class="${statusNoteClass}">${dcsData.currentEntry.statusNote}</div>
			</div>

			<%-- status history --%>
			<c:if test="${not empty dcsData && fn:length(dcsData.entryList) > 1}">
				<div style="font-size:85%;font-weight:bold;padding-top:2px;border-top:#cccee6 1px solid">Status History</div>
				<c:forEach var="entry" items="${dcsData.entryList}" begin="1">
					<div style="font-size:85%;padding:2px">
						<fmt:formatDate value="${entry.date}" 
										pattern="yyyy-MM-dd h:mm a"/> (${sf:getFullName (entry.editor, userManager)})&nbsp;-
						${sf:getStatusLabel(entry.status, result.docReader.collection, sessionBean)}
						
						<c:if test="${not empty entry.statusNote}" >
							<div class="${statusNoteClass}">${entry.statusNote}</div>
						</c:if>
					</div>
				</c:forEach>
			</c:if>
	</div>
</div>
</div>
