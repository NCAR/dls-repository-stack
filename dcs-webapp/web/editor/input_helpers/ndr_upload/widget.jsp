<%-- <div style="margin-left:100px"><span style="border:purple 2px outset;background-color:#cbbecc;padding:5px;font-size:14pt;font-weight:bold">
	test_widget! <input type="button" value="click me" onclick="alert ('i am an input helper')" /></span>
</div>
 --%>

<script type="text/javascript">

var upload_widget;

var NdrUploadWidget = Class.create ({

	initialize: function (id) {
		this.id = id;
		this.metadata_field = findInputElement (this.id);
		if (!this.metadata_field)
			throw ("metadata field not found");
			
		// field_wrapper wraps original input and the trigger button
		this.field_wrapper = new Element ('div');
		// use Element.wrap form due to IE - see http://www.prototypejs.org/api/element/wrap
		Element.wrap (this.metadata_field, this.field_wrapper);
			
		this.metadata_field.setStyle ({marginRight:'20px'});
		this.field_wrapper.insert (this.make_trigger_button());

	},
	
	make_trigger_button: function () {
		return  new Element("input", {
			type:'button',
			value:'upload file'
			})
			.addClassName ('record-action-button')
			.observe ('click', this.activate_upload.bindAsEventListener(this));
	},
	
	activate_upload: function () {
			// the  tool path is relative to the "editor" directory

			params = { forwardPath : "/editor/input_helpers/ndr_upload/confirmation.jsp" }
			var url = "../ndr/upload.do" + '?' + $H(params).toQueryString();
			newwindow=window.open(url,'Upload_Tool','height=275,width=450');
			newwindow.focus()
			return false;
	},
	
	update_metadata: function (url) {
		this.metadata_field.setValue (url);
	}
	
});

function update_upload_widget (val) {
	upload_widget.update_metadata(val);
}

function pageInit () {
	upload_widget = new NdrUploadWidget ("${id}");
}

/* input for simple type has the field's id */
function findInputElement (id) {
	var e = $(id);
	if (!e)
		throw ("element not found for " + id);
	else {
		return e;
	}
}

Event.observe (window, 'load', pageInit);
</script>

