import React, {Component} from 'react'
import {Button, Modal} from 'react-bootstrap'
import {Link} from 'react-router-dom'

import PlayerColor from './PlayerColor'

export default class GameConfig extends Component {
  static MODE_HUMAN_LISTED             = 0
  static MODE_HUMAN_UNLISTED_INVITE    = 1
  static MODE_HUMAN_UNLISTED_CHALLENGE = 2
  static MODE_HUMAN_VS_BOT             = 3
  static MODE_BOT_VS_BOT               = 4

  constructor() {
    super()

    this.state = {
      limit: 5,
      bonus: 10,
      level: 1,
      color: PlayerColor.WHITE,
      rated: false
    }
  }

  render() {
    return (
      <Modal bsSize="small" show={this.props.show}>
        <Modal.Header>
          <Modal.Title>{this.props.title}</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <label>Time per side: {this.state.limit} minutes</label><br />
          <input
            type="range" min="5" max="90" step="5"
            value={this.state.limit}
            onChange={e => this.setState({limit: parseInt(e.target.value, 10)})}
          />
          <br />

          <label>Bonus time per move: {this.state.bonus} seconds</label><br />
          <input
            type="range" min="0" max="120" step="10"
            value={this.state.bonus}
            onChange={e => this.setState({bonus: parseInt(e.target.value, 10)})}
          />
          <br />

          {this.props.withComputer &&
            <div>
              <label>Computer level: {this.state.level}</label><br />
              <input
                type="range" min="1" max="5"
                value={this.state.level}
                onChange={e => this.setState({level: parseInt(e.target.value, 10)})}
              />
              <br />
            </div>
          }

          <label>Color:</label><br />
          {this.playerColorSelect(PlayerColor.WHITE)}{' '}
          {this.playerColorSelect(PlayerColor.BLACK)}{' '}
          {this.playerColorSelect(PlayerColor.RANDOM)}<br />

          {!this.props.withComputer &&
            <div>
              <br />
              <label>Rate game:</label>{' '}

              {this.props.loggedIn &&
                <input
                  type="checkbox"
                  checked={this.state.rated}
                  onChange={e => this.setState({rated: !this.state.rated})}
                />
              }

              {!this.props.loggedIn &&
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
          <Button bsStyle="primary" onClick={() => this.props.onOk(this.state)}>OK</Button>
          <Button onClick={this.props.onCancel}>Cancel</Button>
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
