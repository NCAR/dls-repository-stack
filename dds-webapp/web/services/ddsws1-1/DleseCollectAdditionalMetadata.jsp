
			<additionalMetadata>
				<dlese_collect>
					<formatOfRecords>${headDocReader.formatOfRecords}</formatOfRecords>
					<isEnabled>${headDocReader.isEnabled}</isEnabled>
					<numRecords>${filesAreSavedToDisc ? headDocReader.numFiles : headDocReader.numIndexed}</numRecords>
					<numRecordsIndexed>${headDocReader.numIndexed}</numRecordsIndexed>
					<partOfDrc>${headDocReader.partOfDRC}</partOfDrc>
					<c:set var="renderingGuidelinesDC"><%-- Only supply rendering guidelines if they are available --%>
						<renderingGuidelines><c:set var="vocabMetaFormat" value="dlese_collect"/>
							<gradeRangeOPML><c:set var="vocabField" value="gr"/><c:set var="vocabItems" value="${headDocReader.gradeRanges}"/>
								<c:set var="grRG"><%@ include file="OPMLOutput.jsp" %></c:set>
								<c:if test="${not empty grRG}">${fn:trim(grRG)}<c:set var="hasRenderingGuidelinesDC" value="${true}"/></c:if>
							</gradeRangeOPML>
							<subjectOPML><c:set var="vocabField" value="su"/><c:set var="vocabItems" value="${headDocReader.subjects}"/>
								<c:set var="suRG"><%@ include file="OPMLOutput.jsp" %></c:set>
								<c:if test="${not empty suRG}">${fn:trim(suRG)}<c:set var="hasRenderingGuidelinesDC" value="${true}"/></c:if>
							</subjectOPML>					
						</renderingGuidelines>
					</c:set>
					<c:if test="${hasRenderingGuidelinesDC}">${renderingGuidelinesDC}</c:if>
				</dlese_collect>				
			</additionalMetadata>



