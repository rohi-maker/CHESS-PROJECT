import React, {Component} from 'react'
import {Button, ButtonToolbar} from 'react-bootstrap'

import Helper from './Helper.js'
import {Position, GameGenerator, MoveData} from 'synergychess-engine'

import './Board.css'

const COORDS_SIZE = 25

const moveToString = (move) => {
  const moveData = new MoveData()

  moveData.init(
    move.from,
    move.to,
    move.rookPlacement ? move.rookPlacement : '',
    move.kingChoice ? move.kingChoice : '',
    move.promotion ? move.promotion.toLowerCase() : ''
  )

  return moveData.toString()
}

export default class Board extends Component {
  static startingSEN = 'r1n1bkqb1n1r/2p6p2/1prnbqkbnrp1/pppppppppppp/12/12/12/12/PPPPPPPPPPPP/1PRNBQKBNRP1/2P6P2/R1N1BKQB1N1R w KQkq KQkq - 0 0'

  constructor(props) {
    super(props)

    this.position = Position()
    this.position.setSEN(props.sen)
    this.kingChoice = ''
    this.kingRemoveChoices = []
    this.castling = false

    this.state = {
      sen: this.position.senString,

      showCoords: props.showCoords,
      showLegalMoves: props.showLegalMoves,
      showLastMove: props.showLastMove,
      viewAsBlackPlayer: props.viewAsBlackPlayer,

      boardSize: 500,

      validMoves: [],
      currentMove: '',
      lastMove: [],
      isPromoting: false,
      value: ''
    }
  }

  componentDidMount() {
    this.resize()
    this.bindedResize = this.resize.bind(this)
    window.addEventListener('resize', this.bindedResize)
  }

  componentWillUnmount() {
    window.removeEventListener('resize', this.bindedResize)
  }

  resize() {
    const boardSize = Math.min(this.table.parentElement.clientWidth, window.innerHeight) - COORDS_SIZE
    this.setState({boardSize})
  }

  move(moveString) {
    const moveData = new MoveData()
    const move = moveData.getDataFromString(moveString)
    const position = this.position
    let mateData = position.updatePositionFromString(moveString)

    // Check checkmate
    let kingRemoveChoices = []
    mateData = mateData.split(",")
    if (mateData[0] === "true") {
      console.log("True checkmate")
    } else if (mateData[1] === "true") {
      console.log("Checkmate")
      kingRemoveChoices = mateData[3].split(" ")
    } else if (mateData[2] === "true") {
      console.log("Stalemate")
    }

    this.kingRemoveChoices = kingRemoveChoices
    this.position = position

    const lastMove = [move[0], move[1]]
    this.setState({
      sen: this.position.senString,
      currentMove: '',
      validMoves: [],
      lastMove
    })
  }

  clickOnPiece(r, c) {
    if (this.state.isPromoting) return

    let row = r, col = c

    let x, y
    [x, y] = Helper.toPos(this.state.currentMove)
    const board = GameGenerator.loadFromSEN(this.state.sen)

    // Choose king to remove
    if (this.kingRemoveChoices.length > 0 && this.kingChoice === '') {
      if (this.kingRemoveChoices.includes(Helper.toSEN(row, col))) {
        let sen = this.state.position.senString
        sen = Helper.move(sen, row, col, '')
        const position = Position()
        position.setSEN(sen)

        this.kingChoice = Helper.toSEN(row, col)
        this.position = position
        this.setState({
          sen,
          currentMove: '',
          lastMove: '',
          validMoves: []
        })
        return
      } else {
        console.log('Invalid Move')
        return
      }
    }

    // Castling
    if (this.castling) {
      if (!this.state.validMoves.reduce(
        (res, e) => res || (row === e[0] && col === e[1]), false)
      ) {
        console.log("Invlid move")
        return
      }

      let move
      if (this.rookPos.length !== 0 && row === this.rookPos[0] && col === this.rookPos[1]) {
        move = {
          from: this.state.currentMove,
          to: this.kingPlacement
        }
      } else {
        move = {
          castling: true,
          from: this.state.currentMove,
          to: this.kingPlacement,
          rookPlacement: Helper.toSEN(row, col)
        }
      }
      this.move(moveToString(move))
      this.props.onMove(move)
      this.castling = false
      return
    }

    // Castling
    if (  this.state.currentMove !== ''
          && board[x][y].toUpperCase() === 'K'
          && x === row && y !== col) {
      // Inner rank
      if (row === 2 || row === 9) {
        if (this.state.validMoves.reduce((res, e) => res || (e[0] === row && e[1] === col), false)) {
          let move = {
            from: this.state.currentMove,
            to: Helper.toSEN(row, col)
          }

          this.move(moveToString(move))
          this.props.onMove(move)
          return
        }
      } else {
        // Outer rank
        board[row][col] = board[x][y]
        board[x][y] = ''
        let sen = this.state.sen
        sen = Helper.move(sen, x, y, '')
        sen = Helper.move(sen, row, col, board[row][col])

        const validMoves = []

        for (let i = Math.min(y, col); i <= Math.max(y, col); i++) {
          if (i !== col) {
            validMoves.push([row, i])
          }
        }

        if (col - y === 1) {
          this.rookPos = [row, 11]
          validMoves.push([row, 11])
        } else if (col - y === -1) {
          this.rookPos = [row, 0]
          validMoves.push([row, 0])
        } else {
          this.rookPos = []
        }

        this.castling = true
        this.kingPlacement = Helper.toSEN(row, col)
        this.setState({
          validMoves,
          sen
        })
        return
      }
    }

    let validMoves = this.state.validMoves
    if (validMoves.reduce((res, e) => res || (e[0] === row && e[1] === col), false)) {
      const move = {
        from: this.state.currentMove,
        to: Helper.toSEN(row, col)
      }

      if (this.kingChoice && this.kingChoice !== '') {
        move.kingChoice = this.kingChoice
      }

      if ((board[x][y].toUpperCase() === 'P' || board[x][y].toUpperCase() === 'I')
          && ((Helper.getTeam(board[x][y]) === 'white' && row === 11)
              || (Helper.getTeam(board[x][y]) === 'black' && row === 0)
          )
          && (Helper.getAvailablePieces(this.state.sen, this.position.getTeamToMove).length != 0)
      ) {
        if (this.state.value === '') {
          this.row = row
          this.col = col
          this.setState({isPromoting: true})
          return
        }
        move.promotion = this.state.value
      }

      console.log(moveToString(move))
      this.move(moveToString(move))
      this.props.onMove(move)
      return
    }

    validMoves = this.position.validMoves(Helper.toSEN(row, col))
    this.setState({
      validMoves: validMoves.map(e => Helper.toPos(e)),
      currentMove: Helper.toSEN(row, col)
    })
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const result = {}
    for (const prop in nextProps) {
      if (nextProps[prop] !== prevState[prop]) {
        result[prop] = nextProps[prop]
      }
    }

    result['validMoves'] = []

    return result
  }

