/* Javascript support for manipulating hierarchical controlled vocabs as expressed in Groups files */

/* a VocabNode is a piece of a hierarchical structure (i.e, VocabTree) 
   based on a hierarchical controlled vocabulary */
var VocabNode = Class.create ({
	initialize: function (name, parent, treeContainer) {
		this.name = name;
		this.parent = parent;
		this.treeContainer = treeContainer;
		this.children = $A();
	},

	getAncestors: function () {
		var ret = $A();
		var marker = this.parent;
		while (marker) {
			ret.push (marker);
			marker = marker.parent;
		}
		return ret;
	},
	getDescendants: function () {
		var descendants =  this.children.inject ([], function (acc, childNode) {
					acc.push (childNode);
					childNode.getDescendants().each (function (node) {
						acc.push (node)
					});
					return acc;
				});
		// console.log ("%d descendants for %s", descendants.size(), this.name);
		return descendants;
	},
	getSiblings: function () {
		if (this.parent) {
			return this.parent.children.inject ([], function (acc, child) {
				if (child != this) {
					acc.push (child);
				}
				return acc;
			}.bind (this));
		} else {
			return $A();
		}
	},
	getSibsAndDescendants: function () {
		var siblings = this.getSiblings();
		var ret = this.getSiblings().inject (this.getDescendants(), function (acc, node) {
			acc.push (node);
			return acc;
		});
		return ret;
	},
	
	report1: function () {
		var children = this.children.inject ([], function (acc, childNode) {
			if (childNode) {
				acc.push (childNode.name);
			}
			return acc;
		}).join (', ');
		log (this.name + " (" + children + ")");
	},
	
	report: function () {
		try {
			var dlist = this.getDescendants();
/* 			var descendants = this.getDescendants().inject ([], function (acc, childNode) {
				if (childNode) {
					acc.push (childNode.name);
				}
				return acc;
			}).join (', '); */
			log (this.name + " (" + dlist.size() + ")");
		} catch (error) {
			throw ("reporting error: " + error);
		}
	}
});
	
/* a vocab tree is a hierarchical structure containing VocabNodes */
var VocabTree = Class.create ({
	initialize: function (vocabTreeData, treeContainer) {
		this.treeContainer = treeContainer;
		this.nodeMap = $H();
		try {
			this.buildTree (vocabTreeData, null);
		} catch (error) {
			// console.log ("build tree error: " + error)
			log ("build tree error");
			log ("  " + error);
		}
	},
	getNewNode: function (name, parent) {
		var node = null;
		try {
			node = new VocabNode (name, parent, this.treeContainer);
		} catch (error) {
			throw ("couldn't create node for " + name);
		}
		this.nodeMap.set(name, node);
		return node;
	},
		
	buildTree: function (data, parentNode) {
		data = $H(data);
		var children = data.get('children');
		if (!children)
			return;
		var parentNode = this.nodeMap.get(data.get('name'));
		$A(children).each (function (childData) {
			childData = $H(childData);
			var childName = escape(childData.get('name'));
			log (childName);
			var childNode = this.getNewNode (childName, parentNode);
			if (parentNode) {
				parentNode.children.push (childNode);
			}
			this.buildTree (childData, parentNode);
		}.bind(this));
	},
	report: function () {
		this.nodeMap.each ( function (pair) {
			pair.value.report();
		});
	}
});

