package com.ecaporali.scheduler.models

import java.time.LocalDateTime

import com.ecaporali.scheduler.models.FreeTime._
import com.ecaporali.scheduler.utils.PerformanceFactory.createPerformance
import org.specs2.mutable.Specification

class FreeTimeTest extends Specification {

  private val start = LocalDateTime.of(2018, 8, 8, 17, 0)

  "DateTimeUtilsTest" can {
    val performance = createPerformance(start)("Guns n'Roses", 0, 60, 8)
    val performanceStart = performance.event.start
    val performanceFinish = performance.event.finish

    "findAtMiddle" should {

      "return Some when lastPlan finish is not equal to currentPlan start" in {
        val firstFinishTime = performanceFinish
        val secondStartTime = performanceFinish.plusHours(1)
        val finishBetween = findFreeTimeInTheMiddle(firstFinishTime, secondStartTime)
        finishBetween must beSome(FreeTime(firstFinishTime, secondStartTime))
      }

      "return None when lastPlan finish is equal to currentPlan start" in {
        val finishBetween = findFreeTimeInTheMiddle(performanceStart, performanceStart)
        finishBetween must beNone
      }
    }

    "findAtEnd" should {

      "return Some when end of concert is not equal to highest finish plan" in {
        val concertEnd = performanceFinish.plusHours(60)
        val finishBetween = findFreeTimeAtEnd(concertEnd, performanceFinish)
        finishBetween must beSome(FreeTime(performanceFinish, concertEnd))
      }

      "return None when end of concert is equal to highest finish plan" in {
        val concertEnd = performanceFinish.plusHours(60)
        val finishBetween = findFreeTimeAtEnd(concertEnd, concertEnd)
        finishBetween must beNone
      }
    }

    "findAtStart" should {

      "return Some when start of concert is not equal to lowest start plan" in {
        val concertStart = performanceStart.minusHours(60)
        val finishBetween = findFreeTimeAtStart(concertStart, performanceStart)
        finishBetween must beSome(FreeTime(concertStart, performanceStart))
      }

      "return None when start of concert is equal to lowest start plan" in {
        val concertStart = performanceStart.minusHours(60)
        val finishBetween = findFreeTimeAtStart(concertStart, concertStart)
        finishBetween must beNone
      }
    }
  }
}
