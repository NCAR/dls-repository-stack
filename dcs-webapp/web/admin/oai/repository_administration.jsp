<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ include file="/baseUrl.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>

<%-- This needs to be saved here since var 'index' is conflicted below --%>
<c:set var="indexLocation" value="${index.indexLocation}"/>
<c:set var="rm" value="${applicationScope.repositoryManager}"/>
<c:set var="title" value="OAI Services"/>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="contextUrl"><%@ include file="/ContextUrl.jsp" %></c:set>

<html:html>

<!-- $Id: repository_administration.jsp,v 1.3 2008/10/14 17:41:03 ostwald Exp $ -->

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

	<%@ include file="/baseHTMLIncludes.jsp" %>
	<link rel="stylesheet" type="text/css" href='${contextPath}/admin/oai/oai_styles.css'>

</head>

<body>

	<st:pageHeader toolLabel="${title}" currentTool="services" />
	
	<st:pageMessages />

	<p>OAI-PMH (Open Archives Initiative - Protocol for Metadata Harvesting)
	exposes the system's metadata for harvesting. Use this page to set repository
	information, encable provider access and explore the provider. 
	</p>
	
	<p><a name="repository"></a><h3>Explore the Provider</h3></p>
	
	<p>
	Submit OAI requests to the system's repository and explore the results.
	</p>
	
	<p>
			<input type="button" value="Access provider explorer"
				onclick="window.location='${contextPath}/admin/oai/oaisearch.do'">
				Go to the OAI Provider.
	</p>
	
	
	<p><a name="repository"></a><h3>Repository Information</h3></p>
	
	<p>
  <form method="post" action="../admin/update_repository_info.do" style="margin-bottom:8px">
			<input type="submit" value="Edit repository info"/>
				<input type="hidden" name="command" value="updateRepositoryInfo"/>
				Update repository name, administrator's email, repository description or namespace identifier.
				
  </form>
	</p>		

	
	<table id="form" cellpadding="6" cellspacing="1" border="0">
		<tr> 
			  <td align="left" id="headrow" style="font-weight:bold;" nowrap>
				Repository name (required):
			  </td>
			  <td id="formrow">
			  	<c:choose>
					<c:when test="${empty rm.repositoryName}">
						<font color="gray"><b>No repository name specified</b>
					</c:when>
					<c:otherwise>
						${rm.repositoryName}
					</c:otherwise>
				</c:choose>			  
			  </td>
		</tr>
		<tr>
			  <td align="left" id="headrow" style="font-weight:bold;"  nowrap>
				Repository administrator's e-mail (required):
			  </td>
			  <td id="formrow">
			  	<c:choose>
					<c:when test="${empty rm.adminEmails[0]}">
						<font color="gray"><b>No e-mail specified </b>
					</c:when>
					<c:otherwise>
						${rm.adminEmails[0]}
					</c:otherwise>
				</c:choose>
			  </td>
		</tr>
		<tr>
			  <td align="left" id="headrow" style="font-weight:bold;"  nowrap>
				Repository description (optional):
			  </td>
			  <td id="formrow">
			  	<c:choose>
					<c:when test="${empty rm.descriptions[0]}">
						<font color="gray"><b>No description specified</b>
					</c:when>
					<c:otherwise>
						<c:out value="${rm.descriptions[0]}" escapeXml="true"/>
					</c:otherwise>
				</c:choose>					  
			  </td>	
		</tr>
		<tr>
			  <td align="left" id="headrow" style="font-weight:bold;"  nowrap>
				Repository namespace identifier (optional):
			  </td>
			  <td id="formrow">
			  	<c:choose>
					<c:when test="${empty rm.repositoryIdentifier}">
						<font color="gray"><b>No namespace identifier specified</b>
					</c:when>
					<c:otherwise>
						${rm.repositoryIdentifier}
					</c:otherwise>
				</c:choose>				  
			  </td>			  
	  </tr>
	  		<tr>
				<td align="left" id="headrow" style="font-weight:bold;"  nowrap>
				Repository base URL (non-editable):
				</td>
				<td id="formrow">
				${myBaseUrl}			  
				</td>			  
	  </tr>
	</table>
	
	  
	<%-- Turn the data provider on/off (enable/disable) --%>
	<table>       
	  <tr>
		<td>
			<br/>  
			<a name="access"></a><h3>Access to the Data Provider</h3>
		</td>
	  </tr>	
	  <tr>
		<td>
		
			<logic:equal value="ENABLED" property="providerStatus" name="raf">
				<html:form 	action="/admin/data-provider-info.do" 
					onsubmit="return confirm( 'Are you sure you want to disable access to the data provider?' )" 
					method="GET">
					<p>
						Access to the data provider is currently <font color="green"><b>ENABLED</b></font>.
						Metadata files are available for harvesting.					</p>
					<p nobr><html:submit property="statusButton" value="Disable provider access" />
				      Prevent all 
					metadata files in the repository from being harvested.</p>
					<input type="hidden" name="providerStatus" value="DISABLED">
					<input type="hidden" name="command" value="changeProviderStatus">
				</html:form>
			</logic:equal>
			<logic:equal value="DISABLED" property="providerStatus" name="raf">		
				<html:form 	action="/admin/data-provider-info.do" 
					onsubmit="return confirm( 'Are you sure you want to enable access to the data provider?' )" 
					method="GET">
					<p>
						Access to the data provider is currently <font color="red"><b>DISABLED</b></font>.
						Your metadata files are not available for harvesting.
					</p>
					<p nobr><html:submit property="statusButton" value="Enable provider access" /> Allows all metadata files in the 
					repository to be harvested.</p>
						<input type="hidden" name="providerStatus" value="ENABLED">
					<input type="hidden" name="command" value="changeProviderStatus">
				</html:form>
			</logic:equal>		
		
	   </td>
	  </tr>
  </table>	  
	
	<%-- Num records per resumption token --%>
	<a name="response"></a><h3>OAI Response Length</h3>	
	<p> 
		The following values define the maximum length of the 
		data provider's response to the OAI 
		<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords" target="_blank">ListRecords</a>
		and
		<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers" target="_blank">ListIdentifiers</a>
		requests. 
		  
		Reduce the length of these responses if a large number of sets have been defined (which requires more processing), 
		system memory is limited or if metadata files are large.			
		Smaller numbers mean that harvesters must make more requests to harvest the entire metadata repository. 
		Larger numbers mean fewer requests are necessary but require greater system resources by both the data provider and the harvester.
		  
	    <html:form 	action="/admin/data-provider-info.do" method="POST" style="margin-bottom:8px">			
			<input type="hidden" name="numRecordsPerToken" value="t">
			<html:submit property="editNumResults" value="Edit response length" />&nbsp;&nbsp; Change the number of returns in the ListRecords and ListIdentifiers
			responses.
		</html:form>
	</p>	
	
	<table width="40%" border="0" cellpadding="6" cellspacing="1" id="form">
		<tr id="headrow">
			<td><b>
			<div align="center">OAI Request</div>
			</b></td>
			<td><b><div align="center">Maximum<br/>
			Response Length</div>
			</b></td>
		</tr>
		<tr id="formrow" nowrap>
			<td>
			  <div align="center"><a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords" target="_blank">ListRecords</a> </div></td>
			<td><div align="center">${rm.numRecordsResults} records</div></td>		
		</tr>
		<tr id="formrow" nowrap>
			<td>
			  <div align="center"><a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers" target="_blank">ListIdentifiers</a></div></td>
			<td><div align="center">${rm.numIdentifiersResults} identifiers</div></td>		
		</tr>		
  </table>

	 <br/><br/>
	 
	<%-- Links to index admin --%>  
