<%-- questionMarkImg.tag
	- displays the questionmark image
	
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="alt" %>
<img src="../images/open.gif" border="0"  width="15" Height="15" alt="${alt}"/>

