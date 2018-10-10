import React, {Component} from 'react'
import {Alert, Button, Checkbox, Col, Glyphicon, Row} from 'react-bootstrap'
import {Route} from 'react-router-dom'
import {MoveData} from 'synergychess-engine'

import Board from '../board/Board'
import GameOverDlg from '../gamepage/GameOverDlg'
import FullscreenButton, {FULLSCREEN_STYLE} from '../FullscreenButton'
import MoveHistory from '../gamepage/MoveHistory'

import PlayerColor from '../gameconfig/PlayerColor'
import State, {STATE_DESCS} from '../State'

import PieceSelect from './PieceSelect'

export default class BoardEditorPage extends Component {
  static routeWithFen = <Route path="/editor/:sen" component={BoardEditorPage}/>
  static routeWithoutFen = <Route exact path="/editor" component={BoardEditorPage}/>

  constructor() {
    super()

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

    const {sen} = this.props.match.params
    const initialSen = sen || Board.startingSEN

    const gameOver = state !== State.ALIVE && state !== State.NOT_STARTED

    return (
      <div ref={r => this.container = r} style={FULLSCREEN_STYLE}>
        <Row>
          <Col md={8}>
            <Board
              ref={r => this.board = r}
              sen={initialSen}

              showCoords={true}
              showLegalMoves={true}
              showLastMove={true}
              viewAsBlackPlayer={flipBoard}
              allowMove={true}
              myColor={flipBoard ? PlayerColor.BLACK : PlayerColor.WHITE}
              onMove={this.onThisBoardMove.bind(this)}

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

            {notations.length > 0 && <MoveHistory notations={notations} />}
          </Col>
        </Row>

        {gameOver && <GameOverDlg status={status} />}
      </div>
    )
  }

  componentDidMount() {
  }

  selectPiece = (putPiece) => {
    this.setState({putPiece})
  }

  togglePlaying = () => {
    const {playing} = this.state
    this.setState({playing: !playing})
  }

  onThisBoardMove(move, [notation, wPiecesCaptured, bPiecesCaptured]) {
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
