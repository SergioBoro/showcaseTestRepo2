# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model.jython import JythonProc;
#from ru.curs.showcase.runtime import AppInfoSingleton
#from ru.curs.showcase.model.jython import JythonDTO
#from ru.curs.showcase.app.api import UserMessage;
#from ru.curs.showcase.util.xml import XMLUtils;  
#from org.xml.sax.helpers import DefaultHandler;
#from ru.curs.showcase.util import TextUtils;

# init vars
main = None
add = None
session = None
filter = None
elementId = None
request = None

class AbstractJythonProc(JythonProc):        
    def execute(self, context):
        global main, add, session, filter
        main = context.getMain().encode("utf-8")
        if context.getAdditional():
            add = context.getAdditional().encode("utf-8")
        session = context.getSession().encode("utf-8")
        if context.getFilter():
            filter = context.getFilter().encode("utf-8")
        return mainproc()
    def getRawData(self, context):
        global main, add, session, filter
        main = context.getMain().encode("utf-8")
        if context.getAdditional():
            add = context.getAdditional().encode("utf-8")
        session = context.getSession().encode("utf-8")
        if context.getFilter():
            filter = context.getFilter().encode("utf-8")
        return mainproc()    
    def getRawData(self, context, element):
        global main, add, session, filter, elementId
        main = context.getMain().encode("utf-8")
        if context.getAdditional():
            add = context.getAdditional().encode("utf-8")
        session = context.getSession().encode("utf-8")
        if context.getFilter():
            filter = context.getFilter().encode("utf-8")
        elementId = element.encode("utf-8")
        return mainproc()     
    def handle(self, requestString):
        global request
        request = requestString.encode("utf-8")
        return mainproc()
        
def mainproc():
    pass 
  
if __name__ == "__main__":       
    mainproc()