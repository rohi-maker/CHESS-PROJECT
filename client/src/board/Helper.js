import React from 'react'
import {GameGenerator} from 'synergychess-engine'

import bishop_b from './imgs/bishop_b.svg'
import bishop_w from './imgs/bishop_w.svg'
import king_b from './imgs/king_b.svg'
import king_w from './imgs/king_w.svg'
import knight_b from './imgs/knight_b.svg'
import knight_w from './imgs/knight_w.svg'
import pawn_b from './imgs/pawn_b.svg'
import pawn_w from './imgs/pawn_w.svg'
import queen_b from './imgs/queen_b.svg'
import queen_w from './imgs/queen_w.svg'
import rook_b from './imgs/rook_b.svg'
import rook_w from './imgs/rook_w.svg'

const IMGS = {
  bishop_b,
  bishop_w,
  king_b,
  king_w,
  knight_b,
  knight_w,
  pawn_b,
  pawn_w,
  queen_b,
  queen_w,
  rook_b,
  rook_w
}

export default class Helper {
  static getImageFilename(sen) {
    let result = this.getPieceName(sen)
    if (sen === sen.toUpperCase()) {
      result += "_w"
    } else {
      result += "_b"
    }
    return IMGS[result]
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
