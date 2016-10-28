function editStatus(recId) {
	// var recId = document.forms[0].recId.value;

	iFrameWindow(recId);
	// infoDialog(recId);
	
	// Escape closes window
	WindowCloseKey.init()
	WindowStore.init();
}

function iFrameWindow (recId) {
	
	var win = new Window({
		//id:"edit_status_modal_win",
		className: 'dcs_alphacube', // bluelighting alphacube mac_os_x 
		// title: "Update Status", 
		width:600, 
		height:500, 
		destroyOnClose: true, 
		recenterAuto:false,
		minimizable:false,
		maximizable:false,
		showEffectOptions: {duration:0.5},
		hideEffectOptions: {duration:0.5},
		// showEffect: Effect.BlindDown,
		// hideEffect: Effect.BlindUp,
		
		// iframe content
		url:"../record_op/status.do?command=edit&modal=true&recId="+recId
	});
	
	// win.setStatusBar("Status bar info"); 
 	win.showCenter(true, 50);
	
	// this works, but only does what close does in the first place
/* 	win.setCloseCallback (function () { 
		alert ("close call back");
		// do NOT call win.close() here, since it creates an infinite loop!
 		win.destroy();
		Windows.removeModalWindow(win); 
	});*/
	
	// AJAX content - but doesn't work for some reason??
/* 	var url = "../record_op/status.do?command=edit&modal=true&recId="+recId;
	win.setAjaxContent (url, {method:"get"}, true, true); */
	
}
	
function infoDialog (recId) {
	/* ISSUE: why aren't javascript, css files loaded for AJAX content? */
	var content = {
		// url:"../record_op/status.do?command=edit&modal=true&recId="+recId,
		url:"../record_op/status.do",
		options: {
			parameters: {
				command:'edit',
				modal: 'true',
				recId: recId
			},
			method:'get',
			asynchronous:'false'
		}
	}
	
	var windowParams = {
		id:"edit_status_modal_win",
		className: "dcs_alphacube", // "mac_os_x", 
		title: "Change Status", 
		width:600, 
		height:600, 
		destroyOnClose: true, 
		recenterAuto:false,
		// iframe content
		// url:"../record_op/status.do?command=edit&modal=true&recId="+recId,
		
		okLabel: "ok"

	};
	
	Dialog.confirm (content, windowParams);
}

function try1 (id) {
/*	alert ("edit status for " + recId); */
	Dialog.info(
		{
			url: "../record_op/status.do?command=edit&recId="+recId, 
			options: {method: 'get'}
		}, 
		{
			className: 'alphacube', // 'mac_os_x', // 'dialog', // "alphacube", 
			width:540, 
			okLabel: "Close"
		}
	);
}
