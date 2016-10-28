<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.RepositoryManager" %>

<jsp:useBean id="helperBean" class="edu.ucar.dls.services.dds.DDSServicesUIHelperBean" scope="application"/>
<jsp:useBean id="opsf" class="edu.ucar.dls.oai.provider.action.form.OaiPmhSearchForm" scope="application"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>
<c:set var="rm" value="${applicationScope.repositoryManager}"/>

<c:set var="maxSetLength" value="45"/>

<html>

<c:set var="title" value="OAI Data Provider"/>

<head>
	<title>${title}</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	
	<%-- template header (CSS styles and JavaScript references) --%>
	<%@ include file="/nav/head.jsp" %> 
	 
	<style>
		/* A pseudo class 'boldlink' for the A tag */
		A.boldlink:link, A.boldlink:hover, A.boldlink:visited, A.boldlink:active {
			text-decoration: none; 
			font-weight: bold;
		}
		
		/* A pseudo class 'boldlink' for the A tag */
		A.boldlink:hover {
			text-decoration: underline; 
		}		
	</style>
	<script type="text/javascript" src='oai_explorer_script.js'></script>
	<script language="JavaScript">
		var advanced = "off";
	</script>

</head>


<c:set var="myBaseUrl" value="${initParam.oaiBaseUrlDisplay}"/>
<c:if test="${!isDeploayedAtDL || empty myBaseUrl}">
	<c:set var="myBaseUrl"><%=((RepositoryManager)getServletContext().getAttribute("repositoryManager")).getProviderBaseUrl(request)%></c:set>
</c:if>

<c:set var="useBaseUrl" scope="session">
	<c:choose>
		<c:when test="${not empty param.baseUrl}">${param.baseUrl}</c:when>
		<c:when test="${not empty useBaseUrl}">${useBaseUrl}</c:when>
		<c:otherwise>${myBaseUrl}</c:otherwise>
	</c:choose>
</c:set>


<%-- Create the sets selection list --%>
<c:catch var="listSetsError">
	<%-- Construct the OAI request (URL) --%>
	<c:url value="${useBaseUrl}" var="setsRequest">
		<c:param name="verb" value="ListSets"/>
	</c:url>
	
	<%-- Perform the web service request --%>	
	<c:import url="${setsRequest}" var="setsRequestOutput" charEncoding="UTF-8" />
	
	<%-- Remove namespaces from the XML and create the DOM --%>
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${setsRequestOutput}" var="listSets"/>
	
	<%-- Create the sets selection list, if sets are present --%>
	<x:set var="setNodes" select="$listSets/OAI-PMH/ListSets/set"/>
	<x:if select="count($setNodes) > 0">
		<c:set var="setsSelectList">
			<select name="sets">
				<option value=''> -- All sets --</option>				
				<x:forEach select="$setNodes">
					<c:set var="curSpec"><x:out select="setSpec"/></c:set>
					<c:set var="curName"><x:out select="setName"/></c:set>
					<c:set var="curName">${empty fn:trim(curName) ? curSpec : curName}</c:set>					
					<c:if test="${ fn:length(curName) > maxSetLength }">
						<c:set var="curName">${ fn:substring(curName,0,maxSetLength)} ...</c:set>	
					</c:if>
					<option value='${curSpec}'>${curName}</option>
				</x:forEach>
			</select>
		</c:set>
	</x:if>
</c:catch>	

<%-- Create the formats selection list --%>
<c:catch var="listFormatsError">
	<%-- Construct the OAI request (URL) --%>
	<c:url value="${useBaseUrl}" var="formatsRequest">
		<c:param name="verb" value="ListMetadataFormats"/>
	</c:url>
	
	<%-- Perform the web service request --%>	
	<c:import url="${formatsRequest}" var="formatsRequestOutput" charEncoding="UTF-8" />
	
	<%-- Remove namespaces from the XML and create the DOM --%>
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${formatsRequestOutput}" var="listFormats"/>
	
	<%-- Create the sets selection list, if sets are present --%>
	<x:set var="formatNodes" select="$listFormats/OAI-PMH/ListMetadataFormats/metadataFormat"/>
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
			<option value=''> -- None available --</option>
		</select>
	</c:set>
