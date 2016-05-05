package com.mmatyus.go.algorithms;

import com.mmatyus.go.ProgressContainer;
import com.mmatyus.go.algorithms.transposition.TranspositionTable;
import com.mmatyus.go.algorithms.transposition.TranspositionTableEntry;
import com.mmatyus.go.algorithms.transposition.TranspositionType;
import com.mmatyus.go.model.Algorithm;
import com.mmatyus.go.model.Board;
import com.mmatyus.go.model.Robot;

public class NegaMaxRobot implements Robot {
  public static final int     MAX_RUN_DEPTH = 7;
  private final Algorithm     algorithm;
  private final int           runDepth;
  private int                 counting;
  private TranspositionTable  table         = new TranspositionTable();
  protected Board             board;
  protected ProgressContainer progressContainer;

  public void setBoard( Board board ) {
    this.board = board;
  }

  public void setProgressContainer( ProgressContainer progressContainer ) {
    this.progressContainer = progressContainer;
  }

  public NegaMaxRobot( Algorithm a, int param ) {
    this.algorithm = a;
    this.runDepth = algorithm.option( param );
  }

  @Override
  public int move( Board board, ProgressContainer pc ) {
    if( board.isGameOver() )
      throw new IllegalStateException( "PROGRAMMER ERROR" );

    System.out.println( "Max steps: " + getMaxSteps( board.availableActions().size() - 1, runDepth ) );
    pc.reset();
    pc.setSum( getMaxSteps( board.availableActions().size() - 1, runDepth ) );
    int bestMove;
    if( board.getnumberOfEmptyCells() == board.getNumberOfCells() )
      return ( board.getNumberOfCells() - 1 ) / 2;
    Board myBoard = board.clone();
    counting = 0;
    int[] result = null;
    switch( algorithm ) {
      case NEGAMAX:
        result = NegaMax( pc, myBoard, runDepth, 1 );
        break;
      case NEGAMAX_AB:
        result = Negamax_AB( pc, myBoard, runDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1 );
        break;
      case NEGAMAX_AB_TT:
        result = Negamax_AB_TT( pc, myBoard, runDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1 );
        break;
      default:
        throw new IllegalStateException( "PROGRAMMER ERROR" );
    }
    bestMove = result[0];
    System.out.println( "Lépésszám: " + counting );
    if( result[1] < 0 )
      bestMove = Board.PASS_MOVE;
    System.out.println( "move: " + bestMove + " value: " + result[1] );
    return bestMove;
  }

  private int[] NegaMax( ProgressContainer progressContainer, Board board, int depth, int sign ) {
    counting++;
    if( counting % 50_000 == 0 )
      progressContainer.setActual( counting );

    int[] result = new int[2];
    result[0] = Integer.MIN_VALUE;
    result[1] = Integer.MIN_VALUE;
    if( depth == 0 || board.isGameOver() ) {
      result[0] = board.getLastMove();
      result[1] = sign * evaluateStrategy_2( board );
      return result;
    }
    for( int pos : board.orderMoves() ) {
      if( pos < 0 )
        continue;
      Board newBoard = board.clone();
      newBoard.move( pos );
      int[] subResult = NegaMax( progressContainer, newBoard, depth - 1, -sign );
      subResult[1] = -subResult[1];
      if( subResult[1] > result[1] ) {
        result[0] = pos;
        result[1] = subResult[1];
      }
    }
    return result;
  }

  private int[] Negamax_AB( ProgressContainer progressContainer, Board board, int depth, int alpha, int beta, int sign ) {
    counting++;
    if( counting % 50_000 == 0 )
      progressContainer.setActual( counting );
    int[] result = new int[2];
    result[0] = Integer.MIN_VALUE;
    result[1] = Integer.MIN_VALUE;
    if( depth == 0 || board.isGameOver() ) {
      result[0] = board.getLastMove();
      result[1] = sign * evaluateStrategy( board );
      return result;
    }
    for( int pos : board.availableActions() ) {
      if( pos < 0 )
        continue;
      Board newBoard = board.clone();
      newBoard.move( pos );
      int[] subResult = Negamax_AB( progressContainer, newBoard, depth - 1, -beta, -alpha, -sign );
      subResult[1] = -subResult[1];
      if( subResult[1] > result[1] ) {
        result[0] = pos;
        result[1] = subResult[1];
      }
      alpha = Math.max( alpha, subResult[1] );
      if( beta <= alpha )
        break;
    }
    return result;
  }

