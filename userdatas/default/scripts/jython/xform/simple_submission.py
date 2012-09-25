# coding: UTF-8
'''
Created on 19.01.2012

@author: den
'''

from ru.curs.showcase.core.jython import JythonProc

# init vars
data = u'''<data a="test">тест</data>'''


class simple_submission(JythonProc):
    def transform(self, context, adata):
        global source, data, root
        data = adata
        return mainproc()


def mainproc():
    return u'''
<!--
<?xml version="1.0" encoding="UTF-8"?>
-->    
<schema>
   <info>
      <name>Николай</name>
      <growth/>
      <eyescolour/>
      <music>Классическая Эстрадная</music>
      <comment>xform из Jython</comment>
   </info>
</schema>
'''

if __name__ == '__main__':
    mainproc()
