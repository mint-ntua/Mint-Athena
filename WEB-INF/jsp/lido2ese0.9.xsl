<?xml version="1.0" encoding="UTF-8"?>
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
			<xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="'http://www.europeana.eu/schemas/ese/ http://www.europeana.eu/schemas/ese/ESE-V3.2.xsd'"/>
			<xsl:for-each select="lido:lidoWrap/lido:lido">
				
				<xsl:choose>

					<xsl:when test="contains(lido:administrativeMetadata/lido:recordWrap/lido:recordType, '/multipleResources') 
						and 
						lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:linkResource[@lido:type='image_thumb']">

						<xsl:for-each select="lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:linkResource[@lido:type='image_thumb']">

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
					
					<xsl:for-each select="../../../..//lido:term[@lido:addedSearchTerm = 'yes']
						| ../../../..//lido:appellationValue[@lido:pref = 'alternate']
						| ../../../..//lido:partOfPlace//lido:appellationValue
						| ../../../..//lido:placeClassification/lido:term
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
						<xsl:value-of select="../../../../lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification[@lido:type = 'europeana:type']/lido:term[position() = 1]" />
					</europeana:type>

					<xsl:for-each select="../../../lido:recordWrap/lido:recordInfoSet/lido:recordInfoLink">
						<xsl:if test="position() = 1">
							<europeana:isShownAt>
								<xsl:value-of select="." />
							</europeana:isShownAt>
						</xsl:if>
					</xsl:for-each>

					<europeana:isShownBy>
						<xsl:value-of select="." />
					</europeana:isShownBy>

				</europeana:record>
								</xsl:for-each>
							</xsl:when>

							<!-- multipleResources: create ESE record for EACH resource -->
							<xsl:when test="contains(lido:administrativeMetadata/lido:recordWrap/lido:recordType, '/multipleResources')">
								<xsl:for-each select="lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet">
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
					
					<xsl:for-each select="../../..//lido:term[@lido:addedSearchTerm = 'yes']
						| ../../..//lido:appellationValue[@lido:pref = 'alternate']
						| ../../..//lido:partOfPlace//lido:appellationValue
						| ../../..//lido:placeClassification/lido:term
						">
							<europeana:unstored>
								<xsl:value-of select="." />
							</europeana:unstored>
					</xsl:for-each>

<!-- Europeana elements in requested order --> 
						<xsl:for-each select="lido:linkResource">
							<xsl:if test="position() = 1">
								<europeana:object>
									<xsl:value-of select="." />
								</europeana:object>
							</xsl:if>
						</xsl:for-each>

					<europeana:provider>ATHENA project</europeana:provider>

					<europeana:type>
						<xsl:value-of select="../../../lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification[@lido:type = 'europeana:type']/lido:term" />
					</europeana:type>

					<xsl:for-each select="../..//lido:recordWrap/lido:recordInfoSet/lido:recordInfoLink">
						<xsl:if test="position() = 1">
							<europeana:isShownAt>
								<xsl:value-of select="." />
							</europeana:isShownAt>
						</xsl:if>
					</xsl:for-each>

					<xsl:for-each select="lido:linkResource">
						<xsl:if test="position() = 1">
							<europeana:isShownBy>
								<xsl:value-of select="." />
							</europeana:isShownBy>
						</xsl:if>
					</xsl:for-each>

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
						<xsl:for-each select="lido:resourceWrap/lido:resourceSet">
							<xsl:if test="position() = 1">
								<xsl:call-template name="resource" />
								<xsl:call-template name="resourceView" />
							</xsl:if>
						</xsl:for-each>
					</xsl:for-each>
					
					<xsl:for-each select=".//lido:term[@lido:addedSearchTerm = 'yes']
						| .//lido:appellationValue[@lido:pref = 'alternate']
						| .//lido:partOfPlace//lido:appellationValue
						| .//lido:placeClassification/lido:term
						">
							<europeana:unstored>
								<xsl:value-of select="." />
							</europeana:unstored>
					</xsl:for-each>

