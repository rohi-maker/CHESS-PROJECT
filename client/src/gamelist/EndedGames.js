import React, {Component} from 'react'
import {Row} from 'react-bootstrap'

import UltimatePagination from '../UltimatePagination'
import EndedGame from './EndedGame'
import serverUrl from '../server'

export default class EndedGames extends Component {
  constructor(props) {
    super(props)
    this.state = {activePage: 1, pages: undefined, games: undefined}
  }

  render() {
    const {activePage, pages, games} = this.state
    if (!games) return 'Loading...'

    return (
      <React.Fragment>
        <Row>
          {games.map(game => <EndedGame key={game.id} game={game} />)}
        </Row>

        {pages > 1 &&
          <UltimatePagination
            currentPage={activePage}
            totalPages={pages}
            hidePreviousAndNextPageLinks
            onChange={this.loadPage}
          />
        }
      </React.Fragment>
    )
  }

  componentDidMount() {
    this.loadPage(1)
  }

  loadPage = (page) => {
    const {username} = this.props
    const pageUrl = `/api/ended-games?page=${page}`
    const url = username ? `${pageUrl}&username=${username}` : pageUrl
    fetch(serverUrl(url))
      .then(res => res.json())
      .then(({pages, games}) => this.setState({activePage: page, pages, games}))
      .catch(e => {})
  }
}
