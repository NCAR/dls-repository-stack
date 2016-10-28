<style type="text/css">

.small_text {
	font-size:10px;
	margin-top:3px;
}

iframe.helper-frame {
	width:800px;
	height:450px;
}

#uploadHelperContainer {
	position:absolute;
	top:0px;
	z-index:111;
	width:100%;
	height:100%;
	text-align:center;
}

/* in effect, the viewport for the input helper */
#helperInputDisp {
	position:relative;
	top:0px;
	width:800px;
	margin:20px auto 20px auto ;
	border:thin blue solid;
	background-color:#eaeafb;
	padding:20px;
}

#modalBackground {
	position:absolute; 
	top:0px; 
	left:0px;
	height:5000px; 
	width:100%;
	z-index:110;
	filter:alpha(opacity=70);-moz-opacity:.70;
	opacity:.70; 
	background-color:black;
}
</style>

<div id="upload_helper_${id}" style="margin-left:25px;">
	
	<input type="button" id="change-${id}" value="change" />

	<c:if test="${false}">
		<div style="border:thin blue dotted">	
			<div class="debug">widget</div>
<%-- 			<div class="small_text">id: ${id}</div>
			<div class="small_text">xpath: ${sf:idToPath (id)}</div> --%>
			<div class="small_text">recId: ${sef.recId}</div>
			<div class="small_text">collection: ${sef.collection}</div>
			<div class="small_text">existingFileName: <span id="debug-filename"></span></div>
			<div class="small_text">action: <span id="debug-action"></span></div>
		</div>
	</c:if>
</div>

<%-- <script type="text/javascript" src="${contextPath}/lib/javascript/prototype.js"></script> --%> 
<script type="text/javascript">
	
