# Notas Rápidas

Megalodonte Android application using **jsonc** UI. The app targets Android 15 (API 35).
Its screen uses only Megalodonte components; JSONC is rendered by the Android bridge.
The [Android JSONC guide](https://github.com/eliezer-dev-software-enginner/megalodonte-android/blob/main/docs/JSONC_ANDROID.md)
lists the supported component types, bindings and platform-specific properties.

Install JDK 25 and Android SDK Platform 35. Clone `megalodonte-android`,
`megalodonte-core`, `megalodonte-theme`, and `megalodonte-reactivity-portable`
under this project's `framework/` directory, or set
`MEGALODONTE_ANDROID_HOME` to the Android framework checkout. Then run:

```sh
./gradlew :app:assembleDebug :app:lintDebug
```

The APK is `app/build/outputs/apk/debug/app-debug.apk`. Open this directory in
Android Studio to run on an Android 15 device or emulator. If its editor shows
an import or constructor error while Gradle builds successfully, use **Sync
Project with Gradle Files** to refresh the local framework modules.

## Example behavior

Create, open, and delete local notes. Titles and bodies persist on the device with SharedPreferences.

The Java host registers `/notes` and `/editor` with Megalodonte's `AndroidRouter`.
Both screens are JSONC assets (`list.view.jsonc` and `editor.view.jsonc`). Tap
**Nova nota** to navigate to the editor; Save returns to the list. Enter a
note number and tap **Abrir nota** to edit the existing note, or **Excluir nota**
to delete it. The editor's Voltar button and Android Back return to the list.

The project was generated with `megalodonte-cli --platform android --mode jsonc` and then completed as a functional sample. Run `./gradlew :app:assembleDebug :app:lintDebug :app:check` to validate it. The UI is native Android through Megalodonte; no WebView is used.
