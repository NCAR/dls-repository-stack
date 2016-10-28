/*
	this file contains specialized classes to customize the cells that present
	collection names.
	- currently used to provide extra decoration indicating a collection is managed
	in the ndr.
	- in the future we might add a menu for collections that lets user zoom to different
	views (home, search, manage, settings, access) for the collections
*/
var DcsCollectionsTable = Class.create (DcsTable, {
	_className : "DcsCollectionsTable",
	
	createTableRow: function (rowKey, rowProps) {
		return new DcsCollectionsTableRow (this, rowKey, rowProps);
	}
});

var DcsCollectionsTableRow = Class.create (DcsTableRow, {
	_className : "DcsCollectionsTableRow",
		
	render: function () {
		this.element = this.makeElement();
		this.decorate();
		this.table.colProps.each (function (pair, i) {
			if (i == 0)
				this.addCell (new DcsCollectionNameCell (this, pair.key, i));
			else
				this.addCell (new DcsTableCell (this, pair.key, i));
		}.bind (this));
	}
});

var DcsCollectionNameCell = Class.create (DcsTableCell, {
	_className : "DcsCollectionNameCell",
		
	render: function () {
		this.element = this.makeElement();
		this.decorate();
		
/*      // NDR-collection dog-ear disabled
		var image = this.row.table.images.imagePath + "/nw-wedge.gif";	
		if (this.row.getProp('authority') == 'ndr') {
			this.element.setStyle ({
					backgroundImage:"url('" + image + "')",
					backgroundRepeat:'no-repeat'
			});
		}
*/
	}

});

