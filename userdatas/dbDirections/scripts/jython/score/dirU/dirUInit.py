# coding: utf-8

from xml.dom import minidom

import ru.curs.celesta.Celesta as Celesta
import ru.curs.celesta.ConnectionPool as ConnectionPool
import ru.curs.celesta.CallContext as CallContext
from  score.dirU import GeneralFunctions


Celesta.getInstance()
conn = ConnectionPool.get()
context = CallContext(conn, 'user1')

initTables = ['filtersConditions', 'fieldsTypes', 'filtersConditionsByTypes']
_dirU_orm = __import__("score.dirU._dirU_orm", globals(), locals(), initTables, -1)

for tableClass in initTables:
    tableInstance = getattr(_dirU_orm, "%sCursor" % tableClass)
    GeneralFunctions.packageInsertData("%s.xml" % tableClass, tableInstance(context))

ConnectionPool.putBack(conn)
