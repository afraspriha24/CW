# Project Report

## GitHub

My Repo: [CW](https://github.com/afraspriha24/CW)

## Compilation Instructions

1. **Requirements**:
    - Oracle Open JDK 24.0.1
    - IntelliJ IDEA

2. **To Compile and Run in IntelliJ IDEA**:
   - **Open the project**:
       - Clone the [Repository](https://github.com/afraspriha24/CW)
       - Launch IntelliJ IDEA.
       - Choose **Open** and select the project root folder (`CW`).

   - **Set Project SDK**:
       - Click on `Settings Icon` > `Project Structure` or press `ctrl + alt + shift + S`
       - Then inside `Project Settings` select `Project`
       - Set **Project SDK** to **Oracle Open JDK 24** (install it if not installed)
   - **Build and Run**:
     - Click on Run Button
     - Or press `ctrl + F5`

## Implemented and Working Properly
- **Score bug fixed** — scoring only updates when tiles are merged correctly.
- Full UI refactor of `Cell` interactions using encapsulated methods
- Player Account support — switch users, create new profiles, and track high scores.
- Recent game score tracking per player.
- Restart functionality from end game screen.
- Home screen UI: start game, profile management, high scores, game rules.
- High scores and recent scores shown per profile.

## Implemented but Not Working Properly

## Features Not Implemented
- Custom board size selection (e.g., 5x5, 6x6)
- Game win notification (e.g., reaching 2048)
- Profile avatar or user image
- Login and Database

## New Java Classes

`AccountManager.java`
- Manages creation and lookup of Account objects.
- Isolates static list from Account class for better structure.

`GameBoard.java`
- Extracted from `GameScene` as part of the UI and logic separation.
- Handles board/grid initialization and tile generation.
- Improves modularity and clarity between UI logic and game state.

`HomeScreen.java`
- Replaces original `Main` menu logic with a structured home screen.
- Features:
    - Start New Game
    - Create New Profile
    - Edit Existing Profile (via dropdown)
    - View Recent Scores
    - View Game Rules
    - Exit App

`DataManager.java`
- Handles persistent storage of last logged-in user.
- Reads/writes username from a simple local file.

 
## Modified Java Classes
`Account.java`
- Refactored into a clean user model (no static data).
- Tracks score per session and recent scores (up to 10).
- Added methods:
    - `addRecentScore(long)`
    - `getRecentScores()`

`TextMaker.java`
- Singleton helper for creating styled `Text` nodes.
- No longer requires passing `Group` for each text.
- Method `changeTwoText(...)` now handles text swap and positioning.

`Cell.java`
- Fully refactored to encapsulate both logic and visuals.
- All updates (merge, swap, apply value, modify state) are internal.
- No external class manipulates fill color or `Text` nodes directly.

 `GameScene.java`
- Completely relies on `Cell` abstraction for movement.
- `moveLeft`, `moveRight`, etc. use `swapContent()` and `mergeInto()`.
- Score updates handled only on merges.
- Handles game over by calling `EndGame` screen with restart/home callbacks.

 `EndGame.java`
- Displays score and two buttons:
    - Restart Game (uses fresh scene via `Main`)
    - Back to Home
- Callback-driven UI, no scene manipulation in `EndGame` itself.

`Main.java`
- Controls app flow and scene switching.
- Uses `AccountManager` and `DataManager` to remember the user.
- Rebuilds fresh scenes for every new game or restart.

## Unexpected Problems

- **Score Issue**: Initially, the score incremented incorrectly, which was resolved by updating the scoring logic.
- Unused Code Discovery: Initially believed `Account.java` was already used in the game. After analysis, determined it was never integrated.
- **UI-State Management**: Managing UI (e.g., `Text` and `Color`) externally became error-prone. This was resolved by encapsulating all visual state inside `Cell.java`.