# coding: utf-8
'''
Created on 19.12.2011

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO

# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""


class testGridJython(JythonProc):
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
    data = u'''
    <records>
        <rec>
            <name>Тест</name>
        </rec>
    </records>'''
    settings = u'''
    <gridsettings>
       <labels>
        <header>
        <h3>Test Grid jython data</h3>
        </header>
      </labels>
      <columns>
        <col id="name" />
      </columns>
      <properties flip="false" pagesize="15" totalCount="0" profile="grid.nowidth.properties"/>
   </gridsettings>'''
    res = JythonDTO(data, settings)
    return res
    

if __name__ == "__main__": 
    mainproc();
