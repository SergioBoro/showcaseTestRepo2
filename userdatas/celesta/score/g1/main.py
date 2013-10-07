# coding: utf-8
def navigator(context, session):
    print 'Get navigator data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'Sesion "%s".' % session
    
    data = u'''
    <navigator width="200px">
        <group id="0" name="Примеры">
            <level1 id="00" name="Компоненты">
            </level1>
        </group>
    </navigator>
    '''    
    return data