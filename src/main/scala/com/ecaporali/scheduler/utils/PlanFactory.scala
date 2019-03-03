package com.ecaporali.scheduler.utils

import java.time.LocalDateTime

import com.ecaporali.scheduler.models.{Performance, Plan}
import com.ecaporali.scheduler.utils.DateTimeUtils.dateTime
import com.ecaporali.scheduler.utils.PerformanceFactory.createPerformance

/**
  * @author Enrico Caporali
  */
object PlanFactory {

  private val start = LocalDateTime.of(2018, 8, 8, 17, 0)

  def createDefaultPlans(): Seq[Plan] = {
    val createPlanAt = createPlan(start) _
    val createPerformanceAt = createPerformance(start) _

    Seq(
      createPlanAt(0, 60, createPerformanceAt("Metallica", 0, 60, 10)),
      createPlanAt(60, 120, createPerformanceAt("Rolling Stones", 60, 120, 9)),
      createPlanAt(120, 180, createPerformanceAt("ACDC", 120, 180, 8)),
      createPlanAt(180, 300, createPerformanceAt("Queen", 180, 300, 10))
    )
  }

  def createPlan(concertStartDateTime: LocalDateTime)(startTime: Int, finishTime: Int, performance: Performance): Plan = {
    Plan(dateTime(concertStartDateTime, startTime), dateTime(concertStartDateTime, finishTime), performance)
  }
}
