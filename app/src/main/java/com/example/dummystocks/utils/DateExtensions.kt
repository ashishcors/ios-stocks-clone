package com.example.dummystocks.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

const val DATE_FORMAT_MMMM_DD = "MMMM dd"

fun LocalDateTime.timeAgo(): String {
  val now = LocalDateTime.now()
  val seconds = java.time.Duration.between(this, now).seconds
  val minutes = seconds / 60
  val hours = minutes / 60
  val days = hours / 24
  val weeks = days / 7
  val months = days / 30
  val years = days / 365

  return when {
    seconds < 60 -> "Just now"
    minutes < 60 -> "$minutes min ago"
    hours < 24 -> "${hours}h ago"
    days < 7 -> "${days}d ago"
    weeks < 4 -> "${weeks}w ago"
    months < 12 -> "${months}m ago"
    else -> "${years}y ago"
  }
}

fun LocalDate.format(
  outputFormat: String,
  getUTCTime: Boolean = false
): String {
  val dateFormat = DateTimeFormatter.ofPattern(outputFormat)
    .apply {
      if (getUTCTime) this.withZone(ZoneOffset.UTC)
    }
  return this.format(dateFormat)
}