
<script type="text/javascript" >

// canned links for DLESE as collection in production NDR
var dleseCollectionLinks = [

	{
		"label":"DLESE Collection Metadata",
		"handle":"2200/20061002124900276T",
		"repo":"server7"
	},
	
		{
		"label":"DLESE Collection MetadataProvider",
		"handle":"2200/20061002124900610T",
		"repo":"server7"
	},

	{
		"label":"DLESE Collection Resource",
		"handle":"2200/20061002124858953T",
		"repo":"server7"
	},
	{
		"label":"DLESE Collection Aggregator",
		"handle":"2200/20061002124859565T",
		"repo":"server7"
	},

	{
		"label":"DLESE Collection Agent (dlese.org Agent)",
		"handle":"2200/20061002124859263T",
		"repo":"server7"
	}
]

var VisionLearningLinks = [
	{
		"label":"VisionLearning Collection Metadata",
		"handle":"2200/20061002125557400T",
		"repo":"server7"
	},
	
		{
		"label":"VisionLearning Collection MetadataProvider",
		"handle":"2200/20061002125557727T",
		"repo":"server7"
	},

	{
		"label":"VisionLearning Collection Resource",
		"handle":"2200/20061002125556082T",
		"repo":"server7"
	},
	{
		"label":"VisionLearning Collection Aggregator",
		"handle":"2200/20061002125556695T",
		"repo":"server7"
	},

	{
		"label":"VisionLearning Collection Agent (visionlearning.com)",
		"handle":"2200/20061002125556377T",
		"repo":"server7"
	}
]

var mathDLLinks = [
	{
		"label":"MathDL Collection Metadata",
		"handle":"2200/20061002125007209T",
		"repo":"production"
	},
	
		{
		"label":"MathDL Collection MetadataProvider",
		"handle":"2200/20061002125007661T",
		"repo":"production"
	},

	{
		"label":"MathDL Collection Resource",
		"handle":"2200/20061002125005762T",
		"repo":"production"
	},
	{
		"label":"MathDL Collection Aggregator",
		"handle":"2200/20061002125006440T",
		"repo":"production"
	},

	{
		"label":"MathDL Collection Agent (www.mathdl.org)",
		"handle":"2200/20061002125006088T",
		"repo":"production"
	}
]

// canned links for test ndr
var ndrTestLinks = [
	{
		"label":"Collection Metadata Provider",
		"handle":"2200/test.20070726160248653T"
	},
	{
		"label":"Collection Resource",
		"handle":"2200/test.20070726184936697T"
	},
	{
		"label":"Collection Aggregator",
		"handle":"2200/test.20070726190044390T"
	},
	{
		"label":"Collection Metadata",
		"handle":"2200/test.20070726185520835T"
	}

]

function cannedExplorerLinksInit() {
		$('canned-explorer-links').hide();
		$('el-header').observe ("click", function (evnt) { $("canned-explorer-links").toggle() });
		showLinkSet (dleseCollectionLinks, 'left-canned-links');
		showLinkSet (VisionLearningLinks, 'right-canned-links');
}

ndrRepositories = {
	"production" : "http://ndr.nsdl.org/api",
	"server7" : "http://server7.nsdl.org/api",
	"test" : "http://ndrtest.nsdl.org/api"
}

function showLinkSet (linkSet, target) {

	// debug ("there are " + linkSet.length + " explorer links");
	
	var target = $(target);	
	
	$A(linkSet).each (function (el) {
		el = $H(el);
		var elink = $(document.createElement ("a"));
		var id = "el"+el.handle;
		elink.setAttribute ("href", "#");
		elink.setAttribute ("id", id);
		elink.update (el.label);
		elink.observe ('click', function (evnt) {
					
				Event.stop (evnt);
				$('repository-select').value = ndrRepositories[el.repo];
				// 	(el.repo == "production" ? "http://ndr.nsdl.org/api" : "http://ndrtest.nsdl.org/api");
				$('explorerHandle').value = el.handle;
				setNdrApiBaseUrl();
				doNDRCall ("get", el.handle);
		});

		var div = $(document.createElement ("div"));
		div.setStyle ({marginLeft:"20px",marginTop:"2px"});
		div.appendChild (elink);
		target.appendChild (div);
	});
}

Event.observe (window, 'load', cannedExplorerLinksInit);
</script>

<div style="margin: 5px 0px 10px 0px">
<h4><a href="#" id="el-header">Canned Explorer Links</a></h4> 
<table width="100%" id="canned-explorer-links" cellpadding="5px" cellspacing="1px" bgcolor="#333366">
<tr valign="top">
	<td width="50%" bgcolor="#e6e6ed">
		<b>DLESE Collection Objects</b>
		<div id="left-canned-links"></div>
	</td>
	<td width="50%" bgcolor="#e6e6ed">
		<b>Right Canned Links</b>
		<div id="right-canned-links"></div>
	</td>
</tr>
</table>
</div>
