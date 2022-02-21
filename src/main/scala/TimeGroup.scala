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

import scala.annotation.tailrec
import scala.concurrent.duration.DurationInt

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 21/02/2022
  */
object TimeGroup extends App {
  println("TimeGroup start test:")

  final case class Utx(id: String, time: Long, in: String, out: String)

  val groupByTimeDuration = (1.minute.toMillis * 1.5).toLong
  val timeBegin           = System.currentTimeMillis - 13.day.toMillis

  val list = {
    var t = timeBegin
    val b = List.newBuilder[Utx]
    for (i <- 0 until 300) {
      if (i != 0 && i % 34 == 0) { t += (2 * groupByTimeDuration); t = t / 1000 * 1000 }
      else t += 2.seconds.toMillis
      b += Utx(id = i.toString, time = t, in = t.toString, out = (t + 32.seconds.toMillis).toString)
    }
    b.result
  }

  def grouped: List[List[Utx]] = {
    val builder = List.newBuilder[List[Utx]]
    def calc(lis: List[Utx]): (List[Utx], List[Utx]) = {
      // 需要确保`lis`是倒序排列的。
      val max = lis.head //.maxBy(_.time)
      var buf = Nil.asInstanceOf[List[Utx]]
      lis.span { u =>
        if (
          // 满足时间差关系的计划订单被排列在同一行。
          (max.time - u.time) <= groupByTimeDuration &&
          // 同一行不应有消化关系。
          buf.forall(_.in != u.out) && buf.forall(_.out != u.in)
        ) {
          buf ::= u; true
        } else false
      }
    }
    @tailrec
    def loop(lis: List[Utx]): List[List[Utx]] = {
      if (lis.isEmpty) builder.result
      else {
        val (l1, l2) = calc(lis)
        if (l1.nonEmpty) builder += l1
        loop(l2)
      }
    }
    // 倒序排列，以便于后续的`lis.span()`。
    loop(list.sortBy(_.time).reverse)
  }

  println("grouped:")
  grouped.foreach { lis =>
    println(s"$lis")
  }
  println("TimeGroup test finished!!!")
}
