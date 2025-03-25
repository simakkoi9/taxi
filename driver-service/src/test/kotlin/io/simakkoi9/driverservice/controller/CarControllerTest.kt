package io.simakkoi9.driverservice.controller

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.response.CarResponse
import io.simakkoi9.driverservice.service.CarService
import io.simakkoi9.driverservice.util.CarTestDataUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockKExtension::class)
class CarControllerTest {

    @MockK
    lateinit var carService: CarService

    @InjectMockKs
    lateinit var carController: CarController

    private lateinit var carCreateRequest: CarCreateRequest
    private lateinit var carUpdateRequest: CarUpdateRequest
    private lateinit var carResponse: CarResponse
    private lateinit var updatedCarResponse: CarResponse

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun testCreateCar_ShouldReturnResponse_Valid() {
        carCreateRequest = CarTestDataUtil.getCarCreateRequest()
        carResponse = CarTestDataUtil.getCarResponse()
        every { carService.createCar(carCreateRequest) } returns carResponse

        val result = carController.createCar(carCreateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(carResponse, result) }
        )
        verify(exactly = 1) { carService.createCar(carCreateRequest) }
        confirmVerified(carService)
    }

    @Test
    fun testUpdateCar_ShouldReturnResponse_Valid() {
        carUpdateRequest = CarTestDataUtil.getCarUpdateRequest()
        updatedCarResponse = CarTestDataUtil.getCarResponse(model = "Corolla")
        every { carService.updateCar(CarTestDataUtil.ID, carUpdateRequest) } returns updatedCarResponse

        val result = carController.updateCar(CarTestDataUtil.ID, carUpdateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(updatedCarResponse, result) }
        )
        verify(exactly = 1) { carService.updateCar(CarTestDataUtil.ID, carUpdateRequest) }
        confirmVerified(carService)
    }

    @Test
    fun testDeleteCar_ShouldReturnResponse_Valid() {
        carResponse = CarTestDataUtil.getCarResponse()
        every { carService.deleteCar(CarTestDataUtil.ID) } returns carResponse

        val result = carController.deleteCar(CarTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(carResponse, result) }
        )
        verify(exactly = 1) { carService.deleteCar(CarTestDataUtil.ID) }
        confirmVerified(carService)
    }

    @Test
    fun testGetCar_ShouldReturnResponse_Valid() {
        carResponse = CarTestDataUtil.getCarResponse()
        every { carService.getCar(CarTestDataUtil.ID) } returns carResponse

        val result = carController.getCar(CarTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(carResponse, result) }
        )
        verify(exactly = 1) { carService.getCar(CarTestDataUtil.ID) }
        confirmVerified(carService)
    }

    @Test
    fun testGetAllCars_ShouldReturnPagedResponse_Valid() {
        carResponse = CarTestDataUtil.getCarResponse()
        val pageResponse = CarTestDataUtil.getPageResponse(listOf(carResponse))
        every { carService.getAllCars(CarTestDataUtil.PAGE, CarTestDataUtil.SIZE) } returns pageResponse

        val result = carController.getAllCars(CarTestDataUtil.PAGE, CarTestDataUtil.SIZE)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(pageResponse, result) }
        )
        verify(exactly = 1) { carService.getAllCars(CarTestDataUtil.PAGE, CarTestDataUtil.SIZE) }
        confirmVerified(carService)
    }
}
