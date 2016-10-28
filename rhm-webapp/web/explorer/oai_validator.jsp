<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/TagLibIncludes.jsp" %>
<%-- Schema validates an OAI data provider response --%>

<c:remove var="oaiResponse" scope="session"/>

<html>
<head>
	<title>OAI Validator</title>
	<%@ include file="/head.jsp" %>
	
</head>
<body>
	<c:set var="pageViewContext" value="noLinks"/>
	<%@ include file="/top.jsp" %>
	
	<h1>OAI Validator</h1>
	
<%-- 	<input title="Close this window" type="button" value="Close" onClick='window.close()' class="smallButton" style="position: absolute; top:80px; left: 650px; width: 70px;">
 --%>
 	<div title="Close this window" onclick="window.close()" style="position: absolute; top:90px; left: 720px; cursor:pointer;">
		<div class="closeBx" style="margin-left:13px"></div>
		<div>(<a href="javascript:void(0)" onclick="window.close()">close</a>)</div>
	</div>
	
	<table class="compactTable" cellpadding="0" cellspacing="0" style="margin-bottom:10px;">
		<c:if test="${not empty param.repositoryName}">
			<tr>
				<td><b>Repository:</b></td>
				<td><b>${param.repositoryName}</b></td>
			</tr>
		</c:if>
		<c:if test="${not empty param.adminEmail}">
			<tr>
				<td>Admin e-mail:</td>
				<td><a href="mailto:${param.adminEmail}">${param.adminEmail}</a></td>
			</tr>
		</c:if>
		<tr>
			<td>Base URL:</td>
			<td>${param.baseUrl}</td>
		</tr>		
		<tr>
			<td>Request:</td>
			<td>
				${param.verb}
				<c:if test="${not empty param.identifier}"><nobr>[identifier: ${param.identifier}]</nobr></c:if>
				<c:if test="${not empty param.set}"><nobr>[set: ${param.set}]</nobr></c:if>
				<c:if test="${not empty param.metadataPrefix}"><nobr>[format: ${param.metadataPrefix}]</nobr></c:if>
				<c:if test="${not empty param.resumptionToken}"><nobr>[resumptionToken: ${param.resumptionToken}]</nobr></c:if>				
			</td>		
		</tr>
	</table>

	<c:url value="${param.baseUrl}" var="oaiRequest">
		<c:param name="verb" value="${param.verb}"/>
		<c:if test="${not empty param.identifier}">
			<c:param name="identifier" value="${param.identifier}"/>
		</c:if>
		<c:if test="${not empty param.set}">
			<c:param name="set" value="${param.set}"/>
		</c:if>
		<c:if test="${not empty param.metadataPrefix}">
			<c:param name="metadataPrefix" value="${param.metadataPrefix}"/>
		</c:if>
		<c:if test="${not empty param.resumptionToken}">
			<c:param name="resumptionToken" value="${param.resumptionToken}"/>
		</c:if>
	</c:url>
		
	<c:url value="/explorer/oai_validation_report.jsp" var="oaiValidatorUrl">
		<c:param name="rt" value="validate"/>
	</c:url>

	<div id="oaiErrorMessageContainer" style="display:none">
		The data provider returned an error message:
		<div style="margin: 2px 0px 0px 20px; background-color: #FBFFE0">
			<span id="errorMessage"></span> 
			<nobr>&nbsp; (code: <span id="errorCode"></span>)</nobr>
		</div>
	</div>
	<div id="numRecordsContainer" style="display:none"> 
		Validated <span id="numRecords">(n/a)</span> ${param.verb == 'ListIdentifiers' ? 'identifiers' : 'records'} and <span id="numDeletions">(n/a)</span> deletions (this batch).
	</div>	
	<div id="resumeContainer" style="display:none">	
		<div id="resumeMessage"></div>	
		<div>
			&#187; <a href="javascript:void(0);" onclick="mkResume('validate',parms.verb,resumptionToken,true);">Validate next batch of ${param.verb == 'ListIdentifiers' ? 'identifiers' : 'records'} in this list</a>
		</div>
	</div>
	<div id="msgContainer"></div>
	
	
	<%-- Container to display the validation result --%>
	<div id="validationResult" style="border:1px solid black; background-color: #eee; padding:8px; margin-top:10px">
		<div class="loadingMsg">Fetching OAI response from the data provider...</div>
	</div>
	
	
	<script>
		var numRecords = null;
		var numIdentifiers = null;
		var numDeletions = null;
		var resumptionToken = null;
		var completeListSize = null;
		var cursor = null;
		var oaiError = null;
		new Ajax.Request('<c:url value="/explorer/oai_fetcher.jsp"/>', {
			parameters: { oaiRequest:'${f:jsEncode(oaiRequest)}'}, 
			onSuccess: function(transport) {
				if(transport.responseText.isJSON()) {
					try {	
						var responseJson = transport.responseText.evalJSON();
						if(responseJson.error) {
							var msg = 'Unable to fetch from the OAI data provider. Error message: <div style="margin:1em"><code>' + responseJson.error.errorMessage + '</code></div>';
							$('validationResult').update(msg);
							return;
						}
						else if(responseJson.oaiError)
							oaiError = responseJson.oaiError;
						else if(responseJson.resume) {
							resumptionToken = responseJson.resume.resumptionToken;
							if(responseJson.resume.completeListSize)
								completeListSize = responseJson.resume.completeListSize;
							if(responseJson.resume.cursor)
								cursor = responseJson.resume.cursor;
							if(responseJson.resume.numRecords)
								numRecords = responseJson.resume.numRecords;
							if(responseJson.resume.numIdentifiers)
								numRecords = responseJson.resume.numIdentifiers;								
							if(responseJson.resume.numDeletions)
								numDeletions = responseJson.resume.numDeletions; 									
						}
						else if(responseJson.listResponse) {
							if(responseJson.listResponse.numRecords)
								numRecords = responseJson.listResponse.numRecords;
							if(responseJson.listResponse.numIdentifiers)
								numIdentifiers = responseJson.listResponse.numIdentifiers;
							if(responseJson.listResponse.numDeletions)
								numDeletions = responseJson.listResponse.numDeletions; 									
						}						
					} catch (e) { return; }
				}
				// If no fetch error, do validation step:
				$('validationResult').update('<div class="loadingMsg">Validating OAI response...</div>');
				new Ajax.Updater('validationResult', '${f:jsEncode(oaiValidatorUrl)}', {
					onSuccess: function(transport) { 
						if(oaiError != null) {
							$('errorMessage').update(oaiError.errorMessage);
							$('errorCode').update(oaiError.errorCode);
							$('oaiErrorMessageContainer').show();
						}
						else if(resumptionToken != null) {
							if(completeListSize != null && cursor != null)
								$('resumeMessage').update('Showing partial list starting at position ' + cursor + '. Complete list size ' + completeListSize + '.');
							else if(completeListSize != null)
								$('resumeMessage').update('Showing partial list. Complete list size ' + completeListSize + '.');
							else
								$('resumeMessage').update('Showing partial list.');									
							$('resumeContainer').show();
						}
						else if(parms.verb == 'ListRecords')
							$('msgContainer').update('All records have been validated in this list.');
						else if(parms.verb == 'ListIdentifiers')
							$('msgContainer').update('All identifiers have been validated in this list.');

						if(numRecords != null || numDeletions != null) {
							if(numRecords != null)
								$('numRecords').update(numRecords);
							else if(numIdentifiers != null)
								$('numRecords').update(numIdentifiers);								
							if(numDeletions != null)
								$('numDeletions').update(numDeletions);								
							$('numRecordsContainer').show();
						}
					}
				});
			},
			onFailure: function(transport) {
				$('validationResult').update('There was an error fetching from the OAI data provider.');
			} 
		});
	</script>

	
	<div style="height:200px;"></div>
	
	<%@ include file="/bottom.jsp" %>
</body>
</html>


