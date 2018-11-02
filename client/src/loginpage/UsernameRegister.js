import React, {Component} from 'react'
import {Alert, Button, Col, ControlLabel, Form, FormControl, FormGroup, Checkbox} from 'react-bootstrap'
import qs from 'qs'

import validateEmail from '../validateEmail'
import serverUrl from '../server'

export default class UsernameRegister extends Component {
  constructor(props) {
    super(props)

    this.state = {
      email: window.$me.socialEmail,
      username: '',
      password: '',
      password2: '',
      sending: false,
      status: undefined,
      checked: false
    }
  }

  render() {
    const emailValidationState = this.emailValidationState()
    const usernameValidationState = this.usernameValidationState()
    const passwordValidationState = this.passwordValidationState()
    const password2ValidationState = this.password2ValidationState()
    const submitDisabled =
      emailValidationState !== null ||
      usernameValidationState !== null ||
      passwordValidationState !== null ||
      password2ValidationState !== null ||
      !this.state.checked

    return (
      <Form horizontal onSubmit={e => this.props.onFormSubmit(this.doWithReCaptchaResponse, e)}>
        {this.state.status && <Alert bsStyle="info">{this.state.status}</Alert>}

        <FormGroup validationState={emailValidationState}>
          <Col componentClass={ControlLabel} sm={4}>
            Email
          </Col>
          <Col sm={8}>
            <FormControl
              type="email"
              placeholder="Email"
              value={this.state.email}
              onChange={e => this.setState({email: e.target.value.trim()})}
            />
          </Col>
        </FormGroup>

        <FormGroup validationState={usernameValidationState}>
          <Col componentClass={ControlLabel} sm={4}>
            Username
          </Col>
          <Col sm={8}>
            <FormControl
              type="text"
              placeholder="Cannot change later"
              value={this.state.username}
              onChange={e => this.setState({username: e.target.value.trim().toLowerCase()})}
            />
          </Col>
        </FormGroup>

        <FormGroup validationState={passwordValidationState}>
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
          <Col componentClass={ControlLabel} sm={4}>
          </Col>
          <Col sm={8}>
            <Checkbox
              inputRef={ref => {this.checkbox = ref}}
              onChange={this.isChecked.bind(this)}
              validationState='success'
            >
              <a
                href="https://play.synergychess.net/terms"
                rel="noopener noreferrer"
                target="_blank"
              >
                By joining this site I agree to the terms, and conditions
              </a>
            </Checkbox>
          </Col>
        </FormGroup>

        <FormGroup>
          <Col smOffset={4} sm={8}>
            <Button type="submit" disabled={submitDisabled || this.state.sending}>
              Register
            </Button>
          </Col>
        </FormGroup>
      </Form>
    )
  }

  isChecked() {
    if (this.checkbox && this.checkbox.checked !== this.state.checked)
      this.setState({checked: this.checkbox.checked})
  }

  emailValidationState() {
    return validateEmail(this.state.email)
      ? null
      : 'warning'
  }

  usernameValidationState() {
    return this.state.username.length > 0
      ? null
      : 'warning'
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

  doWithReCaptchaResponse = (response) => {
    this.setState({sending: true, status: 'Please wait...'})

    const {username, email, password} = this.state

    const body = qs.stringify({
      'g-recaptcha-response': response,
      username,
      email,
      password
    })

    fetch(serverUrl('/api/register-username'), {
      credentials: 'include',
      method: 'POST',
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body
    })
    .then(res => {
      if (res.redirected) {
        window.location = serverUrl('/')
        return undefined
      }

      return res.text()
    })
    .then(text => this.setState({sending: false, status: text}))
    .catch(e => this.setState({sending: false, status: 'Could not register'}))
  }
}
