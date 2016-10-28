// dlese_setSiteBase(): If your site base path is anything other than '/', you need to call this function.
// Param 1: Site base path (i.e., '/my_site/')
dlese_setSiteBase( "/dds/admin/examples/" );
dlese_docIsSideNav = true;

// MENU DEFINITION -- Define your menu here.  
// ---------------
// 1) Always use the variable name M0 for the root tree
// 2) NEVER use the variable name MO (or anything so undescriptive) for anything BUT the root tree
var M0 = new TreeMenu( 'M0' );

	var menu1 = new TreeMenu( 'menu1' );
	ATE( M0, 'dlese_templates_example_side_menus_dynamic.jsp', 'Menu 1', menu1 );	

		ATE( menu1, 'dlese_templates_example_side_menus_dynamic.jsp?demo0', 'Sub item 1' );
		ATE( menu1, 'dlese_templates_example_side_menus_dynamic.jsp?demo1', 'Sub item 2' );

	var menu2 = new TreeMenu( 'menu2' );
	ATE( M0, 'dlese_templates_example_side_menus_dynamic.jsp?demo2', 'Menu 2', menu2 );	

		var subMenu21 = new TreeMenu( 'subMenu21' );
		ATE( menu2, 'dlese_templates_example_side_menus_dynamic.jsp?demo23', 'Sub menu 2.1', subMenu21 );
			ATE( subMenu21, 'dlese_templates_example_side_menus_dynamic.jsp?demo24', 'Sub menu item 1' );
			ATE( subMenu21, 'dlese_templates_example_side_menus_dynamic.jsp?demo25', 'Sub menu item 2' );


		var subMenu22 = new TreeMenu( 'subMenu22' );
		ATE( menu2, 'dlese_templates_example_side_menus_dynamic.jsp?demo26', 'Sub menu 2.1', subMenu22 );
			ATE( subMenu22, 'dlese_templates_example_side_menus_dynamic.jsp?demo27', 'Sub menu item 1' );
			ATE( subMenu22, 'dlese_templates_example_side_menus_dynamic.jsp?demo28', 'Sub menu item 2' );

	ATE( M0, 'index.jsp', 'HOME' );	


// OpenNewWindow is a convenience function that makes use of a powerful feature of Javascript: all parameters
// ------------- are optional!  Of course you will only get positive results if you specify the first parameter
// in this case--the URL of the page you want to open.  By default, all browser features are displayed, and 
// the window will be "focused", or brought to the front of any other open windows--if the user already had
// that window open, this will ensure that clicking the link brings it forward.  Note that the default window
// "target" (unique window id) is '_blank', which ALWAYS opens a new window, regardless of whether the user
// had clicked the link before.
//
// If your needs are simple, you'll probably find it easier to specify 'nw' for the target parameter of the 
// ATE() function, since it opens and focuses a single uniquely named window.  But if you want more control
// over the size and look of the window (see the "Author" link under "About the Tree Menus" for an example)
// then this function will hopefully make it easier for you.
function OpenNewWindow( url, 			// location to open 
						target,			// a unique id for this window (defaults to '_blank')
						width,			// window width (pixels)
						height, 		// window height (bad idea unless intended as a small 'popup' window)
						dontFocus, 		// don't focus the window after opening (not recommended)
						// For each of the following, pass a 1 or true to turn OFF the feature, 
						// since having them on is the default (same as passing '', or 0):
						resizable, 		// can the window be resized? 
						toolbar, 		// display navigation buttons (back, forward, etc.)? 
						location, 		// display the address (URL) bar of the window?
						directories, 	// display the directories bar ("Links" bar in IE)?
						menubar, 		// display the menubar ("File", "Edit", etc.)?
						status, 		// display status bar (at bottom of window)?
						scrollbars ) {	// allow the user to scroll if content is larger than the window?
		if ( !width ) width = ''; else width = 'width=' + width + ',';
		if ( !height ) height = ''; else height = 'height=' + height + ',';
		if ( !target ) target = '_blank';
		if ( !resizable ) resizable = 'resizable=1,'; else resizable = 'resizable=0,' + resizable + ',';
		if ( !toolbar ) toolbar = 'toolbar=1,'; else toolbar = 'toolbar=0,';
		if ( !location ) location = 'location=1,'; else location = 'location=0,';
		if ( !directories ) directories = 'directories=1,'; else directories = 'directories=0,';
		if ( !menubar ) menubar = 'menubar=1,'; else menubar = 'menubar=0,';
		if ( !status ) status = 'status=1,'; else status = 'status=0,';
		if ( !scrollbars ) scrollbars = 'scrollbars=1'; else scrollbars = 'scrollbars=0';
		var props = width + height + resizable + toolbar + location + directories + menubar + status + scrollbars;
		var w = window.open( url, target, props );
		if ( !dontFocus && window.focus ) w.focus();
} 

/* Try uncommenting the following line and reloading (not from browser cache!) to see what your site
looks like in older browsers that can run Javascript.  NOTE: Always provide an alternative means of
navigation for users WITHOUT Javascript (many users turn it off if when they do have it)!  See
the 'Accessibility issues' section of the example site for details on how to do this. */

//IS_DHTML_BROWSER = false;




