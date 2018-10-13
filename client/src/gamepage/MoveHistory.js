import React, {Component} from 'react'
import {Table} from 'react-bootstrap'

import './MoveHistory.css'

// Example move with self king removal: xa1, Nxk4
// This method brings xa1 to the previous move of the opponent.
const adjustKingRemovals = (moves) => {
  const ret = []

  for (let i = 0; i < moves.length; i++) {
    const move = moves[i]
    const ms = move.split(', ')

    if (ms.length === 2) {
      ret.push(ms[1])
      ret[i] += ms[0]
    } else {
      ret.push(move)
    }
  }

  return ret
}

const halfMovesToFullMoves = (halfMoves) => {
  const ret = []
  var fullMove = []

  halfMoves.forEach(halfMove => {
    fullMove.push(halfMove)
    if (fullMove.length === 2) {
      ret.push(fullMove)
      fullMove = []
    }
  })

  if (fullMove.length > 0) {
    ret.push(fullMove)
  }

  return ret
}

export default class MoveHistory extends Component {
  render() {
    const {notations} = this.props
    if (notations.length === 0) return null

    const adjustedMoves = adjustKingRemovals(notations)
    const fullMoves = halfMovesToFullMoves(adjustedMoves)
    return (
      <div className="move-history">
        <Table striped bordered condensed hover>
          <thead>
            <tr>
              <th>#</th>
              <th>White</th>
              <th>Black</th>
            </tr>
          </thead>
          <tbody>
            {fullMoves.map(([whiteMove, blackMove], idx) => (
              <tr key={idx}>
                <td>{idx + 1}</td>
                <td>{whiteMove}</td>
                {blackMove && <td>{blackMove}</td>}
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
    )
  }
}
