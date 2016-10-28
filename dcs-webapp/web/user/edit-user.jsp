<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.schemedit.security.access.ActionPath" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">User Information</c:set>
<c:set var="ldapLoginEnabled" 
			 value="${sf:listContains (loginModules,'edu.ucar.dls.schemedit.security.login.LdapLogin')}" />

<c:set var="ucasLoginEnabled" 
			 value="${sf:listContains (loginModules,'edu.ucar.dls.schemedit.security.login.UcasLogin')}" />
<html:html locale="true">
<head>
<title><st:pageTitle title="${title}" /></title>
<%@ include file="/baseHTMLIncludes.jsp" %>
<link rel='stylesheet' type='text/css' href='${contextPath}/lib/access-styles.css'>
<link rel='stylesheet' type='text/css' href='${contextPath}/user/edit-user-styles.css'>

<script type="text/javascript">

var togglingHeaders = {};

var togglingHeaderData = {
	<c:if test="${userForm.passwordOptional}">
		'local-password' : {
			'label' : 'Optional NCS-only password',
			'target' : function () { return $$('.password-input') },
			'prompt' : 'This password is valid only within this NCS'
		},
	</c:if>
	<c:if test="${ldapLoginEnabled}">
		'ldap-directory-search' : {
			'label' : 'Search Ldap Directory',
			'target' : 'ldap-search',
			'prompt' : 'Search the Ldap directory by username or fullname'
		},
	</c:if>
	<c:if test="${ucasLoginEnabled}">
		'ucas-directory-search' : {
			'label' : 'Search UCAR Directory',
			'target' : 'ucas-search',
			'prompt' : 'Search the UCAR directory by firstname and/or lastname'
		},
	</c:if>
	'registered-users-header' : {
		'label' : 'Registered Users',
		'target' : 'registered-users',
		'prompt' : 'See a list of users currently registered for this system'
	}		
}

var TogglingHeader = Class.create ({
	initialize: function (id) {
		data = togglingHeaderData[id]
		if (!data) throw ("data not found for " + id);
		if (!$(id)) throw ("element not found for " + id);
		this.id = id;
		this.label = data['label']
		this.prompt = data['prompt']
		this.target = this._getTarget(data['target']);
		this.widget;
		this.element = $(id);
		this.visible = false;
		this.render ();
	},
	
	_getTarget : function (targetData) {
		if (typeof (targetData) == 'function') {
			return targetData();
		}
		else {
			return $A([$(targetData)]);
		}
	},
	
	setVisibility : function (visible) {
		fn = (visible ? 'show' : 'hide');
		this.target.invoke (fn);
		this.widget.src = (visible ? "../images/opened.gif" : "../images/closed.gif");
		this.visible = visible;
	},
	
	render : function () {
		var widget = $(new Image ());
		widget.writeAttribute ('title', this.prompt);
		widget.observe ('click', this.toggle.bindAsEventListener(this));
		widget.observe ('mouseover', function (evnt) {
			widget.setStyle ({cursor:'pointer'});
		});
		var link = new Element ('a').update (this.label);
		link.writeAttribute ("href", "#");
		link.writeAttribute ('title', this.prompt);
		link.observe ("click", this.toggle.bind(this));
		this.element.update (widget);
		this.element.insert (link);
		this.widget = widget;
		this.setVisibility (this.visible);
		
		// format targets
		this.target.each (function (t) {
			if (this.id == 'local-password')
				t.setStyle ({backgroundColor:'#E3E4F1'});
			else
				t.setStyle ({marginLeft:'17px'});
		}.bind(this));
	},
	
	toggle : function () {
		this.setVisibility (!this.visible);
	}
});
		
/* highligh all occurances of sub in text - used in directory search results*/
function highlight (text, sub) {
	if (!sub)	return text;
	return text.gsub (new RegExp(sub,'i'), function (match) {
		return '<span style="font-weight:bold;color:#000099">' + match[0] + '</span>';
	});
}

