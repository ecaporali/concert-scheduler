package com.ecaporali.scheduler

import com.ecaporali.scheduler.controllers.Scheduler
import com.ecaporali.scheduler.models.Plan
import com.ecaporali.scheduler.services.ConcertRuleService
import com.ecaporali.scheduler.utils.DateTimeUtils.{formatDateTime, formatTime}
import com.ecaporali.scheduler.utils.PerformanceFactory.createDefaultPerformances

/**
  * @author Enrico Caporali
  */
object Main {

  def main(args: Array[String]): Unit = {
    val performances = createDefaultPerformances()
    val scheduler = new Scheduler(ConcertRuleService(performances.map(_.event)))
    val scheduleOrAppError = scheduler.createSchedule(performances)

    val result = scheduleOrAppError match {
      case Right(schedule) => schedule.plans.map(formatResult).mkString
      case Left(error) => s"${error.context}: ${error.message}"
    }
    println(result)
  }

  private def formatResult(plan: Plan): String = {
    val performance = plan.performance
    val event = performance.event
    val result =
      s"""
         |${formatTime(plan.start)} - ${formatTime(plan.finish)} ${event.name.padTo(15, " ").mkString}
         |${formatDateTime(event.start)} ${formatDateTime(event.finish)} ${performance.priority}
      """.stripMargin.replaceAll("\n", " ")
    result + "\n"
  }
}
