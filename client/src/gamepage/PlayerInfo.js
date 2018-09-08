import React, {Component} from 'react'
import {Link} from 'react-router-dom'
import {Well} from 'react-bootstrap'

import Clock from './Clock'
import PlayerColor from '../gameconfig/PlayerColor'
import PiecesCaptured from './PiecesCaptured'
import AdvantagePoint from './AdvantagePoint'

const getColor = (iJoined, upper) => {
  const {creatorColor, creatorId, creatorPoint, opponentId, opponentPoint} = iJoined
  const opponentColor = 1 - creatorColor

  const iAmPlayer =
    window.$me.username === creatorId ||
    window.$me.username === opponentId

  const creatorIsUpper = iAmPlayer
    ? (window.$me.username !== creatorId)
    : creatorColor === PlayerColor.BLACK

  const [color, username, point] = upper
    ? (creatorIsUpper ? [creatorColor, creatorId, creatorPoint] : [opponentColor, opponentId, opponentPoint])
    : (creatorIsUpper ? [opponentColor, opponentId, opponentPoint] : [creatorColor, creatorId, creatorPoint])

  return {color, username, point}
}

export default class PlayerInfo extends Component {
  render() {
    const {iJoined, upper, wPiecesCaptured, bPiecesCaptured} = this.props
    const {color, username, point} = getColor(iJoined, upper)
    const [colorName, myCapturedPieces, enemyCapturedPieces] = color === PlayerColor.BLACK ?
      ['Black', bPiecesCaptured, wPiecesCaptured] :
      ['White', wPiecesCaptured, bPiecesCaptured]

    return (
      <Well bsSize="small">
        <label>{colorName}:</label>{' '}
        {point > 0 ? <Link to={`/users/${username}`}>{username} {` (${point})`}</Link> : username}
        {username === window.$me.username && <p><b>(Me)</b></p>}

        <Clock ref={r => this.clock = r} iJoined={iJoined} color={color} />

        <PiecesCaptured color={color} piecesCaptured={myCapturedPieces} />

        <AdvantagePoint myCapturedPieces={myCapturedPieces} enemyCapturedPieces={enemyCapturedPieces} />
      </Well>
    )
  }

  onStateGameChanged(gameState) {
    this.clock.onStateGameChanged(gameState)
  }

  onMove(timeSum) {
    this.clock.onMove(timeSum)
  }
}
