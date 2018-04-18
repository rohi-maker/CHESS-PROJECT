import React, {Component} from 'react'
import {Button, Col, Glyphicon, Jumbotron, Row} from 'react-bootstrap'
import {Route} from 'react-router-dom'

import Board from '../board/Board'

const startingSEN = "r1n1bkqb1n1r/2p6p2/1prnbqkbnrp1/pppppppppppp/12/12/12/12/PPPPPPPPPPPP/1PRNBQKBNRP1/2P6P2/R1N1BKQB1N1R w KQkq KQkq - 0 0"

export default class GamePage extends Component {
  static route = <Route exact path="/games/:gameId" component={GamePage}/>

  render() {
    return (
      <div>
        <Row>
          <Col md={8}>
            <Board
              ref={r => this.board =r}
              sen={startingSEN}
              lastMove=""

              showCoords={true}
              showLegalMoves={true}
              showLastMove={true}
              viewAsBlackPlayer={true}
              allowMove={true}
              onMove={this.onThisBoardMove.bind(this)}
            />
          </Col>

          <Col md={4}>
            <Jumbotron>
              Player name and clock will be displayed here.
            </Jumbotron>

            <Jumbotron>
              Player name and clock will be displayed here.
            </Jumbotron>

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

            <Jumbotron>
              Game moves will be displayed here.
            </Jumbotron>
          </Col>
        </Row>
      </div>
    )
  }

  setBoardOption(key, value) {
    const {boardOptions} = this.state
    boardOptions[key] = value
    this.setState({boardOptions})
  }

  onThisBoardMove(move) {

  }
}
