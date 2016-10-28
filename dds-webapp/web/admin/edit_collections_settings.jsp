<%-- <jsp:useBean id="raf" class="edu.ucar.dls.repository.action.form.RepositoryAdminForm" scope="request"/> --%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/response.tld" prefix="resp" %>
<%@ taglib uri="/WEB-INF/tlds/request.tld" prefix="req" %>
<%@ taglib uri="/WEB-INF/tlds/datetime.tld" prefix="dt" %>



<%-- Create a scripting variable named "index" from the parameter "currentIndex" --%>
<bean:parameter name="currentIndex" id="index" value="0"/>

<html:html>

<head>
<title>Edit collection manager settings</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%-- <link rel='stylesheet' type='text/css' href='/dlese_shared/dlese_styles.css'> --%>
<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>

<%-- <script language="JavaScript" src="../oai_script.js"></script> --%>
<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}	
</script>

</head>

<body text="#000000" bgcolor="#ffffff" onLoad=sf()>
<table width="93%" border="0" align="center">
  <tr> 
    <td> 

	<html:form action="/admin/admin" method="GET">
	<logic:present parameter="add">
		<h3>Add OAI provider setting</h3>
	</logic:present>
	<logic:notPresent parameter="add">
		<h3>Edit collections setting</h3>
	</logic:notPresent>

		<logic:messagesPresent> 		
		<table width="70%" bgcolor="#000000" cellspacing="1" cellpadding="8">
		  <tr bgcolor="ffffff"> 
			<td>
			<font color="red">
			<html:messages id="msg"> 
				<bean:write name="msg"/>										
			</html:messages>
			Please fix the error and click "Save," or choose "Cancel."</font>
			</td>
		  </tr>
		</table>		
		<br><br>
		</logic:messagesPresent>
	
		<logic:present name="raf" property="xmlError"> 
			<table width="70%" bgcolor="#000000" cellspacing="1" cellpadding="8">
			  <tr bgcolor="ffffff"> 
				<td>
					The description field must consist of valid, well-formed XML that contains the URL of an XML 
					schema by which it is validated. 					
					A validation check of the XML you entered revealed the following:<br><br>
					<font color="red"><bean:write name="raf" property="xmlError" filter="false" /></font>
					<br><br>Please fix the error and click "Save," or choose "Cancel."								
				</td>
			  </tr>
			</table>
			<br><br>
		</logic:present> 		


		
	


		<%-- ######## Collection ######## --%>
		<logic:present parameter="currentSetName">
		<logic:notPresent parameter="currentSetDirectory">
		<table bgcolor="#666666" cellpadding=10 cellspacing="1" border="0">					
			<script>function sf(){document.raf.currentSetName.focus();}</script>
			<logic:present parameter="add">
				<input type="hidden" name="add" value="t">
			</logic:present>
			<input type="hidden" name="currentIndex" value="<req:parameter name="currentIndex"/>">
			<tr bgcolor="#ddeeee">						
				<td align=right nowrap><b>Collection name</b>:*</td>
				<td><html:text property="currentSetName" size="50"/></td>
			</tr>
			<tr bgcolor="#ddeeee">
				<td align=right nowrap><b>Collection key</b>:*</td>
				<td>
								
					<html:messages id="msg" property="currentSetSpec"> 
						<font color="red">*<bean:write name="msg"/></font><br><br>										
					</html:messages>
					<html:select property="currentSetSpec" size="1">
						<html:options  name="raf" property="validCollectionKeys"/>
					</html:select>						
					<%-- <html:text property="currentSetSpec" size="50"/> --%>
				</td>
			</tr>
			<%-- <tr bgcolor="#ddeeee">
				<td align=right nowrap><b>Collection-level record</b>:<br>(XML only)</td>
				<td><html:textarea property="currentSetDescription" cols="75" rows="20"/></td>
			</tr> --%>
		</table>
		<table>
			<tr bgcolor="#ffffff">						
				<td colspan=2><b>* required fields</b><br></td>
			</tr>
		</table>
		<table>
			<tr bgcolor="#ffffff">						
				<td colspan=2>
					<br>Notes on these fields:<br>
					<i>Collection name</i> - A descriptive name for this collection meant for human consumption,
					for example &quot;DLESE Community Collection.&quot;<br>
					<i>Collection key</i> - A unique key that is used by the index to reference this collection, 
					for example "dcc." Choose a value from the list provided.<br>
					<%-- <i>Collection-level record</i> - An adn-collection XML record for this collection. --%>
					<br>
				</td>
			</tr>
		</table>
		</logic:notPresent>
		</logic:present>
		
		<%-- ######## Set data dirs and formats ######## --%>
		<logic:present parameter="currentSetDirectory">
		<table bgcolor="#666666" cellpadding=10 cellspacing="1" border="0">
			<%-- <script>function sf(){document.raf.currentSetFormat.focus();}</script> --%>
			<input type="hidden" name="currentIndex" value="<req:parameter name="currentIndex"/>">
			<input type="hidden" name="currentDirInfoIndex" value="<req:parameter name="currentDirInfoIndex"/>">					
			<input type="hidden" name="currentSetName" value="<req:parameter name="currentSetName"/>">
			<logic:present parameter="add">
				<input type="hidden" name="add" value="t">
			</logic:present>
			<tr bgcolor="#ddeeee">										
				<td colspan="2"><b><req:parameter name="currentSetName"/></b></td>
			</tr>					
			<tr bgcolor="#ddeeee">
				<td align=right nowrap><b>Format of files</b>:*<br>(metadataPrefix)</td>
				<td>
					<html:messages id="msg" property="currentSetFormat"> 
						<font color="red">*<bean:write name="msg"/></font><br><br>										
					</html:messages>					
					<html:select property="currentSetFormat" size="1">
						<html:options  name="raf" property="validMetadataFormats"/>
					</html:select>					
					<%-- <html:text property="currentSetFormat" size="50"/> --%>
				</td>
			</tr>
			<tr bgcolor="#ddeeee">						
				<td align=right nowrap><b>Directory of files</b>:*</td>
				<td>
					<html:messages id="msg" property="setDirectory"> 
						<font color="red">*<bean:write name="msg"/></font><br><br>										
					</html:messages>				
					<html:text property="currentSetDirectory" size="85"/>
					<logic:notEmpty name="raf" property="collectionsBaseDir">
						<br>The base directory for collections files is <bean:write name="raf" property="collectionsBaseDir"/>
						<br>
						Choose a sub-directory, for example <bean:write name="raf" property="collectionsBaseDir"/>/adn/dcc
					</logic:notEmpty>
				</td>
			</tr>
		</table>
		<table>
			<tr bgcolor="#ffffff">						
				<td colspan=2><b>* required fields</b><br></td>
			</tr>
		</table>
		<table>
			<tr bgcolor="#ffffff">						
				<td colspan=2>
					<br>Notes on these fields:<br>
					<i>Format of files</i> - A key that referrs to the format of the 
					metadata files, for example &quot;adn&quot; or &quot;dlese_ims.&quot;
					Choose a value from the list.<br>
					<i>Directory of files</i> - The directory on the server that contains the
					metadata files in XML format. 
					<logic:notEmpty name="raf" property="collectionsBaseDir">
						<br>Note, the base directory for collections files is <bean:write name="raf" property="collectionsBaseDir"/>
						<br>
						Choose a sub-directory, for example <bean:write name="raf" property="collectionsBaseDir"/>/adn/dcc
					</logic:notEmpty>					
					<br>
				</td>
			</tr>
		</table>
		</logic:present>				
		
		<%-- Footer --%>
		<br>
		<table>		
			<%-- <tr bgcolor="#ffffff">
				<td colspan=2>
				<font size="-1">&nbsp;[ <a href="JavaScript:swin('../documentation/index.jsp#fields',600,500)">See details about this field</a> ]</font>
				</td>
			</tr>  --%>		
			<tr bgcolor="#ffffff">
				<td colspan=2>
				<html:submit value="Save"/>
				<html:button property="cancel" value="Cancel" onclick="window.location='admin.do'"/>
				</td>
			</tr>
		</table>
					
	</html:form>		
   
    </td>
  </tr>
</table>


</body>

</html:html>
