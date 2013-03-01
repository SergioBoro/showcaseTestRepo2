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
			<item id="1" name="Lazy load item" leaf="false"/>
			<item id="2" name="Расходование денежных средств" cls="folder">
				<children>
					<item id="21" name="Оплата поставщикам за товар" leaf="true" attr1="a" checked="false"/>
					<item id="22" name="Расходы по таможенному оформлению" leaf="true" attr1="b" checked="false"/>
					<item id="23" name="Расходы  на аренду, коммунальные услуги" cls="folder">
						<children>
							<item id="231" name="Аренда недвижимости" leaf="true" attr1="c" checked="false"/>
							<item id="232" name="Коммунальные услуги" leaf="true" attr1="d" checked="false"/>
							<item id="233" name="Расходы на содержание сооружений и оборудования" leaf="true" attr1="e" checked="false"/>
						</children>
					</item>
					<item id="24" name="Расходы на персонал" cls="folder">
						<children>
							<item id="241" name="Расходы на оплату труда" leaf="true" attr1="f" checked="false"/>
							<item id="242" name="Страхование персонала, мед.услуги" leaf="true" attr1="g" checked="false"/>
							<item id="243" name="Матпомощь, подарки" leaf="true" attr1="m" checked="false"/>
						</children>
					</item>
					<item id="25" name="Услуги связи" leaf="true" attr1="k" checked="false"/>
					<item id="26" name="Маркетинг и реклама" leaf="true" attr1="l" checked="false"/>
					<item id="27" name="Обеспечение безопасности" leaf="true" attr1="n" checked="false"/>
				</children>   
			</item>
			<item id="3" name="Поступление денежных средств" cls="folder">
				<children>
					<item id="31" name="Доход от продажи товара" leaf="true" attr1="o" checked="false"/>
					<item id="32" name="Расходы по таможенному оформлению" leaf="true" attr1="p" checked="false"/>
					<item id="33" name="Возврат денежных средств от контрагентов" leaf="true" attr1="r" checked="false"/>				
					<item id="34" name="Проценты по депозитам" leaf="true" attr1="s" checked="false"/>
				</children>   
			</item>
    </items>'''
    settings = u'''
    <properties>
    </properties>
    '''
    res = JythonDTO(data, settings)
    return res

if __name__ == "__main__":
    mainproc()