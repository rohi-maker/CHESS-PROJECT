import React, {Component} from 'react'
import {Route} from 'react-router-dom'
import {Button, Col, Form, FormControl, Row} from 'react-bootstrap'

import EndedGames from './EndedGames'

export default class EndedGamesPage extends Component {
  static route = <Route exact path="/ended" component={EndedGamesPage}/>

  constructor(props) {
    super(props)
    this.state = {username: '', usernameInput: ''}
  }

  render() {
    const {username, usernameInput} = this.state

    return (
      <Row>
        <Col md={5} sm={6}>
          <h2>Ended games</h2>

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
        </Col>

        <Col md={7} sm={6}>
          <EndedGames key={username} username={username} />
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
