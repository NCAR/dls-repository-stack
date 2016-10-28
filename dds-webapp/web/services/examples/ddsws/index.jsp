<%--
	Example JSP for displaying information using the DDSWebService (ddsws v1.1)
	See @DDS_SERVER_BASE_URL@/services/
	
	Provided by the Digital Library for Earth System Education (DLESE)
	free and with no warranty. This example may be copied and modified.
	e-mail: support@dlese.org

	These examples are available for download at 
	http://sourceforge.net/project/showfiles.php?group_id=23991&package_id=123037
	
	Requires Apache Tomcat 5 (http://jakarta.apache.org/tomcat/)
	or other JSP 2.0 compliant JSP container and the 
	JSTL v1.1 tag libraries, which are included in the examples download
	or can be obtained from http://java.sun.com (standard.jar and jstl.jar).

	This is JSP client version @DDSWS_CLIENT_VERSION@.		
--%>

<%-- Reference the JSTL tag libraries that we may use --%>
<%@ page language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ page isELIgnored ="false" %>

<html>
<head>
<title>DLESE Search Web Service JSP Template and Examples, Version @DDSWS_CLIENT_VERSION@</title> 

<style type='text/css'><!--
BODY {
	margin-left: 20px;
	margin-right: 20px;	
	margin-top: 20px;
	margin-bottom: 20px;
	padding: 0px;	
	font-family: Arial, Helvetica, sans-serif; 
	font-size: 10pt; 			
} 

H1 { 
	Font-Family : Arial, Helvetica, sans-serif; 
	Font-Size : 14pt ;
	Color : #333366; 
} 

H2 { 
	Font-Family : Arial, Helvetica, sans-serif; 
	Font-Size : 12pt ;
	Color : #333366; 
} 

H3 { 
	Font-Family : Arial, Helvetica, sans-serif; 
	Font-Size : 10pt ;
	Color : #333366;
	margin-top: 25px;
	margin-bottom: 0px;
} 
	  
TD { 
	font-family: Arial, Helvetica, sans-serif; 
	font-size: 10pt; 
} 
--></style>

</head>
<body>

<h1>DLESE Search Web Service JSP Template and Examples</h1>

<P>
The template and examples provided in this toolkit illustrate how to use the 
<a href="@DDS_SERVER_BASE_URL@/services/">DLESE
search web service</a> to implement a fully functional library search page
and other useful clients that access and incoporate information from the 
<a href="http://www.dlese.org/" title="Digital Library for Earth System Education">DLESE</a>
metadata repository. These examples are implemented using
<a href="http://java.sun.com/products/jsp/">Java Server Pages (JSP)</a>, 
and developers are free to 
<a href="http://sourceforge.net/project/showfiles.php?group_id=23991&package_id=123037">download this toolkit</a> 
and use the code provided as 
a starting point for their own client implementations.
</P>

<p>This is client version @DDSWS_CLIENT_VERSION@, which uses search service version 
<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp">DDSWS v1.1</a>.</p>

  <p>
  <a href="http://sourceforge.net/project/showfiles.php?group_id=23991&package_id=123037">Download 
  the JSP template and examples here</a>.
	
   Requires <a href="http://jakarta.apache.org/tomcat/">Apache Tomcat 5</a>
   or other JSP 2.0 compliant JSP container.
	</p>
	
	<p>
	<table border="0" cellpadding="2" cellspacing="0" >
		<tr>
			<td colspan="2">
				Here are some useful resources to help understand the code and technologies 
				used in the template and examples:
			</td>
		</tr>
		<tr>
			<td>
			- <a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp">DDSWebService documentation</a><br>		
			- <a href="http://www.zvon.org/xxl/XPathTutorial/General/examples.html">XPath tutorial at zvon</a><br>
			- <a href="http://www.w3schools.com/xpath/default.asp">XPath tutorial at W3C schools</a>
			</td>
			<td>
			&nbsp; - <a href="http://www.w3.org/TR/xpath">XPath language description</a><br>
			&nbsp; - <a href="http://java.sun.com/j2ee/1.4/docs/tutorial/doc/index.html">JSTL tutorial from Sun</a> (see chapter 14)<br>
			&nbsp; - <a href="http://www.jadecove.com/jstl-quick-reference.pdf">JSTL reference guide</a>
			</td>
		</tr>
	</table>
	</p>

  
  <p>
  <br>

	<h2>JSP template</h2>
	<p>
	This template provides a fully functional, customizable search page that searches for educational 
	in DLESE. It can be installed out-of-the-box and then tailored to match the graphics, 
	colors, and design of a given web site and customized to search over and highlight library resources 
	for a specific audience.</p>
	<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
		<tr>
			<td>
				<nobr>
				<a href="templates/index.jsp">Template</a> 
				</nobr>
			</td>
			<td>
				This template creates a 'library-in-a-box' search page that includes checkbox menus for grade ranges, subjects,
				resource types and collections. The HTML may be modified and menus may be added or removed as desired. In addition, 
				the page's search scope may be customized to highlight any number of contextualized 
				search domains targeted for a particular audience.
			</td>
		</tr>	
	</table>

	<br><br>
	
	<h2>Examples</h2>		
	<p>The following JSP examples are useful to help learn the web service protocol. 
	The index below shows example clients listed by the web service
	request they illustrate. Each client is implemented in a single JSP page.
	More simple examples appear at the top with 
	complexity increasing as you go down. As you go through these examples, use the 
	<a href="@DDS_SERVER_BASE_URL@/services/index.jsp#ddsws">DDSWebService explorer</a> to create
	and view the web service requests and responses, and be sure to look at the 
	<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp">DDSWebService v1.1 documentation</a> for complete
	information about each web service request.</p> 	
	
	<h3>GetRecord</h3>
	<p>The <a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#GetRecord">GetRecord</a> request is used to access the metadata from a single record</p>
	<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
		<tr>
			<td>
				<nobr>
				<a href="GetRecord-example1.jsp">Example 1</a>
				</nobr>
			</td>
			<td>
				Displays the title, description and URL from a single ADN record 
			</td>
		</tr>
		<tr>
			<td>
				<nobr>
				<a href="GetRecord-example2.jsp">Example 2</a>
				</nobr>
			</td>
			<td>
				Displays the title, description and URL from a single ADN record
				and allows the user to enter the ID of the record to display
			</td>
		</tr>		
	</table>


	<h3>UserSearch</h3>
	<p>The <a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#UserSearch">UserSearch</a> 
	request is used perform textual and field-based searches using the same
	query language and search logic available to users who perform searches through the DLESE web site.
    </p>
	<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
		<tr>
			<td>
				<nobr>
				<a href="UserSearch-example1.jsp">Example 1</a> 
				</nobr>
			</td>
			<td>
				Performs a textual search for <i>water on mars</i> and displays the first ten results 
			</td>
		</tr>
		<tr>
			<td>
				<nobr>
				<a href="UserSearch-example2.jsp">Example 2</a>
				</nobr>
			</td>
			<td>
				Performs a textual search using the text entered by the user and
				displays the first ten results
			</td>
		</tr>
		<tr>
			<td>
				<nobr>
				<a href="UserSearch-example3.jsp">Example 3</a>
				</nobr>
			</td>
			<td>
				Performs a textual search using the text entered by the user
				and adds links for paging through the set of results
			</td>
		</tr>			
	</table>

	<h3>ListGradeRanges</h3>
	<p>The <a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListGradeRanges">ListGradeRanges</a> 
	request is used to discover the grade range vocabularies that are available in the library.
	ListGradeRanges is one of five <a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#VocabList">vocabulary list requests</a>,
	including <a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListGradeRanges">ListGradeRanges</a>, 
	<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListSubjects">ListSubjects</a>, 
	<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListResourceTypes">ListResourceTypes</a>, 
	<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListContentStandards">ListContentStandards</a>, and 
	<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListCollections">ListCollections</a>.
	The examples here may be generalized to any of these vocabulary list requests.</p>
	<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
		<tr>
			<td>
				<nobr>
				<a href="ListGradeRanges-example1.jsp">Example 1</a> 
				</nobr>
			</td>
			<td>
				Displays a list of grade ranges that are available in the library
			</td>
		</tr>
		<tr>
			<td>
				<nobr>
				<a href="ListGradeRanges-example2.jsp">Example 2</a>
				</nobr>
			</td>
			<td>
				Renders a drop-down menu to select a grade range and displays 
				the first ten records in the library for that grade range after it has been 
				selected by the user
			</td>
		</tr>			
	</table>

	
	<h3>ListCollections</h3>
	<p>The <a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListCollections">ListCollections</a> 
	request is used to discover the metadata collections that are available in the library and to display
	information about the collections, such as the name of the collection and the number of records it contains.
	ListCollections is one of five <a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#VocabList">vocabulary list requests</a>,
	including <a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListGradeRanges">ListGradeRanges</a>, 
	<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListSubjects">ListSubjects</a>, 
	<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListResourceTypes">ListResourceTypes</a>, 
	<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListContentStandards">ListContentStandards</a>, and 
	<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListCollections">ListCollections</a>.</p>
	<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
		<tr>
			<td>
				<nobr>
				<a href="ListCollections-example1.jsp">Example 1</a> 
				</nobr>
			</td>
			<td>
				Displays information about the collections that are in the library
			</td>
		</tr>
		<tr>
			<td>
				<nobr>
				<a href="ListCollections-example2.jsp">Example 2</a>
				</nobr>
			</td>
			<td>
				Renders a drop-down menu to select a collection and displays 
				the first ten records from the collection after it has been selected by the user
			</td>
		</tr>			
	</table>

	<h3>Searching by date and sorting by field</h3>
	<p>These examples show how to use the <a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#Search">Search</a> 
	request to add searching by date and sorting by field to your pages. Searching by date
	and sorting by field can be performed independently or added to any Search request and combined with
	textual searches and searches limited by grade range, subject, resource type, content standard, etc.
    </p>
	<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
		<tr>
			<td>
				<nobr>
				<a href="DateSearch-example1.jsp">Example 1</a> 
				</nobr>
			</td>
			<td>
				Performs a search for records that are new to the library within the 
				last 28 days and sorts the results by their date.
			</td>
		</tr>
		<tr>
			<td>
				<nobr>
				<a href="DateSearch-example2.jsp">Example 2</a> 
				</nobr>
			</td>
			<td>
				Performs a search for records that are new to the library within the 
				last 28 days and allows the user to sort by date, title, URL, ID or collection.			
			</td>
		</tr>				
	</table>

	
	<h3>Putting it all together</h3>
	<p>These examples demonstrate real-world use of the protocol by accessing and combining several requests.</p>
	<table border="1" cellpadding="2" cellspacing="0" style="background-color:#eeeeee" width="95%"> 
		<tr>
			<td>
				<nobr>
				<a href="full_example1.jsp">Example 1</a> 
				</nobr>
			</td>
			<td>
				This example allows a user to perform a textual search for educational resources 
				in the library and view and page through the results. Shows an example of using
				the UserSearch request.
			</td>
		</tr>
		<tr>
			<td>
				<nobr>
				<a href="full_example2.jsp">Example 2</a> 
				</nobr>
			</td>
			<td>
				This example allows a user to perform a textual search for educational resources 
				in the library, select a grade range and subject to limit their search and view and 
				page through the results. Shows examples of using the 
				<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#UserSearch">UserSearch</a>, 
				<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListGradeRanges">ListGradeRanges</a> and 
				<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListSubjects">ListSubjects</a> requests 
				in a single page and implements caching of the grade ranges and subjects for efficiency.
			</td>
		</tr>
		<tr>
			<td>
				<nobr>
				<a href="full_example3.jsp">Example 3</a> 
				</nobr>
			</td>
			<td>
				This example allows a user to perform a textual search for educational resources 
				in the library, select a grade range and subject to limit their search, view and 
				page through the results and display the full metadata for a single record. Shows 
				examples of using the 
				<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#UserSearch">UserSearch</a>, 
				<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#GetRecord">GetRecord</a>, 
				<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListGradeRanges">ListGradeRanges</a>,  
				<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListListResourceTypes">ListListResourceTypes</a> and 
				<a href="@DDS_SERVER_BASE_URL@/services/ddsws1-1/service_specification.jsp#ListSubjects">ListSubjects</a> 
				requests in a single page and implements caching of the grade range and subject 
				select lists for efficiency.
			</td>
		</tr>		
	</table>
	
  </p>
  <p>
  <center>
  Toolkit provided by the <a href="http://www.dlese.org/">Digital Library for Earth System Education</a> (DLESE)</center>
 
</body>
</html>


