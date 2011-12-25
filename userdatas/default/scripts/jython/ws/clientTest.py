# coding: utf-8

from __future__ import with_statement
from suds.client import Client
#from org.xml.sax.helpers import DefaultHandler;
#from ru.curs.showcase.util import TextUtils;

# init vars
request = '''
<a:schema xmlns:a="urn:rostransnadzor.ru:anyWithAsm:ed"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="urn:anyWithAsm:ed file:/D:/web-services/any2AsmPlans.xsd"
    dateTime="2011-12-01T12:12:12" ID="11111111-1111-1111-1111-111111111111" version="123" mode="0">
    <a:query>
        <a:inn>381704859664</a:inn>
        <a:ogrn>305381714500081</a:ogrn>
        <a:organizationName>ДЕМЕНТ АННА ГРИГОРЬЕВНА</a:organizationName>
        <a:organizationNameSearchWhole>true</a:organizationNameSearchWhole>
    </a:query>
</a:schema>
'''


def mainproc():
    #url = 'http://pluton.rostransnadzor.ru:8085/rostrans/forall/webservices?wsdl'
    url = 'http://share.curs.ru:8080/mrs/forall/webservices?wsdl'
    client = Client(url)
    print client
    print client.service.handle(request, "getPlans.py")

if __name__ == "__main__":
    mainproc()
