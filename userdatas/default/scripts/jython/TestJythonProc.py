# coding=UTF-8
# don't work in jython # -*- coding UTF-8 -*-
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.model.event import JythonProc;
from ru.curs.showcase.util.xml import XMLUtils;  
from datetime import date;

class TestJythonProc(JythonProc):
    def __init__(self):
        print "init func!";        
    def execute(self, obj):
        self.obj = obj;   
        if (not obj.getMain()):       
            raise Exception("не нравится мне этот контекст!")        
        print "main context is %s" % (obj.getMain());
        print "add context is %s" % (obj.getAdditional());
        print "filter context is %s" % (obj.getFilter());
        print "session context is %s" % (obj.getSession());
        # пример работы с функциями Showcase
        session = XMLUtils.stringToDocument(obj.getSession());
        print "userdata is %s" % (session.getDocumentElement().getElementsByTagName("userdata").item(0).getChildNodes().item(0).getNodeValue());
        doc = XMLUtils.createEmptyDoc("activity");
        el = doc.createElement("context");
        doc.getDocumentElement().appendChild(el);
        el.setAttribute("main", obj.getMain());
        # пример работы с модулями Jython
        print "Дата: %s" % date.today();