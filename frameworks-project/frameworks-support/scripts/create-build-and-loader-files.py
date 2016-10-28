#!/usr/bin/python

#python library modules
import os		
import sys	
import string	
import time
import shutil	
import ftplib
import urllib

# other python modules
import filelist

# Purpose: To generate the the fields and groups filename lists for use in the metadata-ui-project build files of
# fields-list.xml (exists for each framework)
# groups-list.xml (exists for each framework)
# CurrentMetadataUI.xml

# Note: the AllMetadataUI.xml file lists all the frameworks and versions in the project. It should be used when executing
# the vocabs_id_assign.sh script (which also call the integrity script)

# This script will generate the CurrentMetadataUI.xml file that is sent to the metadata website to be used by DDS and DCS.
# It will use the default frameworks unless a user executing the script decides otherwise. In which case the 
# CurrentMetadataUI.xml will still be produce and separate file will be produce for the user defined file. The user 
# defined file will also have the defaults that produce the CurrentMetadataUI.xml file.  

# Author: Katy Ginger
# Origanl creation date: 2006-07-21
# Last modified 2007-11-02

#----------------------------------------------
# MAIN PROGRAM
#-----------------------------------------------

#------------------------------------------------------------------------------------------------------------------------
# Set directory information
#------------------------------------------------------------------------------------------------------------------------
framedir = '../../frameworks/'
xsldir = '../xsl/'
xslfile1 = 'frameworks-xml-info-to-text.xsl'
xslfile2 = 'get-fields-name.xsl'
builddir = '/build/'
fieldsdir = '/fields/'
groupsdir = '/groups/'
txtdir = '/txt-pieces/'
play = 'play'
inputdir = 'play/input/'
outputdir = 'play/output/'
#frameworksWeb ='http://www.dlese.org/Metadata/documents/xml/frameworks.xml'
frameworksWeb ='http://www.dlsciences.org/frameworks-support/xml/frameworks.xml'
frameworksFile = '../xml/frameworks.xml'

os.mkdir(play)
os.mkdir(inputdir)
os.mkdir(outputdir)

#--------------------------------------------------------------------------------------------------------
# Determine frameworks to process
# the frameworks to process are available at the frameworksWeb variable
# create a play directory
# make a local copy of frameworks.xml from the web
# transform the frameworks.xml document to a text file
# read in the text file into a list 
# remove .xml  files which are in content both xml and text
#--------------------------------------------------------------------------------------------------------
# Method 1 - read from the Web the frameworks to process
# 2007-11-02: Decided not the best method, because with the move to the new DLESE host, the frameworks that
# are absolutely needed are adn/0.6.50, dlese_anno/1.0.00, dlese_collect/1.0.00 and news_opps/1.0.00
# but the Metadata website was listing additional frameworks like adn/0.7.00 to support other projects
# Therefore use Method 2 and ask what frameworks to produce
#2008-05-21: Now have a DLS sourceforge project that has new frameworks in it but not the DLESE frameworks.
# So still use Method 2 but the list is short.

#urllib.urlretrieve(frameworksWeb, inputdir + frameworksFile)
#urllib.urlcleanup()
#listing = os.popen('transform ' + xsldir + xslfile1 + ' ' + inputdir + ' ' +outputdir).readlines()
# print listing
#toProcess = filelist.scan(outputdir+frameworksFile)
#print ToProcess
#os.remove(outputdir + frameworksFile)
#os.remove(inputdir + frameworksFile)

# Method 2 - process all frameworks hard coded here.

#toProcess = ['adn-item/0.6.50*http://www.dlese.org/Metadata/adn-item/0.6.50/record.xsd*',
#'adn-item/0.7.00*http://www.dlese.org/Metadata/adn-item/0.7.00/record.xsd*',
#'annotation/1.0.00*http://www.dlese.org/Metadata/annotation/1.0.00/annotation.xsd*',
#'annotation/0.1.01*http://www.dlese.org/Metadata/annotation/0.1.01/annotation.xsd*',
#'news-opps/1.0.00*http://www.dlese.org/Metadata/news-opps/1.0.00/news-opps.xsd*',
#'collection/1.0.00*http://www.dlese.org/Metadata/collection/1.0.00/collection.xsd*']

