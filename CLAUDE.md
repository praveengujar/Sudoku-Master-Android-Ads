# CLAUDE.md - Sudoku Master Android App

## Project Overview
This is the Android equivalent of the iOS Sudoku Master app, built with modern Android architecture and Material You design patterns optimized for Pixel devices.

## Key Commands
Run these commands after making significant changes to ensure code quality:

### Build Commands
```bash
./gradlew clean
./gradlew assembleDebug
./gradlew build
```

### Testing Commands
```bash
./gradlew test
./gradlew connectedAndroidTest
```

### Lint and Type Check
```bash
./gradlew lint
./gradlew lintDebug
```

## Recent Major Updates

### Latest Fixes & Improvements (January 2025)
1. **🔧 Biometric Authentication Fix**: Completely rebuilt biometric authentication system
   - Created comprehensive BiometricAuthManager utility class
   - Fixed coroutine-based authentication with proper error handling
   - Resolved dependency injection issues with Hilt
   - Updated AuthTokenStorage for secure biometric token access

2. **🎨 App Icon Update**: Fixed app icon display from image assets
   - Updated AndroidManifest.xml to use proper adaptive icons
   - Added Android 13+ support with monochrome Material You theming
   - Fixed resource references for consistent icon display across Android versions

3. **🏗️ Build System Improvements**: Resolved compilation issues
   - Fixed KSP (Kotlin Symbol Processing) cache corruption
   - Cleaned up conflicting property declarations in ViewModels
   - Ensured stable build configuration for development

### UI/UX Enhancements
1. **Material You Design**: Complete redesign with Pixel 10 Pro optimizations
2. **Dark Login Screen**: Attractive dark theme with vibrant colorful buttons
3. **Profile Management**: Complete profile screen with settings and logout functionality
4. **Modern App Icon**: Material You design with adaptive icons and monochrome support

### Architecture Improvements
1. **MVVM with Jetpack Compose**: Modern declarative UI framework
2. **Hilt Dependency Injection**: Clean architecture with proper DI
3. **JWT Authentication**: Secure token-based auth with biometric support
4. **Room Database**: Offline storage with encrypted preferences

### Key Features Implemented
- ✅ Login/Register with dark theme and colorful UI
- ✅ Profile screen with user info and logout functionality
- ✅ **Biometric authentication (Face ID/Fingerprint) - FIXED**
- ✅ Guest mode with proper profile switching
- ✅ **Modern Material You app icon design - UPDATED**
- ✅ Compact game layout with banner ad space
- ✅ 9x9 Sudoku grid with proper 3x3 sub-grid borders
- ✅ Action icons: difficulty levels (E/M/H) + actions (Redo/Hint/AutoSolve)
- ✅ JWT token management with automatic refresh
- ✅ Offline mode with Room database storage
- ✅ Meta Audience Network ad integration (stub implementation)

### Backend Integration
- **Google Cloud Run**: Deployed backend API at `https://sudoku-master-api-93673815784.us-central1.run.app/api/`
- **JWT Authentication**: 15-minute access tokens with 7-day refresh tokens
- **User Management**: Registration, login, token refresh, and user deletion endpoints

## Current Status
The app is fully functional with all major features implemented:

1. **Authentication System**: Complete with biometric support
2. **Game Interface**: Modern UI matching reference design
3. **Profile Management**: Full user profile with settings
4. **Offline Capabilities**: Local storage with Room database
5. **Ad Integration**: Ready for Meta Audience Network ads

## Development Notes

### Recent Fixes
- **✅ FIXED: Biometric Authentication** - Completely rebuilt system with BiometricAuthManager
- **✅ FIXED: App Icon Display** - Updated to use proper adaptive icons with Material You support
- **✅ FIXED: Build Issues** - Resolved KSP cache corruption and compilation errors
- Fixed Material 3 adaptive dependencies issues
- Resolved profile switcher functionality
- Added proper navigation between screens
- Implemented dark theme login screen with colorful accents

### Next Steps for Production
1. Configure Meta Audience Network placement IDs
2. Add production backend endpoints
3. Implement comprehensive error handling
4. Add unit and integration tests
5. Optimize performance and memory usage

## Technical Implementation Details

### Biometric Authentication System
The biometric authentication system was completely rebuilt with a comprehensive solution:

```kotlin
// BiometricAuthManager.kt - New utility class
@Singleton
class BiometricAuthManager @Inject constructor() {
    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onCancel: () -> Unit = {}
    )
}

// AuthTokenStorage.kt - Updated for coroutine support
private suspend fun authenticateWithBiometric(activity: FragmentActivity): Boolean {
    return suspendCancellableCoroutine { continuation ->
        biometricAuthManager.authenticate(
            activity = activity,
            title = "Authenticate to access Sudoku Master",
            subtitle = "Use your biometric credential to sign in",
            onSuccess = { if (continuation.isActive) continuation.resume(true) },
            onError = { error -> if (continuation.isActive) continuation.resume(false) }
        )
    }
}
```

### App Icon Configuration
Updated to use proper adaptive icons with Material You support:

```xml
<!-- AndroidManifest.xml - Updated references -->
android:icon="@mipmap/ic_launcher"
android:roundIcon="@mipmap/ic_launcher_round"

<!-- mipmap-anydpi-v33/ic_launcher.xml - Android 13+ support -->
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
    <monochrome android:drawable="@drawable/ic_launcher_monochrome"/>
</adaptive-icon>
```

## File Structure Highlights
```
app/src/main/java/com/sudokumaster/android/
├── presentation/ui/
│   ├── auth/AuthScreen.kt          # Dark theme login with colorful buttons
│   ├── game/GameScreen.kt          # Main game interface
│   ├── profile/ProfileScreen.kt    # User profile with logout functionality
│   └── game/components/            # Reusable game components
├── data/
│   ├── local/                      # Room database and DataStore
│   ├── remote/                     # Retrofit API service
│   └── repository/                 # Repository implementations
├── domain/                         # Business logic and models
├── di/AppModule.kt                # Dependency injection setup
├── utils/
│   ├── BiometricAuthManager.kt    # NEW: Biometric authentication utility
│   ├── AdManagerStub.kt           # Ad management utilities
│   └── NetworkMonitor.kt          # Network monitoring utilities
```

## Important Configuration
The app uses version catalogs for dependency management. All major dependencies are configured in `gradle/libs.versions.toml`.

## Testing
Before deployment, ensure:
1. All login/logout functionality works properly
2. Profile screen navigation is functional
3. Biometric authentication is tested on supported devices
4. Offline mode works correctly
5. Ad integration is properly configured for production