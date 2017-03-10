<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="../../TagLibIncludes.jsp" %>
<html>
<head>
	<title>Harvest Repository Manager Settings and Configuration</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
	
	<%-- Include style/menu templates --%>
	<%@ include file="../../head.jsp" %>
	

</head>
<body>
<%-- Include style/menu templates --%>
<c:import url="../top.jsp" />
<div id="breadcrumbs">
	<a href='<c:url value="/admin"/>' class="collTitleLnk">Admin</a>
	&gt;
	Server Configuration
</div>
<h1>Harvest Repository Manager Settings and Configuration</h1>

<p> 	     	  
      View the current software settings and configuration. 
	  To change the settings, edit the context parameters defined in the
	  Tomcat server.xml file, which overrides the default values in the
	  context's WEB-INF/web.xml file. Restart Tomcat to apply new settings.
</p>

<h3>E-mail Notifier Settings</h3>
<p>
	<div class="paramTitle">To e-mail address</div>
	<div class="paramValue">${empty initParam.toEmails ? '-None supplied-' : initParam.toEmails}</div>
</p>

<p>
	<div class="paramTitle">URL link to Harvest Repository Manager sent in e-mail</div>
	<div class="paramValue">${initParam.harvestManagerUrl}</div>
</p>
<p>
	<div class="paramTitle">Mail server</div>
	<div class="paramValue">${initParam.mailServer}</div>
</p>

<h3>UCAR Collection System (DCS) Settings</h3>
<p>
	<div class="paramTitle">DCS DDSWS Web service baseURL (to pull in the harvest collection records)</div>
	<div class="paramValue">${initParam.ncsWebServiceURL}</div>
</p>
<p>
	<div class="paramTitle">DCS Editor URL (to link to edit the collection records)</div>
	<div class="paramValue">${initParam.ncsViewEditRecordUrl}</div>
</p>

<h3>Harvest Settings</h3>

<p>
<div class="paramTitle">The URI path where the config files for the different native formats are located. ie, nsdl_dc.xml, oai_dc.xml etc..
Edit and deploy these files to make changes in the ingest configuration, then re-harvest or re-process the collections
to apply those changes.</div>
<div class="paramValue">${initParam.ingestorConfigsURI}</div>
</p>

<p>
<div class="paramTitle">Re-harvest automatically on the last day of each month to re-normalize and re-process each record
	according to the latest harvest configurations (otherwise manual only).</div>
<div class="paramValue">${initParam.reprocessCollectionsMonthly}</div>
</p>

<p>
	<div class="paramTitle">Database where ingest schemas and tables reside</div>
	<div class="paramValue">${initParam.harvestDbUrl}</div>
</p>

<p>
<div class="paramTitle">Time of day in which harvests are triggered (24 h)</div>
<div class="paramValue">
    <c:choose>
        <c:when test="${initParam.harvestCheckInterval != '86400'}">
            Automatic harvests are disabled. Manual harvests only.
        </c:when>
        <c:otherwise>
            ${initParam.harvestCheckTime}
        </c:otherwise>
    </c:choose>
</div>
</p>

<p>
	<div class="paramTitle">Max amount of threads that can be running at one time. Once this number is reached an exception is thrown
			due to the fact that threads are getting hung up somewhere</div>
	<div class="paramValue">${initParam.maxIngestorThreads}</div>
</p>
<p>
	<div class="paramTitle">The base path where the Ingestor workspace and permanent files should be stored.</div>
	<div class="paramValue">${initParam.ingestorBaseFilePathStorage}</div>
</p>
<p>
	<div class="paramTitle">Threshold for how many records are allowed to error out before the entire harvest is aborted.</div>
	<div class="paramValue"><fmt:formatNumber type="percent" 
            maxIntegerDigits="3" value="${initParam.maxErrorThreshold}" /></div>
</p>

<p>
	<div class="paramTitle">Max number of examples that will be created when an issue happens within a individual processor(Note only applies to record processors).</div>
	<div class="paramValue">${initParam.maxProcessorIssueReportingExamples}</div>
</p>

<p>
	<div class="paramTitle">Minimum threshold for the difference in record counts between the previous harvest and the new one. 
			If this threshold is not met, a warning will be added to the harvest but will be allowed to go forward.</div>
	<div class="paramValue"><fmt:formatNumber type="percent" 
            maxIntegerDigits="3" value="${initParam.minRecordCountDiscrepencyThreshold}" /> </div>
</p>

<p>
	<div class="paramTitle">URL to get or create resource and metadata handles.</div>
	<div class="paramValue">${initParam.handleServiceURL}</div>
</p>
<p>
	<div class="paramTitle">Zip the harvest files in a compressed file and delete single files.</div>
	<div class="paramValue">${initParam.zipHarvestFiles}</div>
</p>

<h3>Harvest Repository Manager Persistence</h3>
<p>
<div class="paramTitle">Harvest Repository Manager persistent files stored at</div>
<div class="paramValue">${initParam.harvestManagerPersistentDataDir}</div>
</p>




<%-- Include style/menu templates --%>
<%@ include file="../../bottom.jsp" %>
</body>
</html>

