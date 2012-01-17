# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model.jython import JythonProc
#from ru.curs.showcase.runtime import AppInfoSingleton
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
request = None
pyconn = None


class AbstractJythonProc(JythonProc):
    def execute(self, context):
        global main, add, session, filterContext, elementId
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        return mainproc()

    def getRawData(self, *args):
        global main, add, session, filterContext, pyconn, elementId
        context = args[0]
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = args[1]
        pyconn = args[2]
        return mainproc()

    def handle(self, requestString):
        global request
        request = requestString
        return mainproc()


def mainproc():
    print "all is ok!"

if __name__ == "__main__":
    mainproc()
