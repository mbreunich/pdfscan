# PDF Scan

Android-App zum Scannen von Dokumenten und Exportieren als PDF. Nutzt die Kamera und ML Kit Document Scanner zur automatischen Dokumentenerkennung.

## Features

- Dokumente über die Kamera scannen (ML Kit Document Scanner)
- Bildbearbeitung mit Filtern
- Export als PDF
- Material 3 Design mit Jetpack Compose

## Tech-Stack

- **Sprache:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **DI:** Hilt (Dagger)
- **Kamera:** CameraX
- **Scanner:** Google ML Kit Document Scanner
- **Navigation:** Jetpack Navigation Compose
- **Build:** Gradle 8.7, AGP 8.5.0
- **Min SDK:** 26 (Android 8.0)
- **Target/Compile SDK:** 35

## Voraussetzungen

- **JDK 17** – z.B. OpenJDK 17
- **Android SDK** – Platform 35, Build-Tools 34+

### JDK installieren (Ubuntu/Debian)

```bash
sudo apt install openjdk-17-jdk
```

### Android SDK installieren (ohne Android Studio)

```bash
mkdir -p ~/Android/Sdk/cmdline-tools
cd ~/Android/Sdk/cmdline-tools
curl -sL https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -o tools.zip
unzip tools.zip && mv cmdline-tools latest && rm tools.zip

export ANDROID_HOME=~/Android/Sdk
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$PATH

yes | sdkmanager --install "platforms;android-35" "build-tools;34.0.0" "platform-tools"
```

## Umgebungsvariablen

Folgende Variablen müssen gesetzt sein (z.B. in `~/.bashrc`):

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=~/Android/Sdk
```

## Build

### Debug

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

### Release

```bash
./gradlew assembleRelease
```

APK: `app/build/outputs/apk/release/app-release-unsigned.apk`

> **Hinweis:** Die Release-APK ist unsigniert. Zum Signieren wird ein Keystore benötigt:
>
> ```bash
> keytool -genkey -v -keystore release.keystore -alias mykey -keyalg RSA -keysize 2048 -validity 10000
> ```
>
> Anschließend in `app/build.gradle.kts` eine `signingConfig` konfigurieren oder manuell mit `apksigner` signieren.

## Projektstruktur

```
app/src/main/java/com/pdfscan/app/
├── MainActivity.kt
├── PdfScanApp.kt
├── data/
│   ├── repository/        # Daten-Repository
│   └── storage/           # Dateispeicher
├── di/                    # Hilt-Module
├── domain/
│   ├── model/             # Datenmodelle (Document, ScannedPage)
│   └── usecase/           # Use Cases (Filter, PDF-Export)
└── ui/
    ├── navigation/        # NavGraph, Routes
    ├── screens/           # Home, Editor, Export
    ├── theme/             # Material 3 Theme
    └── viewmodels/        # ViewModels
```

## Lizenz

Noch keine Lizenz festgelegt.
