#!/bin/bash

# Script pour redémarrer complètement l'émulateur Android avec les bons paramètres

echo "Arrêt de tous les émulateurs en cours d'exécution..."

# Déterminer le chemin de ADB
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    # Windows avec Git Bash
    ADB_PATH="$LOCALAPPDATA/Android/Sdk/platform-tools/adb.exe"
    EMULATOR_PATH="$LOCALAPPDATA/Android/Sdk/emulator/emulator.exe"
    echo "Utilisation de ADB sous Windows: $ADB_PATH"
else
    # macOS ou Linux
    ADB_PATH=~/Library/Android/Sdk/platform-tools/adb
    EMULATOR_PATH=~/Library/Android/Sdk/emulator/emulator
    # Vérifier si le chemin existe, sinon essayer un autre emplacement courant
    if [ ! -f "$ADB_PATH" ]; then
        ADB_PATH=~/Android/Sdk/platform-tools/adb
        EMULATOR_PATH=~/Android/Sdk/emulator/emulator
    fi
    echo "Utilisation de ADB sous Unix: $ADB_PATH"
fi

# Vérifier si ADB existe
if [ ! -f "$ADB_PATH" ]; then
    echo "ERREUR: ADB introuvable à $ADB_PATH"
    echo "Essai avec la commande 'adb' depuis le PATH..."
    ADB_PATH="adb"
fi

# Arrêter tous les émulateurs en cours d'exécution
EMULATOR_RUNNING=$("$ADB_PATH" devices | grep -c emulator)
if [ $EMULATOR_RUNNING -gt 0 ]; then
    echo "Émulateurs détectés : $EMULATOR_RUNNING"
    
    # Arrêter tous les émulateurs via adb
    for device in $("$ADB_PATH" devices | grep emulator | cut -f1)
    do
        echo "Arrêt de l'émulateur $device..."
        "$ADB_PATH" -s $device emu kill
    done
fi

# Attendre un moment pour que les processus se terminent
sleep 3

# S'assurer que tous les processus d'émulateur sont tués
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    # Windows avec Git Bash
    echo "Terminaison forcée des processus d'émulateur..."
    taskkill //F //IM qemu-system-x86_64.exe > /dev/null 2>&1
    taskkill //F //IM qemu-system-x86.exe > /dev/null 2>&1
    taskkill //F //IM emulator.exe > /dev/null 2>&1
    taskkill //F //IM adb.exe > /dev/null 2>&1
else
    # macOS ou Linux
    echo "Terminaison forcée des processus d'émulateur..."
    pkill -f 'qemu-system-' > /dev/null 2>&1
    pkill -f 'emulator' > /dev/null 2>&1
    pkill -f 'adb' > /dev/null 2>&1
fi

# Attendre que tous les processus soient terminés
sleep 2

# Redémarrer le serveur ADB
echo "Redémarrage du serveur ADB..."
"$ADB_PATH" kill-server
sleep 1
"$ADB_PATH" start-server
sleep 1

# Liste des AVDs disponibles
echo "Recherche des émulateurs Android disponibles..."
cd "$LOCALAPPDATA/Android/Sdk/emulator" || { echo "Dossier émulateur introuvable"; exit 1; }

# Lister les AVDs disponibles
echo "Les émulateurs disponibles sont:"
./emulator -list-avds

# Demander à l'utilisateur quelle AVD utiliser ou utiliser Pixel_8 par défaut
echo "Lancement de l'émulateur Pixel_8..."
echo "Lancement avec paramètres optimisés..."

# Lancer l'émulateur avec des options optimisées pour éviter les problèmes de démarrage
./emulator -avd Pixel_8 -no-snapshot-load -no-boot-anim -no-audio -gpu host

# Attendre que l'émulateur soit prêt (important)
echo "Attente du démarrage de l'émulateur..."
"$ADB_PATH" wait-for-device

echo "Émulateur démarré. Veuillez attendre que le système Android termine son démarrage."
echo "Vous pouvez lancer l'application avec: ./run_android_app.sh" 