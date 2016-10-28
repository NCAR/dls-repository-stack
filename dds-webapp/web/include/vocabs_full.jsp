		<c:catch var="xslError">				
			<c:set var="transformXml"><vocabs:getResponseOPML/></c:set>		
			<c:import var="transformXsl" url="include/vocabs_tab_indented.xsl" />
			<x:transform xml="${ transformXml }" xslt="${ transformXsl }" />
		</c:catch>
		<c:if test="not empty $xslError">
			<!-- $xslError -->
		</c:if>	

		<%--	
		<textarea style="border: 1px solid grey; background-color: #dddddd; padding-left: 20px; width: 900px; height: 150px;"><vocabs:getResponseOPML/></textarea>
		--%>
