
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<input id="${id}_bounding_box_button" style="display:none" 
		type="button" value="Bounding box tool" onclick="boundingBoxHelper.openBoundingBoxTool('${id}')"
		title="Activate a tool to fill in the four coordinates"/>
		
<script type="text/javascript"  src="${contextPath}/editor/input_helpers/bounding_box/boundingBoxHelper.js"></script>

<script type="text/javascript">

// test for presence of the coordinates fields before displaying widget
Event.observe (window,'load', function () {
	// console.log ("id: ${id}");
	var southCoord = "${sf:pathToId(id)}" + "${sf:pathToId('/')}" + "southCoord";
 	if ($(southCoord))
	{
		$('${id}_bounding_box_button').show();
		boundingBoxHelper.init('northCoord','southCoord','eastCoord','westCoord',"${sf:pathToId('/')}",6 );
	}
});

</script>





