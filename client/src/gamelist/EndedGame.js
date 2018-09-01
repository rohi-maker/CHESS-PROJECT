import React from 'react'
import {Link} from 'react-router-dom'
import {Col} from 'react-bootstrap'

import Board from '../board/Board'
import PlayerColor from '../gameconfig/PlayerColor'

export default function EndedGame({game}) {
  const {id, fen, creatorId, creatorColor, creatorPoint, opponentId, opponentPoint} = game
  const [blackUsername, redUsername, blackPoint, redPoint] = creatorColor === PlayerColor.BLACK
      ? [creatorId, opponentId, creatorPoint, opponentPoint]
      : [opponentId, creatorId, opponentPoint, creatorPoint]

  return (
    <Col key={id} md={6}>
      <div className="gamelist-black">
        <Link to={`/games/${id}`}>
          {blackUsername}
          {blackPoint > 0 && <React.Fragment> ({blackPoint})</React.Fragment>}
        </Link>
      </div>

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

      <div className="gamelist-red">
        <Link to={`/games/${id}`}>
          {redUsername}
          {redPoint > 0 && <React.Fragment> ({redPoint})</React.Fragment>}
        </Link>
      </div>
    </Col>
  )
}
