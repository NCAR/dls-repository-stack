<%-- editMeLabel.tag
	creates a linked field label for record views that will open a metadata editor to this field.
	--%><%@ tag isELIgnored ="false"
%><%@ tag language="java"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ attribute name="reader" required="true"  type="edu.ucar.dls.index.reader.XMLDocReader"
%><%@ attribute name="xpath" required="true" 
%><%@ attribute name="page"  required="true" 
%><%@ attribute name="label" required="true" %>

<a title="edit field"
	href='../editor/edit.do?command=edit&recId=${reader.id}&pathArg=${xpath}&page=${page}'>${label}</a>
