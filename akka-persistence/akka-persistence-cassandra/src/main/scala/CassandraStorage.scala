/**
 * Copyright (C) 2009-2010 Scalable Solutions AB <http://scalablesolutions.se>
 */

package se.scalablesolutions.akka.state

import org.codehaus.aspectwerkz.proxy.Uuid

object CassandraStorage extends Storage {
  type ElementType = Array[Byte]

  def newMap: PersistentMap[ElementType, ElementType] = newMap(Uuid.newUuid.toString)
  def newVector: PersistentVector[ElementType] = newVector(Uuid.newUuid.toString)
  def newRef: PersistentRef[ElementType] = newRef(Uuid.newUuid.toString)

  def getMap(id: String): PersistentMap[ElementType, ElementType] = newMap(id)
  def getVector(id: String): PersistentVector[ElementType] = newVector(id)
  def getRef(id: String): PersistentRef[ElementType] = newRef(id)

  def newMap(id: String): PersistentMap[ElementType, ElementType] = new CassandraPersistentMap(id)
  def newVector(id: String): PersistentVector[ElementType] = new CassandraPersistentVector(id)
  def newRef(id: String): PersistentRef[ElementType] = new CassandraPersistentRef(id)
}

/**
 * Implements a persistent transactional map based on the Cassandra distributed P2P key-value storage.
 *
 * @author <a href="http://jonasboner.com">Jonas Bon&#233;r</a>
 */
class CassandraPersistentMap(id: String) extends PersistentMap[Array[Byte], Array[Byte]] {
  val uuid = id
  val storage = CassandraStorageBackend
}

/**
 * Implements a persistent transactional vector based on the Cassandra
 * distributed P2P key-value storage.
 *
 * @author <a href="http://jonasboner.com">Jonas Bon&#233;r</a>
 */
class CassandraPersistentVector(id: String) extends PersistentVector[Array[Byte]] {
  val uuid = id
  val storage = CassandraStorageBackend
}

class CassandraPersistentRef(id: String) extends PersistentRef[Array[Byte]] {
  val uuid = id
  val storage = CassandraStorageBackend
}