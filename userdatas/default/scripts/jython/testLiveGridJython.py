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
sortcols = None #объект типа java.util.List<ru.curs.gwt.datagrid.model.Column>


class testLiveGridJython(JythonProc):
    def getRawData(self, context, elId, scols, frecord, psize):
        global main, add, session, filterContext, elementId, sortcols, firstrecord, pagesize
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        sortcols = scols
        firstrecord = frecord
        pagesize = psize
        return mainproc()


def mainproc():    
    data = u'''
    <records>
        <rec>
            <name>Тест</name>
        </rec>
    </records>'''
    res = JythonDTO(data)
    
    
#    print 'qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq'
    
    
    return res
    

if __name__ == "__main__": 
    mainproc();
