# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model.jython import JythonProc
from ru.curs.showcase.util.xml import XMLUtils
from ru.curs.showcase.runtime import AppInfoSingleton
#from org.xml.sax.helpers import DefaultHandler;
#from ru.curs.showcase.util import TextUtils;

# init vars
request = ""

class GetFile(JythonProc):        
    def handle(self, input):
        global request
        request = input.encode("utf-8")
        return mainproc()
        
def mainproc():
    requestDoc = XMLUtils.stringToDocument(request)
    commandName = requestDoc.getDocumentElement().getAttributes().getNamedItem("type").getNodeValue() 
    if commandName == "getDP":
        filename = requestDoc.getDocumentElement().getAttributes().getNamedItem("param").getNodeValue()
        path = AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\datapanelstorage\\"
        file = open(path + filename)
        return unicode(file.read(), "utf-8")
    raise Exception(commandName + " не реализована!") 
  
if __name__ == "__main__":       
    mainproc()