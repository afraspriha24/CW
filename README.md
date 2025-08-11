## 2048 JavaFX Game

A desktop implementation of the 2048 puzzle game built with JavaFX and Maven. Supports multiple board sizes, user profiles, and persistent high scores.

### Tech stack
- **Java**: 17
- **JavaFX**: 20
- **Build tool**: Maven (with Wrapper included)

### Prerequisites
- JDK 17 installed and set as your active Java version
- Internet access for Maven to download dependencies

### Run (recommended)
- Using the Maven Wrapper on Windows:
```bash
mvnw.cmd clean javafx:run
```
- Using the Maven Wrapper on macOS/Linux:
```bash
./mvnw clean javafx:run
```
- If you have Maven installed:
```bash
mvn clean javafx:run
```

This uses the `javafx-maven-plugin` to launch `com.example.demo.Main` with the correct JavaFX modules.

### Build a distributable runtime (no separate JRE required)
Create a custom runtime image with JavaFX bundled via jlink:
```bash
# Windows
mvnw.cmd clean javafx:jlink

# macOS/Linux
./mvnw clean javafx:jlink
```
Run the generated launcher:
- Windows: `target/app/bin/app.bat`
- macOS/Linux: `target/app/bin/app`

### Package jar (advanced)
```bash
mvn clean package
```
The fat runtime is not included in the jar. Running the jar directly requires supplying a JavaFX-enabled module path. Prefer `javafx:run` during development or `javafx:jlink` for distribution.

### Controls and gameplay
- **Move tiles**: Arrow keys (↑ ↓ ← →)
- **Goal**: Combine tiles to reach 2048 (and beyond)
- **Board sizes**: Choose 4×4, 5×5, or 6×6 on the home screen

### Data persistence
Player profiles and last active player are stored in the project root:
- `players.dat` — saved player accounts and scores
- `last_player.txt` — last active profile

To reset local data, close the app and delete these files.

### Project structure
- `src/main/java/com/example/demo` — application code (entry point: `Main`)
- `src/main/resources/com/example/demo` — FXML/resources
- `pom.xml` — Maven build, JavaFX and JUnit config

### Run in IntelliJ IDEA
You can either:
- Use the Maven tool window to run the `javafx:run` goal, or
- Run the `Main` class and ensure JavaFX modules are available (the Maven goal is simpler and recommended).

### Troubleshooting
- If you see JavaFX module errors, ensure you are using **JDK 17** and run via `javafx:run`.
- If the app does not start or shows a blank screen, do a clean build: `mvn clean javafx:run` (or via the Wrapper).

### License
Add your chosen license here.


