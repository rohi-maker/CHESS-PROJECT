export default ({
  ALIVE          : 0,

  WHITE_CHECKMATE: 1,
  BLACK_CHECKMATE: 2,

  WHITE_STALEMATE: 3,
  BLACK_STALEMATE: 4,
  DRAW_MATERIAL  : 5,

  DRAW_50        : 6,
  DRAW_REP       : 7,

  DRAW_AGREE     : 8,

  WHITE_RESIGN   : 9,
  BLACK_RESIGN   : 10,

  WHITE_CANCEL   : 11,
  BLACK_CANCEL   : 12,

  WHITE_NO_TIME  : 13,
  BLACK_NO_TIME  : 14,

  NOT_STARTED    : 15
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
  'Black resigned',

  'White canceled game',
  'Black canceled game',

  'White won (black was out of time)',
  'Black won (white was out of time)',

  'Waiting for players'
]
