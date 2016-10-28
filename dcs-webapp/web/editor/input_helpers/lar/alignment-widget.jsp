<%-- <div style="margin-left:25px">
	<span style="width:200px;display:block;text-align:center;margin:25px 0px 0px 0px;padding:5px;border:purple 2px outset;background-color:#cbbecc;font-size:14pt;font-weight:bold;margin-top:5px;">
		test_widget! 
		<input type="button" value="click me" onclick="alert ('i am an simple input helper')" />
	</span>
	<div style="font-size:10px;margin-top:3px">xpath: ${sf:idToPath (id)}</div>
	<div style="font-size:10px;margin-top:3px">inputHelper: text/widget.html</div>
	
</div> --%>

<%-- <div><span style="border:thin blue solid;background-color:#fcdede;padding:1px;">Standard Input Helper</span>
			<div style="font-size:10px;margin-top:3px">id: ${id}</div>
			<div style="font-size:10px;margin-top:3px">xpath: ${sf:idToPath (id)}</div> --%>
			
			
<%-- 			<c:set var="my_xpath" value="${sf:idToPath (id)}" />
			<div style="font-size:10px;margin-top:3px">typePath: ${sf:pathToId ('my_xpath+'/standard/id/@type')}</div> --%>

<script type="text/javascript">
/* 	Event.observe (window, 'load', function (event) {
		var helper = new StandardHelper ('${id}', '${sf:idToPath (id)}');
		log ('helper.type: ' + helper.getType());
		STANDARD_HELPERS.addMember (helper);
		}); */
</script>
