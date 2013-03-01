# coding: utf-8
'''
Created on 17.12.2011

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc, JythonDTO
from ru.curs.showcase.util.xml import XMLUtils
from org.xml.sax.helpers import DefaultHandler
from ru.curs.showcase.util import TextUtils

# init vars
data = '''
    <item text="item1" cls="folder">
        <children>
            <item text="item 1.1" leaf="true" checked="false"/>
        </children>
    </item>'''
result = u''


class myHandler(DefaultHandler):
    isFirst = 1
    def startElement(self, namespaceURI, lname, qname, attrs):
        global result
        if (qname == "items"):
            result += u'['
        elif (qname == "item"):
            if (myHandler.isFirst != 1):
                result += u','
            result += u'{'            
            if (attrs.getIndex("id") > -1):
                result += u"id: '" + attrs.getValue('id') + "'"
            if (attrs.getIndex("name") > -1):
                result += u", name: '" + attrs.getValue('name') + "'"            
            if (attrs.getIndex("cls") > -1):
                result += u", cls: '" + attrs.getValue('cls') + "'"
            if (attrs.getIndex("expanded") > -1):
                result += u", expanded: " + attrs.getValue('expanded')
            if (attrs.getIndex("leaf") > -1):
                result += u", leaf: " + attrs.getValue('leaf')
            if (attrs.getIndex("checked") > -1):
                result += u", checked: " + attrs.getValue('checked')
            if (attrs.getIndex("attr1") > -1):
                result += u", attr1: '" + attrs.getValue('attr1') + "'"     
        elif (qname == "children"):
            result += u", children: ["
            myHandler.isFirst = 1
    def endElement(self, namespaceURI, lname, qname):
        global result
        if (qname == "items"):
            result += u']'
        elif (qname == "item"):
            result += u'}'            
            myHandler.isFirst = 0
        elif (qname == "children"):
            result += u"]"

class handleExtJsTree(JythonProc):

    def postProcess(self, context, elId, adata):
        global data
        data = adata
        return mainproc()


def mainproc():	
    global result
    parser = XMLUtils.createSAXParser()
    stream = TextUtils.stringToStream(data)
    parser.parse(stream, myHandler())
    return JythonDTO([result])

if __name__ == "__main__":
    from org.python.core import codecs
    codecs.setDefaultEncoding('utf-8')
    mainproc()
