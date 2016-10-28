#!/bin/csh -f
#
# Hit JSPs to pre-load them so they are more responsive for users.
#
# John Weatherley

set DATE = "`date -I`"

set BASE_URL = "http://www.dlese.org"

set TMP = /var/tmp/touch_jsps.txt
set MAIL_TO = "jweather@dpc.ucar.edu"

# The JSHTML service (pre-load the menus):
wget -O /dev/null "$BASE_URL/dds/services/jshtml1-1/default.jsp" >& $TMP
wget -O /dev/null "$BASE_URL/dds/services/jshtml1-1/smart_link_query_builder.jsp" >>& $TMP
wget -O /dev/null "$BASE_URL/dds/services/ddsws1-1/service_explorer.jsp" >>& $TMP
wget -O /dev/null "$BASE_URL/dds/services/ddsws1-0/service_explorer.jsp" >>& $TMP

# Check to see if wget output an error code
if ( $status == 0 ) then
	set SUBJECT = "Touched JSP successfully on `uname -n` ($DATE)"
else
	set SUBJECT = "Error touching JSPs on `uname -n` ($DATE)"
endif

less $TMP | mail -n -s "$SUBJECT" $MAIL_TO

rm -f $TMP




