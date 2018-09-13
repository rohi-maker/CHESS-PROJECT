import React, {Component} from 'react'
import {Nav, Navbar, NavItem} from 'react-bootstrap'
import {Link, withRouter} from 'react-router-dom'

import {USER_GUEST} from '../userType'
import serverUrl from '../server'

import logo from './logo.png'

class Header extends Component {
  render() {
    const {username, userType} = window.$me

    return (
      <Navbar inverse collapseOnSelect>
        <Navbar.Header>
          <Link to="/"><img src={logo} alt="Synergy Chess" /></Link>
          <Navbar.Toggle />
        </Navbar.Header>

        <Navbar.Collapse>
          <Nav>
            <NavItem eventKey={1} href="/" onClick={this.goTo.bind(this)}>
              Home
            </NavItem>

            <NavItem eventKey={2} href="/ended-games" onClick={this.goTo.bind(this)}>
              Ended games
            </NavItem>

            <NavItem eventKey={3} href="/ranking" onClick={this.goTo.bind(this)}>
              Ranking
            </NavItem>

            {userType === USER_GUEST &&
              <NavItem eventKey={4} href="/login" onClick={this.goTo.bind(this)}>
                Login | {username}
              </NavItem>
            }

            {userType !== USER_GUEST &&
              <NavItem eventKey={4} href={`/users/${username}`} onClick={this.goTo.bind(this)}>
                {username}
              </NavItem>
            }

            {userType !== USER_GUEST &&
              <NavItem eventKey={5} onClick={this.logout}>
                Logout
              </NavItem>
            }
          </Nav>
        </Navbar.Collapse>
      </Navbar>
    )
  }

  goTo(e) {
    e.preventDefault()
    this.props.history.push(e.target.getAttribute('href'))
    return false
  }

  logout() {
    fetch(serverUrl('/api/logout'), {
      credentials: 'include',
      method: 'DELETE'
    })
    .then(res => window.location = '/')
    .catch(e => window.location = '/')
  }
}

export default withRouter(Header)
