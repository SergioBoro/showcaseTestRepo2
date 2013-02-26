# coding: utf-8
'''
Created on 15.02.2013

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO

# init vars
main = ""
add = ""
session = ""
filterContext = ""


class extJsTreeGetData(JythonProc):
    def getPluginData(self, context, attr):
        global main, add, session, filterContext, elementId
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        return mainproc(attr)


def mainproc(attributes):
    parentId=''
    curValue=''
    paramMap = attributes.getParamMap()
    if paramMap!=None:
        pId = paramMap.get('id')
        if (pId!=None):
            parentId=pId+'.'
        pCurValue = paramMap.get('curValue')
        if (pCurValue!=None):
            curValue=' ['+pCurValue+']'
    data = u'''
    <items>
		<item id="'''+parentId+'''1" name="Lazy loaded item '''+parentId+'''1'''+curValue+'''" leaf="false"/>
		<item id="'''+parentId+'''2" name="Lazy loaded item '''+parentId+'''2'''+curValue+'''" leaf="false"/>
    </items>'''
    res = JythonDTO(data)
    return res

if __name__ == "__main__":
    mainproc()