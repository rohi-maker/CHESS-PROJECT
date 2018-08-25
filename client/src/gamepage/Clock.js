import React, {Component} from 'react'

import State from '../State'
import './Clock.css'

const formatTime = (ms) => {
  const s = Math.round(ms / 1000)
  const r = s % 60
  const m = (s - r) / 60
  const ss = r > 9 ? `${r}` : `0${r}`
  return `${m}:${ss}`
}

export default class Clock extends Component {
  constructor(props) {
    super(props)

    const {iJoined, color} = this.props
    const {timeLimit1, timeSum1, timeLimit2, timeSum2} = iJoined
    const [timeLimit, timeSum] = color === 0
      ? [timeLimit1, timeSum1]
      : [timeLimit2, timeSum2]

    this.state = {timeLimit, timeSum}
  }

  render() {
    const {timeLimit, timeSum} = this.state
    const ms = timeLimit - timeSum
    return <p className="clock">{formatTime(ms)}</p>
  }

  componentWillUnmount() {
    this.stop()
  }

  onStateChanged(state) {
    if (state === State.ALIVE)
      this.start()
    else
      this.stop()
  }

  onMove(timeSum) {
    console.log('timeSum', timeSum)
    this.toggle(timeSum)
  }

  toggle(timeSum) {
    const {timeBonusSecs} = this.props
    const {timeLimit} = this.state

    if (this.stop()) {
      this.setState({
        timeLimit: timeLimit + timeBonusSecs * 1000,
        timeSum
      })
    } else {
      this.start()
    }
  }

  start() {
    this.interval = setInterval(this.countDown, 1000)
  }

  stop() {
    if (this.interval) {
      clearTimeout(this.interval)
      this.interval = null
      return true
    } else {
      return false
    }
  }

  countDown = () => {
    const {timeLimit, timeSum} = this.state
    const sum = timeSum + 1000
    if (sum >= timeLimit) {
      this.stop()
      this.setState({timeSum: timeLimit})
    } else {
      this.setState({timeSum: sum})
    }
  }
}
