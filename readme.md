# Daily Reflection App

This Android app provides daily reflections sourced from multiple versions, allowing users to select their preferred source and read daily devotional content. The application uses Jetpack Compose for UI, Retrofit with OkHttp for networking, and state management with ViewModel and Compose mutable state.

***

## Project Structure

```
.
├── app
│   ├── build
│   │   ├── generated
│   │   ├── intermediates
│   │   ├── kotlin
│   │   ├── outputs
│   │   └── tmp
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src
│       ├── androidTest
│       ├── main
│       └── test
├── build
│   └── reports
│       └── problems
├── build.gradle.kts
├── gradle
│   ├── libs.versions.toml
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
└── settings.gradle.kts
```


***

## Key Features

- Multi-source daily reflections (`sh`, `rh`, `roc`)
- Bottom navigation for main sections: Home (today's reflection), Sources (choose version), Favorites (coming soon)
- API integration with Retrofit and OkHttp
- Timeout configured for network reliability
- Error screen with retry and optional illustration support
- Date formatting of reflection dates for improved readability
- Localization support with Bahasa Indonesia labels in navigation and UI messages

***

## Important Components

### UI

- **ReflectionScreen.kt**
Displays today's reflection content and formatted date.
- **SourcesScreen.kt**
Lists primary sources (`Santapan Harian`, `Renungan Harian`, `Renungan Oswald Chambers`) with a bordered selectable item design matching the app’s theme.
- **FavoritesScreen.kt**
Display list of favorited daily reflection
- **ReflectionBottomBar.kt**
  Bottom navigation bar with three tabs:
    - Beranda (Home)
    - Sumber (Sources)
    - Favorit (Favorites)
- **MainReflectionScreen.kt**
Root composable managing navigation state between above screens with Scaffold and bottom bar.


### ViewModel

- **ReflectionViewModel.kt**
Maintains app state: selected source, loading states, error messages, and current reflection data.
Supports source selection with API fetching via Retrofit.
Includes networking timeout settings (suggested 10s connect, 20s read, 15s write).
Exception handling with error state for UI display.


### Networking

- **ReflectionApi.kt**
Retrofit service interface with dynamic path parameter for version selection.
Includes header interceptor with `accesskey`.
OkHttpClient configured with timeouts for network efficiency.

***

## Date Formatting

- Backend date in ISO 8601 (`2025-09-09T02:41:18.648Z`) is parsed and formatted to a user-friendly format like `"09 September"` using `java.time` API and `DateTimeFormatter`.

***

## Error Handling UI

- Error messages are displayed centered with a clean white background.
- Retry button placed prominently below the message and conveniently accessible.
- Optional integration of illustrative images (from unDraw) to visually represent errors or network issues.

***

## Language Localization

- The app UI is localized in Bahasa Indonesia for labels and messages including the bottom navigation bar and error/favorites messages.

***

## Build \& Run

1. Ensure your environment supports Jetpack Compose and Retrofit.
2. Add dependencies in `app/build.gradle.kts` as needed (OkHttp, Retrofit, Gson, Coroutines).
3. Import vector drawable illustrations and place them in `res/drawable`.
4. Ensure your minimum SDK and desugaring rules support `java.time`.
5. Sync and build the project using Gradle scripts.

***

## To-Do

- [x] Add splash screen
- [x] Implement the **Favorites** feature functionality to allow users to save and view favorite reflections
- [x] Enhance **loading and error states** with animations or additional user-friendly illustrations
- [x] Expand support for **offline caching** to allow reading reflections without internet access
- [x] Improve UI/UX with **animations, transitions, and polished designs** for all screens
- [ ] Implement **unit and UI tests** for critical components to ensure reliability
- [ ] Add **settings screen** to allow customization such as theme switching (dark/light mode) and notification preferences
- [ ] Add profile page for tracking
- [ ] Add clickable passage for read the verse

***

### Acknowledgments
This project would not have been possible without the valuable resources and tools that supported its development. We sincerely acknowledge:

* The Bible content provided by alkitab.mobi, which forms the core of daily reflections.

* The open-source technologies including Jetpack Compose, Retrofit, OkHttp, and Gson for enabling modern Android development.

* The unDraw project for providing free, high-quality illustrations used to improve app user experience.

I am grateful for these contributions that enabled the creation and polishing of this app.

***

For questions or further help integrating features or UI components, feel free to reach out.

