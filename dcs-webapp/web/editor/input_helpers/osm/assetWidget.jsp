<c:set var="contextPath"><%=request.getContextPath().trim()%></c:set>

<%-- <div style="margin-left:25px">

	<div style="font-size:10px;margin-top:3px">xpath: ${sf:idToPath (id)}</div>
	<div style="font-size:10px;margin-top:3px">id: ${id}</div>
	
</div> --%>

<!-- <div?<span style="border:thin blue solid;background-color:yellow;padding:3px;">TEST WIDGET</span></div> -->

<script type="text/javascript">
// Eventually, we want to get the REPO path from configuration!!
var repoPath = "http://nldr.library.ucar.edu/repository"
document.observe ("dom:loaded", function () {
	new AssetUrlHelper ('${id}', "${contextPath}", repoPath);
});
</script>
