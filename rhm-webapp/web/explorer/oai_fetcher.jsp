<%-- Simply fetches an OAI response and stores it in session scope. Meant to be called by Ajax. --%>

<%@ include file="/TagLibIncludes.jsp" %>

<c:remove var="oaiResponse" scope="session"/>

<%-- Fetch data from the OAI provider and store it in a session variable. --%>
<c:catch var="oaiFetchError">
	<c:set var="oaiResponse" scope="session">${f:timedImportUsingEncoding(param.oaiRequest,'UTF-8',120000)}</c:set>
</c:catch>

<c:if test="${empty oaiFetchError}">
    <c:catch var="oaiParseError">
        <c:if test="${not empty oaiResponse}">
			<c:set var="oaiResponse" scope="session">${f:replaceFirst(fn:trim(oaiResponse),".*\\<\\?xml.*\\?\\>","")}</c:set>
            <xtags:parse id="oaiResponseDom">${oaiResponse}</xtags:parse>
        </c:if>
    </c:catch>
</c:if>

<c:catch var="overallError">
	<%-- Return error JSON message with data or error: --%>
	<c:choose>
		<c:when test="${not empty oaiFetchError}">
			<c:remove var="oaiResponse" scope="session"/>
			<c:set var="errorResponse">
				<error>
					<errorMessage>Error fetching from the repository: The repository may have returned an http error code such as 404 or 500, or the processor timed out waiting for it to respond.</errorMessage>
				</error>
			</c:set>
			${f:xml2json(errorResponse)}		
		</c:when>
        <c:when test="${not empty oaiParseError}">
            <c:remove var="oaiResponse" scope="session"/>
            <c:set var="errorResponse">
                <error>
                    <errorMessage>Error parsing the response: ${oaiParseError}</errorMessage>
                </error>
            </c:set>
            ${f:xml2json(errorResponse)}
        </c:when>
		<c:when test="${not empty oaiResponseDom}">
			<xtags:variable id="errorElement" select="$oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='error']" type="node"/>
			<c:if test="${empty errorElement}">
				<xtags:variable id="resumptionToken" select="$oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListRecords']/*[local-name()='resumptionToken'] | $oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListIdentifiers']/*[local-name()='resumptionToken']"/>
				<xtags:choose>
					<xtags:when test="count($oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListRecords']) > 0">
						<xtags:variable id="numRecords" select="count($oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListRecords']/*[local-name()='record']/*[local-name()='metadata']/*)"/>
						<xtags:variable id="numDeletions" select="count($oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListRecords']/*[local-name()='record']/*[local-name()='header'][@status='deleted'])"/>
					</xtags:when>
					<xtags:when test="count($oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListIdentifiers']) > 0">
						<xtags:variable id="numIdentifiers" select="count($oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListIdentifiers']/*[local-name()='header']/*[local-name()='identifier'])"/>
						<xtags:variable id="numDeletions" select="count($oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListIdentifiers']/*[local-name()='header'][@status='deleted'])"/>
					</xtags:when>
				</xtags:choose>					
			</c:if>
			<c:choose>
				<c:when test="${not empty errorElement}">
					<c:set var="resumeResponse">
						<oaiError>
							<errorCode><xtags:valueOf select="$errorElement/@code"/></errorCode>
							<errorMessage><xtags:valueOf select="$errorElement"/></errorMessage>
						</oaiError>
					</c:set>
					${f:xml2json(resumeResponse)}
				</c:when>			
				<c:when test="${not empty resumptionToken}">
					<xtags:variable id="completeListSize" select="$oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListRecords']/*[local-name()='resumptionToken']/@completeListSize | $oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListIdentifiers']/*[local-name()='resumptionToken']/@completeListSize"/>
					<xtags:variable id="cursor" select="$oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListRecords']/*[local-name()='resumptionToken']/@cursor | $oaiResponseDom/*[local-name()='OAI-PMH']/*[local-name()='ListIdentifiers']/*[local-name()='resumptionToken']/@cursor"/>
					<c:set var="resumeResponse">
						<resume>
							<resumptionToken>${resumptionToken}</resumptionToken>
							<c:if test="${not empty completeListSize}">
								<completeListSize>${completeListSize}</completeListSize>
							</c:if>
							<c:if test="${not empty cursor}">
								<cursor>${cursor}</cursor>
							</c:if>
							<c:if test="${not empty numRecords}">
								<numRecords>${numRecords}</numRecords>
							</c:if>
							<c:if test="${not empty numIdentifiers}">
								<numIdentifiers>${numIdentifiers}</numIdentifiers>
							</c:if>							
							<c:if test="${not empty numDeletions}">
								<numDeletions>${numDeletions}</numDeletions>
							</c:if>															
						</resume>
					</c:set>
					${f:xml2json(resumeResponse)}
				</c:when>
				<c:when test="${not empty numRecords || not empty numIdentifiers || not empty numDeletions}">
					<c:set var="listResponse">
						<listResponse>
							<c:if test="${not empty numRecords}">
								<numRecords>${numRecords}</numRecords>
							</c:if>				
							<c:if test="${not empty numIdentifiers}">
								<numIdentifiers>${numIdentifiers}</numIdentifiers>
							</c:if>
							<c:if test="${not empty numDeletions}">
								<numDeletions>${numDeletions}</numDeletions>
							</c:if>								
						</listResponse>
					</c:set>
					${f:xml2json(listResponse)}		
				</c:when>
				<c:otherwise>
					<%-- No json response... --%>
				</c:otherwise>				
			</c:choose>
		</c:when>
		<c:when test="${empty oaiResponse}">
			<c:set var="errorResponse">
				<error>
					<errorMessage>There was no response from the data provider.</errorMessage>
				</error>
			</c:set>
			${f:xml2json(errorResponse)}		
		</c:when>
		<c:otherwise>
		</c:otherwise>
	</c:choose>
</c:catch>
<c:if test="${not empty overallError}">
	<c:set var="overallError">Error oai_fetcher.jsp: ${overallError}</c:set>
	${f:systemOut(overallError)}
</c:if>
