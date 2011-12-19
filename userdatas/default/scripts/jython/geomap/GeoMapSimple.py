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

class GeoMapSimple(JythonProc):        
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
    cityProps = u'''
'<properties>
                        <event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
                                    <add_context>'+[Name_Ru]+'</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                        </event>
                    </properties>'    
    ''' 
    data = [u'''SELECT 'l1' AS [ID], 'Города' AS [Name], 'POINT' AS [ObjectType], '%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)' AS [HintFormat]
    ''',
    u'''SELECT [ID], [Name_Ru] AS [Name], 'l1' AS [LayerID], [Lat], [Lon],
    CASE [ID] WHEN 1849 THEN 'Нижний Новгород - город с показателями' ELSE NULL END AS [Tooltip],
    CASE [ID] WHEN 1849 THEN 'cityStyle' ELSE NULL END AS StyleClass, '''+cityProps+u''' AS [~~properties] FROM [GeoObjects] WHERE [Status_ID]=1 AND [Name_Ru] LIKE '%город'
    ''']
    settings = u'''
<geomapsettings>
        <labels>
            <header><p>Карта с раскрашенными по значению показателя регионами</p>
            <h2>И совершенно бесплатно, только сегодня работает масштабирование колесиком мыши</h2>
            </header>
    
        </labels>
        <properties legend="top" />
        <template> 
    {
       registerModules: [["solution", "../../solutions/default/js"]],
       managerModule: "solution.test",    
       style: [
    {
        fid: "l2",
        stroke: "black",
        strokeWidth: 1,
        styleFunction: {
            getStyle: "djeo.util.numeric.getStyle",
            options: {
                numClasses: 7,
                colorSchemeName: "Reds",
                attr: "mainInd",
                breaks: "djeo.util.jenks.getBreaks",
                calculateStyle: "djeo.util.colorbrewer.calculateStyle"
            }
        },
        legend: "djeo._getBreaksAreaLegend",
        name: "имя"
    },
{
       point: {
            strokeWidth: 2,
            fill: "blue",
            shape: "circle"
       }
}
]
      
}    
        </template>                      
</geomapsettings>    
    '''
    return JythonDTO(data, settings)
  
if __name__ == "__main__":       
    mainproc()