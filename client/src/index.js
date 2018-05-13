import React from 'react'
import ReactDOM from 'react-dom'
import {Alert} from 'react-bootstrap'

import App from './App'
import serverUrl from './server'

import './index.css'

window.$debug = (
  process.env.NODE_ENV !== 'production' ||
  window.location.search.indexOf('debug=true') >= 0
)

const serverAlert = (
  <Alert bsStyle="warning">
    <p>Server busy, please try again.</p>
    <p><a href="">Reload.</a></p>
  </Alert>
)

fetch(serverUrl('/whoami'), {credentials: 'include'})
  .then(res => res.json())
  .then(me => {
    window.$me = me
    ReactDOM.render(<App />, document.getElementById('root'))
  })
  .catch(e =>
    ReactDOM.render(serverAlert, document.getElementById('root'))
  )