/* version of VocabTree that reads data from fields file */
var JsonVocabTree = Class.create (VocabTree, {
	/* vocabLayoutJson is the groups file in json form. The part of the file
	   that specifies the vocabTree starts with the "body" node */
	initialize: function (vocabLayoutJson, treeContainer) {
		if (!treeContainer)
			throw ("no tree container");
		this.treeContainer = treeContainer;
		this.nodeMap = $H();
		try {
			var body = vocabLayoutJson.opml.body;
			if (!body)
				throw "body element not found for layoutJson"
			this.buildTree (body, null);
		} catch (error) {
			// console.log ("JsonVocabTree.buildTree error: " + error)
			log ("JsonVocabTree.buildTree error (treeContainer=" + this.treeContainer.identify()+")-->");
			log ("  " + error);
		}
	},
	buildTree: function (data, parentNode) {
		data = $H(data);
		var children = data.get('outline');
		if (!children) {
			// buildTree is called recursively and bottoms out when there are no children, so 
			// this point in code is reached MANY times ...
			return;
		}
		// json represents lists of 1 as singlton objects ...
		if (children.length === undefined)
			children = [children];
		var parentNode = this.nodeMap.get(data.get('vocab'));
		$A(children).each (function (childData) {
			childData = $H(childData);
			var childName = childData.get('vocab');
			/* Don't create node if there is no 'vocab' (vocabLayout
			   will not create checkbox in this case)
			*/
			if (childName) {
				
				var childNode = this.getNewNode (childName, parentNode);
				if (parentNode) {
					parentNode.children.push (childNode);
				}
			}
			this.buildTree (childData, parentNode);
		}.bind(this));
	},
	report: function () {
		log ("JsonVocabTree");
		this.nodeMap.each ( function (pair) {
			pair.value.report();
		});
	}
});

/* smart checkboxes know when they are selected and can affect the selection of related
   nodes in a "smart" way
*/
var SmartCheckBox = Class.create (VocabNode, {
	initialize: function ($super, name, parent, treeContainer) {
		$super(name, parent, treeContainer);

		this.input = this.findInput ();
		
		if (!this.input) {
			// log ("searchName: " + searchName);
			throw ("input not found for " + this.name);
		}
		this.input.observe ('click', this.handleClick.bindAsEventListener(this));
	},
	
	/* if a checkbox value contains a quote, then the selector won't be legal. In this 
	case, loop through checkboxes and compare one-by-one to find input */
	findInput: function () {

		try {
			return this.treeContainer.select ("input[type='checkbox'][value='" + this.name + "']")[0];
		} catch (error) {
			// console.log ("couldn't find select for " + this.name + " (" + error + ")");
		}
		
		return this.treeContainer.select ("input[type='checkbox']").find (function (cb) {
				return (cb.value == this.name);
			}.bind(this));
	},
	
	handleClick: function () {
		// console.log ("%s got clicked - selected: %s", this.name, this.isSelected());

		/* if node has been selected, then make sure all ancestors are selected */
		if (this.isSelected()) {
			this.getAncestors().invoke ('select');
		}
		else {
			/* if node has been DEselected
				 1 - desect all descendants
				 2 - deselect all anscestors that don't have a selected descendants
			*/
			
			this.getDescendants().invoke('deselect');
			
			// is any sibling or Descendant selected??
			if (!this.sibOrDescSelected()) {
				this.getAncestors().each ( function (node) {
					if (!node.descendantSelected())
						node.deselect();
				});
			}
		}
	},
	/* returns true if any sibling or descendant is selected */
	sibOrDescSelected: function () {
		return this.getSibsAndDescendants().find (function (node) {
						return node.isSelected();
					 });
	},
	
	descendantSelected: function () {
		return this.getDescendants().find (function (node) {
						return node.isSelected();
					 });
	},	
	
	select: function () {
		this.input.checked = true;
	},
	isSelected: function () {
		return this.input.checked;
	},
	deselect: function () {
		this.input.checked = false;
	},
	disable: function () {
		this.input.disable();
	},
	enable: function () {
		this.input.enable();
	}
});


var SmartCheckBoxTree = Class.create (JsonVocabTree, {
	getNewNode: function (name, parent) {
		var node = null;
		try {
			node = new SmartCheckBox (name, parent, this.treeContainer);
		} catch (error) {
			throw ("couldn't create SmartCheckBox for " + name + ". Reason: " + error);
		}
		this.nodeMap.set(name, node);
		return node;
	}
});
