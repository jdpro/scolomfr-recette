<?xml version="1.0"?>
<!--
    Transformation d'une notice ScoLOMFR v2.0 en ScoLOMFR v2.0 strict
    dont les valeurs ne contiennent que des identifiants Uri sémantiques

    Pierre Dittgen, PASS-TECH

    Cette feuille de transformation XSLT est placée sous licence Creative
       commons Attribution Partage à l'Identique 3.0 France
    © 2015 Direction du Numérique pour l’Éducation - Ministère de l'Éducation
       nationale, de l'enseignement supérieur et de la Recherche / Réseau Canopé
-->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lom="http://ltsc.ieee.org/xsd/LOM"
    xmlns:lomfr="http://www.lom-fr.fr/xsd/LOMFR"
    xmlns:scolomfr="http://www.lom-fr.fr/xsd/SCOLOMFR"
>

<!-- Sortie -->
<xsl:output method="xml" indent="yes" encoding="UTF-8"
     cdata-section-elements="lom:entity" />

<!-- Chemin des fichiers vocabulaires -->
<xsl:param name="vocpath" />

<!-- Identifiant URI de la langue française, utilisé plusieurs fois
     dans cette feuille de style -->
<xsl:variable name="FR_LANG_URI">http://id.loc.gov/vocabulary/iso639-2/fre</xsl:variable> 

<!-- Affichage des source values -->
<xsl:template match="concept" mode="sourcevaluelabel">

    <!-- Le préfixe de l'élément englobant est conservé
         pour les balises source, value et label -->
    <xsl:param name="prefix" />
    <xsl:param name="nsuri" />

    <xsl:element name="{$prefix}source" namespace="{$nsuri}">
        <xsl:text>SCOLOMFRv3.0</xsl:text>
    </xsl:element>
    <xsl:element name="{$prefix}value" namespace="{$nsuri}">
        <xsl:value-of select="@id" />
    </xsl:element>
    <xsl:element name="{$prefix}label" namespace="{$nsuri}">
        <xsl:value-of select="@prefTerm" />
    </xsl:element>
</xsl:template>

<!-- template générique pour le traitement d'un élément
     de type source/value -->
<xsl:template match="node()" mode="migratesourcevalue">
    <xsl:param name="vocid" />
    <xsl:param name="eltdesc" />

    <xsl:variable name="nsuri" select="namespace-uri(.)" />
    <xsl:variable name="val" select="*[local-name()='value']" />
    <xsl:variable name="withprefix" select="contains(name(),':')" />
    <xsl:variable name="voc_doc_path" select="concat($vocpath,'/scolomfr_pt_voc_',$vocid,'.xml')" />
    <xsl:variable name="concepts" select="document($voc_doc_path)//concept[value/text()=$val]" />

    <xsl:copy>
        <xsl:choose>
            <xsl:when test="count($concepts) = 0">
                <xsl:call-template name="err">
                    <xsl:with-param name="errmsg"><xsl:value-of select="$eltdesc" /> : valeur "<xsl:value-of select="$val"/>" non trouvée dans le vocabulaire scolomfr-voc-<xsl:value-of select="$vocid" />. Élément inchangé.</xsl:with-param>
                </xsl:call-template>
                <xsl:apply-templates />
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="nbconcepts" select="count($concepts)" />
                <xsl:if test="$nbconcepts &gt; 1">
                    <xsl:call-template name="err">
                        <xsl:with-param name="errmsg"><xsl:value-of select="$eltdesc" /> : la valeur "<xsl:value-of select="$val"/>" correspond à <xsl:value-of select="$nbconcepts" /> concepts dans le vocabulaire scolomfr-voc-<xsl:value-of select="$vocid" />.</xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
                <xsl:apply-templates select="$concepts" mode="sourcevaluelabel">
                    <xsl:with-param name="nsuri"><xsl:value-of select="$nsuri"/></xsl:with-param>
                    <xsl:with-param name="prefix">
                        <xsl:choose>
                            <xsl:when test="$withprefix">
                                <xsl:choose>
                                    <xsl:when test="$nsuri='http://ltsc.ieee.org/xsd/LOM'">lom:</xsl:when>
                                    <xsl:when test="$nsuri='http://www.lom-fr.fr/xsd/LOMFR'">lomfr:</xsl:when>
                                    <xsl:when test="$nsuri='http://www.lom-fr.fr/xsd/SCOLOMFR'">scolomfr:</xsl:when>
                                    <xsl:otherwise />
                                </xsl:choose>
                            </xsl:when>
                            <xsl:otherwise />
                        </xsl:choose>
                    </xsl:with-param>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:copy>
