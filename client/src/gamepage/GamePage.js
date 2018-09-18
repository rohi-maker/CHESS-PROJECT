import React, {Component} from 'react'
import {Alert, Button, Col, Glyphicon, Row, Tab, Tabs} from 'react-bootstrap'
import {Route} from 'react-router-dom'
import {MoveData} from 'synergychess-engine'

import Board from '../board/Board'
import GameConfig from '../gameconfig/GameConfig'
import GameOverDlg from './GameOverDlg'
import PlayerColor from '../gameconfig/PlayerColor'
import State, {STATE_DESCS} from '../State'

import Chat from './Chat'
import MoveHistory from './MoveHistory'
import PlayerInfo from './PlayerInfo'
import TimeConfig from './TimeConfig'

import SockJS from 'sockjs-client'
import serverUrl from '../server'

const fullscreenEnabled =
  document.fullscreenEnabled ||
  document.webkitFullscreenEnabled ||
  document.mozFullScreenEnabled ||
  document.msFullscreenEnabled

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

const CONTAINER_FULLSCREEN_STYLE = {width: '100%', height: '100%'}

export default class GamePage extends Component {
  static route = <Route exact path="/games/:gameId" component={GamePage}/>

  constructor() {
    super()

    this.state = {
      iJoined: undefined,
      state: undefined,
      status: 'Loading...',
      flipBoard: false,
      notations: [],
      wPiecesCaptured: {},
      bPiecesCaptured: {}
    }
  }

  render() {
    const {iJoined, state, status, flipBoard, notations, wPiecesCaptured, bPiecesCaptured} = this.state

    if (!iJoined) return status

    const gameOver = state !== State.ALIVE && state !== State.NOT_STARTED

    const {myColor, timeLimitSecs, timeBonusSecs} = iJoined
    const viewAsBlackPlayer = myColor === PlayerColor.BLACK
    const iAmPlayer = iJoined.myColor === PlayerColor.WHITE || iJoined.myColor === PlayerColor.BLACK
    const allowMove = iAmPlayer && state === State.ALIVE

    return (
      <div ref={r => this.container = r} style={CONTAINER_FULLSCREEN_STYLE}>
        <Row>
          <Col md={8}>
            <Board
              ref={r => this.board = r}
              sen={Board.startingSEN}

              showCoords={true}
              showLegalMoves={true}
              showLastMove={true}
              viewAsBlackPlayer={flipBoard ? !viewAsBlackPlayer : viewAsBlackPlayer}
              allowMove={allowMove}
              myColor={iJoined.myColor}
              onMove={this.onThisBoardMove.bind(this)}
            />

            <Button bsStyle="primary" onClick={() => this.setState({flipBoard: !flipBoard})}>
              <Glyphicon glyph="refresh" />{' '}
              Flip board
            </Button>
          </Col>

          <Col md={4}>
            <Alert>{status}</Alert>

            {notations.length < 2 && GameConfig.isAbusable(iJoined) &&
              <Alert>Game will be automatically canceled if first moves are not made within 30"</Alert>
            }

            <TimeConfig timeLimitSecs={timeLimitSecs} timeBonusSecs={timeBonusSecs} />

            <PlayerInfo
              ref={r => this.upperPlayerInfo = r}
              upper={true}
              iJoined={iJoined}
              wPiecesCaptured={wPiecesCaptured}
              bPiecesCaptured={bPiecesCaptured}
            />

            <PlayerInfo
              ref={r => this.lowerPlayerInfo = r}
              upper={false}
              iJoined={iJoined}
              wPiecesCaptured={wPiecesCaptured}
              bPiecesCaptured={bPiecesCaptured}
            />

            {fullscreenEnabled &&
              <React.Fragment>
                <Button bsStyle="primary" onClick={this.toggleFullscreen}>
                  <Glyphicon glyph="fullscreen" />{' '}
                  Fullscreen
                </Button>{' '}
              </React.Fragment>
            }

            {iAmPlayer && state === State.ALIVE &&
              <Button bsStyle="primary" onClick={this.quit}>
                <Glyphicon glyph="remove-circle" />{' '}
                {notations.length < 2 ? 'Cancel' : 'Resign'}
              </Button>
            }

            <br />
            <br />

            <Tabs id="Chat" defaultActiveKey={1}>
              <Tab eventKey={1} title="Chat">
                <Chat ref={r => this.chat = r} sendMsg={this.sendChatMsg} />
              </Tab>

              <Tab eventKey={2} title="Moves">
                <MoveHistory notations={notations} />
              </Tab>
            </Tabs>
          </Col>
        </Row>

        {gameOver && <GameOverDlg status={status} />}
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
            for (const move of moves) {
              const [notation, wPiecesCaptured, bPiecesCaptured] = this.board.move(move)
              this.appendMoveHistory(notation)
              this.setState({wPiecesCaptured, bPiecesCaptured})
            }
          }
        )

        break
      }

      case 'Move': {
        const {move, timeSum} = msg

        // When self move is echoed back, there's only timeSum info
        if (move) {
          const [notation, wPiecesCaptured, bPiecesCaptured] = this.board.move(move)
          this.appendMoveHistory(notation)
          this.setState({wPiecesCaptured, bPiecesCaptured})
        }

        this.upperPlayerInfo.onMove(timeSum)
        this.lowerPlayerInfo.onMove(timeSum)

        break
      }

      case 'Chat': {
        this.chat.onMsg(msg.username, msg.msg)
        break
      }

      case 'StateChanged': {
        const {state} = msg
        this.setState({state, status: STATE_DESCS[state]})

        this.upperPlayerInfo.onStateGameChanged(state)
        this.lowerPlayerInfo.onStateGameChanged(state)

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

  onThisBoardMove(move, [notation, wPiecesCaptured, bPiecesCaptured]) {
    const moveString = moveToString(move)
    this.sock.send(JSON.stringify({type: 'Move', move: moveString}))

    this.appendMoveHistory(notation)
    this.setState({wPiecesCaptured, bPiecesCaptured})
  }

  sendChatMsg = (msg) => {
    this.sock.send(JSON.stringify({
      type: 'Chat',
      msg
    }))
  }

  toggleFullscreen = () => {
    const fullscreenElement =
      document.fullscreenElement ||
      document.webkitFullscreenElement ||
      document.mozFullScreenElement ||
      document.msFullscreenElement

    if (fullscreenElement) {
      const exitFullscreen =
        document.exitFullscreen ||
        document.webkitExitFullscreen ||
        document.mozCancelFullScreen ||
        document.msExitFullscreen

      if (exitFullscreen) exitFullscreen.call(document)
    } else {
      const requestFullscreen =
        this.container.requestFullscreen ||
        this.container.webkitRequestFullScreen ||
        this.container.mozRequestFullScreen ||
        this.container.msRequestFullscreen
      requestFullscreen.call(this.container)
    }
  }

  quit = () => {
    this.sock.send(JSON.stringify({
      type: this.state.notations.length < 2 ? 'Cancel' : 'Resign'
    }))
  }

  appendMoveHistory(notation) {
    const {notations} = this.state
    notations.push(notation)
    this.setState({notations})
  }
}
