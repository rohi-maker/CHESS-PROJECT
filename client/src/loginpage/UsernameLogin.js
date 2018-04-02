import React, {Component} from 'react'
import {Alert, Button, Col, ControlLabel, Form, FormControl, FormGroup} from 'react-bootstrap'
import qs from 'qs'

import serverUrl from '../server'

export default class UsernameLogin extends Component {
  constructor(props) {
    super(props)

    this.state = {
      usernameOrEmail: '',
      password: '',
      sending: false,
      status: undefined
    }
  }

  render() {
    return (
      <Form horizontal onSubmit={e => this.props.onFormSubmit(this.doWithReCaptchaResponse.bind(this), e)}>
        {this.state.status && <Alert bsStyle="info">{this.state.status}</Alert>}

        <FormGroup>
          <Col componentClass={ControlLabel} sm={4}>
            Username or email
          </Col>
          <Col sm={8}>
            <FormControl
              type="text"
              placeholder="Username or email"
              value={this.state.email}
              onChange={e => this.setState({usernameOrEmail: e.target.value.trim()})}
            />
          </Col>
        </FormGroup>

        <FormGroup>
          <Col componentClass={ControlLabel} sm={4}>
            Password
          </Col>
          <Col sm={8}>
            <FormControl
              type="password"
              placeholder="Password"
              value={this.state.password}
              onChange={e => this.setState({password: e.target.value.trim()})}
            />
          </Col>
        </FormGroup>

        <FormGroup>
          <Col smOffset={4} sm={8}>
            <Button type="submit" disabled={this.state.sending}>Login</Button>
          </Col>
        </FormGroup>
      </Form>
    )
  }

  doWithReCaptchaResponse(response) {
    this.setState({sending: true, status: 'Please wait...'})

    const {usernameOrEmail, password} = this.state

    const body = qs.stringify({
      'g-recaptcha-response': response,
      usernameOrEmail,
      password
    })

    fetch(serverUrl('/api/login/username'), {
      credentials: 'include',
      method: 'POST',
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body
    })
    .then(res => {
      if (res.redirected) {
        window.location = '/'
      } else {
        this.setState({sending: false, status: 'Incorrect username or email or password'})
      }
    })
    .catch(e => this.setState({sending: false, status: 'Could not login'}))
  }
}
