This is a Kotlin Multiplatform project targeting Android and iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform, you need this entry point for your iOS app.
* [/shared](./shared/src) contains code shared across Compose Multiplatform applications (`commonMain`, `androidMain`, `iosMain`).

---

## Etap 1 - Interfejs użytkownika i nawigacja

W ramach pierwszego etapu projektu zaimplementowano podstawę aplikacji mobilnej:
1. **Start projektu i temat**: Uruchomienie projektu Kotlin Multiplatform z Compose Multiplatform, opierającego się na systemie zarządzania bezpieczeństwem i ewakuacją.
2. **Motyw i biblioteka komponentów**: Wykorzystanie nowoczesnego systemu stylów Material 3 (`MaterialTheme`).
3. **Pięć ekranów**: Stworzenie dedykowanych widoków: Start, Szukaj, Powiadomienia, Ulubione oraz Profil.
4. **Dolna nawigacja**: Implementacja paska nawigacji (`NavigationBar` i `NavigationBarItem`) do przełączania między ekranami.
5. **Układ responsywny**: Wykorzystanie `BoxWithConstraints` w celu dynamicznej zmiany nawigacji na boczny pasek (`NavigationRail`) na szerszych ekranach (tablety, komputery / macOS).

---

## Etap 2 - Cykl życia aplikacji i zachowanie stanu

W ramach drugiego etapu projektu (zgodnie z wymaganiami Laboratorium 3) zaimplementowano:
1. **Obserwację zdarzeń cyklu życia (`LifecycleEventEffect`)**:
   - W kodzie wspólnym (`commonMain`) dodano komponent `ObserwatorCyklu`, który monitoruje zdarzenia `ON_START`, `ON_RESUME`, `ON_PAUSE` oraz `ON_STOP` niezależnie na platformie Android oraz iOS za pomocą biblioteki `lifecycle-runtime-compose`.
2. **Zachowanie stanu ekranów (`rememberSaveable`)**:
   - Zachowywanie wybranej zakładki dolnej/bocznej nawigacji (`selectedRoute`).
   - Zachowywanie wpisanej frazy w wyszukiwarce (`searchQuery`).
   - Zachowywanie danych formularza profilu (`profileName`, `profileEmail`).
   - Zachowywanie stanu przełącznika powiadomień (`notificationsEnabled`).
   - Stan ten nie ulega utracie podczas obrotu urządzenia (zmiana konfiguracji) ani przy wstrzymaniu aplikacji.

---

### Uruchamianie aplikacji

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: Otwórz katalog [/iosApp](./iosApp) w programie Xcode i uruchom stamtąd.

### Uruchamianie testów

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`