</xsl:template>

<!-- Template générique pour les éléments qui ne sont composés que d'une seule
     balise : <language> et <format> -->
<xsl:template match="node()" mode="migratesingleelt">
    <xsl:param name="vocid" />
    <xsl:param name="eltdesc" />

    <xsl:variable name="val" select="." />
    <xsl:variable name="voc_doc_path" select="concat($vocpath,'/scolomfr_pt_voc_',$vocid,'.xml')" />
    <xsl:variable name="concepts" select="document($voc_doc_path)//concept[value/text()=$val]" />

    <xsl:choose>
        <xsl:when test="count($concepts) = 0">
            <xsl:call-template name="err">
                <xsl:with-param name="errmsg"><xsl:value-of select="$eltdesc" /> : valeur "<xsl:value-of select="$val"/>" non trouvée dans le vocabulaire scolomfr-voc-<xsl:value-of select="$vocid" />. Élément inchangé.</xsl:with-param>
            </xsl:call-template>
            <xsl:element name="{name()}" namespace="{namespace-uri()}">
                <xsl:copy-of select="@*" />
                <xsl:apply-templates />
            </xsl:element>
        </xsl:when>
        <xsl:otherwise>
            <xsl:if test="count($concepts) &gt; 1">
                <xsl:call-template name="err">
                    <xsl:with-param name="errmsg"><xsl:value-of select="$eltdesc" /> : la valeur "<xsl:value-of select="$val"/>" correspond à plusieurs concepts dans le vocabulaire scolomfr-voc-<xsl:value-of select="$vocid" />.</xsl:with-param>
                </xsl:call-template>
            </xsl:if>
            <xsl:variable name="orignode" select="." />
            <xsl:for-each select="$concepts">
                <xsl:element name="{name($orignode)}" namespace="{namespace-uri($orignode)}">
                    <xsl:value-of select="@id" />
                </xsl:element>
            </xsl:for-each>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- Template pour les éléments de type LANGSTRING -->
<xsl:template match="lom:string[@language]">

    <xsl:variable name="val" select="@language" />
    <xsl:variable name="voc_doc_path" select="concat($vocpath,'/scolomfr_pt_voc_001.xml')" />
    <xsl:variable name="concepts" select="document($voc_doc_path)//concept[value/text()=$val]" />

    <xsl:choose>
        <xsl:when test="count($concepts) = 0">
            <xsl:call-template name="err">
                <xsl:with-param name="errmsg">Élément de type LANGSTRING : code langue "<xsl:value-of select="$val"/>" non trouvé dans le vocabulaire scolomfr-voc-001 />. Élément inchangé.</xsl:with-param>
            </xsl:call-template>
            <xsl:copy>
                <xsl:copy-of select="@*" />
                <xsl:apply-templates />
            </xsl:copy>
        </xsl:when>
        <xsl:otherwise>
            <xsl:if test="count($concepts) &gt; 1">
                <xsl:call-template name="err">
                    <xsl:with-param name="errmsg">Élément de type LANGSTRING : le code langue "<xsl:value-of select="$val"/>" correspond à plusieurs langues dans le vocabulaire scolomfr-voc-001.</xsl:with-param>
                </xsl:call-template>
            </xsl:if>
            <xsl:variable name="orignode" select="." />
            <xsl:variable name="prefix">
                <xsl:if test="name($orignode) != local-name($orignode)">
                    <xsl:text>lom:</xsl:text>
                </xsl:if>
            </xsl:variable>
            <xsl:for-each select="$concepts">
                <xsl:element name="{$prefix}string" namespace="http://ltsc.ieee.org/xsd/LOM">
                    <xsl:attribute name="language"><xsl:value-of select="@id"/></xsl:attribute>
                    <xsl:value-of select="$orignode/text()" />
                </xsl:element>
            </xsl:for-each>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- 1.3 langue (voc001) -->
<xsl:template match="lom:language[parent::lom:general]">
    <xsl:apply-templates select="." mode="migratesingleelt">
        <xsl:with-param name="vocid">001</xsl:with-param>
        <xsl:with-param name="eltdesc">1.3 langue</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 1.7 Structure (voc007) -->
