<style type="text/css">

.small_text {
	font-size:10px;
	margin-top:3px;
}

.rr_image_container {
	width:85px;
	/* height:85px; */
	border:thin solid black;
	padding:5px;
	background-color:#e3e2e2;
}

.rr_image {
	width:75px;
}
</style>

<div id="rr_helper_${id}" style="margin-left:25px">
	<table border=0 style="border-collapse:collapse;">
		<tr valign="middle" align="center">
			<td>
				<input type="button" value="Upload Image" class="upload-button" />
			</td>
			<td valign="middle" style="padding-left:40px;">
				<div class="rr_image_container">
					<img class="rr_image" src=""/>
					<div>Your Image</div>
				</div>
			</td>
		</tr>
	</table>

<%-- 	<div class="small_text">id: ${id}</div>
	<div class="small_text">xpath: ${sf:idToPath(id)}</div> --%>
</div>

<script type="text/javascript">
	
if (typeof (RRHelper) == 'undefined') {

	var rrHelpers = new Hash();

	function openRRUploader (id) {
		// what information you pass to the upload form (and through it on to the image server) is supplied here
		// in this case, we are sending the recID (maybe the image server needs this as part if its nameing scheme for
		// the uploaded image
		log ("OPENER/LOADER - " + id);
		var params = {
			recID : document.forms['sef'].recId.value,
			imgserver : '/schemedit/editor/input_helpers/lar/relatedResource-upload-handler.html',
			helperID : id
			}
		log ("make url");
		var uploadUrl = '/schemedit/editor/input_helpers/lar/relatedResource-upload.html' + '?' + $H(params).toQueryString();
		log ("uploadUrl: " + uploadUrl);
		var win = window.open (uploadUrl, 'upload', 'resizable=true,scrollbars=yes, height=500px,width=600px');
		win.focus();
		return false;
	}

	function callback (imageUrl, helperId) {
		log ("raw helperId: " + helperId)
		imageUrl = decodeURIComponent(imageUrl);
		helperId = decodeURIComponent(helperId);
		log ("CALLBACK - imageUrl: " + imageUrl)
		log ("  - helperId: " + helperId)
		var helper = rrHelpers.get(helperId);
		
		try {		
		
			helper.url_input.value = imageUrl;
			log ('updated imageURL');
			helper.thumbnail.src = imageUrl;
		} catch (error) {
			log (" - ERROR: " + error);
		}
	}
	
	var RRHelper = Class.create ({
		initialize: function (id) {
			this.id = id;
			
			log ("rrHelper");
			log (" - id " + this.id);
			
			this.rr_helper_elm = $('rr_helper_'+this.id);
			if (!this.rr_helper_elm)
				throw ("rr_helper not found at 'rr_helper_'"+this.id + "'");
			
			this.thumbnail = this.rr_helper_elm.down('img.rr_image');
			if (!this.thumbnail)
				throw ("thumbnail not found");
				
			this.type_input = $(this.id+'_^_@type');
			if (!this.type_input)
				throw ('type_input not found at ' + this.id+'_^_@type');
				
			this.url_input = $(this.id+'_^_@URL');
			if (!this.url_input)
				throw ('url_input not found');
				
			this.type_input.observe ('change', function (event) {
				if (this.getType() == 'Has Image' || this.getType() == 'Has Thumbnail Image') {
					this.activate();
				}
				else {
					this.deactivate();
				}
			}.bind(this));
			
			this.url_input.observe ('change', function (event) {
				if (this.rr_helper_elm.visible()) {
					this.thumbnail.src = this.getUrl();
				}
			}.bind(this));
			
			log ("about to click handle upload button");
			this.rr_helper_elm.down('.upload-button').observe ('click', function () {
				log ("CLK");
				openRRUploader (this.id, this.url_input.identify());
			}.bind(this));
				
			if (this.getType() == 'Has Image' || this.getType() == 'Has Thumbnail Image') {
				this.activate();
			}
			else {
				this.deactivate();
			}
			
			log ("initialzied");
		},
		
		activate: function () {
			this.debug (this.getType())
			this.rr_helper_elm.show();
			this.thumbnail.src = this.getUrl();
		},
		
		deactivate: function () {
			this.debug('');
			this.thumbnail.src = '';
			this.rr_helper_elm.hide();
		},
		
		getType: function () {
			return $F(this.type_input)
		},
		
		getUrl: function () {
			return $F(this.url_input);
		},
		
		debug: function (s) {
			if (this.rr_helper_elm.down('.debug'))
				this.rr_helper_elm.down('.debug').update (s)
		}
	});
}

document.observe ('dom:loaded', function () {
	rrHelpers.set("${id}", new RRHelper ("${id}"));
});

</script>