toProcess = ['mast/1.0*http://www.dlsciences.org/frameworks/mast/1.0/mast-dc-gem.xsd*']


#--------------------------------------------------------------------------------------------------------------
# Process frameworks
# 1. Capture the framework to process and its schema uri in separate variables
# 2. Open the files that will be generated in the build directory
# 3. Transform the fields files 
# 4. Sort lists for fields and filename lists
# 5. Write output for fields and filename list files
# 6. Sort groups lists and write it to a file
# 7. Copy framework mui files to the metadata website on dev and qa (NO LONGER DOING)
# 8. Remove play and temporary files
# 9. Remove play and temporary directories
# 10. Ask about the base loader file, CurrentMetadataUI.xml and if anyother base loader files need to be made
# 11. Copy base loader files for the project to the metadata website on dev and qa (NO LONGER DOING)
#---------------------------------------------------------------------------------------------------------------

#---------------------------------------------------------------------------------------------------------------
#1 Capture schema and framework to process
#---------------------------------------------------------------------------------------------------------------
print ''
print 'This script creates the build files for MUI fields and groups files (fields-list.xml; groups-list.xml).'
print 'This script also creates the CurrentMetadataUI.xml file. More info on this file follows later.'
print ''
print 'The script processes the metadata frameworks and versions assoicated with the MUI project.'
print 'These frameworks and versions are hardcoded in this script. To change, edit the "toProcess" sequence.' 
print ''
print 'The script indicates the metadata frameworks and versions being processed.'
print ''
for x in toProcess:
	data = str.split(x,'*')
#	print data
	frame = data[0]
	uri = data[1]
	print 'Processing: '+ frame

#---------------------------------------------------------------------------------------------------------------
# 2 Open files
#---------------------------------------------------------------------------------------------------------------
	# filename-list file
	#2007-11-02 The file has been retired. No longer needed by DCS.
	#outputFile = open(framedir + frame + builddir + 'filename-list.xml', 'w')
	#outputFile.write('<?xml version="1.0" encoding="UTF-8" ?>\n')
	#outputFile.write('<!-- alphabetical listing of framework fields filenames -->\n')
	#outputFile.write('<!-- alphabetical listing of framework fields field names -->\n')
	#outputFile.write('<metadataFieldsInfo>\n')
	#outputFile.write('	<files uri="http://www.dlese.org/Metadata/' + frame + fieldsdir + '">\n')
		
	# fields-list file		
	output2File = open(framedir + frame + builddir + 'fields-list.xml', 'w')
	output2File.write('<?xml version="1.0" encoding="UTF-8" ?>\n')
	output2File.write('<!-- alphabetical listing of framework fields filenames -->\n')
	output2File.write('<!-- alphabetical listing of framework fields field names -->\n')
	output2File.write('<metadataFieldsInfo>\n')
	output2File.write('	<files>\n')

	# groups-list file
	# be sure to list the default files first
	output3File = open(framedir + frame + builddir + 'groups-list.xml', 'w')
	output3File.write('<?xml version="1.0" encoding="UTF-8" ?>\n')
	output3File.write('<!-- alphabetical listing of framework groups filenames -->\n')
	output3File.write('<metadataGroupsInfo>\n')
	output3File.write('	<files>\n')
	#outputFile.write('	<files uri="http://www.dlese.org/Metadata/' + x + groupsdir + '">\n')
	
#---------------------------------------------------------------------------------------------------------------
# 3 Transform files
#---------------------------------------------------------------------------------------------------------------
	# use inputdir and outputdir as the play dirs
	# transform from fields to inputdir
	listing = os.popen('transform ' + xsldir + xslfile2 + ' ' + (framedir + frame + fieldsdir + ' ') + inputdir).readlines()
	files = filelist.listingwithnodirs(inputdir)
	#print listing
	#print files

