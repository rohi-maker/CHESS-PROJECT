import React, {Component} from 'react'
import {Alert, Button, Col, ControlLabel, Form, FormControl, FormGroup} from 'react-bootstrap'
import qs from 'qs'

import serverUrl from '../server'

export default class AdminChangeUsername extends Component {
  constructor() {
    super()
    this.state = {status: '', newUsername: '', sending: false}
  }
  render() {
    const {status, newUsername, sending} = this.state

    return (
      <Form horizontal onSubmit={this.rename}>
        {status && <Alert bsStyle="info">{status}</Alert>}

        <FormGroup>
          <Col componentClass={ControlLabel} sm={4}>
            Rename username
          </Col>
          <Col sm={8}>
            <FormControl
              type="text"
              placeholder="New username"
              value={newUsername}
              onChange={e => this.setState({newUsername: e.target.value.trim()})}
            />
          </Col>
        </FormGroup>

        <FormGroup>
          <Col smOffset={4} sm={8}>
            <Button type="submit" disabled={newUsername.length === 0 || sending}>
              Rename
            </Button>
          </Col>
        </FormGroup>
      </Form>
    )
  }

  rename = (e) => {
    e.preventDefault()

    const {newUsername} = this.state
    if (newUsername.length === 0) return

    this.setState({sending: true, status: 'Renaming...'})

    const {username} = this.props
    const body = qs.stringify({newUsername})

    fetch(serverUrl('/api/admin/rename-username/' + username), {
      credentials: 'include',
      method: 'PATCH',
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body
    })
    .then(res => res.text())
    .then(text => this.setState({sending: false, status: text}))
    .catch(e => this.setState({sending: false, status: 'Could not rename username'}))
  }
}
