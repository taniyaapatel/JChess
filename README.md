# ♟️ JavaFX Chess Project

A feature-rich, graphical **Chess Application** built with **JavaFX** and **Java 21 / Maven**. This desktop application features interactive piece movement, custom high-resolution piece graphics, real-time board rotation after every move, check & checkmate validation, stalemate handling, en passant, and castling rules.

---

## 🌟 Key Features

- **🎨 Graphical UI**: Built using JavaFX `Scene`, `Group`, `Pane`, and custom high-resolution PNG graphics.
- **🔄 Auto-Flipping Board**: Automatically flips the perspective after each turn so the active player always plays from the bottom view.
- **👑 Special Move Support**:
  - **Castling**: Handles both Kingside (`O-O`) and Queenside (`O-O-O`) castling with automatic Rook positioning.
  - **En Passant**: Accurately tracks pawn double-step moves and enables En Passant capture.
  - **Pawn Promotion**: Automatically promotes pawns reaching the opposite 8th rank.
- **⚡ Check & Checkmate Detection**: Real-time validation preventing illegal moves while in check, detecting checkmate and stalemate conditions.
- **🎯 Click-to-Move Controls**: Visual highlight of the active square and valid destination targets.

---

## 🛠️ Prerequisites & Technology Stack

- **Java Development Kit (JDK)**: JDK 11 or higher (Tested with **OpenJDK 21 LTS**).
- **Build Tool**: Apache Maven 3.6+ with OpenJFX plugin support.
- **JavaFX Libraries**: `javafx-controls` (v13+) and `javafx-fxml` (v13+).

---

## 🚀 How to Run the Project

### Option 1: Quick Launch (Double-Click or Command Line)
Run the convenience script from the project root folder:
```powershell
.\run.bat
```

---

### Option 2: Run via Maven (Standard CLI)
If Maven (`mvn`) is installed on your system PATH:

1. Open your terminal / command prompt.
2. Navigate to the `demo` directory:
   ```bash
   cd demo
   ```
3. Run the JavaFX Maven target:
   ```bash
   mvn javafx:run
   ```

---

### Option 3: Run using Included Portable Maven
If Maven is not installed in system environment variables, use the included portable Maven setup:

1. Navigate to the `demo` folder:
   ```powershell
   cd "f:\taniya chess java\new chess\Chess\demo"
   ```
2. Execute via the portable `mvn.cmd`:
   ```powershell
   & "..\..\maven\apache-maven-3.9.9\bin\mvn.cmd" javafx:run
   ```

---

## 📁 Project Structure

```
Chess/
├── run.bat                          # One-click launcher script
├── README.md                        # Documentation & setup instructions
├── demo/                            # Maven project root
│   ├── pom.xml                      # Maven configuration & JavaFX dependencies
│   └── src/
│       └── main/
│           ├── java/com/example/    # Java Source Code
│           │   ├── App.java         # Main JavaFX application & game UI logic
│           │   ├── Piece.java       # Piece model & movement rules
│           │   ├── Location.java    # Coordinate tracking helper
│           │   ├── PrimaryController.java
│           │   └── SecondaryController.java
│           └── resources/com/example/PiecePics/
│               ├── whitePawn.png, whiteKing.png, whiteQueen.png...
│               └── blackPawn.png, blackKing.png, blackQueen.png...
```

---

## 🎮 How to Play

1. **Start Game**: Launching the app initializes a standard 8x8 chess board with White moving first.
2. **Select Piece**: Click on any piece of your color. The selected tile highlights in **bright green**.
3. **Move Piece**: Click on a destination square. If valid, the piece moves and the board automatically rotates for the opponent's turn.
4. **Win Condition**: The terminal will print checkmate or stalemate notifications when game-ending conditions are met.
