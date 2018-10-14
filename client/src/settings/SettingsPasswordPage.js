import React, {Component} from 'react'
import {Alert, Button, Col, ControlLabel, Form, FormControl, FormGroup, Panel, Row} from 'react-bootstrap'
import {Link, Route} from 'react-router-dom'
import qs from 'qs'

import serverUrl from '../server'

export default class SettingsPasswordPage extends Component {
  static route = <Route exact path="/settings/password" component={SettingsPasswordPage}/>

  constructor(props) {
    super(props)

    this.state = {
      oldPassword: '',
      password: '',
      password2: '',
      sending: false,
      status: undefined
    }
  }

  render() {
    const resetPassword = window.location.search.indexOf('reset') > 0

    const passwordValidationState = this.passwordValidationState()
    const password2ValidationState = this.password2ValidationState()
    const submitDisabled =
      passwordValidationState !== null ||
      password2ValidationState !== null

    return (
      <Row>
        <Col md={5}>
          <h2>Settings</h2>

          <ul>
            <li><b><Link to="/settings/password">Password</Link></b></li>
            <li><Link to="/settings/email">Email</Link></li>
          </ul>
        </Col>

        <Col md={7}>
          <Panel><Panel.Body>
            <h3>Change password</h3>

            <Form horizontal onSubmit={this.onSave}>
              {this.state.status && <Alert bsStyle="info">{this.state.status}</Alert>}

              {!resetPassword &&
                <FormGroup validationState={passwordValidationState}>
                  <Col componentClass={ControlLabel} sm={4}>
                    Old password
                  </Col>
                  <Col sm={8}>
                    <FormControl
                      type="password"
                      placeholder="Old password"
                      value={this.state.oldPassword}
                      onChange={e => this.setState({oldPassword: e.target.value.trim()})}
                    />
                  </Col>
                </FormGroup>
              }

              <FormGroup validationState={passwordValidationState}>
                <Col componentClass={ControlLabel} sm={4}>
                  New password
                </Col>
                <Col sm={8}>
                  <FormControl
                    type="password"
                    placeholder="New password"
                    value={this.state.password}
                    onChange={e => this.setState({password: e.target.value.trim()})}
                  />
                </Col>
              </FormGroup>

              <FormGroup validationState={password2ValidationState}>
                <Col componentClass={ControlLabel} sm={4}>
                  Confirm password
                </Col>
                <Col sm={8}>
                  <FormControl
                    type="password"
                    placeholder="Confirm password"
                    value={this.state.password2}
                    onChange={e => this.setState({password2: e.target.value.trim()})}
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

  passwordValidationState() {
    return this.state.password.length > 0
      ? null
      : 'warning'
  }

  password2ValidationState() {
    return this.state.password2.length > 0 && this.state.password2 === this.state.password
      ? null
      : 'warning'
  }

  onSave = (e) => {
    this.setState({sending: true, status: 'Please wait...'})

    const {reset} = qs.parse(window.location.search, {ignoreQueryPrefix: true})
    const {oldPassword, password} = this.state

    const body = qs.stringify({
      reset,
      oldPassword,
      newPassword: password
    })

    fetch(serverUrl('/api/settings/password'), {
      credentials: 'include',
      method: 'PUT',
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body
    })
    .then(res => res.text())
    .then(text => this.setState({sending: false, status: text}))
    .catch(e => this.setState({sending: false, status: 'Could not change password'}))

    e.preventDefault()
    return false
  }
}
