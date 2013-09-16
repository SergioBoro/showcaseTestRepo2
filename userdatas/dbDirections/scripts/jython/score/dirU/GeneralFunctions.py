# coding: utf-8

from xml.dom import minidom

'''
    функция реализует корректное присваивание значений столбцам таблицы
'''
def convertToCelestaType(tableInstance, columnName, columnValue):
    
    type = tableInstance.meta().getColumns()[columnName].jdbcGetterName()
    
    if type == 'getDouble':
        columnValue = float(columnValue)
    elif type == 'getBoolean':
        columnValue = bool(columnValue)
    elif type == 'getInt':
        columnValue = int(columnValue)
    elif type == 'getBinary':
        columnValue = float(columnValue)
    elif type == 'getDate':
        columnValue = columnValue
    else:
        columnValue = columnValue

    tableInstance.__setattr__(columnName, columnValue)

    return


'''
    функция реализует пакетную загрузку информации из xml в базу данных
'''
def packageInsertData (tableXMLPath, tableInstance):
    
    dataForInsertXML = minidom.parse(tableXMLPath)
    
    for rowForInsert in dataForInsertXML.getElementsByTagName('row'):        
        for column in rowForInsert.childNodes:
            if column.localName:
                convertToCelestaType(tableInstance,
                                     column.localName,
                                     column.childNodes[0].data)
        try:
            tableInstance.insert()
        except:
            tableInstance.update()
