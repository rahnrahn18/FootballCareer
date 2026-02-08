# Championstar Soccer - Developer Documentation

## 1. Project Overview
**Championstar Soccer** is a text-based football career simulation game built for Android using **Kotlin 2.1.0** and **Jetpack Compose**. The project focuses on procedural generation, "smart math" simulation, and deep role-playing mechanics (career growth, transfers, business).

## 2. Architecture
The project follows a Domain-Driven Design (DDD) inspired structure, separating pure logic from data and UI.

### Directory Structure (`app/src/main/kotlin/com/championstar/soccer/`)

#### `core/math`
*   **Purpose:** Pure mathematical utilities independent of game logic.
*   `GameMath.kt`: Random number generation, clamping, lerping.
*   `Probability.kt`: Poisson distribution (for match scores), weighted random.
*   `Curves.kt`: Sigmoid (growth), Logarithmic (diminishing returns), Exponential Decay (aging).
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
