#!/bin/bash

# Script spécifique pour Git Bash

# Afficher le type d'OS pour vérification
echo "Système d'exploitation: $OSTYPE"

# Déterminer le chemin de ADB
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    # Windows avec Git Bash
    ADB_PATH="$LOCALAPPDATA/Android/Sdk/platform-tools/adb"
    EMULATOR_PATH="$LOCALAPPDATA/Android/Sdk/emulator"
    echo "Utilisation de ADB sous Windows: $ADB_PATH"
else
    # macOS ou Linux
    ADB_PATH=~/Library/Android/Sdk/platform-tools/adb
    EMULATOR_PATH=~/Library/Android/Sdk/emulator
    # Vérifier si le chemin existe, sinon essayer un autre emplacement courant
    if [ ! -f "$ADB_PATH" ]; then
        ADB_PATH=~/Android/Sdk/platform-tools/adb
        EMULATOR_PATH=~/Android/Sdk/emulator
    fi
    echo "Utilisation de ADB sous Unix: $ADB_PATH"
fi

# Vérifier si ADB existe
if [ ! -f "$ADB_PATH" ]; then
    echo "ERREUR: ADB introuvable à $ADB_PATH"
    echo "Essai avec la commande 'adb' depuis le PATH..."
    ADB_PATH="adb"
fi

# Vérifier si nous sommes bien dans Git Bash
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    echo "Chemin de l'émulateur: $EMULATOR_PATH"
    
    if [ -d "$EMULATOR_PATH" ]; then
        echo "Le dossier de l'émulateur existe."
        
        # Vérifier si l'émulateur est déjà en cours d'exécution
        EMULATOR_RUNNING=$("$ADB_PATH" devices | grep -c emulator)
        
        if [ $EMULATOR_RUNNING -gt 0 ]; then
            echo "Un émulateur est déjà en cours d'exécution."
            echo "Utilisation de l'option -read-only pour lancer une seconde instance..."
            cd "$EMULATOR_PATH"
            ./emulator -avd Pixel_8 -read-only
        else
            cd "$EMULATOR_PATH"
            # Lister les AVDs disponibles
            echo "Les émulateurs disponibles sont:"
            ./emulator -list-avds
            
            # Lancer l'émulateur Pixel_8
            echo "Lancement de l'émulateur Pixel_8..."
            ./emulator -avd Pixel_8
        fi
    else
        echo "ERREUR: Le dossier $EMULATOR_PATH n'existe pas!"
        echo "Veuillez vérifier l'emplacement de votre SDK Android."
    fi
else
    echo "Ce script est conçu pour Git Bash sur Windows."
    echo "Il semble que vous utilisiez un autre environnement: $OSTYPE"
fi 