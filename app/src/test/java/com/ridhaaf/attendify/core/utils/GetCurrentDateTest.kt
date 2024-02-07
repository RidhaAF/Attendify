package com.ridhaaf.attendify.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.text.SimpleDateFormat
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class GetCurrentDateTest {
    @Test
    fun getCurrentDate_Today_ReturnsEquals() {
        val mockedTime = 1707282665795 // February 7, 2024 in milliseconds
        val format = "EE, d MMMM yyyy"
        val expectedDate = SimpleDateFormat(format, Locale.getDefault()).format(mockedTime)

        val currentDate = getCurrentDate()
        assertEquals(expectedDate, currentDate)
    }
}