package com.ridhaaf.attendify.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.text.SimpleDateFormat
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class DefaultFormatterTest {
    @Test
    fun timeFormatter_Today_ReturnsEquals() {
        // Test case 1: time is not zero
        val time1 = 1707282665795 // February 7, 2024 in milliseconds
        val format = "HH:mm:ss"
        val expectedTime1 = SimpleDateFormat(format, Locale.getDefault()).format(time1)
        assertEquals(expectedTime1, timeFormatter(time1))

        // Test case 2: time is zero
        val time2 = 0L
        assertEquals("00:00:00", timeFormatter(time2))
    }

    @Test
    fun dateFormatter_Today_ReturnsEquals() {
        // Test case: February 7, 2021
        val time = 1707282665795 // February 7, 2024 in milliseconds
        val format = "EE, d MMMM yyyy"
        val expectedDate = SimpleDateFormat(format, Locale.getDefault()).format(time)
        assertEquals(expectedDate, dateFormatter(time))
    }
}