if( window.boundingBoxHelper == undefined )
{
	var boundingBoxHelper = new function(){
		var _northField, _southField, _eastField, _westField, _delimiter;
		var bbWindow;
		
		return {
			init: function(northField,southField,eastField,westField,delimiter, precision) {
				_northField = northField;
				_southField = southField;
				_eastField = eastField;
				_westField = westField;
				_delimiter = delimiter;
				_precision = precision || 2;
			},
			
			openBoundingBoxTool: function(id) {
				// the bounding box tool path is relative to the "editor" directory
				var url = "input_helpers/bounding_box/gmapBoundingBox.jsp";
				url = url + '?id=' + id + '&precision='+_precision;
				//alert ("opening: " + url)
				bbWindow=window.open(url,'BoundingBoxTool','height=700,width=800');
				bbWindow.focus()
				return false;
			},
			
			getBounds: function(id) {
				var minlat_id = id + _delimiter + _southField;
				var maxlat_id = id + _delimiter + _northField;
				var minlng_id = id + _delimiter + _westField;
				var maxlng_id = id + _delimiter + _eastField;
				var bounds={
					minlat: parseFloat($(minlat_id).value),
					maxlat: parseFloat($(maxlat_id).value),
					minlng: parseFloat($(minlng_id).value),
					maxlng: parseFloat($(maxlng_id).value)
				};
				if( isNaN(bounds.minlat) || isNaN(bounds.maxlat) || isNaN(bounds.minlng) || isNaN(bounds.maxlng) )
					return false;
				else
					return bounds;
			},
			
			setBounds: function(minlat,maxlat,minlng,maxlng, id) {
				/* the input elements in the metadata editor can't use xpaths, but instead are computed by
				   the "sf:pathToId" jsp function that converts the xpath to a javascript id
				*/
				
				 // create ids for each of the input fields
				var minlat_id = id + _delimiter + _southField;
				var maxlat_id = id + _delimiter + _northField;
				var minlng_id = id + _delimiter + _westField;
				var maxlng_id = id + _delimiter + _eastField;
				
				/* $(id) notation is prototype's shortcut for document.getElementById */
				$(minlat_id).value = minlat;
				$(maxlat_id).value = maxlat;
				$(minlng_id).value = minlng;
				$(maxlng_id).value = maxlng;
			}
		};
	}();
}
