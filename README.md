# Liste de courses

> On part pour acheter des fraises et on revient avec un chariot plein… mais sans fraises.

Application Android de gestion de liste de courses : ajouter des produits, organiser par catégories, cocher au fur et à mesure — pour ne plus jamais revenir les mains vides.

## Fonctionnalités

- **Liste de courses** — ajout de lignes, cases à cocher
- **Gestion des produits** — catalogue de produits réutilisables
- **Gestion des catégories** — catégories avec pastilles de couleur
- **Persistance locale** — les données restent sur l'appareil (aucun réseau)

## Technique

| Élément | Valeur |
|---|---|
| Langage | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Navigation | Navigation Compose |
| Persistance | Gson → SharedPreferences |
| SDK minimum | 31 (Android 12) |
| SDK cible / compilation | 35 (Android 15) |
| Build | Gradle Kotlin DSL + version catalog (`gradle/libs.versions.toml`) |

## Structure

```
app/src/main/java/fr/aghi/listedecourses/
├── MainActivity.kt                    # point d'entrée
├── model/                             # Product, ShoppingItem, ProductCategory
├── data/ShoppingListStorage.kt        # persistance locale
└── ui/
    ├── components/                    # écrans liste / produits / catégories
    └── theme/                         # thème (Color, Type, Theme)
```

## Scripts d'aide

| Script | Rôle |
|---|---|
| `run_app.sh` / `run_app.bat` | build et lancement sur l'appareil connecté |
| `run_emulator.sh` | démarrage de l'émulateur |
| `restart_emulator.sh` | redémarrage de l'émulateur |
| `kill_emulator.sh` | arrêt de l'émulateur |
| `launch_emulator_gitbash.sh` | démarrage émulateur depuis Git Bash (Windows) |

## Build

- Ouvrir le projet dans **Android Studio** (SDK 35 requis)
- Ou en ligne de commande :

  ```bash
  ./gradlew assembleDebug
  ```

- APK généré : `app/build/outputs/apk/debug/app-debug.apk`
