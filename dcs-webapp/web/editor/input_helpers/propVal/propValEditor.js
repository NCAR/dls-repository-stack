/* presents an interface for editing property/value fields, writing results
	into a metadata field (specified by id) as dlimited string
	NOTE: metadata field must be either a TEXT TEXTAREA input
*/
var PropValEditor = Class.create({

	initialize: function (id, default_fields) {
		// default_fields will always be present - others may be added during editing
		this.default_fields = $A(default_fields)
		this.id = id;
		this.metadata_field = findInputElement (this.id);
		if (!this.metadata_field)
			throw ("metadata field not found");
		
		this.helper_element = $(this.id + "_helper");
		if (!this.helper_element)
			throw ("helper Element not found");
				
		this.fields_element = this._get_fields_element();
			
		/* replace the metadata_field form element with a hidden input, but
		   preserve the value */
		try {
			var hidden_input = new Element ('input', {
				type : 'hidden',
				name : this.metadata_field.readAttribute ('name'),
				value : $F(this.metadata_field)
			});
			this.metadata_field.up().replaceChild (hidden_input, this.metadata_field);
			this.metadata_field = hidden_input;
		} catch (error) {
			throw ("couldn't hide original: " + error);
		}
		this.field_map = this._get_field_map (this.metadata_field.getValue());
		this.helper_element.insert (this._get_global_new_field_button());
		this.render ();
	},
		
	/* initialize the DOM with table to house input fields, and return the 'tbody' element */
	_get_fields_element: function () {
		fields_element = new Element ('tbody');
		this.helper_element.insert (new Element ('table').insert (fields_element));
		return fields_element;
	},
	
	
	/* parse the value of the metadata field into a hash of key/listOfValue pairs*/
	_get_field_map: function () {
		/* first convert the values from the fieldValue into a map */
		var field_map = $H()
		this.metadata_field.getValue().split("\\n").each (function (line) {
			if (!line) return // nothing to process - bail
			
			var prop, val;
			// if there is no field/value delimiter assume contents is a value
			if (line.indexOf("=") == -1) {
				val = line.strip();
			}
			else {
				var splits = line.split("=");
				prop = splits[0].strip();
				val = splits.length > 1 ? splits[1].strip() : "";
			}
			
			if (!prop) prop = "unknown"; // assign unknown prop if necessary
			
			if (!val) return; // bail if there is not a val for this field
			
			// update map
			var field_list = field_map.get(prop) || [];
			field_list.push(new InputField (prop, val, this));
			field_map.set (prop, field_list);
		}.bind(this));
		
		// now we ensure that all the default fields are represented
		this.default_fields.each( function (prop) {
			if (!field_map.get(prop))
				field_map.set(prop, [new InputField (prop, "", this)]);
		}.bind(this));
		
		return field_map;
	},
	
	/* populate the helper_fields and the helper_fields_element */
	render: function () {
		// clear the data structures to be populated
		this.fields_element.update();
		
		// first the default fields
		var orderedFields = this.default_fields.inject ([], function (array, name) {
			array.push (name);
			return array;
		});
		// now the other fields (sorted)
		this.field_map.keys().sort().inject (orderedFields, function (array, prop) {
			if (array.indexOf (prop) == -1)
				array.push (prop);
			return array;
		});
		
		// walk down the field list and render the fields
		orderedFields.each ( function (prop) {
			var field_list = this.field_map.get(prop);
			field_list.each (function (field) {
				this.fields_element.insert (field.element);
			}.bind (this));
		}.bind(this));
		
	},
	
	/* add a new, empty field for given "prop" */
	addField: function (prop, val) {
		// metadata has already been updated
		val = val || ""
		var field = new InputField (prop, val, this);
		var field_list = this.field_map.get (prop) || []
		field_list.push(field)
		this.field_map.set  (prop, field_list);
		this.render();
		field.getInput().focus();
	},

	/* update the metadata field value from the inputHelper fields - ignoring empty fields */
	updateMetadata: function () {
		var metadataValue = this.field_map.values().inject ([], function (array, field_list, index) {	
 			field_list.each ( function (field) {	
				if (field.getValue().strip())
							array.push (field.toString());
				});
			return array;
		}).join ("\\n");
		this.metadata_field.setValue(metadataValue);
	},
	
	removeField: function (field) {
		var prop = field.prop
		var field_list = this.field_map.get(prop);
		
		// create new field list without field to be deleted
		var new_list  = field_list.inject ([], function (array, my_field) {
			if (field != my_field)
				array.push (my_field);
			return array;
		});
		this.field_map.set(prop, new_list);
		
		
		if (new_list.size() == 0) {
			// default fields are always present - add an empty one
			if (this.default_fields.indexOf(prop) != -1) {
				this.addField (prop);
			}
			else {
				// remove this field from field map
				this.field_map.unset(prop);
			}
		}
		this.updateMetadata();
		this.render();
	},
	
	/* button that goes beneath fields and allows creation of new field */
	_get_global_new_field_button: function () {
		var button = new Element ('input', {
			type: 'button',
			value: ' + ',
			title: 'add a new field name'
		})
		button.addClassName("record-action-button")
		button.observe ('click', function (event) {
			var prop = prompt ("enter new field name", "");
			if (!prop)
				return;
			
			if (this.field_map.get(prop)) {
				alert ("A \'" + prop + "\' property already exists");
				return;
			}
			
			this.addField (prop);
		}.bind(this));
		return button;
	}
});

