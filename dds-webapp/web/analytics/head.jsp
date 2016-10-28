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
<%@ page language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="f" uri="http://dls.ucar.edu/tags/dlsELFunctions" %>
<%@ taglib prefix="xtags" uri="http://jakarta.apache.org/taglibs/xtags-1.0" %>
<%@ page isELIgnored ="false" %>

		
	<%-- <link rel='stylesheet' type='text/css' href='/dlese_shared/dlese_styles.css'> --%>
	<style type="text/css">
		HTML BODY H3{
			margin-bottom:5px;
		}
		label
		{
			width:90px;
			display: inline-block
		}
		.dataTypeOptions
		{
			margin-left:50px;
		}
		.collections
		{
			margin-left:50px;
		}
		.graph_option
		{
			margin-bottom:5px;
		}
		#graph_options
		{
			margin-left:1px;
			position:absolute;
			background-color:white;
	
			padding:5px;
			background-color: #E4E4E4;
	   		 border-radius: 0.3125em !important;
	    	box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1) !important;
	    	z-index:5;
		}
		#graph_button
		{
			width:100px;
			
			float:right;
		}
		
		body {
		  font: 10px sans-serif;
		}

		path.highlighted_line{
			stroke-width: 5px;
		}
		 div.tooltip {   
		  position: absolute;                   
                 
		  height: 50px;                 
		  padding: 8px;             
		  font: 12px sans-serif;        
		  background: lightsteelblue;   
		  border: 0px;      
		  border-radius: 8px;           
		  pointer-events: none; 
		  white-space:nowrap;  
		  min-width:150px;     
		}
		
		.dropdown
		{
		transition: box-shadow 0.2s ease-out 0s;
		background-color: #E4E4E4;
	    border-radius: 0.3125em !important;
	    box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1) !important;
	    color: rgba(0, 0, 0, 0.8);
	    cursor: pointer;
	    display: inline-block;
	    line-height: 1.33;
	    padding: 0.5em .7em;
	    white-space: normal;
	    word-wrap: break-word;
	    }
		
		#dropdown_icon
		{
			font-size: 12px;
			
		}
		.datepicker{
			width:80px
		}
		.hover_over{
			cursor: pointer; cursor: hand;
		}
	</style>		
	<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>
    <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/themes/smoothness/jquery-ui.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/analytics/include/svg_styles.css" />
    
	<script src="http://d3js.org/d3.v3.js">-</script>
	<script src="//ajax.googleapis.com/ajax/libs/scriptaculous/1.9.0/scriptaculous.js"></script>
	<script type="text/javascript" src="http://www.google.com/jsapi"></script>
	
	<%-- Unfortunetly no month pickers could be found for prototype so we also include jquery with no conflicts prefix set --%>
 	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script type="text/javascript">
        var $j = jQuery.noConflict();
    </script>
  	<script type="text/javascript" src="${pageContext.request.contextPath}/analytics/include/jquery.monthpicker.js"></script>
 

	<script>
	var analytics_folder_path = "${pageContext.request.contextPath}/analytics"
		document.observe("dom:loaded", function() {
		  // Setup the options drop down
		  $('graph_options').toggle()
		  $('show_hide_options').observe('click', function(event){$('graph_options').toggle()});
		  
		  // initially hide all containers for tab content
		  $('dataType').observe('change', changeDataType);
		  $$('input[type="radio"][name="dataTypeSelectionOption"]').each(function(element){
			  element.observe('change', updateDataTypeOptions);
	      });
		  $$('input[type="radio"][name="collectionSelectionOption"]').each(function(element){
			  element.observe('change', updateCollectionSelections);
	      });
		  
		  updateDataTypeOptions();
		  updateCollectionSelections();
		  // End 
		  
		  // graph the data
		  if(graphData.length>0)
			  createGraph();
  
		  // setup the month picker
		  options = {
			    pattern: 'yyyy-mm',
			    startYear: 2010,
			    monthNames: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Agu', 
			                 'Sep', 'Out', 'Nov', 'Dec']
			};

		  $j('#id_from_date').monthpicker(options);
		  $j('#id_to_date').monthpicker(options);
		  // end setup month picker
		  
		  // Setup the select and de-select all links for collections and option types
		  // There are two for each
		  $("collections_select_all").observe("click", function(){
		      $$(".collection").each(function(box){
		        box.checked =true;
		      });
		    });
		  $("collections_deselect_all").observe("click", function(){
		      $$(".collection").each(function(box){
		        box.checked =false;
		      });
		    });
		  
		  $$(".data_types_select_all").each(function (multi_selector) {
			  $(multi_selector).observe("click", function(){
				  $(this).up().select('.dataTypeOption').each(function(box){
			        box.checked =true;
			      });
			  
		 	 });  
		  });
		  $$(".data_types_deselect_all").each(function (multi_selector) {
			  $(multi_selector).observe("click", function(){
				  $(this).up().select('.dataTypeOption').each(function(box){
			        box.checked =false;
			      });
		 	 });  
		  });
		  
		  $("id_help_link").observe('click',function(){
		  	javascript:window.open (analytics_folder_path+'/help.html', '','width=600, height=600, alwaysRaised=yes, scrollbars=1');
		  });
		  // end
		  
		  
		});
		function changeDataType()
		{
			$$('.dataTypeOptions').invoke('hide')
			updateDataTypeOptions();
		}
		function updateDataTypeOptions()
		{
			options_value = $$('input:checked[type="radio"][name="dataTypeSelectionOption"]').pluck('value');
			dataType = $('dataType').getValue()

			optionsDiv = $(dataType+'_dataTypeOptions');

			if(dataType!="" && options_value=="selected")
				$(optionsDiv).show();
			else if(dataType!="")
				$(optionsDiv).hide();
			
			// Uncheck all other option checkboxes, we don't want extra ones submitted
			$$('.dataTypeOptions').each(function(div){
		        if(div!=optionsDiv)
		        {
		        	$(div).select(".dataTypeOption").each(function(box){
				        box.checked = false;
				      });		
		        }
		      });	
			$$('.data_types_multi_selector').each(function(box){
				box.checked=false
		        
		      });	
			
			
			// Collections is a special case, since collections are the options not the 
			// filter by. Therefore we don't want them to be able to select collections using
			// select collections
			if(dataType=="Collection")
				$("selectCollections").hide()
			else
				$("selectCollections").show()
		}
		
		function updateCollectionSelections()
		{
			collections_value = $$('input:checked[type="radio"][name="collectionSelectionOption"]').pluck('value');
			collections_div = $("collections")
			
			if(collections_value=='selected')
				collections_div.show()
			else
				collections_div.hide()

		}
		function isFormValid()
		{
			// Making sure that options are selected if they are supposed to before submission
			options_value = $$('input:checked[type="radio"][name="dataTypeSelectionOption"]').pluck('value');
			if(options_value=="selected")
			{
				selections = $$('input:checked[name="selectedDataTypeOptions"]').pluck('value')
				if(selections.length==0)
				{
					alert("If options is set to selected, then at least one selections should be selected.")
					return false
				}
			}
			
			collections_value = $$('input:checked[type="radio"][name="collectionSelectionOption"]').pluck('value');
			if(collections_value=="selected")
			{
				selections = $$('input:checked[name="selectedCollections"]').pluck('value')
				if(selections.length==0)
				{
					alert("If collections is set to selected, then at least one collection should be selected.")
					return false
				}
			}
			
			return true
		}
		
		// Download the svg chart that is currently displayed. output_format should be
		// pdf, jpg or png
		function submit_download_form(output_format)
		{
			// stylesheet_url is something that is unfortuente that we have to add. The issue is
			// that the svg that is being seen must have a css enabled for a few styling points and
			// we want what is downloaded to look the same. To do this we are sending the full
			// path of the svg.css that we are using so it can also be used to render the image. 
			// It cannot know in advance since dlese tools is seperate from the web application ie ncs, dds. 
			var stylesheet_url = location.protocol+"//"+document.location.host+"${pageContext.request.contextPath}/analytics/include/svg_styles.css"
			var svg = $$("svg")[0];
			// Extract the data as SVG text string
			var svg_xml = (new XMLSerializer).serializeToString(svg);
			$("id_svg_raw").setValue(svg_xml);
			$("svg_style_sheet_url").setValue(stylesheet_url);
			$("id_output_format").setValue(output_format);
			$("id_svg_form").submit();	
		}		
	</script>
	
	<script>
		var parseDate = d3.time.format("%Y-%m").parse;
		var graphData = [];
		var options = [];
		var dates = [];
		var yMin = 10000000000;
		var yMax = 10;
		var dataTypeOptionsMappings = [];
		dataTypeOptionsMappings['all'] = 'All'
		
		var allMap = false;
		
		<%-- Create the data for the graph, by creating javascript varable called graphData
		also keep track of the max record count, min record count and dates for figuring out
		the axis. --%>
		<c:if test="${not empty rsf.graphData}">
		    <util:sortMap var="sortedGraphData" map="${rsf.graphData}" sortKey="key"/>

			<logic:iterate id="graphData" name="sortedGraphData">
				dates=[]
				values = []
				<logic:iterate id="value_data" name="graphData" property="value">
					values.push({"date":parseDate("${value_data.date}"), "count":"${value_data.count}"})
					dates.push(parseDate("${value_data.date}"));
					if(${value_data.count}<yMin)
						yMin = ${value_data.count}
					if(${value_data.count}>yMax)
						yMax = ${value_data.count}
				</logic:iterate>
				
				if('${graphData.key}'=='all')
					allMap = true;
				graphData.push({"option":"${graphData.key}", "values":values})  
				options.push("${graphData.key}");
				
			</logic:iterate>
		</c:if>
		
		// We want the min y on the axis to have some breathing room. So it doesn't look like something
		// is 0 but really isn't since it would be placed on the bottom of the graph. This gives
		// the bottom of the graph some breathing room
		if(yMin>100)
			yMin = yMin-100;
		else
			yMin = 0;
		
		var legendOptionsLength = 0;
		// Create included facet results as a javascript variable. This is used for the legen
		var includedFacetResultTypes = [];

		<logic:iterate id="facetResultType" name="rsf" property="includedFacetResultTypes">
				includedFacetResultTypes.push('${facetResultType}');
		</logic:iterate>

		var optionsOnlyAllShown = false
		<c:choose>
			<c:when test="${rsf.dataTypeSelectionOption!='selected' && rsf.cumulative}">
				legendOptionsLength = 1
				optionsOnlyAllShown = true
			</c:when>
			<c:otherwise>
				legendOptionsLength = includedFacetResultTypes.length
			</c:otherwise>
		</c:choose>
		
		// Create collections selected javascript variable for display in the label.
		var collectionSelectionOption = '${rsf.collectionSelectionOption}';
		var selectedCollections = [];
		<c:if test="${not empty rsf.selectedCollections}">
			<logic:iterate id="collection" name="rsf" property="selectedCollections">
				selectedCollections.push('${collection}');
			</logic:iterate>
		</c:if>
		
		var allCollections = [];
		<c:if test="${not empty rsf.collections}">
			<logic:iterate id="collection" name="rsf" property="collections">
				allCollections.push('${collection.key}');
			</logic:iterate>
		</c:if>
		
		// Setup what will be used for the collections legend. It was decided that
		// if all is selected for collections we just show all. Otherwise we show all
		// collections that were selected. Even if every one of them are selected individually
		var legendCollections= selectedCollections
	    if(collectionSelectionOption=='all')
		{
	    	legendCollections = ['all']
		}
		
		// Figure out how big the entire legend needs to be. The size will just be 
		// the bigger of the two. options or collections
		var legendCollectionsLength = legendCollections.length
		if(allMap)
			legendOptionsLength++;
		
		var legend_length = 0
		if(legendOptionsLength>legendCollectionsLength)
			legend_length = legendOptionsLength
		else
			legend_length = legendCollectionsLength
		
		// Legend size is the actuall size in pixels that we will be accounting for when
		// creating the svg height. Just by trial and error, each row of the legen is about 12 px
		// and 100 pixels takes care of the box surronding it and padding between it and the graph
		legend_size = legend_length*12 + 100
	</script>
	
	<script>
	
	function createGraph()
	{
		<c:choose>
			<%-- When line lables are on we need to make the marigin bigger in the svg to hold
			the extra characters.--%>
			<c:when test="${rsf.lineLabels}">
				if(!optionsOnlyAllShown)
				{
				 	var longestLength = 4;
				 	for(var i=0; i<includedFacetResultTypes.length;i++)
				 	{
				 		aLength = dataTypeOptionsMappings[includedFacetResultTypes[i]].length;
				 		if(aLength>longestLength)
				 			longestLength = aLength
				 	}
				 	// the width is considered 6 pixels for each character in the longest length
				 	// data type. Plus 6 padding
					margin_right = Math.floor(longestLength*5.7)+20
				}
				else
				{
					margin_right = 25
				}
			</c:when>
			<c:otherwise>
				// otherwise just use 20 pixels
				margin_right = 20
			</c:otherwise>
		</c:choose>
		
		var margin = {top: 30, right: margin_right, bottom: legend_size, left: 60},
		   width = 680 - margin.left //- margin.right,
		   height = 380;
		
		// Find the starting spot for the legend. notice the 70 pixel spacing between it and the chart
		var legendStart=70+height;
		var x = d3.time.scale()
		   .range([0, width]);
		
		var y = d3.scale.linear()
		    .range([height, 0]);
		
		var color = d3.scale.category20();
		
		// We want a custom x axis formating. So on All year janruary it shows the year and
		// for all months it shows the 3 letter month. Division count is making it so there
		// are never to many x axais shown
		
		xAxisDivisionCount = Math.floor((dates.length/15)+1)
		var xAxisformat = d3.time.format.multi([
		                                   ["%b", function(d) { return d.getMonth(); }],
		                                   ["%Y", function() { return true; }]
		                                 ]);
		var xAxis = d3.svg.axis()
		    .scale(x)
		    .orient("bottom")
		    .ticks(d3.time.months,xAxisDivisionCount)
		    .tickFormat(xAxisformat, xAxisDivisionCount);
		
		// tickSubdivide 0 means we only want integers not floating points
		var yAxis = d3.svg.axis()
		    .scale(y)
		    .orient("left")
		    .tickSubdivide(0);
		var line = d3.svg.line()
		    .x(function(d) { return x(d.date); })
		    .y(function(d) { return y(d.count); });
		
		// add the svg to the page moving it so we have a margin top and left
		var svg = d3.select("#graph").append("svg")
		    .attr("width", width + margin.left + margin.right)
		    .attr("height", height + margin.top + margin.bottom)
		  .append("g")
		    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
		
		// This autmotically selects are color pallete. Which is super nice since we have no
		// idea how many different data options we are going to be showing
		 color.domain(options);
		
		 x.domain(d3.extent(dates));

		 y.domain([
		   yMin,
		   yMax
		 ]);
		
		 // Put the x axis on the very bottom
		 svg.append("g")
		     .attr("class", "x axis")
		     .attr("transform", "translate(0," + height + ")")
		     .call(xAxis);
		 
		 // put the y axis on the left. but make it up and down. via rotate
		 svg.append("g")
		     .attr("class", "y axis")
		     .call(yAxis)
		   .append("text")
		     .attr("transform", "rotate(-90)")
		     .attr("y", 10)
		     .attr("dy", ".71em")
		     .style("text-anchor", "end")
		     .text("Record Count");
		
		 // Graph the data on the chart. Appending class option to them all
		 var graph = svg.selectAll(".option")
		     .data(graphData)
		   .enter().append("g")
		     .attr("class", "option");
		
		 // These are the lines drawn on the graph
		graph.append("path")
		     .attr("class", "line")
		     .attr("class", function(d) { return toValidCSSName(d.option)+" line"})
		     .attr("d", function(d) { return line(d.values); })
		     .attr("data-legend",function(d) { return d.option})
		     .style("stroke", function(d) { return color(d.option); })
	    
		// if we want line labels. Add the data type option text to the end of the paths, with
		// the designated color
		//.attr("transform", function(d) { return "translate(" + (x(d.value.date)+3) + "," + y(d.value.count) + ")"; })
		<c:if test="${rsf.lineLabels}">
		    var line_label_transistions= create_line_label_transistions(graphData, y)
			graph.append("text")
		      .datum(function(d) { return {name: d.option, value: d.values[d.values.length - 1]}; })
		      .style("font-size","12px")
		      .style("fill",function(d) {return color(d.name); })
		      .attr("transform", function(d) { return "translate(" + (x(d.value.date)+10) + "," + line_label_transistions[d.name] + ")"; })
		      .attr("x", 3)
		      .attr("dy", ".35em")
		      .text(function(d) { return unescapeHtml(dataTypeOptionsMappings[d.name]); });
		</c:if>
		
		// This is the Options header for the legend
		svg.append("text")
		  .attr("transform","translate(-10,"+(legendStart)+")")
		  .style("font-size","15px")
		  .text("Options")
		 
		 // Add the legend below the header, this calls addOptionsLegend function to do 
	     // the work to keep this code a little cleaner
		 optionLegend = svg.append("g")
		  .attr("class","legend")
		  .attr("transform","translate(0,"+(legendStart+25)+")")
		  .style("font-size","12px")
		  .call(addOptionsLegend)
		
		  // If we need to show a collection legend, this follows the same flow as the options
		  // legend defined above
		  <c:if test="${rsf.dataType!='Collection'}">
			  svg.append("text")
			  .attr("transform","translate(365,"+(legendStart)+")")
			  .style("font-size","15px")
			  .text("Collections")
		  
			  collectionLegend = svg.append("g")
			  .attr("class","legend")
			  .attr("transform","translate(360,"+(legendStart+25)+")")
			  .style("font-size","12px")
			  .call(addCollectionLegend)
		  </c:if>
		 
		 
	      // This block of code is adding points on the line paths for each data point,
	      // For each data point that is added we also add onmouseover and onmouseout to
	      // add a tooltip div. Most of this code was taken for another site. So this isn't
	      // documented that great, since I don't really understand some of what its doing
	      var formatTime = d3.time.format("%Y-%m");
		  var div = d3.select("#tooltip")  
		  var point = graph.append("g")
		  .attr("class", "line-point");

		  point.selectAll('circle')
		  .data(function(d){ return d.values})
		  .enter().append('circle')
		  .attr("cx", function(d) { return x(d.date) })
		  .attr("cy", function(d) { return y(d.count) })
		  .attr("r", 3)
		  .style("fill", function(d) { return color(this.parentNode.__data__.option); })
		  .style("stroke", function(d) { return color(this.parentNode.__data__.option); })
		  .on("mouseover", function(d) {   
			  d3.select(this).classed("hover_over", true);
            div.transition()        
                .duration(200)      
                .style("opacity", .9);      
            div .html("DataSet: "+dataTypeOptionsMappings[this.parentNode.__data__.option]+
            		 "<br/>Date: "+ formatTime(d.date) + 
            		 "<br/>"  + "Count: "+Number(d.count).toLocaleString('en'))  
                .style("left", (width/2+130) + "px")     
                .style("top", (height/2+50) + "px");    
            })                  
         .on("mouseout", function(d) {   
        	 d3.select(this).classed("hover_over", false);
            div.transition()        
                .duration(500)      
                .style("opacity", 0);   
         });
		 // end of block
		
		 
		  // Add the title to the svg map
		  <c:set var="title" ><c:if test="${rsf.cumulative}">Cumulative</c:if> ${rsf.dataType}</c:set>

		  svg.append("text")
	        .attr("x", (width / 2))             
	        .attr("y", 0 - (margin.top / 2))
	        .attr("text-anchor", "middle")  
	        .style("font-size", "16px") 
	        .style("text-decoration", "underline")  
	        .text("${title}");
	}
	
	// This is the code that actually adds the options legend to the svg map and
	// all of its interactivity
	function addOptionsLegend(g)
	{
			 g.each(function() {
				    var g= d3.select(this),
				        items = {},
				        svg = d3.select(g.property("nearestViewportElement")),
				        legendPadding = g.attr("data-style-padding") || 5,
				        lb = g.selectAll(".legend-box").data([true]),
				        li = g.selectAll(".legend-items").data([true])
					// Draw a box around the data
				    lb.enter().append("rect").classed("legend-box",true)
				    li.enter().append("g").classed("legend-items",true)
				    svg.selectAll("[data-legend]").each(function() {
				        var self = d3.select(this)
				        items[self.attr("data-legend")] = {
				          pos : self.attr("data-legend-pos") || this.getBBox().y,
				          color : self.attr("data-legend-color") != undefined ? self.attr("data-legend-color") : self.style("fill") != 'none' ? self.style("fill") : self.style("stroke") 
				        }
				      })

				    items = d3.entries(items).sort(function(a,b) { return a.value.pos-b.value.pos})
				  
				   // If all is selected, we still want to show what all includes, even though
				   // its not part of the data array. So we add them to the items list
				   if(items[0]['key']=='all' && !optionsOnlyAllShown)
				   {
					   for(i=0;i<includedFacetResultTypes.length;i++)
					   {
						   items.push({"key":includedFacetResultTypes[i],"value":{"pos":i+1,"color":"white"}})
					   }
				   }
				    
				    // For each item add the text to the legend. along with mouseover and
				    // mouseout events which highlights the corresponding data path that the 
				    // text desribes. We use toValidCSSName method to make a unique css name
				    // for each data type that can be used. This exact class was also added to the path lines
				   li.selectAll("text")
				        .data(items,function(d) { return d.key})
				        .call(function(d) { d.enter().append("text")})
				        .call(function(d) { d.exit().remove()})
				        .attr("y",function(d,i) { return i+"em"})
				        .attr("x","1em")
				        .attr("title", function(d){return unescapeHtml(dataTypeOptionsMappings[d.key])})
				        .text(function(d) { 
				        	text = unescapeHtml(dataTypeOptionsMappings[d.key])
				        	if(text.length>45)
				        		text = text.slice(0, 42) +"..."
				        	return text})
				        .on("mouseover", function(d) {
				        	d3.selectAll("."+toValidCSSName(d.key))
				            .classed("highlighted_line", true)
				            d3.select(this).classed("hover_over", true)
				       			 })
				        .on("mouseout", function(d) {       
				        	d3.selectAll("."+toValidCSSName(d.key))
				            .classed("highlighted_line", false)
				            d3.select(this).classed("hover_over", false)
				       	   });
				    
				    // We only want colored circles if they are actual data types that are being shown. 
				    // if this is showing all via cumulative option. There is only one line. The rest of the
				    // text are just showing whats in all. The interactivity is the same as described
				    // above in the text
					if(items[0]['key']=='all')
				    {
						items = [items[0]]
				    }
				    li.selectAll("circle")
				        .data(items,function(d) { return d.key})
				        .call(function(d) { d.enter().append("circle")})
				        .call(function(d) { d.exit().remove()})
				        .attr("cy",function(d,i) { return i-0.25+"em"})
				        .attr("cx",0)
				        .attr("r","0.4em")
				        .style("fill",function(d) { return d.value.color})  
				        .on("mouseover", function(d) {
				        	d3.selectAll("."+toValidCSSName(d.key))
				            .classed("highlighted_line", true)
				            d3.select(this).classed("hover_over", true)
				       			 })                  
				        .on("mouseout", function(d) {       
				        	d3.selectAll("."+toValidCSSName(d.key))
				            .classed("highlighted_line", false)
				            d3.select(this).classed("hover_over", false)
				       	   });
				      
				    // Reposition and resize the box
				    var lbbox = li[0][0].getBBox()  
				    lb.attr("x",(lbbox.x-legendPadding))
				        .attr("y",(lbbox.y-legendPadding))
				        .attr("height",(lbbox.height+2*legendPadding))
				        .attr("width",(lbbox.width+2*legendPadding))

				  })
				  return g	 
	
		}
	
	// Pretty much the exact code as the optionLegend Except this doesn't have 
	//  the on mouseover and onmouseout to enable interactivity
	function addCollectionLegend(g)
	{
		 g.each(function() {
			    var g= d3.select(this),
			        items = {},
			        svg = d3.select(g.property("nearestViewportElement")),
			        legendPadding = g.attr("data-style-padding") || 5,
			        li = g.selectAll(".legend-items").data([true])
					lb = g.selectAll(".legend-box").data([true]),
				lb.enter().append("rect").classed("legend-box",true)
			    li.enter().append("g").classed("legend-items",true)
				
			   li.selectAll("text")
			        .data(legendCollections,function(d) { return d})
			        .call(function(d) { d.enter().append("text")})
			        .call(function(d) { d.exit().remove()})
			        .attr("y",function(d,i) { return i+"em"})
			        .attr("x","1em")
			        .attr("title", function(d){return unescapeHtml(dataTypeOptionsMappings[d])})
			        .text(function(d) { 
			        	text = unescapeHtml(dataTypeOptionsMappings[d])
			        	if(text.length>45)
			        		text = text.slice(0, 42) +"..."
			        	return text}) 
			        
			    // Reposition and resize the box
				    var lbbox = li[0][0].getBBox()  
				    lb.attr("x",(lbbox.x-legendPadding))
				        .attr("y",(lbbox.y-legendPadding))
				        .attr("height",(lbbox.height+2*legendPadding))
				        .attr("width",(lbbox.width+2*legendPadding))
			  })
			  return g	 
	}
	
	function unescapeHtml(unsafe) {
	    return unsafe
	        .replace(/&amp;/g, "&")
	        .replace(/&lt;/g, "<")
	        .replace(/&gt;/g, ">")
	        .replace(/&quot;/g, "\"")
	        .replace(/&#039;/g, "'");
	}
	

    // Custom mathod that takes anystring and creates a hopefully unique valid css name
    // css names must not start with a number, cannot have spaces or any other special characters
    /// thats why a hash is so useful
	function toValidCSSName(aString) {
	  var hash = 0, i, chr, len;
	  if (aString.length == 0) return hash;
	  for (i = 0, len = aString.length; i < len; i++) {
	    chr   = aString.charCodeAt(i);
	    hash  = ((hash << 5) - hash) + chr;
	    hash |= 0; // Convert to 32bit integer
	  }
	  return '_'+hash.toString();
	};
	
	
	// Main function that calls the defined functions to create the correct spacing for option values
	function create_line_label_transistions(graphData, scaleFunction)
	{
		data = create_option_y_data(graphData, scaleFunction)
		groups = create_groups(data)
		align_ranges(groups)
		return create_line_label_transistions_via_groups(groups)
	}
	
	var char_spacing_high = 6.5
	var char_spacing_low = 6.5
	var total_char_spacing = char_spacing_high+char_spacing_low
	
	// Create the line label transitions mapping given groups that have been aligned.
	// The summary of what this is doing. Is to sort the data that resides in the group. And spacing
	// each data from the range_low by the given spacing. Therefore all elements will be evenly spaced apart
	// from the range_low to the range_high
	function create_line_label_transistions_via_groups(groups)
	{
		line_label_transistions = {}
		for(groupIndex=0;groupIndex<groups.length;groupIndex++)
		{
			group = groups[groupIndex]
			group_data = group["data"]
			group_data.sort(compare_data)
			
			range_low = group['range_low']
			for(dataIndex=0;dataIndex<group_data.length;dataIndex++)
			{
				data_element = group_data[dataIndex]
				
				// Sometimes the range_low is 0. If its 0 that means that range was never set for that group. 
				if(range_low!=0)
					correction = char_spacing_high
				else
					correction = 0		
				// add the correction to make up for the fact that the range_low is actually the top of the line. Not
				// actually in the middle of the line where the svg actually should draw the character
				line_label_transistions[data_element['optionName']]=range_low+correction+(dataIndex*(total_char_spacing))
			}	
		}
		return line_label_transistions
	}
	
	// The following 3 functions are sort operations for data and groups
	function compare_groups(a,b) {
		  if (a['range_low'] < b['range_low'])
		     return -1;
		  if (a['range_low'] > b['range_low'])
		    return 1;
		  return 0;
	}
	
	function compare_data(a, b)
	{
		if (a['y'] < b['y'])
		     return -1;
		  if (a['y'] > b['y'])
		    return 1;
		  return 0;
	}
	
	function compare_data_desc(a, b)
	{
		if (a['y'] < b['y'])
		     return 1;
		  if (a['y'] > b['y'])
		    return -1;
		  return 0;
	}
	
	// Function takes all the groups and tries to move them up or down making the rest of the groups fit.
	// It tries to do its best job without getting to crazy. We start at the top group and work our way
	// down. Therefore we assume that any groups before the current one is placed the best we can. The summary
	// of what it is doing is take a group range. Does its low reside in the range of the group before it. If it
	// does move it down a certain amount(movement_step). Does it still reside then move it down some more. Otherwise stop
	// If it didn't have to move it. Then take the groups high range, does that reside in the group after this one. If so
	// try to move it up, as much as it can without hitting the group above it to try to make it so it doesn't reside in 
	// the next groups range. This makes the next group easier to align
	function align_ranges(groups)
	{
		// first sort it to make sure the first group has the lowest y
		groups.sort(compare_groups)
		
		movement_step = 2
		
		// We don't want to move more then 20 in the range. To keep the graph at least somewhat corresponding to the 
		// data points
		max_movement = 20
		// Note we do not try to align the top group, since that might push it off the graph
		for(groupIndex=1;groupIndex<groups.length;groupIndex++)
		{
			group = groups[groupIndex]
			range_low = group['range_low']
			range_high = group['range_high']
			
			previous_group = groups[groupIndex-1]
	
			movement=0
			for(movement=0;movement<=max_movement;movement+=movement_step)
			{
				// does it reside in the previous group
				if(range_low<previous_group["range_high"])
				{
					range_low+=movement
					range_high+=movement
				}
				else
					break
			}
			
			if(movement==0 && groupIndex+1<groups.length)
			{
				// since it doesn't cause an issue for the previous group, see if we can bring it off the next group,
				// This is to make it easier to align later groups
				next_group = groups[groupIndex+1]
				movement=0

				for(movement=0;movement<=max_movement;movement+=movement_step)
				{
				
					if(range_high>next_group["range_low"])
					{
						// if this moves it into the previous groups way, then no good
						if(range_low-movement_step<=previous_group["range_high"])
							break
						// otherwise try it
						range_low-=movement_step
						range_high-=movement_step
					}
					else
						break
				}
			}
			// Now set the new ranges
			group['range_low'] = range_low
			group['range_high'] = range_high
		}
	}
	
	// The method takes the data and groups them. It does this by giving ranges to the groups. So they catch all
	// data that might fall in it. For example if the first piece of data's y is at 10 and we want it to have 13 pixles of space. 
	// Its range would be (10-7.5) through (10+7.5) = 2.5 through 17.5. Therefore if the next data was at 16. It fits in that range. Therefore it would join
	// the group. The center point would therefore be (10+16)/2= 13. Which is the midpoint between the two. Thent to find
	// the new range. we take how many pieces of info are in the group. There are 2, therefore the spacing we need is (13*2)=26.
	// Space that out evenly on the center point. center_point-(26/2)- (26/2)+center_point. Range is 0 - 26
	function create_groups(data)
	{
		data.sort(compare_data_desc)
		
		groups = []
		for(dataIndex=0;dataIndex<data.length;dataIndex++)
		{
			data_element = data[dataIndex]
			y = data_element["y"]
			found_group = null
			for(groupIndex=0;groupIndex<groups.length;groupIndex++)
			{
				group = groups[groupIndex]
				if(y<group['range_high']&& y>group['range_low'])
				{
					found_group = group
					break
				}
			}

			if(found_group==null)
			{
				groups.push({'data':[data_element], 'range_high':y+char_spacing_low, 'range_low':y-char_spacing_high, 'total_y':y})
			}
			else
			{
				found_group['data'].push(data_element)
				
				total_y = found_group['total_y'] + y
				
				center_point = total_y/found_group['data'].length
				
				range = (found_group['data'].length*total_char_spacing)/2
				found_group['range_high']=center_point+range
				found_group['range_low']=center_point-range
				
				found_group['total_y'] = total_y
				
				if(found_group['range_low']<0)
					found_group['range_low'] = 0
	
			}
		}
		return groups
	}
	
	// This function takes the last month of data for all the option types. puts them into
	// a dict, with its corresponding y value
	function create_option_y_data(graphData, scaleFunction)
	{
		data = []
		for(optionIndex=0;optionIndex<graphData.length;optionIndex++)
		{
			optionMap = graphData[optionIndex]
			values = optionMap['values']
			lastDateValueMap = values[values.length-1]
			y = scaleFunction(lastDateValueMap['count'])
			data.push({'optionName':optionMap['option'], 'y':y})
		}
		return data
	}

	</script>