package hu.mmatyus.algorithms;

import hu.mmatyus.algorithms.tt.TranspositionTable;
import hu.mmatyus.algorithms.tt.TranspositionTableEntry;
import hu.mmatyus.algorithms.tt.TranspositionTableEntry.Type;
import hu.mmatyus.model.Algorithm;
import hu.mmatyus.model.Board;
import hu.mmatyus.model.GameConfig;
import hu.mmatyus.model.Player;
import hu.mmatyus.model.Robot;

import java.util.ArrayList;
import java.util.List;

public class NegaMaxRobot implements Robot {
  public static final int    MAX_RUN_DEPTH = 7;
  private final Algorithm    algorithm;
  private final int          runDepth;
  private int                counting;
  private TranspositionTable table         = new TranspositionTable();

  /*
   * public NegaMaxRobot( int runDepth ) {
   * if( runDepth > MAX_RUN_DEPTH )
   * throw new IllegalStateException( "PROGRAMMER ERROR" );
   * this.runDepth = runDepth;
   * }
   */

  public NegaMaxRobot( Player player ) {
    if( player.type != Player.Type.COMPUTER || player.algo == Algorithm.UCT )
      throw new IllegalStateException( "PROGRAMMER ERROR" );
    this.algorithm = player.algo;
    this.runDepth = algorithm.option( player.param );
  }

  @Override
  public int move( Board board ) {
    if( board.isGameOver() )
      throw new IllegalStateException( "PROGRAMMER ERROR" );
    int bestMove;
    if( board.getnumberOfEmptyCells() == board.getNumberOfCells() )
      return ( board.getNumberOfCells() - 1 ) / 2;
    Board myBoard = board.clone();
    counting = 0;
    int[] result = null;
    switch( algorithm ) {
      case NEGAMAX:
       result = NegaMax( myBoard, runDepth, 1 );
        break;
      case NEGAMAX_AB:
        result = Negamax_AB( myBoard, runDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1 );
        break;
      case NEGAMAX_AB_TT:
        result = Negamax_AB_TT( myBoard, runDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1 );
        break;
      default:
        throw new IllegalStateException( "PROGRAMMER ERROR" );
    }
    //int[] result = negaMax( myBoard, runDepth, 1 );
    //int[] result = alphaBetaPruning( myBoard, runDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1 );
    //int[] result = alphaBetaPruningWithTranspositionTable( myBoard, runDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, 1 );
    bestMove = result[0];
    System.out.println( "Lépésszám: " + counting );
    if( result[1] < 0 )
      bestMove = Board.PASS_MOVE;
    System.out.println( "move: " + bestMove + " value: " + result[1] );
    return bestMove;
  }

  private int[] NegaMax( Board board, int depth, int sign ) {
    counting++;
    int[] result = new int[2];
    result[0] = Integer.MIN_VALUE;
    result[1] = Integer.MIN_VALUE;
    if( depth == 0 || board.isGameOver() ) {
      result[0] = board.getLastMove();
      result[1] = sign * evaluateStrategy_2( board );
      //System.out.println(depth + "\tpos: "+result[0] + "\tscore: "+result[1] + "\tsteps: ");
      return result;
    }
    for( int pos : board.orderMoves() ) {
      if( pos < 0 )
        continue;
      Board newBoard = board.clone();
      newBoard.move( pos );
      int[] subResult = NegaMax( newBoard, depth - 1, -sign );
      subResult[1] = -subResult[1];
      if( subResult[1] > result[1] ) {
        result[0] = pos;
        result[1] = subResult[1];
        //System.out.println(depth + "\tpos: "+subResult[0] + "\tscore: "+subResult[1]);
      }
    }
    //System.out.println(depth + "\tpos: "+result[0] + "\tscore: "+result[1]);
    return result;
  }

  private int[] Negamax_AB( Board board, int depth, int alpha, int beta, int sign ) {
    counting++;
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
      int[] subResult = Negamax_AB( newBoard, depth - 1, -beta, -alpha, -sign );
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

  private int[] Negamax_AB_TT( Board board, int depth, int alpha, int beta, int sign ) {
    counting++;
    int[] result = new int[2];
    result[0] = Integer.MIN_VALUE;
    result[1] = Integer.MIN_VALUE;

    TranspositionTableEntry tte = table.getResult( board.getZobristHashKey() );
    if( tte != null && tte.getDepth() >= depth ) {
      if( tte.getType() == Type.EXACT_VALUE ) { // stored value is exact
        result[0] = tte.getBestMove();
        result[1] = tte.getEvaluation();
        return result;
      }
      if( tte.getType() == Type.LOWERBOUND && tte.getEvaluation() > alpha )
        alpha = tte.getEvaluation(); // update lowerbound alpha if needed
      else if( tte.getType() == Type.UPPERBOUND && tte.getEvaluation() < beta )
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
        table.addResult( board.getZobristHashKey(), Type.LOWERBOUND, result[0], result[1], depth );
      else if( result[1] >= beta ) // an upperbound value
        table.addResult( board.getZobristHashKey(), Type.UPPERBOUND, result[0], result[1], depth );
      else
        // a true minimax value
        table.addResult( board.getZobristHashKey(), Type.EXACT_VALUE, result[0], result[1], depth );
      return result;
    }
    for( int pos : board.availableActions() ) {
      if( pos < 0 )
        continue;
      Board newBoard = board.clone();
      newBoard.move( pos );
      int[] subResult = Negamax_AB_TT( newBoard, depth - 1, -beta, -alpha, -sign );
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
      table.addResult( board.getZobristHashKey(), Type.LOWERBOUND, result[0], result[1], depth );
    else if( result[1] >= beta ) // an upperbound value
      table.addResult( board.getZobristHashKey(), Type.LOWERBOUND, result[0], result[1], depth );
    else
      // a true minimax value
      table.addResult( board.getZobristHashKey(), Type.EXACT_VALUE, result[0], result[1], depth );
    return result;
  }
/*
  private int positionScore( int boardSize, int pos ) {
    final int cx = boardSize / 2;
    final int cy = boardSize / 2;
    int x = pos % boardSize;
    int y = pos / boardSize;
    int dx = Math.abs( x - cx );
    int dy = Math.abs( y - cy );
    return boardSize - 1 - dx - dy; // -> 0 .. boardSize - 1
  }

  private List<Integer> getNegighboursPos( int boardSize, int pos ) {
    List<Integer> result = new ArrayList<>();
    int x = pos % boardSize;
    int y = pos / boardSize;
    int p;
    for( int i = -1; i <= 1; i++ ) {
      for( int j = -1; j <= 1; j++ ) {
        p = y * boardSize + x;
        if( 0 <= p && p <= boardSize * boardSize && p != pos ) {
          result.add( p );
        }
      }
    }
    return result;
  }
*/
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
    //int score = Math.min( Math.max( ( liberties[currentPlayer] - liberties[nextPlayer] ), -5 ), 5 ) + -4 * ( euler[currentPlayer] - euler[nextPlayer] ) + 5 * ( numOfPieces[currentPlayer] - numOfPieces[nextPlayer] ) - ( numOfPieces[2 + currentPlayer] - numOfPieces[2 + nextPlayer] );
    int score = Math.min( Math.max( ( liberties[currentPlayer] - liberties[nextPlayer] ), -5 ), 5 ) + -4 * ( euler[currentPlayer] - euler[nextPlayer] ) + 5 * ( numOfPieces[currentPlayer] - numOfPieces[nextPlayer] ) - 20 * numOfPieces[2 + currentPlayer];
    return score;
  }
}
