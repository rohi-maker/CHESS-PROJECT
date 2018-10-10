import React from 'react'
import {Well} from 'react-bootstrap'

import PlayerColor from '../gameconfig/PlayerColor'
import PiecesCaptured from '../gamepage/PiecesCaptured'
import AdvantagePoint from '../gamepage/AdvantagePoint'

const isEmpty = (piecesCaptured) => {
  const keys = Object.keys(piecesCaptured)
  return keys.every(key => piecesCaptured[key] === 0)
}

export default function PlayerInfo({color, wPiecesCaptured, bPiecesCaptured}) {
  const [colorName, myCapturedPieces, enemyCapturedPieces] = color === PlayerColor.BLACK ?
    ['Black', bPiecesCaptured, wPiecesCaptured] :
    ['White', wPiecesCaptured, bPiecesCaptured]

  if (isEmpty(myCapturedPieces)) return null

  return (
    <Well bsSize="small">
      <label>{colorName}:</label>
      <br />

      <PiecesCaptured color={color} piecesCaptured={myCapturedPieces} />

      <AdvantagePoint myCapturedPieces={myCapturedPieces} enemyCapturedPieces={enemyCapturedPieces} />
    </Well>
  )
}
