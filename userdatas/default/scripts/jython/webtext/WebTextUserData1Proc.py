# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model.jython import JythonProc
from ru.curs.showcase.model.jython import JythonDTO
from ru.curs.showcase.app.api import UserMessage
#from ru.curs.showcase.util.xml import XMLUtils
#from org.xml.sax.helpers import DefaultHandler
#from ru.curs.showcase.util import TextUtils

# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""


class WebTextUserData1Proc(JythonProc):
    def getRawData(self, context, elId):
        global main, add, session, filterContext, elementId
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        return mainproc()


def mainproc():
    if main == "плохой":
        return UserMessage(u"1", u"проверка на ошибку сработала")
    data = u'<div><a href="?userdata=test1" target="_blank">Перейти к userdata test1</a></div>'
    settings = None
    res = JythonDTO(data, settings)
    return res

if __name__ == "__main__":
    mainproc()
