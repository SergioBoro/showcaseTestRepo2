<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 

<%@page import="ru.curs.showcase.security.SecurityParamsFactory"%>    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=8"/>
	<title>Showcase index page</title>
    <link rel="stylesheet" href="xsltforms/xsltforms.css" type="text/css" />	
	<link rel="shortcut icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
	<link rel="icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" language="javascript" src="secured/secured.nocache.js"></script>
    
    <link rel="stylesheet" href="js/dijit/themes/claro/claro.css"/>
    <script type="text/javascript">
        var djConfig = {
            parseOnLoad: false,
            isDebug: true,
            modulePaths: {'course': '../course', 'courseApp': '../..'},
            gfxRenderer: 'svg,silverlight,vml'
        };
    </script>      
    <script src="js/dojo/dojo.js"></script>
    <script src="js/course/charting.js"></script>
    <script src="js/course/geo.js"></script>
    <script src="js/course/geo/Feature.js"></script>
    <script src="js/course/geo/Placemark.js"></script>
    <script src="js/course/geo/FeatureContainer.js"></script>
    <script src="js/course/geo/Map.js"></script>
    <script src="js/course/geo/Style.js"></script>
    <script src="js/internalShowcase.js"></script>
    <script src="solutions/default/js/solution.js"></script>
    
    <script>
     var appContextPath="Showcase";
    </script> 
    	
</head>
<body class="claro">


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
	 
</body>
</html>