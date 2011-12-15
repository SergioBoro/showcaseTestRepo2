# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model.jython import JythonProc;
from ru.curs.showcase.model.jython import JythonDTO
#from ru.curs.showcase.app.api import UserMessage;
#from ru.curs.showcase.util.xml import XMLUtils;  
#from org.xml.sax.helpers import DefaultHandler;
#from ru.curs.showcase.util import TextUtils;

# init vars
main = ""
add = ""
session = ""
filter = ""
elementId = ""
pyconn = None

class ChartSimple(JythonProc):        
    def getRawData(self, context, elId, conn):
        global main, add, session, filter, pyconn
        main = context.getMain().encode("utf-8")
        if context.getAdditional():
            add = context.getAdditional().encode("utf-8")
        session = context.getSession().encode("utf-8")
        if context.getFilter():
            filter = context.getFilter().encode("utf-8")
        elementId = elId.encode("utf-8")
        pyconn = conn
        return mainproc()  
        
def mainproc():
    cur = pyconn.cursor()
    cur.execute("SELECT TOP 10 [Journal_48_Name], [FJField_14] into #tmp FROM [Journal_48]") 
       
    data = u'SELECT [Journal_48_Name], [FJField_14] FROM #tmp'
    settings = u'''
<chartsettings>
        <labels>
            <header><h2>Простой график</h2></header>
        </labels>
        <properties legend="left" selectorColumn="Journal_48_Name" width="500px" height="500px" flip="false" hintFormat="%x (%labelx): %value"/>
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="16">
                                    <add_context>hide</add_context>
                                </element>                                                             
                            </datapanel>
                        </action>    
            <template>                        
{
"plot": {
        "type": "Pie", 
        "tension": "S", 
        "gap": 3, 
        "markers": true, 
        "precision": 3
    },     
    "theme": "dojox.charting.themes.PrimaryColors", 
    "action": [
        {
            "type": "dojox.charting.action2d.Shake", 
            "options": {
                "duration": 500
            }
        }, 
        {
            "type": "dojox.charting.action2d.Tooltip"
        }
    ], 
    "axisX": {
        "fixLower": "major", 
        "fixUpper": "major", 
        "minorLabels": false, 
        "microTicks": false, 
        "rotation": -90, 
        "minorTicks": false
    }, 
    "axisY": {
        "vertical": true
    }
,
    eventHandler: "eventCallbackChartHandler"
}
        </template>    
        </chartsettings>     
    '''
    return JythonDTO(data, settings)
  
if __name__ == "__main__":       
    mainproc()