<?xml version="1.0" encoding="UTF-8"?>
<!--
  Splits the flat list of tariffs into sub-lists, one element per mobile operator.
  XSLT 1.0, grouping by the Muenchian method (key + generate-id).
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://lab2.ua/tariffs"
                xmlns:t="http://lab2.ua/tariffs"
                exclude-result-prefixes="t">

    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:key name="tariffs-by-operator" match="t:Tariff" use="t:OperatorName"/>

    <xsl:template match="/t:Tariffs">
        <Tariffs>
            <!-- the first tariff of every operator starts a new group -->
            <xsl:for-each select="t:Tariff[generate-id() =
                                           generate-id(key('tariffs-by-operator', t:OperatorName)[1])]">
                <xsl:element name="{translate(t:OperatorName, ' ', '_')}">
                    <xsl:apply-templates select="key('tariffs-by-operator', t:OperatorName)"/>
                </xsl:element>
            </xsl:for-each>
        </Tariffs>
    </xsl:template>

    <!-- copies a tariff as is, without carrying over unused namespace declarations -->
    <xsl:template match="t:*">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
