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

## Etap 3 - Architektura warstwowa, nawigacja i CRUD

1. **Podział na warstwy** (`shared/src/commonMain/.../projekt`):
   - **UI** – `App.kt`, `ui/IncydentScreens.kt` (lista, formularz; Compose, bez logiki biznesowej),
   - **ViewModel** – `viewmodel/IncydentViewModel.kt` (logika prezentacji, walidacja, stan jako `StateFlow`),
   - **Repozytorium / dane** – `data/IncydentRepository.kt` (interfejs + `InMemoryIncydentRepository`), model `data/Incydent.kt`.
2. **Nawigacja**: dolny pasek / boczny `NavigationRail` między 5 zakładkami oraz ekran formularza (dodawanie/edycja) ze strzałką wstecz w nagłówku; stan nawigacji zachowywany przy obrocie.
3. **CRUD** na liście zgłoszeń (zakładka Start): dodawanie (przycisk ＋), edycja, usuwanie z potwierdzeniem, oznaczanie jako ulubione (lista w zakładce Ulubione), wyszukiwanie w zakładce Szukaj.

---

## Zmiany poza etapami - warstwa wizualna (bez zmian w działaniu)

1. **Własny motyw** (`ui/theme/Theme.kt`): stonowany granat z pomarańczowym akcentem, wersja jasna i ciemna, własne zaokrąglenia (`Shapes`) i typografia (`Typography`).
2. **Przełącznik motywu jasny/ciemny** w nagłówku każdej zakładki (słońce – suwak – księżyc); domyślnie zgodny z ustawieniem systemu, wybór zachowywany przy obrocie ekranu (`rememberSaveable`).
3. **Ikony Material zamiast emoji** (zależność `material-icons-core`) w nawigacji, na kartach i w formularzu.
4. **Karta zgłoszenia**: pogrubiony tytuł, plakietka priorytetu (czerwona / żółta / zielona), lokalizacja z ikoną pinezki, opis do 2 linii, akcje (ulubione, edycja, usuwanie) jako ikony.
5. **Nagłówki ekranów** (`TopAppBar`), zakładka „Start” przemianowana na „Zgłoszenia”; formularz z nagłówkiem i strzałką wstecz, przyciski Zapisz/Anuluj na całą szerokość; chipy priorytetu w kolorach plakietek.
6. **Stany puste** (ikona + tekst) oraz ekrany Alerty i Profil ułożone w karty.
7. **Obsługa klawiatury w formularzu** (`imePadding`, przewijana treść, akcje IME Next/Done, chowanie klawiatury po dotknięciu tła), chipy priorytetu w układzie zawijanym (`FlowRow`), w poziomie przycisk „+” obok listy zamiast nad kartami, ciemne warianty plakietek priorytetu, jednoliniowe pola profilu i czytelne podsumowanie przy pustych danych; w wyszukiwarce i na Profilu klawiaturę chowa klawisz Search/Done oraz dotknięcie tła (naprawa błędu na iOS, gdzie klawiatura zasłaniała dolną nawigację).
8. Logika, ViewModel, repozytorium i nawigacja pozostały bez zmian.

---

### Uruchamianie aplikacji

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: Otwórz katalog [/iosApp](./iosApp) w programie Xcode i uruchom stamtąd.

### Uruchamianie testów

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`
