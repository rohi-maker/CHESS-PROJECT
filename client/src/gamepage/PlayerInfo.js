import React, {Component} from 'react'
import {Well} from 'react-bootstrap'

import Clock from './Clock'
import PlayerColor from '../gameconfig/PlayerColor'
import PiecesCaptured from './PiecesCaptured'

const getColor = (iJoined, upper) => {
  const {creatorId, creatorColor, opponentId} = iJoined
  const opponentColor = 1 - creatorColor

  const iAmPlayer =
    window.$me.username === creatorId ||
    window.$me.username === opponentId

  const creatorIsUpper = iAmPlayer
    ? (window.$me.username !== creatorId)
    : creatorColor === PlayerColor.BLACK

  const [color, username] = upper
    ? (creatorIsUpper ? [creatorColor, creatorId] : [opponentColor, opponentId])
    : (creatorIsUpper ? [opponentColor, opponentId] : [creatorColor, creatorId])

  return {color, username}
}

export default class PlayerInfo extends Component {
  render() {
    const {iJoined, upper, wPiecesCaptured, bPiecesCaptured} = this.props
    const {color, username} = getColor(iJoined, upper)
    const [colorName, piecesCaptured] = color === PlayerColor.BLACK ?
      ['Black', bPiecesCaptured] :
      ['White', wPiecesCaptured]

    return (
      <Well bsSize="small">
        <label>{colorName}:</label>{' '}
        {username}
        {username === window.$me.username && <p><b>(Me)</b></p>}

        <Clock ref={r => this.clock = r} iJoined={iJoined} color={color} />

        <PiecesCaptured color={color} piecesCaptured={piecesCaptured} />
      </Well>
    )
  }

  onStateChanged(state) {
    this.clock.onStateChanged(state)
  }

  onMove(timeSum) {
    this.clock.onMove(timeSum)
  }
}
