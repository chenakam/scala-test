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

/** 3. 构造排列。
  *
  * 从 n 个不同元素中任取 m(m <= n)个元素，按照一定的顺序排列起来，叫做从 n 个不同元 素中取出 m 个元素的一个排列。当 m=n 时所有的排列情况叫全排列。
  * 现在想要从 1...N 这 N 个数的全排列中找到一种排列，使其第 L 位到第 R 位的和为 S， 求任意合法排列，若没有合法排列则输出 -1 。
  * 样例数据:
  * N=5，L=3，R=5，S=8，输出 [3, 4, 5, 2, 1](也可以是 [5, 2, 4, 3, 1], [5, 2, 1, 4, 3] 等任意合法排列)
  * N=5，L=3，R=4，S=1，输出 -1
  * N=2，L=2，R=2，S=2，输出 [1, 2]
  */
object Main5 {

  def main(args: Array[String]): Unit = {
    loadInput().foreach { case (n, l, r, s) =>
      val (b, arr) = fullPermutation(n, l, r, s)
      println(
        if (b) arr.mkString("[", ", ", "]")
        else -1
      )
    }
  }

  private def fullPermutation(N: Int, L: Int, R: Int, S: Int): (Boolean, Array[Int]) = {
    @inline def swap(arr: Array[Int], i: Int, j: Int): Unit = {
      if (i != j) { val temp = arr(i); arr(i) = arr(j); arr(j) = temp }
    }
    def loop(arr: Array[Int], i: Int = 0): Boolean = {
      if (i >= arr.length) arr.slice(L - 1, R).sum == S
      else {
        for (k <- i until arr.length) {
          swap(arr, i, k)
          if (loop(arr, i + 1)) return true
          swap(arr, i, k)
        }
        false
      }
    }
    val arr = (1 to N).toArray
    (loop(arr), arr)
  }

  private def loadInput(): Option[(Int, Int, Int, Int)] = {
    val sc = new Scanner(System.in)
    try {
      val arr = sc.nextLine().split(" *[,，]+ *").filterNot(_.isEmpty)
      val N   = arr(0).split("[= ]+").filterNot(_.isEmpty).last.toInt
      val L   = arr(1).split("[= ]+").filterNot(_.isEmpty).last.toInt
      val R   = arr(2).split("[= ]+").filterNot(_.isEmpty).last.toInt
      val S   = arr(3).split("[= ]+").filterNot(_.isEmpty).last.toInt
      Some((N, L, R, S))
    } catch {
      case e: Exception => e.printStackTrace(); None
    } finally sc.close()
  }
}
