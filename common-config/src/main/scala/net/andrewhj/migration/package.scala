package net.andrewhj

import zio.stream.ZStream

package object migration {
  type ZSTask[A] = ZStream[Any, Throwable, A]
}
