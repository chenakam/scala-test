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

import java.io.{File, IOException, RandomAccessFile}

/**
  * @author Chenai Nakam(chenai.nakam@gmail.com)
  * @version 1.0, 07/04/2022
  */
trait CrossProcessAtomic {
  def get: String
  def set(value: String): Unit
  def getAndSet(newValue: String): String
  def compareAndSet(expect: String, update: String): Boolean
  def tryCompareAndSet(expect: String, update: String): Boolean
}

class CrossProcessAtomicByFile(val name: String, val dir: File = null) extends CrossProcessAtomicFileLockImpl

/** 由于写一个值就释放，不会占用太多时间（最多秒级）。释放后，另一个 block 住的进程就能立即获得锁，性能不会有太大影响，所以通常不需用 [[CrossProcessAtomic.tryCompareAndSet]]。 */
trait CrossProcessAtomicFileLockImpl extends CrossProcessAtomic {
  private lazy val random  = new RandomAccessFile(new File(dir, name), "rw")
  private lazy val channel = random.getChannel

  def name: String
  def dir: File

  @throws[IOException]
  private def reset() = {
    // `random.seek()` 文档中有这么一段：
    // The file length will change only by writing after the offset has been set beyond the end of the file.
    // "文件长度改变的唯一条件是：在把 offset（即参数 pos）设置为超出文件长度后的写操作"。之前还有一句：
    // Setting the offset beyond the end of the file does not change the file length.
    // 这表明是可以将文件长度设置为`0`的（只要之后写数据，会自动增长）。
    random.setLength(0)
    random.seek(0)
    random
  }

  /** 根据实现，即使是空字符串，也会写入两个字节（的长度）。 */
  @throws[IOException]
  override def set(value: String): Unit = withBlockingLock { reset().writeUTF(value) }

  @throws[IOException]
  override def get: String = withBlockingLock { random.seek(0); random.readUTF() }

  @throws[IOException]
  override def getAndSet(newValue: String): String = withBlockingLock {
    random.seek(0)
    val prev = random.readUTF()
    reset().writeUTF(newValue)
    prev
  }

  @throws[IOException]
  override def compareAndSet(expect: String, update: String): Boolean = withBlockingLock {
    random.seek(0)
    val prev = random.readUTF()
    val b    = prev == expect
    if (b) reset().writeUTF(update)
    b
  }

  @throws[IOException]
  override def tryCompareAndSet(expect: String, update: String): Boolean = withNonBlockLock {
    random.seek(0)
    val prev = random.readUTF()
    val b    = prev == expect
    if (b) reset().writeUTF(update)
    b
  }.getOrElse(false)

  def withBlockingLock[T](action: => T): T = {
    val lock = channel.lock()
    try { action }
    finally { lock.release() }
  }

  def withNonBlockLock[T](action: => T): Option[T] = {
    val lock = Option(channel.tryLock())
    try { lock.map { _ => action } }
    finally { lock.foreach { _.release() } }
  }

  override def finalize(): Unit = {
    try channel.close()
    catch { case _: Throwable => }
    try random.close()
    catch { case _: Throwable => }
    super.finalize()
  }
}
