import React, {Component} from 'react'
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
      <div>
        <p>Live games:</p>

        {Object.keys(games).map(gameRef => this.renderChessBoard(gameRef))}
      </div>
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

    const {moves} = iJoined
    for (const move of moves) this.boards[gameRef].move(move)
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

    const [p1Username, p2Username] = game.creatorColor === PlayerColor.P1
      ? [game.creatorId, game.opponentId]
      : [game.opponentId, game.creatorId]

    return (
      <div key={game.id}>
        <span className="gamelist-p2">
          <Link to={`/games/${game.id}`}>{p2Username}</Link>
        </span>

        <Board
          ref={r => this.boards[gameRef] = r}
          sen={Board.startingSEN}
          lastMove=""

          showCoords={false}
          showLegalMoves={false}
          showLastMove={true}
          viewAsBlackPlayer={false}
          allowMove={false}
        />

        <span className="gamelist-p1">
          <Link to={`/games/${game.id}`}>{p1Username}</Link>
        </span>

        <br />
        <br />
      </div>
    )
  }
}
