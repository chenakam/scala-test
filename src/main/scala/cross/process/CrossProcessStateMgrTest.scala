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
package cross.process

import java.util.UUID

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 08/04/2022
  */
object CrossProcessStateMgrTest extends App {
  val process = Runtime.getRuntime.toString + "/" + UUID.randomUUID()
  println(s"process:$process")

  lazy val stateMgr = new CrossProcessStateMgrByAtomicFile(
    process,
    "cross_process_state_mgr_test",
    ("state_0", false),
    ("state_1", false),
    ("state_2", false)
  )

  stateMgr.startup()

  for (i <- 0 until 1000) {
    println(s"holdNext (times $i):${stateMgr.holdNext} curr:${stateMgr.get}")
    Thread.sleep((1000 * math.random).toLong)
    stateMgr.release()
    Thread.sleep((1000 * math.random).toLong)
    stateMgr.waitingFor(3000) {
      val b = stateMgr.holdNext
      println(s"waitingFor (times $i):$b released:${stateMgr.isReleased(true)}")
      b
    }
    println()
  }
}

object CrossProcessStateMgrTest1 extends App {
  val process = Runtime.getRuntime.toString + "/" + UUID.randomUUID()
  println(s"process:$process")

  lazy val stateMgr = new CrossProcessStateMgrByAtomicFile(
    process,
    "cross_process_state_mgr_test",
    ("state_0", false),
    ("state_1", false),
    ("state_2", false)
  )

  stateMgr.startup()

  for (i <- 0 until 1000) {
    println(s"holdNext (times $i):${stateMgr.holdNext} curr:${stateMgr.get}")
    Thread.sleep((1000 * math.random).toLong)
    stateMgr.release()
    Thread.sleep((1000 * math.random).toLong)
    stateMgr.waitingFor(3000) {
      val b = stateMgr.holdNext
      println(s"waitingFor (times $i):$b released:${stateMgr.isReleased(true)}")
      b
    }
    println()
  }
}
