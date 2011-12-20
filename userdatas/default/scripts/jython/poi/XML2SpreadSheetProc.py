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

from org.wiztools.xml2spreadsheet import *
from java.io import FileOutputStream, FileInputStream
import os.path

# init vars
main = None
outputFile = None
session = None
filter = None
elementId = None
request = None
pyconn = None

class XML2SpreadSheetProc(JythonProc):        
    def execute(self, context):
        global main, outputFile, session, filter
        main = context.getMain().encode("utf-8")
        if context.getAdditional():
            outputFile = context.getAdditional().encode("utf-8")
        session = context.getSession().encode("utf-8")
        if context.getFilter():
            filter = context.getFilter().encode("utf-8")
        return mainproc()
        
def mainproc():    
    XML2SpreadSheet.convert(FileInputStream(os.path.dirname(__file__) + "\poi.xml"), FileOutputStream(outputFile))
  
if __name__ == "__main__":       
    mainproc()