<xsl:template match="lom:structure">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">007</xsl:with-param>
        <xsl:with-param name="eltdesc">1.7 Structure</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 1.8 Niveau d'agrégation (voc008) -->
<xsl:template match="lom:aggregationLevel">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">008</xsl:with-param>
        <xsl:with-param name="eltdesc">1.8 Niveau d'aggregation</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 1.9 Type documentaire DC (voc004) -->
<xsl:template match="lomfr:documentType">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">004</xsl:with-param>
        <xsl:with-param name="eltdesc">1.9 Type documentaire DC</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 1.10 Typologie générale de documents (voc005) -->
<xsl:template match="scolomfr:generalResourceType">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">005</xsl:with-param>
        <xsl:with-param name="eltdesc">1.10 Typologie générale de documents</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 2.2 État (voc002) -->
<xsl:template match="lom:status">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">002</xsl:with-param>
        <xsl:with-param name="eltdesc">2.2 État</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 2.3.1 Rôle (contributeur) (voc003) -->
<xsl:template match="lom:role[ancestor::lom:lifeCycle]">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">003</xsl:with-param>
        <xsl:with-param name="eltdesc">2.3.1 Rôle (contributeur)</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 3.2.1 Rôle (métadonnées) (voc013) -->
<xsl:template match="lom:role[ancestor::lom:metaMetadata]">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">013</xsl:with-param>
        <xsl:with-param name="eltdesc">3.2.1 Rôle (métadonnées)</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 3.4 Langue des métadonnées (voc001) -->
<xsl:template match="lom:language[parent::lom:metaMetadata]">
    <xsl:apply-templates select="." mode="migratesingleelt">
        <xsl:with-param name="vocid">001</xsl:with-param>
        <xsl:with-param name="eltdesc">3.4 Langue des métadonnées</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 4.1 Format (technique) (voc006) -->
<xsl:template match="lom:format">
    <xsl:apply-templates select="." mode="migratesingleelt">
        <xsl:with-param name="vocid">006</xsl:with-param>
        <xsl:with-param name="eltdesc">4.1 Format (technique)</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 4.3.1.3 Type de gestion des données à caractère personnel (voc044) -->
<xsl:template match="scolomfr:personalDataProcessType">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">044</xsl:with-param>
        <xsl:with-param name="eltdesc">4.3.1.3 Type de gestion des données à caractère personnel</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 4.4.1.1 Type (technique) (voc023) -->
<xsl:template match="lom:type[ancestor::lom:technical]">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">023</xsl:with-param>
        <xsl:with-param name="eltdesc">4.4.1.1 Type (technique)</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 4.4.1.2 Nom (technique) (voc024) -->
<xsl:template match="lom:name[ancestor::lom:technical]">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">024</xsl:with-param>
        <xsl:with-param name="eltdesc">4.4.1.2 Nom (technique)</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 5.2 Type pédagogique de la ressource (voc010) -->
<xsl:template match="lom:learningResourceType">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">010</xsl:with-param>
        <xsl:with-param name="eltdesc">5.2 Type pédagogique de la ressource</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 5.5 Public cible (voc011) -->
<xsl:template match="lom:intendedEndUserRole">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">011</xsl:with-param>
        <xsl:with-param name="eltdesc">5.5 Public cible</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 5.6 Niveau (pédagogie) (voc012) -->
<xsl:template match="lom:context">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">012</xsl:with-param>
        <xsl:with-param name="eltdesc">5.6 Niveau (pédagogie)</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 5.8 Difficulté (pédagogie) (voc025) -->
<xsl:template match="lom:difficulty">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">025</xsl:with-param>
        <xsl:with-param name="eltdesc">5.8 Difficulté (pédagogie)</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 5.11 Langue de l'utilisateur (voc001) -->
<xsl:template match="lom:language[parent::lom:educational]">
    <xsl:apply-templates select="." mode="migratesingleelt">
        <xsl:with-param name="vocid">001</xsl:with-param>
        <xsl:with-param name="eltdesc">5.11 Langue de l'utilisateur</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 5.12 Activités induites (voc019) -->
<xsl:template match="lomfr:activity">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">019</xsl:with-param>
        <xsl:with-param name="eltdesc">5.12 Activités induites</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 5.13 Validation des acquis (à passer en LANGSTRING français) -->
