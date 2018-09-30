import React from 'react'
import {Button, Glyphicon} from 'react-bootstrap'

export const FULLSCREEN_STYLE = {width: '100%', height: '100%'}

const fullscreenEnabled =
  document.fullscreenEnabled ||
  document.webkitFullscreenEnabled ||
  document.mozFullScreenEnabled ||
  document.msFullscreenEnabled

const toggleFullscreen = (container) => {
  const fullscreenElement =
    document.fullscreenElement ||
    document.webkitFullscreenElement ||
    document.mozFullScreenElement ||
    document.msFullscreenElement

  if (fullscreenElement) {
    const exitFullscreen =
      document.exitFullscreen ||
      document.webkitExitFullscreen ||
      document.mozCancelFullScreen ||
      document.msExitFullscreen

    if (exitFullscreen) exitFullscreen.call(document)
  } else {
    const requestFullscreen =
      container.requestFullscreen ||
      container.webkitRequestFullScreen ||
      container.mozRequestFullScreen ||
      container.msRequestFullscreen
    requestFullscreen.call(container)
  }
}

export default function FullscreenButton({getContainer}) {
  if (!fullscreenEnabled) return null

  return (
    <Button bsStyle="primary" onClick={() => toggleFullscreen(getContainer())}>
      <Glyphicon glyph="fullscreen" />{' '}
      Fullscreen
    </Button>
  )
}
