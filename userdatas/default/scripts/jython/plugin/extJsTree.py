# coding: utf-8
'''
Created on 17.12.2011

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO

# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""


class extJsTree(JythonProc):
    def getRawData(self, context, elId):
        global main, add, session, filterContext, elementId
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        return mainproc()


def mainproc():
    data = u'''
    <items>
        <item text="Расходование денежных средств" cls="folder">
			<children>
				<item text="Оплата поставщикам за товар" leaf="true" checked="false"/>
				<item text="Расходы по таможенному оформлению" leaf="true" checked="false"/>
				<item text="Расходы  на аренду, коммунальные услуги" cls="folder">
					<children>
						<item text="Аренда недвижимости" leaf="true" checked="false"/>
						<item text="Коммунальные услуги" leaf="true" checked="false"/>
						<item text="Расходы на содержание сооружений и оборудования" leaf="true" checked="false"/>
					</children>
				</item>
				<item text="Расходы на персонал" cls="folder">
					<children>
						<item text="Расходы на оплату труда" leaf="true" checked="false"/>
						<item text="Страхование персонала, мед.услуги" leaf="true" checked="false"/>
						<item text="Матпомощь, подарки" leaf="true" checked="false"/>
					</children>
				</item>
				<item text="Услуги связи" leaf="true" checked="false"/>
				<item text="Маркетинг и реклама" leaf="true" checked="false"/>
				<item text="Обеспечение безопасности" leaf="true" checked="false"/>
            </children>   
        </item>
		<item text="Поступление денежных средств" cls="folder">
			<children>
				<item text="Доход от продажи товара" leaf="true" checked="false"/>
				<item text="Расходы по таможенному оформлению" leaf="true" checked="false"/>
				<item text="Возврат денежных средств от контрагентов" leaf="true" checked="false"/>				
				<item text="Проценты по депозитам" leaf="true" checked="false"/>
            </children>   
        </item>
    </items>'''
    settings = u'''
    <properties height="400px" width="300px">
    </properties>
    '''
    res = JythonDTO(data, settings)
    return res

if __name__ == "__main__":
    mainproc()