function pageInit () {
	// initialize toggling headers
	$$('.toggling-header').each ( function (th) {
		var id = th.identify();
		try {
			togglingHeaders[id] = new TogglingHeader (id);
		} catch (error) {
			alert ("could not instantiate header for " + id + "\n(" + error + ")");
		}
	});
			
	// expose registered users if a dupUserName was attempted
	if (${not empty userForm.dupUsername}) {
		try {
			togglingHeaders['registered-users-header'].setVisibility(true);
		} catch (error) {
			alert ("couldnt expose registered users: " + error);
		}
	}
	
	// activates select to assign admin role. (only present when sessionUser has admin role)
	if ($('userIsAdmin')) {
		$('userIsAdmin').observe ('change', function (evnt) {
				var obj = Event.element(evnt);
				var selectedIndex = obj.selectedIndex
				obj.selectedIndex = selectedIndex == 1 ? 0 : 1;
				var msg = obj.options[selectedIndex].value == "false" ?
									"Are you sure you want to remove admin role? \nThis will strip this user of all collection access." :
									"Are you sure you want to grant admin role? \nAdmin users have great powers in this system.";
				if (confirm (msg))
					obj.selectedIndex = selectedIndex;
			});
		}
		
		if (${userForm.newUser}) {
			// defeat pesky auto-completion
			if (${userForm.fileLoginEnabled}) {
				$('pass').setValue("${userForm.pass}");
				$('pass2').setValue("${userForm.pass2}");
			}
			$('lastname').setValue("${userForm.lastname}");
		}
}

Event.observe (window, 'load', pageInit, false);

</script>
</head>
<body>
<st:pageHeader currentTool="" toolLabel="${title}" />

<st:pageMessages />

<%-- <div style="padding:5px;background-color:#ece7e7;border:thin purple solid">
	<h5>active login modules</h5>
	<c:forEach var="module" items="${loginModules}">
		<div style="margin-left:20px">${module}</div>
	</c:forEach>
	
	<br/>
	<c:if test="${userForm.passwordRequired}">
		<div>Password is <b>REQUIRED</b></div>
		<div style="margin-left:20px">password: "${userForm.pass}"</div>
	</c:if>	
	<c:if test="${userForm.passwordOptional}">
		<div>Password is <b>Optional</b></div>
		<div style="margin-left:20px">password: "${userForm.pass}"</div>
	</c:if>	
	<c:if test="${not userForm.fileLoginEnabled}">
		<div>Password is <b>Disabled</b></div>
	</c:if>	
	<br/>
	<c:if test="${not empty userForm.dupUsername}">
		<div>DUP User: "${userForm.dupUsername}"</div>
	</c:if>
	
</div> --%>

