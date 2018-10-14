import React, {Component} from 'react'
import {Alert, Button, Col, ControlLabel, Form, FormControl, FormGroup, Panel, Row} from 'react-bootstrap'
import {Link, Route} from 'react-router-dom'
import qs from 'qs'

import validateEmail from '../validateEmail'
import serverUrl from '../server'

export default class SettingsEmailPage extends Component {
  static route = <Route exact path="/settings/email" component={SettingsEmailPage}/>

  constructor(props) {
    super(props)

    this.state = {
      email: '',
      sending: false,
      status: undefined
    }
  }

  render() {
    const emailValidationState = this.emailValidationState()
    const submitDisabled = emailValidationState !== null

      return (
      <Row>
        <Col md={5}>
          <h2>Settings</h2>

          <ul>
            <li><Link to="/settings/password">Password</Link></li>
            <li><b><Link to="/settings/email">Email</Link></b></li>
          </ul>
        </Col>

        <Col md={7}>
          <Panel><Panel.Body>
            <h3>Change email</h3>

            <Form horizontal onSubmit={this.onSave}>
              {this.state.status && <Alert bsStyle="info">{this.state.status}</Alert>}

              <FormGroup validationState={emailValidationState}>
                <Col componentClass={ControlLabel} sm={4}>
                  New email
                </Col>
                <Col sm={8}>
                  <FormControl
                    type="email"
                    placeholder="New email"
                    value={this.state.email}
                    onChange={e => this.setState({email: e.target.value.trim()})}
                  />
                </Col>
              </FormGroup>

              <FormGroup>
                <Col smOffset={4} sm={8}>
                  <Button type="submit" disabled={submitDisabled || this.state.sending}>
                    Change
                  </Button>
                </Col>
              </FormGroup>
            </Form>
          </Panel.Body></Panel>
        </Col>
      </Row>
    )
  }

  emailValidationState() {
    return validateEmail(this.state.email)
      ? null
      : 'warning'
  }

  onSave = (e) => {
    this.setState({sending: true, status: 'Please wait...'})

    const {email} = this.state
    const body = qs.stringify({email})

    fetch(serverUrl('/api/settings/email'), {
      credentials: 'include',
      method: 'PUT',
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body
    })
    .then(res => res.text())
    .then(text => this.setState({sending: false, status: text}))
    .catch(e => this.setState({sending: false, status: 'Could not change email'}))

    e.preventDefault()
    return false
  }
}
