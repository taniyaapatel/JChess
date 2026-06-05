/**
 * Chess Engine Frontend Controller
 * Handles board rendering, user interaction, and API communication.
 */

// ==================== State ====================
let gameState = null;
let selectedSquare = null;
let validMoves = [];

// ==================== API ====================

async function fetchState() {
    const res = await fetch('/api/state');
    gameState = await res.json();
    render();
}

async function makeMove(fromRow, fromCol, toRow, toCol) {
    const res = await fetch('/api/move', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fromRow, fromCol, toRow, toCol })
    });
    const data = await res.json();
    gameState = data.state;
    selectedSquare = null;
    validMoves = [];
    render();
    if (data.state.promotionPending) {
        showPromotionModal();
    }
}

async function fetchValidMoves(row, col) {
    const res = await fetch(`/api/valid-moves?row=${row}&col=${col}`);
    const data = await res.json();
    return data.moves || [];
}

async function promotePawn(pieceType) {
    const res = await fetch('/api/promote', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pieceType })
    });
    const data = await res.json();
    gameState = data.state;
    hidePromotionModal();
    render();
}

async function restartGame() {
    const res = await fetch('/api/restart', { method: 'POST' });
    const data = await res.json();
    gameState = data.state;
    selectedSquare = null;
    validMoves = [];
    hidePromotionModal();
    render();
}

// ==================== Rendering ====================

function render() {
    if (!gameState) return;
    renderBoard();
    renderStatus();
    renderCapturedPieces();
    renderMoveCounter();
}

function renderBoard() {
    const boardEl = document.getElementById('chess-board');
    boardEl.innerHTML = '';

    for (let r = 0; r < 8; r++) {
        for (let c = 0; c < 8; c++) {
            const sq = document.createElement('div');
            const isLight = (r + c) % 2 === 0;
            sq.className = `square ${isLight ? 'light' : 'dark'}`;
            sq.id = `sq-${r}-${c}`;
            sq.dataset.row = r;
            sq.dataset.col = c;

            // Last move highlight
            if (gameState.lastMove) {
                const lm = gameState.lastMove;
                if (r === lm.fromRow && c === lm.fromCol) sq.classList.add('last-move-from');
                if (r === lm.toRow && c === lm.toCol) sq.classList.add('last-move-to');
            }

            // Selected square
            if (selectedSquare && selectedSquare.row === r && selectedSquare.col === c) {
                sq.classList.add('selected');
            }

            // Valid move / capture indicators
            const isValidTarget = validMoves.some(m => m.row === r && m.col === c);
            if (isValidTarget) {
                const targetPiece = gameState.board[r][c];
                if (targetPiece) {
                    sq.classList.add('valid-capture');
                } else {
                    sq.classList.add('valid-move');
                }
            }

            // Check highlight on king
            if (gameState.inCheck) {
                const piece = gameState.board[r][c];
                if (piece && piece.type === 'KING' && piece.color === gameState.currentTurn) {
                    sq.classList.add('in-check');
                }
            }

            // Piece rendering
            const piece = gameState.board[r][c];
            if (piece) {
                const pieceEl = document.createElement('span');
                pieceEl.className = `piece ${piece.color === 'WHITE' ? 'white-piece' : 'black-piece'}`;
                pieceEl.textContent = piece.symbol;
                sq.appendChild(pieceEl);
            }

            sq.addEventListener('click', () => onSquareClick(r, c));
            boardEl.appendChild(sq);
        }
    }
}

