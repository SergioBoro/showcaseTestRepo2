<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 
<%@page import="ru.curs.showcase.security.SecurityParamsFactory"%>    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%
	String userdataId = request.getParameter("userdata");
	if (userdataId == null) {
		userdataId = "default";
	}
%>

	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=8"/>
	<title>Showcase index page</title>
    <link rel="stylesheet" href="xsltforms/xsltforms.css" type="text/css" />	
	<link rel="shortcut icon" href="solutions/<%=userdataId%>/resources/favicon.ico" type="image/x-icon" />
	<link rel="icon" href="solutions/<%=userdataId%>/resources/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" language="javascript" src="secured/secured.nocache.js"></script>
    
    <link rel="stylesheet" href="js/dijit/themes/claro/claro.css"/>
    
    <script type="text/javascript">
        var dojoConfig = {
            parseOnLoad: false,
            isDebug: false,
            paths: {'course': '../course', 'djeo': '../djeo', 'courseApp': '../..'},
            gfxRenderer: 'svg,silverlight,vml'
        };
     </script>   

	<script src="js/dojo/dojo.js" data-dojo-config="
    djeoEngine: 'djeo',
    geKey: 'ABQIAAAA-DMAtggvLwlIYlUJiASaAxRQnCpeV9jusWIeBw0POFqU6SItGxRWZhddpS8pIkVUd2fDQhzwPUWmMA',
    ymapsKey: 'AMOPgE4BAAAA9Y-BUwMAonjZ5NBRJDj54c-cDVPzQcYlLNAAAAAAAAAAAACPSuKS9WyCiMuXm9An1ZKCx5Pk-A==',
    paths: {djeo:'../djeo'}">
	</script>
	
<!-- if you plan to use Google Earth browser plugin on non localhost server - copy your own key; apply for a key at http://code.google.com/apis/maps/signup.html -->
<!-- if you plan to use Yandex Maps on non localhost server - copy your own key; apply for a key at http://api.yandex.ru/maps/form.xml	-->
	              
    <script src="js/internalShowcase.js"></script>
    <script src="solutions/<%=userdataId%>/js/solution.js"></script>
    
    <script>
     var appContextPath="<%=request.getContextPath()%>";
    </script>
              
</head>
<body class="claro">

<%
	String authGifSrc = String.format("%s/authentication.gif?sesid=%s",
			SecurityParamsFactory.getAuthServerUrl(), request.getSession()
					.getId());
%>


<!--    не удалять!-->
<!--    <div style="float:right;" ><a href="<c:url value="/logout"/>">Выйти</a> </div>-->
<!--	<a href="<c:url value="/j_spring_security_logout" />">Выйти</a>-->
<!--	<br/>-->

<script type="text/javascript" src="xsltforms/xsltforms.js">
	/* */
</script>

<div id="target"></div>

	<div id=showcaseHeaderContainer></div>
	<div id=showcaseAppContainer></div>
	<div id=showcaseBottomContainer></div>
	
    <img src="<%=authGifSrc%>" alt=" " id="authenticationImage" style="visibility:hidden; width: 0px; height: 0px" />
    
</body>
</html>