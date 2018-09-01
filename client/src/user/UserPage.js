import React, {Component} from 'react'
import {Col, Jumbotron, Row} from 'react-bootstrap'
import {Link, Route} from 'react-router-dom'

import {USER_GUEST} from '../userType'

export default class UserPage extends Component {
  static route = <Route exact path="/users/:username" component={UserPage}/>

  render() {
    const myPage =
      window.$me.username === this.props.match.params.username &&
      window.$me.userType !== USER_GUEST

    return (
      <div>
        <Row>
          <Col md={5} sm={6}>
            <Jumbotron>
              <p>User info will be displayed here.</p>

              {myPage && <Link to="/settings/password">Settings</Link>}
            </Jumbotron>
          </Col>

          <Col md={7} sm={6}>
            <Jumbotron>
              <p>User games will be displayed here.</p>
            </Jumbotron>
          </Col>
        </Row>
      </div>
    )
  }
}
