<%-- Creates HTML for a single Mmd Rec. This JSP should be included in an enclosing JSP
with the var 'mmdRec' set to hold an mmdRec --%>

<table border=1 cellpadding=4 cellspacing=0>
	<c:choose>
		<c:when test="${empty mmdRec}">
			<tr>
				<td><b>This record is empty</b></td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr>
				<td><b>Record ID</b></td>
				<td><b>Collection</b></td>
				<td><b>Status</b></td>
				<td><b>Last checked on</b></td>
			</tr>		
			<tr>
				<td nobr>
				${mmdRec.id}<br>
				[<a href="../display.do?fullview=${mmdRec.id}">Full view</a>]
				[<a href="?id=${mmdRec.id}&collection=${mmdRec.collKey}&verb=ViewIDMapper">IDMapper report</a>]
				</td>
				<td>${mmdRec.collKey}</td>
				<td>${mmdRec.statusString}</td>
				<td>
					<fmt:formatDate value="${f:convertLongToDate( mmdRec.recCheckDate )}" pattern="MM/dd/yy - hh:mm:ss a z" />
				</td>				
			</tr>
		</c:otherwise>
	</c:choose>			
</table>



