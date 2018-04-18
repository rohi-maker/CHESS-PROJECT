import React, {Component} from 'react'
import {GameGenerator} from 'synergychess-engine'

export default class Helper extends Component {
  static getImageFilename(sen) {
    let result = "/" + this.getPieceName(sen)
    if (sen === sen.toUpperCase()) {
      result += "_w.svg"
    } else {
      result += "_b.svg"
    }
    return result
  }

  static getPieceName(sen) {
    let result
    switch(sen.toUpperCase()) {
      case "P": result = "pawn"; break
      case "I": result = "pawn"; break
      case "B": result = "bishop"; break
      case "N": result = "knight"; break
      case "R": result = "rook"; break
      case "Q": result = "queen"; break
      case "K": result = "king"; break
      default: break
    }
    return result
  }

  static getImage(sen) {
    if (sen === undefined || sen === null || sen === "") {
      return <div />
    } else {
      return <img 
        src={this.getImageFilename(sen)}
        alt={this.getPieceName(sen)}
      />
    }
  }

  static getTeam(sen) {
    let result = ""
    if (sen === sen.toUpperCase()) {
      result += "white"
    } else {
      result += "black"
    }
    return result
  }

  static senString(board) {
    const result = []

    for (const row of board.reverse()) {
      for (const piece of row) {
        if (piece === "") {
          if (typeof result[result.length - 1] === "number") {
            result[result.length - 1]++
          } else {
            result.push(1)
          }
        } else {
          result.push(piece)
        }
      }
      result.push("/")
    }
    result.pop()

    return result.join("")
  }

  static toSEN(row, col) {
    return String.fromCharCode(col + 'A'.charCodeAt(0)) + (row + 1)
  }

  static toPos(sen) {
    return [parseInt(sen.substr(1, sen.length - 1) - 1, 10), sen.charCodeAt(0) - 65]
  }

  static move(sen, row, col, value) {
    const board = GameGenerator.loadFromSEN(sen)
    board[row][col] = value
    let newSen = sen
    newSen = newSen.split(" ")
    newSen[0] = Helper.senString(board)
    newSen = newSen.join(" ")
    return newSen
  }

  static getAvailablePieces(sen, team) {
    const board = sen.split(" ", 1)[0]
    const pieces = {
      "B": 4,
      "N": 4,
      "R": 4,
      "Q": 2
    }

    const result = []
    for (const piece in pieces) {
      let pieceName = (team === "white") ? piece : piece.toLowercase()
      let count = (board.match(new RegExp(pieceName, "g")) || []).length
      if (count < pieces[piece]) {
        result.push(piece)
      }
    }
    return result
  }
}
