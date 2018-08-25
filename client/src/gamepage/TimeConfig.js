import React from 'react'
import {Well} from 'react-bootstrap'

const formatSecs = (secs) => {
  if (secs <= 0) return 'none'

  if (secs < 60) return `${secs}"`

  const remSecs = secs % 60
  const minutes = (secs - remSecs) / 60
  return remSecs === 0 ? `${minutes}'` : `${remSecs}"`
}

export default function TimeConfig({timeLimitSecs, timeBonusSecs}) {
  return (
    <Well>
      <label>Time:</label>
      {' '}
      <ul>
        <li>Limit: {formatSecs(timeLimitSecs)}</li>
        <li>Bonus per move: {formatSecs(timeBonusSecs)}</li>
      </ul>
    </Well>
  )
}
