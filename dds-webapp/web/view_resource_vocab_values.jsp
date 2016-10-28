<%-- This iterates through a set of cached vocab values.  Values must first be registered into the
cache via the ddsViewResourceForm vocabCacheValue property, and the group that is to be displayed
using this include must be indicated via the vocabCacheGroup property. Note that items are treated
seperately if they are a sub-header rather than a leaf node, and that each value (label) is placed
into a DIV that calls out styles groupLevelN and itemLevelN, where N is the level of the vocab
hierarchy, with 1 being the top level. --%>
<c:forEach var="vocabValue" items="${ ddsViewResourceForm.cachedVocabValuesInOrder }">
	<div class="desc">
		<c:if test="${ not empty vocabValue.subList }">
			<div class='groupLevel${ vocabValue.groupLevel }'>
				${ vocabValue.label }:
			</div>
		</c:if>
		<c:if test="${ empty vocabValue.subList }">
			<div class='itemLevel${ vocabValue.groupLevel }'>						
				${ vocabValue.label }
			</div>
		</c:if>				
	</div>
</c:forEach>
