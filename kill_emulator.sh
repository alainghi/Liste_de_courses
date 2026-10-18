#!/bin/bash

# Script pour arrêter tous les émulateurs Android en cours d'exécution

echo "Recherche des émulateurs en cours d'exécution..."

# Déterminer le chemin de ADB
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    # Windows avec Git Bash
    ADB_PATH="$LOCALAPPDATA/Android/Sdk/platform-tools/adb"
    echo "Utilisation de ADB sous Windows: $ADB_PATH"
else
    # macOS ou Linux
    ADB_PATH=~/Library/Android/Sdk/platform-tools/adb
    # Vérifier si le chemin existe, sinon essayer un autre emplacement courant
    if [ ! -f "$ADB_PATH" ]; then
        ADB_PATH=~/Android/Sdk/platform-tools/adb
    fi
    echo "Utilisation de ADB sous Unix: $ADB_PATH"
fi

# Vérifier si ADB existe
if [ ! -f "$ADB_PATH" ]; then
    echo "ERREUR: ADB introuvable à $ADB_PATH"
    echo "Essai avec la commande 'adb' depuis le PATH..."
    ADB_PATH="adb"
fi

# Vérifier si des émulateurs sont en cours d'exécution
EMULATOR_RUNNING=$("$ADB_PATH" devices | grep -c emulator)

if [ $EMULATOR_RUNNING -gt 0 ]; then
    echo "Émulateurs détectés : $EMULATOR_RUNNING"
    
    # Arrêter tous les émulateurs via adb
    for device in $("$ADB_PATH" devices | grep emulator | cut -f1)
    do
        echo "Arrêt de l'émulateur $device..."
        "$ADB_PATH" -s $device emu kill
    done
    
    # Attendre un moment pour que les processus se terminent
    sleep 3
    
    # Vérifier à nouveau
    EMULATOR_STILL_RUNNING=$("$ADB_PATH" devices | grep -c emulator)
    if [ $EMULATOR_STILL_RUNNING -gt 0 ]; then
        echo "Certains émulateurs sont toujours en cours d'exécution."
        echo "Tentative de terminaison forcée..."
        
        # Trouver et tuer les processus d'émulateur sur Windows
        if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
            # Tuer les processus qemu-system pour Windows
            echo "Recherche et arrêt des processus qemu-system..."
            taskkill //F //IM qemu-system-x86_64.exe > /dev/null 2>&1
            taskkill //F //IM qemu-system-x86.exe > /dev/null 2>&1
            taskkill //F //IM qemu-system-armel.exe > /dev/null 2>&1
            taskkill //F //IM qemu-system-aarch64.exe > /dev/null 2>&1
            taskkill //F //IM emulator.exe > /dev/null 2>&1
            echo "Processus d'émulateur terminés."
        else
            # Tuer les processus qemu-system pour Linux/Mac
            echo "Recherche et arrêt des processus qemu-system..."
            pkill -f 'qemu-system-' > /dev/null 2>&1
            pkill -f 'emulator' > /dev/null 2>&1
            echo "Processus d'émulateur terminés."
        fi
    else
        echo "Tous les émulateurs ont été arrêtés avec succès."
    fi
else
    echo "Aucun émulateur Android n'est en cours d'exécution."
fi

echo "Opération terminée." 