package com.ecaporali.scheduler.models

import java.time.LocalDateTime

/**
  * @author Enrico Caporali
  */
case class FreeTime(start: LocalDateTime, finish: LocalDateTime)

object FreeTime {

  def findFreeTimeAtStart(concertStart: LocalDateTime, lowestStartTimePlan: LocalDateTime): Option[FreeTime] = {
    if (concertStart.isEqual(lowestStartTimePlan)) None
    else Some(FreeTime(concertStart, lowestStartTimePlan))
  }

  def findFreeTimeAtEnd(concertEnd: LocalDateTime, highestFinishTimePlan: LocalDateTime): Option[FreeTime] = {
    if (concertEnd.isEqual(highestFinishTimePlan)) None
    else Some(FreeTime(highestFinishTimePlan, concertEnd))
  }

  def findFreeTimeInTheMiddle(firstPlanFinish: LocalDateTime, secondPlanStart: LocalDateTime): Option[FreeTime] = {
    if (firstPlanFinish.isEqual(secondPlanStart)) None
    else Some(FreeTime(firstPlanFinish, secondPlanStart))
  }
}
