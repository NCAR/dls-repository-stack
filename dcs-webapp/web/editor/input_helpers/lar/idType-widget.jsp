<%-- NOTE: this helper does NOTHING at this point! --%>

<%-- <div><span style="border:thin blue solid;background-color:#ffffcc;padding:1px;">IdType Helper</span>
			<div style="font-size:10px;margin-top:3px">id: ${id}</div>
			<div style="font-size:10px;margin-top:3px">xpath: ${sf:idToPath (id)}</div>
</div> --%>

<script type="text/javascript">

	/* Remove ASN from select options (ASN Standards cannot be created via choice element */
	Event.observe (window, 'load', function (event) {
		var select = $("${id}");
		// removeASNOption(select); // defined in lar-editor-support
	});
</script>
