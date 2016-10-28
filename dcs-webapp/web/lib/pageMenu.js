/* Javascript classes supporting the pageMenu that appears at the top
of each page 

- included in pages via baseHTMLIncludes.jsp
- inserted in pages by pageHeader.tag
- data supplied by pageMenuData.jsp

*/

var pageMenu = null;
var debug = false;
var currentMenuItem = null;
var verbose = false;

function myCreateElement (tag, props) {
	var element = null;
	try {
		element = $(document.createElement(tag));
		if (props)
			element.writeAttribute (props);
	} catch (error) {
		throw "could not create element: " + error;
	}
	return element;
}

var Menu = Class.create ({
	// data is a HashMap
	initialize: function (parent, data, currentTool) {
		data = $H(data);
		this.parent = $(parent);
		this.currentTool = currentTool;
		this.itemWidth = data.get('width');
		this.itemHeight = data.get('height');
		
		var table = myCreateElement('table');
		table.addClassName('mainMenu');
		
		parent.insert (table);
		var row = $(table.insertRow(0));
		
		// data.items is an array of hashes 
		this.items = data.get('items').collect ( function (itemData, index) {
			itemData = $H(itemData);
			
			var item = new MenuItem (row.insertCell(index), itemData, this, index);
			
			item.setAttribute ("height", this.itemHeight || 0);
			item.setAttribute ("width", this.itemWidth || 0);
			
			return item;
		}.bind (this));
		
  		this.items.each ( function (item) {
			item.renderSubMenu ();
		});
	},
	
	getItem: function (id) {
		// look through items (menuItems) for specified id
		var item = this.items.find ( function (menuItem) {
			return (menuItem.id == id);
		});
			
		if (item == null) {
			this.items.find ( function (menuItem) {
				if (menuItem.subMenu) {
					item = menuItem.subMenu.getItem (id);
					return item != null;
				}
			});	
		}
		return item;
	}
});

// --- MENU ITEM ---
var MenuItem = Class.create({
	initialize: function ( element, data, menu, index ) {
		this.element = $(element)
		this.data = $H(data);
		this.id = data.get('id');
		this.menu = menu;
		this.isCurrentTool = (this.id == this.menu.currentTool);
		this.label = data.get('label');
		this.href = this._getHref(data);
		this.subMenu = null;
		this.role = data.get('role') || null;
		
		element.setAttribute  ("id", this.id);
		element.addClassName ("menuItem");
		if (this.isCurrentTool)
			element.addClassName ("current");
		
		var labelElement = null;
		try {
			labelElement = this.getLabelElement();
		} catch (error) {
			throw "getLabelElement() error: " + error;
		}
		
		try {
			element.appendChild (labelElement);
		} catch (error) {
			throw "could not append labelElement";
		}

		if (!this.isCurrentTool) {
			element.observe ("mouseover", function (event) {
				element.addClassName ("over");
			});
			element.observe ("mouseout", function (event) {
				element.removeClassName ("over");
			});
		}
		element.observe ('click', this.clickHandler.bindAsEventListener (this));
	},
	
	_getHref: function () {
		var href = this.data.get('href');
		if (href == "#") return href;
		
		// if an href is not absolute, treat it as context-relative
		if (!href.startsWith ("http://")) {
			href = contextPath + this.data.get('href');
			// we augment the search link with cached searching params when possible
			if (this.id == "search") {
				if (searchParams != undefined && searchParams.length > 0) {
					paigingParam = (paigingParam != undefined) ? paigingParam : "";
					href = href + "?s=" + paigingParam + searchParams;
				}
			}
		}
		return href;
	},
	
	setAttribute: function (att, value) {
		this.element.setAttribute (att, value);
	},
	
	getLabelElement: function () {
		
		var labelElement = myCreateElement("div");
		labelElement.update (this.label);
		labelElement.addClassName ('menu-item-label');
		
		if (!this.isAuthorized())
			labelElement.addClassName ("disabled");
		else if (this.isCurrentTool) {
			labelElement.addClassName ("current");
		}
		return labelElement;
	},
	
	clickHandler: function (event) {
		event.stop();
		if (this.href == "#")
			return alert (this.id + " not yet implemented!");
		
		var doPopup = this.data.get('popup') && this.data.get('popup').toLowerCase() == "true"
		
		if (this.isAuthorized() || 
			confirm ("You are not authorized to access " + this.label + 
					 " - would you like to login as a different user?")) {
						 
			if (doPopup) {
				popup (this.href);
			} else if (editorMode) {
				guardedExit (this.href);
			} else {
				window.location = this.href;
			}
		}
	},
	
	/*
		check the authorization hash to see if this user has the required role. If the auth hash
		is not defined, or if there is no role for this menuItem, then return TRUE;
	*/
	isAuthorized: function () {
		return auth == null || this.role == null || auth[this.role];
	},
	

	renderSubMenu: function () {

		if (this.data.get('subMenu') && this.isAuthorized()) {
			var id = this.id + "_subMenu";
			var top = this.menu.itemHeight;
			var left = this.element.positionedOffset().left;
			var subMenu = new SubMenu (this.data.get('subMenu'), this.menu, id, top, left);
			this.subMenu = subMenu;
			
			this.element.observe ("mouseover", function (event) {
				subMenu.element.show();
			});
			this.element.observe ("mouseout", function (event) {
				subMenu.element.hide();
			});
		}
	}
});


