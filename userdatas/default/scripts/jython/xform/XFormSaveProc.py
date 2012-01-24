# coding: utf-8
'''
Created on 19.01.2012

@author: den
'''

from ru.curs.showcase.model.jython import JythonProc
from ru.curs.showcase.runtime import AppInfoSingleton
import codecs

# init vars
source = 'formdata.xml'
data = u'''<?xml version="1.0" encoding="UTF-8"?>
<test a="test">тест</test>'''
root = 'x:/jprojects/Showcase/userdatas/default/data/xforms'


class XFormSaveProc(JythonProc):
    def save(self, context, elId, adata):
        global source, data, root
        if context.getAdditional():
            source = context.getAdditional()
        else:
            source = context.getMain()
        data = adata
        root = AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\data\\xforms\\"
        return mainproc()


def mainproc():
    filename = root + source
    f = codecs.open(filename, 'w', 'utf-8')
    f.write(data)
    f.close()
    return None

if __name__ == '__main__':
    mainproc()
