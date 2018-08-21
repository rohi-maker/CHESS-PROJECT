import React, {Component} from 'react'

import {IMGS} from '../board/Helper'
import PlayerColor from '../gameconfig/PlayerColor'

import './PiecesCaptured.css'

const renderPiece = (color, pieceName, num) => {
  if (num === 0) return null

  const c = color === PlayerColor.BLACK ? 'b' : 'w'
  const src = IMGS[`${pieceName}_${c}`]
  return [
    <img key={pieceName} alt={pieceName} src={src} className="piece-captured" />,
    num
  ]
}

export default class PiecesCaptured extends Component {
  render() {
    const {color, piecesCaptured} = this.props
    const pieceNames = Object.keys(piecesCaptured)
    return pieceNames.map(pieceName =>
      renderPiece(color, pieceName, piecesCaptured[pieceName])
    )
  }
}
