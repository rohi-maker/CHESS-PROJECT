import React, {Component} from 'react'
import {Alert, Button, Col, ControlLabel, Form, FormControl, FormGroup} from 'react-bootstrap'
import qs from 'qs'

import serverUrl from '../server'

export default class AdminSetUserPoint extends Component {
  constructor(props) {
    super(props)

    const {point} = props
    this.state = {status: '', point, sending: false}
  }

  render() {
    const {status, point, sending} = this.state

    return (
      <Form horizontal onSubmit={this.rename}>
        {status && <Alert bsStyle="info">{status}</Alert>}

        <FormGroup>
          <Col componentClass={ControlLabel} sm={4}>
            Set user point
          </Col>
          <Col sm={8}>
            <FormControl
              type="text"
              placeholder="point"
              value={point}
              onChange={e => this.setState({point: e.target.value.trim()})}
            />
          </Col>
        </FormGroup>

        <FormGroup>
          <Col smOffset={4} sm={8}>
            <Button type="submit" bsStyle="primary" disabled={point.length === 0 || sending}>
              Set
            </Button>
          </Col>
        </FormGroup>
      </Form>
    )
  }

  rename = (e) => {
    e.preventDefault()

    const {point} = this.state

    // Check NaN
    const pointInt = parseInt(point, 10)
    if (!pointInt) {
      this.setState({status: 'Invalid number'})
      return
    }

    this.setState({sending: true, status: 'Setting point...'})

    const {username} = this.props
    const body = qs.stringify({point: '' + pointInt})

    fetch(serverUrl('/api/admin/set-user-point/' + username), {
      credentials: 'include',
      method: 'PATCH',
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body
    })
    .then(res => res.text())
    .then(text => this.setState({sending: false, status: text}))
    .catch(e => this.setState({sending: false, status: 'Could not set user point'}))
  }
}
