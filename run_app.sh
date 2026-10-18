#!/bin/bash

# Vérifie si l'émulateur est déjà en cours d'exécution
adb devices | grep -q "emulator-" 
if [ $? -ne 0 ]; then
    echo "Démarrage de l'émulateur Android..."
    cd ~/Library/Android/Sdk/emulator && ./emulator -avd Pixel_8 &
    
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