  getPromotionButtons() {
    if (this.state.isPromoting) {
      const availablePieces = Helper.getAvailablePieces(this.state.sen, this.position.getTeamToMove)

      return <ButtonToolbar>
        {availablePieces.map(e =>
          <Button
            key={Helper.getPieceName(e)}
            bsStyle={this.state.value === Helper.getPieceName(e) ? 'success' : 'default'}
            onClick={f => {
              this.setState({
                value: e,
                isPromoting: false
              }, () => {
                this.clickOnPiece(this.row, this.col)
              })
            }}
          >
            {Helper.getImage(e)}
          </Button>
        )}
      </ButtonToolbar>
    } else {
      return <div />
    }
  }

  getPosAsBlackPlayer(position) {
    const pos = (typeof position === 'string') ? Helper.toPos(position) : position.slice()

    if (pos.length === 0) {
      return []
    }

    const p = pos
    p[0] = this.getLineAsBlackPlayer(p[0])
    p[1] = 11 - this.getLineAsBlackPlayer(p[1])
    return p
  }

  getLineAsBlackPlayer(row) {
    let r = row
    if (!this.state.viewAsBlackPlayer) {
      r = 11 - r
    }
    return r
  }

  getCellClassName(i, j, validMoves, currentMove, lastMove, sen) {
    const color = (i + j) % 2 === 0 ? 'black' : 'white'

    const hilightLegal =
      this.state.showLegalMoves
      && validMoves.length > 0
      && validMoves.reduce((res, e) => res || (i === e[0] && j === e[1]), false)

    const hilightCurrentMove =
      i === currentMove[0] && j === currentMove[1]
      && !this.castling
      && Helper.getTeam(sen) === this.position.getTeamToMove

    const hilightLastMove =
      this.state.showLastMove
      && lastMove.length > 0
      && lastMove.reduce((res, e) => res || (i === e[0] && j === e[1]), false)

    return color +
      (hilightLegal ? ' highlight highlight-legal' : '') +
      (hilightCurrentMove ? ' highlight highlight-current-move' : '') +
      (hilightLastMove ? ' highlight highlight-last-move' : '')
  }

  render() {
    let board = GameGenerator.loadFromSEN(this.state.sen)
    const validMoves = this.state.validMoves.map(e => this.getPosAsBlackPlayer(e))
    const currentMove = this.getPosAsBlackPlayer(this.state.currentMove)
    const lastMove = this.state.lastMove.map(e => this.getPosAsBlackPlayer(e))

    const pieceSize = this.state.boardSize / 12

    if (!this.state.viewAsBlackPlayer) {
      board = board.reverse()
    } else {
      board = board.map(e => e.reverse())
    }

    return (
      <div>
        {this.getPromotionButtons()}

        <table className="board" ref={r => this.table = r}>
          <tbody>
            {board.map((e, i) =>
              <tr key={i}>
                {e.map((sen, j) =>
                  <td
                    key={j}
                    className={this.getCellClassName(i, j, validMoves, currentMove, lastMove, sen)}

                    style={{
                      width: pieceSize,
                      height: pieceSize
                    }}

                    id={String.fromCharCode(65 + i) + j}
                    onClick={(e) => (this.state.allowMove)
                      ? this.clickOnPiece(
                          this.getLineAsBlackPlayer(i),
                          11 - this.getLineAsBlackPlayer(j)
                      )
                      : {}
                    }
                  >
                    {Helper.getImage(sen)}
                  </td>
                )}

                {/* Rank name */}
                {this.state.showCoords && <td className="rowName"> {this.getLineAsBlackPlayer(i) + 1} </td>}
              </tr>
            )}
          </tbody>

          {/* File name */}
          {(this.state.showCoords) &&
            <tfoot>
              <tr>
                {board.map((e, i) =>
                  <td key={i} className="colName">
                    {String.fromCharCode('A'.charCodeAt(0) + this.getLineAsBlackPlayer(11 - i))}
                  </td>
                )}
              </tr>
            </tfoot>
          }
        </table>
      </div>
    )
  }
}