import React from 'react'
import ReactDOM from 'react-dom'

import App from './App'
import serverUrl from './server'

import './index.css'

window.$debug = true

fetch(serverUrl('/whoami'), {credentials: 'include'})
  .then(res => res.json())
  .then(me => {
    window.$me = me
    ReactDOM.render(<App />, document.getElementById('root'))
  })
  .catch(e => alert('Server busy, please try again'))
