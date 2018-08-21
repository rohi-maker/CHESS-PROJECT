import React, {Component} from 'react'
import {Table} from 'react-bootstrap'

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

    const fullMoves = halfMovesToFullMoves(notations)
    return (
      <div style={{height: 220, overflow: 'auto'}}>
        <Table striped bordered condensed hover>
          <thead>
            <tr>
              <th>#</th>
              <th>White</th>
              <th>Black</th>
            </tr>
          </thead>
          <tbody>
            {fullMoves.map((fullMove, idx) => (
              <tr key={idx}>
                <td>{idx + 1}</td>
                <td>{fullMove[0]}</td>
                <td>{fullMove[1]}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
    )
  }
}
