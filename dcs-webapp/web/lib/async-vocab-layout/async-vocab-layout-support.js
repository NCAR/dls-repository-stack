// keeps track of the various AsyncVocabLayout instances
var asyncVocabLayouts = $H();

/* vocab-driven layout. has two modes: edit and view
*/
var AbstractAsyncVocabLayout = Class.create ({
	initialize: function (id, xpath, elementPath, initialFieldValue) {
		this.id = id;
		asyncVocabLayouts.set(this.id, this);
		this.xpath = xpath;
		this.elementPath = elementPath;
		this.initialFieldValue = initialFieldValue;

		this.viewElement = $(this.id + "_async_view_element");
		this.editElement = $(this.id + "_async_edit_element");
		this.selectionCache = $(this.id + "_hidden");

		this.initialized = false;
		this.initControls();
		this.doViewMode();
		this.initialized = true;
	},

	initializeSelectionCache: function () {
		alert ("initializeSelectionCache (" + this.elementPath + " is abstract and must be defined in concrete class");
	},

	renderFieldValue: function  () {
		alert ("renderFieldValue is abstract and must be defined in concrete class");
	},

	initControls: function () {
		$A([this.viewElement, this.editElement]).each ( function (modeTag) {
			var mode = modeTag == this.viewElement ? 'view' : 'edit';
			var buttonClass = mode == 'view' ? 'edit-button' : 'view-button';
			// log ('initControls; mode is ' + mode + '  buttonClass is ' + buttonClass);
			var button = modeTag.down('div.'+buttonClass);
			if (!button)
				log ("selection button not found for " + mode + " (" + modeTag.identify() + ")");
			button.observe ("mouseover", function () {
				button.setStyle ({'textDecoration':'underline', 'cursor':'pointer'});
			});
			button.observe ("mouseout", function () {
				button.setStyle ({'textDecoration':'none', 'cursor':'none'});
			});
			if (mode == 'view')
				button.observe ("click", this.doEditMode.bindAsEventListener(this));
			if (mode == 'edit')
				button.observe ("click", this.doViewMode.bindAsEventListener(this));
		}.bind(this));
	},

	/* display the current selection (display mode) */
	doViewMode: function () {
		
		this.editElement.hide();
		
		/* 
			when in View mode, we preserve the value for this field by caching a value
			in the "SelectionCache". If we were editing, then the value to be cached comes
			from the form (see updateSelectionCache).
		*/
		if (!this.initialized) {
			this.initializeSelectionCache (this.initialFieldValue);
		}
		else {
			// selection cache is needed only during initial view. once input elements are
			// constructed they are always visible to form and cached must be destroyed
			this.selectionCache.update()
		}
		this.renderFieldValue();
		this.viewElement.show();

	},

	/* render the vocabLayout (select mode) */
	doEditMode: function (event) {

		var vocabLayout = $(this.id + "_vocab_layout");
		
		
		var form = document.forms[0]; 
		if (form == undefined)
			throw "form is undefined for doEditMode";
		form.pathArg.value = this.xpath;
		form.command.value = "vocabLayout";

		var params = $(form).serialize(true);
		
		this.selectionCache.update(); // this wipes out the selection cache
		
		this.viewElement.hide();
		this.editElement.show();

		// only get layout once
		if (vocabLayout.empty()) {
			// log ("vocabLayout is EMPTY - loading now ...");
			vocabLayout.update ("loading ....");

			new Ajax.Request ('edit.do', {
				method:'post',
				parameters: params,
				onComplete: function(transport) {
					try {
						var html = transport.responseText;
						vocabLayout.update (html);
						vocabLayout.fire ("asyncVocab:loaded", {asyncVocabId:this.id});
					} catch (error) {
						alert ("displayVocabLayout ajax error: " + error);
					}
				}.bind(this),
				onFailure: function() {
					alert('Unable to update vocab Layout display')
				}
			});
		}
		else {
			vocabLayout.fire ("asyncVocab:loaded", {asyncVocabId:this.id});
		}		
	},

	/* see metadata-editor-support.toggleDisplayState */
	show: function () {
		var element = $(this.id);
		if (!element.visible())
			toggleDisplayState (this.id);
	},
	
	/* returns a LIST of values present in the form for this.elementPath */
	getFormValueList: function  () {
		var form = document.forms[0]; // this is the only thing chrome will accept
		if (form == undefined)
			throw "form is undefined";
		var params = $(form).serialize(true);
		var valueObj = $H(params).get(this.elementPath);
		return Object.isString (valueObj) ? $A([valueObj]) : valueObj;
	},

	getCachedValues: function () {
		// log ("cached values for " + this.id);
		var values = this.selectionCache.select ('input').inject ([], function (acc, input) {
			// log (" - " + input.value);
			acc.push (input.value);
			return acc;
		});
		return values;
	},
	
	debugState: function () {
		log ("STATE - " + this.elementPath + '\n------------');
		log ("cached values");
		var cachedVals = this.getCachedValues();
		if (!cachedVals || cachedVals.length == 0)
			log (" --> none");
		else
			cachedVals.each( function (val) { log ('- ' + val) } );
		log ("form values");
		var formVals = this.getFormValueList();
		if (!formVals)
			log (" --> none");
		else
			formVals.each( function (val) { log ('- ' + val) } );
		
		log ("form elements");
		var form = document.forms[0];
		form.getInputs().each( function (input) {
			if (input.name == this.elementPath) {
				var skip = false;
				if (input.type == 'radio' && !input.checked)
					skip = true;
				if (!skip)
					log ('- ' + input.type + '  ' + input.name + '  ' + input.value);
			}
		}.bind(this));
		log ('------------');
	}
});

