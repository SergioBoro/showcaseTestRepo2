# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model.jython import JythonProc
from ru.curs.showcase.app.api import UserMessage
#from ru.curs.showcase.util.xml import XMLUtils
#from org.xml.sax.helpers import DefaultHandler
#from ru.curs.showcase.util import TextUtils

# init vars
main = ""
add = ""
session = ""
filterContext = ""


class NoValidateJythonProc(JythonProc):
    def execute(self, context):
        global main, add, session, filterContext
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        return mainproc()


def mainproc():
    return UserMessage("test2", u"из Jython")

if __name__ == "__main__":
    mainproc()
