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

from org.apache.poi.ss.usermodel import Cell;
from org.apache.poi.ss.usermodel import Row;
from org.apache.poi.ss.usermodel import Sheet;
from org.apache.poi.ss.usermodel import Workbook;
from org.apache.poi.ss.util import CellReference;
from org.apache.poi.xssf.streaming import SXSSFWorkbook;
from java.io import FileOutputStream
# init vars
main = None
add = None
session = None
filter = None
elementId = None
request = None
pyconn = None

class excelCreate(JythonProc):        
    def execute(self, context):
        global main, add, session, filter
        main = context.getMain().encode("utf-8")
        if context.getAdditional():
            add = context.getAdditional().encode("utf-8")
        session = context.getSession().encode("utf-8")
        if context.getFilter():
            filter = context.getFilter().encode("utf-8")
        return mainproc()
        
def mainproc():
    wb = SXSSFWorkbook(100); # keep 100 rows in memory, exceeding rows will be flushed to disk
    sh = wb.createSheet();
    for rownum in xrange(1000):
        row = sh.createRow(rownum);
        for cellnum in xrange(10):
            cell = row.createCell(cellnum);
            address = CellReference(cell).formatAsString();
            cell.setCellValue(address);                    
    out = FileOutputStream(add + "/sxssf.xlsx");
    wb.write(out);
    out.close();     
  
if __name__ == "__main__":       
    mainproc()