function renderStatus() {
    const turnDot = document.getElementById('turn-dot');
    const turnText = document.getElementById('turn-text');
    const msgEl = document.getElementById('game-message');

    const isWhite = gameState.currentTurn === 'WHITE';
    turnDot.className = `turn-dot ${isWhite ? 'white' : 'black'}`;
    turnText.textContent = `${isWhite ? "White" : "Black"}'s Turn`;

    msgEl.className = 'game-message';
    switch (gameState.gameState) {
        case 'CHECK':
            msgEl.textContent = '⚠ Check!';
            msgEl.classList.add('check');
            break;
        case 'CHECKMATE':
            const winner = isWhite ? 'Black' : 'White';
            msgEl.textContent = `♚ Checkmate! ${winner} wins!`;
            msgEl.classList.add('checkmate');
            turnText.textContent = `${winner} Wins`;
            break;
        case 'STALEMATE':
            msgEl.textContent = '½ Stalemate — Draw';
            msgEl.classList.add('stalemate');
            turnText.textContent = 'Game Over';
            break;
        case 'PROMOTION_PENDING':
            msgEl.textContent = 'Choose promotion piece';
            break;
        default:
            msgEl.textContent = '';
    }
}

function renderCapturedPieces() {
    const whiteEl = document.getElementById('captured-by-white');
    const blackEl = document.getElementById('captured-by-black');

    whiteEl.innerHTML = gameState.capturedByWhite
        .map(p => `<span class="captured-piece">${p.symbol}</span>`).join('');
    blackEl.innerHTML = gameState.capturedByBlack
        .map(p => `<span class="captured-piece">${p.symbol}</span>`).join('');
}

function renderMoveCounter() {
    document.getElementById('move-counter').textContent = `Move: ${gameState.moveCount}`;
}

// ==================== Interaction ====================

async function onSquareClick(row, col) {
    if (!gameState) return;
    if (gameState.gameState === 'CHECKMATE' || gameState.gameState === 'STALEMATE') return;
    if (gameState.promotionPending) return;

    const clickedPiece = gameState.board[row][col];

    // If a square is already selected
    if (selectedSquare) {
        const isValidTarget = validMoves.some(m => m.row === row && m.col === col);

        if (isValidTarget) {
            await makeMove(selectedSquare.row, selectedSquare.col, row, col);
            return;
        }

        // Clicking same square deselects
        if (selectedSquare.row === row && selectedSquare.col === col) {
            selectedSquare = null;
            validMoves = [];
            render();
            return;
        }

        // Clicking another own piece selects it
        if (clickedPiece && clickedPiece.color === gameState.currentTurn) {
            selectedSquare = { row, col };
            validMoves = await fetchValidMoves(row, col);
            render();
            return;
        }

        // Clicking invalid square deselects
        selectedSquare = null;
        validMoves = [];
        render();
        return;
    }

    // No square selected — select a piece of current player
    if (clickedPiece && clickedPiece.color === gameState.currentTurn) {
        selectedSquare = { row, col };
        validMoves = await fetchValidMoves(row, col);
        render();
    }
}

// ==================== Promotion Modal ====================

function showPromotionModal() {
    const modal = document.getElementById('promotion-modal');
    const choices = document.getElementById('promotion-choices');
    const isWhite = gameState.currentTurn === 'WHITE';

    // Note: after a move causing promotion, the turn hasn't switched yet during PROMOTION_PENDING
    // The color of the promoted pawn is the OPPOSITE of currentTurn after the move was made
    // Actually, promotion happens before turn switch, so we need the color of the moving player
    // Let's determine from the last move's piece
    const lastMove = gameState.lastMove;
    let promoColor = 'WHITE';
    if (lastMove) {
        const piece = gameState.board[lastMove.toRow][lastMove.toCol];
        if (piece) promoColor = piece.color;
    }

    const symbols = promoColor === 'WHITE'
        ? { QUEEN: '♕', ROOK: '♖', BISHOP: '♗', KNIGHT: '♘' }
        : { QUEEN: '♛', ROOK: '♜', BISHOP: '♝', KNIGHT: '♞' };

    choices.innerHTML = '';
    for (const [type, symbol] of Object.entries(symbols)) {
        const btn = document.createElement('button');
        btn.className = 'promotion-btn';
        btn.textContent = symbol;
        btn.title = type;
        btn.addEventListener('click', () => promotePawn(type));
        choices.appendChild(btn);
    }

    modal.style.display = 'flex';
}

function hidePromotionModal() {
    document.getElementById('promotion-modal').style.display = 'none';
}

// ==================== Init ====================
document.addEventListener('DOMContentLoaded', fetchState);
