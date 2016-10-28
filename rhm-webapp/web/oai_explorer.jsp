<%@ include file="/TagLibIncludes.jsp" %>
<c:choose>
	<c:when test="${not empty param.baseUrl}">
		<c:set var="collBaseUrl" value="${param.baseUrl}" scope="session"/>
	</c:when>
	<c:when test="${empty param.baseUrl && param.baseUrl != null}">
		<c:remove var="collBaseUrl" scope="session"/>
	</c:when>	
</c:choose>

<%-- Create the Identify dom --%>
<c:catch var="identifyError">
	<%-- Construct the OAI request (URL) --%>
	<c:url value="${collBaseUrl}" var="identifyRequest">
		<c:param name="verb" value="Identify"/>
	</c:url>

	<%-- Perform the web service request for Identify --%>
	<c:set var="identifyRequestOutput">${f:replaceFirst(f:timedImport(identifyRequest,2000),".*\\<\\?xml.*\\?\\>","")}</c:set>

	<%-- Remove namespaces from the XML and create the DOM --%>
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${identifyRequestOutput}" var="identifyDom"/>
</c:catch>


<%-- Create the sets selection list --%>
<c:catch var="listSetsError">
	<%-- Construct the OAI request (URL) --%>
	<c:url value="${collBaseUrl}" var="setsRequest">
		<c:param name="verb" value="ListSets"/>
	</c:url>
	
	<xtags:parse id="listSets">
		<xtags:style xsl="/remove_namespace.xsl" xml="${setsRequest}"/>
	</xtags:parse>
		
	<xtags:if test="count($listSets/OAI-PMH/ListSets/set) > 0">
		<c:set var="setsSelectList">
			<select name="sets">
				<option value=''> -- All sets --</option>
				<xtags:forEach select="$listSets/OAI-PMH/ListSets/set" sort="setSpec">
					<c:set var="curSpec"><xtags:valueOf select="setSpec"/></c:set>
					<c:set var="curName"><xtags:valueOf select="setName"/></c:set>
					<option value='${curSpec}'>
						<c:choose>
							<c:when test="${empty fn:trim(curName)}">
								${curSpec}
							</c:when>
							<c:otherwise>
								[${curSpec}] ${curName}
							</c:otherwise>
						</c:choose>
					</option>
				</xtags:forEach>
			</select>
		</c:set>
	</xtags:if>
</c:catch>	

<c:if test="${not empty listSetsError}">
	<!-- Error with ListSets request: ${listSetsError} -->
</c:if>


<%-- Create the formats selection list --%>
<c:catch var="listFormatsError">

	<%-- Construct the OAI request (URL) --%>
	<c:url value="${collBaseUrl}" var="identifyRequest">
		<c:param name="verb" value="Identify"/>
	</c:url>

	<%-- Construct the OAI request (URL) --%>
	<c:url value="${collBaseUrl}" var="formatsRequest">
		<c:param name="verb" value="ListMetadataFormats"/>
	</c:url>
	
	<%-- Perform the web service request for ListMetadataFormats --%>
	<c:if test="${empty formatsRequestOutput}">	
		<c:set var="formatsRequestOutput">${f:replaceFirst(f:timedImportUsingEncoding(formatsRequest,"UTF-8",4000),".*\\<\\?xml.*\\?\\>","")}</c:set>
	</c:if>
	
	<%-- Remove namespaces from the XML and create the DOM --%>
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${formatsRequestOutput}" var="listFormatsDom"/>
	
	<%-- Create the sets selection list, if sets are present --%>
	<x:set var="formatNodes" select="$listFormatsDom/OAI-PMH/ListMetadataFormats/metadataFormat"/>
	<x:if select="count($formatNodes) > 0">
		<c:set var="formatsSelectList">
			<select name="formats">
				<x:forEach select="$formatNodes">
					<c:set var="format"><x:out select="metadataPrefix"/></c:set>
					<option value='${format}'>${format}</option>
				</x:forEach>
			</select>
		</c:set>
	</x:if>
</c:catch>	
<c:if test="${empty formatsSelectList}">
	<c:set var="formatsSelectList">
		<select name="formats">
			<option value='oai_dc'>oai_dc</option>
			<option value='nsdl_dc'>nsdl_dc</option>
		</select>
	</c:set>
</c:if>

<p>
	Submit OAI-PMH requests to
	${topTab == "explorer" ? "an OAI" : "this collections"} data provider or Static OAI Repository
	and view or validate the XML responses. 
	This page assumes familiarity with <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm" target="_blank">OAI-PMH</a>
	and the <a href="http://www.openarchives.org/OAI/2.0/guidelines-static-repository.htm" target="_blank">Static OAI Repository</a> specification.
</p>

<c:if test="${empty collBaseUrl}">
	<p>
		To begin, please indicate an OAI data provider to explore:
	</p>
</c:if>

