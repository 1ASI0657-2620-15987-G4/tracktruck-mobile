# TrackTruck Mobile

Native Android client for TrackTruck, built with Kotlin and Jetpack Compose.

## Local configuration

The Android emulator uses `http://10.0.2.2:8080/api/v1/` by default. Override it without changing source code:

```powershell
.\gradlew.bat assembleDebug -PTRACKTRUCK_API_BASE_URL=https://your-api.example/api/v1/
```

Firebase is optional for compilation. To enable Firebase authentication, provide an authorized `app/google-services.json`; the file is intentionally ignored and is not inherited from the source project.
