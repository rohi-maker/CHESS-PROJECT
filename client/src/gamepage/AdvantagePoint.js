const points = {
  pawn: 1,
  knight: 3,
  bishop: 4,
  rook: 5,
  queen: 9,
  king: 0
}

export default function AdvantagePoint({myCapturedPieces, enemyCapturedPieces}) {
  let diff = 0

  Object.keys(points).forEach(piece => {
    const numEnemy = enemyCapturedPieces[piece] || 0
    const numMe = myCapturedPieces[piece] || 0
    diff += (numEnemy - numMe) * points[piece]
  })

  return diff <= 0 ? null : `+${diff}`
}
