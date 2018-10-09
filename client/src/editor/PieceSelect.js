import React from 'react'
import {Glyphicon, ToggleButtonGroup, ToggleButton} from 'react-bootstrap'

import Helper from '../board/Helper'

const PIECES = ['k', 'q', 'b', 'r', 'n', 'p']

const SIZE = 40
const IMG_STYLE = {width: SIZE, height: SIZE}
const GLYPH_STYLE = {fontSize: SIZE * 0.85, width: SIZE, height: SIZE}

const renderPiece = (piece, isWhite) => {
  const p = isWhite ? piece.toUpperCase() : piece
  return (
    <ToggleButton key={p} value={p}>
      {Helper.getImage(p, IMG_STYLE)}
    </ToggleButton>
  )
}

export default function PieceSelect({selectedPiece, onSelect}) {
  return (
    <React.Fragment>
      <ToggleButtonGroup type="radio" name="piece" value={selectedPiece} onChange={onSelect}>
        {PIECES.map(p => renderPiece(p, false))}
      </ToggleButtonGroup>

      <br /><br />

      <ToggleButtonGroup type="radio" name="piece" value={selectedPiece} onChange={onSelect}>
        {PIECES.map(p => renderPiece(p, true))}
      </ToggleButtonGroup>

      <br /><br />

      <ToggleButtonGroup type="radio" name="piece" value={selectedPiece} onChange={onSelect}>
        <ToggleButton value={'x'}>
          <div style={GLYPH_STYLE}>
            <Glyphicon glyph="remove"  />
          </div>
        </ToggleButton>
      </ToggleButtonGroup>
    </React.Fragment>
  )
}
