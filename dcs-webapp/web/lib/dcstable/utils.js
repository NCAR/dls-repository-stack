/*
	applies the following to each 'dcs-table-row' of the table
	- assigns handlers to mouseovers to 'highlight'
	- assigns LINK mouseover handlers to all cells for that row
	-- we want to assign these at the cell-level
*/

var ImageManager = Class.create ({
	initialize :function (imagePath) {
		this.imagePath = imagePath;
		this.downArrow = new Image ();
		this.downArrow.src = imagePath + "/descending.gif"; // these img names seem backwards?
		this.upArrow = new Image ();
		this.upArrow.src = imagePath + "/ascending.gif";
		this.blankArrow = new Image ();
		this.blankArrow.src = imagePath + "/clear.gif";
		this.ndrDogEar = new Image();
		this.ndrDogEar.src = imagePath + '/nw-wedge.gif';
	},
			
	getArrow: function (spec) {
		switch (spec) {
			case "up":
				src = this.upArrow.src;
				break;
			case "down":
				src = this.downArrow.src;
				break;
			default:
				src = this.blankArrow.src;
		}
		return new Element("img", {src:src}).addClassName ("sort-widget");
	},
	
	getUpArrow: function () {
		return this.getArrow("up");
	},
	
	getDownArrow: function () {
		return this.getArrow("down");
	},
	
	getBlankArrow: function () {
		return this.getArrow("blank");
	},
	
	getNdrDogEarSrc: function () {
		return this.ndrDogEar.src;
	}
});


function createUrl (path, queryParams, hash) {
	var url = path;
	if (queryParams)
		url += "?" + $H(queryParams).toQueryString();
	if (hash)
		url += "#" + hash;
	return url;
}
	
function reportStyles  (element)  {
	var s = "StyleRpt";
	['border',].each ( function (style) {
		s += ", " + style + ":  " + element.getStyle (style);
	});
	s += ",  classes" + element.classNames();
	log (s);
	
	log ("element: " + element.inspect());
}

function doModalAction (evnt, row, action, args) {
	// log ("doModalAction: action: " + action + " args: " + args);
	
	if (row == null) {
		alert ("row not found");
		return;
	}
	
	row.table.highlightRow (row.key);
	action.apply(args);
	
	// how to now redisplay (table.redraw)??
	evnt.element().removeClassName ("selected");
	row.table.highlightRow (null);
}

