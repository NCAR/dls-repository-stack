<%@ include file="/include/setup.jsp" %><%@ 
	page import="java.util.Enumeration" %><%@
	page import="java.util.Date" %>
<html>
	<head>
		<title>DLESE Histogram Summary</title>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<style type="text/css"><!--
			BODY {
				margin-left: auto;
				margin-right: auto;
				margin-top: 7px;
				margin-bottom: 1px;
				padding: 0px;
				font-family: Arial, Helvetica, sans-serif; 
				font-size: 10pt; 			
			} 
			
			H1 { 
				Font-Family : Arial, Helvetica, sans-serif ; 
				Font-Size : 14pt ;
				Color : #333366; 
			} 
	
			H2 { 
				Font-Family : Arial, Helvetica, sans-serif ; 
				Font-Size : 12pt ;
				Color : #333366; 
			} 
			
			H4 { 
				Font-Family : Arial, Helvetica, sans-serif ; 
				Font-Size : 10pt ;
				Color : #333333; 
			} 
			.pad {
				padding-right: 12px;
			}		
		  -->
		</style>	
	</head>
	<body>	
		<table width="80%">
			<tr>
				<td align="left" colspan="2">
					<div class=pad>Generated on:<br><%= new java.util.Date().toString() %></div>
				</td>
			</tr>
			<tr>
				<td align="right" width="240">
					<div class=pad><h1><u>Category</u></h1></div>
				</td>
				<td>
					<h1><u>Number of resources</u></h1>
				</td>
			</tr>			
			<c:forEach var="collection" items="${ histogramForm.collections }">
				<c:if test="${ !collection.vocabNode.noDisplay }">
						
						<tr><td align=right width=240>&nbsp;</td><td>&nbsp;</td></tr>
						<tr><td align=right width=240>&nbsp;</td><td>&nbsp;</td></tr>
							
						<tr>
							<td align=right width=240>
								<div class=pad><h2>Collection: ${ collection.vocabNode.label }</h2></div>
							</td>
							<td>
								<h2>${ collection.libraryTotal }</h2>
							</td>
						</tr>
						
						<c:choose>
							<c:when test="${ collection.vocabNode.id == '0*' }">
								<jsp:setProperty name="histogramForm" property="hasCollectionSpecified" value="false" />
							</c:when>
							<c:otherwise>
								<jsp:setProperty name="histogramForm" property="hasCollectionSpecified" value="true" />
								<c:set var="collMetaName"><vocabs:getTranslatedValue metaFormat="dlese_collect" 
									field="key" value="${ collection.vocabNode.id }" /></c:set>
								<jsp:setProperty name="histogramForm" property="collectionMetaName" value="${ collMetaName }" />						
							</c:otherwise>
						</c:choose>
	
						<tr>
							<td align=right width=240>
								<div class="pad"><h4>${ vocabGroup.vocabNode.label }</h4></div>
							</td>
							<td>&nbsp;</td>
						</tr>
						
						<jsp:setProperty name="histogramForm" property="currentVocabName" value="gradeRange"/>
						<jsp:setProperty name="histogramForm" property="currentVocabFramework" value="adn"/>
						<tr><td align=right width=240>&nbsp;</td><td>&nbsp;</td></tr>
						<tr><td align=right width=240><div class=pad><h4>Grade Range</h4></div></td><td>&nbsp;</td></tr>					
						<c:forEach items="${ histogramForm.vocabList }" var="vocabGroup" varStatus="status">
							<c:if test="${ !vocabGroup.vocabNode.noDisplay }">
								<c:if test="${ empty vocabGroup.vocabNode.subList }">
									<c:if test="${ vocabGroup.libraryTotal != 0 }">
										<tr>
											<td align=right width=240>						
												<div class=pad>${ vocabGroup.vocabNode.label }</div>
											</td>
											<td>
												${ vocabGroup.libraryTotal }						
											</td>
										</tr>										
									</c:if>
								</c:if>			
							</c:if>
						</c:forEach>
										
						<jsp:setProperty name="histogramForm" property="currentVocabName" value="resourceType"/>
						<jsp:setProperty name="histogramForm" property="currentVocabFramework" value="adn"/>
						<tr><td align=right width=240>&nbsp;</td><td>&nbsp;</td></tr>
						<tr><td align=right width=240><div class=pad><h4>Resource Type</h4></div></td><td>&nbsp;</td></tr>					
						<c:forEach items="${ histogramForm.vocabList }" var="vocabGroup" varStatus="status">
							<c:if test="${ !vocabGroup.vocabNode.noDisplay }">
								<c:if test="${ empty vocabGroup.vocabNode.subList }">
									<c:if test="${ vocabGroup.libraryTotal != 0 }">
										<tr>
											<td align=right width=240>						
												<div class=pad>${ vocabGroup.vocabNode.label }</div>
											</td>
											<td>
												${ vocabGroup.libraryTotal }						
											</td>
										</tr>										
									</c:if>
								</c:if>			
							</c:if>
						</c:forEach>
										
						<jsp:setProperty name="histogramForm" property="currentVocabName" value="subject"/>
						<jsp:setProperty name="histogramForm" property="currentVocabFramework" value="adn"/>
						<tr><td align=right width=240>&nbsp;</td><td>&nbsp;</td></tr>
						<tr><td align=right width=240><div class=pad><h4>Subject</h4></div></td><td>&nbsp;</td></tr>					
						<c:forEach items="${ histogramForm.vocabList }" var="vocabGroup" varStatus="status">
							<c:if test="${ !vocabGroup.vocabNode.noDisplay }">
								<c:if test="${ empty vocabGroup.vocabNode.subList }">
									<c:if test="${ vocabGroup.libraryTotal != 0 }">
										<tr>
											<td align=right width=240>						
												<div class=pad>${ vocabGroup.vocabNode.label }</div>
											</td>
											<td>
												${ vocabGroup.libraryTotal }						
											</td>
										</tr>										
									</c:if>
								</c:if>			
							</c:if>
						</c:forEach>
										
				</c:if>
			</c:forEach>
		</table> 
	
	</body>
</html> 