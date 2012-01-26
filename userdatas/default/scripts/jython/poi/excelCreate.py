# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
#from ru.curs.showcase.runtime import AppInfoSingleton
#from ru.curs.showcase.core.jython import JythonDTO
#from ru.curs.showcase.app.api import UserMessage;
#from ru.curs.showcase.util.xml import XMLUtils
#from org.xml.sax.helpers import DefaultHandler
#from ru.curs.showcase.util import TextUtils

from org.apache.poi.ss.util import CellReference
from org.apache.poi.xssf.streaming import SXSSFWorkbook
from java.io import FileOutputStream

# init vars
main = None
outputFile = None
session = None
filterContext = None
elementId = None
request = None
pyconn = None


class excelCreate(JythonProc):
    def execute(self, context):
        global main, outputFile, session, filterContext
        main = context.getMain()
        if context.getAdditional():
            outputFile = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        return mainproc()


def mainproc():
    iMemoryRows = 100
    wb = SXSSFWorkbook(iMemoryRows)
    sh = wb.createSheet()
    for rownum in xrange(50000):
        row = sh.createRow(rownum)
        for cellnum in xrange(10):
            cell = row.createCell(cellnum)
            address = CellReference(cell).formatAsString()
            cell.setCellValue(address)
    out = FileOutputStream(outputFile)
    wb.write(out)
    out.close()

if __name__ == "__main__":
    mainproc()
