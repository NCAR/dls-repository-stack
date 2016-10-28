<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/response.tld" prefix="resp" %>
<jsp:useBean id="rssForm" class="edu.ucar.dls.services.dds.action.form.DDSRSS20Form" scope="request"/>
<%-- Set the response type --%>
<c:choose>
	<c:when test="${param.rt == 'text'}"><resp:setContentType>text/plain</resp:setContentType></c:when>
	<c:when test="${param.rt == 'validate'}"><resp:setContentType>text/html</resp:setContentType></c:when>
	<c:otherwise>
		<resp:setContentType>text/xml</resp:setContentType>
	</c:otherwise>
</c:choose>
<dds:setKeywordsHighlight keywords="<%= request.getParameter( "q" ) %>" highlightColor="#000099" />
<%-- RSS (newsfeed) view of DDS search results --%>
<c:choose> <%-- RSS feeds can indicate with param 'show' how many results to display: --%>
	<c:when test="${ not empty param.show }">
		<c:set var="showCount" value="${ param.show }" />
	</c:when>
	<c:otherwise>
		<c:set var="showCount" value="250" />
	</c:otherwise>
</c:choose>
<rss version="2.0">
	<channel>
		<c:set var="htmlUrl">http://www.dlese.org/library/query.do?q=&amp;s=0&amp;sortby=wndate&amp;wntype=${ param.wntype }&amp;wnfrom=recent</c:set>
		<link>${ htmlUrl }</link>
		<c:choose>
			<c:when test="${not empty param.wntype}">	<%-- What's new --%>
				<c:if test="${ param.wntype == 'itemnew' }">
					<title>DLESE - New resources</title>
					<description>New resources in the DLESE library</description>
					<c:set var="category">New resources</c:set>
				</c:if>
				<c:if test="${ param.wntype == 'itemannocomplete' }">
					<title>DLESE - Newly reviewed resources</title>
					<description>Recently reviewed resources in the DLESE library</description>					
					<c:set var="category">Newly reviewed resources</c:set>
				</c:if>
				<c:if test="${ param.wntype == 'itemannoinprogress' }">
					<title>DLESE - New resources under review</title>
					<description>Resources recently under review in the DLESE library</description>
					<c:set var="category">New resources under review</c:set>
				</c:if>			
			</c:when>
			<c:otherwise>	<%-- Query-based --%>
				<title>DLESE Resources: &quot;<c:out value="${ rssForm.q }"/>&quot;</title>
				<description>Top search results from the DLESE library</description>	
				<c:set var="category">Library search</c:set>			
			</c:otherwise>
		</c:choose>
		<category>${ category }</category>			
		<ttl>120</ttl>
		<image>
			<url>http://www.dlese.org/images/newsfeed_logo.jpg</url>
			<title>
				Digital Library for Earth System Education
			</title>
			<link>http://www.dlese.org/</link>
			<width>
				64
			</width>
			<height>
				24
			</height>
		</image>
		<textInput>
			<title>
				Search DLESE
			</title>
			<link>http://www.dlese.org/library/query.do</link>
			<description>
				Find resources pertaining to Earth System education
			</description>
			<name>
				q
			</name>
		</textInput>
		<c:choose>
			<c:when test="${ empty rssForm.results }">
				<item>
					<title>No resources</title>
					<link>${ htmlUrl }</link>
					<description>
						DLESE currently has no resources of this type.
					</description>
				</item>
			</c:when>
			<c:otherwise>
				<c:forEach var="result" items="${ rssForm.results }" end="${ showCount-1 }" varStatus="status">
					<item>
						<title><c:out value="${ result.docReader.title }" escapeXml="true"/></title>
						<link><c:out value="${ result.docReader.url }" escapeXml="true"/></link>
						<description><![CDATA[<dds:keywordsHighlight truncateString="true">${ result.docReader.description }</dds:keywordsHighlight>
							<a href="http://www.dlese.org/library/catalog_${ result.docReader.id }.htm">Full description</a>				
						]]></description>
						<category>${ category }</category>
						<c:if test="${ result.docReader.whatsNewDate != '' }">
							<fmt:parseDate var="pubDate" pattern="MMM dd, yyyy" 
								value="${ result.docReader.whatsNewDate }"/>						
							<pubDate>
								<fmt:formatDate value="${ pubDate }"
									pattern="E, dd MMM yyyy" /> 00:00:000 MDT
							</pubDate>
						</c:if>							
					</item>
				</c:forEach>	
			</c:otherwise>
		</c:choose>
	</channel>
</rss>