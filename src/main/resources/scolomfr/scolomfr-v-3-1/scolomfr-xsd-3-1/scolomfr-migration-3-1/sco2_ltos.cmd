@ECHO off
:: Conversion d'une notice ScoLOMFRv2.0 "lax" en notice "strict"
:: utilisant l'outil xmlstarlet (xml.exe fourni)
::
:: Pierre Dittgen, PASS-TECH
::
:: Ce script est placé sous licence Creative commons Attribution Partage
:: à l'Identique 3.0 France
:: © 2015 Direction du Numérique pour l’Éducation - Ministère de l'Éducation
:: nationale, de l'enseignement supérieur et de la Recherche / Réseau Canopé


:: Aucun fichier passé en paramètre
:: On s'arrête là
IF "%1"=="" GOTO usage

:: fichier ScoLOMFR d'entrée
SET sco_filepath=%1

:: on teste son existence
IF NOT EXIST %sco_filepath% (
    @ECHO Erreur : fichier "%sco_filepath%" introuvable
    EXIT /B 1
)

:: pas de second fichier, on écrit sur la sortie standard
IF "%2"=="" GOTO set_stdout

:: fichier ScoLOMFR de sortie
SET out=%2
@ECHO Fichier de sortie : %out%

:: Appel de la conversion
GOTO convert

:: Sortie standard
:set_stdout
SET out=CON

:: Conversion proprement dite
:convert
xml.exe tr %~dp0/xsl/lax_to_v3.0.xsl -s vocpath=../vocs %sco_filepath% | xml.exe fo -e UTF-8 - > %out%
GOTO eof

:: Notice d'usage
:usage
@ECHO Usage: %0 ^<laxscolomfrfile^> [ ^<strictscolomfrfile^> ]
EXIT /B 1

:eof
if "%2"=="" EXIT /B 1
@ECHO Fin du traitement
