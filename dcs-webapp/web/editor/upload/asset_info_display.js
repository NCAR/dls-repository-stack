/**
@module upload_helper_script
@submodule aset_info_display
*/
/**
@class global functions
*/

/**
@return the selected asset radio input
@method getSelectedAsset
@static
*/
function getSelectedAsset () {
	return $('upload-tool').select('input[type=radio].asset-selector').find (function (radio) {
				return radio.checked;
			});
}

/**
@return the collection corresponding to the selected asset if there is one
@method getSelectedCollection
@static
*/
function getSelectedCollection () {
	var button = getSelectedAsset();
	if (!button) {
		log ("button not found");
		return null;
	}
	else
		return button.value;
}

/**

@class Asset
*/
var Asset = Class.create({
		
	initialize: function (assetInfo) {
		// log ('Asset');
		// log (JSON.stringify(assetInfo, null, 2));
		this.assetInfo = assetInfo;
		this.isVirtual = isNaN(assetInfo.size);
		this.collection = this.assetInfo.collection;
		this.records = this.assetInfo.records;
		var collectionInfo = getCollectionInfo(this.collection);
		if (!collectionInfo)
			throw ("collectionInfo NOT FOUND for asset directory: " + this.collection);
		this.collectionName = collectionInfo.name;
		this.fileName = ASSET_MANAGER.upload.fileName
	},
	
	render: function (selected) {

		// log ("Asset.render() = " + this.collection);
		// log (" -- selected: " + selected);

		var container = new Element ('div', {id : this.collection})
				.addClassName ("asset");
				
		var collectionName = new Element('div')
				.addClassName('collection-name')
				.update(this.collectionName);
				
		var radio = new Element('input', {type:'radio', name:'selected-asset', value:this.collection})
							// .setStyle({float:'left'})
							.addClassName('asset-selector')
							.observe('click', handleAssetSelector);	
		
		if (typeof (selected) !== 'undefined') {
			if (selected)
				radio.checked = selected;
		}
		else if (this.collection == ASSET_MANAGER.upload.collection) {
			radio.checked = true;
		}
		
		var details;
		try {
			if (false && isNaN(this.assetInfo.size)) {
				// there are no details because this is an empty collection
				log ("FOUND EMPTY ASSET");
			}
			else {
				details = this.renderDetails();
			}
		} catch (error) {
			log ("renderDetails ERROR: " + error);
		}
		
		try {
			records = this.renderRecords ();
		} catch (error) {
			log ("renderRecords ERROR: " + error);
		}
		
		if (!this.isVirtual) {
			// for virtual assets there is no file to download
			// container.insert (renderDownloadButton(this.collection, this.fileName));
			var downloadIcon = createStackedIcon ("fa-download", function (event) {
					var asset_url = contextPath + '/content/' + this.collection + '/' + this.fileName;
					window.open(asset_url, '_blank');
				}.bind(this), {title: "Download this asset"});
			downloadIcon.addClassName('asset-link');
			downloadIcon.setStyle({fontSize:'1.3em'});
			container.insert (downloadIcon);
		}
		
		return container
			.insert(radio)
			.insert(collectionName)
			.insert(details)
			.insert(records);
	},
	
	renderDetails: function() {
		// log ('renderDetails()');
		var details = new Element('div').addClassName('details');
		
		var lastMod = moment(this.assetInfo.lastModified);
		
		// these items are displayed as detail cells 
		var content = [
			['Asset dir', this.collection],
		]
		
		if (!this.isVirtual) {
			content = content.concat ([
					['Size' , formatFileSize (this.assetInfo.size)],
					['Last Modified', lastMod.format(modDateFormat)]
			]);
		}
		
		content.each (function (detail, i) {
				var label = detail[0];
				var value = detail[1];
				var fmt_label = "";
				if (label)
					fmt_label = '<span class="small-ui-label">'+label+":</span> ";
				if (value)
					fmt_label += value;
			details.insert (new Element('div')
								.addClassName('detail')
								.update (fmt_label));
			if (i < content.length - 1)
				details.insert (new Element ('span')
					.update ('|')
					.addClassName ('vert-divider'));
			
		});
		return details;		
	},
	
	renderRecords: function () {
		var recordList = new Element('ul')
			.setStyle({margin:'0'});
			
		// Do we want to sort records?
			
		if (this.isVirtual) {
			recordList.insert (new Element('li')
				.update ("This is the asset collection for the current metadata record. There is " +
						 "currently no asset matching the file to upload.")
				.setStyle({fontStyle:'italic', fontSize:"90%"}));
		}
		else if (this.records && this.records.length > 0) {
			// log ("about to render " + this.records.length + " records");
			this.records.each (function (recordInfo) {
				if (false) { // the old way
					var recordIdText = recordInfo.recordId;
					if (recordInfo.recordId == recordId)
						recordIdText = '<span style="font-weight:bold;">' + recordIdText + '</span>';
					var entry = recordIdText + ' - ' + recordInfo.collection;
					var recordItem = recordList.appendChild (new Element('li').update (entry).setStyle({fontSize:'90%'}));
				} else { // NEW
					
					var recordDisplay = new Element ('span').update (recordInfo.recordId).addClassName ('record');
					if (recordInfo.recordId == recordId)
						recordDisplay.addClassName('current');
					var recordItem = recordList.appendChild (new Element('li')
																.update (recordDisplay)
																.addClassName('record-list-item'));
				}
			});
		}
		else {
			log ("empty");
			recordList.insert (new Element('li')
				.update ("no metadata records catalog this asset")
				.setStyle({fontStyle:'italic'}));
		}
		
		return recordList;		
	}
			
});

