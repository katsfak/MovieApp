## CineFlix

An Android movie discovery app built with Java, MVVM, and a clean layered architecture.

CineFlix lets users browse popular movies, open rich details, view reviews and similar titles, play trailers, save favorites locally, and manage a simple local-auth profile with dark mode support.

## Features

- User authentication flow (sign up, login, logout) using a local Room database
- Movie list screen with loading skeletons and pull-to-refresh
- Movie details screen with:
  - poster and metadata
  - cast and reviews
  - similar movies
  - share action
- Trailer playback via YouTube links
- Favorite movies persisted with Room
- Profile screen with credentials update and dark mode toggle
- Navigation Component based multi-screen flow

## Tech Stack

- **Language:** Java 11
- **Build System:** Gradle (Kotlin DSL)
- **Android Gradle Plugin:** 8.13.2
- **Architecture:** MVVM + Repository + UseCase layers
- **Dependency Injection:** Hilt
- **Networking:** Retrofit + Gson + OkHttp Logging Interceptor
- **Async/Reactive:** RxJava3 + RxAndroid
- **Local Database:** Room
- **UI:** AndroidX, Material Components, ViewBinding, RecyclerView, SwipeRefreshLayout
- **Image Loading:** Glide
- **Navigation:** AndroidX Navigation + Safe Args

## Requirements

- Android Studio (latest stable recommended)
- JDK 17 or newer (recommended for AGP 8.13.2)
- Java source/target compatibility: 11
- Android SDK:
  - `compileSdk = 36`
  - `targetSdk = 36`
  - `minSdk = 24`
- Internet connection (for TMDb API requests)

## Getting Started

### 1) Clone and open the project

```powershell
git clone https://github.com/katsfak/MovieApp
cd "MovieApp"
```

Open the folder in Android Studio and let Gradle sync.

### 2) Configure local properties

Make sure `local.properties` contains your Android SDK location and TMDb API key:

```properties
sdk.dir=C:\\Users\\<YourUser>\\AppData\\Local\\Android\\Sdk
TMDB_API_KEY=your_tmdb_api_key_here
```

`local.properties` is git-ignored, so your key is not committed to GitHub.

## Build and Run

### Run from Android Studio

1. Select an emulator or a connected Android device.
2. Click **Run** for the `app` module.

### Build from terminal

```powershell
.\gradlew.bat assembleDebug
```

### Install on connected device/emulator

```powershell
.\gradlew.bat installDebug
```

### Run unit tests

```powershell
.\gradlew.bat testDebugUnitTest
```

### Run instrumentation tests

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

## Project Structure

```text
app/src/main/java/com/example/movieapp/
|- common/          # mappers
|- data/
|  |- auth/         # local auth/session manager
|  |- database/     # Room entities, DAO, DB
|  |- model/        # remote DTOs
|  |- network/      # network utilities
|  |- networkServices/
|  \- repo/         # repository implementations
|- di/              # Hilt modules
|- domain/
|  |- models/       # UI/domain models
|  |- repoInterfaces/
|  \- usecase/
\- ui/
   |- auth/
   |- detailsScreen/
   |- homeScreen/
   |- profile/
   \- MainActivity.java
```

## Navigation Flow

- `loginFragment` -> `signupFragment` -> `movieListFragment`
- `movieListFragment` -> `detailsScreen`
- `movieListFragment` -> `profileFragment` -> `favoritesFragment`
- `detailsScreen` -> `reviewsListFragment`

Navigation graph file: `app/src/main/res/navigation/my_graph.xml`

## Configuration and Security Notes

- Current auth implementation uses a local Room database for demo/local state.
- Credentials are stored locally and are not suitable for production security requirements.
- Logout clears the current logged-in user flag from the local database.
- TMDb API key is loaded from local `local.properties` (`TMDB_API_KEY`) into `BuildConfig` at build time.

## Troubleshooting

- **Gradle sync fails**
  - Verify internet connection
  - Confirm `sdk.dir` in `local.properties`
  - Re-sync Gradle and run:

  ```powershell
  .\gradlew.bat --refresh-dependencies
  ```

- **Build fails with Java version errors**
  - Ensure Android Studio uses JDK 11 for Gradle.

- **No movie data appears**
  - Verify `TMDB_API_KEY` is present in `local.properties` and is valid.
  - Verify network availability.

## Suggested Improvements

- Move secrets to secure local/env config
- Add pagination for movie lists
- Add offline caching strategy for detail/review payloads
- Add CI checks for lint + unit tests
- Add screenshot section for each main screen

## Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a pull request

## License

No license is currently defined.

If you plan to distribute this project, add a `LICENSE` file (for example MIT or Apache-2.0).
