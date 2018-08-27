import React, {Component} from 'react'
import {Button, ButtonToolbar, OverlayTrigger, Table, Tooltip} from 'react-bootstrap'
import FontAwesome from 'react-fontawesome'
import {withRouter} from 'react-router-dom'
import SockJS from 'sockjs-client'

import GameConfig from '../gameconfig/GameConfig'
import PlayerColor from '../gameconfig/PlayerColor'

import serverUrl from '../server'
import {withoutGuest} from '../username'

import './Lobby.css'

const tooltip = (msg) => (
  <Tooltip id={msg}>{msg}</Tooltip>
);

class Lobby extends Component {
  constructor() {
    super()

    this.state = {
      configs: [],
      configDlg: {
        show: false,
        title: 'Game config',
        withComputer: false,
        loggedIn: false
      }
    }
  }

  render() {
    return (
      <div>
        <p>Join games:</p>

        <Table className="lobby" bordered hover>
          <thead>
            <tr>
              <th>Player</th>
              <th>Rating</th>
              <th>Time</th>
            </tr>
          </thead>

          <tbody>
            {this.state.configs.map(this.renderGameConfig.bind(this))}
          </tbody>
        </Table>

        <ButtonToolbar>
          <OverlayTrigger placement="top" overlay={tooltip('Create new game')}>
            <Button bsStyle="primary" onClick={() => this.showGameConfigDlg(false)}>
              <FontAwesome name="plus" fixedWidth={true} />
            </Button>
          </OverlayTrigger>

          <OverlayTrigger placement="top" overlay={tooltip('Play with computer')}>
            <Button bsStyle="primary" onClick={() => this.showGameConfigDlg(true)}>
              <FontAwesome name="android" fixedWidth={true} />
            </Button>
          </OverlayTrigger>
        </ButtonToolbar>

        <GameConfig
          {...this.state.configDlg}
          onOk={this.createNewGame.bind(this)}
          onCancel={this.hideGameConfigDlg.bind(this)}
        />
      </div>
    )
  }

  componentWillMount() {
    this.sock = new SockJS(serverUrl('/api/sockjs/lobby'))

    this.sock.onopen = () => {
      this.sock.send(window.$me.encryptedUsername)
    }

    this.sock.onmessage = (e) => {
      const json = e.data
      if (window.$debug) {
        console.log(json)
      }

      const msg = JSON.parse(json)
      this.handleServerMsg(msg)
    }
  }

  componentWillUnmount() {
    this.sock.close()
  }

  renderGameConfig({creatorId, creatorColor, creatorPoint, timeLimitSecs, timeBonusSecs}, idx) {
    const color = <PlayerColor color={creatorColor} />

    const rating = (creatorPoint === 0) ? 'Guest' : creatorPoint

    const tempo =
      timeLimitSecs >= 0
      ? `${timeLimitSecs / 60}'+${timeBonusSecs}"`
      : '∞'


    const className = creatorId === window.$me.username ? 'bg-danger' : ''
    const onClick = () => creatorId === window.$me.username ? this.removeOffer() : this.startGame(creatorId)

    return (
      <tr key={idx} className={className} onClick={onClick}>
        <td>{color} {withoutGuest(creatorId)}</td>
        <td>{rating}</td>
        <td>{tempo}</td>
      </tr>
    )
  }

  handleServerMsg(msg) {
    switch (msg.type) {
      case 'OfferList': {
        const {configs} = msg
        this.setState({configs})
        break
      }

      case 'OfferCreated': {
        const {config} = msg
        const {configs} = this.state
        configs.push(config)
        this.setState({configs})
        break
      }

      case 'OfferRemoved': {
        const {gameId} = msg
        const {configs} = this.state
        const newConfigs = configs.filter(config => config.gameId !== gameId)
        this.setState({configs: newConfigs})
        break
      }

      case 'GameStarted': {
        const {gameId} = msg
        this.props.history.push('/games/' + gameId)
        break
      }

      default:
    }
  }

  showGameConfigDlg(withComputer) {
    const {configDlg} = this.state
    configDlg.show = true
    configDlg.withComputer = withComputer
    configDlg.title = withComputer ? 'Play with computer' : 'Register game'
    this.setState({configDlg})
  }

  hideGameConfigDlg() {
    const {configDlg} = this.state
    configDlg.show = false
    this.setState({configDlg})
  }

  createNewGame({limit, bonus, level, color, rated}) {
    const {withComputer} = this.state.configDlg

    this.sock.send(JSON.stringify({
      type: 'CreateOffer',
      gameName: '',
      mode: withComputer ? GameConfig.MODE_HUMAN_VS_BOT : GameConfig.MODE_HUMAN_LISTED,
      limit: limit * 60,
      bonus,
      level,
      color,
      rated
    }))

    this.hideGameConfigDlg()
  }

  removeOffer() {
    this.sock.send(JSON.stringify({
      type: 'RemoveOffer'
    }))
  }

  startGame(creatorId) {
    this.sock.send(JSON.stringify({
      type: 'StartGame',
      creatorId
    }))
  }
}

export default withRouter(Lobby)
