# Championstar Soccer - Developer Documentation v2.0

## âš ï¸ CURRENT STATUS: PROTOTYPE (10% COMPLETE)
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

### ðŸ”´ Critical Bugs / incomplete Features
1.  **Match Integration:** The interactive `MatchScreen` exists but is not triggered by `TimeEngine`. Matches are currently simulated instantly in the background. **Next Step:** Integrate `MatchScreen` into the event loop so users play the match instead of just seeing the result.
2.  **Date Persistence:** Loading a saved game (`career_save_v1.json`) restores Player/League data but resets the `GameDate` to Week 1. **Next Step:** Parse/Serialize `currentDate` in `GameStorage`.
3.  **Transfer Market:** `TransferScreen` is a UI placeholder. No logic exists to list players or process transfers.
4.  **Business logic:** You can view businesses, but the "Buy" button is mocked (does not deduct money or add to portfolio).

### ðŸŸ  UI/UX Improvements
*   **Navigation:** The "Back" button behavior needs unification across all screens.
*   **Assets:** "Seeded" Picsum images are good placeholders, but real assets (local resource IDs) should eventually replace them.

---
*Updated: 2024-05-22**   `Curves.kt`: Sigmoid (growth), Logarithmic (diminishing returns), Exponential Decay (aging).
*   `EconomyMath.kt`: Market value valuations, wage calculations, ROI.

#### `data/static`
*   **Purpose:** Massive static datasets to fuel procedural generation.
*   `NameDatabase.kt`: Thousands of names by region.
*   `LeagueDatabase.kt`: Templates for 30 leagues (Tiers 1-4).
*   `EventDatabase.kt`: Text templates for matches and random events.
*   `BusinessDatabase.kt`: 50+ investment types.
*   `ShopDatabase.kt`: 30 items (Star/Glory currency).
*   `AchievementDatabase.kt`: 25 milestones.
*   `GameFoundations.kt`: Enums defining the 5 Point, 5 Financial, 10 Skill, and 60 Game Skill aspects.

#### `domain/models`
*   **Purpose:** Data classes representing the game state.
*   `Player`: The core entity (stats, contract, agent, history).
*   `Team`: Roster, budget, tactics.
*   `League`: Collection of teams, standings.
*   `Contract`: Wage, bonuses, release clause.
*   `Agent`: Level, negotiation skill.
*   `GameDate`: Year/Month/Week tracking.

#### `simulation/engine`
*   **Purpose:** The "Brain" of the game. Processes turns and logic.
*   `TimeEngine.kt`: The main loop (`jumpToNextEvent`). Advances time, triggers maintenance.
*   `MatchEngine.kt`: Simulates matches minute-by-minute using Poisson/Weighted Random.
*   `WorldGenerator.kt`: Creates the initial world (30 leagues, ~12k players).
*   `TransferEngine.kt`: Evaluation logic ("Desire Score") and contract generation.
*   `CareerEngine.kt`: "Zero to Hero" logic (Trial offers, Agent upgrades).
*   `ShopEngine.kt` & `AchievementEngine.kt`: Progression systems.

#### `ui`
*   **Purpose:** Modern Jetpack Compose Interface.
*   `screens/`: `DashboardScreen`, `LeagueScreen`, `ShopScreen`.
*   `components/`: Reusable assets (`PitchBackground`, `PlayerAvatar`, `CurrencyIcons`).
*   `theme/`: Gold/Dark Green color palette.

## 3. Key Game Systems

### The "Smart Math" Simulation
*   **Match Scores:** Calculated using Poisson Distribution based on Team Strength difference (xG).
*   **Player Growth:** Uses Sigmoid curves. Fast growth at 17-23, peak at 27, decay starts at 29 via Exponential Decay.
*   **Market Value:** `Value = Base * (Rating^2.5) * AgeFactor * Reputation`.

### Career Progression
1.  **Start:** User created as 17yo Free Agent with Level 1 Agent.
2.  **Trials:** Can only join Tier 4 clubs initially.
3.  **Rise:** Good form -> Reputation -> Better Agent -> Higher Tier Offers.
4.  **Retirement:** Forced at age 35 (extendable to 40 via Shop).

### Economy
*   **Currencies:**
    *   `STAR`: Earned via Achievements/Matches. Used for consumables.
    *   `GLORY`: Premium currency. Used for "God Mode" items (Reset Age).
*   **Investments:** Players can buy businesses (Lemonade Stand to Diamond Mine) for passive income.

## 4. Current State & Next Steps
*   **Current:** The core loop (Simulate -> Match -> Event) works. UI allows navigation and basic interaction.
*   **Next Steps (Jules Session 2):**
    *   **Save/Load System:** Persist the `World` object using Room or JSON serialization.
    *   **Match Visualizer:** A 2D view of the match events (dots on a pitch).
    *   **Training Minigames:** Interactive ways to boost specific skills.

## 5. File Tree
```
com.championstar.soccer
├── MainActivity.kt
├── core
│   └── math
│       ├── Curves.kt
│       ├── EconomyMath.kt
│       ├── GameMath.kt
│       └── Probability.kt
├── data
│   └── static
│       ├── AchievementDatabase.kt
│       ├── BusinessDatabase.kt
│       ├── EventDatabase.kt
│       ├── GameFoundations.kt
│       ├── LeagueDatabase.kt
│       ├── NameDatabase.kt
│       ├── ShopDatabase.kt
│       └── TraitDatabase.kt
├── domain
│   └── models
│       ├── GameDate.kt
│       └── Models.kt (Player, Team, etc.)
├── simulation
│   └── engine
│       ├── AchievementEngine.kt
│       ├── AgentEngine.kt
│       ├── CareerEngine.kt
│       ├── EconomyEngine.kt
│       ├── EventEngine.kt
│       ├── GrindEngine.kt
│       ├── MatchEngine.kt
│       ├── PlayerGrowthEngine.kt
│       ├── RankingEngine.kt
│       ├── ScheduleEngine.kt
│       ├── ShopEngine.kt
│       ├── SquadEngine.kt
│       ├── TimeEngine.kt
│       ├── TransferEngine.kt
│       └── WorldGenerator.kt
└── ui
    ├── components
    │   ├── Backgrounds.kt
    │   ├── Icons.kt
    │   └── PlayerAvatar.kt
    ├── screens
    │   ├── DashboardScreen.kt
    │   ├── LeagueScreen.kt
    │   └── ShopScreen.kt
    └── theme
        ├── Color.kt
        └── Theme.kt
```
