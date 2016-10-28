var manageSubMenuData = { // width:150, 
	items: [
	{ 
		label : "Manage Collections", 
		id : "manage_collections", 
		href : "/manage/collections.do"
	},
	<c:if test="${authenticationEnabled}">
	{ 
		label : "Manage Users", 
		id : "manage_users", 
		href : "/manage/userManager.do"
	},
	{ 
		label : "Assign Collection Access", 
		id : "assign_collection_access", 
		href : "/manage/collectionAccessManager.do"
	},
	</c:if>
	{ 
		label : "Sessions", 
		id : "sessions", 
		href : "/manage/sessions.do"
	},
	{ 
		label : "Export Reports", 
		id : "export_reports", 
		href : "/manage/collections.do?command=export&report=true"
	} <c:if test="${not empty suggestionServiceManager}">,
	{ 
		label : "Standards", 
		id : "standards", 
		href : "/asn/asn.do?command=manage"
	}</c:if>
	
	<c:if test="${not empty nldrProperties}">,
	{
		label : "NLDR", 
		id : "nldr", 
		href : "/manage/nldr.do"
	}
	</c:if>	
	
]};

var servicesSubMenuData = { // width:150, 
	items: [
	{ 
		label : "OAI Services ", 
		id : "oai_services", 
		href : "/admin/data-provider-info.do"
	},
	{ 
		label : "Web Services", 
		id : "web_services", 
		href : "/admin/admin.do?page=services"
	},
	{ 
		label : "Explorer", 
		id : "explorer", 
		href : "/admin/services/service_explorer.jsp"
	}
]};

var settingsSubMenuData = { // width:250, 
	items: [
	{ 
		label : "Collection Settings", 
		id : "collection_settings", 
		href : "/admin/admin.do?page=collections"
	},
	{ 
		label : "Metadata Frameworks", 
		id : "metadata_frameworks", 
		href : "/admin/frameworks.do"
	},
	{ 
		label : "Indexing", 
		id : "index_settings", 
		href : "/admin/admin.do?page=index"
	},
	{ 
		label : "Index Field List", 
		id : "index_fields", 
		href : "/admin/reporting/ListFields.jsp?verb=ListFields"
	},
<%-- 	<c:if test="${authenticationEnabled}">

	{ 
		label : "Security Settings", 
		id : "security", 
		href : "/admin/roles/accessManager.do"
	}
	,
	</c:if> --%>
	{ 
		label : "App Configuration", 
		id : "config", 
		href : "/admin/admin.do?page=config"
	},
	{ 
		label : "Schema Viewer", 
		id : "schema_viewer", 
		href : "${contextUrl}/schema/schema.do",
		popup : "true"
	}
]};

var helpSubMenuData = { // width:250, 
	items: [
	{ 
		label : "About", 
		id : "about", 
		href : "/about.jsp",
		popup : "true"
	},
	
	<c:if test="${not empty initParam.instanceHelp}">
	{
		label : "${not empty initParam.instanceHelpLabel ? initParam.instanceHelpLabel : 'Instance Help'}",
		// label : "Instance Help"
		id : "instance_help",
		href : "${initParam.instanceHelp}",
		popup : "true"
	},
	</c:if>
	
	{ 
		label : "Installation", 
		id : "installation", 
		href : "/docs/INSTALL_INSTRUCTIONS.txt",
		popup : "true"
	},

	{
		label : "User Guide",
		id : "manual",
		href : "/docs/dcs-users-guide.pdf",
		popup : "true"
	}
	
	<%-- ,
	{ 
		label : "FAQs", 
		id : "faq", 
		href : "#"
	},

	{ 
		label : "Configuring", 
		id : "configuring", 
		href : "#"
	} --%>
]};

var ndrSubMenuData = { // width:250, 
	items: [
<c:choose>
<c:when test="${ndrServiceActive}">
	{ 
		label : "Admin", 
		id : "ndr_collections", 
		href : "/ndr/ndr.do?command=manage"
	},
	{ 
		label : "Explorer", 
		id : "ndr_explorer", 
		href : "/ndr/ndr.do?command=explore"
	},
	{ 
		label : "Browser", 
		id : "ndr_browser", 
		href : "/ndr/ndr.do?command=browse"
	}
	/*
	,
	{ 
		label : "Mappings", 
		id : "ndr_mappings", 
		href : "/ndr/ci.do"
	},

	{ 
		label : "Finder", 
		id : "ndr_finder", 
		href : "/ndr/ndr.do?command=find"
	}
*/
</c:when>
<%-- <c:when test="${ndrDeactivated}">
	{ 
		label : "Finder", 
		id : "ndr_finder", 
		href : "/ndr/ndr.do?command=find"
	} --%>
</c:choose>
]};

// width seems to be required (but it can be 0) or IE can't locate submenus
// height is used to calculate the top position of the submenu!
var pageMenuData = {  height:20, // width:0,
	items: [
	{ 
		label : "Home",	
		id : "home", 
		href : "/browse/home.do" 
	},
	{ 
		label : "Search", 
		id : "search", 
		href : "/browse/query.do" 
	},
	{ 
		label : "Manage", 
		id : "manage", 
		role : "manager", 
		href : "/manage/collections.do", 
		subMenu : manageSubMenuData
	},
	
<c:if test="${ndrServiceEnabled}">
	{ 
		label : "NDR", 
		id : "ndr", 
		role : "manager", 
		href : "/ndr/ndr.do?command=manage",
		subMenu : ndrSubMenuData
	},
</c:if>
	
	{ 
		label : "Settings", 
		id : "settings", 
		role : "admin", 
		href : "/admin/admin.do?page=collections",
		subMenu : settingsSubMenuData
	},
	{ 
		label : "Services", 
		id : "services", 
		role : "admin", 
		href : "/admin/data-provider-info.do",
		subMenu : servicesSubMenuData
	},
	{ 
		label : "Help", 
		id : "about", 
		href : "/about.jsp",
		popup : "true",
		subMenu : helpSubMenuData
	}
]};
