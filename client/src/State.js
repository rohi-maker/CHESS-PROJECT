export default ({
  ALIVE        : 0,

  P1_CHECKMATE : 1,
  P2_CHECKMATE : 2,

  P1_STALEMATE :  3,
  P2_STALEMATE :  4,
  DRAW_MATERIAL: 5,

  DRAW_50       : 6,
  DRAW_REP      : 7,

  DRAW_AGREE   : 8,

  P1_RESIGN    : 9,
  P2_RESIGN    : 10,

  P1_CANCEL    : 11,
  P2_CANCEL    : 12,

  P1_NO_TIME   : 13,
  P2_NO_TIME   : 14,

  NOT_STARTED  : 15
})

export const STATE_DESCS = [
  'Game started',

  'White won (checkmate)',
  'Black won (checkmate)',

  'Draw (stalemate)',
  'Draw (stalemate)',
  'Draw (insufficient material)',

  'Draw (50 moves limit)',
  'Draw (3 fold repetition)',

  'Draw (agreement)',

  'White resigned',
  'Red resigned',

  'White canceled game',
  'Red canceled game',

  'White won (black was out of time)',
  'Black won (white was out of time)',

  'Waiting for players'
]
