<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="ru.curs.showcase.security.SecurityParamsFactory"%>
<%@page import="ru.curs.showcase.runtime.UserDataUtils"%>      
<%@page import="ru.curs.showcase.runtime.ExternalClientLibrariesUtils"%>
<%@page import="ru.curs.showcase.runtime.AppInfoSingleton"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%
	String host = request.getRemoteHost();
	String userdataId = request.getParameter("userdata");	
	String query = request.getQueryString();
	
	if(userdataId != null){
		AppInfoSingleton.getAppInfo().getHostUserdataMap().put(host, query);
	}
	if (userdataId == null) {
		userdataId = "default";
		if(AppInfoSingleton.getAppInfo().getHostUserdataMap().get(host) == null) {
			if(query != null) {
				AppInfoSingleton.getAppInfo().getHostUserdataMap().put(host, query + "&userdata=" + userdataId);
			}
			if(query == null) {
				AppInfoSingleton.getAppInfo().getHostUserdataMap().put(host, "userdata=" + userdataId);
			}
		}
	} 
	
	String title = "Showcase index page";
	if (UserDataUtils.getOptionalProp("index.title", userdataId) != null) {
		title = UserDataUtils.getOptionalProp("index.title", userdataId);
	}
%>

	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!--
	<meta http-equiv="X-UA-Compatible" content="IE=8"/>
-->	
	<title><%=title%></title>
	
<%
if(request.getParameter("userdata") == null && AppInfoSingleton.getAppInfo().getHostUserdataMap().get(host).contains("userdata=")) {
%>
<script type="text/javascript">
var host = window.location.host;
var path = window.location.pathname;
var protocol = window.location.protocol;
window.location.replace(protocol + "//" + host + path + "?<%=AppInfoSingleton.getAppInfo().getHostUserdataMap().get(host)%>");
</script>
<%}%>

    <link rel="stylesheet" href="xsltforms/xsltforms.css" type="text/css" />	
	<link rel="shortcut icon" href="solutions/<%=userdataId%>/resources/favicon.ico" type="image/x-icon" />
	<link rel="icon" href="solutions/<%=userdataId%>/resources/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" language="javascript" src="secured/secured.nocache.js"></script>
    
    <link rel="stylesheet" href="js/dijit/themes/claro/claro.css"/>
    <link rel="stylesheet" href="js/dojox/calendar/themes/claro/Calendar.css"/>
    <link rel="stylesheet" href="js/dojox/calendar/themes/claro/MonthColumnView.css"/>
    
      <%=ExternalClientLibrariesUtils.addExternalCSSByStaticMetod(request.getParameter("userdata"))
  %>  
    
    <script>
        var dojoConfig = {
//        	deps: [ "dojox/mobile", "dojox/mobile/parser", "dojox/mobile/compat" ],
// нужно для поддержки мобильности в dgrid'ах,  
// закомменчено поскольку не работали клики в аккордеоне 

            parseOnLoad: false,
            isDebug: false,            
            djeoEngine: 'djeo',
            geKey: '',
            ymapsKey: '<%=UserDataUtils.getGeoMapKey("ymapsKey", request.getServerName())%>',            
            paths: {'course': '../course', 'djeo': '../djeo', 'courseApp': '../..'},
            gfxRenderer: 'svg,silverlight,vml'
        };
     </script>   
     <script src="js/dojo/dojo.js"></script>   
	 <script src="js/jscolor/jscolor.js"></script>     
	
<!-- Google Earth not need keys now! -->
<!-- if you plan to use Yandex Maps on non localhost server - copy your own key; apply for a key at http://api.yandex.ru/maps/form.xml	-->
<!-- for store keys use files in userdata root!  -->

    <script src="js/internalShowcase.js"></script>
    <script src="solutions/<%=userdataId%>/js/solution.js"></script>
    <script>
     var appContextPath="<%=request.getContextPath()%>";
    </script>
    
  <%=ExternalClientLibrariesUtils.addExternalLinksByStaticMetod(request.getParameter("userdata"))
  %>  

     
</head>
<body class="claro">

<%
	String authGifSrc = String.format("%s/authentication.gif?sesid=%s",
			SecurityParamsFactory.getAuthServerUrl(), request.getSession()
					.getId());

    authGifSrc = SecurityParamsFactory.correctAuthGifSrcRequestInCaseOfInaccessibility(authGifSrc);
%>


<!--    не удалять!-->
<!--    <div style="float:right;" ><a href="<c:url value="/logout"/>">Выйти</a> </div>-->
<!--	<a href="<c:url value="/j_spring_security_logout" />">Выйти</a>-->
<!--	<br/>-->

<script type="text/javascript" src="xsltforms/xsltforms.js">
	/* */
</script>


<div id="target"></div>
<div id="mainXForm" style="display: none;"></div>


     <!--[if lte IE 7]>
     <p style="margin: 0.2em 0; background: #ccc; color: #000; padding: 0.2em 0;">Ваша текущая версия Internet explorer устарела. Приложение будет работать некорректно. <a href="http://browsehappy.com/">Обновите свой браузер!</a></p>
     <![endif]-->

	<div id="showcaseHeaderContainer"></div>
	<div id="showcaseAppContainer"></div>
	<div id="showcaseBottomContainer"></div>
	
<%if ("true".equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication"))) {%><img src="<%=authGifSrc%>" alt=" " id="authenticationImage" style="visibility:hidden; width: 0px; height: 0px" /><%}%>
    
</body>
</html>