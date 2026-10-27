# Notas Rápidas

Megalodonte Android sample application for keeping notes on one device. Its
**JSONC** UI targets Android 15 (API 35); the minimum SDK is also API 35.
Its screen uses only Megalodonte components; JSONC is rendered by the Android bridge.
The [Android JSONC guide](https://github.com/eliezer-dev-software-enginner/megalodonte-android/blob/main/docs/JSONC_ANDROID.md)
lists the supported component types, bindings and platform-specific properties.

## Build and run

Install JDK 25 and Android SDK Platform 35. This project also requires local
checkouts of `megalodonte-android`, `megalodonte-core`, `megalodonte-theme`,
and `megalodonte-reactivity-portable`. Set `MEGALODONTE_ANDROID_HOME` to the
`megalodonte-android` checkout. Place the three other checkouts in a
`libraries/` directory alongside that checkout, for example:

```text
workspace/
  megalodonte-android/
  libraries/
    megalodonte-core/
    megalodonte-theme/
    megalodonte-reactivity-portable/
```

The Gradle settings also accept `../../framework/megalodonte-android` relative
to this repository when the environment variable is unset. From the project
root, run:

```sh
./gradlew :app:assembleDebug :app:lintDebug
```

The APK is `app/build/outputs/apk/debug/app-debug.apk`. Open the project root in
Android Studio to run on an Android 15 device or emulator. If its editor shows
an import or constructor error while Gradle builds successfully, use **Sync
Project with Gradle Files** to refresh the local framework modules.

## Example behavior

Create, open, and delete local notes. Titles and bodies persist on the device
with Android SharedPreferences. A note needs both a title and a body to save.

The Java host registers `/notes` and `/editor` with Megalodonte's `AndroidRouter`.
Both screens are JSONC assets (`list.view.jsonc` and `editor.view.jsonc`). Tap
**Nova nota** to navigate to the editor; Save returns to the list. Enter a
note's number from the displayed list and tap **Abrir nota** to edit it, or **Excluir nota**
to delete it. The editor's Voltar button and Android Back return to the list.

The project was generated with `megalodonte-cli --platform android --mode jsonc` and then completed as a functional sample. Run `./gradlew :app:assembleDebug :app:lintDebug :app:check` to validate it. The UI is native Android through Megalodonte; no WebView is used.
