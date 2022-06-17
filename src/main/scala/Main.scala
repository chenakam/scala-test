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

object Main {

  def main(args: Array[String]): Unit = {
    val sc = new Scanner(System.in)
    try {
      val arr = sc.nextLine().split("[ ]+")
      val n   = arr.head.toInt
      val m   = arr.tail.head.toInt
      //println(s"n:$n, m:$m")
      val array = new Array[Array[Int]](n)
      var index = 0
      while (index < n) {
        fillLine(sc, array, index)
        //array(index).ensuring(_.length == m)
        index += 1
      }
      val sums = for (
        i    <- 0 until m;
        j    <- 0 until n;
        iLen <- 1 to m - i;
        jLen <- 1 to n - j
      ) yield sum(i, iLen, j, jLen, array)

      println(sums.max)
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      sc.close()
    }
  }

  def fillLine(sc: Scanner, array: Array[Array[Int]], i: Int): Unit = {
    val arr = sc.nextLine().split("[ ]+").map(_.toInt)
    array(i) = arr
  }

  def sum(i: Int, iLen: Int, j: Int, jLen: Int, arr: Array[Array[Int]]): Int = {
    println(s"i:$i, il:$iLen, j:$j, jl:$jLen")
    arr.slice(j, j + jLen).map(_.slice(i, i + iLen).sum).sum
  }
}