if (typeof (Widget) === 'undefined') {
	
	var widgetRegistry = {};
	
	function getWidget(widgetId) {
		return widgetRegistry[widgetId];
	}
	
	var Widget = Class.create ( {
		initialize: function (id) {
			// log ("WIDGET: " + id);
			this.id = id;
			this.uploadHelperContainer = null;
			this.helperInputDisp = null;
			this.iFrameId = null;
			this.trigger = $("change-" + this.id);
			var read_only_el = $(id+'-readonly');
			if (!read_only_el)
				throw ("read_only_el NOT FOUND"); 
			
			this.input = read_only_el.down("input[type='hidden']");
			if (!this.input)
				throw ("INPUT not found for " + this.id);
			
			this.input.writeAttribute ("autocomplete", "off");
			
			this.display = read_only_el.down('div.filename');
			if (!this.display)
				throw ("DISPLAY not found for " + this.id);
			
			$('change-'+ this.id).observe ('click', function (event) {
				// this.activate(event);
			}); 
			
			
			var url = $(this.input).value.trim();
			this.updateDisplay(url);

			
			$('change-'+ this.id).observe ('click', this.activate.bind(this)); 			
			this.escapeHandler = document.on('keyup', this.handleKeyUp.bind(this));
			this.escapeHandler.stop()			
		},
		
		updateDisplay: function (url) {
			log ("Update display - " + url);
			this.display.update(url);
			if (this.isProtectedUrl(url)) {
				var collection = this.getAssetDir(url);
				var filename = this.getAssetFilename (url);
				this.display.insert (this.downloadButton(collection, filename));
			}
			if (this.input.value == null || this.input.value.strip().empty())
				this.trigger.value = "Add Url";
			else
				this.trigger.value = "Change";
				
		},
		
		activate: function (event) {
			log ("ACTIVATE");
		
			this.getModalBackground().show();
			
			this.uploadHelperContainer = $('uploadHelperContainer');
			this.helperInputDisp = $('helperInputDisp');
			if (!this.uploadHelperContainer || !this.helperInputDisp) {
				this.helperInputDisp = new Element('div', {id:'helperInputDisp'});
				this.uploadHelperContainer = new Element('div', {id:'uploadHelperContainer'})
					.update(this.helperInputDisp);
				document.body.insert(this.uploadHelperContainer);
			}
			
			// log ("document.scrollOffsets: " + document.viewport.getScrollOffsets());
			this.helperInputDisp.setStyle({top:document.viewport.getScrollOffsets().top+'px'});
			
			this.iFrameId = "uFrm" + new Date().getTime().toString();
			var iframe = new Element('iframe', {
									id:this.iFrameId, 
									name:this.iFrameId,
									scrolling: 'no',
									frameborder: 'no'
								})
						.addClassName ('helper-frame');
						
			iframe.update (new Element('p').update (
				"It appears that your browser does not support iframes, which is needed " +
				"for the upload function. " + 
			   "Please make sure iframes are not disabled in your browser or by your " +
			   "personal firewall/security software."))
			
			this.helperInputDisp
				.update(iframe)
				.show();
				
			this.uploadHelperContainer.show();
				
			this.escapeHandler.start();
				
			log (" - about to Load iFrame");
			this.loadIFrame ();
		},
			
		loadIFrame: function () {
			log ("loadIFrame()");
			var existingAssetPath = this.input.value.strip();
			// var existingFileName = existingAssetPath ? existingAssetPath.split('/').last() : '';
			// log ('existingFileName: ' + existingFileName);
							
			var action = "helper";
							
			try { 
				$("debug-filename").update (existingFileName);
				$("debug-action").update (action);
			} catch (error) {}
			
			var iframeParams = {
				action : action,
				frameId : this.iFrameId,
				recordId : "${sef.recId}",
				widgetId : this.id,
				// existingFileName : existingFileName,
				existingAssetPath : existingAssetPath,
				// sizeDisplay : "${sizeDisplay}",
				collection : "${sef.collection}"
			}
			
			var iframeSrc = contextPath + "/editor/upload.do?" + $H(iframeParams).toQueryString();
			log ("iframeSrc: " + iframeSrc);
			if (true)
				$(this.iFrameId).src = iframeSrc
			else
				log ("- would have loaded iframe with " + iframeSrc);
		},
		
		getModalBackground: function () {
			var modal_background = $('modalBackground');
			if (!modal_background) {
				modal_background = new Element('div', {id:'modalBackground'})
						.setStyle({display:'none'});
				document.body.insert (modal_background);
			}
			if (!modal_background)
				throw ("modal_background not found nor created");
			return modal_background;
		},
	
	
		doUploadHelperComplete: function (fileName, collection) {
			collection = collection || "${sef.collection}";
			log ('! doUploadHelperComplete() fileName: ' + fileName);
			if (fileName != null) {
				if (!this.input) // where we will write new url
					alert ("input not found for id: " + this.id);
				
				var url;
				var url_pat = /.*\:\/\//;
				if (fileName == "" || url_pat.exec(fileName)) {
					log (' url pat matched');
					url = fileName;
				}
				else
					url = "http://ccs.dls.ucar.edu/home/protected/" + collection + "/" + fileName;
				log (' - url: ' + url);
				this.input.value = url;
				// this.display.update(url);
				this.updateDisplay(url);
			}
			
			
			this.modal_close();
		},
		
		modal_close: function() {
			this.escapeHandler.stop();
			this.helperInputDisp.hide();
			this.uploadHelperContainer.hide();
			this.getModalBackground().hide();
		},
		
		handleKeyUp: function(event) {
			log ("KEYUP");
			if (event.keyCode && event.keyCode == 27) {
				this.modal_close();
				return;
			}
		},
		
		downloadButton: function (collection, filename) {
			var asset_url = contextPath + '/content/' + collection + '/' + filename;
			// log (' - asset_url: ' + asset_url);
			
			var img = new Element('i')
				.addClassName('fa fa-download fa-lg')
				.setStyle({paddingLeft:'10px', color:'#666666', verticalAlign:"-2px"})
				.observe('mouseover', function (event) { 
					event.element().setStyle({color:'blue'})
				})
				.observe('mouseout', function (event) { 
					event.element().setStyle({color:'#666666'})
				});
			
			return new Element('a', {
										href:asset_url, 
										target:'_blank',
										title:'download this asset'
									})
									.update(img)
									.addClassName('asset-link');
		},
		
		downloadButtonStacked: function (collection, filename) {
			var asset_url = contextPath + '/content/' + collection + '/' + filename;
			// log (' - asset_url: ' + asset_url);
			
			var stack = new Element('span')
					.addClassName('fa-stack fa-lg')
					.setStyle({paddingLeft:'10px', color:'#666666',fontSize:"1.2em"})
					.observe('mouseover', function (event) { 
						event.findElement('.fa-stack').setStyle({color:'blue'})
					})
					.observe('mouseout', function (event) { 
						event.findElement('.fa-stack').setStyle({color:'#666666'})
					})
					.insert (new Element('i').addClassName('fa fa-square-o fa-stack-2x'))
					.insert (new Element('i').addClassName('fa fa-download fa-stack-1x'));
			
				
				
			
			return new Element('a', {
										href:asset_url, 
										target:'_blank',
										title:'download this asset'
									})
									.update(stack)
									.addClassName('asset-link');
		},
		
		isProtectedUrl: function (url) {
			return url.startsWith("http://ccs.dls.ucar.edu/home/protected/");
		},
		
		getAssetDir: function (url) {
			if (this.isProtectedUrl (url))
				return url.split('/').splice(-2,1);
			else
				return null;
		},
		
		getAssetFilename: function (url) {
			return url.split('/').splice(-1,1);
		}		
		
		
	});
}

document.observe ('dom:loaded', function () {
		
		widgetRegistry["${id}"] = new Widget ("${id}");
		// log (' - ' + $H(widgetRegistry).size() + ' widgets registered');
});

</script>

