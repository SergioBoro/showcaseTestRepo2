# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model.jython import JythonProc;
#from ru.curs.showcase.model.jython import JythonDTO
#from ru.curs.showcase.app.api import UserMessage;
#from ru.curs.showcase.util.xml import XMLUtils;  
#from org.xml.sax.helpers import DefaultHandler;
#from ru.curs.showcase.util import TextUtils;

# init vars
main = None
session = None

class dp1301(JythonProc):           
    def getRawData(self, context):
        global main, session
        main = context.getMain().encode("utf-8")
        session = context.getSession().encode("utf-8")
        return mainproc()     
        
def mainproc():
    return u'''
<datapanel>    
    <tab id="05" name="Мультиселектор. Передача начальных значений">
        <element id="0501" type="xforms" template="Showcase_Template_multiselector.xml"
            proc="xforms_proc_no_data">
            <proc id="proc2" name="xforms_submission1" type="SUBMISSION" />
        </element>                    
    </tab>
    <tab id="06" name="InlineUploader. Обработка ошибок">
        <element id="0001" type="xforms" template="Showcase_Template_uploaders_simple.xml"
            proc="xforms_proc_all">
            <proc id="proc1" name="xforms_saveproc1" type="SAVE" />
            <proc id="04" name="xforms_upload_by_userdata" type="UPLOAD" />
            <proc id="041" name="xforms_upload_by_userdata" type="UPLOAD" />
            <proc id="05" name="xforms_upload_by_userdata_err" type="UPLOAD" />
            <proc id="051" name="xforms_upload_by_userdata" type="UPLOAD" />            
        </element>    
    </tab>
    <tab id="07" name="Упрощенное задание селекторов в XForms" layout="VERTICAL">
        <element id="0502" type="xforms" template="Showcase_Template_multiselector_simple.xml"
            proc="xforms_proc_no_data">
            <proc id="proc2" name="xforms_submission1" type="SUBMISSION" />
        </element>                    
    </tab>
    <tab id="08" name="Проверка шаблона карты на сервере">
        <element id="08" type="geomap" proc="geomap_world" cacheData="false"/>    
    </tab>    
</datapanel>   
    ''' 
  
if __name__ == "__main__":       
    mainproc()