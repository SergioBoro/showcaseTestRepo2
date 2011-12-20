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

from net.sf.jasperreports.engine import *;
from net.sf.jasperreports.engine.export import *;
from java.util import *;
import os.path

# init vars
main = None
outputFile = None
session = None
filter = None
elementId = None
request = None
pyconn = None

class pdfCreate(JythonProc):        
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
    fileName = os.path.dirname(__file__) + "/simple.jasper"
    hm = HashMap()
    # Fill the report using an empty data source
    data = JasperFillManager.fillReport(fileName, hm, JREmptyDataSource())      
    # Create a PDF exporter
    exporter = JRPdfExporter();    
    # Configure the exporter (set output file name and print object)
    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFile)
    exporter.setParameter(JRExporterParameter.JASPER_PRINT, data)
    #Export the PDF file
    exporter.exportReport()
  
if __name__ == "__main__":       
    mainproc()