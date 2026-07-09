# ♚ Java Chess Engine

A complete two-player chess game built with **pure Java** and a **browser-based UI**, designed for educational purposes, resume showcase, and technical interview discussions.

---

## 🎯 Project Overview

This project demonstrates strong **Object-Oriented Programming** skills, **clean architecture**, and comprehensive **chess rule implementation** — all without any external frameworks or libraries.

| Aspect | Details |
|--------|---------|
| **Backend** | Pure Java (JDK 8+) |
| **Frontend** | HTML, CSS, Vanilla JavaScript |
| **Server** | Built-in `com.sun.net.httpserver` |
| **Architecture** | MVC-inspired separation |
| **Dependencies** | Zero external dependencies |
| **Deployment** | Localhost only |

---

## ✨ Features

### Chess Rules
- ✅ Standard 8×8 board with correct initial setup
- ✅ All six piece types with full movement validation
- ✅ Turn-based gameplay
- ✅ Piece capture
- ✅ Check detection & notification
- ✅ Checkmate detection
- ✅ Stalemate detection
- ✅ **Castling** (kingside & queenside)
- ✅ **En passant** capture
- ✅ **Pawn promotion** with piece selection
- ✅ Illegal move prevention (king safety)

### User Interface
- ✅ Interactive chessboard with click-to-move
- ✅ Valid move highlighting (green dots)
- ✅ Capture square highlighting (red)
- ✅ Selected piece highlighting (blue)
- ✅ Last move indicators
- ✅ King check animation (pulsing red)
- ✅ Turn indicator with color dot
- ✅ Captured pieces display
- ✅ Promotion piece selection modal
- ✅ Move counter
- ✅ Restart game button
- ✅ Responsive design (mobile-friendly)

---

## 🏗️ Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────┐
│              Browser (View)             │
│   HTML + CSS + JavaScript               │
│   - Board rendering                     │
│   - User interaction                    │
│   - API communication                   │
└──────────────┬──────────────────────────┘
               │ HTTP (localhost:8080)
┌──────────────▼──────────────────────────┐
│          ChessServer (View Layer)       │
│   com.sun.net.httpserver.HttpServer     │
│   - Static file serving                │
│   - REST-like JSON API                  │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      Game + MoveValidator (Controller)  │
│   - Turn management                    │
│   - Move execution & validation        │
│   - Game state management              │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│       Board + Pieces (Model)            │
│   - Piece[8][8] board representation   │
│   - Piece hierarchy (inheritance)      │
│   - Move generation (polymorphism)     │
└─────────────────────────────────────────┘
```

### Class Diagram

```
                    ┌──────────────┐
                    │  «abstract»  │
                    │    Piece     │
                    ├──────────────┤
                    │ -color       │
                    │ -type        │
                    │ -hasMoved    │
                    ├──────────────┤
                    │ +getPossible │
                    │  Moves()    │
                    │ +getAttacked │
                    │  Squares()  │
                    │ +copy()      │
                    └──────┬───────┘
           ┌───────┬───────┼───────┬───────┬───────┐
           ▼       ▼       ▼       ▼       ▼       ▼
        ┌──────┐┌──────┐┌──────┐┌──────┐┌──────┐┌──────┐
        │ Pawn ││ Rook ││Knight││Bishop││Queen ││ King │
        └──────┘└──────┘└──────┘└──────┘└──────┘└──────┘

  ┌───────────┐     ┌───────────────┐     ┌──────────┐
  │   Board   │◄────│     Game      │────►│MoveValid.│
  ├───────────┤     ├───────────────┤     ├──────────┤
  │Piece[8][8]│     │-board         │     │+getLegal  │
  │+getPiece()│     │-players[2]    │     │ Moves()  │
  │+setPiece()│     │-currentTurn   │     │+isInCheck│
  │+findKing()│     │-gameState     │     │+isCheck  │
  │+deepCopy()│     │-moveHistory   │     │ mate()   │
  └───────────┘     │+makeMove()    │     │+isStale  │
                    │+promotePawn() │     │ mate()   │
  ┌───────────┐     │+restart()     │     └──────────┘
  │  Player   │     │+toJson()      │
  ├───────────┤     └───────────────┘
  │-name      │
  │-color     │     ┌───────────────┐
  └───────────┘     │ ChessServer   │
                    ├───────────────┤
  ┌───────────┐     │+start()       │
  │ Position  │     │-handleState() │
  ├───────────┤     │-handleMove()  │
  │-row       │     │-handlePromote│
  │-col       │     └───────────────┘
  └───────────┘