</c:if>

<body bgcolor="#FFFFFF" text="#000000" 
link="#220066" vlink="#006600" alink="#220066" onLoad="document.getRecordForm.identifier.focus()">
<!-- Sub-title just underneath logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   OAI <br>
   Data Provider 
</div>


<%-- Include the nav and top html --%>
<%@ include file="/nav/top.jsp" %>


    <%--If we have a description, then the admin has set up the repo, so go ahead and display repo title and description --%>
    <c:choose>
        <c:when test="${not empty rm.descriptions[0]}">
            <h1>${rm.repositoryName}</h1>
            <p>${rm.descriptions[0]}</p>
            <hr/>
            <br/>
        </c:when>
        <c:otherwise>
            <h1>${title}</h1>
        </c:otherwise>
    </c:choose>

	<p> 
		Items in the repository can be harvested using the <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm">Open Archives Initiative Protocol for Metadata Harvesting</a> (OAI-PMH).
		<c:if test="${!isDeploayedAtDL}">
			The entire repository can be harvested as a whole or collections may be harvested individually using the
            OAI <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Set">set</a> construct.
		</c:if>
	</p>  		
		
	<h3>Explorer</h3>
	
	  <p>Submit OAI-PMH requests to the data provider using the forms below 
	  and view or validate the XML responses. 
	  This page assumes familiarity with <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm">OAI-PMH requests</a>.
	  </p>	   
	  
	  <form name="baseUrl" style="padding-top:0; padding-bottom:0; margin-top:0; margin-bottom:0; ">
		  <p>
				Explore baseURL: <input type="text" name="baseUrl" value="${useBaseUrl}" size="50"/>
				<input title="Update Base URL" type="submit" value="Update Base URL"/>
				<br/><span style="color:666666">(The baseURL for this data provider is: ${myBaseUrl})</span>
		  </p>
	  </form>

	<table style="margin-top:10px;">		
		<%-- ListIdentifiers, ListRecords --%>		
		<tr>
			<td colspan=3>
				<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers" target="_blank" class="boldlink" title="See description of ListIdentifiers">ListIdentifiers</a>		
				and
				<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords" target="_blank" class="boldlink" title="See description of ListRecords">ListRecords</a>
			</td>
		</tr>
		<tr>
			<td nowrap>
				Choose verb:
			</td>
			<form name="listRecordsIdentifers" action='javascript:mkListRecordsIdentifers("")'>
			<td nowrap colspan=2>
				<select name="verb">
					<option value="ListRecords">ListRecords</option>
					<option value="ListIdentifiers">ListIdentifiers</option>
				</select>
				<c:if test="${not empty setsSelectList}">
					&nbsp;Set:
					${setsSelectList}
				</c:if>
				&nbsp;Format:
				${formatsSelectList}
				<input title="View the ListRecords or ListIdentifiers response" type="button" value="view" onClick='mkListRecordsIdentifers("")'>
				<input title="Validate the ListRecords or ListIdentifiers response" type="button" value="validate" onClick='mkListRecordsIdentifers("rt=validate")'>
			</td>		
			<tr>
				<td>&nbsp;</td>
				<td align=right>
					Records modified since:
				</td>
				<td>
					<select name="from">
						<option value='none'> -- No value -- </option>
						<c:forEach items="${opsf.utcDates}" var="datePair">
							<option value='${datePair.date}'>${datePair.label}</option>	
						</c:forEach>
					</select>
					&nbsp;[ <a href="">update time</a> ]
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td align=right>
					Records modified before:
				</td>
				<td>
					<select name="until">
						<option value='none'> -- No value -- </option>			
						<c:forEach items="${opsf.utcDates}" var="datePair">
							<option value='${datePair.date}'>${datePair.label}</option>	
						</c:forEach>
					</select>
					&nbsp;[ <a href="">update time</a> ]
				</td>
			</tr>
			</form>			

		<tr>
			<td colspan=3 height=20>&nbsp;
				
			</td>
		</tr>
		</table>
		
		<%-- GetRecord --%>
		<table>
		<form name="getRecordForm" action='javascript:mkGetRecord("")'>
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
				<input title="View the GetRecord response" type="button" value="view" onClick='mkGetRecord("")'>
				<input title="Validate the GetRecord response" type="button" value="validate" onClick='mkGetRecord("rt=validate")'>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td nowrap colspan=2>			
				Return in format:
				${formatsSelectList}
			</td>
		</tr>
		</form>	
		</table>
		
				


		
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
			<form name="resumptionForm" action="javascript:mkResume("")">
			<td colspan=2>
				<input type="text" name="resumptionToken" size="30">
				<select name="verb">
                    <option value="ListRecords">ListRecords</option>
					<option value="ListIdentifiers">ListIdentifiers</option>
				</select>
				<input title="Resume and view the ListRecords or ListIdentifiers response" type="button" value="view" onClick="mkResume('')">
				<input title="Resume and validate the ListRecords or ListIdentifiers response" type="button" value="validate" onClick="mkResume('rt=validate')">
			</td>
			</form>	
		</tr>

		<tr>
			<td colspan=3 height=20>&nbsp;
				
			</td>
		</tr>

		<%-- Identify --%>		
		<tr>
			<td colspan=3>
				<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Identify" target="_blank" class="boldlink" title="See description of Identify">Identify</a>
			</td>
		</tr>
		<tr>
			<form name="Identify" action="">
			<td colspan=3>
				<input title="View the Identify response" type="button" value="view" onClick='window.location = getBaseUrl() + "?verb=Identify"'>
				<input title="Valicate the Identify response" type="button" value="validate" onClick='window.location = getBaseUrl() + "?verb=Identify&rt=validate"'>
			</td>
			</form>	
		</tr>			
		<tr>
			<td colspan=3 height=20>&nbsp;
				
			</td>
		</tr>		
		
		<%-- ListSets --%>		
		<tr>
			<td colspan=3>
				<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListSets" target="_blank" class="boldlink" title="See description of ListSets">ListSets</a>
			</td>
		</tr>
		<tr>
			<form name="ListSets" action="">
			<td colspan=3>
				<input title="View the ListSets response" type="button" value="view" onClick='window.location = getBaseUrl() + "?verb=ListSets"'>
				<input title="Validate the ListSets response" type="button" value="validate" onClick='window.location = getBaseUrl() + "?verb=ListSets&rt=validate"'>
			</td>
			</form>	
		</tr>		

		<tr>
			<td colspan=3 height=20>&nbsp;
				
			</td>
		</tr>
		
		<%-- ListMetadataFormats --%>		
		<tr>
			<td colspan=3>
				<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListMetadataFormats" target="_blank" class="boldlink" title="See description of ListMetadataFormats">ListMetadataFormats</a>
			</td>
		</tr>
		<tr>
			<form name="ListMetadataFormats" action="">
			<td colspan=3>
				<input title="View the ListMetadataFormats response" type="button" value="view" onClick='window.location = getBaseUrl() + "?verb=ListMetadataFormats"'>
				<input title="Validate the ListMetadataFormats response" type="button" value="validate" onClick='window.location = getBaseUrl() + "?verb=ListMetadataFormats&rt=validate"'>
			</td>
			</form>	
		</tr>				
	</table>
	
	  
<table height="10">
  <tr> 
    <td>&nbsp;</td>
  </tr>
</table>


<%-- Include bottom html --%>
<%@ include file="/nav/bottom.jsp" %>    

   
</body>
</html>


