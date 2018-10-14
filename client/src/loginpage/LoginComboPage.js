import React, {Component} from 'react'
import {Col, Jumbotron, Panel, Row, Tab, Tabs} from 'react-bootstrap'
import FontAwesome from 'react-fontawesome'
import {Route} from 'react-router-dom'

import UsernameLogin from './UsernameLogin'
import UsernameRegister from './UsernameRegister'
import ForgotPassword from './ForgotPassword'

import ReCaptcha from './ReCaptcha'
import serverUrl from '../server'

export default class LoginComboPage extends Component {
  static loginRoute = <Route exact path="/login" component={LoginComboPage}/>
  static registerRoute = <Route exact path="/register" component={LoginComboPage}/>
  static forgotPasswordRoute = <Route exact path="/forgot-password" component={LoginComboPage}/>

  render() {
    const href = window.location.href
    const activeTab = href.indexOf('/login') >= 0
      ? 1
      : href.indexOf('/register') >= 0
        ? 2
        : 3

    return (
      <Row>
        <Col md={5}>
          <Jumbotron>
            <p>
              <a href={serverUrl('/api/login/facebook')}>
                <FontAwesome name="facebook" fixedWidth={true} />{' '}
                Login with Facebook
              </a>
            </p>

            <p>
              <a href={serverUrl('/api/login/twitter')}>
                <FontAwesome name="twitter" fixedWidth={true} />{' '}
                Login with Twitter
              </a>
            </p>

            <p>
              <a href={serverUrl('/api/login/gplus')}>
                <FontAwesome name="google-plus" fixedWidth={true} />{' '}
                Login with G+
              </a>
            </p>
          </Jumbotron>
        </Col>

        <Col md={7}>
          <ReCaptcha />

          <Tabs defaultActiveKey={activeTab} id="login-email">
            <Tab eventKey={1} title="Login">
              <Panel><Panel.Body>
                <br />
                <UsernameLogin onFormSubmit={this.onFormSubmit} />
              </Panel.Body></Panel>
            </Tab>

            <Tab eventKey={2} title="Register">
              <Panel><Panel.Body>
                <br />
                <UsernameRegister onFormSubmit={this.onFormSubmit} />
              </Panel.Body></Panel>
            </Tab>

            <Tab eventKey={3} title="Forgot password">
              <Panel><Panel.Body>
                <br />
                <ForgotPassword onFormSubmit={this.onFormSubmit} />
              </Panel.Body></Panel>
            </Tab>
          </Tabs>
        </Col>
      </Row>
    )
  }

  onFormSubmit = (doWithCaptchaResponse, e) => {
    ReCaptcha.onFormSubmit(doWithCaptchaResponse, e)
  }
}
