<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page import="ru.curs.showcase.runtime.LoggingEventDecorator"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.Collection"%>
<%@page import="ru.curs.showcase.runtime.AppInfoSingleton"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Последние записи лога Showcase</title>
<link rel="shortcut icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
<link rel="icon" href="solutions/default/resources/favicon.ico" type="image/x-icon" />
<style>
<!--
th, td
 {
 	border: 1px solid black;
 	white-space: pre-wrap;
 }
 table
 {
	table-layout:fixed; 
 }
-->
</style>
</head>
<body>
<%
	Collection<LoggingEventDecorator> lastLogEvents = AppInfoSingleton.getAppInfo().getLastLogEvents();
%>
<table width="90%">
<c:forEach items="<%=lastLogEvents%>" var="event">
<tr>
<td width="7%">${event.getLevel()} </td>
<td width="18%">${event.getTime()} </td>
<td width="77%">${event.getMessage()} </td>
</tr>
</c:forEach>
</table>

</body>
</html>