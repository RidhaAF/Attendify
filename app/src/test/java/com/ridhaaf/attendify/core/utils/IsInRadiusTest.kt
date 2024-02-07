package com.ridhaaf.attendify.core.utils

import android.location.Location
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class IsInRadiusTest {
    @Mock
    private lateinit var employeeLocation: Location

    @Before
    fun setUp() {
        employeeLocation = mock(Location::class.java)
    }

    @Test
    fun isInRadius_EmployeeWithinRadius_ReturnsTrue() {
        `when`(employeeLocation.latitude).thenReturn(OfficeLocation.LATITUDE + 0.001)
        `when`(employeeLocation.longitude).thenReturn(OfficeLocation.LONGITUDE + 0.001)

        val result = isInRadius(employeeLocation)

        assertTrue(result)
    }

    @Test
    fun isInRadius_EmployeeOutsideRadius_ReturnsFalse() {
        `when`(employeeLocation.latitude).thenReturn(OfficeLocation.LATITUDE + 5)
        `when`(employeeLocation.longitude).thenReturn(OfficeLocation.LONGITUDE + 5)

        val result = isInRadius(employeeLocation)

        assertFalse(result)
    }
}