# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.jython import JythonDTO

def navigator(context, session):
    print 'Get navigator data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'Sesion "%s".' % session
    
    data = u'''
    <navigator width="200px">
        <group id="1" name="Примеры">
            <level1 id="11" name="Компоненты">
                <level2 id="111" name="WebText">
                    <action>
                        <main_context></main_context>
                        <datapanel type="webText.xml" tab="firstOrCurrent"></datapanel>
                    </action>
                </level2>
                <level2 id="112" name="XForms">
                    <action>
                        <main_context></main_context>
                        <datapanel type="xforms.xml" tab="firstOrCurrent"></datapanel>
                    </action>
                </level2>
                <level2 id="113" name="Grid">
                    <action>
                        <main_context></main_context>
                        <datapanel type="grid.xml" tab="firstOrCurrent"></datapanel>
                    </action>
                </level2>
                <level2 id="114" name="Plugin">
                    <action>
                        <main_context></main_context>
                        <datapanel type="plugin.xml" tab="firstOrCurrent"></datapanel>
                    </action>
                </level2>     
            </level1>
        </group>
    </navigator>
    '''    
    return data

def webtext(context, main, add, filterinfo, session, elementId):
    print 'Get navigator data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    data = u'''
    <h1>
        <a href="#" onclick="gwtWebTextFunc('${elementId}','testID');">Показать сообщение</a>
    </h1>
    '''
    settings = u'''
    <properties>
        <event name="single_click" linkId="testId">
             <action >
                 <main_context>Москва</main_context>
                     <client>
                         <activity id="activityID" name="showcaseShowAddContext">
                             <add_context>
                                 add_context действия.
                             </add_context>
                        </activity>
                    </client>
            </action>
        </event>
    </properties>
    '''    
    return JythonDTO(data, settings)
