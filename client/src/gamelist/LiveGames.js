import React, {Component} from 'react'
import {Col, Row} from 'react-bootstrap'
import {Link} from 'react-router-dom'

import Board from '../board/Board'
import PlayerColor from '../gameconfig/PlayerColor'

import SockJS from 'sockjs-client'
import serverUrl from '../server'

import './gamelist.css'

export default class LiveGames extends Component {
  constructor() {
    super()

    // Keys are gameRefs
    this.boards = {}
    this.state = {games: {}}
  }

  render() {
    const {games} = this.state

    return (
      <React.Fragment>
        <p>Live games:</p>

        <Row>
          {Object.keys(games).map(gameRef => this.renderChessBoard(gameRef))}
        </Row>
      </React.Fragment>
    )
  }

  componentWillMount() {
    this.sock = new SockJS(serverUrl('/api/sockjs/live-games'))

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

  handleServerMsg(msg) {
    // IJoined messages are put in array of 3 elements,
    // others are put in array of 2 elements.

    if (msg.length === 3) {
      const [gameRef, gameId, iJoined] = msg
      this.handleIJoinedMsg(gameRef, gameId, iJoined)
    } else {
      const [gameRef, gameMsg] = msg
      this.handleGameMsg(gameRef, gameMsg)
    }
  }

  handleIJoinedMsg(gameRef, gameId, iJoined) {
    const {games} = this.state

    iJoined.id = gameId
    games[gameRef] = iJoined
    this.setState({games})
  }

  handleGameMsg(gameRef, msg) {
    switch (msg.type) {
      case 'Move': {
        const {move} = msg
        this.boards[gameRef].move(move)
        break
      }

      default:
    }
  }

  renderChessBoard(gameRef) {
    const {games} = this.state
    const game = games[gameRef]

    const {id, creatorId, creatorColor, creatorPoint, opponentId, opponentPoint} = game
    const [p2Username, p1Username, p2Point, p1Point] = creatorColor === PlayerColor.BLACK
      ? [creatorId, opponentId, creatorPoint, opponentPoint]
      : [opponentId, creatorId, opponentPoint, creatorPoint]

    return (
      <Col key={id} md={6}>
        <span className="gamelist-p2">
          <Link to={`/games/${id}`}>
            {p2Username}
            {p2Point > 0 && <React.Fragment> ({p2Point})</React.Fragment>}
          </Link>
        </span>

        <Board
          ref={r => this.initBoard(gameRef, game, r)}
          sen={Board.startingSEN}

          showCoords={false}
          showLegalMoves={false}
          showLastMove={true}
          viewAsBlackPlayer={false}
          allowMove={false}
        />

        <span className="gamelist-p1">
          <Link to={`/games/${id}`}>
            {p1Username}
            {p1Point > 0 && <React.Fragment> ({p1Point})</React.Fragment>}
          </Link>
        </span>

        <br />
        <br />
      </Col>
    )
  }

  initBoard(gameRef, game, board) {
    if (!board || this.boards[gameRef]) return

    this.boards[gameRef] = board

    const {moves} = game
    for (let move of moves) board.move(move)
  }
}
