The DDSWebService client template and examples show how
to use the DDS web service (ddsws v1.1) in a JSP client. 

To install and run the template and examples you must have the following:
  
1. search.war - the JSP web service client template and examples.
2. Java2 platform v1.4 or later. 
3. Apache Tomcat 5 or later or other JSP 2.0 compliant JSP container.

Download the template and examples from: 
http://sourceforge.net/project/showfiles.php?group_id=23991&package_id=123037
After downloading, unzip the archive to get the search.war file

------- Installing the examples in Tomcat -------

Step 1: Download and install the Tomcat (Tomcat 5 or later required). 
To install Tomcat, follow the instructions available from Apache at
http://jakarta.apache.org/tomcat/index.html 
(see the 'User Guide' in the Tomcat 5 documentation area). If installing on 
Windows, be sure to follow Tomcat's instructions for enabling
JSP compiling by copying the JDK lib\tools.jar to your Tomcat common\lib 
directory.
You must install a Java2 SDK v1.4 (or later) prior to installing Tomcat. 
Java2 is available from Sun at:
http://java.sun.com/j2se/
For Mac OSX (Jaguar or later): Java2 comes pre-installed or may be installed
using software update.

Step 2: Place the file 'search.war' into the 'webapps' directory found in    
your Tomcat installation.
Tomcat will automatically unpack the 'search.war' archive, creating a directory and 
application context named 'search'. 

Step 3: The template and examples should now be ready to view. Launch a
web browser and type in the URL to the web service servlet context, which will
have the following form: http://localhost:8080/search/ or 
http://www.myserver.edu:8080/search/
- The address will use the context path created in step 3 ('search'). 
If the domain name to your server is http://www.myserver.edu and Tomcat 
was been installed using the default port (8080), the URL to the examples
software will be http://www.myserver.edu:8080/search/
- Alternatively you may access the software using the localhost domain
shortcut: http://localhost:8080/search/.
- If there are errors accessing the software try repeating or verifying    
steps 1 and 2.
- Tip: Check the Tomcat logs for error messages located in the 'logs' 
directory of your Tomcat installation if there are problems.
  
Step 4 (optional): Edit and modify the JSP pages 
located in webapps/search as desired. See comments included at the top of the 
JSP page for details. 
Tip: You may edit the HTML, CSS and JavaScript in each JSP page as desired to provide 
a custom look and feel for your search interface and rename, duplicate or move the JSP
files within the Tomcat context as needed. 

Additional support is available by e-mailing support@dlese.org.

