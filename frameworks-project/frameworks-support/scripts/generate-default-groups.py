#!/usr/bin/python

#python library modules
import os		
import sys	
import string	
import shutil	
import urllib

# other python modules
import filelist

# Purpose: To generate the default groups files from the  fields in the metadata-ui-project
# Author: Katy Ginger
# Origanl creation date: 2006-08-03


#----------------------------------------------
# MAIN PROGRAM
#-----------------------------------------------

#------------------------------------------------------------------------------------------------------------------------
# Set directory information
#------------------------------------------------------------------------------------------------------------------------
framedir = '../../frameworks/'
xsldir = '../xsl/'
xslfile1 = 'frameworks-xml-info-to-text.xsl'
xslfile2 = 'fields-1.0-to-groups-OPML-1.0.xsl'
fieldsdir = '/fields/'
groupsdir = '/groups/'
play = 'play'
inputdir = 'play/input/'
outputdir = 'play/output/'
frameworksWeb ='http://www.dlese.org/Metadata/documents/xml/frameworks.xml'
frameworksFile = 'frameworks.xml'

#--------------------------------------------------------------------------------------------------------
# Determine frameworks to process
# the frameworks to process are available on at: http://www.dlese.org/Metadata/documents/xml/frameworks.xml
# create a play directory
# make a local copy of frameworks.xml from the web
# transform the frameworks.xml document to a text file
# read ithe text file into a list 
# remove .xml  files which are in content both xml and text
#--------------------------------------------------------------------------------------------------------
os.mkdir(play)
os.mkdir(inputdir)
os.mkdir(outputdir)

# Retrieves DLESE web-based version of frameworks.xml and puts it in play/input with filename frameworks.xml 
urllib.urlretrieve(frameworksWeb, inputdir + frameworksFile)
urllib.urlcleanup()

# Comment out above URL retrieve lines to use the frameworks.xml file that is part of the frameworks-project
# Thus, anyone can control what frameworks are then processes.
# Still need to copy local version to the play/input directory so that the rest of the script works
# In the frameworks-project, frameworks.xml lives in:
# frameworks-project/frameworks-support directory while this script is executed in the 
# frameworks-project/frameworks-support/scripts directory.
shutil.copyfile( '../' + frameworksFile, inputdir + frameworksFile)

listing = os.popen('transform ' + xsldir + xslfile1 + ' ' + inputdir + ' ' +outputdir).readlines()
#print listing
toProcess = filelist.scan(outputdir+frameworksFile)
#print ToProcess
os.remove(outputdir + frameworksFile)
os.remove(inputdir + frameworksFile)

#--------------------------------------------------------------------------------------------------------------
# Process frameworks
# 1. Capture the framework to process and it schema uri in separate variables
# 2. Transform the fields files 
# 3. Rename files
# 4. Remove play and temporary directories
#---------------------------------------------------------------------------------------------------------------

#---------------------------------------------------------------------------------------------------------------
#1 Capture schema and framework to process
#---------------------------------------------------------------------------------------------------------------
for x in toProcess:
	data = str.split(x,'*')
#	print data
	frame = data[0]
	print 'Processing: '+ frame
	
#---------------------------------------------------------------------------------------------------------------
# 2 Transform files
#---------------------------------------------------------------------------------------------------------------
	# transform from fieldsdir to groupsdir
	print 'Using fields files to make default groups files'
	listing = os.popen('transform ' + xsldir + xslfile2 + ' ' + (framedir + frame + fieldsdir + ' ') + (framedir + frame + groupsdir + ' ')).readlines()
	#print listing

#---------------------------------------------------------------------------------------------------------------
# 3 Change names of files from fields to groups
#---------------------------------------------------------------------------------------------------------------
	#be in groupsdir
	print 'Renaming groups files to default groups files'
	print ' '
	os.chdir(framedir + frame + groupsdir)
	listing = os.popen('mv_it  fields groups-default').readlines()
	#print listing
	os.chdir('../../../../frameworks-support/scripts')
	
#---------------------------------------------------------------------------------------------------------------
#  4 Remove play and temporary directories			
#---------------------------------------------------------------------------------------------------------------
os.rmdir(inputdir)
os.rmdir(outputdir)
os.rmdir(play)	
