# coding: utf-8
'''
Created on 15.02.2013

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
import xml.etree.ElementTree as ET

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
    xmlParams = attributes.getXmlParams()
    if xmlParams!=None:
        root = ET.fromstring(xmlParams)        
        pId = root.find('.//id')
        if pId!=None and pId.text!=None:
            parentId=pId.text+'.'
        pCurValue = root.find('.//curValue')
        if pCurValue!=None and pCurValue.text!=None:
            curValue=' ['+pCurValue.text+']'
    data = u'''
    <items>
		<item id="'''+parentId+'''1" name="Lazy loaded item '''+parentId+'''1'''+curValue+'''" leaf="false"/>
		<item id="'''+parentId+'''2" name="Lazy loaded item '''+parentId+'''2'''+curValue+'''" leaf="false"/>
    </items>'''
    res = JythonDTO(data)
    return res

if __name__ == "__main__":
    mainproc()