import React, {Component} from 'react'

import './chat.css'

export default class Chat extends Component {
  render() {
    return (
      <div className="chat">
        <textarea className="chat-msgs" />

        <br />

        <input className="chat-input" />
      </div>
    )
  }
}
