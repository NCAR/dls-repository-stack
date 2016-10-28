<%-- This is used by the what's new page resources list --%>
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr>
	<td colspan="2">
		<div class="searchResultDescription">
			<dds:keywordsHighlight truncateString="true">
				<bean:write name="result" property="docReader.description" filter="false" />
			</dds:keywordsHighlight>
			<nobr>
				<a href='/library/catalog_<bean:write 
					name="result" property="docReader.id" filter="false" />.htm'>Full description</a>.
			</nobr> 
			<logic:greaterThan name="result" property="docReader.numCompletedAnnos" value="1">	
				See <a href='view_resource.do?reviews=<bean:write 
					name="result" property="docReader.id" filter="false" />'>reviews,
						teaching tips, related resources, etc</a>.
			</logic:greaterThan>
			<logic:equal name="result" property="docReader.numCompletedAnnos" value="1">	
			 	<logic:equal name="result" property="docReader.hasRelatedResource" value="true">
					See <a href='view_resource.do?reviews=<bean:write 
						name="result" property="docReader.id" filter="false" />'>reviews,
							teaching tips, related resources, etc</a>.
				</logic:equal>
				<logic:equal name="result" property="docReader.hasRelatedResource" value="false">	
					<logic:notEmpty name="result" property="docReader.completedAnnos[0].url">
						See <a href='<bean:write name="result" property="docReader.completedAnnos[0].url"/>' 
							target="_blank">reviews, teaching tips, related resources, etc</a>.	
					</logic:notEmpty>		
				</logic:equal>								
			</logic:equal>		
			
			<logic:present name="result" property="docReader.contentStandards">
				This resource supports 
				<a href="view_resource.do?description=<bean:write 
				name="result" property="docReader.id" filter="false" />#standards">educational standards</a>.				
			</logic:present>		
		</div>
	</td>
</tr>  
<tr>
	<td colspan="2" height="8"></td>
</tr>
</table>
