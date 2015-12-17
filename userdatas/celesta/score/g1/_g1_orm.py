# coding=UTF-8
# Source grain parameters: version=1.0, len=125, crc32=83372D95; compiler=7.
"""
THIS MODULE IS BEING CREATED AUTOMATICALLY EVERY TIME CELESTA STARTS.
DO NOT MODIFY IT AS YOUR CHANGES WILL BE LOST.
"""
import ru.curs.celesta.dbutils.Cursor as Cursor
import ru.curs.celesta.dbutils.ViewCursor as ViewCursor
import ru.curs.celesta.dbutils.ReadOnlyTableCursor as ReadOnlyTableCursor
from java.lang import Object
from jarray import array
from java.util import Calendar, GregorianCalendar
from java.sql import Timestamp
import datetime

def _to_timestamp(d):
    if isinstance(d, datetime.datetime):
        calendar = GregorianCalendar()
        calendar.set(d.year, d.month - 1, d.day, d.hour, d.minute, d.second)
        ts = Timestamp(calendar.getTimeInMillis())
        ts.setNanos(d.microsecond * 1000)
        return ts
    else:
        return d

class testCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.intAttr = None
        self.textAttr = None
        self.context = context
    def _grainName(self):
        return 'g1'
    def _tableName(self):
        return 'test'
    def _parseResult(self, rs):
        self.id = rs.getInt('id')
        if rs.wasNull():
            self.id = None
        self.intAttr = rs.getInt('intAttr')
        if rs.wasNull():
            self.intAttr = None
        self.textAttr = rs.getString('textAttr')
        if rs.wasNull():
            self.textAttr = None
        self.recversion = rs.getInt('recversion')
    def _setFieldValue(self, name, value):
        setattr(self, name, value)
    def _clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.intAttr = None
        self.textAttr = None
    def _currentKeyValues(self):
        return array([None if self.id == None else int(self.id)], Object)
    def _currentValues(self):
        return array([None if self.id == None else int(self.id), None if self.intAttr == None else int(self.intAttr), None if self.textAttr == None else unicode(self.textAttr)], Object)
    def _setAutoIncrement(self, val):
        pass
    def _preDelete(self):
        for f in testCursor.onPreDelete:
            f(self)
    def _postDelete(self):
        for f in testCursor.onPostDelete:
            f(self)
    def _preInsert(self):
        for f in testCursor.onPreInsert:
            f(self)
    def _postInsert(self):
        for f in testCursor.onPostInsert:
            f(self)
    def _preUpdate(self):
        for f in testCursor.onPreUpdate:
            f(self)
    def _postUpdate(self):
        for f in testCursor.onPostUpdate:
            f(self)
    def _getBufferCopy(self, context):
        result = testCursor(context)
        result.copyFieldsFrom(self)
        return result
    def copyFieldsFrom(self, c):
        self.id = c.id
        self.intAttr = c.intAttr
        self.textAttr = c.textAttr
        self.recversion = c.recversion
    def iterate(self):
        if self.tryFindSet():
            while True:
                yield self
                if not self.nextInSet():
                    break

