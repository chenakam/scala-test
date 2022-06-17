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

/** 1. 均富卡。
  *
  * 给定 n 个数(例如 [4, 3, 1, 7, 1])，每次操作是选择其中若干个数，将它们替换为它们的平均 数。
  * 例如:选择第 1，2，3，5 四个数，对应的数是 4，3，1，1，它们的平均数为 2.25，所以操 作后这 5 个数就变为了 [2.25，2.25，2.25，7，2.25]。
  * 假定玩家可以进行任意次操作(也可以为 0 次)，若给定一个 w，问最多可以有多少个数能 不小于 w。
  * （实际没说明白。应该是，问：
  *   最多可以有多少个数（输入的数）能够【使得在任意次操作后，均值】不小于 w。）
  *
  * 样例数据:
  * [11, 9, 11, 9]，w = 10，输出 4
  * [5, 1, 2, 1]，w = 3，输出 2
  * [4, 3]，w = 5，输出 0
  */
object Main3 {

  def main(args: Array[String]): Unit = {
    loadInput().foreach { case (ints, w) =>
      //println(ints.mkString(", "))
      //println(w)
      println { maxCount(ints, w) }
    }
  }

  private def maxCount(arr: Array[Int], w: Int): Int = {
    val list = arr.sorted.toList
    for (
      i <- list.indices;
      n = list.length - i
      if list.drop(i).sum / n >= w
    ) return n
    0
  }

  private def loadInput(): Option[(Array[Int], Int)] = {
    val sc = new Scanner(System.in)
    try {
      val arr =
        sc.nextLine()
          .split("[ ]*[,，\\[\\]]+[ ]*")
          .filterNot(_.isEmpty)
      val w    = arr.last.split("[= ]+").last.toInt
      val ints = arr.dropRight(1).map { _.toInt }
      Some(ints, w)
    } catch {
      case e: Exception => e.printStackTrace(); None
    } finally sc.close()
  }
}
