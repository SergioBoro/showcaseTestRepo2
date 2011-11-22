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

class WebTextUserData1Proc(JythonProc):        
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
    if main == "плохой":
        return JythonDTO.createError(UserMessage(u"1", u"проверка на ошибку сработала"))
    data = u'<div><a href="?userdata=test1" target="_blank">Перейти к userdata test1</a></div>'
    settings = None
    res = JythonDTO.createResult(data, settings)
    return res
  
if __name__ == "__main__":       
    mainproc()