import React, {Component} from 'react'
import {Col, Row} from 'react-bootstrap'
import {Route} from 'react-router-dom'

import Lobby from './Lobby'
import LiveGames from '../gamelist/LiveGames'

export default class TopPage extends Component {
  static route = <Route exact path="/" component={TopPage}/>

  render() {
    return (
      <Row>
        <Col md={5} sm={6}>
          <Lobby />
        </Col>

        <Col md={7} sm={6}>
          <LiveGames />
        </Col>
      </Row>
    )
  }
}
