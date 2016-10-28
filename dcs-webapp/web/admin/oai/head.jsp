<%@ include file="../../JSTLTagLibIncludes.jsp" %>

<script language="JavaScript"> 

var Menu1 = new Array ()
var subMenu1 = new Array ()

	Menu1[0] = new Array("Overview", '<c:url value="/"/>',"_top", "left")
	 subMenu1[0] = new Array()
	 
	Menu1[1] = new Array ("Search", '<c:url value="/search.jsp"/>',"_top", "left")
	 subMenu1[1] = new Array()
	
	Menu1[2] = new Array ("Explore", '<c:url value="/oaisearch.do"/>',"_top", "left")
	 subMenu1[2] = new Array()
	 

	Menu1[3] = new Array("Provider", '<c:url value="/admin/provider_status_setup.jsp"/>',"_top", "left")
	 subMenu1[3] = new Array()
	 subMenu1[3][0] = new Array ("Setup and Status", '<c:url value="/admin/provider_status_setup.jsp"/>',"_top")
	 subMenu1[3][1] = new Array ("Repository Information and Administration", '<c:url value="/admin/data-provider-info.do"/>',"_top")	 
	 subMenu1[3][2] = new Array ("Metadata Files Configuration", '<c:url value="/admin/data-provider.do"/>',"_top")
	 subMenu1[3][3] = new Array ("Sets Configuration", '<c:url value="/admin/data-provider/sets.do"/>',"_top")
	 subMenu1[3][4] = new Array ("Admin search", '<c:url value="/admin/query.do"/>',"_top")	 
	 //subMenu1[3][5] = new Array ("ODL Search Explorer", '<c:url value="/oaisearch.do?show=odl"/>',"_top")
	 //subMenu1[3][6] = new Array ("ODL Search Client", '<c:url value="/odl_search_client.jsp"/>',"_top")

     
   Menu1[4] = new Array("Harvester", '<c:url value="/admin/harvester.do"/>',"_top", "left")
     subMenu1[4] = new Array()
     subMenu1[4][0] = new Array ("Setup and Status", '<c:url value="/admin/harvester.do"/>',"_top")
     subMenu1[4][1] = new Array ("View Harvest History", '<c:url value="/admin/harvestreport.do"/>?q=doctype:0harvestlog+AND+!repositoryname:%22One+time+harvest%22&s=0&report=View+Harvest+History', "_top")
    
  
   Menu1[5] = new Array("Documentation", '<c:url value="/docs/"/>',"_top", "left")
     subMenu1[5] = new Array()
     subMenu1[5][0] = new Array ("Provider Documentation", '<c:url value="/docs/provider.jsp"/>',"_top")
     subMenu1[5][1] = new Array ("Harvester Documentation", '<c:url value="/docs/harvester.jsp"/>',"_top")
     subMenu1[5][2] = new Array ("ODL Search Specification", '<c:url value="/docs/odlsearch.do"/>',"_top")
     subMenu1[5][3] = new Array ("Obtaining & Installing jOAI", '<c:url value="/docs/obtaining_installing_joai.jsp"/>',"_top")
     subMenu1[5][4] = new Array ("About jOAI", '<c:url value="/docs/about.jsp"/>',"_top")
     
     indicator = "<img src='${pageContext.request.contextPath}/images/tridown.gif' border='0'>" // Symbol to show if a sub menu is present (subIndicate must be to set to 1)
                                                    // Use standard HTML <img> tag. You can use a character instead of an image. 
                                                    // e.g.      indicator = ">"
</script>
<script type="text/javascript" src='${pageContext.request.contextPath}/menu.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/oai_script.js'></script>

<link rel="stylesheet" type="text/css" href='${pageContext.request.contextPath}/oai_styles.css'>