#---------------------------------------------------------------------------------------------------------------
#4 Sort lists
#---------------------------------------------------------------------------------------------------------------
	# need to create a dictionary to associate the field name with its xpath and the appropriate file name (so can be sorted)
	fields = {}
	for y in files:
		# z is a list with z[0] of the form 'fieldname:xpath'
		z = filelist.scan(inputdir + y)
		fieldsList = string.split(z[0], ':')
		# fieldsList is a list of the form ['fieldname', 'xpath']
		# {fieldname 1: [xpath, filename-no-dir], fieldname 2: [xpath, filename-no-dir]}
		fields[fieldsList[0]] = [fieldsList[1], y]

	keys = fields.keys()
	keys.sort()
	# write the XML <file> lines first and the <field> lines second
	# fields[z][1] is the filename-no-dir
	# fields[z][0] is the xpath

#---------------------------------------------------------------------------------------------------------------
# 5 Write output for fields and filename list files
#---------------------------------------------------------------------------------------------------------------
        #2007-11-02 The file associated with outputFile has been retired. No longer needed by DCS.
	for y in keys:
		output2File.write('		<file>' + frame+fieldsdir+fields[y][1] + '</file>\n')
#		outputFile.write('		<file>' + fields[y][1] + '</file>\n')
			
#	outputFile.write('	</files>\n')
	output2File.write('	</files>\n')
	#fields field names
#	outputFile.write('	<fields>\n')
	output2File.write('	<fields>\n')
	for y in keys:
#		outputFile.write('		<field>' + y + '</field>\n')
		output2File.write('		<field>' + y + '</field>\n')
#	outputFile.write('	</fields>\n')
	output2File.write('	</fields>\n')
#	outputFile.write('</metadataFieldsInfo>\n')
	output2File.write('</metadataFieldsInfo>\n')
#	outputFile.close()
	output2File.close()

#---------------------------------------------------------------------------------------------------------------
# 6 Sort groups list info and write it to a file		
#---------------------------------------------------------------------------------------------------------------
	groupslist = filelist.listingwithnodirs(framedir + frame + groupsdir)
	groupslist.sort()
	groupsdefault=[]
	groupsaudience=[]
	for y in groupslist:
		# remember string.find returns a number greater than 0 if found and if statement evaluation to 1 or 0
		if string.find(y, 'default') > 0:
			groupsdefault.append(y)
		else:
			groupsaudience.append(y)

	for y in groupsdefault:
		output3File.write('		<file>' + frame+groupsdir +y + '</file>\n')
		#outputFile.write('		<file>' + y + '</file>\n')
	for y in groupsaudience:
		output3File.write('		<file>' + frame+groupsdir+y + '</file>\n')			
		#outputFile.write('		<file>' + y + '</file>\n')
	output3File.write('	</files>\n')
	output3File.write('</metadataGroupsInfo>\n')
	output3File.close()

#---------------------------------------------------------------------------------------------------------------
# 7 Copy framework mui files to the metadata website on dev and qa
#---------------------------------------------------------------------------------------------------------------
#2007-11-02: No copying to the metadata website (dev or qa) can be done since DLESE moved to a new host.
# There is no ability to do local copying.
# 2008-05-21: See script copy-frameworks.py to copy frameworks to dlsciences.org.

# first remove fields files in metadata dev (in case files have been removed from the project)
	#removeList = filelist.listingwithdirs(metadev + frame + fieldsdir)
	#for z in removeList:
	#	os.remove(z)

# first remove fields files in metadata qa (in case files have been removed from the project)
	#removeList = filelist.listingwithdirs(metaqa + frame + fieldsdir)
	#for z in removeList:
	#	os.remove(z)
		
# first remove groups files in metadata dev (in case files have been removed from the project)
	#removeList = filelist.listingwithdirs(metadev + frame + groupsdir)
	#for z in removeList:
	#	os.remove(z)

# first remove groups files in metadata dev (in case files have been removed from the project)
	#removeList = filelist.listingwithdirs(metaqa + frame + groupsdir)
	#for z in removeList:
	#	os.remove(z)
		
# then copy fields files to metadata dev
# the list 'files' already has the list of fields files	
	#for y in files:
	#	shutil.copy((framedir + frame + fieldsdir + y),(metadev + frame + fieldsdir + y))
	#	shutil.copy((framedir + frame + fieldsdir + y),(metaqa + frame + fieldsdir + y))
		
