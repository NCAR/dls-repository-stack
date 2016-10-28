<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="../../TagLibIncludes.jsp" %>
<html>
<head>
	<title>Harvest Repository Manager Reports</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	
	<%-- Include style/menu templates --%>
	<%@ include file="../../head.jsp" %>
	<script>
	
	document.observe("dom:loaded", function() {
			setupTSVReport();
		});
	
	
		function runAlignmentReport(type)
		{
			if(type=='zip')
				report = "educationLevelsVsAlignmentsReportZip";
			else if(type=='xml')
				report = "educationLevelsVsAlignmentsReportConcat";
			else
				report = "educationLevelsVsAlignmentsAsnReport";
			var data = {'report':report, 'day_padding':$('id_day_padding').getValue()};
			runReport(data);
		}
		
		function runReport(data)
		{
			window.location.href = "runReport.do?" + EncodeQueryData(data);	
		}
		
		function EncodeQueryData(data)
		{
		   var ret = [];
		   for (var d in data)
		      ret.push(encodeURIComponent(d) + "=" + encodeURIComponent(data[d]));
		   return ret.join("&");
		}
		
		// All this javascript is specifically for the TSV report. Sets up pre saved criteria
		
		var metadata_xpath_prefix = "metadata/nsdl_dc"; 
		var storedContent_paramters = "Handle:head/id\n"+
		 "Comment:storedContent/content\n"+
		 "Title:"+metadata_xpath_prefix+"/title\n"+
		 "Type:"+metadata_xpath_prefix+"/type\n"+
		 "Subject:"+metadata_xpath_prefix+"/subject\n"+
		 "EdLevel:"+metadata_xpath_prefix+"/educationLevel\n";
		
		// These were requested criteria that was asked for by clients. May be altered and added too.
		var annoComments = ["anno comments", ["http://ucarconnect_placeholder/oai_dds/services/ddsws1-1?verb=Search&q=indexedXpaths:\"/comm_anno/text\"&storedContent=/relation.isAnnotatedBy//text//comm_anno/text&xmlFormat=nsdl_dc", 
		                                      storedContent_paramters]];
		
		
		var paradataComments = ["paradata comments", ["http://ucarconnect_placeholder/oai_dds/services/ddsws1-1?verb=Search&q=indexedXpaths:\"/commParadata/usageDataSummary/string\"&storedContent=/relation.paradataProvidedBy//text//commParadata/usageDataSummary/string&xmlFormat=nsdl_dc", 
		                                              storedContent_paramters]];

		
		var tsvExamples = [annoComments, paradataComments];
		
		function runTSVReport()
		{
			var data = {'report':"IndexerXpathsToTSVReport", 'base_url':$('id_tsv_baseUrl').getValue(), 
					'parameters':$('id_tsv_parameters').getValue()};
			runReport(data);
		}
		
		function setupTSVReport()
		{
			if(tsvExamples.length>0)
			{
				menu = $('id_presaved_tsv_criteria');
				for(var i=0;i<tsvExamples.length;i++)
					menu.options[menu.options.length] = new Option(tsvExamples[i][0],tsvExamples[i][0]);
				$('id_tsv_baseUrl').value="";
				$('id_tsv_parameters').value="";
				
				menu.observe('change', function(event){
					menu = $('id_presaved_tsv_criteria');
					selected_option = menu.getValue();
					if(selected_option=='None')
					{
						parameters = "";
						base_url = "";
					}
					else
					{
						for(var i=0;i<tsvExamples.length;i++)
						{
							if(selected_option==tsvExamples[i][0])
							{
								base_url = tsvExamples[i][1][0];
								parameters = tsvExamples[i][1][1];
							}
						}
					}
					$('id_tsv_baseUrl').value=base_url;
					$('id_tsv_parameters').value=parameters;
				});	

			}
		}
		
		
	</script>
</head>
<body>
<%-- Include style/menu templates --%>
<c:import url="../top.jsp" />

<h1>Reports</h1>

<p> 	     	
	  <a href="#">Education Levels vs. Alignments Report</a>
      (<a href="javascript:runAlignmentReport('zip')">ZIP</a>,
      <a href="javascript:runAlignmentReport('xml')">XML</a>,
      <a href="javascript:runAlignmentReport('asn')">ASN</a>)&nbsp;&nbsp;
      Day Padding: 
      <select id="id_day_padding">
      	<option value="0">0</option>
      	<option value="1">1</option>
      	<option value="2">2</option>
      	<option value="3">3</option>
      	<option value="4">4</option>
      	<option value="5">5</option>
      </select>
      <br>
      Combines together the alignment reports for all collections. This report can be a ZIP file of 
      all reports or a XML file that contains all reports as a single file. 
      The alignment reports that are used for this report are only pulled from the most recent harvest. 
      A collection alignment report compares what each record has defined as its EducationLevels
      to its alignment's defined educationLevels. If the alignments don't match to the
      defined education levels then its reported. If no issues are found
      a report will not be created. These individual reports can also be found in 
      Harvest Detail underneath the Repository Search tab for a particular
      harvest. Day Padding is selecting which day padding alignment report to pull from. Day
      padding is a way to give the alignments leeway about how close it needs to match the records
      alignment report. For example if the records grade levels were 3 & 5 and day padding was set to 2. 
      The padding report is showing alignments that are not Grade 1 through Grade 7. 

</p>
<br/>
<br/>
<p> 	     	
	  <a href="javascript:runTSVReport()">Indexer Xpaths to CSV Report</a>
	  <br/>
	  <br/>
	  <i>Pre Saved Criteria:</i>
	  </br>
	  <select id="id_presaved_tsv_criteria">
	  	<option value="None">None</option>
	  </select>
	  <br/><br/>
	  <i>Base URL:</i>
	  <br/>
	  <input type="text" id="id_tsv_baseUrl" style="width:498px"/>
	  <br/>
	  <br/>
	  <i>Parameters:</i>
	  <br/>
	  <textarea cols=60 style="height:60px;max-height:60px" id="id_tsv_parameters"></textarea>
      <br><br/>
      Creates a TSV report from a call to the Search web service on the UCAR index. Each record in results will be a single row in the report.
      Xpath parameters are sent into the report which will be used to gather what data should be shown in the report for each record.
      Each field xpath may return more then one value since there is no restriction set. Therefore each field value will be in its own column
      with a header that is indexed. Pre Saved Criteria are reports that were asked for and added to the code to be either re-ran or changed for customization.
      <br/><br/>
      Important things to notice. <br/>
      1) The base URL MUST NOT contain the paging attributes s= and n=. These are added by the report.<br/>
      2) Parameters must be separated by a line break and each parameter must the format title:xpath. Where the title is what will be shown in the header.<br/>
	  3) Xpaths must be a relative path from the record element and should be written without any mention of namespace. The XML is localized prior to the xpaths being executed.
</p>

<%-- Include style/menu templates --%>
<%@ include file="../../bottom.jsp" %>
</body>
</html>

