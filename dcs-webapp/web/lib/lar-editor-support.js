/* javascript supporting the osm framework.
currenlty includes only code for asset input helper */

var StandardsHelpers = Class.create( {
	initialize: function () {
		this.members = {};
	},
	addMember: function (standardsHelper) {
		this.members[standardsHelper.id] = standardsHelper;
	},
	getMember: function (id) {
		return this.members[id];
	}
});

var StandardHelpers = Class.create( {
	initialize: function () {
		this.members = {};
	},
	addMember: function (standardHelper) {
		this.members[standardHelper.id] = standardHelper;
	},
	getMember: function (id) {
		return this.members[id];
	}
});

var STANDARDS_HELPERS = new StandardsHelpers ();
var STANDARD_HELPERS = new StandardHelpers ();

var StandardsHelper = Class.create ( {
	initialize: function (id, path) {
		this.id = id;
		this.path = path;
		this.label = $(this.id+'_label');
		this.type_select = $(this.id+'_^_standard_^_id_^_@type')
		this.std_box = $(this.id+'_^_standard_box');
		this.align_box = $(this.id+'_^_alignmentDegree_box');
		// log ("StandardsHelper id: " + id);
		
		this.std_id_input = $(this.id + '_^_standard_^_id');
		
		this.id_type = this.getType();
		// log ("id_type: " + this.id_type);
		
		if (!$(this.id+'_^_standard')) {
			// log ("standard element not found for " + this.id);
			return;
		}
		
		// THIS FAILS for ASN STANDARDS
		if (!$(this.id+'_^_standard').down('table')) {
			// STATE: empty
			this.label.update ('empty(no standard element)');
			this.label.show();
			// hide standard and alignmentDegree elements ...
			
			// leave shown so we can see how it works ...
			// this.std_box.hide();
			// this.align_box.hide();
			var choice1 = new Choice1 (this.id, this.path);
			this.label.insert ({'after' : choice1.dom});

		}		
		else if (!this.type_select) {
			this.label.update ('no type select attribute');
			this.label.show();
			// do nothing ...
		}		
		else if (!this.id_type) {
			this.label.update ("no type defined: show select!");
			this.label.show();
		}
		else if (this.id_type == 'ASN') {
			alert ('ASN - alert!!');
			/* ASN 
			- show the id (and std text) as static
			- hide the id/@type (make it hidden attr)
			*/
			this.label.update (this.id_type);
			this.label.show();
			var input = this.std_id_input;
			if (!input)
				alert("danger - no id input box for an ASN standard!");
			else {
				this.std_box.update (new Element ('div').update ($F(input)));
				var id_type_name = 'valueOf(' + this.path + '/standard/id/@type)';
				this.std_box.insert (new Element ('input', {type:'hidden', name:id_type_name, value:'ASN'}).update ($F(input)));
			}
		}
		else {
			this.label.update (this.id_type);
			this.label.show();
		}
	},
	getType: function () {

		var input_id = this.id+'_^_standard_^_id_^_@type'
		// log ("input_id: " + input_id);
		var input = $(input_id);
		if (input) {
			// log ("found");
			return $F(input)
		}
		else {
			// log ("NOT found");
			return null;
		}

	},
	hideStandardBox: function () {
		// $(this.id+'_^_standard_box').hide();
		log ("standardBox: " + this.std_box.identify());
		this.std_box.hide();
		$(this.id+'_^_alignmentDegree_box').show();
		this.align_box.show(); // necessary for some reason???
	},
	showStandardBox: function () {
		this.std_box.show();
	}

});

var Choice1 = Class.create ({
	initialize: function (id, path) {
		this.id = id;
		this.path = path;
		this.dom = this.initDom();
	},
	
	initDom: function() {
		var dom = new Element('div').update ('choose between ASN and Other');
		dom.observe ('click', function () { alert ("yo choice1") });
		return dom;
	}
});

var StandardHelper = Class.create ( {
	initialize: function (id, path, repoPath) {
		this.id = id;
		this.path = path;
		log ("StandardHelper id: " + id);
	},
	
	getType: function () {
		var input_id = this.id+'_^_id_^_@type'
		log ("input_id: " + input_id);
		var input = $(input_id);
		if (input) {
			log ("found");
			return $F(input)
		}
		else {
			log ("NOT found");
			return null;
		}

	}
});


function removeASNOption (select) {
	for (i=0;i<select.options.length;i++) {
		var option = select.options[i];
		if (option.value == "ASN") {
			log ("found one");
			select.options[i] = null;
		}
	}
}
