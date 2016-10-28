<%=(request.getScheme() + "://" + request.getServerName()  + (request.getServerPort() == 80? "" : (":" + request.getServerPort())) + request.getContextPath()).trim()%>
