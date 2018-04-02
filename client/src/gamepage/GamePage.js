import React, {Component} from 'react'
import {Button, Col, Glyphicon, Jumbotron, Row} from 'react-bootstrap'
import {Route} from 'react-router-dom'

export default class GamePage extends Component {
  static route = <Route exact path="/games/:gameId" component={GamePage}/>

  render() {
    return (
      <div>
        <Row>
          <Col md={8}>
            <Jumbotron>
              Chessboard will be displayed here.
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
          </Col>

          <Col md={4}>
            <Jumbotron>
              Player name and clock will be displayed here.
            </Jumbotron>

            <Jumbotron>
              Game moves will be displayed here.
            </Jumbotron>

            <Jumbotron>
              Player name and clock will be displayed here.
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
}
