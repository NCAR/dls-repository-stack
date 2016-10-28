/*
	NDRGlobals - javascript namespace providing global values required to communicate with NDR
*/

var NDRGlobals = {

	ndrApiBaseUrl : "${ndrForm.ndrApiBaseUrl}",
	
	contentProxyBaseUrl : "${ndrPrimaryContentProxy}",
	
	baseUrl : "${contextPath}",
	
	explorerLink : function (handle) {
		return "ndr.do?command=explore&handle=" + handle;
	},
	
	/* getHandle grabs a ndr handle from an href (or any string). returns null if
		 a handle is not found. */
	getHandle: function (href) {

		var re = this.ndrApiBaseUrl.startsWith ("http://ndrtest") ? 
						/2200\/test.[0-9]+T/ : // ndrTest server
						/2200\/[0-9]+T/; // ndrProd
		
		// the second re below matches handles for the local dls ndr
		return href.match (re) || href.match (/ndr:[0-9]+/);
	},
	
	isHandle: function (str) {
		return (this.getHandle (str) == str);
	},
	
	getLink: function (href, text, attrs) {
		var a = new Element ("a", {href:href})
		a.update (text != null ? text : href);
		if (target) a.setAttribute ("target", target);
		return a;
	},
	
	popup : function (href) {
		var features = "innerHeight=700,height=700,innerWidth=650,width=650,resizable=yes,scrollbars=yes";
		features += ",locationbar=yes,menubar=yes,location=yes,toolbar=yes";
		resourcewin = window.open (href, "popup", features);
		resourcewin.focus();
		return false;
	},
	
	spinner : "<img src='${contextPath}/images/fedora-spinner.gif' height='25px' border='0' />"
	
}

