import React, {Component} from 'react'
import {Alert, Button, Col, Glyphicon, Row, Well} from 'react-bootstrap'
import {Route} from 'react-router-dom'

import Board from '../board/Board'
import PlayerColor from '../gameconfig/PlayerColor'
import {STATE_DESCS} from '../State'

import PlayerInfo from './PlayerInfo'

import SockJS from 'sockjs-client'
import serverUrl from '../server'

export default class GamePage extends Component {
  static route = <Route exact path="/games/:gameId" component={GamePage}/>

  constructor() {
    super()

    this.state = {
      game: undefined,
      status: undefined
    }
  }

  render() {
    const {game, state} = this.state

    if (!game) return 'Loading...'

    const {myColor} = game
    const allowMove = myColor === PlayerColor.P1 || myColor === PlayerColor.P2

    return (
      <div>
        <Row>
          <Col md={8}>
            <Board
              ref={r => this.board =r}
              sen={Board.startingSEN}
              lastMove=""

              showCoords={true}
              showLegalMoves={true}
              showLastMove={true}
              viewAsBlackPlayer={true}
              allowMove={allowMove}
              onMove={this.onThisBoardMove.bind(this)}
            />
          </Col>

          <Col md={4}>
            <Alert>{STATE_DESCS[state]}</Alert>

            <PlayerInfo upper={true} ijoined={game} />

            <PlayerInfo upper={false} ijoined={game} />

            <Button bsStyle="primary">
              <Glyphicon glyph="remove-circle" />{' '}
              Resign
            </Button>{' '}

            <Button bsStyle="primary">
              <Glyphicon glyph="arrow-left" />{' '}
              Undo
            </Button>{' '}

            <Button bsStyle="primary">
              <Glyphicon glyph="education" />{' '}
              Hint
            </Button>

            <br />
            <br />

            <Well bsSize="small">
              Game moves will be displayed here
            </Well>
          </Col>
        </Row>
      </div>
    )
  }

  componentDidMount() {
    this.sock = new SockJS(serverUrl('/api/sockjs/play'))

    this.sock.onopen = () => {
      this.sock.send(window.$me.encryptedUsername + ' ' + this.props.match.params.gameId)
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

  handleServerMsg(msg) {
    switch (msg.type) {
      case 'GameNotFound': {
        break
      }

      case 'IJoined': {
        this.setState({game: msg, state: msg.state})

        const {moves} = msg
        for (const move of moves) this.board.move(move)

        break
      }

      case 'Move': {
        const {move} = msg
        if (move) this.board.move(move)
        break
      }

      case 'StateChanged': {
        const {state} = msg
        this.setState({state})
        break
      }

      default:
    }
  }

  setBoardOption(key, value) {
    const {boardOptions} = this.state
    boardOptions[key] = value
    this.setState({boardOptions})
  }

  onThisBoardMove(move) {
    this.sock.send(JSON.stringify({type: 'Move', move}))
  }
}
