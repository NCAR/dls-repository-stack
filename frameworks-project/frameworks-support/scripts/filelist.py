#!/usr/bin/python

import os
import sys
import string

# Functions to create a listing of files in a supplied directory 


#----------------------------------------------
# listingwithdirs
#-----------------------------------------------
# Gets a listing of files with the directory name as part of the filename
# Ignores symbolic links and subdirectories in the given directory
# Need to set up a blank files [] list in order to work 

def listingwithdirs(directory):
        stack=[directory]
        files=[]
        while stack:
                directory = stack.pop()
                for file in os.listdir(directory):
                        inname = directory + file
			if os.path.isfile(inname):
				fullname = os.path.join(directory, file)
				files.append(fullname)
				if os.path.isdir(fullname) and not os.path.islink(fullname):
					stack.append(fullname)
        return files
	
#----------------------------------------------
# listingwithnodirs
#-----------------------------------------------
# Gets a listing of files without the directory name not part of the filename
# Ignores symbolic links and subdirectories in the given directory
# Need to set up a blank files [] list in order to work 

def listingwithnodirs(directory):
	if not os.path.isdir(directory):
		print 'Sorry, ' + directory + ' is not a directory.'
		sys.exit(1)
	files = []
	for file in os.listdir(directory):
		inname = directory + file
		if os.path.isfile(inname):
			files.append(file)
	return files
	
#----------------------------------------------
# listdirsonly
#-----------------------------------------------
# Gets a listing of directories within a directory 
# Does not include the directory path name as part of the directory
# Ignores symbolic links and files in the given directory
# Need to set up a blank files [] list in order to work 

def listdirsonly(directory):
	if not os.path.isdir(directory):
		print 'Sorry, ' + directory + ' is not a directory.'
		sys.exit(1)
	files = []
	for file in os.listdir(directory):
		inname = directory + file
		if os.path.isdir(inname):
			files.append(file)
	return files

#----------------------------------------------
# scan
#-----------------------------------------------
# Reads the contents of a given file line by line and puts it in a list

def scan (fname):
	lines = open(fname).readlines()
	return lines
	
#----------------------------------------------
# treedirs
#-----------------------------------------------
# Gets a listing of files with the directory name as part of the filename
# Traverses subdirectories
# Need to set up a blank files [] list in order to work 
# From the Python Standard Library (pg 34 of the book)

def treedirs(directory):
	# like os.listdir, but traverses a directory tree
        stack=[directory]
        files=[]
        while stack:
                directory = stack.pop()
                for file in os.listdir(directory):
                        fullname = os.path.join(directory, file)
                        files.append(fullname)
                        if os.path.isdir(fullname) and not os.path.islink(fullname):
                                stack.append(fullname)
        return files
	
