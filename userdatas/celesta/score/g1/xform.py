# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.jython import JythonDTO

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
    return JythonDTO(data, settings)

def save(context, main, add, filterinfo, session, elementId, data):
    print 'Save xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'data "%s".' % data
    
def submit(context, main, add, filterinfo, session, data):
    print 'Transform xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'data "%s".' % data
    
    return data;
