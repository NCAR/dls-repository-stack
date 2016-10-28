<%@ include file="/lib/includes.jspf" %>

<%-- <h4>Hello world from async-single-vocab-layout.jsp</h4>
<div>pathArg: ${sef.pathArg}</div>
<div>elementPath: enumerationValuesOf(${sef.pathArg})</div> --%>

<vl:vocabLayoutSingleSelect elementPath="valueOf(${sef.pathArg})" />
			



