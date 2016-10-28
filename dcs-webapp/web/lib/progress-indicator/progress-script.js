/* function toPercent (numStr) {
	var fl = parseFloat (numStr)
	var percent = 0
	if (fl)
		percent = Math.ceil(fl * 100);
	return percent.toString() + "%"
}
		
 */	
function updateProgress (pe, progressUrl, finalize) {
	// alert ("updateProgress");
	var progressIndicator = $("progress-indicator");
	var rndm = new Date().getTime();
	var url = progressUrl + "&foo="+rndm;
	new Ajax.Request (url, {
		/* method:'get', */
		onSuccess: function (transport) {
			try {
				var json = transport.responseText.evalJSON();
				var progress = json.progress
				if (!progress) {
					throw ("didn't get progress");
				}
				var status = progress.status;
				log ("status: " + status);
				if (progressIndicator) progressIndicator.show();
				updateProgressBar (progress);
				if (status != 'active') {
					pe.stop();
					finalize();
				}
			} catch (error) {
				// alert (error)
				log (error)
				// pe.stop();
			}
		}
	});
}

function updateProgressBar (progress) {
	var fractionComplete = parseInt(progress.done) / parseInt (progress.total);
	var total_dims = $('progress-bar').getDimensions();
	var img_width = Math.floor (total_dims.width * fractionComplete);
	$('progress-msg').update (progress.msg);
	$('percent-complete').update (Math.ceil (fractionComplete * 100));
	$('progress-done').update (progress.done)
	$('progress-total').update (progress.total)
	
	var bar = $('progress-bar-img');
	bar.setStyle ({width:img_width, height:bar.getStyle('height')})
}

// Event.observe (window, 'load', function () { updateProgressBar (0) } );
