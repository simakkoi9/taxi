package io.simakkoi9.driverservice.controller

import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockKExtension::class)
class DriverControllerTest {

    @MockK
    lateinit var driverService: DriverService

    @InjectMockKs
    lateinit var driverController: DriverController

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
        every { driverService.createDriver(driverCreateRequest) } returns driverResponse

        val result = driverController.createDriver(driverCreateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify { driverService.createDriver(driverCreateRequest) }
        confirmVerified(driverService)
    }

    @Test
    fun testUpdateDriver_ShouldReturnResponse_Valid() {
        every { driverService.updateDriver(DriverTestDataUtil.ID, driverUpdateRequest) } returns updatedDriverResponse

        val result = driverController.updateDriver(DriverTestDataUtil.ID, driverUpdateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(updatedDriverResponse, result) }
        )
        verify { driverService.updateDriver(DriverTestDataUtil.ID, driverUpdateRequest) }
        confirmVerified(driverService)
    }

    @Test
    fun testDeleteDriver_ShouldReturnResponse_Valid() {
        every { driverService.deleteDriver(DriverTestDataUtil.ID) } returns driverResponse

        val result = driverController.deleteDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify { driverService.deleteDriver(DriverTestDataUtil.ID) }
        confirmVerified(driverService)
    }

    @Test
    fun testGetDriver_ShouldReturnResponse_Valid() {
        every { driverService.getDriver(DriverTestDataUtil.ID) } returns driverResponse

        val result = driverController.getDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify { driverService.getDriver(DriverTestDataUtil.ID) }
        confirmVerified(driverService)
    }

    @Test
    fun testSetCarForDriver_ShouldReturnResponse_Valid() {
        val carId = CarTestDataUtil.ID
        every { driverService.setCarForDriver(DriverTestDataUtil.ID, carId) } returns driverResponse

        val result = driverController.updateCarForDriver(DriverTestDataUtil.ID, carId)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify { driverService.setCarForDriver(DriverTestDataUtil.ID, carId) }
        confirmVerified(driverService)
    }

    @Test
    fun testRemoveCarForDriver_ShouldReturnResponse_Valid() {
        every { driverService.removeCarForDriver(DriverTestDataUtil.ID) } returns driverResponse

        val result = driverController.removeCarForDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify { driverService.removeCarForDriver(DriverTestDataUtil.ID) }
        confirmVerified(driverService)
    }

    @Test
    fun testGetAllDrivers_ShouldReturnResponse_Valid() {
        val pageResponse = DriverTestDataUtil.getPageResponse(listOf(driverResponse))
        every { driverService.getAllDrivers(DriverTestDataUtil.PAGE, DriverTestDataUtil.SIZE) } returns pageResponse

        val result = driverController.getAllDrivers(DriverTestDataUtil.PAGE, DriverTestDataUtil.SIZE)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(pageResponse, result) }
        )
        verify { driverService.getAllDrivers(DriverTestDataUtil.PAGE, DriverTestDataUtil.SIZE) }
        confirmVerified(driverService)
    }
}
