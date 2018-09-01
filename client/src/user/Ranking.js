import React, {Component} from 'react'
import {Pagination, Table, Well} from 'react-bootstrap'
import {Link} from 'react-router-dom'

import serverUrl from '../server'

const RANKING_PAGE_SIZE = 20

export default class Ranking extends Component {
  constructor(props) {
    super(props)
    this.state = {activePage: 1, total: undefined, ranking: undefined}
  }

  render() {
    const {total, ranking} = this.state

    return (
      <Well>
        {!ranking && 'Loading...'}

        {ranking &&
          <React.Fragment>
            <Table striped bordered condensed hover>
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Username</th>
                  <th>Point</th>
                </tr>
              </thead>

              <tbody>
                {ranking.map((r, i) =>
                  <tr key={i}>
                    <td>{r.r}</td>
                    <td><Link to={`/users/${r.u}`}>{r.u}</Link></td>
                    <td>{r.p}</td>
                  </tr>
                )}
              </tbody>
            </Table>

            {total > RANKING_PAGE_SIZE &&
              <Pagination>{this.renderPaginationItems()}</Pagination>
            }
          </React.Fragment>
        }
      </Well>
    )
  }

  componentDidMount() {
    const {username} = this.props
    username ? this.search(username) : this.loadPage(1)
  }

  renderPaginationItems() {
    const {activePage, total} = this.state

    const totalPages = Math.ceil(total / RANKING_PAGE_SIZE)

    let items = []
    for (let page = 1; page <= totalPages; page++) {
      items.push(
        <Pagination.Item key={page} active={page === activePage} onClick={() => this.loadPage(page)}>
          {page}
        </Pagination.Item>
      )
    }

    return items
  }

  loadPage(page) {
    const startOrder = (page - 1) * RANKING_PAGE_SIZE + 1
    fetch(serverUrl(`/api/ranking/%20/${startOrder}?maxResults=${RANKING_PAGE_SIZE}`))
      .then(res => res.json())
      .then(([total, ranking]) => this.setState({activePage: page, total, ranking}))
      .catch(e => {})
  }

  search(username) {
    fetch(serverUrl(`/api/ranking/%20/search?maxResults=${RANKING_PAGE_SIZE}&q=${username}`))
      .then(res => res.json())
      .then(ranking => this.setState({
        activePage: -1,
        total: -1,
        ranking: ranking.sort((r1, r2) => r1.r - r2.r)
      }))
      .catch(e => {})
  }
}
