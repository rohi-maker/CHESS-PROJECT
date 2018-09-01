import React, {Component} from 'react'
import {Button, Col, Form, FormControl, Glyphicon, Row} from 'react-bootstrap'
import {Route} from 'react-router-dom'

import Ranking from './Ranking'

export default class RankingPage extends Component {
  static route = <Route exact path="/ranking" component={RankingPage}/>

  constructor(props) {
    super(props)
    this.state = {username: '', usernameInput: ''}
  }

  render() {
    const {username, usernameInput} = this.state

    return (
      <Row>
        <Col md={5} sm={6}>
          <h2>Ranking</h2>

          <Form horizontal onSubmit={this.search}>
            <FormControl
              type="text"
              placeholder="Username"
              value={usernameInput}
              onChange={e => this.setState({usernameInput: e.target.value})}
            />

            <br />

            <Button type="submit">
              Search
            </Button>
          </Form>

          <br />

          <p>
            <Glyphicon glyph="star" />
            {' '}User ranking is updated regularly every 5 minutes.
          </p>
        </Col>

        <Col md={7} sm={6}>
          <Ranking key={username} username={username} />
        </Col>
      </Row>
    )
  }

  search = (e) => {
    const {usernameInput} = this.state
    this.setState({username: usernameInput.trim()})
    e.preventDefault()
    return false
  }
}
