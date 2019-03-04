package com.ecaporali.scheduler.services

import java.time.LocalDateTime

import com.ecaporali.scheduler.models.FreeTime.{findFreeTimeAtEnd, findFreeTimeAtStart, findFreeTimeInTheMiddle}
import com.ecaporali.scheduler.models.{Event, FreeTime, Performance, Plan}
import com.ecaporali.scheduler.utils.DateTimeUtils.toTimestamp

/**
  * @author Enrico Caporali
  */
trait RuleService {
  def optimizeSchedule(plans: Seq[Plan], performance: Performance): Seq[Plan]
}

sealed class ConcertRuleService(concertStart: LocalDateTime, concertEnd: LocalDateTime) extends RuleService {
  require(concertStart.isBefore(concertEnd), "Concert start time must be before end time")

  override def optimizeSchedule(plans: Seq[Plan], performance: Performance): Seq[Plan] = {
    val event = performance.event
    val plan = createPlan(performance) _

    val availablePlans = findAvailableTimes(plans).flatMap { time =>
      if (event.isWithinFreeTime(time)) Some(plan(event.start, event.finish))
      else if (event.isUntilEndFreeTime(time)) Some(plan(event.start, time.finish))
      else if (event.isAfterStartFreeTime(time)) Some(plan(time.start, event.finish))
      else if (event.isOnlyWithinFreeTime(time)) Some(plan(time.start, time.finish))
      else None
    }

    plans ++ availablePlans
  }

  private def createPlan(performance: Performance)(start: LocalDateTime, finish: LocalDateTime) =
    Plan(start, finish, performance)

  private def findAvailableTimes(plans: Seq[Plan]): Seq[FreeTime] = {
    val groupSize = 2
    val maybeFreeTimeAtStart = findFreeTimeAtStart(concertStart, plans.head.start)
    val maybeFreeTimeAtEnd = findFreeTimeAtEnd(concertEnd, plans.reverse.head.finish)
    val inBetweenFreeTimes = plans.sliding(groupSize).flatMap {
      case first +: second +: _ => findFreeTimeInTheMiddle(first.finish, second.start)
      case _ +: Nil => None
    }.toVector

    (maybeFreeTimeAtStart, maybeFreeTimeAtEnd) match {
      case (Some(freeTimeAtStart), Some(freeTimeAtEnd)) => freeTimeAtStart +: inBetweenFreeTimes :+ freeTimeAtEnd
      case (None, Some(freeTimeAtEnd)) => inBetweenFreeTimes :+ freeTimeAtEnd
      case (Some(freeTimeAtStart), None) => freeTimeAtStart +: inBetweenFreeTimes
      case (None, None) => inBetweenFreeTimes
    }
  }
}

object ConcertRuleService {
  def apply(events: Array[Event]): ConcertRuleService = {
    val concertStart = events.map(_.start).minBy(toTimestamp)
    val concertEnd = events.map(_.finish).maxBy(toTimestamp)
    new ConcertRuleService(concertStart, concertEnd)
  }
}
