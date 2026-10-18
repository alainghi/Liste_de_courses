@echo off
:: Script pour lancer l'application Android sous Windows

echo Verification des emulateurs en cours d'execution...

:: Verifier si adb est accessible
where adb >nul 2>&1
if %errorlevel% neq 0 (
    echo ADB n'est pas accessible. Tentative d'utilisation du chemin complet...
    set ADB_PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe
    if not exist "%ADB_PATH%" (
        echo ERREUR: ADB introuvable a %ADB_PATH%
        echo Assurez-vous que le SDK Android est installe et que le chemin est correct.
        pause
        exit /b 1
    )
) else (
    set ADB_PATH=adb
)

:: Verifier si un emulateur est en cours d'execution
"%ADB_PATH%" devices | findstr "emulator-" > nul
if %errorlevel% neq 0 (
    echo Aucun emulateur n'est en cours d'execution.
    echo Veuillez demarrer l'emulateur avant d'executer ce script.
    pause
    exit /b 1
)

echo Compilation et installation de l'application...
call gradlew.bat installDebug

if %errorlevel% neq 0 (
    echo ERREUR lors de la compilation ou de l'installation.
    pause
    exit /b 1
)

echo Lancement de l'application...
"%ADB_PATH%" shell am start -n "fr.aghi.listedecourses/fr.aghi.listedecourses.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER

if %errorlevel% neq 0 (
    echo ERREUR lors du lancement de l'application.
    pause
    exit /b 1
)

echo Application lancee avec succes!
pause 