// --- SUB MENU ---
var SubMenu = Class.create ({
	initialize: function (data, parentMenu, id, top, left) {
		this.data = $H(data);
		this.left = left;
		this.top = top;
		this.id = id;
		this.width = this.data.get('width');
		this.element = this._getElement();
		this.counter = 0;
		
		var height = this.data.get('height') || 20;
		var width = this.width;
		var table = this.element.appendChild (myCreateElement ("table"));
		
		this.items = this.data.get('items').collect ( function (subItemData, index) {
			var row = table.insertRow (index);
			var subItem = new SubMenuItem (row.insertCell (0), subItemData, width, height);
			return subItem;
		});

		$(parentMenu.parent).appendChild (this.element);
	},
	
	_getElement: function () {
		var element = myCreateElement ("div", {"id":this.id});
		
		element.addClassName ("subMenu");
		// here is where we position the submmenu
		element.setStyle ({top:this.top, left:this.left});
		element.hide();
		
 		element.observe ("mouseover", function (event) {
			element.show();
		}.bind(this));
		element.observe ("mouseout", function (event) {
			element.hide();
		}.bind(this));
		return element;
	},
	getItem: function (id) {
		return this.items.find( function (item) {
			return item.id == id;
		});
	}

});

// --- SUB MENU ITEM ---
var SubMenuItem = Class.create(MenuItem, {
	/* element is a table cell, data is a hash */
	initialize: function ( element, data, width, height ) {
		this.element = $(element);
		this.data = $H(data);
		this.id = this.data.get('id');
		this.href = this._getHref ();
		this.label = this.data.get ('label')
		this.counter = 0;
		
		this.element.setStyle ({
			height: height,
			id: this.id
		});
		if (width) {
			this.element.setStyle ({width: width});
		}
		this.element.addClassName ("subMenuItem");
		this.element.update (this.label);
		this.element.observe ("mouseover", function (event) {
			this.element.addClassName ("over");
		}.bind(this));
		this.element.observe ("mouseout", function (event) {
			this.element.removeClassName ("over");
		}.bind(this));
		this.element.observe ('click', this.clickHandler.bindAsEventListener (this));

		return this.element;
	},
	getLabelElement : function () {
		// log ("creating labelElement ...");
		var labelElement = myCreateElement ("div").update(this.label);
		labelElement.addClassName ("label");

		return labelElement;
	},

	isAuthorized: function () {
		/*
			SubMenuItems are guarded by their parent menuItem, so we only have to include
			this method to keep "clickHandler" from breaking.
		*/
		return true;
	}
	
});

function popup (url, win) {
	var features = "innerHeight=500,height=500,innerWidth=550,width=550,resizable=yes,scrollbars=yes";
	features += ",locationbar=no,menubar=no,location=no,toolbar=no";
	win = win || "_blank";
	var targetwin = window.open (url, win, features);
	targetwin.focus();
}


function menuInit (parent, currentTool) {
	// log ("Prototype Version: " + Prototype.Version);
	try {
	
	// pageMenuData is a HashMap
	pageMenu = new Menu (parent, pageMenuData, currentTool);

	} catch (error) {
		alert ("pageMenu.init: " + error);
	}
	
	return pageMenu;
}

