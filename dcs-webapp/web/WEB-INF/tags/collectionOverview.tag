<%-- browseWithRecordButton.tag
	- requires that a docReader is supplied 
	- creates a BUTTON that brings up the selected record in the browser
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="reader" required="true" type="edu.ucar.dls.index.reader.DleseCollectionDocReader" %>

<c:set var="dcsSetInfo" value="${sf:getDcsSetInfo(reader.key, sessionBean.sets)}" />
<c:if test="${not empty dcsSetInfo}">
<table bgcolor="eeeeee" width="90%" cellpadding="1" cellspacing="0">
	<tr>
		<td colspan="2">
			<div class="searchResultValues">
			<font color="#333333"><b>Collection overview</b></font>
			</div>
		</td>
	</tr>						
	<tr>
		<td nowrap>
			<div class="searchResultValues">
				<em>Format of files:</em> 
			</div>
		</td>
		<td>
			<div class="searchResultValues">${dcsSetInfo.format}</div>
		</td>
	</tr>						
	<tr>
		<td nowrap>
			<div class="searchResultValues">
				<em>Num Records:</em> 
			</div>
		</td>
		<td>
			<div class="searchResultValues">
				<a href='query.do?s=0&q=&scs=0${dcsSetInfo.setSpec}'
					title='browse all the records in ${dcsSetInfo.name}'>${dcsSetInfo.numFiles}</a>
			</div>
		</td>
	</tr>
	<tr>
		<td nowrap>
			<div class="searchResultValues">
				<em>Num Valid:</em> 
			</div>
		</td>
		<td>
			<div class="searchResultValues">
				<a href='query.do?q=&s=0&scs=0${dcsSetInfo.setSpec}&vld=valid' 
					title='Browse valid items in ${dcsSetInfo.name}'>${dcsSetInfo.numValid}</a>
			</div>
		</td>
	</tr>
	<tr>
		<td nowrap>
			<div class="searchResultValues">
				<em>Num Not Valid:</em> 
			</div>
		</td>
		<td>
			<div class="searchResultValues">
				<a href='query.do?q=&s=0&scs=0${dcsSetInfo.setSpec}&vld=notvalid' 
					title='Browse non-valid items in ${dcsSetInfo.name}'>${dcsSetInfo.numNotValid}</a>
			</div>
		</td>
	</tr>	
</table>
</c:if>

