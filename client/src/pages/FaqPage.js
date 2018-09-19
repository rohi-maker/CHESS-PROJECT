import React, {Component} from 'react'
import {Link} from 'react-router-dom'
import {Route} from 'react-router-dom'

export default class FaqPage extends Component {
  static route = <Route exact path="/faq" component={FaqPage}/>

  render() {
    return (
      <div>
        <h2>Frequently Asked Questions</h2>

        <h3>What Rating system do you use?</h3>
        <p>Synergy Chess uses the Glicko2 rating system. Both Glicko and
          Glicko-2 rating systems are under public domain and found
          implemented on game servers online (like Lichess, Free Internet Chess
          Server, Chess.com, Counter Strike: Global Offensive, Team Fortress 2,
          Guild Wars 2, and Dominion Online).</p>

        <h3>Are the Rules different?</h3>
        <p>Some. All rules that apply to chess apply to Synergy Chess. There are
          some extra rules needed for Castling on lines 1 & 12, Pawn
          Promotion, First checkmate, and Double check. <Link to="/rules">Click here</Link> to learn
          extra rules.</p>

        <h3>Are there any special moves?</h3>
        <p>No. All pieces move the same as standard chess. Pawn promotion is
          slightly different. In Synergy Chess a Pawn can only be promoted to a
          piece that has already been captured (except King).</p>

        <h3>Is the Notation different?</h3>
        <p>Some. Standard Chess notation applies throughout Synergy Chess,
          with some additional notation needed.</p>

        <ul>
        <li><code>#</code> is used when the first King is in checkmate.</li>
        <li><code>x?</code> (? = position on board) will follow to indicate
        that the King has been captured & removed from the board.</li>
        </ul>

        <p>EG: <code>54. Qe9#xe11</code>
        states that the Queen has moved to E9, putting the first King
        (on E11) in checkmate.</p>

        <ul>
        <li><code>xe11</code> – the King has been captured, and removed from the board.</li>
        <li><code>+</code> is used in chess to state that a King is in check.</li>
        <li><code>++</code> is used to state that both Kings are now in check at the same time.
        If the attacking piece cannot be taken, then notation will continue with <code>x?</code>.</li>
        </ul>

        <p>EG: If Player 1 has Kings on <code>D12</code> & <code>G11</code>,
          and Player 2 moves a Knight from <code>D8</code> to <code>E10</code>,
          both Kings are now in check. If the Knight cannot be taken, Player 1 must choose which
          King to lose, and then  deal with the remaining check (Player 1 does not miss,
          or lose a turn for removing a King). <code>x?</code> states that this is the King Player 1
          opted to lose. This move could be recorded:</p>

        <p><code>43. Ne10++xg11      Ke11</code></p>

        <ul>
        <li><code>##</code> states that the second King is in checkmate & the game is now over.
        This can occur as a Double Checkmate (rare, but not impossible). Usually this would
        happen later in the game after the first King was captured earlier.</li>
        </ul>

        <h3>Can I play against computer instead of human?</h3>
        <p>Yes. The game is only programmed at level 1 for now. The computer
          can think 3 moves ahead. More logic, and levels will be upgraded soon.</p>

        <h3>Can I keep my current Title / Ranking?</h3>
        <p>If you have a current FIDE Title, or Ranking, Synergy Chess will honor
          this. Contact us with your details, and we will adjust your Title / Ranking.
          Titles, or Rankings from any other chess site will not be recognized.
          All players will start at 1500 points, unless FIDE rated.</p>

        <h3>Can I try for free?</h3>
        <p>Yes. Synergy Chess offers all players a 2 month Free Trial Period. If for
          any reason you don’t want to continue your subscription, cancel in
          time, and no fees will apply.</p>
      </div>
    )
  }
}
