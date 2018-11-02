import React, {Component} from 'react'

import './Chat.css'

const createChatLine = (username, msg) => {
  const line = document.createElement('div')

  const u = document.createElement('b')
  u.appendChild(document.createTextNode(`${username}: `))

  const m = document.createTextNode(msg)

  line.appendChild(u)
  line.appendChild(m)
  return line
}

export default class Chat extends Component {
  constructor(props) {
    super(props)
    this.state = {msg: ''}
  }

  render() {
    return (
      <React.Fragment>
        <div ref={r => this.output = r} className="chat-output" />

        <form onSubmit={this.onSubmit}>
          <input ref={r => this.input = r} className="chat-input" />
        </form>
      </React.Fragment>
    )
  }

  onMsg(username, msg) {
    const line = createChatLine(username, msg)
    this.output.appendChild(line)
    this.output.lastChild.scrollIntoView({behavior: "smooth", block: "end"});
  }

  onSubmit = (event) => {
    const msg = this.input.value.trim()
    if (msg.length > 0) {
      this.onMsg(window.$me.username, msg)
      this.props.sendMsg(msg)
      this.input.value = ''
    }

    event.preventDefault()
    return false
  }
}