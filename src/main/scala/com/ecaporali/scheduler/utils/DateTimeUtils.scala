package com.ecaporali.scheduler.utils

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneOffset}

/**
  * @author Enrico Caporali
  */
object DateTimeUtils {

  def dateTime(dateTime: LocalDateTime, minutes: Int): LocalDateTime =
    dateTime.plusMinutes(minutes)

  def toTimestamp(dateTime: LocalDateTime): Long =
    dateTime.toEpochSecond(ZoneOffset.UTC)

  def formatTime(dateTime: LocalDateTime): String =
    dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

  def formatDateTime(dateTime: LocalDateTime): String =
    dateTime.format(DateTimeFormatter.ofPattern("dd-MM-Y HH:mm"))
}
