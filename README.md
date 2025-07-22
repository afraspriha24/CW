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

## Implemented but Not Working Properly

## Features Not Implemented

## New Java Classes

`AccountManager.java`
- Manages creation and lookup of Account objects.
- Isolates static list from Account class for better structure.
 
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
- **Cell.java**: Modified `adder` method to fix the scoring issue. It now returns merged value for score updates.
- **GameScene.java**: Removed `sumCellNumbersToScore`, updated `moveHorizontally` and `moveVertically` to handle score updates, and adjusted game loop.

## Unexpected Problems

- **Score Issue**: Initially, the score incremented incorrectly, which was resolved by updating the scoring logic.
- Unused Code Discovery: Initially believed `Account.java` was already used in the game. After analysis, determined it was never integrated.