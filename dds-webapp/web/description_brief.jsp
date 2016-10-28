<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr>
	<td colspan="2">
		<div class="searchResultDescription">
			<dds:keywordsHighlight truncateString="true">
				${ result.docReader.description }
			</dds:keywordsHighlight>
			<nobr>
				<a href="catalog_${ result.docReader.id }.htm">Full description</a>.
			</nobr> 
			<c:set var="relatedDocReader" value="${ result.docReader }" />
			<%@ include file="description_related_link.jsp" %>
			
			<c:if test="${ not empty result.docReader.contentStandards }">
				This resource supports 
				<a href="view_resource.do?description=${ result.docReader.id }#standards">educational 
				standards</a>.				
			</c:if>	
		</div>
	</td>
</tr>  
<tr>
	<td colspan="2">		
		<c:if test="${ not empty result.docReader.collectionKey }">			
			<div class="searchResultDescription">
			<nobr>This resource is in these collections:</nobr>
			<c:set var="recordId" value="${ result.docReader.id }" />
			<c:set var="coll0" value="${ result.docReader.collectionKey }" />
			<a href='<%= request.getContextPath() 
				%>/view_resource.do?description_all=${ recordId	}&coll=${ coll0 }'><vocabs:uiLabel 
				metaFormat="dlese_collect" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" 
				field="key" value="${ coll0 }" /></a><c:if 
					test="${ not empty result.docReader.associatedCollectionKeys }"><c:forEach 
						var="ky" items="${ result.docReader.associatedCollectionKeys }">,
			<a href='<%= request.getContextPath() 
				%>/view_resource.do?description_all=${ recordId	}&coll=${ ky }'><vocabs:uiLabel 
				metaFormat="dlese_collect" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" 
				field="key" value="${ ky }" /></a></c:forEach>
				</c:if>
			</div>
		</c:if>		
	</td>
</tr>    
<tr><td height="4" colspan="2"></td></tr>	
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0">		
<tr>
	<td valign="top" align="left" width="54%">
		<c:if test="${ not empty result.docReader.multiGradeRanges }">
			<div class="searchResultValues">
				<nobr><em>Grade level:</em></nobr>

				<vocabs:setResponseGroup metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="gr" />
				<c:forEach items="${ result.docReader.multiGradeRanges }" var="resp">
					<vocabs:setResponseValue value="${ resp }" />
				</c:forEach>
				<%@ include file="include/vocabs_brief.jsp" %>
				
			</div>
		</c:if>
		<c:if test="${ not empty result.docReader.multiResourceTypes }">
			<div class="searchResultValues">
				<nobr><em>Resource type:</em></nobr>

				<vocabs:setResponseGroup metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="re" />
				<c:forEach items="${ result.docReader.multiResourceTypes }" var="resp">
					<vocabs:setResponseValue value="${ resp }" />
				</c:forEach>
				<%@ include file="include/vocabs_brief.jsp" %>
				
			</div>	
		</c:if>
		<c:if test="${ not empty result.docReader.multiSubjects }">
			<div class="searchResultValues">
				<nobr><em>Subject:</em></nobr>
				
				<vocabs:setResponseGroup metaFormat="adn" audience="${ initParam.metadataVocabAudience }" language="${ initParam.metadataVocabLanguage }" field="su" />
				<c:forEach items="${ result.docReader.multiSubjects }" var="resp">
					<vocabs:setResponseValue value="${ resp }" />
				</c:forEach>
				<%@ include file="include/vocabs_brief.jsp" %>
				
			</div>
		</c:if>
	</td>
	<td valign="top" align="left" width="46%">
		&nbsp;
	</td>
</tr>
<tr>
	<td colspan="2" height="8"></td>
</tr>
</table>

