# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.core.jython import JythonDownloadResult
from ru.curs.gwt.datagrid.model import Column
from java.io import ByteArrayInputStream
from java.lang import String

def getDataAndSetting(context, main, add, filterinfo, session, elementId, sortColumnList):
    print 'Get grid data and setting from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    if sortColumnList != None:
        for column in sortColumnList:
            print 'sort columnID "%s".' % column.getId()
    
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

def getSetting(context, main, add, filterinfo, session, elementId):
    print 'Get grid setting from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    settings = u'''
    <gridsettings>
       <labels>
        <header>
        <h3>Test Grid jython data</h3>
        </header>
      </labels>
      <columns>
        <col id="name" />
        <col id="file"  width="130px" type="DOWNLOAD" linkId="download"/> 
      </columns>
      <properties flip="false" pagesize="15" totalCount="0" profile="grid.nowidth.properties"/>
    </gridsettings>'''
    
    res = JythonDTO(None, settings)
    return res

def getData(context, main, add, filterinfo, session, elementId, sortColumnList, firstrecord, pagesize):
    print 'Get grid data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'firstrecord "%s".' % firstrecord
    print 'pagesize "%s".' % pagesize
        
    if sortColumnList != None:
        for column in sortColumnList:
            print 'sort columnID "%s".' % column.getId()
    
    data = u'''
    <records>
        <rec>
            <name>Тест</name>
            <file>Файл</file>
        </rec>
    </records>'''
    
    res = JythonDTO(data, None)
    return res

def downloadFile(context, main, add, filterinfo, session, elementId, recordId):
    print 'Save xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'recordId "%s".' % recordId
    
    fileName = 'test.txt'
    data = String('grid data')
    return JythonDownloadResult(ByteArrayInputStream(data.getBytes()),fileName)