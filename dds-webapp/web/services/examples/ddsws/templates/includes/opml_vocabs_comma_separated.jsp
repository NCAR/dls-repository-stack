<%--
	This JSP page displays the voabab (grade ranges, resources types, etc).
	
	This is JSP client version @DDSWS_CLIENT_VERSION@.		
--%>

<%-- <c:import var="transformVocabsCommaSeparated" url="xsl_files/vocabs_comma_separated.xsl" /> --%>

<x:forEach select="$vocabOPML/opml/body/outline" var="outline1" varStatus="i1"> 
	<x:set var="outline2" select="$outline1/outline"/>
	<x:choose>
		<x:when select="count($outline2) > 0">

		
			<x:forEach select="$outline2" var="outline2" varStatus="i2"> 
				<x:set var="outline3" select="$outline2/outline"/>
				<x:choose>
					<x:when select="count($outline3) > 0">
						greater than 0,
					</x:when>
					<x:otherwise>
						<x:out select="$outline2/@text"/>${i2.last ? '' : ','}
					</x:otherwise>
				</x:choose>
			</x:forEach> 		
		
		
		
		</x:when>
		<x:otherwise>
			<x:out select="$outline1/@text"/>${i1.last ? '' : ','}
		</x:otherwise>
	</x:choose>
</x:forEach> 
