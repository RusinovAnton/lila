package lila.game

import chess.Color

import ornicar.scalalib.Random

import java.security.SecureRandom

final class IdGenerator(gameRepo: GameRepo)(implicit ec: scala.concurrent.ExecutionContext) {

  import IdGenerator._

  def game: Fu[Game.ID] = {
    val id = uncheckedGame
    gameRepo.exists(id).flatMap {
      case true  => game
      case false => fuccess(id)
    }
  }

}

object IdGenerator {

  private[this] val secureRandom     = new SecureRandom()
  private[this] val whiteSuffixChars = ('0' to '4') ++ ('A' to 'Z') mkString
  private[this] val blackSuffixChars = ('5' to '9') ++ ('a' to 'z') mkString

  def uncheckedGame: Game.ID = Random nextString Game.gameIdSize

  def player(color: Color): Player.ID = {
    // Trick to avoid collisions between player ids in the same game.
    val suffixChars = color.fold(whiteSuffixChars, blackSuffixChars)
    val suffix      = suffixChars(secureRandom nextInt suffixChars.size)
    Random.secureString(Game.playerIdSize - 1) + suffix
  }
}
