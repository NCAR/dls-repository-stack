<%-- <div style="margin-left:25px">
	<span style="width:200px;display:block;text-align:center;margin:25px 0px 0px 0px;padding:5px;border:purple 2px outset;background-color:#cbbecc;font-size:14pt;font-weight:bold;margin-top:5px;">
		test_widget! 
		<input type="button" value="click me" onclick="alert ('i am an simple input helper')" />
	</span>
	<div style="font-size:10px;margin-top:3px">xpath: ${sf:idToPath (id)}</div>
	<div style="font-size:10px;margin-top:3px">inputHelper: text/widget.html</div>
	
</div> --%>

<%-- <div><span style="border:thin blue solid;background-color:#ffffcc;padding:1px;">Standards Input Helper</span>
			<div style="font-size:10px;margin-top:3px">id: ${id}</div>
			<div style="font-size:10px;margin-top:3px">xpath: ${sf:idToPath (id)}</div>
</div> --%>


<h4>I am a standard-widget</h4>

<div id="${id}_label" style="padding:0px 2px 0px 2px;border:2px solid black;display:none"></div>

<script type="text/javascript">
	Event.observe (window, 'load', function (event) {
		var helper = new StandardsHelper ('${id}', '${sf:idToPath (id)}');
		STANDARDS_HELPERS.addMember (helper);
		if (helper.id_type == 'ASN') {
			if (isBrowserOldIE()) {
				$('IE-msg').show();
			}
			else {
				// $("asn-select-widget").show()
			}
		}
		else {
			if (helper.type_select) {
				removeASNOption(helper.type_select);
			}
		}
	});
</script>
