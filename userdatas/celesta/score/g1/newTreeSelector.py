# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory


    
def getTreeSelectorData(context, main=None, add=None, filterinfo=None, session=None, 
                        params=None, curValue=None, startsWith=None, parentId=None):
    
    if parentId!=None:
        parentId=parentId+'.'
    else:
        parentId=''        

    if curValue!=None and curValue!='':
        curValue=' ['+curValue+']'
    else:
        curValue=''

    
    data = u'''
    <items>
        <item id="'''+parentId+'''1" name="Название1 записи '''+parentId+'''1'''+curValue+'''" column1="Значение1" column2="solutions/default/resources/imagesingrid/test.jpg"  leaf="false" checked="false"                       treeGridNodeCloseIcon="solutions/default/resources/imagesingrid/TreeGridNodeClose.gif" treeGridNodeOpenIcon="solutions/default/resources/imagesingrid/TreeGridNodeOpen.gif" treeGridNodeLeafIcon="solutions/default/resources/imagesingrid/TreeGridLeaf.png"/>
        <item id="'''+parentId+'''2" name="Название2 записи '''+parentId+'''2'''+curValue+'''" column1="Значение2" column2="solutions/default/resources/imagesingrid/test.jpg"  leaf="false"                                       treeGridNodeCloseIcon="solutions/default/resources/imagesingrid/TreeGridNodeClose.gif" treeGridNodeOpenIcon="solutions/default/resources/imagesingrid/TreeGridNodeOpen.gif" treeGridNodeLeafIcon="solutions/default/resources/imagesingrid/TreeGridLeaf.png"/>
        <item id="'''+parentId+'''3" name="Название3 записи '''+parentId+'''3'''+curValue+'''" column1="Значение3" column2="solutions/default/resources/imagesingrid/test.jpg"                                hasChildren="false" treeGridNodeCloseIcon="solutions/default/resources/imagesingrid/TreeGridNodeClose.gif" treeGridNodeOpenIcon="solutions/default/resources/imagesingrid/TreeGridNodeOpen.gif" treeGridNodeLeafIcon="solutions/default/resources/imagesingrid/TreeGridLeaf.png"/>        
        <item id="'''+parentId+'''4" name="Название4 записи '''+parentId+'''4'''+curValue+'''" column1="Значение4" column2="solutions/default/resources/imagesingrid/test.jpg"               checked="true"   hasChildren="true"  treeGridNodeCloseIcon="solutions/default/resources/imagesingrid/TreeGridNodeClose.gif" treeGridNodeOpenIcon="solutions/default/resources/imagesingrid/TreeGridNodeOpen.gif" treeGridNodeLeafIcon="solutions/default/resources/imagesingrid/TreeGridLeaf.png"/>        
    </items>'''

#    context.message('dd11', u'Заголовок4', u"solutions/default/resources/group_icon_default.png");
#    context.warning('dd22');    
#    context.error('dd44', u'Заголовок4', u"solutions/default/resources/group_icon_default.png");
    
    
    #res = JythonDTO(data, UserMessageFactory().build(555, u"Новый триселектор успешно построен из Celesta"))
    res = JythonDTO(data)
    
    
    return res





def getTreeSelectorData2(context, main=None, add=None, filterinfo=None, session=None, 
                        params=None, curValue=None, startsWith=None, parentId=None):
    
    if parentId!=None:
        parentId=parentId+'.'
    else:
        parentId=''        

    if curValue!=None and curValue!='':
        curValue=' ['+curValue+']'
    else:
        curValue=''

    
    data = u'''
    <items>
        <item id="'''+parentId+'''1" name="Название1 записи '''+parentId+'''1'''+curValue+'''" column1="Значение1" column2="solutions/default/resources/imagesingrid/test.jpg"  leaf="false" checked="true"                      />
        <item id="'''+parentId+'''2" name="Название2 записи '''+parentId+'''2'''+curValue+'''" column1="Значение2" column2="solutions/default/resources/imagesingrid/test.jpg"  leaf="false"                  hasChildren="true"  />
        <item id="'''+parentId+'''3" name="Название3 записи '''+parentId+'''3'''+curValue+'''" column1="Значение3" column2="solutions/default/resources/imagesingrid/test.jpg"                                hasChildren="false" />        
        <item id="'''+parentId+'''4" name="Название4 записи '''+parentId+'''4'''+curValue+'''" column1="Значение4" column2="solutions/default/resources/imagesingrid/test.jpg"               checked="true"   hasChildren="true"  />        
    </items>'''

#    context.message('dd11', u'Заголовок4', u"solutions/default/resources/group_icon_default.png");
#    context.warning('dd22');    
#    context.error('dd44', u'Заголовок4', u"solutions/default/resources/group_icon_default.png");
    
    
    #res = JythonDTO(data, UserMessageFactory().build(555, u"Новый триселектор успешно построен из Celesta"))
    res = JythonDTO(data)
    
    
    return res

