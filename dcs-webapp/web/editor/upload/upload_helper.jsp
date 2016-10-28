<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<html>
	<head>
		<c:import url="head.jsp"/>
		<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
		
		<script type="text/javascript">
		
		var recordId = "${uploadForm.recordId}";
		var protectedDir = "${initParam.primaryContentPathOnServer}";
			
		var COLLECTIONS = []
		// convert setInfos in uploadForm.assetCollections
		
		<c:forEach var="setInfo" items="${uploadForm.userAssetCollections}">
		
		COLLECTIONS.push ({name:"${setInfo.name}", 
						   key:"${setInfo.setSpec}", 
						   size:"${setInfo.numIndexed}", 
						   xmlFormat:"${setInfo.format}"});
		</c:forEach>
		log (COLLECTIONS.length + " assetCollections");
		
		// global utils
		function isProtectedUrl (url) {
			return url.startsWith("http://ccs.dls.ucar.edu/home/protected/");
		}
		
		function getAssetDir (url) {
			if (isProtectedUrl (url))
				return url.split('/').splice(-2,1);
			else
				return null;
		}
		
		function getAssetFilename (url) {
			return url.split('/').splice(-1,1);
		}
		
		function makeItemKey(filename, collection) {
			return filename + '|' + collection;
		}
		
		/**
		should replace makeItemKey for all assets makeAssetKey
		
		NOTE: collection | filename
		*/
		function makeAssetKey(collection, filename) {
			if (filename.indexOf('.') == -1)
				log ("WARN makeAssetKey got bogus FILENAME? " + filename);
			return collection  + '|' +  filename;
		}
		
		function getCollectionInfo (key) {
			return COLLECTIONS.find (function (info) {return info.key == key});
		}
		
		function formatFileSize (bytes) {
			var fileSize = 0;
			if (bytes > 1024 * 1024)
				fileSize = (Math.round(bytes * 100 / (1024 * 1024)) / 100).toString() + 'MB';
			else
				fileSize = (Math.round(bytes * 100 / 1024) / 100).toString() + 'KB';
			return fileSize;
		}
		
		function createStackedIcon (fa_class, callback, args) {
			var eventname = args && args.eventname ? args.eventname : 'click';
			var background_class = 'fa-square-o';
			if (args && args.background_class)
				background_class = args.background_class
			var icon = new Element('span')
				.addClassName("am-stacked-icon fa-stack fa-lg")
				.insert (new Element('li').addClassName("fa " + fa_class + " fa-stack-1x"))
				.insert (new Element('li').addClassName("fa " + background_class + " fa-stack-2x"))
				.observe ('mouseover', function(event) {
					var icon = event.findElement('.fa-stack').addClassName('over');
				})
				.observe ('mouseout', function(event) {
					var icon = event.findElement('.fa-stack').removeClassName('over');
				})
				
				.observe (eventname, callback);
				
			if (args && args.title)
				icon.writeAttribute("title", args.title);
			return icon;
		}
		
	/* 	function renderDownloadButton (collection, filename) {
			var asset_url = contextPath + '/content/' + collection + '/' + filename;
			// log (' - asset_url: ' + asset_url);
			
			var img = new Element('i')
				.addClassName('fa fa-download fa-lg')
				.observe('mouseover', function (event) { 
					event.element().addClassName('fa-over') 
				})
				.observe('mouseout', function (event) { 
					event.element().removeClassName('fa-over');
				});
			
			return new Element('a', {
										href:asset_url, 
										target:'_blank',
										title:'download this asset'
									})
									.update(img)
									.addClassName('asset-link');
		}
	 */	
		var modDateFormat = 'M/D/YY h:mm a';

		</script>
		<script type="text/javascript" src="${contextPath}/editor/upload/upload_helper_script.js"></script>
		<script type="text/javascript" src="${contextPath}/editor/upload/collections_selector.js"></script>
		<script type="text/javascript" src="${contextPath}/editor/upload/asset_info_display.js"></script>
		<script type="text/javascript" src="${contextPath}/editor/upload/orphan_info_display.js"></script>
		
	</head>
	<body class="uploadBody">
<c:set var="maxFileNameLen" value="34"/>
<c:set var="fileNameDisp">
	<c:choose>
		<c:when test="${fn:length(fileName) > maxFileNameLen}">
			${fn:substring(fileName,0,3)}...${fn:substring(fileName,fn:length(fileName)-(maxFileNameLen-4),fn:length(fileName))}	
		</c:when>
		<c:otherwise>${fileName}</c:otherwise>
	</c:choose>
</c:set>

<c:set var="recordId" value="${uploadForm.recordId}"/>

