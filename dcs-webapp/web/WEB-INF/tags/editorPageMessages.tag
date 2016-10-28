<%-- page-messages.tag 
	- requires that a propertyId param is set in calling page 
	- displays messages that pertain to a page (rather than an element)
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%-- Error messages --%>
<logic:messagesPresent>
	<div id="page-messages">
	<%-- editing errors --%>
	<logic:messagesPresent property="pageErrors">
		<div class="page-error-box">
			<div class="error-msg">
				<b>Oops! - Errors were found in your input. </b>
				<%-- [ <a href="javascript:toggleVisibility ('editor_error_messages');"> show/hide </a> ] --%>
				<input type="button" class="record-action-button" 
							value="hide/show" onclick="toggleVisibility ('editor_error_messages');" />
				</div>
				<div id="editor_error_messages" style="display:block">
					<html:messages id="msg" property="pageErrors">
						<div class="error-msg"><li>${msg}</li></div>
					</html:messages>
				</div>
		</div>
	</logic:messagesPresent>

	<logic:messagesPresent property="entityErrors">
		<div class="page-error-box">
			<div class="error-msg"><b>Attention! - Formatting errors were found in your input that require repair before this
				record can be saved or edited further.</b></div>
				<html:messages id="msg" property="entityErrors">
					<div class="error-msg"><li>${msg}</li></div>
			</html:messages>
		</div>
	</logic:messagesPresent>
	
	<%-- general errors --%>
	<logic:messagesPresent property="error">
		<div class="page-error-box">
			<html:messages id="msg" property="error">
				<div class="error-msg"><li>${msg}</li></div>
			</html:messages>
		</div>
	</logic:messagesPresent>
	
	<%-- similar url messages --%>
 	<logic:messagesPresent property="similarUrls">

 		<div class="page-error-box">

			<div class="error-msg"><b>Similar primary URL detected</b></div>
			<c:choose>
				<c:when test="${fn:length(sef.sims) > 1}">
				Your URL is similar to other primary or mirror URLs already cataloged.
				The similar URLs and their record numbers are:
				</c:when>
				<c:otherwise>
				Your URL is similar to another primary or mirrorURL already cataloged. 
				The similar URL and its record number are:
				</c:otherwise>
			</c:choose>
 			<ul>
				<c:forEach var="sim" items="${sef.sims}">
					<div><li><a href="${sim.url}"
					onclick="return doView('${sim.url}')">${sim.url}</a>
						- cataloged in 
						<a href="../browse/display.do?fileid=${sim.id}&rt=text"
						 target="_blank"
						 title="see record xml in new window">${sim.id}</a></li>
				</div>
				</c:forEach>
			</ul>
			Primary URLs must be unique within a collection. If you are sure your URL 
			<a href="${sef.validatedUrl}"
					onclick="return doView('${sef.validatedUrl}')">${sef.validatedUrl}</a>
			is unique click <span class="doc-em">Save Record</span> and continue 
			<span class="doc-em">or</span> click <span class="doc-em">Exit</span> 
			to quit without saving recent changes.
			<html:messages id="msg" property="similarUrls">
				<div>${msg}</div>
			</html:messages>
		</div>
	</logic:messagesPresent>
			
 	<logic:messagesPresent property="duplicateUrls">

 		<div class="page-error-box">

			<div class="error-msg"><b>Duplicate primary URL detected</b></div>
			Your URL is already cataloged as either a primary or mirror URL:
			<ul>
			<c:forEach var="dup" items="${sef.dups}" >
 				<div><li><a href="${dup.url}"
					onclick="return doView('${dup.url}')">${dup.url}</a>
						- cataloged in 
						<a href="../browse/display.do?fileid=${dup.id}&rt=text"
						 target="_blank"
						 title="see record xml in new window">${dup.id}</a></li>
				</div>
			</c:forEach>
			</ul>
			
			Duplicate URLs within a collection are not allowed.<br/>
			Please <span class="doc-em">supply a different URL</span> to continue, 
			<span class="doc-em">or</span> click <span class="doc-em">Exit</span> 
			to quit without saving most recent changes.
			<html:messages id="msg" property="duplicateUrls">
				<div>${msg}</div>
			</html:messages>
		</div>
	</logic:messagesPresent>

 	<logic:messagesPresent property="duplicateValues">

 		<div class="page-error-box">

			<div class="error-msg"><b>Illegal Duplicate Value detected</b></div>
			<p class="compressed">One or more "uniqueValue" fields contain values that have already been cataloged in another
			record, which is not allowed for this type of field.</p>
			
			<html:messages id="msg" property="duplicateValues">
					<li style="margin:0px 0px 3px 15px">${msg}</li>
			</html:messages>
			
			<p class="compressed">In each case, please <span class="doc-em">supply a different value</span> to continue, 
			<span class="doc-em">or</span> click <span class="doc-em">Exit</span> 
			to quit without saving most recent changes.</p>

		</div>
	</logic:messagesPresent>

	
	
<%-- general messages --%>
<logic:messagesPresent property="message">
	<div class="confirm-msg-box">
		<div class="confirm-msg"><b>
			<html:messages id="msg"  property="message"><li>${msg}</li></html:messages>
		</b></div>
	</div>
	</logic:messagesPresent>
  <%-- <HR> --%>
</div>
</logic:messagesPresent>

