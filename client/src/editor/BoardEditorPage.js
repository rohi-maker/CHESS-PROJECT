import React, {Component} from 'react'
import {Alert, Button, Checkbox, Col, Glyphicon, Row} from 'react-bootstrap'
import {Route} from 'react-router-dom'

import Board from '../board/Board'
import GameOverDlg from '../gamepage/GameOverDlg'
import MoveHistory from '../gamepage/MoveHistory'

import FullscreenButton, {FULLSCREEN_STYLE} from '../FullscreenButton'

import PlayerColor from '../gameconfig/PlayerColor'
import State, {STATE_DESCS} from '../State'

import PieceSelect from './PieceSelect'
import PlayerInfo from './PlayerInfo'

export default class BoardEditorPage extends Component {
  static routeWithFen = <Route path="/editor/*" component={BoardEditorPage}/>
  static routeWithoutFen = <Route exact path="/editor" component={BoardEditorPage}/>

  constructor(props) {
    super(props)

    const pos = props.match.params[0]
    this.initialSen = pos ? pos + ' w KQkq KQkq - 0 0' : Board.startingSEN

    this.state = {
      state: State.ALIVE,
      status: STATE_DESCS[State.ALIVE],

      putPiece: 'x',
      playing: false,

      flipBoard: false,
      notations: [],

      wPiecesCaptured: {},
      bPiecesCaptured: {}
    }
  }

  render() {
    const {
      state, status,
      putPiece, playing,
      flipBoard, notations,
      wPiecesCaptured, bPiecesCaptured
    } = this.state

    const gameOver = state !== State.ALIVE && state !== State.NOT_STARTED

    return (
      <div ref={r => this.container = r} style={FULLSCREEN_STYLE}>
        <Row>
          <Col md={8}>
            <Board
              ref={r => this.board = r}
              sen={this.initialSen}

              showCoords={true}
              showLegalMoves={true}
              showLastMove={true}
              viewAsBlackPlayer={flipBoard}
              allowMove={true}
              myColor={flipBoard ? PlayerColor.BLACK : PlayerColor.WHITE}
              onMove={this.onThisBoardMove}

              editor
              putPiece={playing ? undefined : putPiece}
              onPut={this.clearMoveHistory}
            />

            <Button bsStyle="primary" onClick={() => this.setState({flipBoard: !flipBoard})}>
              <Glyphicon glyph="refresh" />{' '}
              Flip board
            </Button>
            {' '}
            <FullscreenButton getContainer={() => this.container} />
          </Col>

          <Col md={4}>
            <Alert>{playing ? status : 'Editing board'}</Alert>

            <Checkbox checked={playing} onClick={this.togglePlaying}>
              Playing
            </Checkbox>

            {!playing && <PieceSelect selectedPiece={putPiece} onSelect={this.selectPiece} />}

            {playing &&
              <React.Fragment>
                <PlayerInfo
                  color={flipBoard ? PlayerColor.WHITE : PlayerColor.BLACK}
                  wPiecesCaptured={wPiecesCaptured}
                  bPiecesCaptured={bPiecesCaptured}
                />

                <PlayerInfo
                  color={flipBoard ? PlayerColor.BLACK : PlayerColor.WHITE}
                  wPiecesCaptured={wPiecesCaptured}
                  bPiecesCaptured={bPiecesCaptured}
                />

                {notations.length > 0 && <MoveHistory notations={notations} />}
              </React.Fragment>
            }
          </Col>
        </Row>

        {gameOver && <GameOverDlg status={status} />}
      </div>
    )
  }

  selectPiece = (putPiece) => {
    this.setState({putPiece})
  }

  togglePlaying = () => {
    const {playing} = this.state
    this.setState({playing: !playing})

    // When begin playing, set the SEN position to URL bar so that user can copy
    if (!playing) {
      const sen = this.board.position.senString
      const pos = sen.substring(0, sen.indexOf(' '))
      window.history.pushState(null, null, '/editor/' + pos)
    }
  }

  onThisBoardMove = (move, [notation, wPiecesCaptured, bPiecesCaptured]) => {
    this.appendMoveHistory(notation)
    this.setState({wPiecesCaptured, bPiecesCaptured})
  }

  appendMoveHistory(notation) {
    const {notations} = this.state
    notations.push(notation)
    this.setState({notations})
  }

  clearMoveHistory = () => {
    this.setState({notations: []})
  }
}
