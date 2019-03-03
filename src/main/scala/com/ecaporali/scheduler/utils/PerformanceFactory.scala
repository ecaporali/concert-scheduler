package com.ecaporali.scheduler.utils

import java.time.LocalDateTime

import com.ecaporali.scheduler.models.{Event, Performance}
import com.ecaporali.scheduler.utils.DateTimeUtils.dateTime

/**
  * @author Enrico Caporali
  */
object PerformanceFactory {

  def createDefaultPerformances(): Array[Performance] = {
    val start = LocalDateTime.of(2018, 8, 8, 17, 0)
    val createPerformanceAt = createPerformance(start) _

    Array(
      createPerformanceAt("Metallica", 0, 30, 10),
      createPerformanceAt("Rolling Stones", 30, 120, 10),
      createPerformanceAt("ACDC", 60, 180, 9),
      createPerformanceAt("Guns n'Roses", 120, 300, 10),
      createPerformanceAt("Queen", 220, 320, 10),
      createPerformanceAt("Green Day", 200, 240, 7),
      createPerformanceAt("U2", 220, 400, 8)
    )
  }

  def createPerformance(concertStartDateTime: LocalDateTime)(name: String, startTime: Int, finishTime: Int, priority: Int): Performance = {
    Performance(createEvent(concertStartDateTime)(name, startTime, finishTime), priority)
  }

  def createEvent(concertStartDateTime: LocalDateTime)(name: String, startTime: Int, finishTime: Int): Event = {
    Event(name, dateTime(concertStartDateTime, startTime), dateTime(concertStartDateTime, finishTime))
  }
}
