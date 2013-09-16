# coding=UTF-8
# Source grain parameters: version=1.0, len=2600, crc32=925FC6B9.
"""
THIS MODULE IS BEING CREATED AUTOMATICALLY EVERY TIME CELESTA STARTS.
DO NOT MODIFY IT AS YOUR CHANGES WILL BE LOST.
"""
import ru.curs.celesta.dbutils.Cursor as Cursor
from java.lang import Object
from jarray import array

class dirTypesCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.name = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'dirTypes'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.name = rs.getString(2)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.name = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.name], Object)
    def preDelete(self):
        for f in dirTypesCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in dirTypesCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in dirTypesCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in dirTypesCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in dirTypesCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in dirTypesCursor.onPostUpdate:
            f(self)

class viewTypesCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.name = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'viewTypes'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.name = rs.getString(2)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.name = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.name], Object)
    def preDelete(self):
        for f in viewTypesCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in viewTypesCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in viewTypesCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in viewTypesCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in viewTypesCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in viewTypesCursor.onPostUpdate:
            f(self)

class foldersCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.name = None
        self.parentId = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'folders'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.name = rs.getString(2)
        self.parentId = rs.getInt(3)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.name = None
        self.parentId = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.name, self.parentId], Object)
    def preDelete(self):
        for f in foldersCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in foldersCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in foldersCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in foldersCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in foldersCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in foldersCursor.onPostUpdate:
            f(self)

class directionsCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.grain = None
        self.name = None
        self.prefix = None
        self.tableName = None
        self.dirTypeId = None
        self.folderId = None
        self.viewTypeId = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'directions'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.grain = rs.getString(2)
        self.name = rs.getString(3)
        self.prefix = rs.getString(4)
        self.tableName = rs.getString(5)
        self.dirTypeId = rs.getInt(6)
        self.folderId = rs.getInt(7)
        self.viewTypeId = rs.getInt(8)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.grain = None
        self.name = None
        self.prefix = None
        self.tableName = None
        self.dirTypeId = None
        self.folderId = None
        self.viewTypeId = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.grain, self.name, self.prefix, self.tableName, self.dirTypeId, self.folderId, self.viewTypeId], Object)
    def preDelete(self):
        for f in directionsCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in directionsCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in directionsCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in directionsCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in directionsCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in directionsCursor.onPostUpdate:
            f(self)

class fieldsTypesCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.name = None
        self.celestaType = None
        self.defLength = None
        self.defPrecision = None
        self.useInKey = None
        self.useInSelector = None
        self.useInFilter = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'fieldsTypes'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.name = rs.getString(2)
        self.celestaType = rs.getString(3)
        self.defLength = rs.getInt(4)
        self.defPrecision = rs.getInt(5)
        self.useInKey = rs.getBoolean(6)
        self.useInSelector = rs.getBoolean(7)
        self.useInFilter = rs.getBoolean(8)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.name = None
        self.celestaType = None
        self.defLength = None
        self.defPrecision = None
        self.useInKey = None
        self.useInSelector = None
        self.useInFilter = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.name, self.celestaType, self.defLength, self.defPrecision, self.useInKey, self.useInSelector, self.useInFilter], Object)
    def preDelete(self):
        for f in fieldsTypesCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in fieldsTypesCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in fieldsTypesCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in fieldsTypesCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in fieldsTypesCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in fieldsTypesCursor.onPostUpdate:
            f(self)

class fieldsCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.dirId = None
        self.name = None
        self.prefix = None
        self.dbFieldName = None
        self.fieldTypeId = None
        self.length = None
        self.precision = None
        self.fieldOrderInGrid = None
        self.isRequired = None
        self.visualLength = None
        self.refKeyId = None
        self.minValue = None
        self.maxValue = None
        self.fieldOrderInSort = None
        self.sortOrder = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'fields'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.dirId = rs.getInt(2)
        self.name = rs.getString(3)
        self.prefix = rs.getString(4)
        self.dbFieldName = rs.getString(5)
        self.fieldTypeId = rs.getInt(6)
        self.length = rs.getInt(7)
        self.precision = rs.getInt(8)
        self.fieldOrderInGrid = rs.getInt(9)
        self.isRequired = rs.getBoolean(10)
        self.visualLength = rs.getInt(11)
        self.refKeyId = rs.getInt(12)
        self.minValue = rs.getString(13)
        self.maxValue = rs.getString(14)
        self.fieldOrderInSort = rs.getInt(15)
        self.sortOrder = rs.getBoolean(16)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.dirId = None
        self.name = None
        self.prefix = None
        self.dbFieldName = None
        self.fieldTypeId = None
        self.length = None
        self.precision = None
        self.fieldOrderInGrid = None
        self.isRequired = None
        self.visualLength = None
        self.refKeyId = None
        self.minValue = None
        self.maxValue = None
        self.fieldOrderInSort = None
        self.sortOrder = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.dirId, self.name, self.prefix, self.dbFieldName, self.fieldTypeId, self.length, self.precision, self.fieldOrderInGrid, self.isRequired, self.visualLength, self.refKeyId, self.minValue, self.maxValue, self.fieldOrderInSort, self.sortOrder], Object)
    def preDelete(self):
        for f in fieldsCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in fieldsCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in fieldsCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in fieldsCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in fieldsCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in fieldsCursor.onPostUpdate:
            f(self)

class refFieldsCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.fieldId = None
        self.visualName = None
        self.refFieldId = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'refFields'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.fieldId = rs.getInt(2)
        self.visualName = rs.getString(3)
        self.refFieldId = rs.getInt(4)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.fieldId = None
        self.visualName = None
        self.refFieldId = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.fieldId, self.visualName, self.refFieldId], Object)
    def preDelete(self):
        for f in refFieldsCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in refFieldsCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in refFieldsCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in refFieldsCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in refFieldsCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in refFieldsCursor.onPostUpdate:
            f(self)

class keysCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.dirId = None
        self.name = None
        self.type = None
        self.useInImport = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'keys'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.dirId = rs.getInt(2)
        self.name = rs.getString(3)
        self.type = rs.getString(4)
        self.useInImport = rs.getBoolean(5)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.dirId = None
        self.name = None
        self.type = None
        self.useInImport = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.dirId, self.name, self.type, self.useInImport], Object)
    def preDelete(self):
        for f in keysCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in keysCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in keysCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in keysCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in keysCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in keysCursor.onPostUpdate:
            f(self)

class keyFieldsCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.keyId = None
        self.fieldId = None
        self.fieldOrder = None
        self.sortOrder = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'keyFields'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.keyId = rs.getInt(2)
        self.fieldId = rs.getInt(3)
        self.fieldOrder = rs.getInt(4)
        self.sortOrder = rs.getBoolean(5)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.keyId = None
        self.fieldId = None
        self.fieldOrder = None
        self.sortOrder = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.keyId, self.fieldId, self.fieldOrder, self.sortOrder], Object)
    def preDelete(self):
        for f in keyFieldsCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in keyFieldsCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in keyFieldsCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in keyFieldsCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in keyFieldsCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in keyFieldsCursor.onPostUpdate:
            f(self)

class filtersConditionsCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.name = None
        self.prefix = None
        self.pythonCond = None
        self.visualCond = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'filtersConditions'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.name = rs.getString(2)
        self.prefix = rs.getString(3)
        self.pythonCond = rs.getString(4)
        self.visualCond = rs.getString(5)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.name = None
        self.prefix = None
        self.pythonCond = None
        self.visualCond = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.name, self.prefix, self.pythonCond, self.visualCond], Object)
    def preDelete(self):
        for f in filtersConditionsCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in filtersConditionsCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in filtersConditionsCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in filtersConditionsCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in filtersConditionsCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in filtersConditionsCursor.onPostUpdate:
            f(self)

class filtersConditionsByTypesCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.fieldTypeId = None
        self.filterConditionId = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'filtersConditionsByTypes'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.fieldTypeId = rs.getInt(2)
        self.filterConditionId = rs.getInt(3)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.fieldTypeId = None
        self.filterConditionId = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.fieldTypeId, self.filterConditionId], Object)
    def preDelete(self):
        for f in filtersConditionsByTypesCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in filtersConditionsByTypesCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in filtersConditionsByTypesCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in filtersConditionsByTypesCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in filtersConditionsByTypesCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in filtersConditionsByTypesCursor.onPostUpdate:
            f(self)

class filtersCursor(Cursor):
    onPreDelete  = []
    onPostDelete = []
    onPreInsert  = []
    onPostInsert = []
    onPreUpdate  = []
    onPostUpdate = []
    def __init__(self, context):
        Cursor.__init__(self, context)
        self.id = None
        self.employeeId = None
        self.name = None
        self.dirId = None
        self.pythonCond = None
        self.visualCond = None
        self.context = context
    def grainName(self):
        return 'dirU'
    def tableName(self):
        return 'filters'
    def parseResult(self, rs):
        self.id = rs.getInt(1)
        self.employeeId = rs.getInt(2)
        self.name = rs.getString(3)
        self.dirId = rs.getInt(4)
        self.pythonCond = rs.getString(5)
        self.visualCond = rs.getString(6)
    def clearBuffer(self, withKeys):
        if withKeys:
            self.id = None
        self.employeeId = None
        self.name = None
        self.dirId = None
        self.pythonCond = None
        self.visualCond = None
    def currentKeyValues(self):
        return array([self.id], Object)
    def currentValues(self):
        return array([self.id, self.employeeId, self.name, self.dirId, self.pythonCond, self.visualCond], Object)
    def preDelete(self):
        for f in filtersCursor.onPreDelete:
            f(self)
    def postDelete(self):
        for f in filtersCursor.onPostDelete:
            f(self)
    def preInsert(self):
        for f in filtersCursor.onPreInsert:
            f(self)
    def postInsert(self):
        for f in filtersCursor.onPostInsert:
            f(self)
    def preUpdate(self):
        for f in filtersCursor.onPreUpdate:
            f(self)
    def postUpdate(self):
        for f in filtersCursor.onPostUpdate:
            f(self)

