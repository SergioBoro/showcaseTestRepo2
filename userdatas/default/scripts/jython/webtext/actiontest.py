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

class actiontest(JythonProc):        
    def getRawData(self, context, elId):
        global main, add, session, filter
        main = context.getMain().encode("utf-8")
        if context.getAdditional():
            add = context.getAdditional().encode("utf-8")
        session = context.getSession().encode("utf-8")
        if context.getFilter():
            filter = context.getFilter().encode("utf-8")
        elementId = elId.encode("utf-8")
        return mainproc()
        
def mainproc():
    data = u'''<div>
    <button type="button" onclick="gwtWebTextFunc('${elementId}','1');">Перейти на панель 12.1 c контекстом Москва</button>
    </div>'''
    settings = u'''
<properties>           
                      <event name="single_click" linkId="1">
                        <action >
                            <main_context>Москва</main_context>
                            <navigator element="1201"/>                        
                        </action>
                       </event>                                                                                             
                    </properties>    
    '''
    res = JythonDTO(data, settings)
    return res
  
if __name__ == "__main__":       
    mainproc()