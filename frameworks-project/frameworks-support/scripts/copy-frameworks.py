#!/usr/bin/python

#python library modules
import os		
import sys	
import string	
import shutil	

# other python modules
import filelist

# PURPOSE: 
# To copy the contents of the CVS frameworks-project to a web development area
# Author: Katy Ginger
# Origanl creation date: 2008-02-20
# assumes the cvs project is checked out in a play area and ready to be copied

# SET directory information

devwebbase1 = '/dls/devweb/www.dlsciences.org/docroot/frameworks/'
devwebbase2 = '/dls/devweb/www.dlsciences.org/docroot/frameworks-support/'
paths = ['../../frameworks/', '../']
#devwebbase1 = '/dls/devel/ginger/play/webdev/frameworks/'
#devwebbase2 = '/dls/devel/ginger/play/webdev/frameworks-support/'

# STEP 1: Clean destinations by deleting all files 
shutil.rmtree(devwebbase1)
shutil.rmtree(devwebbase2)

# STEP 2: Remove CVS files and directories from the project
for x in paths:
	all = filelist.treedirs(x)	
	for y in all:
		z = string.find(y, 'CVS')
		if z != -1 and os.path.isdir(y):
			shutil.rmtree(y)

# STEP 3: Copy appropriate files to destination
shutil.copytree('../../frameworks', devwebbase1)
shutil.copytree('../', devwebbase2)