/*  an input element (label and input) that can report it's
	value as "prop=val" (via toString method).
*/
var InputField = Class.create({

	initialize: function (prop, val, parent) {
		this.prop = prop;
		this.parent = parent;
		this.element = this._make_element (prop, val);
	},
	
	/*  create a input helper field as a table row element. components include
		label, value input, new_field button, remove_field button
	*/
	_make_element: function (prop, val) {
		var label_cell = new Element ('td')
			.addClassName ('label-cell')
			.insert (prop);
			
		var input = new Element ('input', {type:'text', name:prop, value:val, size:"50"})
			.observe ('blur', function () { 
				this.parent.updateMetadata();
			}.bind(this));
		
		var new_field_button = new Element ('input', {
				type:'button', value:' + ',
				title:'add a new \"' + this.prop + '\" field'
			})
			.addClassName ('record-action-button')
			.observe ('click', function (event) {
				this.parent.addField (this.prop);
			}.bind(this));
			
		var remove_field_button = new Element ('input', {
				type:'button', value:' - ',
				title:'remove this field'
			})
			.addClassName ('record-action-button')
			.observe ('click', function (event) {
				this.parent.removeField(this);
			}.bind(this));
			
		var input_cell = new Element ('td')
			.addClassName ('input-cell')
			.insert (input)
			// .insert (new_field_button)  // not for singleton properties
			.insert (remove_field_button);
			
		return new Element('tr').insert(label_cell).insert(input_cell);	

	},
	
	getInput: function () {
		return this.element.select('input')[0];
	},
	
	/* return input value */
	getValue: function () {
		return this.getInput().getValue();
	},
	
	/* return "prop=value" */
	toString: function () {
		return this.prop + "=" + this.getValue();
	}
});

/* utility function to find a text input or textarea element for given id in the metadata editor 
- tries first to grab the first input/text element within the id element, 
- if text/input is not found, tries to grab first textarea
- returns null if input is not found
*/
function findInputElement (id) {
	var e = $(id);
	if (!e)
		throw ("element not found for " + id);
	else {
/* 		var inputs = e.select('input[type="text"]'); // look for text input first
		if (inputs.size() == 0)
			inputs = e.select('textarea')
		if 	(inputs)
			return $(inputs[0]); // select returns a list
		else
			return null; */
		return e;
	}
}

	

