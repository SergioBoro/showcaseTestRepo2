# coding=UTF-8
from lyra.gridForm import GridForm
from lyra.basicForm import form
from lyra.basicForm import formfield
from _testgrain_orm import test2Cursor

@form( )
class TestGridForm4(GridForm):  
    def __init__(self, context):
        super(TestGridForm4, self).__init__(context)
        self.createAllBoundFields()
        
    def _getCursor(self, context):
        
        c = test2Cursor(context) 

        return c
    

    def getGridHeight(self):
        return 20
    
    
        
        

