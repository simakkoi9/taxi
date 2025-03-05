package io.simakkoi9.driverservice.controller

import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.response.DriverResponse
import io.simakkoi9.driverservice.service.DriverService
import io.simakkoi9.driverservice.util.CarTestDataUtil
import io.simakkoi9.driverservice.util.DriverTestDataUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class DriverControllerTest {

    @Mock
    private lateinit var driverService: DriverService

    @InjectMocks
    private lateinit var driverController: DriverController

    private lateinit var driverCreateRequest: DriverCreateRequest
    private lateinit var driverUpdateRequest: DriverUpdateRequest
    private lateinit var driverResponse: DriverResponse
    private lateinit var updatedDriverResponse: DriverResponse

    @BeforeEach
    fun setUp() {
        driverCreateRequest = DriverTestDataUtil.getDriverCreateRequest()
        driverUpdateRequest = DriverTestDataUtil.getDriverUpdateRequest()
        driverResponse = DriverTestDataUtil.getDriverResponse()
        updatedDriverResponse = DriverTestDataUtil.getDriverResponse(name = "otherName")
    }

    @Test
    fun testCreateDriver_ShouldReturnResponse_Valid() {
        `when`(driverService.createDriver(driverCreateRequest)).thenReturn(driverResponse)

        val result = driverController.createDriver(driverCreateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify(driverService).createDriver(driverCreateRequest)
        verifyNoMoreInteractions(driverService)
    }

    @Test
    fun testUpdateDriver_ShouldReturnResponse_Valid() {
        `when`(driverService.updateDriver(DriverTestDataUtil.ID, driverUpdateRequest))
            .thenReturn(updatedDriverResponse)

        val result = driverController.updateDriver(DriverTestDataUtil.ID, driverUpdateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(updatedDriverResponse, result) }
        )
        verify(driverService).updateDriver(DriverTestDataUtil.ID, driverUpdateRequest)
        verifyNoMoreInteractions(driverService)
    }

    @Test
    fun testDeleteDriver_ShouldReturnResponse_Valid() {
        `when`(driverService.deleteDriver(DriverTestDataUtil.ID)).thenReturn(driverResponse)

        val result = driverController.deleteDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify(driverService).deleteDriver(DriverTestDataUtil.ID)
        verifyNoMoreInteractions(driverService)
    }

    @Test
    fun testGetDriver_ShouldReturnResponse_Valid() {
        `when`(driverService.getDriver(DriverTestDataUtil.ID)).thenReturn(driverResponse)

        val result = driverController.getDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify(driverService).getDriver(DriverTestDataUtil.ID)
        verifyNoMoreInteractions(driverService)
    }

    @Test
    fun testSetCarForDriver_ShouldReturnResponse_Valid() {
        val carId = CarTestDataUtil.ID
        `when`(driverService.setCarForDriver(DriverTestDataUtil.ID, carId)).thenReturn(driverResponse)

        val result = driverController.updateCarForDriver(DriverTestDataUtil.ID, carId)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify(driverService).setCarForDriver(DriverTestDataUtil.ID, carId)
        verifyNoMoreInteractions(driverService)
    }

    @Test
    fun testRemoveCarForDriver_ShouldReturnResponse_Valid() {
        `when`(driverService.removeCarForDriver(DriverTestDataUtil.ID)).thenReturn(driverResponse)

        val result = driverController.removeCarForDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify(driverService).removeCarForDriver(DriverTestDataUtil.ID)
        verifyNoMoreInteractions(driverService)
    }

    @Test
    fun testGetAllDrivers_ShouldReturnResponse_Valid() {
        val pageResponse = DriverTestDataUtil.getPageResponse(listOf(driverResponse))
        `when`(driverService.getAllDrivers(DriverTestDataUtil.PAGE, DriverTestDataUtil.SIZE))
            .thenReturn(pageResponse)

        val result = driverController.getAllDrivers(DriverTestDataUtil.PAGE, DriverTestDataUtil.SIZE)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(pageResponse, result) }
        )
        verify(driverService).getAllDrivers(DriverTestDataUtil.PAGE, DriverTestDataUtil.SIZE)
        verifyNoMoreInteractions(driverService)
    }
}
