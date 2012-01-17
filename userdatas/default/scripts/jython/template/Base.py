# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model.jython import JythonProc
#from ru.curs.showcase.model.jython import JythonDTO
#from ru.curs.showcase.app.api import UserMessage
#from ru.curs.showcase.util.xml import XMLUtils
#from org.xml.sax.helpers import DefaultHandler
#from ru.curs.showcase.util import TextUtils

# init vars
main = None
add = None
session = None
filterContext = None
elementId = None


class Base(JythonProc):
    def getRawData(self, context, element):
        global main, add, session, filterContext, elementId
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = element
        return mainproc()


def mainproc():
    return u'''
<?xml-stylesheet href="xsltforms/xsltforms.xsl" type="text/xsl"?>
<html xmlns="http://www.w3.org/1999/xhtml"
xmlns:ev="http://www.w3.org/2001/xml-events"
xmlns:xsd="http://www.w3.org/2001/XMLschema"
xmlns:fs="http://www.curs.ru/ns/FormServer"
xmlns:xf="http://www.w3.org/2002/xforms">
    <head>
        <!-- Простейшие контролы ввода и вывода -->
        <xf:model id="mainModel">
            <xf:instance id="mainInstance">
                <schema xmlns="">
                    <info>
                        <name />
                        <growth />
                        <eyescolour />
                        <music />
                        <comment />
                    </info>
                </schema>
            </xf:instance>

            <xf:submission id="wrong_save" method="post" replace="instance"
            instance="mainInstance" ref="instance('mainInstance')"
                action="secured/submit?proc=xforms_submission11">
                <xf:action ev:event="xforms-submit-done">
                    <xf:message>Submission успешно выполнен</xf:message>
                </xf:action>
                <xf:action if="event('response-body')!='null'"
                ev:event="xforms-submit-error">
                    <xf:message>
                        Ошибка при выполнении submission:
                        <xf:output value="event('response-body')" />
                    </xf:message>
                </xf:action>
            </xf:submission>

            <xf:submission id="good_save" method="post" instance="mainInstance"
                replace="instance" ref="instance('mainInstance')"
                action="secured/submit?proc=xforms_submission1">
                <xf:action ev:event="xforms-submit-done">
                    <xf:message>Submission успешно выполнен</xf:message>
                </xf:action>
                <xf:action if="event('response-body')!='null'"
                ev:event="xforms-submit-error">
                    <xf:message>
                        Ошибка при выполнении submission:
                        <xf:output value="event('response-body')" />
                    </xf:message>
                </xf:action>
            </xf:submission>


            <xf:submission id="xslttransformation" method="post"
                instance="mainInstance" replace="instance"
                ref="instance('mainInstance')"
                action="secured/xslttransformer?xsltfile=xformsxslttransformation_test.xsl">
            </xf:submission>


            <xf:bind>
            </xf:bind>
        </xf:model>
    </head>
    <body>


        <div style="font-size: 15px;"> Имя: </div>
        <div>
            <xf:input ref="/schema/info/name">
                <xf:help>Справка</xf:help>
                <xf:hint>Дополнительная информация</xf:hint>
            </xf:input>


            <xf:trigger>
                <xf:label>Selector</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:load
                        resource="javascript:showSelector({
                       id : 'xformId',
                       procCount : '[dbo].[regionscount]',
                       procList  : '[dbo].[regionslist]',
                       generalFilters      : '',
                       currentValue        : '',
                       windowCaption       : 'Выберите название',
                       onSelectionComplete : function(ok, selected){
                    if (ok) {
                    var a = xforms.defaultModel.defaultInstance.doc.getElementsByTagName('info')[0].getElementsByTagName('name')[0];
                    setValue(a, selected.name);
                    xforms.ready = false;
                    xforms.refresh();
                    xforms.ready = true;
                            }
                                   }});;" />
                </xf:action>
            </xf:trigger>

        </div>
        <div style="font-size: 15px;"> Цвет глаз (1): </div>
        <div>
            <xf:select1 appearance="full" ref="/schema/info/eyescolour">
                <xf:item>
                    <xf:label>Голубой</xf:label>
                    <xf:value>Голубой</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Карий</xf:label>
                    <xf:value>Карий</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Зеленый</xf:label>
                    <xf:value>Зеленый</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Серый</xf:label>
                    <xf:value>Серый</xf:value>
                </xf:item>
            </xf:select1>
        </div>
        <div style="font-size: 15px;"> Цвет глаз (2): </div>
        <div>
            <xf:select1 ref="/schema/info/eyescolour">
                <xf:item>
                    <xf:label>Голубой</xf:label>
                    <xf:value>Голубой</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Карий</xf:label>
                    <xf:value>Карий</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Зеленый</xf:label>
                    <xf:value>Зеленый</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Серый</xf:label>
                    <xf:value>Серый</xf:value>
                </xf:item>
            </xf:select1>
        </div>
        <div style="font-size: 15px;"> Любимая музыка (1): </div>
        <div>
            <xf:select appearance="full" ref="/schema/info/music">
                <xf:item>
                    <xf:label>Классическая</xf:label>
                    <xf:value>Классическая</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Инструментальная</xf:label>
                    <xf:value>Инструментальная</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Эстрадная</xf:label>
                    <xf:value>Эстрадная</xf:value>
                </xf:item>
            </xf:select>
        </div>
        <div style="font-size: 15px;"> Любимая музыка (2): </div>
        <div>
            <xf:select ref="/schema/info/music">
                <xf:item>
                    <xf:label>Классическая</xf:label>
                    <xf:value>Классическая</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Инструментальная</xf:label>
                    <xf:value>Инструментальная</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Эстрадная</xf:label>
                    <xf:value>Эстрадная</xf:value>
                </xf:item>
            </xf:select>
        </div>
        <div style="font-size: 15px;"> Комментарии: </div>
        <div>
            <xf:textarea ref="/schema/info/comment" />
        </div>
        <div style="clear: both;">
            <xf:output
                ref="'Уважаемый ' + /schema/info/name                              + '! Ваш рост:' + /schema/info/growth                              + ', ваш цвет глаз:' + /schema/info/eyescolour                             + ', ваш музыкальные предпочения:' + /schema/info/music" />
        </div>
        <div>

            <xf:trigger>
                <xf:label>Вызов XFormsSubmissionServlet</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:send submission="good_save" />
                </xf:action>
            </xf:trigger>

            <xf:trigger>
                <xf:label>Вызов XFormsSubmissionServlet с ошибкой</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:send submission="wrong_save" />
                </xf:action>
            </xf:trigger>

            <xf:trigger>
                <xf:label>Вызов XFormsTransformationServlet</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:send submission="xslttransformation" />
                </xf:action>
            </xf:trigger>

        </div>


        <div>

            <xf:trigger>
                <xf:label>Сохранить</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:load
                        resource="javascript:gwtXFormSave('xformId', '1',  Writer.toString(xforms.defaultModel.getInstanceDocument('mainInstance')))" />
                </xf:action>
            </xf:trigger>

            <xf:trigger>
                <xf:label>Отфильтровать</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:load
                        resource="javascript:gwtXFormFilter('xformId', '2',  Writer.toString(xforms.defaultModel.getInstanceDocument('mainInstance')))" />
                </xf:action>
            </xf:trigger>
            
            <xf:trigger>
                <xf:label>Обновить</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:load
                        resource="javascript:gwtXFormUpdate('xformId', '3',  Writer.toString(xforms.defaultModel.getInstanceDocument('mainInstance')))" />
                </xf:action>
            </xf:trigger>

        </div>




    </body>
</html>    
    '''

if __name__ == "__main__":
    mainproc()
