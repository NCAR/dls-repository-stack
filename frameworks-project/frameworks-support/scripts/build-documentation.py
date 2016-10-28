#!/usr/bin/python

#python library modules
import os		
import sys	
import string	
import time
import shutil	
import ftplib

# other python modules
import filelist

	

# Purpose: To generate the final html for the fields metadata framework documentation document
# Author: Katy Ginger
# Origanl creation date: 2004-12-08


#----------------------------------------------
# Assumptions
#-----------------------------------------------


#----------------------------------------------
# MAIN PROGRAM
#-----------------------------------------------
#----------------------------------------------------------------------------------------------------------------
# Determine if correct arguments have been provided to script
#----------------------------------------------------------------------------------------------------------------
if len(sys.argv) == 1:
        print ' \n\n'
	print 'There are too few arguments.'
        print 'Script usage is: build-documentation.py ARG where ARG is a metadata format directory as it is on the metadata website.'
	print 'and version number like this adn-item/0.6.50'
        print 'Ending program'
	print '\n\n'
        sys.exit(1)

		
#------------------------------------------------------------------------------------
# Determine host by capturing hostname into a python variable
#--------------------------------------------------------------------------------------
hostname = os.environ['HOSTNAME']

#------------------------------------------------------------------------------------------------------------------------
# Set directory information
#------------------------------------------------------------------------------------------------------------------------
metadatadir = '/dls/devel/ginger/play/frameworks-project/frameworks/'
working_metadatadir = '/dls/devel/ginger/play/framworks-project_docs/'
xsldir = '/dls/devel/ginger/play/frameworks-project/frameworks-support/xsl/' 
xslfile1 = 'display-fields-files.xsl'
xslfile2 = 'get-fields-name.xsl'
fieldsdir = '/fields/'
groupsdir = '/groups/'
htmdir = '/htm-pieces/'
txtdir = '/txt-pieces/'
builddir = '/build/'
frmhtm = 'documents/framework-htm-pieces/'
docs = '/documents/'


#---------------------------------------------------------------------------------------------------------
# Other globals - include html globals
#---------------------------------------------------------------------------------------------------------
now = time.localtime(time.time())
#uparrow = '<p><a href="#top"><img src="/dlese_shared/images/arrowup.gif" alt="Top" border="0" width="14" height="16"></a> </p>'
uparrow = '<p><b><a href="#top">top</a></b></p>'
dateline = '<h6> Last updated: ' + time.strftime("%Y-%m-%d", now) + '<br>Maintained by: <a href=" http://nsdl.org/about/contactus/">National Science Digital Library</a></h6>'

#--------------------------------------------------------------------------------------------------------
# Set the list keystoprocess equal to the arguements provided to the script
# Script name is in  list sys.argv[0] and sys.argv[1:] has the frameworks/versions to process
#--------------------------------------------------------------------------------------------------------
keystoprocess=sys.argv[1:]		
#print keystoprocess
#print ' '

#---------------------------------------------------------------------------------------------------------------
# Prepare collections updating log file
#---------------------------------------------------------------------------------------------------------------
message = '\nFramework documentations being generated at local time: ' + time.asctime(now)+ '\n'
print message

