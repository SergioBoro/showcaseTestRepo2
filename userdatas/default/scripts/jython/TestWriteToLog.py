# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc


class TestWriteToLog(JythonProc):
    def execute(self, context):
        print context.getMain() + u" из jython"
        print u"из jython 2"
