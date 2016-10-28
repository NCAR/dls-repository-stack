<%-- editorViewEditMeLabel.tag
	creates a linked field label for metadata editor "viewRecord" page that will take user to the input field for editing .
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="xpath" required="true" %>
<%@ attribute name="page"  required="true" %>
<%@ attribute name="label" required="true" %>

<a title="edit field" href="javascript:changeForm ('${page}', '${xpath}')">${label}</a>