<xsl:template match="lomfr:credit">
    <xsl:copy>
        <lomfr:string language="{$FR_LANG_URI}">
            <xsl:value-of select="."/>
        </lomfr:string>
    </xsl:copy>
</xsl:template>

<!-- 5.14 Lieux (voc017) -->
<xsl:template match="scolomfr:place">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">017</xsl:with-param>
        <xsl:with-param name="eltdesc">5.14 Lieux</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 5.15 Modalité pédagogique (voc018) -->
<xsl:template match="scolomfr:educationalMethod">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">018</xsl:with-param>
        <xsl:with-param name="eltdesc">5.15 Modalité pédagogique</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 5.16 Outil (voc020) -->
<xsl:template match="scolomfr:tool">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">020</xsl:with-param>
        <xsl:with-param name="eltdesc">5.16 Outil</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 6.1 Coût (voc026) -->
<xsl:template match="lom:cost">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">026</xsl:with-param>
        <xsl:with-param name="eltdesc">6.1 Coût</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 6.2 Droit d'auteur (voc027) -->
<xsl:template match="lom:copyrightAndOtherRestrictions">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">027</xsl:with-param>
        <xsl:with-param name="eltdesc">6.2 Droit d'auteur</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 7.1 Type (relation) (voc009) -->
<xsl:template match="lom:kind">
    <xsl:apply-templates select="." mode="migratesourcevalue">
        <xsl:with-param name="vocid">009</xsl:with-param>
        <xsl:with-param name="eltdesc">7.1 Type (relation)</xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<!-- 9.1 Classification -->
<xsl:template match="lom:classification">
    <xsl:variable name="val" select="lom:purpose/lom:value" />
    <xsl:variable name="voc_doc_path" select="concat($vocpath,'/scolomfr_pt_voc_028.xml')" />
    <xsl:variable name="concepts" select="document($voc_doc_path)//concept[value/text()=$val]" />

    <xsl:copy>
        <xsl:choose>
            <!-- Purpose reconnu -->
            <xsl:when test="count($concepts) = 1">
                <!-- Mise à jour de la structure du lom:purpose -->
                <xsl:apply-templates select="lom:purpose" mode="migratesourcevalue">
                    <xsl:with-param name="vocid">028</xsl:with-param>
                    <xsl:with-param name="eltdesc">9.1 Objectif (classification)</xsl:with-param>
                </xsl:apply-templates>

                <!-- On en déduit le vocabulaire ScoLOMFR normalisé correspondant -->
                <xsl:variable name="conceptid" select="$concepts[1]/@id" />
                <xsl:variable name="vocid">
                    <xsl:choose>
                        <!--<xsl:when test="ends-with($conceptid, '/discipline')">014</xsl:when>-->
                        <!-- Domaines d'enseignement -->
                        <xsl:when test="contains($conceptid, '/scolomfr-voc-028-num-003')">015</xsl:when>
                        <!-- Compétence du socle commun -->
                        <xsl:when test="contains($conceptid, '/competency')">016</xsl:when>
                        <!-- Public cible détaillé -->
                        <xsl:when test="contains($conceptid, '/scolomfr-voc-028-num-011')">021</xsl:when>
                        <!-- Niveau éducatif détaillé -->
                        <xsl:when test="contains($conceptid,'/educational_level')">022</xsl:when>
                        <!-- Diplôme -->
                        <xsl:when test="contains($conceptid,'/scolomfr-voc-028-num-016')">029</xsl:when>
                        <!-- Label -->
                        <xsl:when test="contains($conceptid,'/scolomfr-voc-028-num-013')">045</xsl:when>
                        <!-- Vocabulaire inconnu -->
                        <xsl:otherwise></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <!-- Et mise à jour des taxonPath -->
                <xsl:apply-templates select="lom:taxonPath">
                    <xsl:with-param name="vocid" select="$vocid" />
                </xsl:apply-templates>

                <!-- Et du reste :-) -->
                <xsl:apply-templates select="lom:description|lom:keyword" />
            </xsl:when>

            <!-- Purpose inconnu, on le laisse tel quel -->
            <xsl:otherwise>
                <xsl:apply-templates />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:copy>
</xsl:template>

