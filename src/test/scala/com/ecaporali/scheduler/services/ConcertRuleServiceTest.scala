package com.ecaporali.scheduler.services

import java.time.LocalDateTime

import com.ecaporali.scheduler.models.{Performance, Plan}
import com.ecaporali.scheduler.utils.DateTimeUtils.dateTime
import com.ecaporali.scheduler.utils.PerformanceFactory.createPerformance
import com.ecaporali.scheduler.utils.PlanFactory.createPlan
import org.specs2.mutable.Specification

class ConcertRuleServiceTest extends Specification {

  private val start = LocalDateTime.of(2018, 8, 8, 17, 0)

  "RuleServiceTest" can {

    val concertStart = dateTime(start, 0)
    val concertEnd = dateTime(start, 300)

    val ruleService = new ConcertRuleService(concertStart, concertEnd)

    "optimizeSchedule" should {

      "visit next performance when previous plan just finished" in {
        val plans = Seq(createStandardPlan("Metallica", 0, 60, 10))
        val performance = createStandardPerformance("ACDC", 60, 180, 8)

        val actualPlans = ruleService.optimizeSchedule(plans, performance)
        val expectedPlans = plans :+ createExpectedPlan(60, 180, performance)

        actualPlans must beEqualTo(expectedPlans)
      }

      "visit next performance when previous plan has already long finished" in {
        val plans = Seq(createStandardPlan("Metallica", 0, 60, 10))

        val performance = createStandardPerformance("ACDC", 120, 180, 8)

        val actualPlans = ruleService.optimizeSchedule(plans, performance)
        val expectedPlans = plans :+ createExpectedPlan(120, 180, performance)

        actualPlans must beEqualTo(expectedPlans)
      }

      "still visit performance when another plan with higher priority starts straight after this plan" in {
        val plans = Seq(createStandardPlan("Metallica", 60, 120, 10))

        val performance = createStandardPerformance("ACDC", 0, 60, 8)

        val actualPlans = ruleService.optimizeSchedule(plans, performance)
        val expectedPlans = plans :+ createExpectedPlan(0, 60, performance)

        actualPlans must beEqualTo(expectedPlans)
      }

      "still visit performance when another plan with higher priority starts well after this plan finish time" in {
        val plans = Seq(createStandardPlan("Metallica", 90, 120, 10))

        val performance = createStandardPerformance("ACDC", 0, 60, 8)

        val actualPlans = ruleService.optimizeSchedule(plans, performance)
        val expectedPlans = plans :+ createExpectedPlan(0, 60, performance)

        actualPlans must beEqualTo(expectedPlans)
      }

      "defer performance when previous plan is still running" in {
        val plans = Seq(createStandardPlan("Metallica", 60, 120, 10))

        val performance = createStandardPerformance("ACDC", 90, 180, 8)

        val actualPlans = ruleService.optimizeSchedule(plans, performance)
        val expectedPlans = plans :+ createExpectedPlan(120, 180, performance)

        actualPlans must beEqualTo(expectedPlans)
      }

      "leave performance before finishTime when previous plan with higher priority starts" in {
        val plans = Seq(createStandardPlan("Metallica", 60, 120, 10))

        val performance = createStandardPerformance("ACDC", 90, 180, 8)

        val actualPlans = ruleService.optimizeSchedule(plans, performance)
        val expectedPlans = plans :+ createExpectedPlan(120, 180, performance)

        actualPlans must beEqualTo(expectedPlans)
      }

      "leave and go back to current performance when another plan with higher priority starts and finishes after current plan" in {
        val plans = Seq(createStandardPlan("Metallica", 60, 120, 10))

        val performance = createStandardPerformance("ACDC", 0, 180, 8)

        val actualPlans = ruleService.optimizeSchedule(plans, performance)
        val expectedPlans = plans ++ Seq(
          createExpectedPlan(0, 60, performance),
          createExpectedPlan(120, 180, performance)
        )

        actualPlans must beEqualTo(expectedPlans)
      }

      "skip performance when another higher priority plan starts before and finishes after" in {
        val plans = Seq(createStandardPlan("Metallica", 0, 180, 10))

        val performance = createStandardPerformance("ACDC", 60, 120, 8)
        val actualPlans = ruleService.optimizeSchedule(plans, performance)

        actualPlans must beEqualTo(plans)
      }

      "skip performance when another higher priority plan starts at same time and finishes after" in {
        val plans = Seq(createStandardPlan("Metallica", 0, 180, 10))

        val performance = createStandardPerformance("ACDC", 0, 60, 8)
        val actualPlans = ruleService.optimizeSchedule(plans, performance)

        actualPlans must beEqualTo(plans)
      }

      "skip performance when another higher priority plan starts before and finishes at same time" in {
        val plans = Seq(createStandardPlan("Metallica", 0, 180, 10))

        val performance = createStandardPerformance("ACDC", 120, 180, 8)
        val actualPlans = ruleService.optimizeSchedule(plans, performance)

        actualPlans must beEqualTo(plans)
      }

      "visit performance when another higher priority plan just finished and the next one starts when this performance finishes" in {
        val plans = Seq(
          createStandardPlan("Metallica", 0, 120, 10),
          createStandardPlan("Queen", 180, 300, 10)
        )

        val performance = createStandardPerformance("ACDC", 120, 180, 8)

        val actualPlans = ruleService.optimizeSchedule(plans, performance)
        val expectedPlans = plans :+ createExpectedPlan(120, 180, performance)

        actualPlans must beEqualTo(expectedPlans)
      }

      "visit performance later and leave earlier when another higher priority plan finished and the next one starts before this performance finishes" in {
        val plans = Seq(
          createStandardPlan("Metallica", 0, 120, 10),
          createStandardPlan("Queen", 180, 300, 10)
        )

        val performance = createStandardPerformance("ACDC", 100, 200, 8)
        val actualPlans = ruleService.optimizeSchedule(plans, performance)

        val expectedPlans = plans :+ createExpectedPlan(120, 180, performance)

        actualPlans must beEqualTo(expectedPlans)
      }

      "visit performance later and leave earlier plus go back to this performance in the end" in {
        val plans = Seq(
          createStandardPlan("Metallica", 0, 120, 10),
          createStandardPlan("Queen", 180, 260, 10)
        )

        val performance = createStandardPerformance("ACDC", 100, 300, 8)
        val actualPlans = ruleService.optimizeSchedule(plans, performance)

        val expectedPlans = plans ++ Seq(
          createExpectedPlan(120, 180, performance),
          createExpectedPlan(260, 300, performance)
        )

        actualPlans must beEqualTo(expectedPlans)
      }

      "visit performance first and then leave earlier and never come back" in {
        val plans = Seq(
          createStandardPlan("Metallica", 60, 120, 10),
          createStandardPlan("Queen", 120, 300, 10)
        )

        val performance = createStandardPerformance("ACDC", 0, 300, 8)
        val actualPlans = ruleService.optimizeSchedule(plans, performance)

        val expectedPlans = plans :+ createExpectedPlan(0, 60, performance)

        actualPlans must beEqualTo(expectedPlans)
      }

      "visit performance first then leave, visit again then leave, visit again" in {
        val plans = Seq(
          createStandardPlan("Metallica", 60, 120, 10),
          createStandardPlan("Queen", 180, 260, 10)
        )

        val performance = createStandardPerformance("ACDC", 0, 300, 8)
        val actualPlans = ruleService.optimizeSchedule(plans, performance)

        val expectedPlans = plans ++ Seq(
          createExpectedPlan(0, 60, performance),
          createExpectedPlan(120, 180, performance),
          createExpectedPlan(260, 300, performance)
        )

        actualPlans must beEqualTo(expectedPlans)
      }

      "visit performance first then leave, visit again then leave, visit again" in {
        val plans = Seq(
          createStandardPlan("Metallica", 30, 90, 10),
          createStandardPlan("Queen", 120, 180, 10),
          createStandardPlan("Guns'n Roses", 210, 270, 10)
        )

        val performance = createStandardPerformance("ACDC", 0, 300, 8)
        val actualPlans = ruleService.optimizeSchedule(plans, performance)

        val expectedPlans = plans ++ Seq(
          createExpectedPlan(0, 30, performance),
          createExpectedPlan(90, 120, performance),
          createExpectedPlan(180, 210, performance),
          createExpectedPlan(270, 300, performance)
        )

        actualPlans must beEqualTo(expectedPlans)
      }

      "throw exception when empty plans are given" in {
        new ConcertRuleService(concertStart, concertStart) must throwAn[IllegalArgumentException](
          message = "Concert start time must be before end time"
        )
      }
    }
  }

  private def createStandardPlan(band: String, startTime: Int, finishTime: Int, priority: Int): Plan = {
    createPlan(start)(startTime, finishTime, createStandardPerformance(band, startTime, finishTime, priority))
  }

  private def createStandardPerformance(band: String, startTime: Int, finishTime: Int, priority: Int): Performance = {
    createPerformance(start)(band, startTime, finishTime, priority)
  }

  private def createExpectedPlan(startTime: Int, finishTime: Int, performance: Performance): Plan = {
    createPlan(start)(startTime, finishTime, performance)
  }
}
