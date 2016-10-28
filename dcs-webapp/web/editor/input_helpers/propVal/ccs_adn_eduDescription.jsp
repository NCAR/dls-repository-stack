<%-- Input Helper
- project: CCS 
- framework: ADN
- xpath: /itemRecord/educational/description
- default fields: 'instructionalInfo', 'classPeriods'
--%>

<%-- provide an absolute path to propValEditor --%>
<c:set var="contextUrl"><%@ include file="/ContextUrl.jsp" %></c:set>
<script type="text/javascript" src="${contextUrl}/editor/input_helpers/propVal/propValEditor.js"></script>

<script type="text/javascript">
Event.observe (window, 'load', function (event) {
	var default_fields = ['instructionalInfo', 'classPeriods'];
	try {
		new PropValEditor ("${id}", default_fields);
	} catch (error) {
		alert ("propValEditor error: " + error);
	}
});
</script>

<div id="${id}_helper"></div> 
	

