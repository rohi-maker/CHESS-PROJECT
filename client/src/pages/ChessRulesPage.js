import React, {Component} from 'react'
import {Route} from 'react-router-dom'

export default class ChessRulesPage extends Component {
  static route = <Route exact path="/rules" component={ChessRulesPage}/>

  render() {
    return (
      <div>
        <h2>Rules of Synergy Chess</h2>

        <p><b>
        NB: Bishop = 4 Points.
        In Chess the Pawn = 1 point, the Knight = 3 points, the Bishop = 3 points, the Rook = 5 points, and the Queen = 9 points.
        In Synergy Chess all of the points are the same, except the Bishop.
        Because of the extra long range attacking power compared to the Knight, the Bishop now = 4 points.
        </b></p>

        <h3>1. Introduction</h3>
        <iframe title="1. Introduction" width="669" height="376" src="https://www.youtube.com/embed/daL4DnlOYv4" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>2. Pinned Kings – Bishop</h3>
        <iframe title="2. Pinned Kings – Bishop" width="669" height="376" src="https://www.youtube.com/embed/gZHaVtuQrts" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>3. Pinned Kings – Rook</h3>
        <iframe title="3. Pinned Kings – Rook" width="669" height="376" src="https://www.youtube.com/embed/eTtN8hmuo2s" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>4. Double Check – Knight</h3>
        <iframe title="4. Double Check – Knight" width="669" height="376" src="https://www.youtube.com/embed/4__Kp4h_Tf0" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>5. Double Check – Rook</h3>
        <iframe title="4. Double Check – Rook" width="669" height="376" src="https://www.youtube.com/embed/7Nr_CVjXM5U" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>6. Checkmate – Be Careful !!</h3>
        <iframe title="5.Checkmate – Be Careful !!" width="669" height="376" src="https://www.youtube.com/embed/r7K3bQ45iWQ" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>7. Castling</h3>
        <iframe title="6. Castling" width="669" height="376" src="https://www.youtube.com/embed/TANpFOMX5MY" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>

        <h3>8. Pawn Promotion</h3>
        <iframe title="7. Pawn Promotion" width="669" height="376" src="https://www.youtube.com/embed/q8Mfa38m6xA" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
      </div>
    )
  }
}