<form name="baseUrl" style="padding-top:0; padding-bottom:0; margin-top:0; margin-bottom:0; ">
	<p>
		Base URL or Static Repository URL: <input type="text" name="baseUrl" value="${collBaseUrl}" size="50"/>
		<c:if test="${explorerContext == 'standAlone'}">
			<input title="Update the Base URL" type="button" value="set URL" onClick='doUpdateBaseUrl()'/>
		</c:if>
	</p>
</form>

<c:if test="${not empty identifyDom}">
    <c:set var="staticRepoSchema"><x:out select="$identifyDom/*[local-name()='Repository']/@xsi:schemaLocation"/></c:set>
    <c:set var="isStaticRepo" value="${fn:contains(staticRepoSchema,'www.openarchives.org/OAI/2.0/static-repository.xsd')}"/>

    <c:choose>
        <c:when test="${isStaticRepo}">
            <c:set var="repositoryName"><x:out select="$identifyDom/Repository/Identify/repositoryName"/></c:set>
            <c:set var="adminEmail"><x:out select="$identifyDom/Repository/Identify/adminEmail"/></c:set>
        </c:when>
        <c:otherwise>
            <c:set var="repositoryName"><x:out select="$identifyDom/OAI-PMH/Identify/repositoryName"/></c:set>
            <c:set var="adminEmail"><x:out select="$identifyDom/OAI-PMH/Identify/adminEmail"/></c:set>
        </c:otherwise>
    </c:choose>

	<c:if test="${not empty repositoryName}">
		<div style="font-weight: bold;">Repository name: <span id="repositoryName">${repositoryName}</span></div>
	</c:if>
	<c:if test="${not empty adminEmail}">
		<div>Admin e-mail: <a href="mailto:${adminEmail}"><span id="adminEmail">${adminEmail}</span></a></div>
	</c:if>

	 <div style="margin-top:1em;">
	    <span style="font-weight: bold;">${isStaticRepo ? 'Static Repsository URL':'Base URL'}</span>:
	    <br>
	    ${collBaseUrl}
	 </div>
</c:if>

    <c:if test="${not empty param.collectionId}">
        <c:url value="/collection_details.jsp" var="collInfoUrl">
            <c:param name="id" value="${param.collectionId}"/>
        </c:url>
        <p style="font-weight: bold;"><a href="${collInfoUrl}">Return to Collection Harvest Overview</a></p>
    </c:if>

