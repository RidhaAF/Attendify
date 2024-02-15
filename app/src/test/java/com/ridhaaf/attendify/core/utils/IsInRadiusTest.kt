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
    private lateinit var officeLocation: Location

    @Before
    fun setUp() {
        employeeLocation = mock(Location::class.java)
        officeLocation = mock(Location::class.java)
        `when`(officeLocation.latitude).thenReturn(OfficeLocation.LATITUDE)
        `when`(officeLocation.longitude).thenReturn(OfficeLocation.LONGITUDE)
    }

    @Test
    fun isInRadius_EmployeeWithinRadius_ReturnsTrue() {
        val range = 0.0005 // 50 m away
        `when`(employeeLocation.latitude).thenReturn(OfficeLocation.LATITUDE + range)
        `when`(employeeLocation.longitude).thenReturn(OfficeLocation.LONGITUDE + range)
        `when`(employeeLocation.distanceTo(officeLocation)).thenReturn(50f) // 50 meters away

        val result = isInRadius(employeeLocation, officeLocation)

        assertTrue(result)
    }

    @Test
    fun isInRadius_EmployeeOutsideRadius_ReturnsFalse() {
        val range = 0.05 // 5 km away
        `when`(employeeLocation.latitude).thenReturn(OfficeLocation.LATITUDE + range)
        `when`(employeeLocation.longitude).thenReturn(OfficeLocation.LONGITUDE + range)
        `when`(employeeLocation.distanceTo(officeLocation)).thenReturn(5000f) // 5 km away

        val result = isInRadius(employeeLocation, officeLocation)

        assertFalse(result)
    }

    @Test
    fun isInRadius_NullLocation_ReturnsFalse() {
        val range = 0.0
        `when`(employeeLocation.latitude).thenReturn(range)
        `when`(employeeLocation.longitude).thenReturn(range)

        val result = isInRadius(employeeLocation, officeLocation)

        assertFalse(result)
    }
}