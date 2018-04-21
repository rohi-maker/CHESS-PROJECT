import React, {Component} from 'react'
import {Alert, Button, Col, Glyphicon, Row, Well} from 'react-bootstrap'
import {Route} from 'react-router-dom'
import {MoveData} from 'synergychess-engine'

import Board from '../board/Board'
import PlayerColor from '../gameconfig/PlayerColor'
import {STATE_DESCS} from '../State'

import PlayerInfo from './PlayerInfo'

import SockJS from 'sockjs-client'
import serverUrl from '../server'

const moveToString = (move) => {
  const moveData = new MoveData()

  moveData.init(
    move.from,
    move.to,
    move.rookPlacement ? move.rookPlacement : "",
    move.kingChoice ? move.kingChoice : "",
    move.promotion ? move.promotion : ""
  )

  return moveData.toString()
}

export default class GamePage extends Component {
  static route = <Route exact path="/games/:gameId" component={GamePage}/>

  constructor() {
    super()

    this.state = {
      iJoined: undefined,
      state: undefined,
      status: 'Loading...'
    }
  }

  render() {
    const {iJoined, status} = this.state

    if (!iJoined) return status

    const {myColor} = iJoined
    const viewAsBlackPlayer = myColor === PlayerColor.BLACK
    const allowMove = myColor === PlayerColor.WHITE || myColor === PlayerColor.BLACK

    return (
      <div>
        <Row>
          <Col md={8}>
            <Board
              ref={r => this.board = r}
              sen={Board.startingSEN}

              showCoords={true}
              showLegalMoves={true}
              showLastMove={true}
              viewAsBlackPlayer={viewAsBlackPlayer}
              allowMove={allowMove}
              onMove={this.onThisBoardMove.bind(this)}
            />
          </Col>

          <Col md={4}>
            <Alert>{status}</Alert>

            <PlayerInfo upper={true} iJoined={iJoined} />

            <PlayerInfo upper={false} iJoined={iJoined} />

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
        this.setState({status: 'Game not found'})
        break
      }

      case 'IJoined': {
        this.setState(
          {iJoined: msg, state: msg.state, status: STATE_DESCS[msg.state]},
          () => {
            const {moves} = msg
            for (const move of moves) this.board.move(move)
          }
        )

        break
      }

      case 'Move': {
        const {move} = msg
        if (move) this.board.move(move)
        break
      }

      case 'StateChanged': {
        const {state} = msg
        this.setState({state, status: STATE_DESCS[state]})
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
    const moveString = moveToString(move)
    this.board.move(moveString)
    this.sock.send(JSON.stringify({type: 'Move', move: moveString}))
  }
}
