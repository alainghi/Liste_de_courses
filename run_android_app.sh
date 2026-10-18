#!/bin/bash

# Script pour lancer l'application Android après vérification que l'émulateur est prêt

echo "Vérification de l'émulateur Android..."

# Déterminer le chemin de ADB
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    # Windows avec Git Bash
    ADB_PATH="$LOCALAPPDATA/Android/Sdk/platform-tools/adb.exe"
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

# Vérifier si un émulateur est en cours d'exécution
EMULATOR_RUNNING=$("$ADB_PATH" devices | grep -c emulator)

if [ $EMULATOR_RUNNING -eq 0 ]; then
    echo "Aucun émulateur n'est en cours d'exécution."
    echo "Veuillez démarrer l'émulateur avant d'exécuter ce script."
    exit 1
fi

echo "Émulateur détecté, vérification qu'il est prêt..."

# Attendre que l'émulateur soit complètement démarré
"$ADB_PATH" wait-for-device

# Attendre que le système soit prêt (boot complet)
BOOT_COMPLETE=$("$ADB_PATH" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
while [ "$BOOT_COMPLETE" != "1" ]; do
    echo "En attente du démarrage complet de l'émulateur..."
    sleep 2
    BOOT_COMPLETE=$("$ADB_PATH" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
done

echo "Émulateur Android prêt!"

# Compiler et installer l'application
echo "Compilation et installation de l'application..."
./gradlew installDebug

if [ $? -ne 0 ]; then
    echo "ERREUR lors de la compilation ou de l'installation."
    exit 1
fi

# Attendre un peu pour que l'installation se termine complètement
sleep 2

# Lancer l'application
echo "Lancement de l'application..."
"$ADB_PATH" shell am start -n "fr.aghi.listedecourses/fr.aghi.listedecourses.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER

if [ $? -ne 0 ]; then
    echo "ERREUR lors du lancement de l'application."
    
    # Vérifier le nom du package
    echo "Vérification des packages installés..."
    PACKAGES=$("$ADB_PATH" shell pm list packages | grep -i listedecourses)
    
    if [ -z "$PACKAGES" ]; then
        echo "Aucun package contenant 'listedecourses' n'a été trouvé."
        echo "Voici la liste des packages récemment installés :"
        "$ADB_PATH" shell pm list packages -3 | tail -10
    else
        echo "Packages trouvés : $PACKAGES"
        
        # Extraire le nom exact du package
        PACKAGE_NAME=$(echo $PACKAGES | sed 's/package://g')
        
        echo "Tentative de lancement avec le package exact : $PACKAGE_NAME"
        "$ADB_PATH" shell monkey -p $PACKAGE_NAME -c android.intent.category.LAUNCHER 1
    fi
    
    exit 1
fi

echo "Application lancée avec succès!" 