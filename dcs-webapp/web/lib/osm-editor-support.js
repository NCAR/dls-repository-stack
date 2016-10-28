/* javascript supporting the osm framework.
currenlty includes only code for asset input helper */

var AssetUrlHelper = Class.create ( {
	initialize: function (id, contextPath, repoPath) {
		this.id = id;
		log ("id: " + id);
		this.repoPath = repoPath;
		this.physicalPath = "http://nldr.library.ucar.edu/collections";
		this.input = $(id);
		this.input.writeAttribute ('size',"70");
		this.icon = this._get_icon(contextPath);
		this.input.insert ({after:this.icon});
		this.input.observe ('change', this.handleKeyUp.bindAsEventListener(this));
		this.input.observe ('focus', function (event) {
			if (this.getValue().length == 0) {
				this.input.value = "http://nldr.library.ucar.edu/collections/";
				this.input.select();
			}
		}.bind(this));
		// this.activate()
		this.handleKeyUp ();
	},
	
	handleKeyUp: function (event) {
		// regex = new RegExp (this.pysicalPath);
		var regex = /http\:\/\/nldr.library.ucar.edu\/collections\/([\S]+?)\/([\S]+)/
		var m = regex.exec (this.getValue())
		log (this.getValue())
		if (m) {
			log ("  match col: " + m[1] + ", asset: " + m[2]);
			this.activate(m[1], m[2]);
		}
		else {
			log ("  no match");
			this.deactivate();
		}
	},
	
	getValue: function () {
		return this.input.value;
	},
	
	activate: function (collection, asset) {
		var href = 'http://nldr.library.ucar.edu/repository/assets/' + collection + '/' + asset;
		this.icon.writeAttribute ('href', href);
		this.icon.show();
	},
	
	deactivate: function () {
		this.icon.hide();
	},
	
	_get_icon: function (contextPath) {
 		var icon = $(new Element ("a"));
		icon.setStyle({'display':'none'});
		var img = $(new Image ());
		img.src = contextPath+"/images/pdf-icon.png";
		img.setStyle ({
			height:15, 
			width:15, 
			border:0, 
			padding:"0px 0px 0px 5px"
		});
		icon.update (img);
		return icon; 
	}
});
