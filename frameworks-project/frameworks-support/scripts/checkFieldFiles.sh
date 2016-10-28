#!/bin/csh

# Any java 1.5 will do. The next line is only valid in the DLS environment
# set JAVA_HOME = /dls/devel/common/resources/java/jdk1.5.0_09

set CP = "./lib/DLESETools.jar:./lib/dom4j.jar:./lib/relaxngDatatype.jar:./lib/xsdlib.jar"

# this is the file that contains information about the frameworks to be checked
set FP = "../xml/frameworks.xml"

echo "Running checkFieldFiles with $JAVA_HOME"
if( $#argv == 1 ) then
	if( $argv[1] == "-v") then
		echo "    running in verbose mode"
		$JAVA_HOME/bin/java -cp $CP org.dlese.dpc.schemedit.vocab.integrity.frameworks.IntegrityChecker $FP true
		exit 1
	endif
else
	echo "    to run in verbose mode, add '-v' to command line invocation"
	$JAVA_HOME/bin/java -cp $CP org.dlese.dpc.schemedit.vocab.integrity.frameworks.IntegrityChecker $FP
	exit 1
endif



