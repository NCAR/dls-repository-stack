<%@ include file="/lib/includes.jspf" %>

<%-- We need to pass "elementPath" attribute to vocabLayoutMultiBox - this should come from sef --%>

<%-- <h4>Hello world from async-vocab-layout.jspf</h4>
<div>pathArg: ${sef.pathArg}</div>
<div>elementPath: enumerationValuesOf(${sef.pathArg})</div> --%>

<vl:vocabLayoutMultiBox elementPath="enumerationValuesOf(${sef.pathArg})" />
			



