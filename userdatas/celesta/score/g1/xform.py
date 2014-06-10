# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.core.jython import JythonDownloadResult
from ru.curs.showcase.runtime import AppInfoSingleton
from java.io import FileInputStream
from java.io import File
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory


def main(context, main, add, filterinfo, session, elementId):
    print 'Get xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    data = u'''
    <schema xmlns="">
      <info>
        <name>Белгородская обл.</name>
        <growth />
        <eyescolour />
        <music />
        <comment />
      </info>
    </schema>
    '''
    settings = u'''
    <properties>        
    </properties>
    '''    
    return JythonDTO(data, settings, UserMessageFactory().build(555, u"xforms успешно построен из Celesta"))

def save(context, main, add, filterinfo, session, elementId, data):
    print 'Save xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'data "%s".' % data
    
    return UserMessageFactory().build(555, u"xforms успешно сохранен из Celesta");
    
def submit(context, main, add, filterinfo, session, data):
    print 'Submit xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'data "%s".' % data
    
    return data;

def uploadFile(context, main, add, filterinfo, session, elementId, data, fileName, file):
    print 'Upload file xform from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'data "%s".' % data
    print 'fileName "%s".' % fileName
    
    return UserMessageFactory().build(555, u"Файл успешно загружен из Celesta");    

def downloadFile(context, main, add, filterinfo, session, elementId, data):
    print 'Download file xform from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'data "%s".' % data
    
    fileName = 'app.properties'
    path = AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\" + fileName
    file = File(path)
    return JythonDownloadResult(FileInputStream(file),fileName)