  private int[] Negamax_AB_TT( ProgressContainer progressContainer, Board board, int depth, int alpha, int beta, int sign ) {
    counting++;
    if( counting % 50_000 == 0 )
      progressContainer.setActual( counting );
    int[] result = new int[2];
    result[0] = Integer.MIN_VALUE;
    result[1] = Integer.MIN_VALUE;

    TranspositionTableEntry tte = table.getResult( board.getZobristHashKey() );
    if( tte != null && tte.getDepth() >= depth ) {
      if( tte.getType() == TranspositionType.EXACT_VALUE ) { // stored value is exact
        result[0] = tte.getBestMove();
        result[1] = tte.getEvaluation();
        return result;
      }
      if( tte.getType() == TranspositionType.LOWERBOUND && tte.getEvaluation() > alpha )
        alpha = tte.getEvaluation(); // update lowerbound alpha if needed
      else if( tte.getType() == TranspositionType.UPPERBOUND && tte.getEvaluation() < beta )
        beta = tte.getEvaluation(); // update upperbound beta if needed
      if( alpha >= beta ) {
        result[0] = tte.getBestMove();
        result[1] = tte.getEvaluation();
        return result;
      }
    }
    if( depth == 0 || board.isGameOver() ) {
      result[0] = board.getLastMove();
      result[1] = sign * evaluateStrategy( board );
      if( result[1] <= alpha ) // a lowerbound value
        table.addResult( board.getZobristHashKey(), TranspositionType.LOWERBOUND, result[0], result[1], depth );
      else if( result[1] >= beta ) // an upperbound value
        table.addResult( board.getZobristHashKey(), TranspositionType.UPPERBOUND, result[0], result[1], depth );
      else
        // a true minimax value
        table.addResult( board.getZobristHashKey(), TranspositionType.EXACT_VALUE, result[0], result[1], depth );
      return result;
    }
    for( int pos : board.availableActions() ) {
      if( pos < 0 )
        continue;
      Board newBoard = board.clone();
      newBoard.move( pos );
      int[] subResult = Negamax_AB_TT( progressContainer, newBoard, depth - 1, -beta, -alpha, -sign );
      subResult[1] = -subResult[1];
      if( subResult[1] > result[1] ) {
        result[0] = pos;
        result[1] = subResult[1];
      }
      alpha = Math.max( alpha, subResult[1] );
      if( beta <= alpha )
        break;
    }

    if( result[1] <= alpha ) // a lowerbound value
      table.addResult( board.getZobristHashKey(), TranspositionType.LOWERBOUND, result[0], result[1], depth );
    else if( result[1] >= beta ) // an upperbound value
      table.addResult( board.getZobristHashKey(), TranspositionType.LOWERBOUND, result[0], result[1], depth );
    else
      // a true minimax value
      table.addResult( board.getZobristHashKey(), TranspositionType.EXACT_VALUE, result[0], result[1], depth );
    return result;
  }

  private int evaluateStrategy( Board board ) {
    int us = 1 - board.getNextPlayer();
    int score = 0;
    int boardSize = board.sideLength;
    int boardArea = boardSize * boardSize;
    int[] counted = new int[boardArea + 1];
    for( int pos = 0; pos < boardArea; ++pos ) {
      counted[pos] = 0;
    }
    for( int pos = 0; pos < boardArea; ++pos ) {
      int cell = board.getState( pos );
      if( cell >= 0 ) {
        int ps = board.getLifeOfShape( pos );
        if( counted[ps] == 0 ) {
          score += ( cell == us ? ps : 0 );
          counted[ps] = 1;
        }
      }
    }
    return score;
  }

  public static int evaluateStrategy_2( Board board ) {
    int nextPlayer = board.getNextPlayer();
    int currentPlayer = 1 - nextPlayer;

    int[] liberties = board.getLifesForColor();
    int[] euler = board.getEulerNumber();
    int[] numOfPieces = board.getNumOfPieces();
    int score = Math.min( Math.max( ( liberties[currentPlayer] - liberties[nextPlayer] ), -5 ), 5 ) + -4 * ( euler[currentPlayer] - euler[nextPlayer] ) + 5 * ( numOfPieces[currentPlayer] - numOfPieces[nextPlayer] ) - 20 * numOfPieces[2 + currentPlayer];
    return score;
  }

  @Override
  public Integer call() throws Exception {
    return move( board, progressContainer );
  }

  private int getMaxSteps( int amountOfAvailableActions, int depth ) {
    int result = 0;
    int k = 0;
    int subResult;
    for( int i = depth; i > 0; i-- ) {
      k = amountOfAvailableActions - i;
      System.out.println( amountOfAvailableActions + " " + k );
      if( k < 1 )
        k = 1;
      subResult = 1;
      for( int j = amountOfAvailableActions; j > k; j-- ) {
        subResult *= j;
      }
      result += subResult;
    }
    return result;
  }

}
