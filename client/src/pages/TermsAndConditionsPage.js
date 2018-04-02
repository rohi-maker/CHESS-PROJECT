import React, {Component} from 'react'
import {Route} from 'react-router-dom'

export default class TermsAndConditionsPage extends Component {
  static route = <Route exact path="/terms" component={TermsAndConditionsPage}/>

  render() {
    return (
      <div>
        <h2>WEBSITE TERMS AND CONDITIONS</h2>

        <p>
          Please take the time to read these terms and conditions. By using Our Website and
          the Services and information offered on Our Website, you are agreeing to these
          terms and conditions.
        </p>

        <p>
          If you purchase products through our Website, there will be additional terms and
          conditions relating to the purchase. Please make sure you agree with these terms
          and conditions, which you will be directed to read prior to making your purchase.
        </p>

        <h3>Definitions</h3>

        <ul>
          <li><em>Services</em> means Playing Synergy Chess online.</li>
          <li><em>The Website</em> means the website www.synergychess.net</li>
          <li><em>We / Us</em> etc means Synergy Chess and any subsidiaries, affiliates, employees, officers, agents or assigns.</li>
        </ul>

        <h3>Accuracy of content</h3>

        <p>
          We have taken proper care and precautions to ensure that the information we
          provide on this Website is accurate. However, we cannot guarantee, nor do we
          accept any legal liability arising from or connected to, the accuracy, reliability,
          currency or completeness of anything contained on this Website or on any linked
          site.
        </p>

        <p>
          The information contained on this Website should not take the place of professional advice.
        </p>

        <h3>Use</h3>
        <p>
          The Website is made available for your use on your acceptance and compliance with
          these terms and conditions. By using this Website, you are agreeing to these terms
          and conditions.
        </p>

        <p>
          You agree that you will use this website in accordance with all applicable local, state,
          national and international laws, rules and regulations.
        </p>

        <p>
          You agree that you will not use, nor will you allow or authorise any third party to use,
          the Website for any purpose that is unlawful, defamatory, harassing, abusive,
          fraudulent or obscene way or in any other inappropriate way or in a way which
          conflicts with the Website or the Services.
        </p>

        <p>
          If you contribute to our forum (if any) or make any public comments on this Website
          which are, in our opinion, unlawful, defamatory, harassing, abusive, fraudulent or
          obscene or in any other way inappropriate or which conflict with the Website or the
          Services offered, then we may at our discretion, refuse to publish such comments
          and/or remove them from the Website.
        </p>

        <p>
          We reserve the right to refuse or terminate service to anyone at any time without
          notice or reason.
        </p>

        <h3>Passwords and logins</h3>
        <p>
          You are responsible for maintaining the confidentiality of your passwords and login
          details and for all activities carried out under your password and login.
        </p>

        <h3>Indemnification for loss or damage</h3>
        <p>
          You agree to indemnify Us and hold Us harmless from and against any and all
          liabilities or expenses arising from or in any way related to your use of this Website
          or the Services or information offered on this Website, including any liability or
          expense arising from all claims, losses, damages (actual and consequential), suits,
          judgments, litigation costs and solicitors fees of every kind and nature incurred by
          you or any third parties through you.
        </p>

        <h3>Intellectual property and copyrights</h3>
        <p>
          We hold the copyright to the content of this Website, including all uploaded files,
          layout design, data, graphics, articles, file content, codes, news, tutorials, videos,
          reviews, forum posts and databases contained on the Website or in connection with
          the Services. You must not use or replicate our copyright material other than as
          permitted by law. Specifically, you must not use or replicate our copyright material
          for commercial purposes unless expressly agreed to by Us, in which case we may
          require you to sign a Licence Agreement.
        </p>
        <p>
          If you wish to use content, images or other of our intellectual property, you should
          submit your request to us at the following email address:
        </p>
        <p><a href="mailto:info@synergychess.net">info@synergychess.net</a></p>

        <h3>Trademarks</h3>
        <p>
          The trademarks and logos contained on this Website are trademarks of Synergy
          Chess. Use of these trademarks is strictly prohibited except with Our express, written
          consent.
        </p>

        <h3>Links to external websites</h3>
        <p>
          This Website may contain links that direct you outside of this Website. These links
          are provided for your convenience and are not an express or implied indication that
          we endorse or approve of the linked Website, it’s contents or any associated
          website, product or service. We accept no liability for loss or damage arising out of
          or in connection to your use of these sites.
        </p>
        <p>
          You may link to our articles or home page. However, you should not provide a link
          which suggests any association, approval or endorsement on our part in respect to
          your website, unless we have expressly agreed in writing. We may withdraw our
          consent to you linking to our site at any time by notice to you.
        </p>

        <h3>Limitation of Liability</h3>
        <p>
          We take no responsibility for the accuracy of any of the content or statements
          contained on this Website or in relation to our Services. Statements made are by
          way of general comment only and you should satisfy yourself as to their accuracy.
          Further, all of our Services are provided without a warranty with the exception of any
          warranties provided by law. We are not liable for any damages whatsoever, incurred
          as a result of or relating to the use of the Website or our Services.
        </p>

        <h3>Information Collection</h3>
        <p>
          Use of information you have provided us with, or that we have collected and retained
          relating to your use of the Website and/or our Services, is governed by our Privacy
          Policy. By using this Website and the Services associated with this Website, you are
          agreeing to the Privacy Policy. To view our Privacy Policy and read more about why
          we collect personal information from you and how we use that information, contact
          us via email <a href="mailto:info@synergychess.net">info@synergychess.net</a>.
        </p>

        <h3>Confidentiality</h3>
        <p>
          All personal information you give us will be dealt with in a confidential manner in
          accordance with our Privacy Policy. However, due to circumstances outside of our
          control, we cannot guarantee that all aspects of your use of this Website will be
          confidential due to the potential ability of third parties to intercept and access such
          information.
        </p>
        <h3>Governing Law</h3>
        <p>
          These terms and conditions are governed by and construed in accordance with the
          laws of Queensland, Australia. Any disputes concerning this website are to be
          resolved by the courts having jurisdiction in Queensland.
          We retain the right to bring proceedings against you for breach of these Terms and
          Conditions, in your country of residence or any other appropriate country or
          jurisdiction.
        </p>

        <h3>Chat Violation</h3>
        <p>
          All chat participants are required to keep chat friendly, and free from abuse. Synergy
          Chess reserves the right to block any player from the chat room for any reason, and
          without notice, and may cancel subscriptions in severe cases. If your subscription is
          cancelled for Chat Violation no refunds will be given.
        </p>
      </div>
    )
  }
}
