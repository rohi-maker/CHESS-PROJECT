import React, {Component} from 'react'
import {Route} from 'react-router-dom'

export default class ChessRulesPage extends Component {
  static route = <Route exact path="/rules" component={ChessRulesPage}/>

  render() {
    return (
      <div>
        <h2>Rules of Synergy Chess</h2>

        <h3>1. Introduction</h3>
        <iframe title="1. Introduction" width="669" height="376" src="https://www.youtube.com/embed/gzsmKNyZj-s" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>2. Pinned Kings – Bishop</h3>
        <iframe title="2. Pinned Kings – Bishop" width="669" height="376" src="https://www.youtube.com/embed/mvEXgdsZ9ZE" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>3. Pinned Kings – Queen</h3>
        <iframe title="3. Pinned Kings – Queen" width="669" height="376" src="https://www.youtube.com/embed/lIQXxfvt_18" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>4. Double Check – Knight</h3>
        <iframe title="4. Double Check – Knight" width="669" height="376" src="https://www.youtube.com/embed/mCAYgtiOoso" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>5.Checkmate – Be Careful !!</h3>
        <iframe title="5.Checkmate – Be Careful !!" width="669" height="376" src="https://www.youtube.com/embed/jEFyv0uBswI" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>6. Castling</h3>
        <iframe title="6. Castling" width="669" height="376" src="https://www.youtube.com/embed/I9qHes6g7CA" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>7. Pawn Promotion</h3>
        <iframe title="7. Pawn Promotion" width="669" height="376" src="https://www.youtube.com/embed/RECwplegAYw" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
      </div>
    )
  }
}
