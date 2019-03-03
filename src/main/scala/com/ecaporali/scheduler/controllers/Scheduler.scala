package com.ecaporali.scheduler.controllers

import com.ecaporali.scheduler.models.{Performance, Plan, Schedule}
import com.ecaporali.scheduler.services.RuleService
import com.ecaporali.scheduler.utils.DateTimeUtils.toTimestamp
import com.ecaporali.scheduler.{AppError, AppErrorOr}

/**
  * @author Enrico Caporali
  */
class Scheduler(rules: RuleService) {

  def createSchedule(performances: Array[Performance]): AppErrorOr[Schedule] = {
    if (performances.nonEmpty) Right(optimizeSchedule(performances.toVector))
    else Left(AppError("Scheduler", "Performances cannot be empty"))
  }

  private def optimizeSchedule(performances: Vector[Performance]): Schedule = {
    val firstPerformance +: otherPerformances = performances.sortBy(p => (-p.priority, toTimestamp(p.event.start)))
    val firstPlan = Plan(firstPerformance.event.start, firstPerformance.event.finish, firstPerformance)

    val plans = otherPerformances
      .foldLeft(Seq(firstPlan))((remainder, performance) => rules.optimizeSchedule(remainder, performance))
      .sortBy(plan => (toTimestamp(plan.start), toTimestamp(plan.finish)))

    Schedule(plans)
  }
}
