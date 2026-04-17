package models

trait HodErrors extends Throwable
object HodErrors {
  case object UnexpectedResponse extends HodErrors
  case object InvalidJson extends HodErrors
}