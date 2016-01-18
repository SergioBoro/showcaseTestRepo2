# coding=UTF-8
# Source grain parameters: version=1.0, len=468, crc32=86871CFD; compiler=7.
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
        self.attrVarchar = None
        self.attrInt = None
        self.f1 = None
        self.f2 = None
        self.f4 = None
        self.f5 = None
        self.f6 = None
        self.f7 = None
        self.f8 = None
        self.f9 = None
        self.context = context
    def _grainName(self):
        return 'testgrain'
    def _tableName(self):
        return 'test'
    def _parseResult(self, rs):
        self.id = rs.getInt('id')
        if rs.wasNull():
            self.id = None
        self.attrVarchar = rs.getString('attrVarchar')
        if rs.wasNull():
            self.attrVarchar = None
        self.attrInt = rs.getInt('attrInt')
        if rs.wasNull():
            self.attrInt = None
        self.f1 = rs.getBoolean('f1')
        if rs.wasNull():
            self.f1 = None
        self.f2 = rs.getBoolean('f2')
        if rs.wasNull():
            self.f2 = None
        self.f4 = rs.getDouble('f4')
        if rs.wasNull():
            self.f4 = None
        self.f5 = rs.getDouble('f5')
        if rs.wasNull():
            self.f5 = None
        self.f6 = rs.getString('f6')
        if rs.wasNull():
            self.f6 = None
        self.f7 = rs.getString('f7')
        if rs.wasNull():
            self.f7 = None
        self.f8 = rs.getTimestamp('f8')
        if rs.wasNull():
            self.f8 = None
        self.f9 = rs.getTimestamp('f9')
        if rs.wasNull():
            self.f9 = None
        self.recversion = rs.getInt('recversion')
    def _setFieldValue(self, name, value):
        setattr(self, name, value)
    def _clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.attrVarchar = None
        self.attrInt = None
        self.f1 = None
        self.f2 = None
        self.f4 = None
        self.f5 = None
        self.f6 = None
        self.f7 = None
        self.f8 = None
        self.f9 = None
    def _currentKeyValues(self):
        return array([None if self.id == None else int(self.id)], Object)
    def _currentValues(self):
        return array([None if self.id == None else int(self.id), None if self.attrVarchar == None else unicode(self.attrVarchar), None if self.attrInt == None else int(self.attrInt), None if self.f1 == None else bool(self.f1), None if self.f2 == None else bool(self.f2), None if self.f4 == None else float(self.f4), None if self.f5 == None else float(self.f5), None if self.f6 == None else unicode(self.f6), None if self.f7 == None else unicode(self.f7), _to_timestamp(self.f8), _to_timestamp(self.f9)], Object)
    def _setAutoIncrement(self, val):
        self.id = val
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
        self.attrVarchar = c.attrVarchar
        self.attrInt = c.attrInt
        self.f1 = c.f1
        self.f2 = c.f2
        self.f4 = c.f4
        self.f5 = c.f5
        self.f6 = c.f6
        self.f7 = c.f7
        self.f8 = c.f8
        self.f9 = c.f9
        self.recversion = c.recversion
    def iterate(self):
        if self.tryFindSet():
            while True:
                yield self
                if not self.nextInSet():
                    break

