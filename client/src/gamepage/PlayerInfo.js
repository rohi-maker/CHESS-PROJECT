import React, {Component} from 'react'
import {Well} from 'react-bootstrap'

import PlayerColor from '../gameconfig/PlayerColor'

export default class PlayerInfo extends Component {
  render() {
    const {upper, ijoined} = this.props
    const {creatorId, creatorColor, opponentId} = ijoined
    const opponentColor = 1 - creatorColor

    const iAmPlayer =
      window.$me.username === creatorId ||
      window.$me.username === opponentId

    const creatorIsUpper = iAmPlayer
      ? (window.$me.username !== creatorId)
      : creatorColor === PlayerColor.P2

    const [username, color] = upper
      ? (creatorIsUpper ? [creatorId, creatorColor] : [opponentId, opponentColor])
      : (creatorIsUpper ? [opponentId, opponentColor] : [creatorId, creatorColor])

    const colorName = color === PlayerColor.P2 ? 'Player2' : 'Player1'

    return (
      <Well bsSize="small">
        <label>{colorName}:</label>{' '}
        {username}
        {username === window.$me.username && <p><b>(Me)</b></p>}
      </Well>
    )
  }
}