<c:if test="${not empty collBaseUrl}">


        <c:if test="${isStaticRepo || empty isStaticRepo}">


           <table style="margin-top:2em">
                <%-- Static Repository --%>
                <tr>
                    <td colspan=3>
                        <a href="http://www.openarchives.org/OAI/2.0/guidelines-static-repository.htm#SR_example" target="_blank" class="boldlink" title="See Static Repository Example">Static Repository</a>
                    </td>
                </tr>
                <tr>
                    <td colspan=3>
                        <form name="StaticRepoForm" action="">
                            <input title="View the Static Repository response" type="button" value="view response" onClick='mkStaticRepoRequest()'>
                            <input title="Validate the Static Repository response" type="button" value="validate response" onClick='mkStaticRepoRequest("validate")'>
                        </form>
                    </td>
                </tr>
            </table>
        </c:if>
        <c:if test="${!isStaticRepo || empty isStaticRepo}">

            <c:if test="${not empty identifyError}">
                <!-- Error with Identify request: ${identifyError} -->
                <c:if test="${fn:containsIgnoreCase(identifyError,'ParseException')}">
                    <p>
                        <i>Note: There was problem parsing the Identify response from this provider:</i> <div style="margin-left:1em;"><code>${identifyError}</code></div>
                    </p>
                </c:if>
            </c:if>


            <c:if test="${empty formatsRequestOutput || not empty listFormatsError}">
                <p>
                    <i>Note: There was no response to the ListMetadataFormats request. This data provider may be off line at this time. Refresh your browser to try again.</i>
                </p>
                <!-- Error: ${listFormatsError} -->
            </c:if>

            <%-- ListIdentifiers, ListRecords --%>
            <form name="listRecordsIdentifers" action='javascript:mkListRecordsIdentifers("")'>
                <table style="margin-top:10px;">
                    <tr>
                        <td colspan=3>
                            <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords" target="_blank" class="boldlink" title="See description of ListRecords">ListRecords</a>
                            and
                            <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers" target="_blank" class="boldlink" title="See description of ListIdentifiers">ListIdentifiers</a>
                        </td>
                    </tr>

                    <tr>
                        <td nowrap>
                            Choose verb:
                        </td>

                        <td nowrap colspan=2>
                            <select name="verb">
                                <option value="ListRecords">ListRecords</option>
                                <option value="ListIdentifiers">ListIdentifiers</option>
                            </select>
                            <input title="View the ListRecords or ListIdentifiers response" type="button" value="view response" onClick='mkListRecordsIdentifers("")'>
                            <input title="Validate the ListRecords or ListIdentifiers response" type="button" value="validate response" onClick='mkListRecordsIdentifers("validate")'>
                        </td>
                    </tr>
                    <c:if test="${not empty setsSelectList}">
                        <tr>
                            <td nowrap>Set:</td>
                            <td nowrap colspan=2>${setsSelectList}</td>
                        </tr>
                    </c:if>
                    <tr>
                        <td nowrap>Format:</td>
                        <td nowrap colspan=2>${formatsSelectList}</td>
                    </tr>
                    <tr>
                        <td colspan=3 height=20>&nbsp;</td>
                    </tr>
                </table>
            </form>

            <table>
                <%-- ListSets --%>
                <tr>
                    <td colspan=3>
                        <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListSets" target="_blank" class="boldlink" title="See description of ListSets">ListSets</a>
                    </td>
                </tr>
                <tr>

                    <td colspan=3>
                        <form name="ListSetsForm" action="">
                            <input title="View the ListSets response" type="button" value="view response" onClick='mkSimpleVerbRequest("ListSets")'>
                            <input title="Validate the ListSets response" type="button" value="validate response" onClick='mkSimpleVerbRequest("ListSets","validate")'>
                        </form>
                    </td>

                </tr>

                <tr>
                    <td colspan=3 height=20>&nbsp;</td>
                </tr>

                <%-- ListMetadataFormats --%>
                <tr>
                    <td colspan=3>
                        <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListMetadataFormats" target="_blank" class="boldlink" title="See description of ListMetadataFormats">ListMetadataFormats</a>
                    </td>
                </tr>
                <tr>

                    <td colspan=3>
                        <form name="ListMetadataFormatsForm" action="">
                            <input title="View the ListMetadataFormats response" type="button" value="view response" onClick='mkSimpleVerbRequest("ListMetadataFormats")'>
                            <input title="Validate the ListMetadataFormats response" type="button" value="validate response" onClick='mkSimpleVerbRequest("ListMetadataFormats","validate")'>
                        </form>
                    </td>

                </tr>
                <tr>
                    <td colspan=3 height=20>&nbsp;</td>
                </tr>

                <%-- Identify --%>
                <tr>
                    <td colspan=3>
                        <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Identify" target="_blank" class="boldlink" title="See description of Identify">Identify</a>
                    </td>
                </tr>
                <tr>

                    <td colspan=3>
                        <form name="IdentifyForm" action="">
                            <input title="View the Identify response" type="button" value="view response" onClick='mkSimpleVerbRequest("Identify")'>
                            <input title="Validate the Identify response" type="button" value="validate response" onClick='mkSimpleVerbRequest("Identify","validate")'>
                        </form>
                    </td>

                </tr>
            </table>

            <div style="height:15px"></div>

            <%-- GetRecord --%>
            <form name="getRecordForm" action='javascript:mkGetRecord("")'>
                <table>
                    <tr>
                        <td colspan=3 nowrap>
                            <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#GetRecord" title="See description of GetRecord" class="boldlink" target="_blank">GetRecord</a>
                        </td>
                    </tr>
                    <tr>
                        <td nowrap>
                            Enter ID:
                        </td>
                        <td nowrap colspan=2>
                            <input type="text" name="identifier" size="30">
                            <input title="View the GetRecord response" type="button" value="view response" onClick='mkGetRecord("")'>
                            <input title="Validate the GetRecord response" type="button" value="validate response" onClick='mkGetRecord("validate")'>
                        </td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td nowrap colspan=2>
                            Return in format:
                            ${formatsSelectList}
                        </td>
                    </tr>
                </table>
            </form>

            <%-- ResumptionToken --%>
            <table>
                <tr>
                    <td colspan=3 height=20>&nbsp;

                    </td>
                </tr>
                <tr>
                    <td colspan=3>
                        <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#FlowControl" target="_blank" class="boldlink" title="See description of flow control and resumptionToken">ResumptionToken</a>
                    </td>
                </tr>
                <tr>
                    <td nowrap>
                        Enter a token:
                    </td>
                    <td colspan=2>
                        <form name="resumptionForm" action="javascript:mkResume("")">
                            <input type="text" name="resumptionToken" size="30">
                            <select name="verb">
                                <option value="ListRecords">ListRecords</option>
                                <option value="ListIdentifiers">ListIdentifiers</option>
                            </select>
                            <input title="Resume and view the ListRecords or ListIdentifiers response" type="button" value="view response" onClick="mkResume('')">
                            <input title="Resume and validate the ListRecords or ListIdentifiers response" type="button" value="validate response" onClick="mkResume('validate')">
                        </form>
                    </td>
                </tr>
            </table>

        </c:if>

	
	<table height="10">
	  <tr> 
		<td>&nbsp;</td>
	  </tr>
	</table>
	
</c:if>




