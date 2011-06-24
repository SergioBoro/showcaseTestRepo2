package ru.curs.showcase.app.test;

import java.util.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.client.XFormPanel;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Вспомогательный класс для тестирования XFormPanel и
 * XFormPanelCallbacksEvents.
 */
public final class XFormTestsCommon {

	// CHECKSTYLE:OFF
	public static final String XFORM_DATA =
		"<schema xmlns=\"http://www.w3.org/1999/xhtml\"><info><name>&#1054;&#1090;&#1088;&#1072;&#1073;&#1086;&#1090;&#1082;&#1072; &#1089;&#1077;&#1088;&#1074;&#1083;&#1077;&#1090;&#1072; XFormsTransformationServlet</name><growth></growth><eyescolour>&#1047;&#1077;&#1083;&#1077;&#1085;&#1099;&#1081;</eyescolour><music>&#1048;&#1085;&#1089;&#1090;&#1088;&#1091;&#1084;&#1077;&#1085;&#1090;&#1072;&#1083;&#1100;&#1085;&#1072;&#1103; &#1069;&#1089;&#1090;&#1088;&#1072;&#1076;&#1085;&#1072;&#1103;</music><comment>dddddddddd</comment></info></schema>";

	public static final int LEN_MAININSTANCE = 539;

	static final String XFORM_2_BODY =
		"<span class=\"xforms-message\" id=\"xf-message-0\">Submission успешно выполнен</span><span class=\"xforms-message\" id=\"xf-message-1\"> Ошибка при выполнении submission: <span id=\"xf-output-0\" class=\"xforms-disabled xforms-control xforms-output xforms-appearance-minimal\"><span><span><span class=\"value\"><span class=\"xforms-value\"> </span></span><span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span></span><span class=\"xforms-message\" id=\"xf-message-2\">Submission успешно выполнен</span><span class=\"xforms-message\" id=\"xf-message-3\"> Ошибка при выполнении submission: <span id=\"xf-output-1\" class=\"xforms-disabled xforms-control xforms-output xforms-appearance-minimal\"><span><span><span class=\"value\"><span class=\"xforms-value\"> </span></span><span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span></span><div style=\"font-size: 15px;\"> Имя: </div><div><span id=\"xf-input-0\" class=\"xforms-disabled xforms-control xforms-input xforms-appearance-minimal\"><span><span><span class=\"value\"><input type=\"text\" class=\"xforms-value\"></input></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span><span class=\"xforms-hint\"><span class=\"xforms-hint-icon\" onmouseover=\"show(this, 'hint', true)\" onmouseout=\"show(this, 'hint', false)\"> </span><div class=\"xforms-hint-value\" id=\"xf-hint-0\">Дополнительная информация</div></span><span class=\"xforms-help\"><span class=\"xforms-help-icon\" onmouseover=\"show(this, 'help', true)\" onmouseout=\"show(this, 'help', false)\"> </span><div class=\"xforms-help-value\" id=\"xf-help-0\">Справка</div></span></span></span></span></span><span id=\"xf-trigger-0\" class=\"xforms-disabled xforms-control xforms-trigger xforms-appearance-minimal\"><span><span><span class=\"value\"><button><span id=\"xf-label-0\" class=\"xforms-label\">Selector</span></button></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span></div><div style=\"font-size: 15px;\"> Цвет глаз (1): </div><div><span id=\"xf-select1-0\" class=\"xforms-disabled xforms-control xforms-select1 xforms-appearance-minimal\"><span><span><span class=\"value\"><span class=\"xforms-value\"><div id=\"xf-item-0\" class=\"xforms-disabled xforms-item\"><input type=\"radio\" value=\"Голубой\" class=\"xforms-value\"></input><span id=\"xf-label-1\" class=\"xforms-item-label\">Голубой</span></div><div id=\"xf-item-1\" class=\"xforms-disabled xforms-item\"><input type=\"radio\" value=\"Карий\" class=\"xforms-value\"></input><span id=\"xf-label-2\" class=\"xforms-item-label\">Карий</span></div><div id=\"xf-item-2\" class=\"xforms-disabled xforms-item\"><input type=\"radio\" value=\"Зеленый\" class=\"xforms-value\"></input><span id=\"xf-label-3\" class=\"xforms-item-label\">Зеленый</span></div><div id=\"xf-item-3\" class=\"xforms-disabled xforms-item\"><input type=\"radio\" value=\"Серый\" class=\"xforms-value\"></input><span id=\"xf-label-4\" class=\"xforms-item-label\">Серый</span></div></span></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span></div><div style=\"font-size: 15px;\"> Цвет глаз (2): </div><div><span id=\"xf-select1-1\" class=\"xforms-disabled xforms-control xforms-select1 xforms-appearance-minimal\"><span><span><span class=\"value\"><select class=\"xforms-value\"><option value=\"Голубой\" id=\"xf-item-4\">Голубой</option><option value=\"Карий\" id=\"xf-item-5\">Карий</option><option value=\"Зеленый\" id=\"xf-item-6\">Зеленый</option><option value=\"Серый\" id=\"xf-item-7\">Серый</option></select></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span></div><div style=\"font-size: 15px;\"> Любимая музыка (1): </div><div><span id=\"xf-select-0\" class=\"xforms-disabled xforms-control xforms-select xforms-appearance-minimal\"><span><span><span class=\"value\"><span class=\"xforms-value\"><div id=\"xf-item-8\" class=\"xforms-disabled xforms-item\"><input type=\"checkbox\" value=\"Классическая\" class=\"xforms-value\"></input><span id=\"xf-label-9\" class=\"xforms-item-label\">Классическая</span></div><div id=\"xf-item-9\" class=\"xforms-disabled xforms-item\"><input type=\"checkbox\" value=\"Инструментальная\" class=\"xforms-value\"></input><span id=\"xf-label-10\" class=\"xforms-item-label\">Инструментальная</span></div><div id=\"xf-item-10\" class=\"xforms-disabled xforms-item\"><input type=\"checkbox\" value=\"Эстрадная\" class=\"xforms-value\"></input><span id=\"xf-label-11\" class=\"xforms-item-label\">Эстрадная</span></div></span></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span></div><div style=\"font-size: 15px;\"> Любимая музыка (2): </div><div><span id=\"xf-select-1\" class=\"xforms-disabled xforms-control xforms-select xforms-appearance-minimal\"><span><span><span class=\"value\"><select class=\"xforms-value\" multiple=\"true\" size=\"3\"><option value=\"Классическая\" id=\"xf-item-11\">Классическая</option><option value=\"Инструментальная\" id=\"xf-item-12\">Инструментальная</option><option value=\"Эстрадная\" id=\"xf-item-13\">Эстрадная</option></select></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span></div><div style=\"font-size: 15px;\"> Комментарии: </div><div><span id=\"xf-textarea-0\" class=\"xforms-disabled xforms-control xforms-textarea xforms-appearance-minimal\"><span><span><span class=\"value\"><textarea class=\"xforms-value\"></textarea></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span></div><div style=\"clear: both;\"><span id=\"xf-output-2\" class=\"xforms-disabled xforms-control xforms-output xforms-appearance-minimal\"><span><span><span class=\"value\"><span class=\"xforms-value\"> </span></span><span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span></div><div><span id=\"xf-trigger-1\" class=\"xforms-disabled xforms-control xforms-trigger xforms-appearance-minimal\"><span><span><span class=\"value\"><button><span id=\"xf-label-15\" class=\"xforms-label\">Вызов XFormsSubmissionServlet</span></button></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span><span id=\"xf-trigger-2\" class=\"xforms-disabled xforms-control xforms-trigger xforms-appearance-minimal\"><span><span><span class=\"value\"><button><span id=\"xf-label-16\" class=\"xforms-label\">Вызов XFormsSubmissionServlet с ошибкой</span></button></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span><span id=\"xf-trigger-3\" class=\"xforms-disabled xforms-control xforms-trigger xforms-appearance-minimal\"><span><span><span class=\"value\"><button><span id=\"xf-label-17\" class=\"xforms-label\">Вызов XFormsTransformationServlet</span></button></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span></div><div><span id=\"xf-trigger-4\" class=\"xforms-disabled xforms-control xforms-trigger xforms-appearance-minimal\"><span><span><span class=\"value\"><button><span id=\"xf-label-18\" class=\"xforms-label\">Сохранить</span></button></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span><span id=\"xf-trigger-5\" class=\"xforms-disabled xforms-control xforms-trigger xforms-appearance-minimal\"><span><span><span class=\"value\"><button><span id=\"xf-label-19\" class=\"xforms-label\">Отфильтровать</span></button></span><span><span class=\"xforms-required-icon\">*</span><span class=\"xforms-alert\"><span class=\"xforms-alert-icon\"> </span></span></span></span></span></span></div><div id=\"console\"></div><div id=\"statusPanel\">... Loading ...</div>";
	static final String XFORM_2_CSS =
		"div.ScrollPanel { margin: 0px; padding: 0px; width: 949px; height: 270px; overflow-y: auto; overflow-x: hidden; } th.TableHeader { background-color: #5A8BC3; color: white; padding: 2px; text-align: center; } .xforms-value { border: 1px solid #B3B3B3; padding: 0.3em; margin: 0px 1px 0px 0px; font-size: 11px; width: 96px; } th.NameHeader { width: 900px; } th.SobNumberHeader { width: 26px; } th.SobNameHeader { width: 310px; } th.SobResponsibleHeader { width: 310px; } th.SobConsiderHeader { width: 20px; text-align: center; } th.SobResponsibleHeader1 { width: 184px; } th.SobDateHeader1 { width: 86px; } th.SobPeriodHeader1 { } .InfoTable { text-align: left; background-color: white; color: black; } .WarningInput .xforms-value { color: red; border: 0px; font-weight: bold; width: 800px; font-size: 120%; } .HintInput .xforms-value { color: green; border: 0px; font-weight: bold; width: 800px; font-size: 100%; } .RoleInput .xforms-value { width: 806px; } .StatusInput .xforms-value { width: 785px; } .PeriodInput .xforms-value { width: 120px; } .ExecutorElemInput .xforms-value { width: 816px; } .ExecutorInput .xforms-value { width: 806px; } .ProjectInput .xforms-value { width: 806px; } .TransitionInput .xforms-value { width: 660px; } .DateInput .xforms-value { width: 60px; } .FIOInput .xforms-value { width: 806px; } .EmailInput .xforms-value { width: 200px; } .TelInput .xforms-value { width: 200px; } .RemarkInput .xforms-value { width: 806px; height: 450px; } .CommentInput .xforms-value { width: 806px; height: 100px; } .TaskInput .xforms-value { width: 675px; background-color: #bbbbbb; } .NaprInput .xforms-value { width: 675px; } .ResponsibleInput .xforms-value { width: 675px; } .ConsiderInput .xforms-value { width: 22px; } .CodeInput .xforms-value { width: 50px; } .NaznInput .xforms-value { width: 806px; text-align: right; border: 0px; } .NameInput .xforms-value { width: 870px; } .TaskInput1 .xforms-value { width: 940px; text-align: center; } .NumberInput1 .xforms-value { width: 70%; margin-top: 1px; border: 0px; } .NameInput1 .xforms-value { width: 162px; border: 0px; } .ResponsibleInput1 .xforms-value { width: 191px; border: 0px; } .NumberInput .xforms-value { width: 17px; border: 0px; } .ActionInput .xforms-value { width: 301px; border: 0px; } .ConsiderInput .xforms-value { width: 10px; border: 0px; } .Pok1Input1 .xforms-value { width: 80px; border: 0px; } .DateInput1 .xforms-value { width: 64%; border: 0px; } .CauseInput1 .xforms-value { width: 96.8%; border: 0px; } th.SobNumberHeader1, th.SobNumberHeader, th.SobNameHeader, th.SobAntiKrHeader, th.SobResponsibleHeader, th.SobPeriodHeader1, th.SobConsiderHeader { vertical-align: middle; font-size: 10px; background-color: #5A8BC3; color: white; } .xforms-invalid .xforms-value { border: 1px solid red; } div.Hide { display: none; } div.Show { display: inline; }";
	static final String XFORM_2_SCRIPT1 =
		"var DebugMode = false; var Language = \"navigator\"; var LoadingMsg = \"... Loading ...\"; function initImpl() { Core.fileName='xsltforms.js'; Core.isXhtml = document.body.namespaceURI == \"http://www.w3.org/1999/xhtml\"; Dialog.show('statusPanel'); try { XPath.create(\"'Уважаемый ' + /schema/info/name + '! Ваш рост:' + /schema/info/growth + ', ваш цвет глаз:' + /schema/info/eyescolour + ', ваш музыкальные предпочения:' + /schema/info/music\",new BinaryExpr(new BinaryExpr(new BinaryExpr(new BinaryExpr(new BinaryExpr(new BinaryExpr(new BinaryExpr(new CteExpr('Уважаемый '),'+',new LocationExpr(true,new StepExpr('child',new NodeTestName('','schema')),new StepExpr('child',new NodeTestName('','info')),new StepExpr('child',new NodeTestName('','name')))),'+',new CteExpr('! Ваш рост:')),'+',new LocationExpr(true,new StepExpr('child',new NodeTestName('','schema')),new StepExpr('child',new NodeTestName('','info')),new StepExpr('child',new NodeTestName('','growth')))),'+',new CteExpr(', ваш цвет глаз:')),'+',new LocationExpr(true,new StepExpr('child',new NodeTestName('','schema')),new StepExpr('child',new NodeTestName('','info')),new StepExpr('child',new NodeTestName('','eyescolour')))),'+',new CteExpr(', ваш музыкальные предпочения:')),'+',new LocationExpr(true,new StepExpr('child',new NodeTestName('','schema')),new StepExpr('child',new NodeTestName('','info')),new StepExpr('child',new NodeTestName('','music'))))); XPath.create(\"/schema/info/comment\",new LocationExpr(true,new StepExpr('child',new NodeTestName('','schema')),new StepExpr('child',new NodeTestName('','info')),new StepExpr('child',new NodeTestName('','comment')))); XPath.create(\"/schema/info/eyescolour\",new LocationExpr(true,new StepExpr('child',new NodeTestName('','schema')),new StepExpr('child',new NodeTestName('','info')),new StepExpr('child',new NodeTestName('','eyescolour')))); XPath.create(\"/schema/info/music\",new LocationExpr(true,new StepExpr('child',new NodeTestName('','schema')),new StepExpr('child',new NodeTestName('','info')),new StepExpr('child',new NodeTestName('','music')))); XPath.create(\"/schema/info/name\",new LocationExpr(true,new StepExpr('child',new NodeTestName('','schema')),new StepExpr('child',new NodeTestName('','info')),new StepExpr('child',new NodeTestName('','name')))); XPath.create(\"event('response-body')\",new FunctionCallExpr('http://www.w3.org/2002/xforms event',new CteExpr('response-body'))); XPath.create(\"event('response-body')!='null'\",new BinaryExpr(new FunctionCallExpr('http://www.w3.org/2002/xforms event',new CteExpr('response-body')),'!=',new CteExpr('null'))); XPath.create(\"instance('mainInstance')\",new FunctionCallExpr('http://www.w3.org/2002/xforms instance',new CteExpr('mainInstance'))); XPath.create(\"*[1]\",new LocationExpr(false,new StepExpr('child',new NodeTestType(NodeType.ELEMENT),new PredicateExpr(new CteExpr(1))))); var xf_model_0 = new XFModel(\"mainModel\",null); new XFInstance(\"mainInstance\",xf_model_0,null,'<schema xmlns=\"http://www.w3.org/1999/xhtml\"><info><name>Отработка сервлета XFormsTransformationServlet</name><growth/><eyescolour>Зеленый</eyescolour><music>Инструментальная Эстрадная</music><comment>dddddddddd</comment></info></schema>'); new XFSubmission(\"wrong_save\",xf_model_0,\"instance('mainInstance')\",null,\"secured/submit?proc=xforms_submission11\",\"post\",null,null,null,null,null,null,\"instance\",\"mainInstance\",\"&\",null,true,true,null,null); var xf_message_0 = new XFMessage(\"xf-message-0\",null,null,null,null); var xf_action_0 = new XFAction(null,null).add(xf_message_0); var xf_message_1 = new XFMessage(\"xf-message-1\",null,null,null,null); new XFOutput(\"xf-output-0\",new Binding(true, \"event('response-body')\"),null); var xf_action_1 = new XFAction(\"event('response-body')!='null'\",null).add(xf_message_1); new Listener(document.getElementById(\"wrong_save\"),\"xforms-submit-done\",null,function(evt) {run(xf_action_0,getId(evt.currentTarget ? evt.currentTarget : evt.target),evt,false,true);}); new Listener(document.getElementById(\"wrong_save\"),\"xforms-submit-error\",null,function(evt) {run(xf_action_1,getId(evt.currentTarget ? evt.currentTarget : evt.target),evt,false,true);}); new XFSubmission(\"good_save\",xf_model_0,\"instance('mainInstance')\",null,\"secured/submit?proc=xforms_submission1\",\"post\",null,null,null,null,null,null,\"instance\",\"mainInstance\",\"&\",null,true,true,null,null); var xf_message_2 = new XFMessage(\"xf-message-2\",null,null,null,null); var xf_action_2 = new XFAction(null,null).add(xf_message_2); var xf_message_3 = new XFMessage(\"xf-message-3\",null,null,null,null); new XFOutput(\"xf-output-1\",new Binding(true, \"event('response-body')\"),null); var xf_action_3 = new XFAction(\"event('response-body')!='null'\",null).add(xf_message_3); new Listener(document.getElementById(\"good_save\"),\"xforms-submit-done\",null,function(evt) {run(xf_action_2,getId(evt.currentTarget ? evt.currentTarget : evt.target),evt,false,true);}); new Listener(document.getElementById(\"good_save\"),\"xforms-submit-error\",null,function(evt) {run(xf_action_3,getId(evt.currentTarget ? evt.currentTarget : evt.target),evt,false,true);}); new XFSubmission(\"xslttransformation\",xf_model_0,\"instance('mainInstance')\",null,\"secured/xslttransformer?xsltfile=xformsxslttransformation_test.xsl\",\"post\",null,null,null,null,null,null,\"instance\",\"mainInstance\",\"&\",null,true,true,null,null); var xf_bind_0 = new XFBind(\"xf-bind-0\",xf_model_0,\"*[1]\",null,null,null,null,null,null); new XFInstance(\"srvdata\",xf_model_0,null,'<schema xmlns=\"http://www.w3.org/1999/xhtml\"><context><main>Потери - Всего</main><session>&lt;sessioncontext&gt; &lt;username&gt;super&lt;/username&gt; &lt;userdata&gt;default&lt;/userdata&gt; &lt;/sessioncontext&gt;</session><sessionParamsMap/></context><element><id>61</id><position>16</position><type>XFORMS</type><procName>xforms_proc1</procName><templateName>Showcase_Template.xml</templateName><hideOnLoad>false</hideOnLoad><neverShowInPanel>false</neverShowInPanel><cacheData>false</cacheData><refreshByTimer>false</refreshByTimer><refreshInterval>600</refreshInterval><procs><entry><key>proc1</key><value><id>proc1</id><name>xforms_saveproc1</name><type>SAVE</type></value></entry><entry><key>proc2</key><value><id>proc2</id><name>xforms_submission1</name><type>SUBMISSION</type></value></entry></procs></element></schema>'); new XFInput(\"xf-input-0\",\"text\",new Binding(false, \"/schema/info/name\"),null,null,null,null); var xf_trigger_0 = new XFTrigger(\"xf-trigger-0\",null); var xf_load_0 = new XFLoad(null,\"javascript:showSelector({ id : '61', procCount : '[dbo].[companycount]', procList : '[dbo].[companylist]', generalFilters : '', currentValue : '', windowCaption : 'Выберите название', onSelectionComplete : function(ok, selected){ if (ok) { var a = xforms.defaultModel.defaultInstance.doc.getElementsByTagName('info')[0].getElementsByTagName('name')[0]; setValue(a, selected.name); xforms.ready = false; xforms.refresh(); xforms.ready = true; } }});;\",null,null,null); var xf_action_4 = new XFAction(null,null).add(xf_load_0); new Listener(document.getElementById(\"xf-trigger-0\"),\"DOMActivate\",null,function(evt) {run(xf_action_4,getId(evt.currentTarget ? evt.currentTarget : evt.target),evt,false,true);}); var xf_select1_0 = new XFSelect(\"xf-select1-0\",false,true,new Binding(false, \"/schema/info/eyescolour\"),true); var xf_item_0 = new XFItem(\"xf-item-0\",null,null); var xf_item_1 = new XFItem(\"xf-item-1\",null,null); var xf_item_2 = new XFItem(\"xf-item-2\",null,null); var xf_item_3 = new XFItem(\"xf-item-3\",null,null); var xf_select1_1 = new XFSelect(\"xf-select1-1\",false,false,new Binding(false, \"/schema/info/eyescolour\"),true); var xf_item_4 = new XFItem(\"xf-item-4\",null,null); var xf_item_5 = new XFItem(\"xf-item-5\",null,null); var xf_item_6 = new XFItem(\"xf-item-6\",null,null); var xf_item_7 = new XFItem(\"xf-item-7\",null,null); var xf_select_0 = new XFSelect(\"xf-select-0\",true,true,new Binding(false, \"/schema/info/music\"),true); var xf_item_8 = new XFItem(\"xf-item-8\",null,null); var xf_item_9 = new XFItem(\"xf-item-9\",null,null); var xf_item_10 = new XFItem(\"xf-item-10\",null,null); var xf_select_1 = new XFSelect(\"xf-select-1\",true,false,new Binding(false, \"/schema/info/music\"),true); var xf_item_11 = new XFItem(\"xf-item-11\",null,null); var xf_item_12 = new XFItem(\"xf-item-12\",null,null); var xf_item_13 = new XFItem(\"xf-item-13\",null,null); new XFInput(\"xf-textarea-0\",\"textarea\",new Binding(false, \"/schema/info/comment\"),null,null,null,null); new XFOutput(\"xf-output-2\",new Binding(false, \"'Уважаемый ' + /schema/info/name + '! Ваш рост:' + /schema/info/growth + ', ваш цвет глаз:' + /schema/info/eyescolour + ', ваш музыкальные предпочения:' + /schema/info/music\"),null); var xf_trigger_1 = new XFTrigger(\"xf-trigger-1\",null); var xf_send_0 = new XFDispatch(\"xforms-submit\",\"good_save\",null,null); var xf_action_5 = new XFAction(null,null).add(xf_send_0); new Listener(document.getElementById(\"xf-trigger-1\"),\"DOMActivate\",null,function(evt) {run(xf_action_5,getId(evt.currentTarget ? evt.currentTarget : evt.target),evt,false,true);}); var xf_trigger_2 = new XFTrigger(\"xf-trigger-2\",null); var xf_send_1 = new XFDispatch(\"xforms-submit\",\"wrong_save\",null,null); var xf_action_6 = new XFAction(null,null).add(xf_send_1); new Listener(document.getElementById(\"xf-trigger-2\"),\"DOMActivate\",null,function(evt) {run(xf_action_6,getId(evt.currentTarget ? evt.currentTarget : evt.target),evt,false,true);}); var xf_trigger_3 = new XFTrigger(\"xf-trigger-3\",null); var xf_send_2 = new XFDispatch(\"xforms-submit\",\"xslttransformation\",null,null); var xf_action_7 = new XFAction(null,null).add(xf_send_2); new Listener(document.getElementById(\"xf-trigger-3\"),\"DOMActivate\",null,function(evt) {run(xf_action_7,getId(evt.currentTarget ? evt.currentTarget : evt.target),evt,false,true);}); var xf_trigger_4 = new XFTrigger(\"xf-trigger-4\",null); var xf_load_1 = new XFLoad(null,\"javascript:gwtXFormSave('61', '1', Writer.toString(xforms.defaultModel.getInstanceDocument('mainInstance')))\",null,null,null); var xf_action_8 = new XFAction(null,null).add(xf_load_1); new Listener(document.getElementById(\"xf-trigger-4\"),\"DOMActivate\",null,function(evt) {run(xf_action_8,getId(evt.currentTarget ? evt.currentTarget : evt.target),evt,false,true);}); var xf_trigger_5 = new XFTrigger(\"xf-trigger-5\",null); var xf_load_2 = new XFLoad(null,\"javascript:gwtXFormFilter('61', '2', Writer.toString(xforms.defaultModel.getInstanceDocument('mainInstance')))\",null,null,null); var xf_action_9 = new XFAction(null,null).add(xf_load_2); new Listener(document.getElementById(\"xf-trigger-5\"),\"DOMActivate\",null,function(evt) {run(xf_action_9,getId(evt.currentTarget ? evt.currentTarget : evt.target),evt,false,true);}); var xf_model_config = new XFModel(\"xf-model-config\",null); var xf_instance_config = new XFInstance(\"xf-instance-config\",xf_model_config,null,'<properties><language>navigator</language><calendar.day0>Mon</calendar.day0><calendar.day1>Tue</calendar.day1><calendar.day2>Wed</calendar.day2><calendar.day3>Thu</calendar.day3><calendar.day4>Fri</calendar.day4><calendar.day5>Sat</calendar.day5><calendar.day6>Sun</calendar.day6><calendar.initDay>6</calendar.initDay><calendar.month0>January</calendar.month0><calendar.month1>February</calendar.month1><calendar.month2>March</calendar.month2><calendar.month3>April</calendar.month3><calendar.month4>May</calendar.month4><calendar.month5>June</calendar.month5><calendar.month6>July</calendar.month6><calendar.month7>August</calendar.month7><calendar.month8>September</calendar.month8><calendar.month9>October</calendar.month9><calendar.month10>November</calendar.month10><calendar.month11>December</calendar.month11><format.date>MM/dd/yyyy</format.date><format.datetime>MM/dd/yyyy hh:mm:ss</format.datetime><format.decimal>.</format.decimal><status>... Loading ...</status></properties>'); xforms.init(); } catch (e) { alert(\"XSLTForms Exception--------------------------Error initializing :\"+(typeof(e.stack)==\"undefined\"?\"\":e.stack)+\"\"+(e.name?e.name+(e.message?\"\"+e.message:\"\"):e)); }};";
	static final String XFORM_2_SCRIPT2 =
		"function init() { try { initImpl(); } catch(e) { alert(\"XSLTForms Exception--------------------------Incorrect Javascript code generation:\"+(typeof(e.stack)==\"undefined\"?\"\":e.stack)+\"\"+(e.name?e.name+(e.message?\"\"+e.message:\"\"):e)); } }";