```

### Package Structure

```
src/com/chess/
├── model/                  # Data & domain objects
│   ├── Color.java          # WHITE, BLACK enum
│   ├── PieceType.java      # PAWN..KING enum with Unicode symbols
│   ├── GameState.java      # ACTIVE, CHECK, CHECKMATE, etc.
│   ├── Position.java       # Immutable (row, col)
│   ├── Cell.java           # Position + optional Piece
│   ├── Move.java           # From, To, MoveType, captures
│   ├── Player.java         # Name + Color
│   ├── Board.java          # Piece[8][8] with operations
│   └── piece/
│       ├── Piece.java      # Abstract base class
│       ├── Pawn.java       # Forward, capture, en passant
│       ├── Rook.java       # Horizontal/vertical sliding
│       ├── Knight.java     # L-shaped jumping
│       ├── Bishop.java     # Diagonal sliding
│       ├── Queen.java      # Combined rook + bishop
│       └── King.java       # Single-step + castling
├── controller/
│   ├── MoveValidator.java  # Legal move filtering, check detection
│   └── Game.java           # Game lifecycle controller
├── view/
│   └── ChessServer.java    # HTTP server + JSON API
└── Main.java               # Entry point

public/                     # Frontend assets
├── index.html
├── style.css
└── script.js
```

---

## 🎨 Design Decisions

| Decision | Rationale |
|----------|-----------|
| **Piece[8][8] array** | O(1) access, fixed board size, simple to explain |
| **Abstract Piece class** | Demonstrates inheritance & polymorphism |
| **Separate MoveValidator** | Single Responsibility Principle |
| **Immutable Position** | Thread safety, prevents bugs |
| **Manual JSON serialization** | Zero external dependencies |
| **Built-in HttpServer** | JDK-included, no Spring/Servlet needed |
| **No database** | In-memory state suits a local two-player game |
| **Pseudo-legal + filter pattern** | Clean separation of move generation vs. validation |

---

## 🚀 How to Run

### Prerequisites
- **Java JDK 8+** installed and `javac`/`java` on your PATH

### Quick Start (Windows)
```bash
# Double-click run.bat, or from terminal:
cd "f:\taniya chess java"
run.bat
```

### Manual Build & Run
```bash
# 1. Compile
javac -d out -sourcepath src src/com/chess/Main.java

# 2. Run
java -cp out com.chess.Main

# 3. Open browser
# Navigate to http://localhost:8080
```

### Custom Port
```bash
java -cp out com.chess.Main 3000
```

---

## 🎮 How to Play

1. Open `http://localhost:8080` in your browser
2. **Click** a piece to select it (valid moves appear as green dots)
3. **Click** a highlighted square to make the move
4. Players alternate turns (White → Black → White → ...)
5. The game detects check, checkmate, and stalemate automatically
6. Click **↺ New Game** to restart

---

## 📄 API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/state` | Current game state (JSON) |
| POST | `/api/move` | Make a move `{fromRow, fromCol, toRow, toCol}` |
| POST | `/api/promote` | Promote pawn `{pieceType: "QUEEN"}` |
| POST | `/api/restart` | Reset to initial state |
| GET | `/api/valid-moves?row=R&col=C` | Legal moves for piece |

---

## 📚 Further Reading

- [INTERVIEW_GUIDE.md](INTERVIEW_GUIDE.md) — 130+ interview Q&A covering OOP, design, coding, and HR topics

---

*Built with ♟️ by Taniya — Pure Java, Zero Dependencies*
