/* adapted from CCS toggler functionality

Requirements:
1 - dom structure:
	<div class="toggler" id="XXX">
		<div id="toggler-lnk-XXX" class="togglerClosed">Header text</div>
		<div id="toggler-con-XXX" style="display:none">
			If you see this, then toggler is open
		</div>
	</div>

2 - CSS classes
	.togglerClosed {
		background-image: url(images/arrow_right_gray.gif);
		background-repeat: no-repeat;
		background-position: center left;
		white-space:nowrap;
		padding: 8px 0px 10px 40px; 
		outline: 0;
		background-color: #779878;
	}
	.togglerOpen {
		background-image: url(images/arrow_down_right.gif);
		outline: 0;
	}
	
	.togglerOver {
		cursor:pointer;
		background-color: #779878;
	}

3 - images
must have images in place to correspond to the background-images in classes above

4 - call initializeTogglers after dom is loaded
*/
	
function doToggler(id,closeOthersOfClass) {
	var labelElm = $('toggler-lnk-'+id);
	var toggleElm = $('toggler-con-'+id);
	if(toggleElm) {
		var labelElm = $('toggler-lnk-'+id);
		if(!toggleElm.visible() && closeOthersOfClass) {
			$$('.'+closeOthersOfClass).each(function(otherElm) {
				if(otherElm.hasClassName('togglerOpen') && otherElm.id != 'toggler-lnk-'+id) {
					var otherId = otherElm.id.sub('toggler-lnk-','');
					toggleAction('toggler-con-'+otherId,otherElm,'close');
				}
			});
		}
		toggleAction(toggleElm,labelElm);		
	}	
}

function doTogglerOpen(id) {
	toggleAction($('toggler-con-'+id),$('toggler-lnk-'+id),'open');	
	return false;
}
function doTogglerClose(id) {
	toggleAction($('toggler-con-'+id),$('toggler-lnk-'+id),'close');
	return false;
}

// Internal helper function for toggle, open, close
function toggleAction(toggleElmId,labelElmId,action) {
	// log ("toggleAction: " + toggleElmId.identify() + ", " + labelElmId.identify() + ", " + action);
	var labelElm = $(labelElmId);
	if(!action || action == 'toggle') {
		if($(toggleElmId).visible()) {
			Effect.BlindUp(toggleElmId, { duration: 0.2 });
			labelElm.removeClassName('togglerOpen');
		}
		else {
			Effect.BlindDown(toggleElmId, { duration: 0.2 });
			labelElm.addClassName('togglerOpen');
		}	
	}
	else if (action == 'close' && labelElm.hasClassName('togglerOpen')) {
		Effect.BlindUp(toggleElmId, { duration: 0.2 });
		labelElm.removeClassName('togglerOpen');
	}
	else if (action == 'open' && labelElm && !labelElm.hasClassName('togglerOpen')) {
		Effect.BlindDown(toggleElmId, { duration: 0.2 });
		labelElm.addClassName('togglerOpen');
	}		
}

/* initialize all elements of class "toggler" */
function initializeTogglers () {
	$$('.toggler').each (function (toggler) {
		var id = toggler.identify();
		log ("toggler: " + id);
		var lnk = $('toggler-lnk-'+id);
		var con = $('toggler-con-'+id);
		if (lnk.hasClassName('togglerClosed'))
			doTogglerClose (id);
		else
			doTogglerOpen(id);
			
		lnk.observe ('click', function (event) {
			doToggler(id);
			return false;
		});
		lnk.observe ('mouseover', function (event) {
			lnk.addClassName('togglerOver');
		});
		lnk.observe ('mouseout', function (event) {
			lnk.removeClassName('togglerOver');
		});
	});
	
}	