	// CHECKSTYLE:ON

	private XFormTestsCommon() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Очищает DOM-модель и выполняет начальную инициализацию.
	 */
	public static void clearDOM() {

		com.google.gwt.user.client.Element elem = DOM.getElementById("dynastyle");
		if (elem != null) {
			elem.removeFromParent();
		}

		elem = DOM.getElementById("target");
		if (elem != null) {
			elem.removeFromParent();
		}

		elem = DOM.getElementById("showcaseAppContainer");
		if (elem != null) {
			elem.removeFromParent();
		}

		com.google.gwt.user.client.Element bodyElem = RootPanel.getBodyElement();
		com.google.gwt.user.client.Element div = DOM.createDiv();
		DOM.setElementAttribute(div, "id", "target");
		DOM.insertChild(bodyElem, div, 0);

		div = DOM.createDiv();
		DOM.setElementAttribute(div, "id", "showcaseAppContainer");
		DOM.insertChild(bodyElem, div, 1);

	}

	/**
	 * Создает XForms для тестов.
	 * 
	 * @return XForms
	 */
	public static XForms createXForms2() {

		XForms xform = new XForms();
		final int numParts = 4;
		ArrayList<String> xFormParts = new ArrayList<String>(numParts);
		xFormParts.add(XFORM_2_BODY);
		xFormParts.add(XFORM_2_CSS);
		xFormParts.add(XFORM_2_SCRIPT1);
		xFormParts.add(XFORM_2_SCRIPT2);
		xform.setXFormParts(xFormParts);

		Action ac = new Action();
		ac.setDataPanelActionType(DataPanelActionType.DO_NOTHING);
		// ac.setKeepUserSettings(aKeepUserSettings)
		xform.setDefaultAction(ac);

		ac = new Action();
		ac.setDataPanelActionType(DataPanelActionType.DO_NOTHING);
		ac.setKeepUserSettings(true);
		Event ev = new HTMLEvent();
		ev.setAction(ac);
		((HTMLEvent) ev).setLinkId("1");
		((HTMLEvent) ev).setInteractionType(InteractionType.SINGLE_CLICK);
		xform.getEventManager().setEvents(Arrays.asList(ev));

		return xform;
	}

	/**
	 * Создает XFormPanel для тестов.
	 * 
	 * @return XFormPanel
	 */
	public static XFormPanel createXFormPanelForTests1() {

		DataPanelElementInfo dpei = new DataPanelElementInfo();
		dpei.setId("1");
		dpei.setPosition(1);
		dpei.setType(DataPanelElementType.XFORMS);
		dpei.setRefreshByTimer(true);

		return new XFormPanel(dpei);
	}

	/**
	 * Создает XFormPanel для тестов.
	 * 
	 * @return XFormPanel
	 */
	public static XFormPanel createXFormPanelForTests2() {

		CompositeContext context = new CompositeContext();

		DataPanelElementInfo dpei = new DataPanelElementInfo();
		dpei.setId("1");
		dpei.setPosition(1);
		dpei.setType(DataPanelElementType.XFORMS);
		dpei.setRefreshByTimer(true);

		XForms xform = createXForms2();

		return new XFormPanel(context, dpei, xform);
	}

}
