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

import java.io.File
import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.TimeoutException

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 06/04/2022
  */
trait CrossProcessStateManager {
  type State

  def id: String

  /** 本对象管理的几个状态，不包括 [[release]] 时的状态。`_2`表示如果要 [[holdNext]]，是否需要先 [[release]]（显示调用）。 */
  def states: List[(State, Boolean)]

  /** 初始启动时，如果状态损坏（如置为某一状态而未释放），需要重置。 */
  def startup(): Unit

  /** 设置当前状态，每个进程都必须一致。 */
  def hold(state: State): Boolean

  /** 取得 [[hold]] 方法设置的当前状态，[[None]] 表示已 [[release]] 了。 */
  def get: Option[State]

  /** 取得 [[hold]] 方法设置的当前状态，如果 [[release]] 了就返回上一个，如果还未执行过 [[hold]] 会返回 null。 */
  def getOrPrev: State

  /** 释放当前状态到初始状态（如果当前进程 [[hold]] 了某状态）。 */
  def release(): Unit

  /** 是否已经释放（当前进程的状态）。
    * @param strict true 表示无任何进程 hold 状态才返回 true，false 表示仅关注当前进程是否 hold 状态，不 care 其它进程是否 hold 状态。
    */
  def isReleased(strict: Boolean): Boolean

  /** 只有不需要先 [[release]] 的状态才能成功。 */
  def holdNext: Boolean

  final def next: (State, Boolean) = {
    val i = states.map(_._1).indexOf(getOrPrev) + 1
    states(if (i < 0 || i >= states.size) 0 else i)
  }
  final def indexOf(state: String) = states.map(_._1.toString).indexOf(state)

  /** 一个进程 [[hold]] 了某状态后，才能执行后续操作，而这很可能需要一些时间，然后才能 [[release]]。
    * 在此期间，其它进程需要等待（直到任何进程的 [[release]]）才能成功锁定某状态。
    * @param action 可用于 [[hold]] 或 [[holdNext]]。
    */
  @throws[TimeoutException]
  final def waitingFor(timeout: Long, currentTimeMillis: => Long = System.currentTimeMillis)(action: => Boolean): Unit = {
    val begin = currentTimeMillis
    while (!action) {
      if (currentTimeMillis - begin >= timeout) throw new TimeoutException
      Thread.`yield`()
    }
  }
}

class CrossProcessStateMgrByAtomicFile[StateTpe](process: String, val name: String, val dir: File, stateSeq: (StateTpe, Boolean)*) extends CrossProcessStateMgrAtomicFileImpl {
  def this(process: String, name: String, states: (StateTpe, Boolean)*) = this(process, name, null, states: _*)
  override type State = StateTpe
  override def states      = stateSeq.toList
  override def myProcessId = process
}

trait CrossProcessStateMgrAtomicFileImpl extends CrossProcessStateManager {
  private lazy val atomic       = new CrossProcessAtomicByFile(name, dir)
  private lazy val lastStateRef = new AtomicReference[State]

  override final lazy val id = new File(dir, name).getPath

  def name: String
  def dir: File
  def myProcessId: String

  private val stateNo                                       = ""
  private def withProcessId(state: String)                  = s"$myProcessId:$state"
  private def isBelongToReleased(stateInFile: String)       = stateInFile == stateNo
  private def isBelongToMyProcess(stateInFile: String)      = stateInFile.startsWith(withProcessId(stateNo))
  private def isReleasedOrNonMyProcess(stateInFile: String) = isBelongToReleased(stateInFile) || !isBelongToMyProcess(stateInFile)

  private def parseState(stateInFile: String): Option[State] = {
    if (isReleasedOrNonMyProcess(stateInFile)) None // 包含不是本进程的状态
    else {
      assert(isBelongToMyProcess(stateInFile))
      val prefix = withProcessId(stateNo)
      val pure   = stateInFile.substring(prefix.length)
      val i      = indexOf(pure).ensuring(_ >= 0, s"state belongs to my process:$stateInFile($pure), but not in states:${states.mkString(",")}")
      Some(states(i)._1)
    }
  }

  private def holdInner(expect: String, update: State): Boolean =
    if (atomic.compareAndSet(expect, withProcessId(update.toString))) { lastStateRef.set(update); true }
    else false

  override def startup(): Unit             = atomic.set(stateNo)
  override def hold(state: State): Boolean = holdInner(stateNo, state.ensuring(s => states.exists(_._1 == s)))

  override def holdNext: Boolean = {
    val (state, needReleaseFirst) = next
    val stateInFile               = atomic.get
    val released                  = isBelongToReleased(stateInFile)
    if (needReleaseFirst && !released) false
    else {
      if (released) hold(state)
      else if (isBelongToMyProcess(stateInFile)) holdInner(stateInFile, state)
      else false
    }
  }

  override def release(): Unit = {
    val stateInFile = atomic.get
    if (isBelongToMyProcess(stateInFile)) // 本来就已释放的，就不用了。
      atomic.compareAndSet(stateInFile, stateNo).ensuring(_ == true)
  }
  override def get: Option[State]          = parseState(atomic.get)
  override def getOrPrev: State            = lastStateRef.get
  override def isReleased(strict: Boolean) = if (strict) isBelongToReleased(atomic.get) else isReleasedOrNonMyProcess(atomic.get)
}
