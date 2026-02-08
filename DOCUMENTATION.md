# Championstar Soccer - Developer Documentation v2.0

## ⚠️ CURRENT STATUS: PROTOTYPE (10% COMPLETE)
**This project is in early development.** While the core loop and UI framework are in place, many systems are placeholders or disconnected. Future developers must read the "Known Issues & Roadmap" section before starting work.

---

## 1. Project Overview
**Championstar Soccer** is a horizontal, turn-based football career simulation game (RPG/Tycoon) for Android.
*   **Genre:** Career Simulation / Tycoon (Not a twitch-action game).
*   **Core Loop:** Daily Turn-Based Events -> Decisions -> Match Day -> Progression.
*   **Visual Style:** "Dark Sporty" (Neon Gold/Green/Black), Minimalist Landscape UI.

## 2. Technical Stack (STRICT)
*   **Language:** Kotlin 2.1.0
*   **UI:** Jetpack Compose (Material3)
*   **Persistence:** **Gson (JSON File Storage)**. *Note: Room/SQLite is strictly BANNED due to AndroidIDE/aarch64 native library issues.*
*   **Assets:** `coil-compose` loading seeded `picsum.photos` URLs (Placeholder strategy).
*   **Environment:** AndroidIDE (On-Device Build). `build.gradle.kts` uses JVM 17 target.

## 3. Architecture
The project follows a modular, Domain-Driven Design (DDD) structure:

### `com.championstar.soccer`
*   **`core/math`**: Pure math utilities (Probability, Curves, Economy).
*   **`data/`**:
    *   `static/`: Hardcoded content databases (`EventDatabase`, `BusinessDatabase`, `ShopDatabase`).
    *   `local/`: `GameStorage.kt` (JSON serialization logic).
*   **`domain/models`**: Pure data classes (`Player`, `Team`, `League`, `Business`). *No annotations.*
*   **`simulation/engine`**: The game logic "brains".
    *   `TimeEngine`: Manages the daily turn loop (`GameTurnEvent`).
    *   `EventEngine`: Generates categorized stories (Match, Family, Agent).
    *   `EconomyEngine`: Handles wages, business income.
    *   `MatchEngine`: Simulates match outcomes (currently instant simulation).
*   **`ui/`**:
    *   `screens/`: `DashboardScreen`, `MatchScreen`, `BusinessScreen`, etc.
    *   `components/`: Reusable widgets (`AssetLoader`, `PitchBackground`).
    *   `theme/`: `Theme.kt` (Dark/Gold aesthetics).

## 4. Key Systems

### Event System (Daily Loop)
Instead of a "Next Week" button, the game uses a **Daily Event Box**.
1.  User taps "Next Day".
2.  `TimeEngine.advanceTime()` is called.
3.  Returns a `GameTurnEvent`:
    *   `StoryEvent`: Narrative choice (from `EventDatabase`).
    *   `MatchEvent`: Result of a match (simulated).
    *   `RoutineEvent`: Generic training day.

### Economy & Business
Players can invest in businesses (`BusinessDatabase`) to earn weekly passive income.
*   **Logic:** `EconomyEngine.processWeeklyFinances`.
*   **Currency:** `STAR` (Gameplay) and `GLORY` (Premium).

### Match Simulation
*   **Current:** `MatchEngine` simulates the entire game instantly using Poisson distribution.
*   **Future:** `MatchScreen` contains a minute-by-minute interactive simulation loop, but it is currently **disconnected** from the main `TimeEngine` flow.

## 5. Known Issues & Roadmap (The "90%" Remaining)

### 🔴 Critical Bugs / incomplete Features
1.  **Match Integration:** The interactive `MatchScreen` exists but is not triggered by `TimeEngine`. Matches are currently simulated instantly in the background. **Next Step:** Integrate `MatchScreen` into the event loop so users play the match instead of just seeing the result.
2.  **Date Persistence:** Loading a saved game (`career_save_v1.json`) restores Player/League data but resets the `GameDate` to Week 1. **Next Step:** Parse/Serialize `currentDate` in `GameStorage`.
3.  **Transfer Market:** `TransferScreen` is a UI placeholder. No logic exists to list players or process transfers.
4.  **Business logic:** You can view businesses, but the "Buy" button is mocked (does not deduct money or add to portfolio).

### 🟠 UI/UX Improvements
*   **Navigation:** The "Back" button behavior needs unification across all screens.
*   **Assets:** "Seeded" Picsum images are good placeholders, but real assets (local resource IDs) should eventually replace them.

---
*Updated: 2024-05-22*