#---------------------------------------------------------------------------------------------------------------
# Generate htm pieces from the xml fields files
#---------------------------------------------------------------------------------------------------------------
validdirs = []
for x in keystoprocess:
	# 1. Make a list of valid directories in case user has entered a non-valid directory
	# 2. Remove old files from:
	# htm-pieces (htmdir), txt-pieces (txtdir), filename-list.xml (build), framework-documentation.htm (in the framework directory)
	# before doing transforms
	# 3. Transform XML fields files to htm files (the htm files will have a file extension of .xml)
	# 4.  Extract the field name and xpath by transforming the fields files to text files (has file extension of .xml)
		# Read the text files into a list
		# Add the name of the file being read to the the list
	# 5. Generate the navigation table for the single htm doc and combine htm-pieces files into the single htm document
	# 6. Print ending note

	#1
	if os.path.isdir(metadatadir + x):
		print 'Processing framework: ' + x + '\n'
		validdirs = validdirs + list(x)

		# 2
		if os.path.isdir(working_metadatadir + x + htmdir):
			filesToRemove = filelist.listingwithdirs(working_metadatadir + x + htmdir)
			for y in filesToRemove:
				os.remove(y)
		if os.path.isdir(working_metadatadir + x + txtdir):
			filesToRemove = filelist.listingwithdirs(working_metadatadir + x + txtdir)
			for y in filesToRemove:
				os.remove(y)

		if os.path.isdir(working_metadatadir + x + docs):
			filesToRemove = filelist.listingwithdirs(working_metadatadir + x + docs)
			for y in filesToRemove:
				if y == working_metadatadir + x + docs + 'framework-documentation.htm':
					os.remove(y)

 
 		# 3
		listing = os.popen('transform ' + xsldir + xslfile1 + ' ' + (metadatadir + x + fieldsdir + ' ') + (working_metadatadir
		+ x + htmdir)).readlines()
		
		# 4
		listing = os.popen('transform ' + xsldir + xslfile2 + ' ' + (metadatadir + x + fieldsdir + ' ') + (working_metadatadir
		+ x + txtdir)).readlines()
		files = filelist.listingwithnodirs(working_metadatadir + x + txtdir)
		# need to create a dictionary to associate the field name with its xpath and the appropriate file name
		fields = {}
		for y in files:
			# z is a list with z[0] of the form 'fieldname:xpath'
			z = filelist.scan(working_metadatadir + x + txtdir + y)
			fieldsList = string.split(z[0], ':')
			# fieldsList is a list of the form ['fieldname', 'xpath']
			# {fieldname 1: [xpath, filename-no-dir], fieldname 2: [xpath, filename-no-dir]}
			fields[fieldsList[0]] = [fieldsList[1], y]

			
	
		# 5
		# Set browser title bar and page titles
		# Need to parse the input argument to get the framework and version separately in order to make nice title labels
		# Order of files/info
			# a. banner-top.htm 
			# b. <title>
			# c. banner-aftertitle.htm
			# d. header (page title) <h1></h1> tags
			# e. paragraph intro
			# f. navigation table
			# g. images.htm
			# h. fields documentation
			# i. date line saying when the page was generated
			# j. bottom-banner.htm			
		keys = fields.keys()
		keys.sort()
		frame = string.split(x, '/')
		version = frame [1]
		frame = string.split(frame[0], '-')
		
		if frame[0] == 'adn':
			title = '<title>ADN documentation version ' +  version + ' (DLESE) Digital Library for Earth System Education</title>'
			header = '<h1>Documentation: ADN version ' +  version + '</h1>'
			para = '<p>This metadata framework documentation provides:</p><ul><li>Graphical overview, content examples, cataloging best practices</li><li>A description of each metadata field including occurrences, obligation, data types, terms and definitions</li><li>Bolded items represent controlled vocabulary terms or the name of individual metadata fields</ul><p>The table below organizes the metadata fields in alphabetical order across and then down:</p>'
		elif frame[0] == 'news':
			title = '<title>News and Opportunities documentation version ' + version  + ' (DLESE) Digital Library for Earth System Education</title>'
			header = '<h1>Documentation: News-Opps version ' +  version + '</h1>'
			para = '<p>This metadata framework documentation provides:</p><ul><li>Graphical overview, content examples, cataloging best practices</li><li>A description of each metadata field including occurrences, obligation, data types, terms and definitions</li><li>Bolded items represent controlled vocabulary terms or the name of individual metadata fields</ul><p>The table below organizes the metadata fields in alphabetical order across and then down:</p>'
		elif frame[0] == 'annotation':
			title = '<title>Annotation documentation version ' + version  + ' (DLESE) Digital Library for Earth System Education</title>'
			header = '<h1>Documentation: Annotation framework version ' +  version + '</h1>'
			para = '<p>This metadata framework documentation provides:</p><ul><li>Graphical overview, content examples, cataloging best practices</li><li>A description of each metadata field including occurrences, obligation, data types, terms and definitions</li><li>Bolded items represent controlled vocabulary terms or the name of individual metadata fields</ul><p>The table below organizes the metadata fields in alphabetical order across and then down:</p>'
		elif frame[0] == 'collection':
			title = '<title>Collection documentation version ' + version  + ' (DLESE) Digital Library for Earth System Education</title>'
			header = '<h1>Documentation: Collection framework version ' +  version + '</h1>'
			para = '<p>This metadata framework documentation provides:</p><ul><li>Graphical overview, content examples, cataloging best practices</li><li>A description of each metadata field including occurrences, obligation, data types, terms and definitions</li><li>Bolded items represent controlled vocabulary terms or the name of individual metadata fields</ul><p>The table below organizes the metadata fields in alphabetical order across and then down:</p>'
		elif frame[0] == 'objects':
			title = '<title>Objects documentation version ' + version  + ' (DLESE) Digital Library for Earth System Education</title>'
			header = '<h1>Documentation: Objects framework version ' +  version + '</h1>'
			para = '<p>This metadata framework documentation provides:</p><ul><li>Graphical overview, content examples, cataloging best practices</li><li>A description of each metadata field including occurrences, obligation, data types, terms and definitions</li><li>Bolded items represent controlled vocabulary terms or the name of individual metadata fields</ul><p>The table below organizes the metadata fields in alphabetical order across and then down:</p>'
		elif frame[0] == 'msp2':
			title = '<title>MSP2 Metadata Framework Documentation Version ' + version  + ' (NSDL) National Science Digital Library</title>'
			header = '<h1>MSP2 Metadata Documentation: version ' +  version + '</h1>'
			para = '<p>This metadata framework documentation provides:</p><ul><li>Cataloging best practices</li><li>A description of each metadata field including occurrences, obligation, data types, terms and definitions</li><li>Bolded items represent controlled vocabulary terms or the name of individual metadata fields</ul><p>The table below organizes the metadata fields in alphabetical order across and then down:</p>'
		elif frame[0] == 'osm':
			title = '<title>OSM Metadata Framework Documentation Version ' + version  + ' OpenSky Metadata Repository</title>'
			header = '<h1>OSM Metadata Documentation: version ' +  version + '</h1>'
			para = '<p>This metadata framework documentation provides:</p><ul><li>Cataloging best practices</li><li>A description of each metadata field including occurrences, obligation, data types, terms and definitions</li><li>Bolded items represent controlled vocabulary terms or the name of individual metadata fields</ul><p>The table below organizes the metadata fields in alphabetical order across and then down:</p>'
		
		#2009-08-25: there is no banner-top, banner-aftertitle to include; so comment them out	
		#htmFile = open(metadatadir + frmhtm + 'banner-top.htm', 'r')
		#contents = htmFile.read()
		#htmFile.close()
		#outputFile = open(metadatadir + x + docs +'framework-documentation.htm', 'a')
		outputFile = open(metadatadir + x + docs +'framework-documentation.htm', 'w')
		#outputFile.write(contents)
		outputFile.write(title)
		#htmFile = open(metadatadir + frmhtm + 'banner-aftertitle.htm', 'r')
		#contents = htmFile.read()
		#htmFile.close()
		#outputFile.write(contents)
		outputFile.write(header)
		outputFile.write(para)
		
		# Generate htm table of links based on field names
		#outputFile.write('<table width="75%" border="1" cellpadding="3" cellspacing="0" class="dlese_table">\n')
		outputFile.write('<table width="75%" border="1" cellpadding="3" cellspacing="0">\n')
		z= 0
		count = 1
		y = 0
		# remember the length of a list is always going to be 1 more than the allowable indexes so subtract 1
		while y <= (len(keys)-1):
			outputFile.write('<tr>\n')
			z = 1
			while z <= 3 and y <= (len(keys)-1):
				outputFile.write('<td><a href="#' + str(count) + '">' + keys[y] + '</a></td>\n')
				count = count + 1
				z = z + 1
				y = y + 1
			outputFile.write('</tr>\n')
			
		outputFile.write('</table>\n')
		# Add the images of the framework
		#htmFile = open(metadatadir + x + builddir + 'images.htm', 'r')
		#contents = htmFile.read()
		#htmFile.close()
		#outputFile.write(contents)
		# Make fields documentation part
		count = 0 # need to know the number of fields
		for z in keys:
			# need to know the number of fields
			count = count + 1
			outputFile.write('<a name="' + str(count) + '"></a>')
			fieldsFile = open(working_metadatadir + x + htmdir + fields[z][1], 'r')
			htm = fieldsFile.read()
			outputFile.write(htm)
			outputFile.write(uparrow)
		outputFile.write(dateline)
		#2009-08-25: no bottom banner
		#bottomFile = open(metadatadir + frmhtm + 'bottom-banner.htm', 'r')
		#contents = bottomFile.read()
		#outputFile.write(contents)
		#bottomFile.close()
		outputFile.close()
	
	#Ryan script only works in metadata-ui area so change directory to be there
	#2009-08-25: No longer use the metadata-ui-project
		#os.chdir('/dls/devel/ginger/projects/metadata-ui-project/scripts')
		#listing = os.popen('/export/devel/common/resources/java/jdk1.5.0_05/bin/java -cp ./lib/DleseGui.jar:./lib/xercesImpl-2.4.0.jar org.dlese.dpc.gui.OPMLtoHTMLTreeMenu ../../../../webdev2/dlese.org/docroot/Metadata/md_menu.xml -headless').readlines()
		#os.chdir('/dls/devel/ginger/scripts')
		#print listing

	else:
		print 'Framework: ' + x + ' does not exist. Not processing it.\n'

# 6
print 'For each valid framework, a framework-documentation.htm file has been generated.'
print 'After the CVS project is updated, the framework documentation resides at:'
print 'http://ns.nsdl.org/ncs/x/docs/framework-documentation.htm where x is the framework/version you entered.'
print 'Framework documentation program is done'

		
