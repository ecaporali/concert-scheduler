package com.ecaporali.scheduler.models

import java.time.LocalDateTime

import com.ecaporali.scheduler.utils.PerformanceFactory.createEvent
import org.specs2.mutable.Specification

class EventTest extends Specification {

  private val start = LocalDateTime.of(2018, 8, 8, 17, 0)

  "EventTest" can {
    val event = createEvent(start)("Guns n'Roses", 0, 60)
    val eventStart = event.start
    val eventFinish = event.finish

    "isFinishWithinFreeTime" should {

      "return True when event finish time is before or equal free time finish time " in {
        val freeTime = FreeTime(eventStart, eventFinish.plusMinutes(5))
        val finishBetween = event.isFinishWithinFreeTime(freeTime)
        finishBetween must beTrue
      }

      "return False when event finish time is after free time finish time " in {
        val freeTime = FreeTime(eventStart, eventFinish.minusMinutes(5))
        val finishBetween = event.isFinishWithinFreeTime(freeTime)
        finishBetween must beFalse
      }

      "return False when event finish time is before free time start time " in {
        val freeTime = FreeTime(eventStart.plusMinutes(65), eventFinish.plusMinutes(5))
        val finishBetween = event.isFinishWithinFreeTime(freeTime)
        finishBetween must beFalse
      }
    }

    "isStartWithinFreeTime" should {

      "return True when event start time is equal or after free time start time " in {
        val freeTime = FreeTime(eventStart.minusMinutes(5), eventFinish)
        val finishBetween = event.isStartWithinFreeTime(freeTime)
        finishBetween must beTrue
      }

      "return False when event start time is before free time start time " in {
        val freeTime = FreeTime(eventStart.plusMinutes(5), eventFinish)
        val finishBetween = event.isStartWithinFreeTime(freeTime)
        finishBetween must beFalse
      }

      "return False when event start time is after free time finish time " in {
        val freeTime = FreeTime(eventStart.plusMinutes(65), eventFinish.plusMinutes(5))
        val finishBetween = event.isStartWithinFreeTime(freeTime)
        finishBetween must beFalse
      }
    }

    "isStartBeforeAndFinishAfterFreeTime" should {

      "return True when event start/end is longer than free time start/end" in {
        val freeTime = FreeTime(eventStart.plusMinutes(5), eventFinish.minusMinutes(5))
        val finishBetween = event.isOnlyWithinFreeTime(freeTime)
        finishBetween must beTrue
      }

      "return False when event end time is less than free time end time" in {
        val freeTime = FreeTime(eventStart, eventFinish.plusMinutes(5))
        val finishBetween = event.isOnlyWithinFreeTime(freeTime)
        finishBetween must beFalse
      }

      "return False when event start time is greater than free time start time" in {
        val freeTime = FreeTime(eventStart.minusMinutes(5), eventFinish)
        val finishBetween = event.isOnlyWithinFreeTime(freeTime)
        finishBetween must beFalse
      }
    }

    "isOutsideFreeTimeRange" should {

      "return True when event finish time is equal free time start time" in {
        val freeTime = FreeTime(eventFinish, eventFinish.plusMinutes(30))
        val finishBetween = event.isOutsideFreeTimeRange(freeTime)
        finishBetween must beTrue
      }

      "return True when event start time is equal free time finish time" in {
        val freeTime = FreeTime(eventStart.minusMinutes(30), eventStart)
        val finishBetween = event.isOutsideFreeTimeRange(freeTime)
        finishBetween must beTrue
      }

      "return False when event start and finish time is not equal to free time" in {
        val freeTime = FreeTime(eventStart.minusMinutes(30), eventFinish.plusMinutes(30))
        val finishBetween = event.isOutsideFreeTimeRange(freeTime)
        finishBetween must beFalse
      }
    }
  }
}
