import React, {Component} from 'react'
import {Alert, Button, Col, ControlLabel, Form, FormControl, FormGroup} from 'react-bootstrap'
import qs from 'qs'

import validateEmail from '../validateEmail'
import serverUrl from '../server'

export default class ForgotPassword extends Component {
  constructor(props) {
    super(props)

    this.state = {
      email: '',
      sending: false,
      status: undefined
    }
  }

  render() {
    const {email, sending, status} = this.state
    const emailValidationState = this.emailValidationState()
    const submitDisabled = emailValidationState !== null

    return (
      <Form horizontal onSubmit={e => this.props.onFormSubmit(this.doWithReCaptchaResponse.bind(this), e)}>
        {status && <Alert bsStyle="info">{status}</Alert>}

        <FormGroup validationState={emailValidationState}>
          <Col componentClass={ControlLabel} sm={4}>
            Email
          </Col>
          <Col sm={8}>
            <FormControl
              type="email"
              placeholder="Email"
              value={email}
              onChange={e => this.setState({email: e.target.value.trim()})}
            />
          </Col>
        </FormGroup>

        <FormGroup>
          <Col smOffset={4} sm={8}>
            <Button type="submit" disabled={submitDisabled || sending}>
              Reset password
            </Button>
          </Col>
        </FormGroup>
      </Form>
    )
  }

  emailValidationState() {
    return validateEmail(this.state.email)
      ? null
      : 'warning'
  }

  doWithReCaptchaResponse(response) {
    this.setState({sending: true, status: 'Please wait...'})

    const {email} = this.state

    const body = qs.stringify({
      'g-recaptcha-response': response,
      email
    })

    fetch(serverUrl('/api/forgot-password'), {
      credentials: 'include',
      method: 'POST',
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body
    })
    .then(res => res.text())
    .then(text => this.setState({sending: false, status: text}))
    .catch(e => this.setState({sending: false, status: 'Could not reset password'}))
  }
}