/* controls the single-select asyncronous vocabLayout field */
var AsyncSingleSelect = Class.create (AbstractAsyncVocabLayout, {
 	initialize: function ($super, id, xpath, elementPath, selectedValue) {
		$super (id, xpath, elementPath, selectedValue);
	},

	/* Using info from the FormBean, initialize the hidden selection with
		 the current values for this field
	*/
	initializeSelectionCache: function (selectedValue) {
		var hiddenVar = '<input type="hidden" name="'+this.elementPath+'" value="'+selectedValue+'" />';
		this.selectionCache.update(hiddenVar);
	},

	/* 
		create a hidden variable for the item so the values will be preserved in
		view mode. NOTE: only one item can be selected for AsyncSingleSelect
	*/
	updateSelectionCache: function  () {
		
		var formValues = this.getFormValueList()
		if (formValues == null) {
			log ("AsyncSingleSelect.updateSelectionCache: getFormValuesSelect() returned null");
			this.selectionCache.update();
			return;
		}
		else if (formValues.length < 1) {
			log ("AsyncSingleSelect.updateSelectionCache: formValues is empty");
			this.selectionCache.update();
			return;
		}
		else if (formValues.length > 1) {
			log ("WARNING: there are " + formValues.length + " values for " + this.elementPath + " (a SINGLE SELECT)");
		}
		var value = formValues [0];
		this.selectionCache.update ('<input type="hidden" name="' + this.elementPath + '" value="' + value + '" />');
	},

	// show the currently selected values by drawing from the hidden variables
	renderFieldValue: function  () {
		var currentValuesDisplay = $(this.id + "_value_display");
		var currentValue = null
		try {
			currentValue = this.getFormValueList()[0];
		}
		catch (error) {
			log ("WARNING: renderFieldValue could not get current value");
			currentValue = null;
		}
		currentValuesDisplay.update (currentValue || "Unspecified");
	}

})

var AsyncMultiBox = Class.create (AbstractAsyncVocabLayout, {

	initialize: function ($super, id, xpath, elementPath, selectedValues) {
		$super (id, xpath, elementPath, selectedValues)
		// log ("AsyncMultiBox initialized for " + this.elementPath);
	},

	/* Using info from the FormBean, initialize the hidden selections with
		 the current values for this field
	*/
	initializeSelectionCache: function (selectedValues) {
		this.selectionCache.update();
		$A(selectedValues).each (function (term) {
			this.selectionCache.insert ('<input type="hidden" name="'+this.elementPath+'" value="'+term+'" />');
		}.bind(this));
	},

	// show the currently selected values by drawing from the hidden variables
	renderFieldValue: function  () {
		var currentValuesDisplay = $(this.id + "_value_display");
		var valueList = this.getFormValueList();
		var valueTree = new ValueHierarchy ( valueList );
		currentValuesDisplay.update (valueTree.render());
	},

	/* create a hidden variable for each selected item so the values will be preserved
		 in display mode
	*/
	updateSelectionCache: function  () {
		this.selectionCache.update();
		this.getFormValueList().each (function (term) {
			this.selectionCache.insert ('<input type="hidden" name="' + this.elementPath + '" value="' + term + '" />');
		}.bind(this));
	}
});

/* holds single segment of colon-delimited vocab terms in linked tree */
var ValueNode = Class.create ({
	initialize: function (name, parent) {
		this.name = name;
		this.parent = parent;
		this.children = $A();
	},

	isEmpty: function () {
		return this.children.size() == 0;
	},

	getChild: function (name) {
		return this.children.find ( function (child) {
			return child.name == name;
		});
	},

	addChild: function (name) {
		var child = this.getChild (name);
		if (!child) {
			child = new ValueNode (name, this);
			this.children.push (child);
		}
		return child;
	},

	report: function (level) {
		level = level || 0;
		var indent = '';
		for (var i=0;i<level;i++)
			indent += " ";
		log (indent + this.name);
		this.children.each (function (child) { child.report (level+1) });
	},

	render: function (level) {
		level = level || 0;
		var indent = level * 15;
		var s = ""
		if (this.name != "root") {
			var label = this.children.size() == 0 ? '<b>'+this.name+'</b>' : this.name;
			s = '<div style="margin-left:'+indent+'px">' + label + '</div>'
		}
		this.children.each (function (child) {
			s += child.render(level+1);
		});
		return s;
	}
});

/* structures ValueNodes for hierarchical display of colon-delmited vocab terms */
var ValueHierarchy = Class.create ({
	initialize: function (data) {
		this.root = new ValueNode ("root", null);
		$A(data).each (function (term) {
			if (term) {
				this.insertTerm (term);
			}
		}.bind(this));
	},

	insertTerm: function (term) {
		// value is : delimited string
		var ptr = this.root;
		$A(term.split(":")).each ( function (seg) {
			var child = ptr.addChild (seg);
			ptr = child;
		});
	},

	isEmpty: function () {
		return this.root.isEmpty();
	},

	report: function () {
		log ("value hierachy");
		this.root.report();
	},

	render: function () {
		if (this.isEmpty())
			return "<div><i>There are no selections</i></div>"
		else
			return this.root.render();
	}
});
