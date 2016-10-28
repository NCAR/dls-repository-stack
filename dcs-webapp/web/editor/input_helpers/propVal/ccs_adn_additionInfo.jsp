<%-- Input Helper
- project: CCS 
- framework: ADN
- xpath: /itemRecord/general/additionalInfo
- default fields: 'shortTitle' 
--%>

<%-- provide an absolute path to propValEditor --%>
<c:set var="contextUrl"><%@ include file="/ContextUrl.jsp" %></c:set>
<script type="text/javascript" src="${contextUrl}/editor/input_helpers/propVal/propValEditor.js"></script>

<script type="text/javascript">

Event.observe (window, 'load', function (event) {
	var default_fields = ['shortTitle'];
	try {
		new PropValEditor ("${id}", default_fields);
	} catch (error) {
		alert ("propValEditor error: " + error);
	}
});
</script>

<div id="${id}_helper"></div>
	

