<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [ <!ENTITY nbsp " "> ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
<xsl:output indent="yes" method="xml" omit-xml-declaration="no"/>
    <xsl:template match="/">
        <xsl:copy>
            <xsl:for-each select="xs:schema">
                <xsl:element name="xs:schema">  
                <xsl:copy-of select="@*"/>                  
                <xsl:for-each select="xs:simpleType">
                	<xsl:element name="xs:simpleType">
                		<xsl:copy-of select="@*"/>  
                		<xsl:for-each select="xs:restriction">
                			<xsl:element name="xs:restriction">
                				<xsl:copy-of select="@*"/>                			
                				<xsl:for-each select="xs:enumeration">                					
                					<xsl:copy-of select="."/>
                					<xs:enumeration>
                						<xsl:attribute name="value" select="lower-case(@value)"/>
                					</xs:enumeration>
                				</xsl:for-each>
                			</xsl:element>
                		</xsl:for-each>
                	</xsl:element>
					<xsl:text>

</xsl:text>                	
                </xsl:for-each>                
                </xsl:element>     
        	</xsl:for-each>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
