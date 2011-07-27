<?xml version="1.0" encoding="UTF-8"?>
<!--

  XSL Transform to convert LIDO XML data, according to http://www.lido-schema.org/schema/v0.9/lido-v0.9.xsd, 
	into ESE XML, according to http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd

  By Regine Stein, Deutsches Dokumentationszentrum für Kunstgeschichte - Bildarchiv Foto Marburg, Philipps-Universität Marburg
  Provided for ATHENA project, 2010-09-17. 

-->
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ns0="http://www.lido-schema.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" 
	xmlns:xml="http://www.w3.org/XML/1998/namespace" 
	xmlns:lido="http://www.lido-schema.org" 
    xmlns:europeana="http://www.europeana.eu/schemas/ese/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	exclude-result-prefixes="lido xs fn">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<europeana:metadata 
			xmlns:europeana="http://www.europeana.eu/schemas/ese/" 
			xmlns:dcmitype="http://purl.org/dc/dcmitype/" 
			xmlns:dc="http://purl.org/dc/elements/1.1/" 
			xmlns:dcterms="http://purl.org/dc/terms/">
			<xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="'http://www.europeana.eu/schemas/ese/ http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd'"/>
			<xsl:for-each select="lido:lidoWrap/lido:lido">
				
				<xsl:choose>

					<xsl:when test="contains(lido:administrativeMetadata/lido:recordWrap/lido:recordType, '/multipleResources') 
						and 
						lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:linkResource[@lido:type='image_thumb'][not(.='')]">

						<xsl:for-each select="lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:linkResource[@lido:type='image_thumb'][not(.='')]">

							<!-- multipleResources: create ESE record for each image_thumb resource -->
				<europeana:record>

					<xsl:for-each select="../../../../lido:descriptiveMetadata">
						<xsl:call-template name="descriptiveMetadata" />
					</xsl:for-each>

					<!-- specific resource view information -->
					<xsl:for-each select="..">
						<xsl:call-template name="resourceView" />
					</xsl:for-each>

					<xsl:for-each select="../../..">
						<xsl:call-template name="work" />
						<xsl:call-template name="record" />
					</xsl:for-each>
					<xsl:for-each select="..">
						<xsl:call-template name="resource" />
					</xsl:for-each>
					
					<xsl:for-each select="../../../..//lido:term[@lido:addedSearchTerm = 'yes'][not(.='')]
						| ../../../..//lido:appellationValue[(@lido:pref = 'alternate')][not(.='')]
						| ../../../..//lido:legalBodyName[not(position() = 1)]/lido:appellationValue[not(.='')]
						| ../../../..//lido:partOfPlace//lido:appellationValue[not(.='')]
						| ../../../..//lido:placeClassification/lido:term[not(.='')]
						">
							<europeana:unstored>
								<xsl:value-of select="." />
							</europeana:unstored>
					</xsl:for-each>

<!-- Europeana elements in requested order --> 

					<europeana:object>
						<xsl:value-of select="." />
					</europeana:object>

					<europeana:provider>ATHENA project</europeana:provider>

					<europeana:type>
						<!-- no default value for europeana:type as decided at Ljubjlana plenary -->
						<!--xsl:choose>
							<xsl:when test="../../../../lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification[@lido:type = 'europeana:type']/lido:term">
							<xsl:value-of select="../../../../lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification[@lido:type = 'europeana:type']/lido:term[position() = 1]" />
							</xsl:when>
							<xsl:otherwise>IMAGE</xsl:otherwise>
						</xsl:choose-->
						<xsl:value-of select="../../../../lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification[@lido:type = 'europeana:type'][1]/lido:term[not(.='')][1]" />
					</europeana:type>

					<europeana:dataProvider>
						<xsl:choose>
							<xsl:when test="contains(../../../../lido:lidoRecID, 'DE-Mb112')">Bildarchiv Foto Marburg</xsl:when>
							<xsl:otherwise>
								<xsl:for-each select="../../../lido:recordWrap/lido:recordSource/lido:legalBodyName/lido:appellationValue[not(.='')][1]">
									<xsl:value-of select="." />
									<xsl:if test="position()!=last()">
										<xsl:text> / </xsl:text>
									</xsl:if>
								</xsl:for-each>
							</xsl:otherwise>
						</xsl:choose>
					</europeana:dataProvider>

					<xsl:for-each select="../../../lido:recordWrap/lido:recordInfoSet/lido:recordInfoLink[not(.='')]">
						<xsl:if test="position() = 1">
							<europeana:isShownAt>
								<xsl:value-of select="." />
							</europeana:isShownAt>
						</xsl:if>
					</xsl:for-each>

					<xsl:if test="not(../../../lido:recordWrap/lido:recordInfoSet/lido:recordInfoLink)">
						<europeana:isShownBy>
							<xsl:value-of select="." />
						</europeana:isShownBy>
					</xsl:if>

				</europeana:record>
								</xsl:for-each>
							</xsl:when>

							<!-- multipleResources: create ESE record for EACH resource -->
							<xsl:when test="contains(lido:administrativeMetadata/lido:recordWrap/lido:recordType, '/multipleResources')">
								<xsl:for-each select="lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet[count(not(lido:linkResource='')) &gt; 0]">
				<europeana:record>

					<xsl:for-each select="../../../lido:descriptiveMetadata">
						<xsl:call-template name="descriptiveMetadata" />
					</xsl:for-each>
					
					<!-- specific resource view information -->
					<xsl:call-template name="resourceView" />

					<xsl:for-each select="../..">
						<xsl:call-template name="work" />
						<xsl:call-template name="record" />
					</xsl:for-each>
					<xsl:call-template name="resource" />
					
					<xsl:for-each select="../../..//lido:term[@lido:addedSearchTerm = 'yes'][not(.='')]
						| ../../..//lido:appellationValue[@lido:pref = 'alternate'][not(.='')]
						| ../../..//lido:legalBodyName[not(position() = 1)]/lido:appellationValue[not(.='')]
						| ../../..//lido:partOfPlace//lido:appellationValue[not(.='')]
						| ../../..//lido:placeClassification/lido:term[not(.='')]
						">
							<europeana:unstored>
								<xsl:value-of select="." />
							</europeana:unstored>
					</xsl:for-each>

