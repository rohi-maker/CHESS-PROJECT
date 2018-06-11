import React, {Component} from 'react'
import {Well} from 'react-bootstrap'

import PlayerColor from '../gameconfig/PlayerColor'
import Clock from './Clock'

export default class PlayerInfo extends Component {
  render() {
    const {upper, iJoined} = this.props
    const {creatorId, creatorColor, opponentId} = iJoined
    const opponentColor = 1 - creatorColor

    const iAmPlayer =
      window.$me.username === creatorId ||
      window.$me.username === opponentId

    const creatorIsUpper = iAmPlayer
      ? (window.$me.username !== creatorId)
      : creatorColor === PlayerColor.BLACK

    const [username, color] = upper
      ? (creatorIsUpper ? [creatorId, creatorColor] : [opponentId, opponentColor])
      : (creatorIsUpper ? [opponentId, opponentColor] : [creatorId, creatorColor])

    const colorName = color === PlayerColor.BLACK ? 'Black' : 'White'

    return (
      <Well bsSize="small">
        <label>{colorName}:</label>{' '}
        {username}
        {username === window.$me.username && <p><b>(Me)</b></p>}

        <Clock iJoined={iJoined} color={color} />
      </Well>
    )
  }
}
