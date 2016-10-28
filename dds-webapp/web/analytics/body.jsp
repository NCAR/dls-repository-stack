<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<%@ page import="java.util.Date" %>

<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-nested.tld" prefix="nested" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/response.tld" prefix="resp" %>
<%@ taglib uri="/WEB-INF/tlds/request.tld" prefix="req" %>
<%@ taglib uri="/WEB-INF/tlds/datetime.tld" prefix="dt" %>
<%@ taglib uri='/WEB-INF/tlds/utils.tld' prefix='util' %>
<%@ include file="/JSTLTagLibIncludes.jsp" %>


<util:sortMap var="sorted_dataTypes" map="${rsf.dataTypes}" sortKey="key"/>

<div class="dropdown" id='show_hide_options'>
	Options <span id='dropdown_icon'>&#x25BC;</span>
</div>
<a href="#" id="id_help_link" >Help</a>

<div id="graph_options" >
	<html:form action="/repository_analytics" onsubmit="return isFormValid()" method="get">
		
		<div class="graph_option">
			<label title="The dates from which the snapshot data should be extracted from. Default dates are span all available data. Moving these dates outside those bounds will be automatic zeros. ">Dates:</label>
			<html:text property="fromDate" styleId="id_from_date" styleClass="datepicker"/>
			Through: <html:text property="toDate" styleId="id_to_date" styleClass="datepicker"/>
		</div>
		<div class="graph_option">
			<label title="Which Data Type facet should be graphed on.">Data Type:</label>
			<html:select property="dataType" styleId="dataType">
				<logic:iterate id="dataType" name="sorted_dataTypes">
					<html:option value="${dataType.key}">${dataType.key}</html:option>
				</logic:iterate>
			</html:select>
			<html:submit styleId="graph_button">Graph</html:submit> 
		</div>
		<div class="graph_option">
			<label title="Whether or not to to sum all the data type options together as one trend line. ">Cumulative:</label>
			<html:radio property="cumulative" value="true"/>True
			<html:radio property="cumulative" value="false" />False
		</div>
		
		<div id='selectCollections' style="display:none;">
			<div>
				<label title="Which collections' data should be included when extracting the data.">Collections:</label>
				
				<div style="margin-left:20px">
					<html:radio property="collectionSelectionOption" value="all" />All 
					
					<br/>
					
					<html:radio property="collectionSelectionOption" value="selected" />Selected (Click to Choose)
				</div>
				
			</div>
			
			<div id="collections" class="collections" style="display:none;">
				<util:sortMap var="sortedCollections" map="${rsf.collections}" sortKey="value"/>
				<a id="collections_select_all" href="#">Select All</a> / <a id="collections_deselect_all" href="#">Deselect All</a>  <br />
				
				<logic:iterate id="collection" name="sortedCollections">
					<html:multibox property="selectedCollections" styleClass="collection">${collection.key}</html:multibox>${collection.value}
					<br/>
				</logic:iterate>
			</div>
		</div>
		
		<div>
			<label title="Which data type options should be included in the graph. These are dependent on which Data Type is selected.">Options:</label>
			
			<div style="margin-left:20px">
				<html:radio property="dataTypeSelectionOption" value="all" />All 
				
				<br/>
				
				<html:radio property="dataTypeSelectionOption" value="selected" />Selected (Click to Choose)
				
			</div>
			
		</div>
		
		<logic:iterate id="dataType" name="rsf" property="dataTypes">
			<div id="${dataType.key}_dataTypeOptions" class="dataTypeOptions" style="display:none;">
				
				<util:sortMap var="sorted_dataTypeOptions" map="${dataType.value}" sortKey="value"/>
				
				<a href="#" class="data_types_select_all">Select All</a> / <a href="#" class="data_types_deselect_all">Deselect All</a>  <br />

				<logic:iterate id="dataTypeOption" name="sorted_dataTypeOptions">
					<script>dataTypeOptionsMappings['${dataTypeOption.key}'] = '${fn:escapeXml(dataTypeOption.value)}'</script>
					<html:multibox property="selectedDataTypeOptions" styleClass="dataTypeOption">${dataTypeOption.key}</html:multibox>${dataTypeOption.value}
					<br/>
				</logic:iterate>
			</div>
		</logic:iterate>
		<br/>
		<div class="graph_option">
			<label title="Whether or not to add a Data Type option label at the end of each trend line for clarity.">Line Labels:</label>
			<html:radio property="lineLabels" value="true"/>True
			<html:radio property="lineLabels" value="false" />False
		</div>
	</html:form>
	<div class="graph_option">
		<label title="Export the graph to an external file. For downloading and printing.">Download:</label> <a href="javascript:submit_download_form('pdf')">PDF</a>, <a href="javascript:submit_download_form('jpg')">JPG</a>, <a href="javascript:submit_download_form('png')">PNG</a>
	</div>
</div>
<div id="graph"></div>
<form action="svg_transcoder.do" method="post" id="id_svg_form">
	<input type="hidden" name="svg" id="id_svg_raw"/>
	<input type="hidden" name="output_format" id="id_output_format"/>
	<input type="hidden" name="svg_style_sheet_url" id="svg_style_sheet_url" value=""/>
</form>
<div class="tooltip" id="tooltip" style="opacity:0"></div>