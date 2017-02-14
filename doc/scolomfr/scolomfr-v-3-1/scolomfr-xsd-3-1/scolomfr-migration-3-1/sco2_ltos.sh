#!/bin/bash
# Conversion d'une notice valide ScoLOMFRv2.0 "lax" en notice "strict"
# utilisant l'outil xmlstarlet ou xsltproc
#
# Pierre Dittgen, PASS-TECH
#
# Ce script est placé sous licence Creative commons Attribution Partage
# à l'Identique 3.0 France
# © 2015 Direction du Numérique pour l’Éducation - Ministère de l'Éducation
# nationale, de l'enseignement supérieur et de la Recherche / Réseau Canopé

# Affichage de la notice d'usage si aucun nom de fichier n'est passé
# en paramètre
if [ "$1" == "" ]; then
    echo "usage: $0 <fichierscolomfr> [ <fichierscolomfrstrict> ]"
    exit
fi

# La XSL de traitement
XSL_FILE=`dirname $0`/xsl/lax_to_v3.0.xsl

# Le fichier à traiter
sco_filepath=$1

# Test de la présence du fichier
if [ ! -e $sco_filepath ]; then
    echo "Erreur : fichier d'entrée \"$sco_filepath\" non trouvé"
    exit 0
fi

# Génération par défaut sur la sortie standard
out=/dev/stdout

# ou dans un fichier si un nom est passé en paramètre
if [ "$2" != "" ]; then
    out=$2
    echo "Fichier de sortie : $out"
fi

function fin {
    if [ "$1" != "/dev/stdout" ]; then
        echo "Fin du traitement."
    fi
}

# Génération de la notice strict

# avec xmlstarlet (linux)
hash xmlstarlet 2>/dev/null
if [ $? -eq 0 ]; then
    xmlstarlet tr $XSL_FILE -s vocpath=../vocs $sco_filepath | xmlstarlet fo -e UTF-8 - > $out
    fin $out
    exit 0
fi

# avec xmlstarlet (mac)
hash xml 2>/dev/null
if [ $? -eq 0 ]; then
    xml tr $XSL_FILE -s vocpath=../vocs $sco_filepath | xml fo -e UTF-8 - > $out
    fin $out
    exit 0
fi

# Sinon, avec xsltproc
hash xsltproc 2>/dev/null
if [ $? -eq 0 ]; then
    xsltproc --stringparam vocpath ../vocs $XSL_FILE $sco_filepath > $out
    fin $out
    exit 0
fi

# Sinon, message d'erreur
echo "outil xmlstarlet et xsltproc non trouvés, impossible d'effectuer la transformation XSL"
exit 1