<c:if test="${userForm.newUser || (not empty userForm.username)}">
	<html:form action="/user/userInfo" styleId="userInfo_form">
	<html:hidden property="referer" />
	<c:if test="${userForm.newUser}">
	<div class="table-banner"><div class="title">New User</div></div>
		<html:hidden property="newUser" />
	</c:if>
	<c:if test="${not userForm.newUser}">
		<div class="table-banner"><div class="title">${userForm.fullname}</div></div>
	</c:if>

	<div style="margin-left:10px;">
		<table width="100%" border="0" cellpadding="5" cellspacing="1" bgcolor="#ffffff">
			<tr valign="top">
				<td width="40%">
					<table cellpadding="5">
			
						<c:choose>
							<c:when test="${userForm.newUser}">
								<tr bgcolor="#ffffff">
									<td class="input-label">username</td>
									<td class="input">
										<html:text property="username"  styleId="username"/>
									</td>
								</tr>
							</c:when>
							
							<c:otherwise>
								<html:hidden property="username" />
								<tr bgcolor="#ffffff">
									<td class="input-label">username</td>
									<td><b>${userForm.username}</b></td>
								</tr>
							</c:otherwise>
						</c:choose>
					
						<c:if test="${userForm.fileLoginEnabled}">
						
							<c:if test="${userForm.passwordOptional}">
								<tr>
									<td colspan="2" align="center">
										<div id="local-password" class="toggling-header"></div>
									</td>
								</tr>
							</c:if>
							
							<tr bgcolor="#ffffff" class="password-input">
								<td class="input-label">password</td>
								<td class="input">
									<html:password property="pass" styleId="pass"/>
								</td>
							</tr>
				
							<tr bgcolor="#ffffff" class="password-input">
								<td class="input-label">confirm password</td>
								<td class="input">
									<html:password property="pass2" styleId="pass2"/>
								</td>
							</tr>			
						</c:if>
						
						<tr bgcolor="#ffffff">
							<td class="input-label">first name</td>
							<td class="input"><html:text property="firstname" styleId="firstname"/></td>
						</tr>
						
						<tr bgcolor="#ffffff">
							<td class="input-label">last name</td>
							<td class="input">
								<html:text styleId="lastname" property="lastname" />
								<%-- <input type="text" id="lastname" name="lastname" value="${userForm.lastname}" /> --%>
							</td>
						</tr>
						

						
						<tr bgcolor="#ffffff">
							<td class="input-label">institution</td>
							<td class="input"><html:text property="institution"/></td>
						</tr>
						
						<tr bgcolor="#ffffff">
							<td class="input-label">department</td>
							<td class="input"><html:text property="department"/></td>
						</tr>
						
						<tr bgcolor="#ffffff">
							<td class="input-label">email</td>
							<td class="input"><html:text property="email"/></td>
						</tr>	
						
						<c:if test="${sf:hasRole (sessionScope['user'], 'admin')}">
							<tr bgcolor="#ffffff">
								<td class="input-label">assign admin role</td>
								<td class="input">
<%-- 									<input type="checkbox" name="userIsAdmin"
										<c:if test="${userForm.userHasAdminRole}">CHECKED</c:if> /> --%>
									<select name="userIsAdmin" id="userIsAdmin">
										<option value="false" 
											<c:if test="${not userForm.userHasAdminRole}">SELECTED</c:if> >no</option>
										<option value="true" 
											<c:if test="${userForm.userHasAdminRole}">SELECTED</c:if> >yes</option>
									</select>
								</td>
							</tr>
						</c:if>			
						
						<tr bgcolor="#ffffff">
							<td colspan="2" align="center">
								<input type="hidden" name="command" value="save" />
								<input type="button" value="save" id="save-user" onclick="this.form.submit()" />
								<input type="button" value="exit"
									onclick="location='${userForm.referer}'"/>
								 	<c:if test="${not userForm.newUser && 
																not userForm.userHasAdminRole &&
																sf:hasRole (sessionScope['user'], 'manager')}" >
										<input type="button" value="Assign collection access"
										 onclick="window.location='${contextPath}/manage/userManager.do?username=${userForm.username}&command=edit'"/>
									</c:if>
							</td>
						</tr>
					</table>
				</td>
				
				
				<td width="60%" align="left">
				
					<%-- New User Aids --%>
					<c:if test="${userForm.newUser}">
					
							<%-- Registered Users --%>
							<%@ include file="registeredUsers.jspf" %>
						
							<c:if test="${ldapLoginEnabled or ucasLoginEnabled}">
								<%-- javascript support for directory search --%>
								<script type="text/javascript" src="directory-search.js"></script>
							</c:if>
										 
							<%-- LDAP search --%>
							<c:if test="${ldapLoginEnabled}">
								<%@ include file="ldapSearch.jspf" %>
							</c:if>
								
							<%-- LDAP search --%>
							<c:if test="${ucasLoginEnabled}">
								<%@ include file="ucasSearch.jspf" %>
							</c:if>
					
					</c:if>
				</td>
			</tr>
		</table>
	</div>

	</html:form>
	
</c:if>


</body>
</html:html>