<c:if test="${false}">
	<div class="small_text"><b>upload_helper.jsp</b></div>
	<div class="small_text">action: "${uploadForm.action}"</div>
	<div class="small_text">collection: "${uploadForm.collection}"</div>
	<div class="small_text">recordId: "${recordId}"</div>
	<div class="small_text">contextPath: "${contextPath}"</div>
	<div class="small_text">existingAssetPath: "${uploadForm.existingAssetPath}"</div>
</c:if>

<div id="upload-helper">

<div id="helper-banner">
	<div class="closer upper-right-closer" ><li class="fa fa-times-circle fa-lg"></li></div>
	
	<div style="font-size:11px;float:right;">
		<div>record collection: ${uploadForm.collection}</div>
		<div>recordId: ${uploadForm.recordId}</div>
		<%-- <div>protectedDir: ${initParam.primaryContentPathOnServer}</div> --%>
	</div>
		
	<h1>Asset Manager</h1>
	<div id="current-value">Current Value:
	
		<c:choose>
			<c:when test="${false && empty uploadForm.existingFileName}">Undefined</c:when>
			<c:when test="${true || not fn:contains(uploadForm.existingAssetPath, 'protected')}">
				${uploadForm.existingAssetPath}
			</c:when>
			<c:otherwise>
				${uploadForm.collection}/${uploadForm.existingFileName}
			</c:otherwise>
		</c:choose>
			
	</div>

</div>

<div style="display:none;">
		<div>assetCollections</div>
		<c:forEach var="setInfo" items="${uploadForm.userAssetCollections}">
		<div>${setInfo.name}</div>
		</c:forEach>
</div>

<div id="helper-body">

<ul id="tool-controls">
		<li class="ctrl">
			<button class="toc-button" title="Select an existing file" id="file-select-button">Select</button>
		</li>
		<li class="ctrl">
			<button class="toc-button" title="Upload a local file" id="upload-button">Upload</button>
		</li>
		<li class="ctrl">
			<button class="toc-button" title="Enter a link to a web resouce" id="weblink-button">Weblink</button>
		</li>
		<li class="ctrl">
			<button class="toc-button" title="Delete or rename asset files" id="delete-rename-button">Delete / Rename</button>
		</li>
</ul>

<div id="helper-tools">
	<div id="collections-display" style="display:none;"></div>
	<div id="file-select-tool" class="helper-tool" style="display:none;">
	
		<%-- <div id="collections-toggle" class="ui-button">Collections</div> --%>
	
		<%-- <h2 style="margin:0 0 7px;font-weight:normal;padding:0;">Select a File</h2> --%>
		
		<%-- SELECT-FILES-toolbar --%>
		<ul id="select-files-toolbar">
			<li id="filter-tool"><span class="small-ui-label">Filter</span>
				<input type="text" size="20"
					   id="select-filter"
					   name="select-filter" 
					   value="" />
