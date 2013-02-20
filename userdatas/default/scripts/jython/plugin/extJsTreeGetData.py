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
    parentId='';
    paramMap = attributes.getParamMap()
    if paramMap!=None:
        parentId=paramMap.get('id')+'.'
    data = u'''
    <items>
		<item id="'''+parentId+'''1" name="Lazy loaded item '''+parentId+'''1" leaf="false"/>
		<item id="'''+parentId+'''2" name="Lazy loaded item '''+parentId+'''2" leaf="false"/>
    </items>'''
    res = JythonDTO(data)
    return res

if __name__ == "__main__":
    mainproc()