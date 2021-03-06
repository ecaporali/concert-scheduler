package com.ecaporali.scheduler.models

import java.time.LocalDateTime

/**
  * @author Enrico Caporali
  */
case class Event(name: String, start: LocalDateTime, finish: LocalDateTime) {

  def isWithinFreeTime(freeTime: FreeTime): Boolean =
    isStartWithinFreeTime(freeTime) && isFinishWithinFreeTime(freeTime)

  def isUntilEndFreeTime(freeTime: FreeTime): Boolean =
    !isOutsideFreeTimeRange(freeTime) && isStartWithinFreeTime(freeTime) && !isFinishWithinFreeTime(freeTime)

  def isAfterStartFreeTime(freeTime: FreeTime): Boolean =
    !isOutsideFreeTimeRange(freeTime) && !isStartWithinFreeTime(freeTime) && isFinishWithinFreeTime(freeTime)

  def isOnlyWithinFreeTime(time: FreeTime): Boolean = {
    !start.isAfter(time.start) && !finish.isBefore(time.finish)
  }

  def isStartWithinFreeTime(time: FreeTime): Boolean = {
    !start.isBefore(time.start) && !start.isAfter(time.finish)
  }

  def isFinishWithinFreeTime(time: FreeTime): Boolean = {
    !finish.isAfter(time.finish) && !finish.isBefore(time.start)
  }

  def isOutsideFreeTimeRange(time: FreeTime): Boolean = {
    time.start.isEqual(finish) || time.finish.isEqual(start)
  }
}
