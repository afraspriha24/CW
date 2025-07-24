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

## Implemented but Not Working Properly

## Features Not Implemented

## New Java Classes

`AccountManager.java`
- Manages creation and lookup of Account objects.
- Isolates static list from Account class for better structure.

`GameBoard.java`
- Extracted from `GameScene` as part of the UI and logic separation.
- Handles board/grid initialization and tile generation.
- Improves modularity and clarity between UI logic and game state.
 
## Modified Java Classes

`Account.java`
- Refactored to remove static list.
- Made getUserName() and getScore() public for external access.
- Simplified to represent a single user's state.
- Left in the codebase for future user-based features.

`TextMaker.java`
- Refactored for clarity and encapsulation.
- Removed the need to pass Group root to madeText(...).
- Made class a formal singleton (getSingleInstance()).

`Cell.java`
- Fully refactored for encapsulation and readability.
- All UI updates are now handled internally via:
    - `applyNewValue(int)`
    - `swapContent(Cell)`
    - `mergeInto(Cell)`
    - `attachTextToRoot()`
    - `updateVisuals()`
- Fixed scoring logic in `mergeInto(...)` to prevent incorrect score updates.
- Removed direct color setting from outside classes.

### `GameScene.java`
- Replaced raw UI manipulation of `Cell` with encapsulated methods.
- Updated movement methods to use `mergeInto` and `swapContent`.
- Ensured score updates only happen during valid merges.
- Rewrote `randomFillNumber()` to rely on new `Cell` interface.

### `GameBoard.java`
- Refactored `randomFillNumber()` to use updated `Cell` methods.
- Improved consistency in tile generation logic.

## Unexpected Problems

- **Score Issue**: Initially, the score incremented incorrectly, which was resolved by updating the scoring logic.
- Unused Code Discovery: Initially believed `Account.java` was already used in the game. After analysis, determined it was never integrated.
- **UI-State Management**: Managing UI (e.g., `Text` and `Color`) externally became error-prone. This was resolved by encapsulating all visual state inside `Cell.java`.