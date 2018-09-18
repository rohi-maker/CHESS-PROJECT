import React, {Component} from 'react'
import {Link} from 'react-router-dom'
import {Grid} from 'react-bootstrap'

import './footer.css'

export default class Footer extends Component {
  render() {
    return (
      <footer id="footer">
        <Grid>
          <ul>
            <li><Link to="/rules">Chess rules</Link></li>
            <li> | <Link to="/terms">Terms and conditions</Link></li>
            <li> | <Link to="/faq">FAQ</Link></li>
            <li> | <a href="http://synergychess.net/">About</a></li>
            <li> | <a href="http://synergychess.net/contact/">Contact</a></li>
          </ul>

          <p id="copyright">Copyright © 2018 Robert J Olsson. All rights reserved. No part of Synergy Chess may be reproduced, nor copied in any form, nor by any means, without the prior written permission of the above named person.</p>
        </Grid>
      </footer>
    )
  }
}
