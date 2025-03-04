package io.simakkoi9.driverservice.controller

import io.simakkoi9.driverservice.model.dto.rest.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.response.CarResponse
import io.simakkoi9.driverservice.service.CarService
import io.simakkoi9.driverservice.util.TestDataUtil
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
class CarControllerTest {

    @Mock
    private lateinit var carService: CarService

    @InjectMocks
    private lateinit var carController: CarController

    private lateinit var carCreateRequest: CarCreateRequest

    private lateinit var carUpdateRequest: CarUpdateRequest

    private lateinit var carResponse: CarResponse

    private lateinit var updatedCarResponse: CarResponse

    @BeforeEach
    fun setUp() {
        carCreateRequest = TestDataUtil.getCarCreateRequest()
        carUpdateRequest = TestDataUtil.getCarUpdateRequest()
        carResponse = TestDataUtil.getCarResponse()
        updatedCarResponse = TestDataUtil.getCarResponse(model = "Corolla")
    }

    @Test
    fun testCreateCar_ShouldReturnResponse_Valid() {
        `when`(carService.createCar(carCreateRequest)).thenReturn(carResponse)

        val result = carController.createCar(carCreateRequest)

        assertAll(
            { assertNotNull(result)},
            { assertEquals(carResponse, result) }
        )
        verify(carService).createCar(carCreateRequest)
        verifyNoMoreInteractions(carService)
    }

    @Test
    fun testUpdateCar_ShouldReturnResponse_Valid() {
        `when`(carService.updateCar(TestDataUtil.ID, carUpdateRequest)).thenReturn(updatedCarResponse)

        val result = carController.updateCar(TestDataUtil.ID, carUpdateRequest)

        assertAll(
            { assertNotNull(result)},
            { assertEquals(updatedCarResponse, result) }
        )
        verify(carService).updateCar(TestDataUtil.ID, carUpdateRequest)
        verifyNoMoreInteractions(carService)
    }

    @Test
    fun testDeleteCar_ShouldReturnResponse_Valid() {
        `when`(carService.deleteCar(TestDataUtil.ID)).thenReturn(carResponse)

        val result = carController.deleteCar(TestDataUtil.ID)

        assertAll(
            { assertNotNull(result)},
            { assertEquals(carResponse, result) }
        )
        verify(carService).deleteCar(TestDataUtil.ID)
        verifyNoMoreInteractions(carService)
    }

    @Test
    fun testGetCar_ShouldReturnResponse_Valid() {
        `when`(carService.getCar(TestDataUtil.ID)).thenReturn(carResponse)

        val result = carController.getCar(TestDataUtil.ID)

        assertAll(
            { assertNotNull(result)},
            { assertEquals(carResponse, result) }
        )
        verify(carService).getCar(TestDataUtil.ID)
        verifyNoMoreInteractions(carService)
    }

    @Test
    fun testGetAllCars() {

    }
}
