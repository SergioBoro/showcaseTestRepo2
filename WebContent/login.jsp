<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<%@page import="ru.curs.showcase.security.SecurityParamsFactory"%>   
<%@page import="ru.curs.showcase.runtime.UserDataUtils"%> 
<%@page import="ru.curs.showcase.security.esia.ESIAManager"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<%
	String title = "Авторизация в КУРС: Showcase";
	if (UserDataUtils.getGeneralOptionalProp("login.title") != null) {
		title = UserDataUtils.getGeneralOptionalProp("login.title");
	}
%>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!--
	<meta http-equiv="X-UA-Compatible" content="IE=8"/>
-->	
	<title><%=title%></title>
	<link rel="shortcut icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
	<link rel="icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
	
    <script type="text/javascript">
    	var djConfig = {
            parseOnLoad: false,
            isDebug: false
        };
    </script>  
	<script src="js/dojo/dojo.js"></script>
	
<script type="text/javascript">

	function checkAuthenticationImageSize() {
		var w;
		<%if (UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication") != null && "true".equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication").trim())) {%>
		var pic = document.getElementById("authenticationImage");
		w = pic.offsetWidth;<%}%>  
		<%if (UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication") == null || !("true".equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication").trim()))) {%>
		w = 1000;
		<%}%>
		
		if (w == 178) {	
			if (document.getElementById('helloMessage')) 
		 		dojo.attr("helloMessage", "innerHTML", "");
			if (document.getElementById('informationMessage')) 
				dojo.attr("informationMessage", "innerHTML", "Идет проверка подлинности пользователя...<br>Пожалуйста подождите...");
			id = setTimeout("checkIsAuthenticatedSession()",1000);
		}
		else {			
		    document.formlogin.style.display = "";
		    document.getElementById("j_username").focus();
		}
	}
	
	function checkIsAuthenticatedSession() {
		dojo.xhrGet({
			sync: true,
			url: "<%=request.getContextPath()%>/auth/isAuthenticatedServlet?sesid=<%=request.getSession().getId()%>",
			handleAs: "json",
			preventCache: true,
			timeout: 10000,
			load: function(data) {
				document.getElementsByName("j_username")[0].value = data.login;
				document.getElementsByName("j_password")[0].value = data.pwd;
				document.formlogin.submit();
			},
			error: function(error) {
				alert("Ошибка соединения с сервером аутентификации.");
			}
		});
	}
   

   
</script>	
	
	
	
</head>
<body onLoad="checkAuthenticationImageSize()">

     <!--[if lte IE 7]>
     <p style="margin: 0.2em 0; background: #ccc; color: #000; padding: 0.2em 0;">Ваша текущая версия Internet explorer устарела. Приложение будет работать некорректно. <a href="http://browsehappy.com/">Обновите свой браузер!</a></p>
     <![endif]-->

<%  
	if(UserDataUtils.getGeneralOptionalProp("security.ssl.keystore.path") != null){
		System.setProperty("javax.net.ssl.trustStore", 
				UserDataUtils.getGeneralOptionalProp("security.ssl.keystore.path").trim());
	}
	
	String authGifSrc = String.format("%s/authentication.gif?sesid=%s",
			SecurityParamsFactory.getAuthServerUrl(), request.getSession()
					.getId());

	authGifSrc = SecurityParamsFactory.correctAuthGifSrcRequestInCaseOfInaccessibility(authGifSrc);
%>
<c:if test="${not empty param.error}">
<div id="accessDenied">
  <font color="red">
  <b>Ошибка!</b>
  <br/>
  Имя пользователя и/или пароль неверны!<br/>
  Отказано в доступе. <br/>
  Ответ сервера: ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} 
  <br/>
  </font>
  </div>
</c:if>
<span id="helloMessage" style="font-size: 27px;color:green">Авторизация в КУРС: Showcase</span>
<span id="informationMessage" style="font-family: sans-serif;"></span>
<form name="formlogin" method="POST" action="<c:url value="/j_spring_security_check" />" style="display:none">
<table>


<tr>			    <td align='right'>Домен</td>

<%=SecurityParamsFactory.getHTMLTextForPrividerGroupsComboBoxSecector(authGifSrc)%>

