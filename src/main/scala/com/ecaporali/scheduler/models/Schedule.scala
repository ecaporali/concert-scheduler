package com.ecaporali.scheduler.models

import java.time.LocalDateTime

/**
  * @author Enrico Caporali
  */
case class Performance(event: Event, priority: Int)

case class Plan(start: LocalDateTime, finish: LocalDateTime, performance: Performance)

case class Schedule(plans: Seq[Plan])
