import React from 'react'
import {Link} from 'react-router-dom'
import {Col} from 'react-bootstrap'

import Board from '../board/Board'
import PlayerColor from '../gameconfig/PlayerColor'

export default function EndedGame({game}) {
  const {id, fen, creatorId, creatorColor, creatorPoint, opponentId, opponentPoint} = game
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

      <div className="gamelist-board">
        <Link to={`/games/${id}`}>
          <Board
            sen={fen}

            showCoords={false}
            showLegalMoves={false}
            showLastMove={true}
            viewAsBlackPlayer={false}
            allowMove={false}
          />
        </Link>
      </div>

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
