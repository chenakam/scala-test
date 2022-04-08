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

import scalax.collection.Graph
import scalax.collection.GraphEdge.{DiEdge, EdgeCopy, ExtendedKey, NodeProduct}
import scalax.collection.GraphPredef._

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 04/03/2022
  */
object GraphTopo extends App {
  println("GraphTopo start test:")
  println

  println("topological sort:")
  println

  // format: off

  val root = "A"
  
//  val g = Graph(root ~> "B1", root ~> "B2", root ~> "B3",
//                "B1" ~> "C1", "B2" ~> "C2", "B2" ~> "C3", "B3" ~> "C3", "B3" ~> "C2", "B3" ~> "C1",
//                "C1" ~> "D1", "C2" ~> "D2", "C3" ~> "D3")

//  topological sort:
//  (0,ArrayBuffer(A))
//  (1,ArrayBuffer(B2, B1, B3))
//  (2,ArrayBuffer(C3, C1, C2))
//  (3,ArrayBuffer(D3, D1, D2))
//  topological sort (by component):
//  Right(TopologicalOrder(A, B2, B1, B3, C3, C1, C2, D3, D1, D2))

//  val g = Graph(root ~> "B2", root ~> "B3", root ~> "B1",
//                "B3" ~> "C2", "B1" ~> "C1", "B3" ~> "C3", "B2" ~> "C2", "B3" ~> "C1", "B2" ~> "C3",
//                "C2" ~> "D2", "C1" ~> "D1", "C3" ~> "D3")

//  topological sort:
//  (0,ArrayBuffer(A))
//  (1,ArrayBuffer(B2, B1, B3))
//  (2,ArrayBuffer(C3, C1, C2))
//  (3,ArrayBuffer(D3, D1, D2))
//  topological sort (by component):
//  Right(TopologicalOrder(A, B2, B1, B3, C3, C1, C2, D3, D1, D2))

  val g = Graph(root ~> "B1", root ~> "B2", root ~> "B3", root ~> "e",
                "B3" ~> "C2", "B1" ~> "C1", "B3" ~> "C3", "B2" ~> "C2", "B3" ~> "C1", "B2" ~> "C3",
                "B3" ~> "D2", "C2" ~> "D2", "C1" ~> "D2", "C2" ~> "D1", "C3" ~> "D3", "B1" ~> "D1", "C1" ~> "C2")

//  (0,ArrayBuffer(A))
//  (1,ArrayBuffer(B1, B2, B3))
//  (2,ArrayBuffer(C1, C3))
//  (3,ArrayBuffer(C2, D3))
//  (4,ArrayBuffer(D2, D1))
//  topological sort (by component):
//  Right(TopologicalOrder(A, B1, B2, B3, C1, C3, C2, D3, D2, D1))

//  topological sort:
//  (0,ArrayBuffer(A))
//  (1,ArrayBuffer(B2, B1, B3))
//  (2,ArrayBuffer(C3, C1))
//  (3,ArrayBuffer(D3, C2))
//  (4,ArrayBuffer(D1, D2))
//  topological sort (by component):
//  Right(TopologicalOrder(A, B2, B1, B3, C3, C1, D3, C2, D1, D2))

  // format: on

  (g get "B1").topologicalSort().fold(
    cycleNode => println("cycle node:" + cycleNode),
    _.toLayered foreach println
  )

  // 注意：`g.topologicalSort`和`g.topologicalSortByComponent`要么都加括号，要么都不加，才能确保结果一致（实测：不要加括号，使用默认隐式参数）。
  println
  println("topological sort (by component):")
  g.topologicalSortByComponent foreach println

  println
  println("airport flight:")
  import Flight.ImplicitEdge
  val (ham, ny) = (Airport("HAM"), Airport("JFK")) // two nodes
  val flight    = ham ~> ny ## "007"               // flightNo 007: HAM~>JFK (007)
  val gfl       = Graph(flight)                    // Graph(HAM, JFK, HAM~>JFK (007))
  println { flight }
  println { gfl }

  println
  println("GraphTopo test finished!!!")
}

case class Airport(val code: String) {
  override def toString = code // without Airport-prefix
}

case class Flight[+N](fromAirport: N, toAirport: N, flightNo: String) extends DiEdge[N](NodeProduct(fromAirport, toAirport)) with ExtendedKey[N] with EdgeCopy[Flight] with OuterEdge[N, Flight] {
  private def this(nodes: Product, flightNo: String) {
    this(nodes.productElement(0).asInstanceOf[N], nodes.productElement(1).asInstanceOf[N], flightNo)
  }
  def keyAttributes                         = Seq(flightNo)
  override def copy[NN](newNodes: Product)  = new Flight[NN](newNodes, flightNo)
  override protected def attributesToString = s" ($flightNo)"
}

object Flight {

  implicit final class ImplicitEdge[A <: Airport](val e: DiEdge[A]) extends AnyVal {
    def ##(flightNo: String) = new Flight[A](e.source, e.target, flightNo) // or new Flight[A](e.nodes, flightNo)
  }
}
