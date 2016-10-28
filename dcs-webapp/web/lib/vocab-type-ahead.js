// ---------- TYPE AHEAD SUPPORT ----------
/* vocab type ahead fields have class="vocab-type-ahead".
	The VocabTypeAhead class initializes all such elements
	to act in "type-ahead mode", where possible completions are shown
	for current field value.
*/
var VocabTypeAhead = Class.create ({
		initialize: function () {
		// this.busy = false;
		this.delay = 0.4; // time we wait before auto-completing
		this.nextUrl = null;
		this.valueContainer = $('type-ahead-value-container');
		if (!this.valueContainer) {
			this.valueContainer = new Element ('div', {'id':'type-ahead-value-container'}).setStyle({display:'none'});
			$(document.body).insert(this.valueContainer);
		}
		this.initializeVocabTypeAheadFields();
		this.spinner = this._init_simple_spinner();
		this.lastKeyUp = null;
		document.observe ('click', this.handleDocClick.bindAsEventListener(this));
		this.executer = null;
	},

	_init_simple_spinner: function () {
		var img = $(new Image());
		img.src = "../images/fedora-spinner.gif";
		img.setStyle ({height:"25px",width:"25px"});
		return img;
	},

	handleDocClick: function (event) {
		if (this.valueContainer.visible()) {
			this.emptyAndHide();
		}
	},

	showSpinner: function () {
		this.valueContainer.update (this.spinner);
		this.valueContainer.show();
	},

	initializeVocabTypeAheadFields: function () {
		// log ("initializeVocabTypeAheadFields");

		$$('.vocab-type-ahead').each ( function (el) {
			var initialValue = el.value;
			el.writeAttribute ('autocomplete', 'off')
			// log ("  path: " + path + ", initialValue: " + initialValue);
			el.observe ("keyup", this.handleKeyUp.bindAsEventListener(this));
		}.bind(this));
		log ("  VocabTypeAheadFields initialized");
	},

	handleKeyUp: function (event) {
		var keyCode = event.keyCode;
		// log ("keyup: " + keyCode);
		if (event.keyCode == '13' || event.keyCode == '9' || event.keyCode == '27') {
			this.emptyAndHide();
			return;
		}

		var el = event.element();
		var fragment = $F(el);
		if (!(fragment && fragment.strip().length > 1)) {
			this.emptyAndHide();
			return;
		}
		var name = el.readAttribute ("name");
		var path = name.match(/valueOf\((.*?)\)/)[1];

		var id = el.identify()
		// log ("  name: " + name + ", value: " + value);

		var params = $H($(document.forms[0]).serialize(true));
		params.update({value:fragment, path:path, command:"vocabTypeAhead"});
		var url = "edit.do?" + $H(params).toQueryString();
		// log ("sending url: " + url);

		if (this.executer) {
			this.executer.stop();
			this.executer = null;
		}
		this.executer = new PeriodicalExecuter ( function (pe) {
			log ("fire!");
			this.getTypeAheadValues (url, id, fragment);
			pe.stop();
		}.bind(this), this.delay);
		// this.getTypeAheadValues (url, id, fragment);
	},


	emptyAndHide: function () {
		this.valueContainer.update();
		this.valueContainer.hide();
	},

	getTypeAheadValues: function (url, id, fragment) {

		// do we test for fragement here?

/* 		log ("getTypeAheadValues (" + this.busy + ")");
		if (this.busy) {
			log ("  busy ...");
			this.nextUrl = url;
			return;
		}
		else {
			// this.busy = true;
			log ("  set busy to TRUE ");
		} */
		// this.showSpinner();
 		new Ajax.Request (url, {
			onSuccess: function (transport) {
				// log ("matches: " + transport.responseText);
				var json;
				try {
					json = transport.responseText.strip().evalJSON(true);
				} catch (error) {
					log ("could not parse json: " + error);
					this.emptyAndHide();
					return;
				}
				try {
					this.showTypeAheadValues (id, json.vocabTypeAhead.value, fragment);
				} catch (error) {
					log ("showTypeAheadValues error: " + error);
				}
			}.bind(this)
		});
	},


/* 	setBusyFalse: function  () {
		this.busy = false;
		log (" .. set busy to FALSE");
		if (this.nextUrl) {
			log (" processing nextUrl");
			var url = this.nextUrl;
			this.nextUrl = null;
			this.getTypeAheadValues(url);
		}
	}, */

	showTypeAheadValues: function (id, values, fragment) {
		// this.valueContainer.update();
		// this.emptyAndHide();
		log ("showTypeAheadValues: " + id);
		if (!values) {
			log (" no values to show");
			this.emptyAndHide();
			return;
		}

		var input = $(id);
		var offset = input.cumulativeOffset();
		var dims = input.getDimensions();
		// log ("top: " + offset.top+dims.height);
		this.valueContainer.setStyle ({top:offset.top+dims.height, left:offset.left, width:dims.width})
		if (typeof values == "string")
			values = [values]
		values = $A(values)
		this.valueContainer.update();
		if (values.size() > 500) {
			this.valueContainer.insert (new Element ('div').update (values.size() + " matching terms "));
		}
		else {
			$A(values).each ( function (value) {
				var valueElement = new Element ('div')
/* 				var pattern = "\/"+fragment+"\/i"
				log ('pattern: ' + pattern); */
				// valueElement.update(value.gsub(pattern, '<b>'+fragment+'</b>'));
				var regex = new RegExp(fragment, 'i');
				valueElement.update(value.gsub(regex, '<b>'+fragment+'</b>'));
				valueElement.addClassName ("vocab-type-ahead-term");
				valueElement.observe ('mouseover', function (event) {
					valueElement.addClassName ("vocab-type-ahead-term-over")
				});
				valueElement.observe ('mouseout', function (event) {
					valueElement.removeClassName ("vocab-type-ahead-term-over")
				});
				valueElement.observe ('click', function (event) {
					// log ("selected: " + value);
					input.value = value;
					this.emptyAndHide();
				}.bind(this));
				this.valueContainer.insert (valueElement);
			}.bind(this));
		}
		this.valueContainer.show();
		/* this.setBusyFalse(); */
	}
});
