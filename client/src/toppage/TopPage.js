import React, {Component} from 'react'
import {Col, Jumbotron, Row} from 'react-bootstrap'
import {Route} from 'react-router-dom'

import Lobby from './Lobby'

export default class TopPage extends Component {
  static route = <Route exact path="/" component={TopPage}/>

  render() {
    return (
      <div>
        <Row>
          <Col md={5} sm={6}>
            <Lobby />
          </Col>

          <Col md={7} sm={6}>
            <Jumbotron>
              <h1>Live games will be displayed here.</h1>
              <p>Click to go to the game page.</p>
            </Jumbotron>
          </Col>
        </Row>
      </div>
    )
  }
}
