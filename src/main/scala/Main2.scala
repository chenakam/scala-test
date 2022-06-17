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

/** 给出一行数组，第二行一个数字表示 k。
  * 求把数组中小于 k 的组合在一起（都相邻，不限位置和顺序）时，
  * 最少移动次数。
  */
object Main2 {

  def main(args: Array[String]): Unit = {
    val sc = new Scanner(System.in)
    try {
      val line = sc.nextLine()
      val k    = sc.nextInt()
      require(k >= -100 && k <= 100)
      val intArr =
        line.split("[ ]+")
          .filterNot(_.isEmpty)
          .map { _.toInt }
      intArr.foreach { n =>
        require(n >= -100 && n <= 100)
      }
      val ids = (for (i <- intArr.indices if intArr(i) < k) yield i).toArray
      val res = (
        for (i <- ids if i + ids.length <= intArr.length) yield times(i, ids)
      ).min
      println(res)
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      sc.close()
    }
  }

  def times(i: Int, ids: Array[Int]): Int =
    (for (x <- i + 1 until i + ids.length if !ids.contains(x)) yield x).length
}