<!-- Europeana elements in requested order --> 

						<xsl:for-each select="lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:linkResource[@lido:type = 'image_thumb']">
							<xsl:if test="position() = 1">
								<europeana:object>
									<xsl:value-of select="." />
								</europeana:object>
							</xsl:if>
						</xsl:for-each>

					<europeana:provider>ATHENA project</europeana:provider>

					<europeana:type>
						<xsl:value-of select="lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification[@lido:type = 'europeana:type']/lido:term" />
					</europeana:type>

					<xsl:for-each select="lido:administrativeMetadata/lido:recordWrap/lido:recordInfoSet/lido:recordInfoLink">
						<xsl:if test="position() = 1">
							<europeana:isShownAt>
								<xsl:value-of select="." />
							</europeana:isShownAt>
						</xsl:if>
					</xsl:for-each>

					<xsl:for-each select="lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:linkResource">
						<xsl:if test="position() = 1">
							<europeana:isShownBy>
								<xsl:value-of select="." />
							</europeana:isShownBy>
						</xsl:if>
					</xsl:for-each>

				</europeana:record>					
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</europeana:metadata>
	</xsl:template>
	
	<xsl:template name="descriptiveMetadata">

					<xsl:for-each select="lido:objectIdentificationWrap/lido:titleWrap/lido:titleSet/lido:appellationValue">
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

					<xsl:for-each select="lido:objectClassificationWrap/lido:objectWorkTypeWrap/lido:objectWorkType/lido:term[not(@lido:addedSearchTerm = 'yes')] 
						">
						<dc:type>
								<xsl:if test="@xml:lang">
									<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
								</xsl:if>
							<xsl:value-of select="."/>
						</dc:type>
					</xsl:for-each>

					<xsl:for-each select="lido:objectClassificationWrap/lido:classificationWrap/lido:classification[not(@lido:type = 'europeana:type')]/lido:term[not(@lido:addedSearchTerm = 'yes')] 
						">
						<xsl:choose>
							<xsl:when test="lower-case(../@lido:type) = 'colour'
								or lower-case(../@lido:type) = 'age'
								or lower-case(../@lido:type) = 'object-status'
								">
								<dc:description>
									<xsl:if test="@xml:lang">
										<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
									</xsl:if>
									<xsl:value-of select="../@lido:type" />: <xsl:value-of select="."/>
								</dc:description>
							</xsl:when>
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

					<xsl:for-each select="lido:objectIdentificationWrap/lido:inscriptionsWrap/lido:inscriptions">
						<dc:description>
							<xsl:if test="@xml:lang">
								<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
							</xsl:if>
							<xsl:value-of select="concat(@lido:type, ': ', .)" />
						</dc:description>
					</xsl:for-each>

					<xsl:for-each select="lido:objectIdentificationWrap/lido:repositoryWrap/lido:repositorySet/lido:workID">
						<xsl:variable name="ID"><xsl:value-of select="." /></xsl:variable>
						<xsl:variable name="legalBody">			
							<xsl:choose>
								<xsl:when test="../lido:repositoryName/lido:legalBodyID"><xsl:value-of select="../lido:repositoryName/lido:legalBodyID[1]" /></xsl:when>
								<xsl:otherwise><xsl:value-of select="../lido:repositoryName/lido:legalBodyName/lido:appellationValue[1]" /></xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<dc:identifier>
						   <xsl:value-of select="concat($legalBody, ' - ', @lido:type, ' ',$ID)"/>						           
						</dc:identifier>
					</xsl:for-each>

					<xsl:for-each select="lido:objectIdentificationWrap/lido:displayStateEditionWrap/*">
						<dc:description>
							<xsl:if test="@xml:lang">
								<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
							</xsl:if>
							<xsl:value-of select="concat(substring-after(name(), 'display'), ': ', .)" />
						</dc:description>
					</xsl:for-each>

					<xsl:for-each select="lido:objectIdentificationWrap/lido:objectDescriptionWrap/lido:objectDescriptionSet/lido:descriptiveNoteValue">
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

						<xsl:for-each select="lido:objectIdentificationWrap/lido:objectMeasurementsWrap/lido:objectMeasurementsSet">
							<xsl:choose>
								<xsl:when test="lido:objectMeasurements">
									<xsl:for-each select="lido:objectMeasurements/lido:measurementsSet">
									<dcterms:extent>
										<xsl:value-of select="concat(@lido:type, ': ', @lido:value, ' ', @lido:unit)"/>
										<xsl:for-each select="../lido:extentMeasurements"><xsl:value-of select="concat(' (', ., ')')" /></xsl:for-each>
									</dcterms:extent>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:displayObjectMeasurements">
									<xsl:for-each select="lido:displayObjectMeasurements">
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

					<xsl:for-each select="lido:eventWrap/lido:eventSet/lido:event">
					
						<xsl:variable name="eventType" select="lido:eventType/lido:term[1]"/>
						<xsl:variable name="eventTypeLC" select="lower-case($eventType)"/>
						<xsl:variable name="creation" as="xs:boolean*">
							<xsl:if test="$eventType = 'creation' 
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
									<xsl:for-each select="lido:actorInRole/lido:actor/lido:nameActorSet/lido:appellationValue">
									<!-- ignoring alternative names -->
									<xsl:if test="not(@lido:pref = 'alternate')">
									<dc:creator>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang">
												<xsl:value-of select="@xml:lang" />
											</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="."/>
										<xsl:for-each select="../../../lido:roleActor/lido:term[not(@lido:addedSearchTerm = 'yes')]">
											<xsl:choose>
												<xsl:when test="count(.) = 1 and count(../../lido:roleActor) = 1"> (<xsl:value-of select="." />)</xsl:when>
												<xsl:when test="position() = 1 and ../../lido:roleActor[position() = 1]"> (<xsl:value-of select="." />, </xsl:when>
												<xsl:when test="position() = last() and ../../lido:roleActor[position() = last()]"><xsl:value-of select="." />)</xsl:when>
												<xsl:otherwise><xsl:value-of select="." />, </xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
										<xsl:if test="not(../../../lido:roleActor/lido:term[not(@lido:addedSearchTerm = 'yes')])"> [<xsl:value-of select="$eventType" />]</xsl:if>
									</dc:creator>
									</xsl:if>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="$creation and lido:displayActorInRole">
									<xsl:for-each select="lido:displayActorInRole">
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
									<xsl:for-each select="lido:actorInRole/lido:actor/lido:nameActorSet/lido:appellationValue">
									<!-- ignoring alternative names -->
									<xsl:if test="not(@lido:pref = 'alternate')">
									<dc:contributor>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang">
												<xsl:value-of select="@xml:lang" />
											</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="."/>
										<xsl:for-each select="../../../lido:roleActor/lido:term[not(@lido:addedSearchTerm = 'yes')]">
											<xsl:choose>
												<xsl:when test="count(.) = 1 and count(../../lido:roleActor) = 1"> (<xsl:value-of select="." />)</xsl:when>
												<xsl:when test="position() = 1 and ../../lido:roleActor[position() = 1]"> (<xsl:value-of select="." />, </xsl:when>
												<xsl:when test="position() = last() and ../../lido:roleActor[position() = last()]"><xsl:value-of select="." />)</xsl:when>
												<xsl:otherwise><xsl:value-of select="." />, </xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
										<xsl:if test="not(../../../lido:roleActor/lido:term[not(@lido:addedSearchTerm = 'yes')])"> [<xsl:value-of select="$eventType" />]</xsl:if>
									</dc:contributor>
									</xsl:if>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:displayActorInRole">
									<xsl:for-each select="lido:displayActorInRole">
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

						<xsl:for-each select="lido:culture/lido:term[not(@lido:addedSearchTerm = 'yes')]">
							<xsl:choose>
								<xsl:when test="$creation">
									<dc:creator>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="concat(., ' [', $eventType, ']')"/>
									</dc:creator>
								</xsl:when>
								<xsl:otherwise>
									<dc:contributor>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="concat(., ' [', $eventType, ']')"/>
									</dc:contributor>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>

						<xsl:for-each select="lido:eventMethod/lido:term[not(@lido:addedSearchTerm = 'yes')]">
							<dc:description>
								<xsl:if test="@xml:lang">
									<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
								</xsl:if>

								<xsl:value-of select="concat('Method: ', .)"/>
							</dc:description>
						</xsl:for-each>

						<xsl:for-each select="lido:eventMaterialsTech">
							<xsl:choose>
								<xsl:when test="lido:materialsTech">
									<xsl:for-each select="lido:materialsTech/lido:termMaterialsTech/lido:term[not(@lido:addedSearchTerm = 'yes')]">
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
									<dcterms:medium>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="."/>
									</dcterms:medium>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>
					
						<xsl:for-each select="lido:periodName/lido:term[not(@lido:addedSearchTerm = 'yes')]">
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
									<xsl:for-each select="lido:date">
									<dcterms:created>
										<xsl:choose>
											<xsl:when test="lido:earliestDate = lido:latestDate">
												<xsl:value-of select="concat(lido:earliestDate, ' [', $eventType, ']')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="concat(lido:earliestDate, '-', lido:latestDate, ' [', $eventType, ']')"/>
											</xsl:otherwise>
										</xsl:choose>
									</dcterms:created>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="$creation and lido:displayDate">
									<xsl:for-each select="lido:displayDate">
									<dcterms:created>
										<xsl:if test="@xml:lang">
											<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
										</xsl:if>
										<xsl:value-of select="concat(., ' [', $eventType, ']')"/>
									</dcterms:created>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:date">
									<xsl:for-each select="lido:date">
									<dc:date>
										<xsl:choose>
											<xsl:when test="lido:earliestDate = lido:latestDate">
												<xsl:value-of select="concat(lido:earliestDate, ' [', $eventType, ']')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="concat(lido:earliestDate, '-', lido:latestDate, ' [', $eventType, ']')"/>
											</xsl:otherwise>
										</xsl:choose>
									</dc:date>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:displayDate">
									<xsl:for-each select="lido:displayDate">
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
							<xsl:choose>
								<xsl:when test="lido:place">
									<xsl:for-each select="lido:place/lido:namePlaceSet/lido:appellationValue">
									<!-- ignoring alternative names -->
									<xsl:if test="not(@lido:pref = 'alternate')">
										<dcterms:spatial>
											<xsl:if test="@xml:lang">
												<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
											</xsl:if>
											<xsl:value-of select="concat(., ' [', $eventType, ']')"/>
										</dcterms:spatial>
									</xsl:if>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="lido:displayPlace">
									<xsl:for-each select="lido:displayPlace">
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

					<xsl:for-each select="lido:objectRelationWrap/lido:relatedWorksWrap/lido:relatedWorksSet/lido:relatedWork/lido:object">
						<xsl:choose>
							<xsl:when test="../../lido:relatedWorkRelType/lido:term ='part of'
								or ../../lido:relatedWorkRelType/lido:term ='Teil von'
								">
								<dcterms:isPartOf>
									<xsl:value-of select="concat(lido:objectNote[1], ' [', lido:objectWebResource[1], ']')" />
								</dcterms:isPartOf>
							</xsl:when>
							<xsl:when test="../../lido:relatedWorkRelType ='has part'
								or ../../lido:relatedWorkRelType ='hat Teil'
								">
								<dcterms:hasPart>
									<xsl:value-of select="concat(lido:objectNote[1], ' [', lido:objectWebResource[1], ']')" />
								</dcterms:hasPart>
							</xsl:when>
							<xsl:otherwise>
								<dc:relation>
									<xsl:value-of select="concat(../../lido:relatedWorkRelType/lido:term[1], ': ', lido:objectNote[1], ' [', lido:objectWebResource[1], ']')" />
								</dc:relation>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>

					<xsl:for-each select="lido:objectRelationWrap/lido:subjectWrap/lido:subjectSet">
						<xsl:choose>

							<xsl:when test="lido:subject">
								<xsl:for-each select="lido:subject">
									<xsl:variable name="extent"><xsl:value-of select="lido:extentSubject" /></xsl:variable>
									<xsl:choose>
									<xsl:when test="
										lido:subjectConcept
									">
										<xsl:for-each select="
											lido:subjectConcept/lido:term[not(@lido:addedSearchTerm = 'yes')]
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
											lido:subjectConcept/lido:term[@lido:addedSearchTerm = 'yes']
											">
											<xsl:if test="position() = 1">
											<dc:subject>
												<xsl:if test="@xml:lang">
													<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
												</xsl:if>
												<xsl:if test="not($extent='')">
													<xsl:value-of select="concat($extent, ': ')"/>
												</xsl:if>
												<xsl:value-of select="."/>
											</dc:subject>
											</xsl:if>
										</xsl:for-each>
										</xsl:if>
									</xsl:when>
									<xsl:when test="
										lido:subjectActor/lido:actor
										| lido:subjectPlace/lido:place
									">
										<xsl:for-each select="
											lido:subjectActor/lido:actor/lido:nameActorSet/lido:appellationValue
											| lido:subjectPlace/lido:place/lido:namePlaceSet/lido:appellationValue
											">
											<!-- ignoring alternative names -->
											<xsl:if test="not(@lido:pref = 'alternate')">
											<dc:subject>
												<xsl:if test="@xml:lang">
													<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
												</xsl:if>
												<xsl:if test="not($extent='')">
													<xsl:value-of select="concat($extent, ': ')"/>
												</xsl:if>
												<xsl:value-of select="."/>
											</dc:subject>
											</xsl:if>
										</xsl:for-each>
									</xsl:when>
									<xsl:when test="
										lido:subjectObject/lido:object
									">
										<xsl:for-each select="
											lido:subjectObject/lido:object/lido:objectNote
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
										<xsl:for-each select="lido:subjectDate/lido:date">
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
										<xsl:for-each select="lido:subjectEvent/lido:event">
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
											lido:subjectActor/lido:displayActor
											| lido:subjectDate/lido:displayDate
											| lido:subjectEvent/lido:displayEvent
											| lido:subjectPlace/lido:displayPlace
											| lido:subjectObject/lido:displayObject
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
								<xsl:for-each select="lido:displaySubject">
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
		<xsl:for-each select="lido:resourceViewDescription">
			<dc:description>
				<xsl:if test="@xml:lang">
					<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
				</xsl:if>
				<xsl:variable name="desclang">
					<xsl:choose>
						<xsl:when test="@xml:lang">
							<xsl:value-of select="@xml:lang" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="../../../../lido:descriptiveMetadata/@xml:lang" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="desctype">
					<xsl:choose>
						<xsl:when test="$desclang = 'deu' or $desclang = 'ger'">Fotoinhalt / Ansicht</xsl:when>
						<xsl:otherwise>Resource View</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:value-of select="concat($desctype, ': ', .)" />
			</dc:description>
		</xsl:for-each>
		<xsl:for-each select="lido:resourceViewDate">
			<dc:description>
				<xsl:if test="@xml:lang">
					<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
				</xsl:if>
				<xsl:variable name="desclang">
					<xsl:choose>
						<xsl:when test="@xml:lang">
							<xsl:value-of select="@xml:lang" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="../../../../lido:descriptiveMetadata/@xml:lang" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="desctype">
					<xsl:choose>
						<xsl:when test="$desclang = 'deu' or $desclang = 'ger'">Datierung des Fotos</xsl:when>
						<xsl:otherwise>Resource View Date</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:value-of select="concat($desctype, ': ', .)" />
			</dc:description>
		</xsl:for-each>
		<xsl:for-each select="lido:resourceSource">
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

		<xsl:for-each select="../lido:lidoRecID">
			<dc:identifier>
				<xsl:value-of select="concat(@lido:type, ' ', ., ' [LIDO metadata]')" />
			</dc:identifier>
		</xsl:for-each>					

		<xsl:choose>
			<xsl:when test="lido:rightsWorkWrap/lido:rightsWorkSet/lido:creditLine">
				<xsl:for-each select="lido:rightsWorkWrap/lido:rightsWorkSet/lido:creditLine">
					<!-- ignoring alternative names -->
					<dc:rights>
						<xsl:if test="lido:appellationValue/@xml:lang">
							<xsl:attribute name="xml:lang"><xsl:value-of select="lido:appellationValue/@xml:lang" /></xsl:attribute>
						</xsl:if>
						<xsl:value-of select="lido:appellationValue"/>
					</dc:rights>
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="lido:rightsWorkWrap/lido:rightsWorkSet/lido:rightsHolder/lido:legalBodyName">
				<xsl:for-each select="lido:rightsWorkWrap/lido:rightsWorkSet/lido:rightsHolder/lido:legalBodyName">
					<!-- ignoring alternative names -->
					<xsl:if test="not(lido:appellationValue/@lido:pref = 'alternate')">
						<dc:rights>
							<xsl:if test="lido:appellationValue/@xml:lang">
								<xsl:attribute name="xml:lang"><xsl:value-of select="lido:appellationValue/@xml:lang" /></xsl:attribute>
							</xsl:if>
							<xsl:value-of select="lido:appellationValue"/>
						</dc:rights>
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="../lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:repositoryWrap/lido:repositorySet/lido:repositoryName/lido:legalBodyName/lido:appellationValue">
				<!-- ignoring alternative names -->
					<xsl:if test="not(@lido:pref = 'alternate')">
						<dc:source>
							<xsl:if test="@xml:lang">
								<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
							</xsl:if>
							<xsl:value-of select="concat(., ' [Repository]')"/>
						</dc:source>
					</xsl:if>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template name="record">

		<xsl:for-each select="lido:recordWrap/lido:recordID">
			<xsl:variable name="ID"><xsl:value-of select="." /></xsl:variable>
			<xsl:variable name="legalBody">			
				<xsl:choose>
					<xsl:when test="../lido:recordSource/lido:legalBodyID"><xsl:value-of select="../lido:recordSource/lido:legalBodyID[1]" /></xsl:when>
					<xsl:otherwise><xsl:value-of select="../lido:recordSource/lido:legalBodyName/lido:appellationValue[1]" /></xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<dc:identifier>
			   <xsl:value-of select="concat($legalBody, ' - ', @lido:type, ' ',$ID, ' [Metadata]')"/>						           
			</dc:identifier>
		</xsl:for-each>

		<xsl:choose>
			<xsl:when test="lido:recordWrap/lido:recordRights/lido:creditLine">
				<xsl:for-each select="lido:rightsWorkWrap/lido:rightsWorkSet/lido:creditLine">
					<dc:rights>
						<xsl:if test="@xml:lang">
							<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
						</xsl:if>
						<xsl:value-of select="concat(., ' [Metadata]')"/>
					</dc:rights>
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="lido:recordWrap/lido:recordRights/lido:rightsHolder/lido:legalBodyName">
				<xsl:for-each select="lido:recordWrap/lido:recordRights/lido:rightsHolder/lido:legalBodyName">
					<!-- ignoring alternative names -->
					<xsl:if test="not(lido:appellationValue/@lido:pref = 'alternate')">
						<dc:rights>
							<xsl:if test="lido:appellationValue/@xml:lang">
								<xsl:attribute name="xml:lang"><xsl:value-of select="lido:appellationValue/@xml:lang" /></xsl:attribute>
							</xsl:if>
							<xsl:value-of select="concat(lido:appellationValue, ' [Metadata]')"/>
						</dc:rights>
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="lido:recordWrap/lido:recordSource/lido:legalBodyName/lido:appellationValue">
				<!-- ignoring alternative names -->
					<xsl:if test="not(@lido:pref = 'alternate')">
						<dc:source>
							<xsl:if test="@xml:lang">
								<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
							</xsl:if>
							<xsl:value-of select="concat(., ' [Metadata]')"/>
						</dc:source>
					</xsl:if>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template name="resource">

		<xsl:for-each select="lido:resourceID">
			<xsl:variable name="ID"><xsl:value-of select="." /></xsl:variable>
			<xsl:variable name="legalBody">			
				<xsl:choose>
					<xsl:when test="../lido:resourceSource[@lido:type='Fotoverwalter']"><xsl:value-of select="../lido:resourceSource[@lido:type='Fotoverwalter']" /></xsl:when>
					<xsl:when test="../lido:resourceSource"><xsl:value-of select="../lido:resourceSource[1]" /></xsl:when>
				</xsl:choose>
			</xsl:variable>
			<dc:identifier>
			   <xsl:value-of select="concat($legalBody, ' - ', @lido:type, ' ',$ID, ' [Resource]')"/>						           
			</dc:identifier>
		</xsl:for-each>

		<xsl:choose>
			<xsl:when test="lido:rightsResource/lido:creditLine">
				<xsl:for-each select="lido:rightsResource/lido:creditLine">
					<dc:rights>
						<xsl:if test="@xml:lang">
							<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
						</xsl:if>
						<xsl:value-of select="concat(., ' [Resource]')"/>
					</dc:rights>
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="lido:rightsResource/lido:rightsHolder/lido:legalBodyName">
				<xsl:for-each select="lido:rightsResource/lido:rightsHolder/lido:legalBodyName">
					<!-- ignoring alternative names -->
					<xsl:if test="not(lido:appellationValue/@lido:pref = 'alternate')">
						<dc:rights>
							<xsl:if test="lido:appellationValue/@xml:lang">
								<xsl:attribute name="xml:lang"><xsl:value-of select="lido:appellationValue/@xml:lang" /></xsl:attribute>
							</xsl:if>
							<xsl:value-of select="concat(lido:appellationValue, ' [Resource]')"/>
						</dc:rights>
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="lido:resourceSource">
					<dc:source>
						<xsl:if test="@xml:lang">
							<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
						</xsl:if>
						<xsl:if test="@lido:type"><xsl:value-of select="concat(@lido:type, ': ')" /></xsl:if>
						<xsl:value-of select="." />
						<xsl:value-of select="' [Resource]'" />
					</dc:source>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>
	
</xsl:stylesheet>