/*
 * Copyright (C) 2022-present, Chenai Nakam(chenai.nakam@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hobby.chenai.nakam.test

import java.util.Scanner

/** 2. 构造矩形。 */
object Main4 {

  def main(args: Array[String]): Unit = {
    val sc = new Scanner(System.in)
    try {
      val n = sc.nextLine().split("[ =,，]+").filterNot(_.isEmpty).last.toInt

      val matrix = new Array[Array[Char]](n.ensuring(_ >= 2))

      for (i <- 0 until n) yield fillMatrix(sc, i, n, matrix)

      modMatrix(calcRect(findAsterisk(matrix), n), matrix)

      matrix.foreach { arr =>
        println(arr.mkString(""))
      }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally sc.close()
  }

  private def fillMatrix(sc: Scanner, i: Int, n: Int, mx: Array[Array[Char]]): Unit = {
    val line = sc.nextLine()
    //val chars = line.split(" +").filterNot(_.isEmpty).map(_.ensuring(_.length == 1).head)
    mx(i) =
      if (i == 0) line.trim.toCharArray.ensuring(_.length > n).takeRight(n)
      else line.trim.toCharArray.ensuring(_.length == n)
  }

  private def findAsterisk(mx: Array[Array[Char]]): ((Int, Int), (Int, Int)) = {
    val seq = for (i <- mx.indices; j <- mx(i).indices if mx(i)(j) == '*') yield (i, j)
    (seq.head, seq.tail.head)
  }

  private def calcRect(cod: ((Int, Int), (Int, Int)), n: Int): ((Int, Int), (Int, Int)) = {
    val ((row0, col0), (row1, col1)) = cod
    if (row0 == row1) { val row = n - 1 - row0; ((row, col0), (row, col1)) }
    else if (col0 == col1) { val col = n - 1 - col0; ((row0, col), (row1, col)) }
    else ((row0, col1), (row1, col0))
  }

  private def modMatrix(cod: ((Int, Int), (Int, Int)), mx: Array[Array[Char]]): Unit = {
    val ((row0, col0), (row1, col1)) = cod
    mx(row0)(col0) = '*'
    mx(row1)(col1) = '*'
  }
}

/*
n = 4,
A=--*-
  ----
 *---
  ----

n = 2,
A = -*
    -*
 */