<!-- Gestion du lom:taxonPath -->
<xsl:template match="lom:taxonPath">
    <xsl:param name="vocid" />

    <xsl:copy>
        <xsl:choose>
            <!-- Si on utilise le vocabulaire suggéré, on effectue la migration -->
            <xsl:when test="$vocid != '' and contains(lom:source/lom:string/text(), $vocid)">
                <!-- On copie tel quel le lom:source -->
                <xsl:apply-templates select="lom:source" />

                <!-- On récupère les concepts du vocabulaire -->
                <xsl:variable name="voc_doc_path" select="concat($vocpath,'/scolomfr_pt_voc_',$vocid,'.xml')" />

                <!-- Et on traite chaque lom:taxon -->
                <xsl:apply-templates select="lom:taxon" mode="migratetaxon">
                    <xsl:with-param name="voc" select="document($voc_doc_path)" />
                    <xsl:with-param name="vocid" select="$vocid" />
                </xsl:apply-templates>
            </xsl:when>
            <!-- Sinon, on recopie tel quel -->
            <xsl:otherwise>
                <xsl:apply-templates />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:copy>
</xsl:template>

<!-- Gestion du lom:taxon -->
<xsl:template match="lom:taxon" mode="migratetaxon">
    <xsl:param name="voc" />
    <xsl:param name="vocid" />

        <xsl:variable name="tid" select="lom:id" />
        <xsl:variable name="tentry" select="lom:entry/lom:string" />

        <xsl:choose>
            <!-- On se base d'abord sur l'id -->
            <xsl:when test="$tid != '' and $voc//concept[value/text()=$tid]">
                <xsl:variable name="nbconcepts" select="count($voc//concept[value/text()=$tid])" />
                <xsl:if test="$nbconcepts &gt; 1">
                    <xsl:call-template name="err">
                        <xsl:with-param name="errmsg">9.1 Classification : identifiant "<xsl:value-of select="$tid"/>" correspond à <xsl:value-of select="$nbconcepts" /> concepts dans le vocabulaire scolomfr-voc-<xsl:value-of select="$vocid" />.
</xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
                <xsl:apply-templates select="$voc//concept[value/text()=$tid]" mode="migratetaxon"/>
            </xsl:when>
            <!-- Puis sur le libellé -->
            <xsl:when test="$tentry != '' and $voc//concept[value/text()=$tentry]">
                <xsl:variable name="nbconcepts" select="count($voc//concept[value/text()=$tentry])" />
                <xsl:if test="$nbconcepts &gt; 1">
                    <xsl:call-template name="err">
                        <xsl:with-param name="errmsg">9.1 Classification : libellé "<xsl:value-of select="$tid"/>" correspond à <xsl:value-of select="$nbconcepts" /> concepts dans le vocabulaire scolomfr-voc-<xsl:value-of select="$vocid" />.
</xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
                <xsl:apply-templates select="$voc//concept[value/text()=$tentry]" mode="migratetaxon"/>
            </xsl:when>
            <!-- On émet une erreur si non trouvé en laissant le code en place -->
            <xsl:otherwise>
                <xsl:call-template name="err">
                    <xsl:with-param name="errmsg">9.1 Classification : valeur "<xsl:value-of select="$tentry"/>" non trouvée dans le vocabulaire scolomfr-voc-<xsl:value-of select="$vocid" />. Élément inchangé
</xsl:with-param>
                </xsl:call-template>

                <xsl:copy>
                    <xsl:apply-templates />
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
</xsl:template>

<!-- Création d'un taxon à partir d'un concept -->
<xsl:template match="concept" mode="migratetaxon">
    <lom:taxon>
        <lom:id><xsl:value-of select="@id" /></lom:id>
        <lom:entry>
            <lom:string language="{$FR_LANG_URI}"><xsl:value-of select="@prefTerm" /></lom:string>
        </lom:entry>
    </lom:taxon>
</xsl:template>

<!-- Gestion des messages d'erreur -->
<xsl:template name="err">
    <xsl:param name="errmsg" />

    <xsl:comment>Attention ! <xsl:value-of select="$errmsg" /></xsl:comment>
    <xsl:message>Attention ! <xsl:value-of select="$errmsg" /></xsl:message>
</xsl:template>

<!-- Template racine : On fait une copie par défaut de l'ensemble de la notice -->
<xsl:template match="/ | @* | * | comment() | processing-instruction() | text()">
    <xsl:copy>
        <xsl:apply-templates select="@* | * | comment() | processing-instruction() | text()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
