import React, {Component} from 'react'
import {Button, ButtonToolbar} from 'react-bootstrap'

import Helper from './Helper.js'
import {Position, GameGenerator, MoveData} from 'synergychess-engine'

import './Board.css'

export default class Board extends Component {
  constructor(props) {
    super(props)

    this.position = Position()
    this.position.setSEN(props.sen)
    this.kingChoice = ""
    this.kingRemoveChoices = []
    this.castling = false

    this.handleChange = this.handleChange.bind(this)

    this.state = {
      sen: this.position.senString,

      showCoords: props.showCoords,
      showLegalMoves: props.showLegalMoves,
      showLastMove: props.showLastMove,
      viewAsBlackPlayer: props.viewAsBlackPlayer,

      validMoves: [],
      currentMove: "",
      lastMove: props.lastMove,
      isPromoting: false,
      value: ""
    }
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

    console.log(this.position.senString)
    const lastMove = [move[0], move[1]]
    this.setState({
      sen: this.position.senString,
      lastMove
    })
  }

  clickOnPiece(row, col) {
    if (this.state.isPromoting) return

    if (!this.state.viewAsBlackPlayer) {
      row = 11 - row
    }

    let x, y
    [x, y] = Helper.toPos(this.state.currentMove)
    const board = GameGenerator.loadFromSEN(this.state.sen)

    // Choose king to remove
    if (this.kingRemoveChoices.length > 0 && this.kingChoice === "") {
      if (this.kingRemoveChoices.includes(Helper.toSEN(row, col))) {
        let sen = this.state.position.senString
        sen = Helper.move(sen, row, col, "")
        const position = Position()
        position.setSEN(sen)

        this.kingChoice = Helper.toSEN(row, col)
        this.position = position
        this.setState({
          sen,
          currentMove: "",
          lastMove: "",
          validMoves: []
        })
        return
      } else {
        console.log("Invalid Move")
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
      const move = {
        castling: true,
        from: this.state.currentMove,
        to: this.state.kingPlacement,
        rookPlacement: Helper.toSEN(row, col)
      }
      this.props.onMove(move)
      return
    }

    // Castling
    if (  this.state.currentMove !== "" 
          && board[x][y].toUpperCase() === "K" 
          && Math.abs(y - col) > 1) {
      board[row][col] = board[x][y]
      board[x][y] = ""
      let sen = this.state.sen
      sen = Helper.move(sen, row, col, board[x][y])
      sen = Helper.move(sen, x, y, "")

      const validMoves = []

      for (let i = Math.min(y, col) + 1; i < Math.max(y, col); i++) {
        validMoves.push([row, i])
      }

      this.castling = true
      this.kingPlacement = Helper.toSEN(row, col)
      this.setState({
        validMoves,
        sen
      })
      return
    }

    if (this.state.validMoves.reduce((res, e) => res || (e[0] === row && e[1] === col), false)) {
      let move = {}
      move = {
        from: this.state.currentMove, 
        to: Helper.toSEN(row, col)
      }

      if (this.kingChoice && this.kingChoice !== "") {
        move.kingChoice = this.kingChoice
      }

      if (board[x][y].toUpperCase() === "P" 
          && ((Helper.getTeam(board[x][y]) === "white" && row === 11) 
              || (Helper.getTeam(board[x][y]) === "black" && row === 0)
          )
      ) {
        if (this.state.value === "") {
          this.row = row
          this.col = col
          this.setState({isPromoting: true})
          return
        }
        move.promotion = this.state.value
      }

      this.props.onMove(move)
      return
    }
    
    const validMoves = this.position.validMoves(Helper.toSEN(row, col))
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
    result["position"] = Position()
    result["position"].setSEN(nextProps.sen)
    result["validMoves"] = []

    return result
  }

  handleChange(value) {
    this.setState({value})
  }

  getPromotionButtons() {
    if (this.state.isPromoting) {
      const availablePieces = Helper.getAvailablePieces(this.state.sen, this.position.getTeamToMove)

      return <ButtonToolbar>
        {availablePieces.map(e =>
          <Button
            key={Helper.getPieceName(e)}
            bsStyle={this.state.value === Helper.getPieceName(e) ? "success" : "default"}
            onClick={f => this.handleChange(Helper.getPieceName(e))}
          >
            {Helper.getImage(e)}
          </Button>
        )}
        <Button
          bsStyle="primary"
          onClick={f => {
            this.state.isPromoting = false
            this.clickOnPiece(this.row, this.col)
          }}
        >
          choose
        </Button>
      </ButtonToolbar>
    } else {
      return <div />
    }
  }

  render() {
    let board = GameGenerator.loadFromSEN(this.state.sen)
    if (!this.state.viewAsBlackPlayer) {
      board = board.reverse()
    }

    return (
      <div>
        {this.getPromotionButtons()}

        <table>
          <tbody>
            {board.map((e, i) =>
              <tr key={i}>
                {e.map((sen, j) =>
                  <td
                    key={j}
                    className={
                      ((i + j) % 2 === 0 ? "black" : "white") + 
                      ((this.state.showLegalMoves 
                          && this.state.validMoves.reduce(
                            (res, e) => res || (i === e[0] && j === e[1]), false))
                        || (Helper.toSEN(i, j) === this.state.currentMove
                          && !this.castling
                          && Helper.getTeam(sen) === this.position.getTeamToMove)
                        || (this.state.showLastMove
                          && this.state.lastMove.includes(Helper.toSEN(i, j))) ?
                        " highlight" :
                        ""
                      )
                    } 
                    id={String.fromCharCode(65 + i) + j}
                    onClick={(e) => (this.state.allowMove) ? this.clickOnPiece(i, j) : {}}
                  >
                    {Helper.getImage(sen)}
                  </td>
                )}

                {/* Rank name */}
                {(this.state.showCoords) ? <td className="rowName"> {i + 1} </td> : <div />}
              </tr>
            )}
          </tbody>

          {/* File name */}
          <tfoot>
            {(this.state.showCoords) ? <tr>
              {board.map((e, i) => 
                <td key={i} className="colName"> 
                  {String.fromCharCode(65 + i)} 
                </td>
              )} 
            </tr> : <div />}
          </tfoot>
        </table>
      </div>
    );
  }
}
