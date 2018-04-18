
import React, {Component} from 'react'
import classNames from 'classnames'

import './PlayerColor.css'

export default class PlayerColor extends Component {
  static WHITE = 0
  static BLACK = 1
  static RANDOM = 2

  render() {
    const {color} = this.props
    const name =
      color === PlayerColor.WHITE
      ? '♔' :
      color === PlayerColor.BLACK
      ? '♚' : '?'

    const cls = classNames({
      'player-color': true,
      'player-color-normal': this.props.size !== 'large',
      'player-color-large': this.props.size === 'large',
      'player-color-black': color === PlayerColor.BLACK,
      'player-color-white': color === PlayerColor.WHITE,
      'player-color-random': color === PlayerColor.RANDOM,
      'player-color-selected': this.props.selected
    })

    return (
      <span className={cls} onClick={this.props.onSelect}>{name}</span>
    )
  }
}
