<%--  comboInput tag
 
 - Renders an input element that allows user to make a choice from a supplied set of values, or alternatively
 type in a value.
   
 - Supporting javascript can be found in /lib/combo-input-support.js
--%> 

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<table width="100%" cellpadding="0" cellspacing="0">
	<tr valign="middle">
		<td align="left" id="table-filter-widget-cell" style="white-space:nowrap;display:none" width="220px">
			<%@ include file="/lib/dcstable/tableFilterWidget.jspf" %>
		</td>
		<td><jsp:doBody/></td>
	</tr>
</table>

<script type="text/javascript">
if (!isBrowserOldIE()) {
		log ("capable browser");
		$("table-filter-widget-cell").show();
	}
</script>

