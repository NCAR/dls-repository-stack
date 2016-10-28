<c:if test="${param.facet == 'on' || param.facet == 'true'}">
<%@ 
page import="org.apache.lucene.facet.taxonomy.CategoryPath" %><%@ 
page import="edu.ucar.dls.services.dds.action.form.DDSServicesForm" %><%@
page import="org.apache.lucene.facet.search.results.FacetResultNode" %><%@ 
page import="java.util.HashMap" %>
<c:if test="${not empty param['facet.category']}"><%-- Facet taxonomies/hierarchies (optimized faceting) --%>
	<facetResults>
		<%-- Display the facet results --%>
		<c:forEach var="facetResult" items="${df11.facetResults}">
			<c:set var="categoryPath" value="${facetResult.facetRequest.categoryPath}"/>
			<% 
				CategoryPath myPath = (CategoryPath)pageContext.getAttribute("categoryPath");
				String category = myPath.getComponent(0);
				pageContext.setAttribute("category",category);
				DDSServicesForm myDDSServicesForm = (DDSServicesForm)request.getAttribute("df11");
				String delimiter = myDDSServicesForm.getFacetCategoryDelimiter(category);
				pageContext.setAttribute("delimiter",delimiter);
				HashMap foundCategories = (HashMap)pageContext.getAttribute("foundCategories");
				if(foundCategories == null)
					foundCategories = new HashMap();
				foundCategories.put(category,new Object());
				pageContext.setAttribute("foundCategories",foundCategories);				
				String requestedPath = null;
				pageContext.removeAttribute("delimiterLabel");
				if(delimiter != null && delimiter.trim().length() > 0) {
					pageContext.setAttribute("delimiterLabel","pathDelimiter=\""+delimiter+"\"");
					requestedPath = myPath.toString(delimiter.charAt(0));
					requestedPath = requestedPath.substring(requestedPath.indexOf(delimiter)+1,requestedPath.length());
				}
				if(requestedPath != null && !requestedPath.equals(category))
					pageContext.setAttribute("requestedPathLabel","requestedPath=\""+requestedPath+"\"");				
			%>
			<facetResult category="<c:out escapeXml='true' value='${category}'/>" ${requestedPathLabel} ${delimiterLabel} facetId="${facetResult.facetResultNode.ordinal}" maxResults="${facetResult.facetRequest.numResults}" maxDepth="${facetResult.facetRequest.depth}" maxLabels="${facetResult.facetRequest.numLabel}" count="<fmt:formatNumber type='number' value='${facetResult.facetResultNode.value}'/>">
				<c:forEach var="subResult" items="${facetResult.facetResultNode.subResults}">
					<% 
						CategoryPath subResultPath = ((FacetResultNode)pageContext.getAttribute("subResult")).getLabel();
						String pathLabel = null;
						if(subResultPath != null){
							if(delimiter != null && delimiter.trim().length() > 0)
								pathLabel = subResultPath.toString(delimiter.charAt(0),1,subResultPath.length());
							else
								pathLabel = subResultPath.toString('/',1,subResultPath.length());
						}
						if(pathLabel != null && pathLabel.trim().length() > 0)
							pageContext.setAttribute("pathLabel",pathLabel);
						else
							pageContext.setAttribute("pathLabel",null);
					%>
					<c:if test="${not empty pathLabel}">
						<c:set var="pathLabel">path="<c:out escapeXml='true' value='${pathLabel}'/>"</c:set>
					</c:if>
					<result ${pathLabel} facetId="${subResult.ordinal}" count="<fmt:formatNumber type='number' value='${subResult.value}'/>"/></c:forEach>
			</facetResult>
		</c:forEach>
		<%-- Report empty results: --%>		
		<c:forEach var="category" items="${paramValues['facet.category']}">
			<c:if test="${empty foundCategories[category]}">
				<% 
					DDSServicesForm myDDSServicesForm = (DDSServicesForm)request.getAttribute("df11");
					String category = (String)pageContext.getAttribute("category");
					String delimiter = myDDSServicesForm.getFacetCategoryDelimiter(category);
					pageContext.removeAttribute("delimiterLabel");
					if(delimiter != null && delimiter.trim().length() > 0)
						pageContext.setAttribute("delimiterLabel","pathDelimiter=\""+delimiter+"\"");
					String requestedPath = request.getParameter("f."+category+".path");
					if(requestedPath != null)
						pageContext.setAttribute("requestedPathLabel","requestedPath=\""+requestedPath+"\"");
					
					String maxResults = request.getParameter("f."+category+".maxResults");
					if(maxResults != null)
						pageContext.setAttribute("maxResults",maxResults);
					else
						pageContext.setAttribute("maxResults",myDDSServicesForm.getGlobalMaxFacetResults());
					
					String maxDepth = request.getParameter("f."+category+".maxDepth");
					if(maxDepth != null)
						pageContext.setAttribute("maxDepth",maxDepth);
					else
						pageContext.setAttribute("maxDepth",myDDSServicesForm.getGlobalMaxFacetDepth());					

					String maxLabels = request.getParameter("f."+category+".maxLabels");
					if(maxLabels != null)
						pageContext.setAttribute("maxLabels",maxLabels);
					else
						pageContext.setAttribute("maxLabels",myDDSServicesForm.getGlobalMaxFacetLabels());					
				%>
				<facetResult category="<c:out escapeXml='true' value='${category}'/>" ${requestedPathLabel} ${delimiterLabel} maxResults="${maxResults}" maxDepth="${maxDepth}" maxLabels="${maxLabels}" count="0"/>
			</c:if>
		</c:forEach>
	</facetResults>
</c:if>
<c:if test="${not empty df11.facetMap}"><%-- Facet fields (non-optimized faceting) --%>
	<facetFields>
		<c:forEach var="facet" items="${df11.facetMap}">
			<field name="<c:out escapeXml='true' value='${facet.key}'/>"><c:forEach 
				var="term" items="${facet.value}">
					<term count="${term.value}"><c:out escapeXml='true' value='${term.key}'/></term></c:forEach>
			</field>
		</c:forEach>
	</facetFields>
</c:if>
</c:if>