<!-- Europeana elements in requested order --> 
						<xsl:for-each select="lido:linkResource[not(.='')]">
							<xsl:if test="position() = 1">
								<europeana:object>
									<xsl:value-of select="." />
								</europeana:object>
							</xsl:if>
						</xsl:for-each>

					<europeana:provider>ATHENA project</europeana:provider>

					<europeana:type>
						<xsl:value-of select="../../../lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification[@lido:type = 'europeana:type'][1]/lido:term[not(.='')][1]" />
					</europeana:type>

					<europeana:dataProvider>
						<xsl:for-each select="../..//lido:recordWrap/lido:recordSource/lido:legalBodyName/lido:appellationValue[not(.='')][1]">
							<xsl:value-of select="." />
                            <xsl:if test="position()!=last()">
                                <xsl:text> / </xsl:text>
                            </xsl:if>
						</xsl:for-each>
					</europeana:dataProvider>

					<xsl:for-each select="../..//lido:recordWrap/lido:recordInfoSet/lido:recordInfoLink[not(.='')]">
						<xsl:if test="position() = 1">
							<europeana:isShownAt>
								<xsl:value-of select="." />
							</europeana:isShownAt>
						</xsl:if>
					</xsl:for-each>

					<xsl:if test="not(../..//lido:recordWrap/lido:recordInfoSet/lido:recordInfoLink)">
						<xsl:for-each select="lido:linkResource[not(.='')][1]">
								<europeana:isShownBy>
									<xsl:value-of select="." />
								</europeana:isShownBy>
						</xsl:for-each>
					</xsl:if>

				</europeana:record>											
								</xsl:for-each>
					</xsl:when>

					<xsl:otherwise>

				<europeana:record>

					<xsl:for-each select="lido:descriptiveMetadata">
						<xsl:call-template name="descriptiveMetadata" />
					</xsl:for-each>

					<xsl:for-each select="lido:administrativeMetadata">
						<xsl:call-template name="work" />
						<xsl:call-template name="record" />
					</xsl:for-each>
					<xsl:for-each select="lido:administrativeMetadata">
						<xsl:for-each select="lido:resourceWrap/lido:resourceSet[count(not(lido:linkResource='')) &gt; 0][1]">
							<xsl:call-template name="resource" />
							<xsl:call-template name="resourceView" />
						</xsl:for-each>
					</xsl:for-each>
					
					<xsl:for-each select=".//lido:term[@lido:addedSearchTerm = 'yes'][not(.='')]
						| .//lido:appellationValue[@lido:pref = 'alternate'][not(.='')]
						| .//lido:legalBodyName[not(position() = 1)]/lido:appellationValue[not(.='')]
						| .//lido:partOfPlace//lido:appellationValue[not(.='')]
						| .//lido:placeClassification/lido:term[not(.='')]
						">
							<europeana:unstored>
								<xsl:value-of select="." />
							</europeana:unstored>
					</xsl:for-each>