/**

@class AssetInfoDisplay
*/
var AssetInfoDisplay = Class.create({
		
		initialize: function (jsonAssetInfo) {
			// log ("AssetInfoDisplay");
			this.json = jsonAssetInfo
			this.assetFiles = this.json.assetFiles || []
			this.assetRecords = this.json.assetRecords || []
			
			// group records by their URL collection
			this.assetRecordMap = this.getAssetRecordMap();

			if (false) {				
				log ("ASSET_RECORD_MAP");
				log (JSON.stringify (this.assetRecordMap, null, 2));
			}
			
			this.assets = this.getAssetList();
			if (false) {
				log ("ASSETS");
				log (JSON.stringify (this.assets, null, 2));
			}
			
			this.container = $('assetInfo').update();
			
			// log ("creating assetObjs");
			this.assetObjs = this.assets.inject ([], function (acc, assetInfo) {
					try {
						acc.push(new Asset(assetInfo));
					} catch (error) {
						log ("Asset WARNING: " + error);
					}
					return acc;
			});
			
			if (this.assetObjs.length == 0)
				return;
			
			// if there isn't an asset for ASSET_MANAGER.collection (the metadata collection) then
			// make a fake Asset (with no metadata) for ASSET_MANAGER.collection
			
			
			/*
				make sure there is a way for user to select "record's collection"
				 - i.e., the collection in which the current record resides
				   as opposed to asset collection, which is where the file resides
				
				if an asset does not exist in the home collection, make a temporary
				one so user can choose.
				
				but a choice isn't necessary if this asset doesn't exist, yet!
				
			*/
			// log ("finding homeAsset for ASSET_MANAGER.collection: " + ASSET_MANAGER.collection);
			var homeAsset = this.assetObjs.find(function (asset) {
					return asset.collection == ASSET_MANAGER.collection;
			});
			
			if (homeAsset)
				log ("homeAsset found");
			
			if (!homeAsset) {
				try {
					var homeInfo = getCollectionInfo(ASSET_MANAGER.collection);
					var homeAssetInfo = {
						collection : homeInfo.key,
					}
					// log ("instantiating virtual asset");
					// log (JSON.stringify(homeAssetInfo, null, 2));
					this.assetObjs.push(new Asset (homeAssetInfo));
				} catch (error) {
					log ("ERROR: " + error);
				}
			}
				
			this.render();
			
		},
		
		
		/**
		render the assetInfoDisplay
		
		Should be called only when user input is required to determine in which collection this
		asset will be uploaded.
		@method render
		*/
		render: function () {
			log ("AssetInfoDisplay RENDER");
			log (" - ASSET_MANAGER.upload.collection " + ASSET_MANAGER.upload.collection);
					
			if (this.assetObjs.length > 1) {
				this.container
					.update( new Element('span')
						.setStyle({fontWeight:'bold'})
						.update ('Assets with this name exist in other collections - '))
					.insert (new Element ('span')
						.addClassName('tool-blurb')
						.update('Please choose a destination collection for the uploaded asset. ' + 
								'Note: an existing asset in the destination will be overwritten.'));
			}
			else {
				this.container
					.update( new Element('span')
						.setStyle({fontWeight:'bold'})
						.update ('An asset with this name exists in '+ ASSET_MANAGER.upload.collection + ' - '))
					.insert (new Element ('span')
						.update('The existing asset will be overwritten.')
						.addClassName('tool-blurb'))
			}
			var assetChoice_el = $('assetInfo').appendChild( new Element('div', {id:'assetChoices'}));
			
			// sort collections alpha, but put current collection up top (why?);
			var sorted_assets = this.assetObjs.sortBy (function (asset) {
					// log (" -  " + asset.collection)
					if (asset.collection == ASSET_MANAGER.upload.collection)
						return 'aaaaaa';
					else
						return asset.collection.toLowerCase();
			});
			
			log ("rendering sorted assets");
			var selectedAssigned = false;
			sorted_assets.each (function (asset, i) {
				// log (" - collection: " + asset.collection);
				var selected = false;
				if (i == 0 && !asset.isVirtual && asset.collection == ASSET_MANAGER.upload.collection) {
					selected = true;
					// log (" - selected assigned true");
				}
				else if (!selectedAssigned) {
					selected = sorted_assets.length == 1 || i == 1;
				}
				selectedAssigned = selected;
				// log (" -- " + asset.collection + '(i: ' + i + ') - selected: ' + selected) ;
				try {
					assetChoice_el.insert(asset.render(selected));
					
				} catch (error) {
					log ('Asset render ERROR: ' + error);
				}
			}.bind(this));
		},
		
		
		getAssetList: function () {
			// log ("getAssetList");
			var assetInfos = 
				this.assetFiles.map(function (fileInfo) {
					var collection = fileInfo.collection;
					// log ("- collection: " + collection);
					try {
						var records = this.assetRecordMap[collection];
						
						// records = records.sortBy (function (recordInfo) { })
						
						fileInfo['records'] = this.assetRecordMap[collection];
					} catch (error) {
						log ("whoops: " + error);
					}
					return fileInfo;
				}.bind(this));
				
			return assetInfos;
		},			
		
		/**
		Groups metadata records by the collection used in the assetUrl. For example, if
		a metadata record in collection dpc_tips has a url ending in /pbs-tips/myAsset.pdf, then
		this record is associated with pbs-tips, not dps_tips, collection.
		
		returns a mapping from collection to records as described above
		
		@method getAssetRecordMap
		*/
		getAssetRecordMap: function () {
			return 	this.assetRecords.inject({}, function (acc, recordInfo) {
					
				// find a url for this asset and then get its collection
				var urls = recordInfo.urls || []
				urls = urls.filter (function (url) {
					return getAssetFilename(url) == ASSET_MANAGER.upload.fileName;
				});
				
				// log ('  - ' + urls.length + ' urls found');
				
				urls.each (function (url) {
					var collection = getAssetDir(url);
					// log ("collection: " + collection)
					try {
						var reclist = acc[collection] || []
						reclist.push(recordInfo)
						acc[collection] = reclist;
					} catch (error) {
						log ("ERROR: " + error);
					}
				});
				return acc;
			});
		}
});
							



function handleAssetSelector(event) {
	log ("handleAssetSelector() - " + event.element().value);
}

	

