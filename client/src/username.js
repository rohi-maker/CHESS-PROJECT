export const isGuest = (username) =>
  username.indexOf(' (guest)') >= 0

export const isBot = (username) =>
  username.indexOf(' (computer)') >= 0

export const isLoggedInHuman = (username) =>
  !isGuest(username) && !isBot(username)

export const withoutGuest = (username) => {
  const idx = username.indexOf(' (guest)')
  return (idx >= 0) ? username.substring(0, idx) : username
}
