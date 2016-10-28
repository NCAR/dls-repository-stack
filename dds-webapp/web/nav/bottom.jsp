
<div class="footerBar">

	<div style="text-align: center; margin-top:10px;">
		<a href="http://nsf.gov/" title="Software developed with the support of the National Science Foundation (NSF)"><img src="${pageContext.request.contextPath}/images/nsf1.gif" width="50" height="50"></a>
		<a href="http://dls.ucar.edu/" title="Software developed by Digital Learning Sciences (DLS) at UCAR"><img style="margin-left:10px;" src="${pageContext.request.contextPath}/images/dls-ucp-logo-small-white-v2.jpg" height="45"></a>
	</div>

</div>

<style type="text/css">
<!--
	.footerBar {
		margin-top:30px; 
		padding-top:10px; 
		border-top: solid 1px #aaa; 
		text-align: center;
	}
-->
</style>	

<%-- If the DLESE library is up and running in this domain, display DLESE banners, etc: --%>
<c:if test="${isDeploayedAtDL}">
	<style type="text/css">
	<!--
		.dlese_footerLinks { margin-top:0px; }
		.footerBar { border-top: 0px; }		
	-->
	</style>		

	<%-- DLESE template bottom (footer links and logo) --%>
	<c:import url="${domain}/dlese_shared/templates/content_bottom.html"/>
</c:if>

</td></tr></table><%-- End content cell and table --%>
