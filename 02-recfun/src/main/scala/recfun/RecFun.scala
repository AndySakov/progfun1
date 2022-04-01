package recfun

import scala.annotation.tailrec

object RecFun extends RecFunInterface :

  def main(args: Array[String]): Unit =
    println("Pascal's Triangle")
    for row <- 0 to 10 do
      for col <- 0 to row do
        print(s"${pascal(col, row)} ")
      println()

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int =
    if (c == 0 || (c == r)) 1
    else
      pascal(c - 1, r - 1) + pascal(c, r - 1)

  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean =
    @tailrec
    def isBalanced(c: List[Char], counter: Int = 0): Boolean =
      if c.isEmpty then counter == 0
      else
        val res = c.head match {
          case x if x == '(' => (true, counter + 1)
          case x if x == ')' => if counter == 0 then (false, counter) else (true, counter - 1)
          case _ => (true, counter)
        }
        if res._1 then isBalanced(c.tail, res._2) else false
    isBalanced(chars)

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int =
    if money < 0 | coins.isEmpty then 0
    else if money == 0 then 1
    else countChange(money, coins.tail) + countChange(money - coins.head, coins)
