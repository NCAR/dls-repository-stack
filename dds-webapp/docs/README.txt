
Installation instructions:


1. Place the WAR file 'dds.war' inside the 'webapps' directory
of your Tomcat installation. Then start or restart Tomcat.

2. Tomcat will unpack the WAR file, creating a directory 'dds' inside
the 'webapps' directory. The name of this directory will be the context 
path to the discovery system.

3. Edit the web.xml file found in dds/WEB-INF, changing the following 
parameters as desired:

- sourceFileDirectory - Change to point to the location of the DLESE-IMS
files that will be made discoverable in the system. 

- resourceResultLinkRedirectURL (optional) - If you have implemented 
pattern matching redirects in your web server, change this to point
to the base-URL of the server. See comments in web.xml for more 
details.

- Change any other parameters as desired. In general, no other parameters
need to be customized.

4. Restart Tomcat one more time so that the changes in web.xml take effect.

Note: To prevent a known stability issue with Java running
 on newer versions of Linux (GLIBC 2.2 / Linux 2.4 or
 later such as RedHat 9 and later), users should define an
 environment variable prior to starting the JVM:
 export LD_ASSUME_KERNEL=2.2.5

 Further information about this issue is posted here:
 http://jakarta.apache.org/tomcat/tomcat-5.0-doc/RELEASE-NOTES.txt
  and here:
 http://www.gurulabs.com/files/RELEASE-NOTES-RHL9.html


