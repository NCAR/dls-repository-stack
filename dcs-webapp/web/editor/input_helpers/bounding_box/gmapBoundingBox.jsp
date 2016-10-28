<%@ page language="java" %><%@ page isELIgnored="false" 
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns:v="urn:schemas-microsoft-com:vml" xmlns="http://www.w3.org/1999/xhtml"><head>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<title>Ruler</title>
<style type="text/css">
v\:* {
	behavior:url(#default#VML);
}
BODY { 
	font-family: Arial; 
	font-size: small;
	background-color: #DDD;
}
A:hover {
	color: red; 
	text-decoration: underline; 
}
.labelstyle {
	background-color:#ffffff;
	font-weight:bold;
}
</style>

<!--
NOTE: googleMapsKey must be configured as initParam in context descriptor
- see http://code.google.com/apis/maps/signup.html to obtain key -->
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=${initParam.googleMapsKey}" type="text/javascript"></script>

<style type="text/css"> @media print{.gmnoprint{display:none}}@media screen{.gmnoscreen{display:none}} </style>

</head>

<body onunload="GUnload()">

<div id="novel" style="position:relative;width:290px;text-align:left;float:right;margin-right:20px;">
  <table style="background-color:#EEE;">
    <tr><td colspan="2"><strong>Current Values</strong></td></tr>
  	<tr><td align="right">westCoord: </td><td><input type="text" id="minlng" value="Minimum Longitude" /></td></tr>
  	<tr><td align="right">eastCoord: </td><td><input type="text" id="maxlng" value="Maximum Longitude" /></td></tr>
  	<tr><td align="right">northCoord: </td><td><input type="text" id="maxlat" value="Maximum Latitude" /></td></tr>
  	<tr><td align="right">southCoord: </td><td><input type="text" id="minlat" value="Minimum Latitude" /></td></tr>
  </table>
</div>

<div id="forehead" style="position: relative; top:0px;left:0px;width: 400px;">
	<h4>Geocoder</h4>
    <p>
	Enter a location and the geocoder will attempt to find its latitude and longitude and center the map on that location.
  <form action="#" onsubmit="showAddress(this.haku.value); return false">
        <input type="text" size="40" id="haku" name="haku" title="Type an address"/>
        <input type="submit" id="hae" value=" geocode " title="You can hit enter key as well"/>
  </form>

	</p>
    <p>
  <input type="button" id="add" value="Add Bounding Box" onclick="add()" style="width:150px;" title="Add Bounding Box">
  <input type="button" id="sendvalues" value="Send Values" onclick="sendOpenerValues()" style="width:150px;" title="Send Values">
  <input type="button" id="clearbox" value="Clear Bounding Box" style="width:150px;display:none;" title="Clear Bounding Box">
  
 </p>
</div>


<div id="map" style="width:600px; height:440px; margin-left:20px;"></div>

<script type="text/javascript">
//<![CDATA[

/**
 * Original bounding box implementation based on code
 * from Esa's Google Maps Experiments (http://esa.ilmari.googlepages.com/)
 */
 
 
////map
var map = new GMap2(document.getElementById("map"));
map.addControl(new GLargeMapControl());
map.setCenter(new GLatLng(36.244273,-97.822266), 3);
document.getElementById("sendvalues").style.display="none";

// automatically call function to create bounding box if there are coords
//  present on opener page. (the add() func will also center and zoom to 
//  a hopefully reasonable level for the size of the bounding box
if( opener.boundingBoxHelper.getBounds(getIdParam()) )
	add();


/////////////////////// Ruler code by Esa 2006
// some cleaning 2008

function add()
{
	var button=0;
	var dist=0;
	var line;
	var poly;
	var markerSE;
	var markerSW;
	var markerNE;
	var markerNW;
	var oppositeMarker;
	var clickedMarker;

	var dragOverlay;
	
	// Create a base icon for all of our markers that specifies the
	// shadow, icon dimensions, etc.
	var customIcon = new GIcon(G_DEFAULT_ICON);
	customIcon.iconSize = new GSize(15,15);
	customIcon.shadowSize = new GSize(0,0);
	customIcon.iconAnchor = new GPoint(7,7);
	customIcon.infoWindowAnchor = new GPoint(6,6);
	customIcon.image = "ruler_files/corner.png";
	customIcon.maxHeight = 0;

	function measure( corner ){
		switch(corner) {
			case "SE":
				oppositeMarker = markerNW;
				clickedMarker = markerSE;
				break;
			case "SW":
				oppositeMarker = markerNE;
				clickedMarker = markerSW;
				break;  
			case "NE":
				oppositeMarker = markerSW;
				clickedMarker = markerNE;
				break;  
			case "NW":
				oppositeMarker = markerSE;
				clickedMarker = markerNW;
				break;  
			default:
		}
		
		var minlat = Math.min(clickedMarker.getLatLng().lat(),oppositeMarker.getLatLng().lat());
		var maxlat = Math.max(clickedMarker.getLatLng().lat(),oppositeMarker.getLatLng().lat());
		var minlng = Math.min(clickedMarker.getLatLng().lng(),oppositeMarker.getLatLng().lng());
		var maxlng = Math.max(clickedMarker.getLatLng().lng(),oppositeMarker.getLatLng().lng());
		
		var p1 = new GLatLng(minlat,minlng,false);
		var p2 = new GLatLng(minlat,maxlng,false);
		var p3 = new GLatLng(maxlat,maxlng,false);
		var p4 = new GLatLng(maxlat,minlng,false);
		
		var precision = getPrecisionParam() || 2;
		// alert ('pre: ' + precision);
		document.getElementById("minlat").value=minlat.toFixed(precision);
		document.getElementById("maxlat").value=maxlat.toFixed(precision);
		document.getElementById("minlng").value=minlng.toFixed(precision);
		document.getElementById("maxlng").value=maxlng.toFixed(precision);
		
		if (markerSW.getLatLng().equals(p1)) {	
			markerSW.setLatLng(p1);
			markerSE.setLatLng(p2);
			markerNE.setLatLng(p3);
			markerNW.setLatLng(p4);
		} else if (markerSW.getLatLng().equals(p2)) {		
			markerSW.setLatLng(p2);
			markerSE.setLatLng(p1);
			markerNE.setLatLng(p4);
			markerNW.setLatLng(p3);
		} else if (markerSW.getLatLng().equals(p3)) {	
			markerSW.setLatLng(p3);
			markerSE.setLatLng(p4);
			markerNE.setLatLng(p1);
			markerNW.setLatLng(p2);
		} else if (markerSW.getLatLng().equals(p4)) {	
			markerSW.setLatLng(p4);
			markerSE.setLatLng(p3);
			markerNE.setLatLng(p2);
			markerNW.setLatLng(p1);
		} else if (markerSE.getLatLng().equals(p2)) {	
			markerSW.setLatLng(p1);
			markerSE.setLatLng(p2);
			markerNE.setLatLng(p3);
			markerNW.setLatLng(p4);
		} else if (markerSE.getLatLng().equals(p1)) {		
			markerSW.setLatLng(p2);
			markerSE.setLatLng(p1);
			markerNE.setLatLng(p4);
			markerNW.setLatLng(p3);
		} else if (markerSE.getLatLng().equals(p4)) {	
			markerSW.setLatLng(p3);
			markerSE.setLatLng(p4);
			markerNE.setLatLng(p1);
			markerNW.setLatLng(p2);
		} else if (markerSE.getLatLng().equals(p3)) {	
			markerSW.setLatLng(p4);
			markerSE.setLatLng(p3);
			markerNE.setLatLng(p2);
			markerNW.setLatLng(p1);
		} 		
		
		var rect = [p1,p2,p3,p4,p1];
		
		if(poly)map.removeOverlay(poly);
		poly = new GPolygon(rect, '#0000FF', 1, 1, '#0000FF', .1);
		map.addOverlay(poly);
		
		if( dragOverlay != undefined )
			dragOverlay.redraw(true);
	}

	document.getElementById("add").style.display="none";
	document.getElementById("sendvalues").style.display="inline";
	
	var p1, p2, p3, p4;
	var currentBounds = opener.boundingBoxHelper.getBounds( getIdParam() );
	if( currentBounds )
	{
		p1 = new GLatLng( currentBounds.minlat, currentBounds.minlng );
		p2 = new GLatLng( currentBounds.minlat, currentBounds.maxlng );
		p3 = new GLatLng( currentBounds.maxlat, currentBounds.maxlng );
		p4 = new GLatLng( currentBounds.maxlat, currentBounds.minlng );
		var newcenter = new GLatLng( (currentBounds.maxlat+currentBounds.minlat)/2, (currentBounds.maxlng+currentBounds.minlng)/2, false );
		
		var box = new GLatLngBounds(p1, p3);
		var setzoom = map.getBoundsZoomLevel( box );
		map.setCenter( newcenter, setzoom-1 );
	}
	else
	{
		var bounds = map.getBounds();
		var percentage = 0.13;
		var southWest = bounds.getSouthWest();
		var northEast = bounds.getNorthEast();
		var lngSpan = northEast.lng() - southWest.lng();
		var latSpan = northEast.lat() - southWest.lat();
		
		p1 = new GLatLng(southWest.lat() + latSpan * percentage ,
							 southWest.lng() + lngSpan * percentage);
		p2 = new GLatLng(southWest.lat() + latSpan * percentage ,
							 southWest.lng() + lngSpan * (1-percentage));
		p3 = new GLatLng(southWest.lat() + latSpan * (1-percentage) ,
							 southWest.lng() + lngSpan * (1-percentage));
		p4 = new GLatLng(southWest.lat() + latSpan * (1-percentage) ,
							 southWest.lng() + lngSpan * percentage);								 								
	}
	
	markerOptions = { draggable: true, icon:customIcon, bouncy:false, dragCrossMove:true };

	markerSW = new GMarker(p1,markerOptions);
	map.addOverlay(markerSW);
	markerSE = new GMarker(p2,markerOptions);
	map.addOverlay(markerSE);
	markerNE = new GMarker(p3,markerOptions);
	map.addOverlay(markerNE);
	markerNW = new GMarker(p4,markerOptions);
	map.addOverlay(markerNW);
	
	markerNW.enableDragging();
	markerNE.enableDragging();
	markerSW.enableDragging();
	markerSE.enableDragging();
	
	measure('SE');
		
	GEvent.addListener(markerSE,"drag",function(){measure('SE');});
	GEvent.addListener(markerSE,"dblclick",function(){clr();});
	GEvent.addListener(markerSW,"drag",function(){measure('SW');});
	GEvent.addListener(markerSW,"dblclick",function(){clr();});
	GEvent.addListener(markerNE,"drag",function(){measure('NE');});
	GEvent.addListener(markerNE,"dblclick",function(){clr();});
	GEvent.addListener(markerNW,"drag",function(){measure('NW');});
	GEvent.addListener(markerNW,"dblclick",function(){clr();});
	
	button++;

	function clr() {
		map.removeOverlay(poly);
		map.removeOverlay(markerSE);
		map.removeOverlay(markerSW);
		map.removeOverlay(markerNE);
		map.removeOverlay(markerNW);
		map.removeOverlay(dragOverlay);
		button=0;
		document.getElementById("add").style.display="inline";
		document.getElementById("sendvalues").style.display="none";
		document.getElementById("clearbox").style.display="none";
		dist=0;
		
		document.getElementById("minlat").value='';
		document.getElementById("maxlat").value='';
		document.getElementById("minlng").value='';
		document.getElementById("maxlng").value='';
	}	
	
	GEvent.addDomListener(document.getElementById("clearbox"), "click", function(){clr();});
	document.getElementById("clearbox").style.display = "inline";
	
	
	/**
	 * implementation of Google Maps GOverlay Interface
	 *  to put an invisible rectangle which can be dragged overlaying the 
	 *  visible bounding box polygon. When this invisible layer is dragged,
	 *  the bounding box will move with it (and when box is resized, this
	 *  will resize with it)
	 */
	function DragOverlay(){}
	DragOverlay.prototype = new GOverlay();
	DragOverlay.prototype.initialize = function(map)
	{
		var div = document.createElement('div');
		div.style.position = 'absolute';
		
		map.getPane(G_MAP_FLOAT_SHADOW_PANE).appendChild(div);
		
		this._map = map;
		this._div = div;
		this._draggable = new GDraggableObject(this._div);
		this._draggable.setDraggableCursor('move');
		GEvent.addListener( this._draggable, 'drag', function(){
														var x1 = parseInt(div.style.left);
														var y1 = parseInt(div.style.top);
														var x2 = x1+parseInt(div.style.width);
														var y2 = y1+parseInt(div.style.height);
														markerNW.setLatLng( map.fromDivPixelToLatLng( new GPoint(x1,y1) ) );
														markerNE.setLatLng( map.fromDivPixelToLatLng( new GPoint(x2,y1) ) );
														markerSW.setLatLng( map.fromDivPixelToLatLng( new GPoint(x1,y2) ) );
														markerSE.setLatLng( map.fromDivPixelToLatLng( new GPoint(x2,y2) ) );
														measure("NW");
													}
		);
	}
	DragOverlay.prototype.redraw = function(force)
	{
		if( !force)
			return;
		var bounds=poly.getBounds();
		var sw = bounds.getSouthWest();
		var ne = bounds.getNorthEast();
		var swcoords = map.fromLatLngToDivPixel(sw);
		var necoords = map.fromLatLngToDivPixel(ne);
		this._div.style.width = ( necoords.x - swcoords.x )+ 'px';
		this._div.style.height = ( swcoords.y - necoords.y )+ 'px';
		
		this._draggable.moveTo( new GPoint(swcoords.x, necoords.y) );
	}
	DragOverlay.prototype.remove = function()
	{
		this._div.parentNode.removeChild(this._div);
	}
	
	dragOverlay = new DragOverlay();
	map.addOverlay( dragOverlay );
}

///Geo
var geocoder = new GClientGeocoder();
function showAddress(address){
	geocoder.getLatLng(address,
		function(point){
			if(!point){
				alert(address+" not found");
			}else{
				map.panTo(point);
				// clr();
			}
		}
	)
}

// extract the 'id' paramter from this document's URL
function getIdParam () {
	return getParam('id');
}

function getPrecisionParam() {
	return getParam('precision');
}

function getParam (paramName) {
	var search = window.location.search;
	var params = search.substr(1).split ("&")
	for (var i=0; i<params.length;i++) {
		var param = params[i]
		var splits = param.split('=');
		if (splits[0] == paramName) 
			return splits[1]
	}
	return ''
}
	

function sendOpenerValues() { 
	
	opener.boundingBoxHelper.setBounds(document.getElementById("minlat").value,
					document.getElementById("maxlat").value,
					document.getElementById("minlng").value,
					document.getElementById("maxlng").value,
					getIdParam()); // send id param back to caller to support repeating fields
	opener.focus();
	this.close();
}

	
//]]>

</script>

</body>
</html>