</tr>
<!--   <tr> -->
<!--     <td align="rigfht">Домен</td> -->
<!--     <td> -->
<!--      <select id="j_domain" type="text" name="j_domain"> -->
<!--       <option value="Группа1">Группа1</option> -->
<!--       <option selected  value="Группа2">Группа2</option> -->
<!--      </select> -->
<!--     </td> -->
<!--   </tr> -->

  <!--   test -->
  
  
  
  
  <tr>
    <td align="right">Имя пользователя</td>
    <td><input id="j_username" type="text" name="j_username" /></td>
    <td></td>
  </tr>
  <tr>
    <td align="right">Пароль</td>
    <td><input  id="j_password" type="password" name="j_password" autocomplete = "off"/></td>
    <td></td>
  </tr>
  <tr style="display: none;">
    <td align="right">Запомнить меня</td>
    <td><input type="checkbox" name="_spring_security_remember_me" /></td>
    <td></td>
  </tr>
  
  <!--    
    <td><a href="https://www.yandex.ru/">
    4Вход с помощью учетной записи портала госуслуг</a></td>    

    <td><a href="https://esia-portal1.test.gosuslugi.ru/aas/oauth2/ac?access_type=offline&scope=openid+http%3A%2F%2Fesia.gosuslugi.ru%2Fusr_inf&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%3A8081%2FShowcase&state=b5fbf220-5a2e-4771-a9f4-5b3fe2ce2e28&client_secret=MIIGsQYJKoZIhvcNAQcCoIIGojCCBp4CAQExDzANBglghkgBZQMEAgEFADALBgkqhkiG9w0BBwGgggPHMIIDwzCCAqugAwIBAgIJALdLvXHF-1FwMA0GCSqGSIb3DQEBCwUAMHgxCzAJBgNVBAYTAlJVMQ8wDQYDVQQIDAZSdXNzaWExDzANBgNVBAcMBk1vc2NvdzESMBAGA1UECgwJUk5JTVUgbHRkMQ8wDQYDVQQDDAZTZXJnZXkxIjAgBgkqhkiG9w0BCQEWE3Nzc2xlcGNvdkBnbWFpbC5jb20wHhcNMTYwOTIzMTA1NjA4WhcNMTcwOTE4MTA1NjA4WjB4MQswCQYDVQQGEwJSVTEPMA0GA1UECAwGUnVzc2lhMQ8wDQYDVQQHDAZNb3Njb3cxEjAQBgNVBAoMCVJOSU1VIGx0ZDEPMA0GA1UEAwwGU2VyZ2V5MSIwIAYJKoZIhvcNAQkBFhNzc3NsZXBjb3ZAZ21haWwuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4bkAshSXkZTuDNl5rCO7mZ_BubhVUtosNbdqEJQZkwuVuE8fG-kpBx2XnImiGkrkatth8epba4FSoWf2scB317_qeCkWJbuB35Y_bmEOxnvYTJO45EqgON_oNCDmQU9ifkHIcTXVoQj0cVFP6PQK-dpoHmyz3kNFHAgCXhJOL4-Qnj1__D92tnuXv3NEQPUY5wS_46STNGHrjz_6caMQsSpj0pSIFo98s6ose5suJYgdBHJ5knkcVj15MQOibAZ8RBQw8zLZOuF7S7JZKYQSQFLsY7LOxx-5PlCHtbd1gU0MCIF9jxXX0ap9M8s61rxfqA1153r7o9qDRQPyLZq1dQIDAQABo1AwTjAdBgNVHQ4EFgQU_P8wnrlJ-du5WB8kFm-zfOgdQzgwHwYDVR0jBBgwFoAU_P8wnrlJ-du5WB8kFm-zfOgdQzgwDAYDVR0TBAUwAwEB_zANBgkqhkiG9w0BAQsFAAOCAQEAwZY_6XWiAjmW5dluAS_hSJck8y2cB4s8eZ5M0_KY0bV6U2dLdwpYpLq6OG3jYWkGXZY4YleFYsavo8GRVHT9adM752Hvgsoi8RdlIdgTinkPFPH67rit5sipn6d-2DhekXno1ytiAfJJhRb8i0S6f21ySAXvY8nCvhgF0GAbbv2kPZzsfwvJPjmmyUtAKgQ1KBlEmEv5aOBsG5B5r7YZZcCUQ8SSLsUH-ZKH8yKWarNYMks7t6vf-IcB9oW4IRjTAI6HBplSzPJ29vXid0dxUXEW7A0nr-82CK5ZoLE1-q86sRfjT6IJl4SykdmWP2_O9gDgQjlzA3ZtqLYpoXoZMTGCAq4wggKqAgEBMIGFMHgxCzAJBgNVBAYTAlJVMQ8wDQYDVQQIDAZSdXNzaWExDzANBgNVBAcMBk1vc2NvdzESMBAGA1UECgwJUk5JTVUgbHRkMQ8wDQYDVQQDDAZTZXJnZXkxIjAgBgkqhkiG9w0BCQEWE3Nzc2xlcGNvdkBnbWFpbC5jb20CCQC3S71xxftRcDANBglghkgBZQMEAgEFAKCB-jAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0xNzAyMTMxMDA2NDhaMC8GCSqGSIb3DQEJBDEiBCD_VrvIAPGVWF_5dH7K8jb9XtG27hKAjvSkfNfTMkoNbzCBjgYJKoZIhvcNAQkPMYGAMH4wCwYJYIZIAWUDBAEqMAgGBiqFAwICCTAIBgYqhQMCAhUwCwYJYIZIAWUDBAEWMAsGCWCGSAFlAwQBAjAKBggqhkiG9w0DBzAOBggqhkiG9w0DAgICAIAwDQYIKoZIhvcNAwICAUAwBwYFKw4DAgcwDQYIKoZIhvcNAwICASgwDQYJKoZIhvcNAQEBBQAEggEA26RNmpPlYmXVPPMV_I-8xISA9iVFcBwBUB5xikohOjignSan8ljpWcJyhbkcP2k7TcB3Xh5A-EM7HpWGZplCeLZWU76OLHWi35uavRW_0eNyYzXHMHGWCE_jsR7ieIh2b5PPC7xS0n9DaFZHQES-HYjNunk07NXR0YY3IVnKw85mGJyZuZb0Jsz6yIL6XesKtfm7Javou8yDc89vV2k-Kx2u0Q8QOn0vvmGumFKC8cYirHUdXIqxeMmqd5rId58mRFuykRGP0Tnk2Ln5rxLANAfmBuJ5lEVL8W9fiiKJLnLbxo9jPYqmdOxafr-Zd65PXosOlorwJteMCacdWSUnKA%3D%3D&client_id=PNMO08771&timestamp=2017.02.13+10%3A06%3A48+%2B0000">
    Вход с помощью учетной записи портала госуслуг</a></td>    


  <tr>
  
  
    <td><a href=<%=ESIAManager.getAuthorizationURL()%>>
    Вход с помощью </br>учетной записи  </br>портала госуслуг</a></td>    
  
  </tr>

  
  -->
  
  <tr>
  
    <td><a href="esia?auth=esia">
    Вход с помощью </br>учетной записи  </br>портала госуслуг</a></td>    
  
  </tr>
  
  
  <tr>
    <td colspan="2" align="right">
      <input type="submit" value="Войти" />
      <input type="reset" value="Сбросить" />
    </td>
    <td><%if (UserDataUtils.getGeneralOauth2Properties() != null) {%><a href="oauth?auth=websphere">WebSphere авторизация</a><%}%> <%if (UserDataUtils.getGeneralSpnegoProperties() != null) {%><a href="spnego">Spnego авторизация</a><%}%></td>
  </tr>
  <tr>
  <td colspan="3">
  <a href="forall/state" target="_blank">О программе</a>
  </td>
  </tr>
</table>
</form>

<br/>
<%if (UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication") != null && "true".equalsIgnoreCase(UserDataUtils.getGeneralOptionalProp("security.crossdomain.authentication").trim())) {%><img src="<%=authGifSrc%>" alt=" " id="authenticationImage" style="visibility:hidden" /><%}%>

</body>
</html>