<%-- 				<div id="filter-clear" title="Clear filter"
					 class="closer" style="display:none;">
				<img top="5px" src="${contextPath}/images/circle-closer.png" /></div> --%>
				
				<%-- <div id="filter-clear" class="closer" style="display:none;"><i class="fa fa-times-circle"></i></div> --%>
				<i id="filter-clear" class="fa fa-times-circle fa-2x closer" style="display:none;"></i>
				
			</li>
			
			<li id="match-widget" style="display:none;">
				<%-- <span class="small-ui-label">Match</span> --%>
				<span id="match-spec-start" class="ui-button match-spec-button">Match start</span>
				<span id="match-spec-any" class="ui-button match-spec-button">Match anywhere</span>
			</li>
			<li>
				<div id="collections-toggle" class="ui-button">
					<i id="coll-toggle-icon" class="fa fa-caret-right fa-1x" style="font-size:1.5em">
					</i>Collections</div>
			</li>
				
		</ul>
		
		<%-- VIEW-STATUS --%>
		<div id="view-status-msg">Viewing 
			<span id="filter-status" style="display:none;"><span id="shown_count"></span> of</span>
			<span id="file-count"></span> files in 
			<%-- <a href="javascript:void(0);" class="ui-button" id="collection-display-trigger"><span id="collection-count"></span> collections</a> --%>
			<span id="collection-display-trigger"><span id="collection-count"></span> collections</span>
			<span id="single-collection-name"></span>
		</div>
		
		<%-- <div id="search_results"></div> --%>
		<ul id="search_results"></ul>
		
		
		<%-- COLLECTIONS SELECTOR - gets placed absolutely --%>
		<div id="file-select-collections" class="" style="display:none;">
			<div class="closer upper-right-closer" style="display:none;"><li class="fa fa-times-circle"></li></div>
			<%-- <li class="closer upper-right-closer fa fa-times-circle fa-1x" style="display:block;"></i> --%>
			
			<h2 style="margin:0 0 7px;font-weight:normal;padding:0;display:none;">Select Collections</h2>
			
			<div id="collections_list">
				<select id="selected-collections" name="selected-collections" multiple></select>
			</div>
		</div>
			
	</div>
	
	<%-- UPLOAD TOOL --%>
	<div id="upload-tool" class="helper-tool" style="display:none">
	
		<html:form styleId="fileInputForm" enctype="multipart/form-data" 
			  method="post" action="/editor/upload.do">
			<input type="hidden" name="action" value="upload"/>
			<html:hidden property="recordId" value="${uploadForm.recordId}"/>
			<html:hidden property="collection" value="${uploadForm.collection}"/>
			<html:hidden property="existingFileName" value="${uploadForm.existingFileName}"/>
			
			<c:set var="vertDivider"><span class='vert-divider'>|</span></c:set>
			
			<div id="file-selected-info" style="display:none;">
				<%-- Displayed only when there is a selected file --%>
				<div class="header">
					<div style="font-size:120%;float:left;margin:0px 30px 0px 0px;padding:0;">File to upload</div>
					<div id="selected-file">
						<h4 class="selected-fileName"></h4>
						<%-- <div>Collection: <span class="selected-collection"></span></div> --%>
						<span id="selected-fileSize"></span> ${vertDivider} <span id="selected-lastMod"></span>
					</div>
				</div>

				<div id="assetInfo"></div>
			</div>
			
			<div id="uploadInputs" style="position:relative;margin:20px;">
				<div id="fileInputDiv">
					
					<%-- <input type="file" name="fileToUpload" id="fileToUpload" onchange="fileSelected();"/> --%>
					<html:file styleId="fileInputButton" property="theFile" size="1" 
							 onchange="ASSET_MANAGER.upload.fileSelected();" style="cursor:pointer;height:33px;" />
				</div>
				<div id="uploadButtonDiv">
					<div id="visible-upload-button" class="ui-button" style="position:absolute;">Choose a file</div>
				</div>
			</div>
	
			<div id="progressNumber"></div>
		</html:form>
	</div>
	
	<%-- WEBLINK TOOL --%>
	<div id="weblink-tool" class="helper-tool" style="display:none;margin:20px;">
		 <div class="tool-blurb">Enter the URL to a resource on the web</div> 
		 <input id="weblink-input" type="text" name="weblink-input" value="" />
		 
		 <div id="weblink-messages">
		 	<div id="protected-url-msg" class="msg" style="display:none">
		 		Protected urls are not allowed as weblinks. Use the Select
		 		or Upload tools to catalog a protected asset.
		 	</div>
		 </div>
	</div>
	
	<%-- DELETE RENAME TOOL --%>
	<div id="delete-rename-tool" class="helper-tool" style="display:none;margin:20px;">
		<div id="dr-refresh" title="Refresh listing" style="display:none"><i class="fa fa-refresh fa-lg"></i></div>
		
		<%-- Delete Asset Button not currently used - but keep in dom so javascript doesn't break --%>
 		<div class="ui-button" style="display:none;text-align:right;" id="delete-asset-button">
			<i class="fa fa-trash-o fa-lg" style="vertical-align:-1px;"></i> Delete</div>
		<div class="tool-blurb">All Assets that are NOT cataloged in a metadata record</div> 
		<div class="message" style="display:none"></div>
		 
		 <div id="orphanInfo"></div>
	</div>

</div>

<div id="helper-footer">
	<div id="debug-opener" class="ui-button" style="display:none;float:left;margin-left:20px;height:25px;padding:5px 5px 0px 5px;">Open in new window</div>
	<input id="insert-button" type="button" value="Insert"/>
	<input id="cancel-button" type="button" value="Cancel"/>
</div>

</body>
</html>

<script type="text/javascript">

var debug = false;

if (debug && parent && typeof (parent.Widget) != 'undefined') {
	// we've been opened within the editor
	
	// expose the debug-opener (if we're debugging)
	var opener = $('debug-opener');
	opener.show();
	opener.setStyle({cursor:'pointer'});
	opener.observe('click', function (event) {
			window.open (window.location.href, 'iframe');
	});
}

$('fileInputButton').observe ('mouseover', function (event) {
	$('visible-upload-button').addClassName('over');
});
$('fileInputButton').observe ('mouseout', function (event) {
	$('visible-upload-button').removeClassName('over');
});


</script>
