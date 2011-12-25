# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model.jython import JythonProc
from ru.curs.showcase.model.jython import JythonDTO
#from ru.curs.showcase.util.xml import XMLUtils
#from org.xml.sax.helpers import DefaultHandler
#from ru.curs.showcase.util import TextUtils

# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""


class libtest(JythonProc):
    def getRawData(self, context, elId):
        global main, add, session, filterContext, elementId
        main = context.getMain().encode("utf-8")
        if context.getAdditional():
            add = context.getAdditional().encode("utf-8")
        session = context.getSession().encode("utf-8")
        if context.getFilter():
            filterContext = context.getFilter().encode("utf-8")
        elementId = elId.encode("utf-8")
        return mainproc()


def mainproc():
    excel = "simple.xlsx"
    pdf = "simple.pdf"
    data = u'''<div>
    <button type="button" onclick="gwtWebTextFunc('${elementId}','1');">Сгенерировать PDF</button>
    <button type="button" onclick="gwtWebTextFunc('${elementId}','2');">Сгенерировать Excel</button>
    <hr/>    
    <a href="''' + pdf + u'''" target="_blank">Открыть сгенерированный PDF</a>
    <a href="''' + excel + u'''" target="_blank">Открыть сгенерированный Excel</a>
    </div>'''
    settings = '''
<properties>           
                      <event name="single_click" linkId="1">
                        <action >
                            <main_context>current</main_context>                        
                            <server>
                                <activity id="srv01" name="ireport/pdfCreate.py">
                                    <add_context>../webapps/Showcase/''' + pdf + u'''</add_context>
                                </activity>
                            </server>
                        </action>
                       </event>        
                      <event name="single_click" linkId="2">
                        <action >
                            <main_context>current</main_context>                                                  
                            <server>
                                <activity id="srv02" name="poi/excelCreate.py">
                                    <add_context>d:/PR.dev/java/Tomcat-7.0.8/webapps/Showcase/''' + excel + '''</add_context>
                                </activity>
                            </server>
                        </action>
                       </event>                                                                                      
                    </properties>    
    '''
    res = JythonDTO(data, settings)
    return res

if __name__ == "__main__":
    mainproc()
