import React, {Component} from 'react'
import {Alert, Grid} from 'react-bootstrap'
import {BrowserRouter as Router} from 'react-router-dom'

import Header from './header/Header'
import Footer from './footer/Footer'

import TopPage from './toppage/TopPage'
import GamePage from './gamepage/GamePage'
import EndedGamesPage from './gamelist/EndedGamesPage'

import LoginComboPage from './loginpage/LoginComboPage'

import RankingPage from './user/RankingPage'
import UserPage from './user/UserPage'

import SettingsPasswordPage from './settings/SettingsPasswordPage'
import SettingsEmailPage from './settings/SettingsEmailPage'

import ChessRulesPage from './pages/ChessRulesPage'
import TermsAndConditionsPage from './pages/TermsAndConditionsPage'

export default class App extends Component {
  constructor() {
    super()
    this.state = {flashDismissed: false}
  }

  render() {
    return (
      <Router>
        <Grid fluid>
          <Header />

          {!this.state.flashDismissed && window.$me.flash &&
            <Alert bsStyle="info" onDismiss={this.dismissFlash.bind(this)}>{window.$me.flash}</Alert>
          }

          {TopPage.route}
          {GamePage.route}

          {EndedGamesPage.route}

          {LoginComboPage.loginRoute}
          {LoginComboPage.registerRoute}
          {LoginComboPage.forgotPasswordRoute}

          {RankingPage.route}
          {UserPage.route}

          {SettingsPasswordPage.route}
          {SettingsEmailPage.route}

          {ChessRulesPage.route}
          {TermsAndConditionsPage.route}

          <Footer />
        </Grid>
      </Router>
    )
  }

  dismissFlash() {
    this.setState({flashDismissed: true})
  }
}
