# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model import JythonProc
from ru.curs.showcase.model import JythonDTO
#from ru.curs.showcase.util.xml import XMLUtils;  
#from org.xml.sax.helpers import DefaultHandler;
#from ru.curs.showcase.util import TextUtils;

# init vars
session = ""

class NavJythonProc(JythonProc):        
    def getRawData(self, context):
        global session
        session = context.getSession().encode("utf-8")
        return mainproc()
        
def mainproc():
    return JythonDTO.createResult(
'''<navigator hideOnLoad="true">
    <group id="00" name="Фичи">
        <level1 id="04" name="secret" selectOnLoad="true">
            <action>
                <main_context>
                    Запасы на конец отчетного периода - Всего
                </main_context>
                <datapanel type="dp0903dynSession" tab="firstOrCurrent"/>                
            </action>
        </level1>
    </group>
</navigator>''') 
  
if __name__ == "__main__":       
    mainproc()