<%@ page language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored ="false" %>
<%-- 
This page generates links for downloading software releases.
It is designed to be imported in other pages like so:

<c:import url="product_release_links.jsp?packageId=joai&xmlFileLocation=http://www.dlese.org/downloads/downloads_config.xml" />

To use, set the following http parameters: 

xmlFileLocation - A URL to the configuration file

packageId -	(optional - not used currently) The IDs of the packages you wish 
  			to display as a comma separate list
			For example: joai
			
See an example config file here:
product_release_config_example.xml
--%>

<c:catch var="productReleasesError">
	<x:parse var="xmlDom"><c:import url="${param.xmlFileLocation}"/></x:parse>
	<x:set var="packages" select="$xmlDom/productReleases/package"/>
		<c:set var="numPackages">
			<x:out select="count($packages)"/>
		</c:set>
	<c:choose>
		<c:when test="${empty xmlDom}">
			<c:set var="showEmptyProductReleaseMessage" value="t"/>
		</c:when>	

		<c:otherwise>
			<c:choose>
				<c:when test="${numPackages ==0}">
					<c:set var="showEmptyProductReleaseMessage" value="t"/>
				</c:when>
				<c:otherwise>
					<x:forEach select="$packages" var="package">
						<c:set var="name"><x:out select="$package/name"/></c:set>
						<c:if test="${not empty fn:trim(name)}">
							<h1>${name}</h1>
						</c:if>

						<c:set var="description"><x:out select="$package/description" escapeXml="false"/></c:set>
						<c:if test="${not empty fn:trim(description)}">
							<p>${description}</p>
						</c:if>						
						
						<x:forEach select="$package/release" var="release" varStatus="i">						
							<c:choose>
								<c:when test="${i.count == 1}">
									<h3>Current version</h3>
									<br/>
								</c:when>
								<c:when test="${i.count == 2}">
									<h3>Older versions</h3>
									<br/>
								</c:when>
							</c:choose>
							
							<c:set var="version"><x:out select="$release/@version"/></c:set>
							<c:if test="${not empty fn:trim(version)}">
								<p>Version ${version}</p>
							</c:if>
							
							<c:set var="notes"><x:out select="$release/notes" escapeXml="false"/></c:set>
							<c:if test="${not empty fn:trim(notes)}">
								<p>${notes}</p>
							</c:if>	
							

							<ul>
								<x:forEach select="$release/link" var="link">
									<li>
										<a href='<x:out select="$link/url"/>'><x:out select="$link/name"/></a>
									</li>
								</x:forEach>
							</ul>
						</x:forEach>
					</x:forEach>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>	
</c:catch>
<c:if test="${not empty productReleasesError}"> 
<!-- ######################
 
Error reading the product release XML configuration: ${productReleasesError}

 ###################### -->
	<c:set var="showEmptyProductReleaseMessage" value="t"/>
</c:if>

<c:if test="${not empty showEmptyProductReleaseMessage}">
	<p>No releases avaialable</p>
</c:if>


