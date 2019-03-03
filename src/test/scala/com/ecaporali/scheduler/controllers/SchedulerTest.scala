package com.ecaporali.scheduler.controllers

import java.time.LocalDateTime

import com.ecaporali.scheduler.AppError
import com.ecaporali.scheduler.models.{Performance, Plan, Schedule}
import com.ecaporali.scheduler.services.RuleService
import com.ecaporali.scheduler.utils.DateTimeUtils.toTimestamp
import com.ecaporali.scheduler.utils.PerformanceFactory.createPerformance
import com.ecaporali.scheduler.utils.PlanFactory.createPlan
import org.specs2.mutable.Specification

class SchedulerTest extends Specification {

  private val start = LocalDateTime.of(2018, 8, 8, 17, 0)

  "SchedulerTest" can {

    "createSchedule" should {
      val performances = Array(
        createPerformance(start)("Rolling Stones", 60, 120, 9),
        createPerformance(start)("ACDC", 120, 180, 8),
        createPerformance(start)("Metallica", 0, 60, 10)
      )

      val mockService = new RuleServiceMock(start, performances.map(_.event.finish).maxBy(toTimestamp))
      val scheduler = new Scheduler(mockService)

      "return a collection of plans sorted by priority and start time" in {
        val schedule = scheduler.createSchedule(performances)

        val expectedSchedule = Schedule(Seq(
          createPlan(start)(0, 60, performances(2)),
          createPlan(start)(60, 120, performances(0)),
          createPlan(start)(120, 180, performances(1))
        ))

        schedule must beRight(expectedSchedule)
      }

      "return AppError when there are no performances" in {
        val schedule = scheduler.createSchedule(Array())
        schedule must beLeft(AppError("Scheduler", "Performances cannot be empty"))
      }
    }
  }

  private class RuleServiceMock(concertStart: LocalDateTime, concertEnd: LocalDateTime) extends RuleService {
    override def optimizeSchedule(plans: Seq[Plan], currentPerformance: Performance): Seq[Plan] =
      plans :+ Plan(currentPerformance.event.start, currentPerformance.event.finish, currentPerformance)
  }
}
