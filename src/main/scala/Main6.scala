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
import scala.annotation.tailrec

/** 4. 构造旋转矩阵。 */
object Main6 {

  def main(args: Array[String]): Unit = {
    loadInput().foreach { case (m, n) =>
      genMatrix(m, n).foreach { arr =>
        println { arr.mkString(" ") }
      }
    }
  }

  //   1  2  3  4
  //  10 11 12  5
  //   9  8  7  6
  private def genMatrix(row: Int, col: Int): Array[Array[Int]] = {
    val default   = -1
    val arr       = Array.fill(row, col)(default)
    var direction = 0
    var i         = 0
    var j         = 0

    @inline
    def isEmpty(i: Int, j: Int): Boolean = arr(i)(j) == default
    @tailrec
    def loop(): Unit = {
      var b = false
      direction match {
        case 0 => if (j + 1 < col && isEmpty(i, j + 1)) j += 1 else b = true
        case 1 => if (i + 1 < row && isEmpty(i + 1, j)) i += 1 else b = true
        case 2 => if (j - 1 >= 0 && isEmpty(i, j - 1)) j -= 1 else b = true
        case 3 => if (i - 1 >= 0 && isEmpty(i - 1, j)) i -= 1 else b = true
      }
      if (b) {
        direction = (direction + 1) % 4
        loop()
      }
    }
    val max = row * col
    for (n <- 1 to max) {
      arr(i)(j) = n
      if (n < max) loop()
    }
    arr
  }

  private def loadInput(): Option[(Int, Int)] = {
    val sc = new Scanner(System.in)
    try {
      val m = sc.nextInt()
      val n = sc.nextInt()
      Some((m, n))
    } catch {
      case e: Exception => e.printStackTrace(); None
    } finally sc.close()
  }
}
