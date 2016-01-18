# coding=UTF-8
from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import testCursor

@form
class TestGridForm(GridForm):  
    def __init__(self, context):
        super(GridForm, self).__init__(context)
        self.createAllBoundFields()
        
    def _getCursor(self, context):
        return testCursor(context)



