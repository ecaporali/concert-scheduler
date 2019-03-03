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

    // TODO: refactor the following code
    val availablePlans = findAvailableTimes(plans).flatMap {
      case f@FreeTime(_, _) if event.isStartWithinFreeTime(f) && event.isFinishWithinFreeTime(f) => Some(plan(event.start, event.finish))
      case f@FreeTime(_, finish) if !event.isOutsideFreeTimeRange(f) && event.isStartWithinFreeTime(f) && !event.isFinishWithinFreeTime(f) => Some(plan(event.start, finish))
      case f@FreeTime(start, _) if !event.isOutsideFreeTimeRange(f) && !event.isStartWithinFreeTime(f) && event.isFinishWithinFreeTime(f) => Some(plan(start, event.finish))
      case f@FreeTime(start, finish) if event.isStartBeforeAndFinishAfterFreeTime(f) => Some(plan(start, finish))
      case _ => None
    }

    plans ++ availablePlans
  }

  private def createPlan(performance: Performance)(start: LocalDateTime, finish: LocalDateTime) =
    Plan(start, finish, performance)

  private def findAvailableTimes(plans: Seq[Plan]): Seq[FreeTime] = {

    val maybeFreeTimeAtStart = findFreeTimeAtStart(concertStart, plans.head.start)
    val maybeFreeTimeAtEnd = findFreeTimeAtEnd(concertEnd, plans.reverse.head.finish)
    val inBetweenFreeTimes = plans.sliding(2).flatMap {
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
