<h4>Field File Checker Report</h4>
<c:set var="checker" value="${fff.fieldFilesChecker}" />
<div style="font-style:italic;margin-bottom:8px;">${checker.filesRead} fields files read</div>
<%-- <div class="noToggle">${fn:length(checker.readerErrors)} errors</div> --%>

<%-- READER ERRORS --%>
<c:choose>
	<c:when test="${not empty checker.readerErrors}">
		<div id="readererrors" class="toggler">
			<div id="toggler-lnk-readererrors" class="togglerClosed">
				${fn:length(checker.readerErrors)} fields files could not be read
			</div>
			<ul id="toggler-con-readererrors" class="report-list" style="display:none">
				<c:forEach var="error" items="${checker.readerErrors}">
					<li>${error.location}
						<div>error: ${error.data}</div>
					</li>
				</c:forEach>
			</ul>
		</div>
	</c:when>
	<c:otherwise>
		<div class="noToggle">All fields files were read properly</div>
	</c:otherwise>
</c:choose>

<%-- BAD PATHS --%>
<c:choose>
	<c:when test="${not empty checker.badPaths}">
		<div id="badpaths" class="toggler">
			<div id="toggler-lnk-badpaths" class="togglerClosed">
				${fn:length(checker.badPaths)} illegal Xpaths found
			</div>
			<ul id="toggler-con-badpaths" class="report-list" style="display:none">
				<c:forEach var="error" items="${checker.badPaths}">
					<li>${error.data}
						<div>found in <a href="${error.location}">${error.location}</a></div>
					</li>
				</c:forEach>
			</ul>
		</div>
	</c:when>
	<c:otherwise>
		<div class="noToggle">All Xpaths defined in schema are legal</div>
	</c:otherwise>
</c:choose>

<%-- UNSEEN PATHS --%>
<c:choose>
	<c:when test="${not empty checker.unseenPaths}">
		<div id="unseen" class="toggler">
			<div id="toggler-lnk-unseen" class="togglerClosed">
				${fn:length(checker.unseenPaths)} Xpaths have no corresponding field file:
			</div>
			<ul id="toggler-con-unseen" class="report-list" style="display:none">
				<c:forEach var="path" items="${checker.unseenPaths}">
					<li>${path}</li>
				</c:forEach>
			</ul>
		</div>
	</c:when>
	<c:otherwise>
		<div class="noToggle">All Xpaths defined in schema have corresponding field files</div>
	</c:otherwise>
</c:choose>

<%-- MULTIPLES --%>
<c:choose>
	<c:when test="${not empty checker.multiples}">
		<div class="toggler" id="multiples">
		<div id="toggler-lnk-multiples" class="togglerClosed">${fn:length(checker.multiples)} paths are contained in mulitple fields files:</div>
		<ul id="toggler-con-multiples" class="report-list" class=style="display:none">
		<c:forEach var="item" items="${checker.multiples}">
			<li>${item.key}</li>
				<ul class="report-list">
					<c:forEach var="path" items="${item.value}">
						<li>${path}</li>
					</c:forEach>
				</ul>
		</c:forEach>
		</ul>
	</c:when>
	<c:otherwise>
		<div class="noToggle">No Xpaths were found in multiple fields files</div>	
	</c:otherwise>
</c:choose>

<%-- DUP FIELD NAMES --%>
<c:choose>
	<c:when test="${not empty checker.dupFieldNames}">
		<div class="toggler" id="dupfields">
			<div id="toggler-lnk-dupfields" class="togglerClosed">${fn:length(checker.dupFieldNames)} 
				field names occur in more than 1 file:</div>
			<ul id="toggler-con-dupfields" class="report-list" class=style="display:none">
			<c:forEach var="item" items="${checker.dupFieldNames}">
				<li>${item.key}</li>
					<ul class="report-list">
						<c:forEach var="error" items="${item.value}">
							<li><a href="${error.location}">${error.location}</a>
								<div>${error.data}</div>
							</li>
						</c:forEach>
					</ul>
				
				</c:forEach>
			</ul>
		</div>
	</c:when>
	<c:otherwise>
		<div class="noToggle">No Field Names were found in more than one file</div>	
	</c:otherwise>
</c:choose>