# then copy groups files to metadata dev
# the list 'groupslist' has the list of groups files
	#for y in groupslist:
	#	shutil.copy((framedir + frame + groupsdir + y),(metadev + frame + groupsdir + y))
	#	shutil.copy((framedir + frame + groupsdir + y),(metaqa + frame + groupsdir + y))
		
# then copy build directory files
# they do not have to be removed first since always the same 3 files 
	#buildList = filelist.listingwithnodirs(framedir + frame + builddir)
	#for y in buildList:
	#	shutil.copy((framedir + frame + builddir + y),(metadev + frame + builddir + y))		 
	#	shutil.copy((framedir + frame + builddir + y),(metaqa + frame + builddir + y))

# write a note to user
	#print 'metdata website (DEV and QA) updated: old fields and groups removed and new ones added'
	#print ' '
		
#---------------------------------------------------------------------------------------------------------------		
# 8 Remove play and temporary files
#---------------------------------------------------------------------------------------------------------------
	removeList = filelist.listingwithdirs(inputdir)
	for z in removeList:
		os.remove(z)

#---------------------------------------------------------------------------------------------------------------
#  9 Remove play and temporary directories			
#---------------------------------------------------------------------------------------------------------------
os.rmdir(inputdir)
os.rmdir(outputdir)
os.rmdir(play)

#---------------------------------------------------------------------------------------------------------------
#   10 Create base loader file of CurrentMetadataUI.xml and a user-defined base loader file if need be
#---------------------------------------------------------------------------------------------------------------

forDLESE = ['adn-item/0.6.50','annotation/1.0.00','news-opps/1.0.00','collection/1.0.00']
userDefined = ['mast/1.0']

# CurrentMetadataUI.xml file
outputFile = open(framedir + 'CurrentMetadataUI.xml', 'w')
outputFile.write('<?xml version="1.0" encoding="UTF-8" ?>\n')
outputFile.write('<!-- List of the builder fields and groups files for each metadata framework-->\n')
outputFile.write('<!-- Fields files contain metadata field definitions, terms (and definitions) and cataloging best practices-->\n')
outputFile.write('<!-- Groups files contain audience-based groupings and layout of controlled vocabulary terms-->\n')
outputFile.write('<metadataInfoLists>\n')
outputFile.write('<files>\n')
for x in userDefined:
#for x in forDLESE:
	outputFile.write('<groups>' + x +'/build/groups-list.xml</groups>\n')
for x in userDefined:
#for x in forDLESE:
	outputFile.write('<fields>' + x +'/build/fields-list.xml</fields>\n')
outputFile.write('</files>\n')
outputFile.write('</metadataInfoLists>\n')
outputFile.close()

print ''
print '' 
print 'This script creates the overall base loader file (CurrentMetadataUI.xml) that is used by DDS and DCS.'
print 'The CurrentMetadataUI.xml will include the following frameworks and versions:'
print ''
for z in userDefined:
#for z in forDLESE:
	print z
	print ''
print 'You may create another base loader file by copying the CurrentMetadataUI.xml to a new file.'
print 'The additional frameworks/versions that can be added to this user-defined base loader file include:'
print ''
for x in toProcess:
	data = str.split(x,'*')
#	print data
	frame = data[0]
	if not frame in userDefined:
	#if not frame in forDLESE:
		print  frame 

print ''
print ''
		
#--------------------------------------------------------------------------------------------------------		
# 11 Copy base loader files for the project to the metadata website on dev and qa
#---------------------------------------------------------------------------------------------------------------

#2007-11-02: No copying to the metadata website (dev or qa) can be done since DLESE move to a new host.
# There is no ability to do local copying.

# 2008-5-21: see script copy-frameworks.py in DLS frameworks-project. It copies all frameworks and needed files to dlsciences.org

#metadev = '/dls/devweb/webdev2/dlese.org/docroot/Metadata/'
#metaqa = '/dls/devel/qa/www.dlese.org/docroot/Metadata/'

#shutil.copy((framedir + 'CurrentMetadataUI.xml'), (metadev + 'CurrentMetadataUI.xml')) 
#shutil.copy((framedir + 'AllMetadataUI.xml'), (metadev + 'AllMetadataUI.xml'))
#shutil.copy((framedir + 'LibraryV2.3MetadataUI.xml'), (metadev + 'LibraryV2.3MetadataUI.xml'))


