# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model.jython import JythonProc
from ru.curs.showcase.model.jython import JythonDTO
from ru.curs.showcase.app.api import UserMessage;
import random;
#from ru.curs.showcase.util.xml import XMLUtils;  
#from org.xml.sax.helpers import DefaultHandler;
#from ru.curs.showcase.util import TextUtils;

# init vars
main = ""
add = ""
session = ""
filter = ""
elementId = ""

class WebTextGetJythonProc(JythonProc):        
    def getRawData(self, context, elId):
        global main, add, session, filter, elementId
        main = context.getMain().encode("utf-8")
        if context.getAdditional():
            add = context.getAdditional().encode("utf-8")
        session = context.getSession().encode("utf-8")
        if context.getFilter():
            filter = context.getFilter().encode("utf-8")
        elementId = elId.encode("utf-8")
        return mainproc()
        
def mainproc():
    if main == "плохой":
        return UserMessage(u"1", u"проверка на ошибку сработала")
    data = u"<root><name>"+unicode(main,"utf-8")+"</name><count>"+unicode(random.randrange(1,10000000), "utf-8")+u"</count></root>"
    settings = None
    if add == "withsettings":
        settings = u'''<properties>
                        <action >
                            <main_context>current</main_context>    
                                 <datapanel type="current" tab="current">
                                <element id="d2">
                                    <add_context>я оригинальный</add_context>
                                </element>                                                                                                                            
                            </datapanel>                                                
                            <server>
                                <activity id="srv01" name="sc_init_debug_console_adapter"/>
                            </server>
                        </action>                               
                        <event name="single_click" linkId="1">
                        <action >
                            <main_context>current</main_context>    
                                 <datapanel type="current" tab="current">
                                <element id="d2">
                                    <add_context>я оригинальный</add_context>
                                </element>                                                                                                                            
                            </datapanel>                                                
                            <server>
                                <activity id="srv01" name="sc_init_debug_console_adapter"/>
                            </server>
                        </action>
                       </event>                                                                                      
                    </properties>''';
    res = JythonDTO(data, settings)
    return res
  
if __name__ == "__main__":       
    mainproc()