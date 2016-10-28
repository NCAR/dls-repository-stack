

function AffiliationToolWindow (url, helper) {

	var win = new Window("helper_window", {
		className: 'dcs_alphacube', // bluelighting alphacube mac_os_x
		// title: "Update Status",
		width:600,
		height:500,
		destroyOnClose: true,
		recenterAuto:false,
		minimizable:false,
		maximizable:false,
		showEffectOptions: {duration:0.5},
		hideEffectOptions: {duration:0.5},
		// showEffect: Effect.BlindDown,
		// hideEffect: Effect.BlindUp,

		// iframe content
		url:url
	});

	win.setCloseCallback (function (event) {
		// affiliationTool has set data before closing
		helper.updateRecord();
		return true;
	});

 	win.showCenter(true, 50);
	return win;
}

/* 
Affiliation helper is responsible for getting and setting instanceRecord fields.

The simpleType child elements of "person" are accessed via "setAuthorChildValue
and getAuthorChild.

The instName and instDiv fields are accessed through AbstractAsyncVocabLayout
instances (see "/lib/async-vocab-layout/async-vocab-layout-support.js),
which control the display of their respective fiels, as well as setting values.
*/
var AffiliationHelper = Class.create ({
	initialize: function (id, delimiter) {
		this.affiliationId = id;
		this.delimiter = delimiter;

		this.instNameLayout = null;
		this.instDivisionLayout = null;

		this.toolWindow = null;
		this.toolData = null;
		this.asyncVocabasyncVocabObserverIsSet = false;
	},

	getVocabController: function (layoutName) {
		log ('getVocabController(): - layout name is ' + layoutName);
		var layoutId = this.affiliationId + this.delimiter + layoutName;
		log ('  id: ' + layoutId);
		return asyncVocabLayouts.get (layoutId);
	},

	/* set up listener that will be fired by instNameLayout when it is
	   finished going into edit mode. THEN instDivisionLayout will start.
	   This technique ensures that the ajax and dom operations are sequenced
	*/
	setAsyncVocabObserver: function () {
		if (!this.asyncVocabObserverIsSet) {
			document.observe ('asyncVocab:loaded', function (event) {
				// log ("observed asyncVocab:loaded for " + event.memo.asyncVocabId);
				if (event.memo.asyncVocabId == this.instNameLayout.id) {
					this.instDivisionLayout.doEditMode();
					event.stop();
				}
			}.bind(this));
			this.asyncVocabObserverIsSet = true;
		}
	},
	
	/* before opening the affiliationTool popup, we have to manipulate the input
	   for 'instName' and 'instDivision'.
	   - we set both of these 'vocabControllers' to 'editMode' so they can be updated
	*/
	openAffiliationTool: function () {
		try {
		
			if (!asyncVocabLayouts)
				throw ("asyncVocabLayouts not found");
			this.instNameLayout = this.getVocabController("instName");
			if (!this.instNameLayout) {
				throw ("WARNING: instName controller not found");
			}
			
			log ("instNameLayout id: " + this.instNameLayout.id);
			
			this.instDivisionLayout = this.getVocabController("instDivision");
			if (!this.instDivisionLayout) {
				throw ("WARNING: instDivision controller not found");
			}
			// asyncVocabObserver fires instDiv layout after instName is done
			this.setAsyncVocabObserver(); 
 			this.instNameLayout.doEditMode();
		} catch (error) {
			this.showErrorMsg (error);
			$(this.affiliationId + '_button').disable();
			return;
		}
			
		var url = "input_helpers/osm/affiliationTool.jsp";
		url = url + '?id=${id}';
		window.affiliationHelper = this;
		this.toolWindow = AffiliationToolWindow(url, this);
		// infoDialog(recId);

		// Escape closes window
		WindowCloseKey.init()
		WindowStore.init();
		return false;
	},

	showErrorMsg: function (errMsg) {
		var errorMsgEl = $(this.affiliationId + '_msg');
		errorMsgEl.update (errMsg);
		errorMsgEl.addClassName ("error-msg");
	},
	
	getAuthorId: function () {
		var splits = this.affiliationId.split(this.delimiter);
		return splits.slice(0, splits.length-1).join(this.delimiter);
	},

	/* multi selects (like instDivision) have '_id' appened 
		external denotes an external instName, which may not exist in the vocab*/
	setInstName: function (value, external) {
		var id = this.affiliationId + this.delimiter + "instName_" + value + "_id";
		if ($(id))
			$(id).checked = true;
		else if (external) {
			// this value is not in the vocab. add a button for it at top of list
			var instNameXpath = this.affiliationId.sub(RegExp.quote(this.delimiter), '/', 100) + '/instName';
			var div = new Element('div').addClassName('leaf-wrapper');
			var input = new Element('input', {
				id: id,
				type: 'radio',
				value: value,
				name: 'valueOf(' + instNameXpath + ')',
				checked: true
			});
			var label = new Element ('label', {'for': id}).addClassName('typevocab').update(value);
			div.insert(input);
			div.insert(label);
			var instNameBase = $(this.affiliationId + this.delimiter + "instName");
			var target = instNameBase.down('td');
			if (!target) {
				log ("    setInstName did not find target (" + id + ")");
			}
			else {
				target.insert ({top:div});
			}
		}
		else {
			alert ("ERROR: A checkbox for selected instName could not be found in editor UI\n" + value);
			log ("WARNING: setInstName could not find instName element (" + id + ")");
		}
	},

	/* multi selects (like instDivision) have '_checkbox' appened */
	setInstDivision: function (value) {
		var id = this.affiliationId + this.delimiter + "instDivision_" + value + "_checkbox";
		if ($(id))
			$(id).checked = true;
		else {
			alert ("ERROR: A checkbox for selected instDivision could not be found in editor UI\n"+ value);
			log ("WARNING: setInstDivision could not find element for " + id);
		}
	},

	getAuthorChildValue: function (tag) {
		var element = $(this.getAuthorId()+this.delimiter+tag);
		return (element ? element.value : '');
	},

	setAuthorChildValue: function (tag, value) {
		var element = $(this.getAuthorId()+this.delimiter+tag);
		if (element)
			element.value = value;
	},

	getUpid: function () {
		return this.getAuthorChildValue('@UCARid');
	},

	setUpid: function (upid) {
		if (typeof (upid)=== "undefined")
			upid = "";
		this.setAuthorChildValue('@UCARid', upid);
	},

	getLastName: function () {
		return this.getAuthorChildValue('lastName');
	},

	setLastName: function (value) {
		this.setAuthorChildValue ('lastName', value);
	},

	getFirstName: function () {
		return this.getAuthorChildValue('firstName');
	},

	setFirstName: function (value) {
		this.setAuthorChildValue ('firstName', value);
	},

	getMiddleName: function () {
		return this.getAuthorChildValue('middleName');
	},

	setMiddleName: function (value) {
		this.setAuthorChildValue ('middleName', value);
	},

 	getRecordData: function () {
		params = {
			firstName : this.getFirstName(),
			lastName : this.getLastName(),
			middleName : this.getMiddleName(),
			upid: this.getUpid()
		}
		return params;
	},

	/* populate metadata with results placed into 'toolData' by AffiliationTool */
	updateRecord: function () {
		log ("updateRecord (updating metadata record)");
		if (this.toolData) {

			var safeValue = function (value) {
				return (typeof (value) === "undefined") ? '' : value;
			};
			
			this.setLastName (safeValue(this.toolData['lastName']));
			this.setFirstName (safeValue(this.toolData['firstName']));
			this.setMiddleName (safeValue(this.toolData['middleName']));
			
			var instName;
			if (this.toolData['isExternal']) {
				log (" .. EXTERNAL");
				this.setUpid ("");
				instName = safeValue(this.toolData['instName']);
				this.setInstName(instName, true);
			}
			else {
				log (" .. INTERNAL");
				this.setUpid (this.toolData['upid']);
				instName = "University Corporation for Atmospheric Research";
				this.setInstName(instName);
				if (this.toolData['instDivision'])
					this.setInstDivision(this.toolData['instDivision']);
			}
			

		}
		else {
			log ("no data to set");
		}
		this.instNameLayout.doViewMode();
		this.instNameLayout.show();
		this.instDivisionLayout.doViewMode();
		this.instDivisionLayout.show();
	}
});
