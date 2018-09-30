import React, {Component} from 'react'
import {Alert, Button, Checkbox, Col, Glyphicon, Row, Tab, Tabs} from 'react-bootstrap'
import {Route} from 'react-router-dom'
import {MoveData} from 'synergychess-engine'

import Board from '../board/Board'
import GameOverDlg from '../gamepage/GameOverDlg'
import FullscreenButton, {FULLSCREEN_STYLE} from '../FullscreenButton'
import MoveHistory from '../gamepage/MoveHistory'
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

      selectedPiece: 'x',
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
      selectedPiece, playing,
      flipBoard, notations,
      wPiecesCaptured, bPiecesCaptured
    } = this.state

    let {sen} = this.props.match.params
    sen = sen || Board.startingSEN

    const gameOver = state !== State.ALIVE && state !== State.NOT_STARTED

    return (
      <div ref={r => this.container = r} style={FULLSCREEN_STYLE}>
        <Row>
          <Col md={8}>
            <Board
              ref={r => this.board = r}
              sen={sen}

              showCoords={true}
              showLegalMoves={true}
              showLastMove={true}
              viewAsBlackPlayer={flipBoard}
              allowMove={true}
              myColor={0}
              onMove={this.onThisBoardMove.bind(this)}
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

            {!playing && <PieceSelect selectedPiece={selectedPiece} onSelect={this.selectPiece} />}

            <Checkbox checked={playing} onClick={this.togglePlaying}>
              Playing
            </Checkbox>

            <Tabs id="Chat" defaultActiveKey={1}>
              <Tab eventKey={1} title="Moves">
                <MoveHistory notations={notations} />
              </Tab>
            </Tabs>
          </Col>
        </Row>

        {gameOver && <GameOverDlg status={status} />}
      </div>
    )
  }

  componentDidMount() {
  }

  selectPiece = (selectedPiece) => {
    const {playing} = this.state
    this.setState({selectedPiece})
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
}
