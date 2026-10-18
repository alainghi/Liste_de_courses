#!/bin/bash

# Ce script est conçu pour fonctionner avec Git Bash sur Windows
# et utilise la variable $LOCALAPPDATA correctement

# Vérifier si l'émulateur est déjà en cours d'exécution
adb devices | grep -q "emulator-" 
if [ $? -ne 0 ]; then
    echo "Démarrage de l'émulateur Android..."
    
    # Utilisation du chemin correct pour Git Bash
    if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
        # Sous Windows avec Git Bash
        cd "$LOCALAPPDATA/Android/Sdk/emulator" && ./emulator -avd Pixel_8 &
    else
        # Sous macOS ou Linux
        cd ~/Library/Android/Sdk/emulator && ./emulator -avd Pixel_8 &
    fi
    
    # Attendre que l'émulateur soit prêt
    echo "Attente du démarrage de l'émulateur..."
    adb wait-for-device
    sleep 10  # Donner plus de temps pour que l'émulateur soit complètement opérationnel
else
    echo "L'émulateur est déjà en cours d'exécution."
fi

# Compiler et installer l'application
echo "Compilation et installation de l'application..."
./gradlew installDebug

# Lancer l'application
echo "Lancement de l'application..."
adb shell am start -n fr.aghi.listedecourses/.MainActivity

echo "Application lancée avec succès !" 