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
    const {timeLimit1, timeSum1, timeLimit2, timeSum2, moves} = iJoined
    const [timeLimit, timeSum] = color === 0
      ? [timeLimit1, timeSum1]
      : [timeLimit2, timeSum2]

    this.state = {timeLimit, timeSum}

    this.gameState = iJoined.state
    this.numMoves = moves.length
    this.checkAndStart()
  }

  render() {
    const {timeLimit, timeSum} = this.state
    const ms = timeLimit - timeSum
    return <p className="clock">{formatTime(ms)}</p>
  }

  componentWillUnmount() {
    this.stop()
  }

  onStateGameChanged(gameState) {
    this.stop()
    this.gameState = gameState
    this.checkAndStart()
  }

  onMove(timeSum) {
    this.stop()

    // Need to call updateTime first, then increase numMoves later
    this.updateTime(timeSum)
    this.numMoves++

    this.checkAndStart()
  }

  updateTime(timeSum) {
    if (!this.isMyColor()) return

    const {iJoined} = this.props
    const {timeBonusSecs} = iJoined
    const {timeLimit} = this.state

    this.setState({
      timeLimit: timeLimit + timeBonusSecs * 1000,
      timeSum
    })
  }

  checkAndStart() {
    if (this.shouldCountDown()) {
      this.interval = setInterval(this.countDown, 1000)
    }
  }

  isMyColor() {
    const {color} = this.props
    return this.numMoves % 2 === color
  }

  shouldCountDown() {
    return this.isMyColor() && this.gameState === State.ALIVE && this.numMoves >= 2
  }

  stop() {
    if (this.interval) {
      clearTimeout(this.interval)
      this.interval = null
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
