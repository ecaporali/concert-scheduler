package com.ecaporali

/**
  * @author Enrico Caporali
  */
package object scheduler {

  type AppErrorOr[+A] = Either[AppError, A]

  case class AppError(context: String, message: String)

}
