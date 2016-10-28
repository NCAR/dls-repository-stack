<%@ include file="AdminTagLibIncludes.jsp" %>

<div>
	<em>Id:</em> <a href='display.do?fullview=${mmdRec.id}'>${mmdRec.id}</a>
</div>
<div>
	<em>Collection:</em> ${mmdRec.collKey}
</div>
<div>
	<em>Status:</em> ${mmdRec.statusString}
</div>