<%-- 
	<!-- Index admin via OAI disabled -->
	<table>	
		<tr>		
			<td>
			  <br/>
			  <a name="index"></a><h3>Index administration</h3>			
			</td>
		</tr>		
		<tr>
			<td height="269">			  
				  <p>
				  The data provider maintains an index of all metadata files in the repository. 
				  Each file needs to be indexed before it is available for harvesting or searching. 
				  An index is created automatically each time a new directory of files is 
				  <c:choose>
						<c:when test="${rm.updateFrequency > 0}">
							<c:set var="updateFrequencyTime">
								<c:choose>
									<c:when test="${rm.updateFrequency > 60}">
										<fmt:formatNumber value="${rm.updateFrequency/60}" pattern="#"/> 
										hour${rm.updateFrequency/60 >= 2 ? 's' : ''}
										<c:if test="${rm.updateFrequency%60 != 0}">
											and 
											<fmt:formatNumber value="${rm.updateFrequency%60}" pattern="#"/>
											minute${rm.updateFrequency%60 >= 2 ? 's' : ''}
										</c:if>
									</c:when>
									<c:otherwise>
										${rm.updateFrequency} minute${rm.updateFrequency == 1 ? '' : 's'}
									</c:otherwise>
								</c:choose>
							</c:set>
							configured. The index is updated automatically at regular intervals 
							(every ${updateFrequencyTime}) to reflect 
							changes whenever metadata files are added, modified or deleted.
							The index can also be updated manually at any time using the buttons provided below.
						</c:when>
						<c:otherwise>
							configured, and can be updated manually at any time 
							using the buttons provided below to relect any files 
							you have added, modified or deleted.
						</c:otherwise>
				   </c:choose>			  
				   </p>
				  
				  <table width="100%" cellpadding="0" cellspacing="8" border="0">
					<tr valign="top">
						<td>
							<form action="data-provider-info.do" 
								onsubmit="return confirm( 'This action will update the index for all files in the directories configured above. The process may take several minutes to complete. Continue?' )"
								method="post" style="margin-top:4px;"> 
								
							  <div align="left">
							    <input type="submit" name="command" value="Reindex all files"/>
								<input type="hidden" name="indexAll" value="false"/>
						      </div>
					  </form>					  
					  	</td>
						<td>
							Delete or update each entry in the index, synchronizing the index with 
							the files in the directories. This process occurs
							in the background without disrupting access to the data provider.						</td>						
					</tr>
					<tr valign="top">
						<td>
							<form action="data-provider-info.do" 
								onsubmit="return confirm( 'This action will instruct the indexer to stop at its current stage of progress. Continue?' )"
								method="post" style="margin-top:4px;">
								
							  <div align="left">
							    <input type="hidden" name="command" value="stopIndexing"/>
							    <input type="submit" value="Stop the indexer"/>
						      </div>
					  </form>					  </td>
						<td>
							Stop the indexer at 
							its current stage of operation, if the indexer is currently running.
							Indexing will begin again automatically at the next scheduled update or may be started
							manually by clicking 'Reindex all files' above.						</td>
					</tr>
					<tr valign="top">
						<td>
							<form action="data-provider-info.do" 
								onsubmit="return confirm( 'Warning! This action will DELETE and rebuild the index. All items will become temporarily unavailable until they are reindexed. Continue?' )"
								method="post" style="margin-top:4px;">
								
							  <div align="left">
							    <input type="submit" value="Reset the index"/>
							    <input type="hidden" name="command2" value="rebuildIndex"/>
						      </div>
					  </form>					  </td>
						<td>
							Delete and rebuild the 
							index from scratch. All items become unavailable until they are reindexed. Perform this action only if 
							the index is corrupt or can not be updated properly using the 'Reindex all files' action.						</td>
					</tr>
				</table>

					
		        <table width="100%" cellpadding="0" cellspacing="8" border="0">
                  
                  <tr valign="top">
                    <td width="20%"><FORM>
							  
				      <div align="left">
				        <INPUT TYPE="BUTTON"  VALUE="Check most recent indexing status" ONCLICK="window.location.href='data-provider-info.do?command=showIndexingMessages'">
			          </div>
                    </FORM></td>
                    <td width="80%"> Obtain information about the progress of the indexing process.</td>
                  </tr>
                </table>	
				<logic:greaterThan name="raf" property="numIndexingErrors" value="0">
		        <table width="100%" border="0" cellspacing="8">
                  <tr>
                    <td> Note: Some records had errors during indexing. </td>
                  </tr>
                  <tr valign="top">
                    <td><input name="BUTTON" type="BUTTON" onClick="window.location.href='report.do?q=error:true&s=0&report=Files+that+could+not+be+indexed+due+to+errors'"  value="See indexing errors">
                    Display list of metadata files with their associated indexing errors. </td>
                  </tr>
                </table></logic:greaterThan>		
		        </td>
		</tr>					
				
	  <tr>
	    <td>&nbsp;</td>
      </tr>
	</table>	 
	<!-- end index administration --> 
--%>
	 
<table height="100">
  <tr> 
    <td>&nbsp;</td>
  </tr>
</table>

<%-- Include style/menu templates --%>
<%-- <%@ include file="../../bottom.jsp" %> --%>
</body>
</html:html>


