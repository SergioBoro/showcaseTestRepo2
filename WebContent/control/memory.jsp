<?xml version="1.0" encoding="UTF-8" ?>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="ru.curs.showcase.runtime.AppInfoSingleton"%>
<%@page import="ru.curs.showcase.runtime.ConnectionFactory"%>
<%@page import="ru.curs.showcase.runtime.JythonIterpretatorFactory"%>
<%@page import="ru.curs.showcase.runtime.XSLTransformerPoolFactory"%>
<%@page import="ru.curs.showcase.runtime.MemoryController"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="refresh" content="10;url=memory.jsp" />
<title>Контроль памяти</title>
<link rel="shortcut icon" href="solutions/default/resources/favicon.ico"
	type="image/x-icon" />
<link rel="icon" href="solutions/default/resources/favicon.ico"
	type="image/x-icon" />
<style>
<!--


table {
	table-layout: fixed;
	width: 90%;
}
form {
	text-align: center;
}
div {
	text-align: center;	
}
-->
</style>
</head>

<body>
	<script type="text/javascript">
		function refresh() {
			window.location.reload();
		}
	</script>

	<h3>Управление ресурсами Showcase</h3>
	<table>
		<tr>
			<td width="30%">
				<div>Число JDBC соединений: <%=ConnectionFactory.getInstance().getAllCount()%></div>
				<form target="fake" method="get" action="reset">
					<input type="hidden" name="pool" value="jdbc" /> <input
						type="submit" value="Сбросить пул JDBC соединений"
						onclick="window.setInterval('refresh()',1000);" />
				</form>
			</td>
			<td width="30%">
				<div>Число Jython интерпретаторов: <%=JythonIterpretatorFactory.getInstance().getAllCount()%></div>
				<form target="fake" method="get" action="reset">
					<input type="hidden" name="pool" value="jython" /> <input
						type="submit" value="Сбросить пул Jython интерпретаторов"
						onclick="window.setInterval('refresh()',1000);" />
				</form>
				<div style="font-style: italic">Сброс нужен после изменения любого Jython файла</div>				
			</td>
			<td width="30%">
				<div>Число XSL трансформаций: <%=XSLTransformerPoolFactory.getInstance().getAllCount()%></div>
				<form target="fake" method="get" action="reset">
					<input type="hidden" name="pool" value="xsl" /> <input
						type="submit" value="Сбросить пул XSL трансформаций"
						onclick="window.setInterval('refresh()',1000);" />
				</form>
				<div style="font-style: italic">Сброс нужен после изменения любой XSL трансформации</div>
			</td>
			<td width="30%">
				<div>Размер кэша: <%=AppInfoSingleton.getAppInfo().getCache().asMap().size()%></div>
				<form target="fake" method="get" action="reset">
					<input type="hidden" name="pool" value="dataPanelCache" /> <input
						type="submit" value="Сбросить кэш датапанелей"
						onclick="window.setInterval('refresh()',1000);" />
				</form>
				<div style="font-style: italic">Сбрасывает кэш информационных панелей</div>
			</td>
		</tr>
		<tr>
			<td colspan="3">
				<form target="fake" method="get"
					action="reset">
					<input type="hidden" name="pool" value="all" /> <input
						type="submit" value="Сбросить все пулы"
						onclick="window.setInterval('refresh()',1000);" />
				</form>
			</td>
		</tr>		
	</table>
	
	<h3>Управление памятью JVM</h3>
	<table>
		<tr>
			<td width="30%">				
				<div><h4>Используется heap space: <%=MemoryController.getUsedHeap()%></h4></div>
				<div><h4>Свободно всего heap space: <%=MemoryController.getAllFreeHeap()%></h4></div>				
				<div>Выделено heap space: <%=MemoryController.getCommitedHeap()%></div>										
				<div>Максимум heap space: <%=MemoryController.getMaxHeap()%></div>
			</td>
			<td width="30%">
				<div><h4>Используется PermGen space: <%=MemoryController.getUsedPermGen()%></h4></div>
				<div><h4>Свободно всего PermGen space: <%=MemoryController.getAllFreePermGen()%></h4></div>								
				<div>Выделено PermGen space: <%=MemoryController.getCommitedPermGen()%></div>				
				<div>Начальное значение PermGen space: <%=MemoryController.getInitPermGen()%></div>						
				<div>Максимум PermGen space: <%=MemoryController.getMaxPermGen()%></div>			
			</td>
			<td width="30%">
				<form target="fake" method="get"
					action="reset">
					<input type="hidden" name="gc" value="run" /> <input
						type="submit" value="Запустить уборку мусора"
						onclick="window.setInterval('refresh()',1000);" />
				</form>
			</td>
		</tr>	
	</table>
	
	<h3>Управление userdata</h3>
	<table>
		<tr>
			<td width="30%">	
				<form target="fake" method="get" action="reset">
					<input type="hidden" name="userdata" value="reload" /> 
					<input type="submit"
						value="Перезагрузить userdata"/>
				</form>
				<div style="font-style: italic">На перезагрузку userdata потребуется 10-30 секунд!</div>
			</td>
		</tr>		
	</table>
	
	<h3>Управление Celesta</h3>
	<table>
		<tr>
			<td width="30%">	
				<form target="fake" method="get" action="reset">
					<input type="hidden" name="pool" value="jythonCelesta" /> 
					<input type="submit"
						value="Сбросить пул Jython интерпретаторов в Celesta" onclick="window.setInterval('refresh()',1000);"/>
				</form>
				<div style="font-style: italic">Сброс будет проведен только в пуле Jython интерпретаторов для источников данных Celesta</div>
			</td>
			<td width="30%">	
				<form target="fake" method="get" action="reset">
					<input type="hidden" name="pool" value="celestaReinitialize" /> 
					<input type="submit"
						value="Реинициализировать Celesta" onclick="window.setInterval('refresh()',1000);"/>
				</form>
				<div style="font-style: italic">Чтобы реинициализировать Celesta, нажмите эту кнопку</div>
			</td>
		</tr>		
	</table>
	
	<iframe name="fake"
		style="position: absolute; width: 0; height: 0; border: 0"
		src="javascript:''" />
</body>
</html>