import React, {Component} from 'react'
import {Button, Modal} from 'react-bootstrap'
import {Link} from 'react-router-dom'

import PlayerColor from './PlayerColor'
import {isBot} from '../username'
import {USER_GUEST} from '../userType'

export default class GameConfig extends Component {
  static MODE_HUMAN_LISTED             = 0
  static MODE_HUMAN_UNLISTED_INVITE    = 1
  static MODE_HUMAN_UNLISTED_CHALLENGE = 2
  static MODE_HUMAN_VS_BOT             = 3
  static MODE_BOT_VS_BOT               = 4

  /**
   * The first move of both players is limited to Timed.FIRST_MOVE_TIME (see server), excluding:
   * - Games with bot
   * - Invite games
   */
  static isAbusable({creatorId, opponentId, mode}) {
    if (process.env.NODE_ENV !== 'production') return false

    const vsBot = isBot(creatorId) || isBot(opponentId)
    if (vsBot) return false

    const invite = mode === GameConfig.MODE_HUMAN_UNLISTED_INVITE
    return !invite
  }

  constructor() {
    super()

    this.state = {
      limit: 25,
      bonus: 10,
      level: 1,
      color: PlayerColor.WHITE,
      rated: false
    }
  }

  render() {
    const {show, title, withComputer, onOk, onCancel} = this.props
    const {limit, bonus, level, rated} = this.state

    const loggedIn = window.$me.userType !== USER_GUEST

    return (
      <Modal bsSize="small" show={show}>
        <Modal.Header>
          <Modal.Title>{title}</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <label>Time per side: {limit} minutes</label><br />
          <input
            type="range" min="5" max="90" step="5"
            value={limit}
            onChange={e => this.setState({limit: parseInt(e.target.value, 10)})}
          />
          <br />

          <label>Bonus time per move: {bonus} seconds</label><br />
          <input
            type="range" min="0" max="120" step="10"
            value={bonus}
            onChange={e => this.setState({bonus: parseInt(e.target.value, 10)})}
          />
          <br />

          {withComputer &&
            <div>
              <label>Computer level: {level}</label><br />
              <input
                type="range" min="1" max="5"
                value={level}
                onChange={e => this.setState({level: parseInt(e.target.value, 10)})}
              />
              <br />
            </div>
          }

          <label>Color:</label><br />
          {this.playerColorSelect(PlayerColor.WHITE)}{' '}
          {this.playerColorSelect(PlayerColor.BLACK)}{' '}
          {this.playerColorSelect(PlayerColor.RANDOM)}<br />

          {!withComputer &&
            <div>
              <br />
              <label>Rate game:</label>{' '}

              {loggedIn &&
                <input
                  type="checkbox"
                  checked={rated}
                  onChange={e => this.setState({rated: !rated})}
                />
              }

              {!loggedIn &&
                <span>
                  <input
                    type="checkbox"
                    disabled
                  />
                  <br />
                  <Link to="/login">Login to play rated game</Link>
                </span>
              }
            </div>
          }
        </Modal.Body>

        <Modal.Footer>
          <Button bsStyle="primary" onClick={() => onOk(this.state)}>OK</Button>
          <Button onClick={onCancel}>Cancel</Button>
        </Modal.Footer>
      </Modal>
    )
  }

  playerColorSelect(color) {
    return (
      <PlayerColor
        color={color}
        size="large"
        selected={this.state.color === color}
        onSelect={() => this.setState({color})}
      />
    )
  }
}
