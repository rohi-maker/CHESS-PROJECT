import React, {Component} from 'react'
import ReCAPTCHA from 'react-google-recaptcha'

import {RECAPTCHA_SITE_KEY} from '../server'

let _reCaptcha = null
let _doWithReCaptchaResponse = null

const onReCaptchaResponse = (response) => {
  _reCaptcha.reset()
  _doWithReCaptchaResponse(response)
}

export default class Recaptcha extends Component {
  static onFormSubmit = (doWithReCaptchaResponse, e) => {
    _doWithReCaptchaResponse = doWithReCaptchaResponse
    _reCaptcha.execute()

    e.preventDefault()
    return false
  }

  render() {
    return (
      <ReCAPTCHA
        ref={r => _reCaptcha = r}
        sitekey={RECAPTCHA_SITE_KEY}
        size="invisible"
        onChange={onReCaptchaResponse}
      />
    )
  }
}
