<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ page contentType="text/html; charset=UTF-8" %>

<c:catch var="xmlParseError">
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${param.xml}" var="myRec"/>
	<x:set var="root" select="$myRec/record"/>							
</c:catch>
<c:choose>
	<c:when test="${not empty xmlParseError}">
		<div style="color:red;">Error parsing record: ${xmlParseError}</div>
	</c:when>
	<c:otherwise>
	
		<%-- compute values --%>
		<c:set var="title">
			<x:out select="$root/title"/>
		</c:set>						
		<c:set var="year">
			<x:out select="$root/year"/>
		</c:set>	
		<c:set var="pubname">
			<x:out select="$root/pubname"/>
		</c:set>	
		
		<c:set var="volume">
			<x:out select="$root/volume"/>
		</c:set>
		
		<c:set var="pages">
			<x:out select="$root/pages"/>
		</c:set>
		
		<c:set var="pubstatus">
			<x:out select="$root/pubstatus"/>
		</c:set>
		<c:set var="statusdate">
			<x:out select="$root/statusdate"/>
		</c:set>
		<c:set var="statusdate">
			<x:out select="$root/statusdate"/>
		</c:set>		
		<c:set var="statusdate">
			<x:out select="$root/statusdate"/>
		</c:set>		
		<c:set var="author">
			<x:out select="$root/authors/author"/>
		</c:set>		
		
		<c:set var="pub_id">
			<x:out select="$root/pub_id"/>
		</c:set>	
		
		<c:set var="wos_id">
			<x:out select="$root/wos_id"/>
		</c:set>	
		
		
		<%-- display values --%>
<%-- 		<c:if test="${not empty title}">
			<div class="browse-item-title">${title}</div>
		</c:if>					
		
		<c:if test="${not empty pubname}">
			<div class="searchResultValues"><i style="font-size:120%">${pubname}</i></div>
		</c:if>		
		
<%--		<c:if test="${not empty author}">
			<div class="searchResultValues"><em>Author:</em> &nbsp;
			<x:forEach select="$root//authors/author">
			  	
				<dds:keywordsHighlight><x:out select="."/></dds:keywordsHighlight>

			</x:forEach>
			</div>
		</c:if>	 --%>	
		
		<%-- Display Citation --%>
		
		<c:if test="${not empty author}">
			<x:forEach select="$root//authors/author" varStatus="i">
				<c:set var="lastName">
			    <x:out select="lastName"/>
			  </c:set>	
		          <c:set var="firstName">
			    <x:out select="firstName"/>
			  </c:set>	
				<c:set var="middleName">
			    <x:out select="middleName"/>
			  </c:set>
		    <c:set var="middleName">
			    <x:out select="middleName"/>
			  </c:set>
			
				<dds:keywordsHighlight>
			  ${lastName} ${firstName}${middleName}<c:if test="${not i.last}">,</c:if>
				</dds:keywordsHighlight>
					
			</x:forEach>
		</c:if>

		<span class="citation-year">${year}:</span>
		
		<c:if test="${not empty title}">
			<dds:keywordsHighlight><span style="font-weight:bold">${title},</span></dds:keywordsHighlight>
		</c:if>
		
<%-- 		<c:if test="${not empty editor}">
			<span class="citation-title">In ${edtior}</span>
		</c:if>	 --%>	
		
		<c:if test="${not empty pubname}">
			<span style="font-style:italic">${pubname},</span>
		</c:if>		
		
		<c:if test="${not empty volume}">
			<span class="citation-volume">${volume},</span>
		</c:if>		
		
		<c:if test="${not empty pages}">
			<span class="citation-pages">${pages}</span></c:if><c:choose><c:when 
			test="${not empty doi}"><span class="citation-doi">doi: ${doi}.</span>
		</c:when><c:otherwise>.</c:otherwise></c:choose>

	<hr align="left" size="0.5px" color="#999999" width="50%">
		
		<div class="searchResultValues">
		<c:if test="${not empty wos_id}"><em>WOS ID: </em> ${wos_id}</c:if>
		<c:if test="${not empty pub_id}"><em>PUBS ID: </em> ${pub_id}</c:if>
		</div>
		

	</c:otherwise>
</c:choose>



