import React, {Component} from 'react'
import {Button, Modal} from 'react-bootstrap'

export default class GameOverDlg extends Component {
  constructor() {
    super()
    this.state = {open: true}
  }

  render() {
    const {status} = this.props
    const {open} = this.state

    return (
      <Modal bsSize="small" show={open}>
        <Modal.Header>
          <Modal.Title>Game over</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {status}
        </Modal.Body>

        <Modal.Footer>
          <Button bsStyle="primary" onClick={() => this.setState({open: false})}>OK</Button>
        </Modal.Footer>
      </Modal>
    )
  }
}
