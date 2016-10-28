<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %><%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %><%@ taglib uri="/WEB-INF/tlds/response.tld" prefix="resp" %><%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %><logic:present name="queryForm" property="metadata"><logic:messagesNotPresent><resp:setContentType>text/xml; charset=UTF-8</resp:setContentType><logic:match parameter="rt" value="validate"><resp:setContentType>text/html; charset=UTF-8</resp:setContentType></logic:match><bean:write name="queryForm" property="metadata" filter="false" />
</logic:messagesNotPresent>
</logic:present>
<logic:messagesPresent> 
<html>
<head>
<title>Data not available</title>
</head>
<body>  
  <br/>

  	
	<table width="90%" bgcolor="#000000" cellspacing="1" cellpadding="8">
	  <tr bgcolor="ffffff"> 
		<td>
			<ul>
				<html:messages id="msg" property="message"> 
					<li><bean:write name="msg"/></li>									
				</html:messages>
				<html:messages id="msg" property="error"> 
					<li><font color=red>Error: <bean:write name="msg"/></font></li>									
				</html:messages>
			</ul>
		</td>
		</tr>
	</table>		
  
  
</body>
</html>
</logic:messagesPresent>



          

