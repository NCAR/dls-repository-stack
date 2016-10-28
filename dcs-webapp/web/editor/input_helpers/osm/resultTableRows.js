var AstractResult = Class.create({
	initialize: function (json, searcher) {
		this.json = json;
		this.searcher = searcher;
		this.row = null;
	},

	getDom: function (columns, data) {
		this.row = $(new Element ('tr'));

		this.row.observe ('mouseover', function (event) {
			this.row.addClassName ('result-row-over');
		}.bind(this));

		this.row.observe ('mouseout', function (event) {
			this.row.removeClassName ('result-row-over');
		}.bind(this));
		this.row.observe ('click', this.clickHandler.bind(this));
		// log ("about to populate");
		this.populateRowDom (columns, data);

		return this.row;
	},

	populateRowDom: function (columns, data) {
		var msg = "AbstractResult doesn't have a meaningfull populateRowDom method";
		demoCell = new Element('td').upate (msg);
		this.row.appendChild(demoCell);
	},

	clickHandler: function (event) {
		log ("Abstract Result got a click!");
	}
});

var SearchResult = Class.create (AstractResult, {
	initialize: function ($super, json, searcher) {
		$super (json, searcher)
		this.firstName = json.firstName;
		this.lastName = json.lastName;
		this.upid = json.upid;
		this.middleName = json.middleName;
		this.email = json.email;
		this.preferredName = json.preferredName
		this.position_data = null;
		this.internalOrg = json.internalOrg;
		this.externalOrg = json.externalOrg;
		
		this.isExternal = (this.externalOrg && !this.internalOrg);
		
		this.instDivision = null;
		this.org_name = null;
		this.hierarchy_frag = null;
		
	},
	/* use fnq & lnq (first- and last_name_query) to highlight the results */
	populateRowDom: function (columns, data) {
		// log ("SearchResult.populateRowDom");
		var fnq = data['fnq']
		var lnq = data['lnq']
		// log ("fnq: " + fnq);
		$A(columns).each ( function (col) {
			var col_id = col[0];
			// log (' col_id: ' + col_id);
			var value = $H(this.json).get (col_id) || "&nbsp";
			if (col_id == 'firstName' && fnq)
				value = this.highlight (value, fnq);
			if (col_id == 'lastName') {
				value = this.highlight (value, lnq);
			}
			this.row.appendChild (new Element ('td').update (value));
		}.bind(this));
	},

	/* show the position_data for this searchResult */
/* 	updatePositions: function () {
		// log ("updatePositions!");

		// cache the position data for this SearchResult
		if (this.position_data == null) {

			var peopleDBUrl = PEOPLE_DB_BASE_URL + '/internalPersons/' + this.upid;

			peopleDBProxy (this.searcher.action, peopleDBUrl, function (json) {
				try {

					log ("proxy just returned data from " + peopleDBUrl);

					// here is where we could Instantiate an OrgPosition class
					this.position_data = $A(json.positions).sortBy (function (position) {
						return position.startDate;
					});
					log ("calling displayPositions from updatePositions (" + this.position_data + "");
					this.searcher.positionMgr.displayPositions(this.position_data);

				} catch (error) {
					log ("updatePositions error: " + error);
				}
			}.bind(this));
		}
		else {
			// this.updatePositions();
			this.searcher.positionMgr.displayPositions(this.position_data);
		}

	},
 */

	/* highlight all occurances of 'sub' in 'text' */
	highlight: function (text, sub) {
		if (!sub)	return text;
		return text.gsub (new RegExp(sub,'i'), function (match) {
			return '<span style="font-weight:bold;color:#000099">' + match[0] + '</span>';
		});
	},

	/* click handler for this row */
	clickHandler: function (event) {
		// log ("handleSearchResult Click");
		this.searcher.selectedPerson = this;
		this.searcher.searchResults.each (function (result) {
			if (result == this)
				result.row.addClassName ('result-row-selected');
			else
				result.row.removeClassName ('result-row-selected');
		}.bind(this));
		this.getInstDivision(function () {
			log ("InstDivision: " + this.instDivision);
			this.searcher.doneButton.enable();
		}.bind(this));
	},
	
	getInstDivision: function (callback) {
		// log ("SearchResult: getInstDivision for " + this.internalOrg);
		if (!this.internalOrg) {
			callback();
			return;
		}
		this.org_name = null;
		this.hierarchy_frag = null;
		this.searcher.doneButton.disable()
		this.getMyOrg();
		this.getHierarchy();

		new PeriodicalExecuter(function(pe) {
			if ( this.org_name == null || this.hierarchy_frag == null) {
				// log ("waiting for this.hierarchy_frag and this.org_name");
			}
			else {
				pe.stop();
				
				// log ('orgName: ' + this.org_name + ',  hierarcy: ' + this.hierarchy_frag);
				// Historical orgs will not have a hierarchy
				if (this.hierarchy_frag)
					this.instDivision = this.hierarchy_frag + ':' + this.org_name;
				else
					this.instDivision = this.org_name;
				
				// KLUDGE to compensate for difference between OSM and peopleDB
				this.instDivision = this.instDivision.sub ("NCAR Directorate", "NCAR Director's Office");
				
				if (this.instDivision == "ISSE INACTIVE (ISSE -- x)")
					this.instDivision = "Institute for the Study of Society and Environment (ISSE)"
				
				// log ('instDivision: ' + this.instDivision);
				
				callback()
			}
		}.bind(this), 0.3);		
	},
	getHierarchy: function () {
		// PeopleDB API - https://wiki.ucar.edu/display/peopledb/People+REST+API
		// log ('SearchResult: getHierarchy()');
		var peopleDB2Url = PEOPLE_DB_BASE_URL + '/orgHierarchy?org='+this.internalOrg;
		peopleDBProxy (this.searcher.action, peopleDB2Url, function (json) {

			// log ("looking at " + $A(json).size() + " items");

			var parents = $A(json).inject ([], function (acc, org) {
				var octalLevelCode = parseInt (org.levelCode)
				// log (org.name + " - " + octalLevelCode + " (" + org.levelCode + ") - " + org.acronym);

				// directorate is octal 320 - we have to skip this orgLevelCode!?
				// also skip UCARADMIN (256)
				if (org.acronym != 'NCARDIR' && org.acronym != 'UCARADMIN') {
					// log (" - keeping " + octalLevelCode);
					acc.push (org);
				}
				return acc;
			}).sortBy (function (org) {
				return parseInt (org.levelCode);
			});

			// sanity check
			// log (parents.size() + ' parents');
			this.hierarchy_frag = parents.map (function (org) {
				var vocab = org['name'] + ' (' + org['acronym'] + ')';
				// log (vocab);
				return vocab;
			}).join(':');

		}.bind(this));
	},

	//https://people.api.ucar.edu/orgs/
	getMyOrg: function () {
		// log ("SearchResult: getMyOrg: internalOrg: " + this.internalOrg);
		var peopleDB2Url = PEOPLE_DB_BASE_URL + '/orgs/'+this.internalOrg.sub(' ', '%20', 10);
		peopleDBProxy (this.searcher.action, peopleDB2Url, function (json) {
			var orgName = json.name;
			var acronym = json.acronym;
			
			/* KLUDGE alert - 9/26/13
			   the peopleDbAcronymsToAdjust below are for orgs that are listed in instDivison schema as "Groups" but
			   have been changed in peopleDB to remove "Group" from name and acronym. E.g., 
			   - instDivision: Physical Meteorology Group (PMG)
			   - peopleDb:     Physical Meteorology (PM)
			   
			   Here we adjust from peopleDB form to instDivision form so the proper checkbox can
			   be checked in the UI:
			*/   
			
			var peopleDbAcronymsToAdjust = ['BLT', 'DA','MD','MP','PM','PD','SM']
			
			if (peopleDbAcronymsToAdjust.indexOf(acronym) != -1) {
				orgName += " Group";
				acronym += "G"
				log ("- CHANGED \"" + json.name + "\" to \"" + orgName + "\"");
			}
			
			// a table of mappings, {<peopleDB> : <instDiv>}
			var kludgeMapping = {
				'Computational Math Group' : 'Computational Mathematics Group',
				'Boundary Layer & Turbulence Group' : 'Boundary Layer and Turbulence Group',  
				// note: modifies the orgname modified above! needed because peopleDB '&' convention not consistent
				'Atmospheric Modeling and Predictability' : 'Atmospheric Modeling and Prediction',
				'Reg. Integrated Sciences Collective' : 'Regional Integrated Science Collective',
				'Regional & Process Modeling' : 'Regional and Process Modeling'
			}
			
			if (kludgeMapping[orgName]) {
				orgName = kludgeMapping[orgName]
				log ("- CHANGED orgname to " + orgName);
			}
			
			this.org_name = orgName + ' (' + acronym + ')';
			
		}.bind(this));
	}

});

