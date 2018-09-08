import React, {Component} from 'react'
import {Col, Jumbotron, Panel, Row} from 'react-bootstrap'
import {Link, Route} from 'react-router-dom'

import EndedGames from '../gamelist/EndedGames'

import AdminChangeUsername from './AdminChangeUsername'
import AdminSetUserPoint from './AdminSetUserPoint'

import {USER_GUEST, USER_ADMIN} from '../userType'
import serverUrl from '../server'

export default class UserPage extends Component {
  static route = <Route exact path="/users/:username" component={UserPage}/>

  constructor(props) {
    super(props)
    this.state = {point: undefined, rank: undefined}
  }

  render() {
    const {username} = this.props.match.params
    const myPage =
      window.$me.username === username &&
      window.$me.userType !== USER_GUEST

    const {point, rank} = this.state

    return (
      <div>
        <Row>
          <Col md={5} sm={6}>
            <Jumbotron>
              <p>{username}</p>

              {point &&
                <ul>
                  <li>Point: {point}</li>
                  <li>Rank: {rank}</li>
                </ul>
              }

              {myPage && <Link to="/settings/password">Settings</Link>}

              <br />
              <br />

              {window.$me.userType === USER_ADMIN &&
                <Panel>
                  <Panel.Heading>Admin</Panel.Heading>
                  <Panel.Body>
                    <AdminChangeUsername username={username} />
                    <hr />
                    {point && <AdminSetUserPoint username={username} point={point} />}
                  </Panel.Body>
                </Panel>
              }
            </Jumbotron>
          </Col>

          <Col md={7} sm={6}>
            <EndedGames username={username} />
          </Col>
        </Row>
      </div>
    )
  }

  componentDidMount() {
    const {username} = this.props.match.params
    fetch(serverUrl(`/api/users/${username}`))
      .then(res => res.json())
      .then(ratings => this.setState(ratings['']))
      .catch(e => {})
  }
}