<!-- Europeana elements in requested order --> 

						<xsl:for-each select="lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:linkResource[@lido:type = 'image_thumb'][not(.='')]">
							<xsl:if test="position() = 1">
								<europeana:object>
									<xsl:value-of select="." />
								</europeana:object>
							</xsl:if>
						</xsl:for-each>

					<europeana:provider>ATHENA project</europeana:provider>

					<europeana:type>
						<xsl:value-of select="lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification[@lido:type = 'europeana:type'][1]/lido:term[not(.='')][1]" />
					</europeana:type>

					<europeana:dataProvider>
						<xsl:for-each select="lido:administrativeMetadata/lido:recordWrap/lido:recordSource/lido:legalBodyName/lido:appellationValue[not(.='')][1]">
							<xsl:value-of select="." />
                            <xsl:if test="position()!=last()">
                                <xsl:text> / </xsl:text>
                            </xsl:if>
						</xsl:for-each>
					</europeana:dataProvider>

					<xsl:for-each select="lido:administrativeMetadata/lido:recordWrap/lido:recordInfoSet/lido:recordInfoLink[not(.='')]">
						<xsl:if test="position() = 1">
							<europeana:isShownAt>
								<xsl:value-of select="." />
							</europeana:isShownAt>
						</xsl:if>
					</xsl:for-each>

					<xsl:choose>
						<xsl:when test="lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:linkResource[@lido:type = 'image_master'][not(.='')]">
						<xsl:for-each select="lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:linkResource[@lido:type = 'image_master'][not(.='')]">
							<xsl:if test="position() = 1">
								<europeana:isShownBy>
									<xsl:value-of select="." />
								</europeana:isShownBy>
							</xsl:if>
						</xsl:for-each>
						</xsl:when>
						<xsl:when test="lido:administrativeMetadata/lido:recordWrap/lido:recordInfoSet/lido:recordInfoLink[not(.='')]" />
						<xsl:otherwise>
							<xsl:for-each select="lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:linkResource[not(.='')]">
								<xsl:if test="position() = 1">
									<europeana:isShownBy>
										<xsl:value-of select="." />
									</europeana:isShownBy>
								</xsl:if>
							</xsl:for-each>
						</xsl:otherwise>
					</xsl:choose>

				</europeana:record>					
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</europeana:metadata>
	</xsl:template>
	
	<xsl:template name="descriptiveMetadata">

					<xsl:variable name="desclang">
						<xsl:value-of select="@xml:lang" />
					</xsl:variable>

					<xsl:for-each select="lido:objectIdentificationWrap/lido:titleWrap/lido:titleSet/lido:appellationValue[not(.='')]">
					<xsl:choose>
						<xsl:when test=" @lido:pref = 'alternate'">
							<dcterms:alternative>
								<xsl:if test="@xml:lang">
									<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
								</xsl:if>
								<xsl:value-of select="."/>
							</dcterms:alternative>
						</xsl:when>
						<xsl:otherwise>
							<dc:title>
								<xsl:if test="@xml:lang">
									<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
								</xsl:if>
								<xsl:value-of select="."/>
							</dc:title>
						</xsl:otherwise>
					</xsl:choose>
					</xsl:for-each>

					<xsl:for-each select="lido:objectClassificationWrap/lido:objectWorkTypeWrap/lido:objectWorkType/lido:term[not(.='')][not(@lido:addedSearchTerm = 'yes')]">
						<dc:type>
								<xsl:if test="@xml:lang">
									<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
								</xsl:if>
							<xsl:value-of select="."/>
						</dc:type>
					</xsl:for-each>

					<xsl:for-each select="lido:objectClassificationWrap/lido:classificationWrap/lido:classification[not(contains(@lido:type, 'europeana:')) and not(contains(@lido:type, 'euroepana:'))]/lido:term[not(.='')][not(@lido:addedSearchTerm = 'yes')]">
						<xsl:choose>
							<xsl:when test="lower-case(../@lido:type) = 'colour'
								or lower-case(../@lido:type) = 'age'
								or lower-case(../@lido:type) = 'object-status'
								" />
							<xsl:otherwise>
								<dc:type>
									<xsl:if test="@xml:lang">
										<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
									</xsl:if>
									<xsl:value-of select="."/>
									<xsl:if test="../@lido:type"> (<xsl:value-of select="../@lido:type" />)</xsl:if>
								</dc:type>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>

					<xsl:for-each select="lido:objectIdentificationWrap/lido:objectDescriptionWrap/lido:objectDescriptionSet/lido:descriptiveNoteValue[not(.='')]">
						<dc:description>
								<xsl:if test="@xml:lang">
									<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
								</xsl:if>
								<xsl:if test="../@lido:type">
									<xsl:value-of select="concat(../@lido:type, ': ')"/>
								</xsl:if>
							<xsl:value-of select="."/>
						</dc:description>
					</xsl:for-each>

					<xsl:for-each select="lido:objectIdentificationWrap/lido:inscriptionsWrap/lido:inscriptions[not(.='')]">
						<xsl:variable name="type">
							<xsl:choose>
								<xsl:when test="@lido:type"><xsl:value-of select="." /></xsl:when>
								<xsl:when test="$desclang eq 'de' or $desclang eq 'deu' or $desclang eq 'ger'">Inschrift</xsl:when>
								<xsl:otherwise>Inscription</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<dc:description>
							<xsl:if test="@xml:lang">
								<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
							</xsl:if>
							<xsl:value-of select="concat($type, ': ', .)" />
						</dc:description>
					</xsl:for-each>

					<xsl:for-each select="lido:objectIdentificationWrap/lido:displayStateEditionWrap/*[not(.='')]">
						<dc:description>
							<xsl:if test="@xml:lang">
								<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
							</xsl:if>
							<xsl:value-of select="concat(substring-after(name(), 'display'), ': ', .)" />
						</dc:description>
					</xsl:for-each>

					<xsl:for-each select="lido:objectIdentificationWrap/lido:repositoryWrap/lido:repositorySet[not(@lido:type='former')]/lido:workID[not(.='')]">
						<dc:identifier>
						   <xsl:value-of select="concat(@lido:type, ' ',.)"/>						           
						</dc:identifier>
					</xsl:for-each>

					<xsl:for-each select="lido:objectIdentificationWrap/lido:repositoryWrap/lido:repositorySet[not(.//lido:appellationValue='')]">
						<xsl:variable name="qualifier">
							<xsl:choose>
								<xsl:when test="@lido:repositoryType='former' and ($desclang eq 'de' or $desclang eq 'deu' or $desclang eq 'ger')">Frühere Aufbewahrung/Standort: </xsl:when>
								<xsl:when test="@lido:repositoryType='former'">Former Repository/Location: </xsl:when>
								<xsl:when test="$desclang eq 'de' or $desclang eq 'deu' or $desclang eq 'ger'">Aufbewahrung/Standort: </xsl:when>
								<xsl:otherwise>Repository/Location: </xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<dc:description>
						   <xsl:value-of select="concat($qualifier, lido:repositoryName/lido:legalBodyName[count(not(lido:appellationValue='')) &gt; 0][1]/lido:appellationValue[1], ' ', lido:repositoryLocation/lido:namePlaceSet[count(not(lido:appellationValue='')) &gt; 0][1]/lido:appellationValue[1])"/>
						</dc:description>
					</xsl:for-each>

						<xsl:for-each select="lido:objectIdentificationWrap/lido:objectMeasurementsWrap/lido:objectMeasurementsSet">
							<xsl:choose>
								<xsl:when test="lido:objectMeasurements[not(.='')]">
								<xsl:for-each select="lido:objectMeasurements[not(.='')]">
									<xsl:variable name="qualifier">
										<xsl:choose>
											<xsl:when test="lido:qualifierMeasurements[not(.='')]"><xsl:value-of select="concat(lido:qualifierMeasurements[not(.='')][1], ' ')" /></xsl:when>
											<xsl:otherwise />
										</xsl:choose>
									</xsl:variable>
									<xsl:for-each select="lido:measurementsSet[not(@lido:value='')]">
									<dcterms:extent>
										<xsl:value-of select="$qualifier" />
										<xsl:value-of select="concat(@lido:type, ': ', @lido:value, ' ', @lido:unit)"/>
										<xsl:for-each select="../lido:extentMeasurements[not(.='')]"><xsl:value-of select="concat(' (', ., ')')" /></xsl:for-each>
									</dcterms:extent>
									</xsl:for-each>
									<xsl:for-each select="lido:formatMeasurements[not(.='')]">
										<xsl:variable name="type">
											<xsl:choose>
												<xsl:when test="$desclang eq 'de' or $desclang eq 'deu' or $desclang eq 'ger'">Format</xsl:when>
												<xsl:otherwise>Format</xsl:otherwise>
											</xsl:choose>
										</xsl:variable>
										<dcterms:extent>
											<xsl:value-of select="concat($type, ': ', $qualifier, .)"/>
											<xsl:for-each select="../lido:extentMeasurements[not(.='')]"><xsl:value-of select="concat(' (', ., ')')" /></xsl:for-each>
										</dcterms:extent>
									</xsl:for-each>
									<xsl:for-each select="lido:shapeMeasurements[not(.='')]">
										<xsl:variable name="type">
											<xsl:choose>
												<xsl:when test="$desclang eq 'de' or $desclang eq 'deu' or $desclang eq 'ger'">Form</xsl:when>
												<xsl:otherwise>Shape</xsl:otherwise>
											</xsl:choose>
										</xsl:variable>
										<dcterms:extent>
											<xsl:value-of select="concat($type, ': ', $qualifier, .)"/>
											<xsl:for-each select="../lido:extentMeasurements[not(.='')]"><xsl:value-of select="concat(' (', ., ')')" /></xsl:for-each>
										</dcterms:extent>
									</xsl:for-each>
									<xsl:for-each select="lido:scaleMeasurements[not(.='')]">
										<xsl:variable name="type">
											<xsl:choose>
												<xsl:when test="$desclang eq 'de' or $desclang eq 'deu' or $desclang eq 'ger'">Ausmaß</xsl:when>
												<xsl:otherwise>Scale</xsl:otherwise>
											</xsl:choose>
										</xsl:variable>
										<dcterms:extent>
											<xsl:value-of select="concat($type, ': ', $qualifier, .)"/>
											<xsl:for-each select="../lido:extentMeasurements[not(.='')]"><xsl:value-of select="concat(' (', ., ')')" /></xsl:for-each>
										</dcterms:extent>
									</xsl:for-each>
								</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:displayObjectMeasurements[not(.='')]">
									<xsl:for-each select="lido:displayObjectMeasurements[not(.='')]">
									<dcterms:extent>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="."/>
									</dcterms:extent>
									</xsl:for-each>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>

					<xsl:for-each select="lido:objectClassificationWrap/lido:classificationWrap/lido:classification[lower-case(../@lido:type) = 'colour'
								or lower-case(../@lido:type) = 'age'
								or lower-case(../@lido:type) = 'object-status']/lido:term[not(@lido:addedSearchTerm = 'yes')][not(.='')]">
								<dc:description>
									<xsl:if test="@xml:lang">
										<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
									</xsl:if>
									<xsl:value-of select="../@lido:type" />: <xsl:value-of select="."/>
								</dc:description>
						</xsl:for-each>

					<xsl:for-each select="lido:eventWrap/lido:eventSet/lido:event">
						<xsl:variable name="eventType" select="lido:eventType/lido:term[not(.='')][1]"/>
						<xsl:variable name="eventTypeLC" select="lower-case($eventType)"/>
						<xsl:variable name="creation" as="xs:boolean*">
							<xsl:if test="$eventTypeLC = 'creation' 
								or $eventTypeLC = 'create'
								or $eventTypeLC = 'designing'
								or $eventTypeLC = 'planning'
								or $eventTypeLC = 'production'
								or $eventTypeLC = 'publication'
								or $eventTypeLC = 'entwurf'
								or $eventTypeLC = 'erfindung'
								or $eventTypeLC = 'herstellung'
								or $eventTypeLC = 'planung'
								or $eventTypeLC = 'publikation'
								">
								<xsl:sequence select="true()"/>
							</xsl:if>
						</xsl:variable>
						
						<xsl:for-each select="lido:eventActor">
							<xsl:choose>
								<xsl:when test="$creation and lido:actorInRole">
									<xsl:for-each select="lido:actorInRole/lido:actor/lido:nameActorSet/lido:appellationValue[not(.='')]">
									<!-- ignoring alternative names -->
									<xsl:if test="not(@lido:pref = 'alternate')">
									<dc:creator>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang">
												<xsl:value-of select="@xml:lang" />
											</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="."/>
										<xsl:for-each select="../../../lido:roleActor/lido:term[not(@lido:addedSearchTerm = 'yes')][not(.='')]">
											<xsl:choose>
												<xsl:when test="count(not(.='')) = 1 and count(../../lido:roleActor[not(lido:term='')]) = 1"> (<xsl:value-of select="." />)</xsl:when>
												<xsl:when test="position() = 1 and ../../lido:roleActor[not(lido:term='')][position() = 1]"> (<xsl:value-of select="." />, </xsl:when>
												<xsl:when test="position() = last() and ../../lido:roleActor[not(lido:term='')][position() = last()]"><xsl:value-of select="." />)</xsl:when>
												<xsl:otherwise><xsl:value-of select="." />, </xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
										<xsl:if test="not(../../../lido:roleActor/lido:term[not(@lido:addedSearchTerm = 'yes')][not(.='')])"> [<xsl:value-of select="$eventType" />]</xsl:if>
									</dc:creator>
									</xsl:if>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="$creation and lido:displayActorInRole">
									<xsl:for-each select="lido:displayActorInRole[not(.='')]">
									<dc:creator>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang">
												<xsl:value-of select="@xml:lang" />
											</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="."/>
									</dc:creator>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:actorInRole">
									<xsl:for-each select="lido:actorInRole/lido:actor/lido:nameActorSet/lido:appellationValue[not(@lido:pref = 'alternate')][not(.='')]">
									<!-- ignoring alternative names -->
									<dc:contributor>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang">
												<xsl:value-of select="@xml:lang" />
											</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="."/>
										<xsl:for-each select="../../../lido:roleActor/lido:term[not(@lido:addedSearchTerm = 'yes')][not(.='')]">
											<xsl:choose>
												<xsl:when test="count(not(.='')) = 1 and count(../../lido:roleActor[not(lido:term='')]) = 1"> (<xsl:value-of select="." />)</xsl:when>
												<xsl:when test="position() = 1 and ../../lido:roleActor[not(lido:term='')][position() = 1]"> (<xsl:value-of select="." />, </xsl:when>
												<xsl:when test="position() = last() and ../../lido:roleActor[not(lido:term='')][position() = last()]"><xsl:value-of select="." />)</xsl:when>
												<xsl:otherwise><xsl:value-of select="." />, </xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
										<xsl:if test="not(../../../lido:roleActor/lido:term[not(@lido:addedSearchTerm = 'yes')][not(.='')])"> [<xsl:value-of select="$eventType" />]</xsl:if>
									</dc:contributor>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:displayActorInRole">
									<xsl:for-each select="lido:displayActorInRole[not(.='')]">
									<dc:contributor>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang">
												<xsl:value-of select="@xml:lang" />
											</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="concat(., ' [', $eventType, ']')"/>
									</dc:contributor>
									</xsl:for-each>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="lido:culture/lido:term[not(@lido:addedSearchTerm = 'yes')][not(.='')]">
							<xsl:variable name="type">
								<xsl:choose>
									<xsl:when test="$desclang eq 'de' or $desclang eq 'deu' or $desclang eq 'ger'">kultureller Kontext</xsl:when>
									<xsl:otherwise>cultural context</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$creation">
									<dc:creator>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="concat(., ' [', $type, ']', ' [', $eventType, ']')"/>
									</dc:creator>
								</xsl:when>
								<xsl:otherwise>
									<dc:contributor>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="concat(., ' [', $type, ']', ' [', $eventType, ']')"/>
									</dc:contributor>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="lido:eventMethod/lido:term[not(@lido:addedSearchTerm = 'yes')][not(.='')]">
						<xsl:variable name="type">
							<xsl:choose>
								<xsl:when test="$desclang eq 'de' or $desclang eq 'deu' or $desclang eq 'ger'">Methode</xsl:when>
								<xsl:otherwise>method</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
							<dc:description>
								<xsl:if test="@xml:lang">
									<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
								</xsl:if>
								<xsl:value-of select="concat($type, ': ', ., ' [', $eventType, ']')"/>
							</dc:description>
						</xsl:for-each>

						<xsl:for-each select="lido:eventMaterialsTech">
							<xsl:choose>
								<xsl:when test="lido:materialsTech">
									<xsl:for-each select="lido:materialsTech/lido:termMaterialsTech/lido:term[not(@lido:addedSearchTerm = 'yes')][not(.='')]">
									<xsl:choose>
										<xsl:when test="..[contains(lower-case(@lido:type), 'techn')]">
											<dc:description>
												<xsl:if test="@xml:lang">
													<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
												</xsl:if>
												<xsl:value-of select="concat(../@lido:type, ': ', .)"/>
											</dc:description>
										</xsl:when>
										<xsl:when test="..[contains(lower-case(@lido:type), 'material')]">
											<dcterms:medium>
												<xsl:if test="@xml:lang">
													<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
												</xsl:if>
												<xsl:value-of select="."/>
											</dcterms:medium>
										</xsl:when>
									</xsl:choose>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:displayMaterialsTech">
									<xsl:for-each select="lido:displayMaterialsTech[not(.='')]">
									<dcterms:medium>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="."/>
									</dcterms:medium>
									</xsl:for-each>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>
					
						<xsl:for-each select="lido:periodName/lido:term[not(@lido:addedSearchTerm = 'yes')][not(.='')]">
							<xsl:choose>
								<xsl:when test="$creation">
									<dcterms:created>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="concat(., ' [', $eventType, ']')"/>
									</dcterms:created>
								</xsl:when>
								<xsl:otherwise>
									<dc:date>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="concat(., ' [', $eventType, ']')"/>
									</dc:date>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					
						<xsl:for-each select="lido:eventDate">
							<xsl:choose>
								<xsl:when test="$creation and lido:date">
									<xsl:for-each select="lido:date[not(lido:earliestDate='') and not(lido:latestDate='')]">
									<dcterms:created>
										<xsl:choose>
											<xsl:when test="lido:earliestDate = lido:latestDate">
												<xsl:value-of select="concat(lido:earliestDate, ' [', $eventType, ']')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="concat(lido:earliestDate, '/', lido:latestDate, ' [', $eventType, ']')"/>
											</xsl:otherwise>
										</xsl:choose>
									</dcterms:created>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="$creation and lido:displayDate">
									<xsl:for-each select="lido:displayDate[not(.='')]">
									<dcterms:created>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="concat(., ' [', $eventType, ']')"/>
									</dcterms:created>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:date">
									<xsl:for-each select="lido:date[not(lido:earliestDate='') and not(lido:latestDate='')]">
									<dc:date>
										<xsl:choose>
											<xsl:when test="lido:earliestDate = lido:latestDate">
												<xsl:value-of select="concat(lido:earliestDate, ' [', $eventType, ']')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="concat(lido:earliestDate, '/', lido:latestDate, ' [', $eventType, ']')"/>
											</xsl:otherwise>
										</xsl:choose>
									</dc:date>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:displayDate">
									<xsl:for-each select="lido:displayDate[not(.='')]">
									<dc:date>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="concat(., ' [', $eventType, ']')"/>
									</dc:date>
									</xsl:for-each>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="lido:eventPlace">
							<xsl:variable name="qualifier">
								<xsl:choose>
									<xsl:when test="$desclang eq 'de' or $desclang eq 'deu' or $desclang eq 'ger'"> [Ort]</xsl:when>
									<xsl:otherwise> [Place]</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="lido:place">
									<xsl:for-each select="lido:place/lido:namePlaceSet/lido:appellationValue[not(@lido:pref = 'alternate')][not(.='')]">
									<!-- ignoring alternative names -->
										<dcterms:spatial>
											<xsl:if test="@xml:lang">
												<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
											</xsl:if>
											<xsl:value-of select="concat(., $qualifier, ' [', $eventType, ']')"/>
										</dcterms:spatial>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:displayPlace">
									<xsl:for-each select="lido:displayPlace[not(.='')]">
										<dcterms:spatial>
											<xsl:if test="@xml:lang">
												<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
											</xsl:if>
											<xsl:value-of select="concat(., ' [', $eventType, ']')"/>
										</dcterms:spatial>
									</xsl:for-each>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>
					</xsl:for-each>

					<xsl:for-each select="lido:objectRelationWrap/lido:relatedWorksWrap/lido:relatedWorksSet[count(not(lido:relatedWork/lido:object/lido:objectNote='')) &gt; 0]">
						<xsl:choose>
							<xsl:when test="lido:relatedWorkRelType/lido:term ='part of'
								or lido:relatedWorkRelType/lido:term ='Teil von'
								">
								<dcterms:isPartOf>
									<xsl:for-each select="lido:relatedWork">
										<xsl:for-each select="lido:object">
											<xsl:for-each select="lido:objectNote[not(.='')]">
											<xsl:variable name="type">
												<xsl:choose>
													<xsl:when test="@lido:type"><xsl:value-of select="concat(@lido:type, ': ')" /></xsl:when>
													<xsl:otherwise />
												</xsl:choose>
											</xsl:variable>
											<xsl:choose>
												<xsl:when test="count(../lido:objectNote[not(.='')]) = 1">
													<xsl:value-of select="concat($type, .)" />
												</xsl:when>
												<xsl:when test="position() = 1">
													<xsl:value-of select="concat($type, ., ', ')" /></xsl:when>
												<xsl:when test="position() = last()">
													<xsl:value-of select="concat($type, .)" />
												</xsl:when>
												<xsl:otherwise><xsl:value-of select="concat($type, ., ', ')" /></xsl:otherwise>
											</xsl:choose>
											</xsl:for-each>
											<xsl:if test="lido:objectWebResource[not(.='')]">
												<xsl:value-of select="concat(' [', lido:objectWebResource[not(.='')][1], ']')" />
											</xsl:if>
										</xsl:for-each>
									</xsl:for-each>
								</dcterms:isPartOf>
							</xsl:when>
							<xsl:when test="lido:relatedWorkRelType ='has part'
								or lido:relatedWorkRelType ='hat Teil'
								">
								<dcterms:hasPart>
									<xsl:for-each select="lido:relatedWork">
										<xsl:for-each select="lido:object">
											<xsl:for-each select="lido:objectNote[not(.='')]">
											<xsl:variable name="type">
												<xsl:choose>
													<xsl:when test="@lido:type"><xsl:value-of select="concat(@lido:type, ': ')" /></xsl:when>
													<xsl:otherwise />
												</xsl:choose>
											</xsl:variable>
											<xsl:choose>
												<xsl:when test="count(../lido:objectNote[not(.='')]) = 1">
													<xsl:value-of select="concat($type, .)" />
												</xsl:when>
												<xsl:when test="position() = 1">
													<xsl:value-of select="concat($type, ., ', ')" /></xsl:when>
												<xsl:when test="position() = last()">
													<xsl:value-of select="concat($type, .)" />
												</xsl:when>
												<xsl:otherwise><xsl:value-of select="concat($type, ., ', ')" /></xsl:otherwise>
											</xsl:choose>
											</xsl:for-each>
											<xsl:if test="lido:objectWebResource[not(.='')]">
												<xsl:value-of select="concat(' [', lido:objectWebResource[not(.='')][1], ']')" />
											</xsl:if>
										</xsl:for-each>
									</xsl:for-each>
								</dcterms:hasPart>
							</xsl:when>
							<xsl:when test=".//lido:objectNote[not(.='')]">
								<xsl:variable name="reltype">
									<xsl:choose>
										<xsl:when test="lido:relatedWorkRelType/lido:term"><xsl:value-of select="concat(' [', lido:relatedWorkRelType/lido:term[1], ']')" /></xsl:when>
										<xsl:otherwise />
									</xsl:choose>
								</xsl:variable>
								<dc:relation>
									<xsl:for-each select="lido:relatedWork">
										<xsl:for-each select="lido:object">
											<xsl:for-each select="lido:objectNote[not(.='')]">
											<xsl:variable name="type">
												<xsl:choose>
													<xsl:when test="@lido:type"><xsl:value-of select="concat(@lido:type, ': ')" /></xsl:when>
													<xsl:otherwise />
												</xsl:choose>
											</xsl:variable>
											<xsl:choose>
												<xsl:when test="count(../lido:objectNote[not(.='')]) = 1">
													<xsl:value-of select="concat($type, .)" />
												</xsl:when>
												<xsl:when test="position() = 1">
													<xsl:value-of select="concat($type, ., ', ')" /></xsl:when>
												<xsl:when test="position() = last()">
													<xsl:value-of select="concat($type, .)" />
												</xsl:when>
												<xsl:otherwise><xsl:value-of select="concat($type, ., ', ')" /></xsl:otherwise>
											</xsl:choose>
											</xsl:for-each>
											<xsl:value-of select="$reltype" />
											<xsl:if test="lido:objectWebResource[not(.='')]">
												<xsl:value-of select="concat(' [', lido:objectWebResource[not(.='')][1], ']')" />
											</xsl:if>
										</xsl:for-each>
									</xsl:for-each>
								</dc:relation>
							</xsl:when>
						</xsl:choose>
					</xsl:for-each>

					<xsl:for-each select="lido:objectRelationWrap/lido:subjectWrap/lido:subjectSet">
						<xsl:choose>

							<xsl:when test="lido:subject">
								<xsl:for-each select="lido:subject">
									<xsl:variable name="extent"><xsl:value-of select="lido:extentSubject" /></xsl:variable>
									<xsl:choose>
									<xsl:when test="
										lido:subjectConcept[count(not(lido:term='')) &gt; 0]
									">
										<xsl:for-each select="
											lido:subjectConcept/lido:term[not(.='')][not(@lido:addedSearchTerm = 'yes')]
											">
											<dc:subject>
												<xsl:if test="@xml:lang">
													<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
												</xsl:if>
												<xsl:if test="not($extent='')">
													<xsl:value-of select="concat($extent, ': ')"/>
												</xsl:if>
												<xsl:value-of select="."/>
											</dc:subject>
										</xsl:for-each>
										<!-- usually ignoring addedSearchTerms / special handling Iconclass (to be checked) -->
										<xsl:if test="lido:subjectConcept/lido:conceptID[contains(@lido:source, 'Iconclass')]">
										<xsl:for-each select="
											lido:subjectConcept/lido:term[not(.='')][@lido:addedSearchTerm = 'yes'][1]
											">
											<dc:subject>
												<xsl:if test="@xml:lang">
													<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
												</xsl:if>
												<xsl:if test="not($extent='')">
													<xsl:value-of select="concat($extent, ': ')"/>
												</xsl:if>
												<xsl:value-of select="."/>
											</dc:subject>
										</xsl:for-each>
										</xsl:if>
									</xsl:when>
									<xsl:when test="
										lido:subjectActor/lido:actor
										| lido:subjectPlace/lido:place
									">
										<xsl:for-each select="
											lido:subjectActor/lido:actor/lido:nameActorSet/lido:appellationValue[not(@lido:pref = 'alternate')][not(.='')]
											| lido:subjectPlace/lido:place/lido:namePlaceSet/lido:appellationValue[not(@lido:pref = 'alternate')][not(.='')]
											">
											<!-- ignoring alternative names -->
											<dc:subject>
												<xsl:if test="@xml:lang">
													<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
												</xsl:if>
												<xsl:if test="not($extent='')">
													<xsl:value-of select="concat($extent, ': ')"/>
												</xsl:if>
												<xsl:value-of select="."/>
											</dc:subject>
										</xsl:for-each>
									</xsl:when>
									<xsl:when test="
										lido:subjectObject/lido:object
									">
										<xsl:for-each select="
											lido:subjectObject/lido:object/lido:objectNote[not(.='')]
											">
											<dc:subject>
												<xsl:if test="@xml:lang">
													<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
												</xsl:if>
												<xsl:if test="not($extent='')">
													<xsl:value-of select="concat($extent, ': ')"/>
												</xsl:if>
												<xsl:value-of select="."/>
											</dc:subject>
										</xsl:for-each>
									</xsl:when>
									<xsl:when test="
										lido:subjectDate/lido:date
									">
										<xsl:for-each select="lido:subjectDate/lido:date[not(lido:earliestDate='') and not(lido:latestDate='')]">
											<dc:subject>
												<xsl:if test="not($extent='')">
													<xsl:value-of select="concat($extent, ': ')"/>
												</xsl:if>
												<xsl:choose>
													<xsl:when test="lido:earliestDate = lido:latestDate">
														<xsl:value-of select="lido:earliestDate"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="concat(lido:earliestDate, '-', lido:latestDate)"/>
													</xsl:otherwise>
												</xsl:choose>
											</dc:subject>
										</xsl:for-each>
									</xsl:when>
									<xsl:when test="
										lido:subjectEvent/lido:event
									">
										<xsl:for-each select="lido:subjectEvent/lido:event[count(not(lido:eventName/lido:appellationValue='')) &gt; 0]">
											<dc:subject>
												<xsl:if test="@xml:lang">
													<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
												</xsl:if>
												<xsl:if test="not($extent='')">
													<xsl:value-of select="concat($extent, ': ')"/>
												</xsl:if>
												<xsl:value-of select="concat(lido:eventType/lido:term[1], ': ', lido:eventName/lido:appellationValue[1], ' (', lido:eventID, ')')"/>
											</dc:subject>
										</xsl:for-each>
									</xsl:when>
									<xsl:when test="
										lido:subjectActor/lido:displayActor
										| lido:subjectDate/lido:displayDate
										| lido:subjectEvent/lido:displayEvent
										| lido:subjectPlace/lido:displayPlace
										| lido:subjectObject/lido:displayObject
									">
										<xsl:for-each select="
											lido:subjectActor/lido:displayActor[not(.='')]
											| lido:subjectDate/lido:displayDate[not(.='')]
											| lido:subjectEvent/lido:displayEvent[not(.='')]
											| lido:subjectPlace/lido:displayPlace[not(.='')]
											| lido:subjectObject/lido:displayObject[not(.='')]
											">
											<dc:subject>
												<xsl:if test="@xml:lang">
													<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
												</xsl:if>
												<xsl:if test="not($extent='')">
													<xsl:value-of select="concat($extent, ': ')"/>
												</xsl:if>
												<xsl:value-of select="."/>
											</dc:subject>
										</xsl:for-each>
									</xsl:when>
									</xsl:choose>
								</xsl:for-each>
							</xsl:when>
							<xsl:when test="lido:displaySubject">
								<xsl:for-each select="lido:displaySubject[not(.='')]">
									<dc:subject>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="."/>
									</dc:subject>
								</xsl:for-each>
							</xsl:when>
						</xsl:choose>
					</xsl:for-each>
					
		</xsl:template>

	<xsl:template name="resourceView">
		<xsl:variable name="desclang">
			<xsl:choose>
				<xsl:when test="@xml:lang">
					<xsl:value-of select="@xml:lang" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="../../../lido:descriptiveMetadata/@xml:lang" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:for-each select="lido:resourceViewDescription[not(.='')]">
			<dc:description>
				<xsl:if test="@xml:lang">
					<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
				</xsl:if>
				<xsl:variable name="desctype">
					<xsl:choose>
						<xsl:when test="$desclang = 'deu' or $desclang = 'ger'">Fotoinhalt/Ansicht</xsl:when>
						<xsl:otherwise>Content/View Resource</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:value-of select="concat($desctype, ': ', .)" />
			</dc:description>
		</xsl:for-each>
		<xsl:for-each select="lido:resourceViewDate[not(.='')]">
			<dc:description>
				<xsl:if test="@xml:lang">
					<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
				</xsl:if>
				<xsl:variable name="desctype">
					<xsl:choose>
						<xsl:when test="$desclang = 'deu' or $desclang = 'ger'">Datierung des Fotos</xsl:when>
						<xsl:otherwise>Date Resource</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:value-of select="concat($desctype, ': ', .)" />
			</dc:description>
		</xsl:for-each>
		<xsl:for-each select="lido:resourceSource[not(.='')]">
			<xsl:if test="lower-case(@lido:type) = 'photographer' or contains(lower-case(@lido:type), 'fotograf')">
			<dc:description>
				<xsl:if test="@xml:lang">
					<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
				</xsl:if>
				<xsl:value-of select="concat(@lido:type, ': ', .)" />
			</dc:description>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="work">

		<xsl:choose>
			<xsl:when test="lido:rightsWorkWrap/lido:rightsWorkSet/lido:creditLine[not(.='')]">
				<xsl:for-each select="lido:rightsWorkWrap/lido:rightsWorkSet/lido:creditLine[not(.='')]">
					<!-- ignoring alternative names -->
					<dc:rights>
						<xsl:if test="@xml:lang">
							<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
						</xsl:if>
						<xsl:value-of select="."/>
					</dc:rights>
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="lido:rightsWorkWrap/lido:rightsWorkSet/lido:rightsType[not(.='')] and not(lido:rightsWorkWrap/lido:rightsWorkSet/lido:rightsHolder/lido:legalBodyName)">
				<xsl:for-each select="lido:rightsWorkWrap/lido:rightsWorkSet/lido:rightsType[not(.='')]">
					<dc:rights>
						<xsl:if test="@xml:lang">
							<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
						</xsl:if>
						<xsl:value-of select="."/>
					</dc:rights>
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="lido:rightsWorkWrap/lido:rightsWorkSet/lido:rightsHolder/lido:legalBodyName">
				<xsl:for-each select="lido:rightsWorkWrap/lido:rightsWorkSet/lido:rightsHolder/lido:legalBodyName[not(lido:appellationValue/@lido:pref = 'alternate')]">
						<dc:rights>
							<xsl:if test="lido:appellationValue/@xml:lang">
								<xsl:attribute name="xml:lang"><xsl:value-of select="lido:appellationValue/@xml:lang" /></xsl:attribute>
							</xsl:if>
							<xsl:if test="../../lido:rightsType[not(.='')]">
								<xsl:value-of select="concat(../../lido:rightsType[not(.='')][1], ': ')" />
							</xsl:if>
							<xsl:value-of select="lido:appellationValue"/>
						</dc:rights>
				</xsl:for-each>
			</xsl:when>
		</xsl:choose>

	</xsl:template>

	<xsl:template name="record">

		<xsl:for-each select="lido:recordWrap/lido:recordID[not(.='')]">
			<dc:identifier>
			   <xsl:value-of select="concat(@lido:type, ' ',. , ' [Metadata]')"/>						           
			</dc:identifier>
		</xsl:for-each>

		<xsl:for-each select="lido:recordWrap/lido:recordSource/lido:legalBodyName/lido:appellationValue[not(@lido:pref = 'alternate')]">
		<!-- ignoring alternative names -->
			<dc:source>
				<xsl:choose>
					<xsl:when test="contains(., 'Bildarchiv Foto Marburg')">Deutsches Dokumentationszentrum für Kunstgeschichte - Bildarchiv Foto Marburg</xsl:when>
					<xsl:when test="contains(../../../../../lido:lidoRecID, 'DE-Mb112')"><xsl:value-of select="concat(., ' [Metadata]')"/></xsl:when>
					<xsl:otherwise>
						<xsl:if test="@xml:lang">
							<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
						</xsl:if>
						<xsl:value-of select="."/>
					</xsl:otherwise>
				</xsl:choose>
			</dc:source>
		</xsl:for-each>

	</xsl:template>

	<xsl:template name="resource">

		<xsl:variable name="resourceSource">			
			<xsl:choose>
				<xsl:when test="contains(lido:resourceSource[@lido:type='Fotoverwalter'], 'Bildarchiv Foto Marburg')">info:isil/DE-Mb112</xsl:when>
				<xsl:when test="lido:resourceSource"><xsl:value-of select="../lido:resourceSource[1]" /></xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:for-each select="lido:resourceID[not(.='')]">
			<dc:identifier>
			   <xsl:value-of select="concat($resourceSource, ' - ', ., ' [Resource]')"/>						           
			</dc:identifier>
		</xsl:for-each>

		<xsl:choose>
			<xsl:when test="lido:rightsResource/lido:creditLine[not(.='')]">
				<xsl:for-each select="lido:rightsResource/lido:creditLine[not(.='')]">
					<dc:rights>
						<xsl:if test="@xml:lang">
							<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
						</xsl:if>
						<xsl:value-of select="concat(., ' [Resource]')"/>
					</dc:rights>
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="lido:rightsResource/lido:rightsType[not(.='')] and not(lido:rightsResource/lido:rightsHolder/lido:legalBodyName)">
				<xsl:for-each select="lido:rightsResource/lido:rightsType[not(.='')]">
					<dc:rights>
						<xsl:if test="@xml:lang">
							<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
						</xsl:if>
						<xsl:value-of select="concat(., ' [Resource]')"/>
					</dc:rights>
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="lido:rightsResource/lido:rightsHolder/lido:legalBodyName">
				<xsl:for-each select="lido:rightsResource/lido:rightsHolder/lido:legalBodyName[not(lido:appellationValue/@lido:pref = 'alternate')]">
						<dc:rights>
							<xsl:if test="lido:appellationValue/@xml:lang">
								<xsl:attribute name="xml:lang"><xsl:value-of select="lido:appellationValue/@xml:lang" /></xsl:attribute>
							</xsl:if>
							<xsl:if test="../../lido:rightsType[not(.='')]">
								<xsl:value-of select="../../lido:rightsType[not(.='')][1]" />
							</xsl:if>
							<xsl:value-of select="concat(lido:appellationValue, ' [Resource]')"/>
						</dc:rights>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="lido:resourceSource[not(.='')]">
					<dc:source>
						<xsl:if test="@xml:lang">
							<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
						</xsl:if>
						<xsl:if test="@lido:type"><xsl:value-of select="concat(@lido:type, ': ')" /></xsl:if>
						<xsl:value-of select="." />
					</dc:source>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>
	
</xsl:stylesheet>

