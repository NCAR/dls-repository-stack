<%-- Include all necessary tag libs and set the response type header --%><%@ page language="java" %><%@ taglib 
uri="/WEB-INF/tlds/response.tld" prefix="resp" %><%@ taglib 
uri='/WEB-INF/tlds/vocabulary.tld' prefix='vocab' %><%@ taglib 
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib 
prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %><%@ taglib 
prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ taglib 
prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@ taglib 
prefix="f" uri="http://dls.ucar.edu/tags/dlsELFunctions" %><%@ page isELIgnored ="false" %><%-- Set the response type --%><c:choose><c:when
test="${param.rt == 'text'}"><resp:setContentType>text/plain</resp:setContentType></c:when><c:when 
test="${param.rt == 'validate'}"><resp:setContentType>text/html</resp:setContentType></c:when><c:otherwise><resp:setContentType>text/xml</resp:setContentType></c:otherwise></c:choose>

