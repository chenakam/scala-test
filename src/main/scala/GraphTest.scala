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
import scalax.collection.GraphPredef._
import scalax.collection.edge.Implicits._

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 04/03/2022
  */
object GraphTest extends App {
  println("GraphTest start test:")
  println

  val g = Graph(1 ~ 2 % 4, 2 ~ 3 % 2, 1 ~> 3 % 5, 1 ~ 5 % 3, 3 ~ 5 % 2, 3 ~ 4 % 1, 4 ~> 4 % 1, 4 ~> 5 % 0)

  def n(outer: Int): g.NodeT = g get outer // look up 'outer' that is known to be contained

  println { n(1) findSuccessor (_.outDegree > 3) }               // Option[g.NodeT] = None
  println { n(1) findSuccessor (_.outDegree >= 3) }              // Option[g.NodeT] = Some(3)
  println { n(4) findSuccessor (_.edges forall (_.undirected)) } // Some(2)
  println { n(4) isPredecessorOf n(1) }                          // true
  println { n(1) pathTo n(4) }                                   // Some(Path(1, 1 ~> 3 % 5, 3, 3 ~ 4 % 1, 4))
  println { n(1) pathUntil (_.outDegree >= 3) }                  // Some(Path(1, 1 ~> 3 % 5, 3))

  val spO = n(3) shortestPathTo n(1) // Path(3, 3 ~ 4 % 1, 4, 4 ~> 5 % 0, 5, 1 ~ 5 % 3, 1)
  val sp  = spO.get                  // here we know spO is defined
  println { sp.nodes }  // List[g.NodeT] = Nodes(3, 4, 5, 1)
  println { sp.weight } // Double = 4.0

  def negWeight(e: g.EdgeT) = 5.5f - e.weight

  val spNO = n(3) shortestPathTo (n(1), negWeight)
  println { spNO }            // Some(Path(3, 2 ~ 3 % 2, 2, 1 ~ 2 % 4, 1))
  println { spNO.get.weight } // Double = 6.0

  val pO1 = n(4).withSubgraph(nodes = _ < 4) pathTo n(2)
  println { pO1 }              // Some(Path(4, 3 ~ 4 % 1, 3, 2 ~ 3 % 2, 2))
  println { pO1.map(_.nodes) } // Some(Nodes(4, 3, 2))

  val pO2 = n(4).withSubgraph(edges = _.weight != 2) pathTo n(2)
  println { pO2 }              // Some(Path(4, 4 ~> 5 % 0, 5, 1 ~ 5 % 3, 1, 1 ~ 2 % 4, 2))
  println { pO2.map(_.nodes) } // Some(Nodes(4, 5, 1, 2))

  println
  println("GraphTest test finished!!!")
}
