package io.simakkoi9.driverservice.service.impl

import io.simakkoi9.driverservice.exception.CarNotFoundException
import io.simakkoi9.driverservice.exception.DuplicateCarFoundException
import io.simakkoi9.driverservice.model.dto.rest.PageResponse
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.response.CarResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.Driver
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.model.mapper.CarMapper
import io.simakkoi9.driverservice.repository.CarRepository
import io.simakkoi9.driverservice.repository.DriverRepository
import io.simakkoi9.driverservice.util.MessageKeyConstants
import io.simakkoi9.driverservice.util.TestDataUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.Optional
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class CarServiceImplTest {
    @Mock
    lateinit var carRepository: CarRepository

    @Mock
    lateinit var driverRepository: DriverRepository

    @Mock
    lateinit var carMapper: CarMapper

    @Mock
    lateinit var messageSource: MessageSource

    @InjectMocks
    lateinit var carServiceImpl: CarServiceImpl

    private lateinit var car: Car
    private lateinit var updatedCar: Car
    private lateinit var carCreateRequest: CarCreateRequest
    private lateinit var carUpdateRequest: CarUpdateRequest
    private lateinit var carResponse: CarResponse
    private lateinit var updatedCarResponse: CarResponse
    private lateinit var driver: Driver

    @BeforeEach
    fun setUp() {
        car = TestDataUtil.getCar()
        updatedCar = TestDataUtil.getCar(model = "Corolla")
        carCreateRequest = TestDataUtil.getCarCreateRequest()
        carUpdateRequest = TestDataUtil.getCarUpdateRequest()
        carResponse = TestDataUtil.getCarResponse()
        updatedCarResponse = TestDataUtil.getCarResponse(model = "Corolla")
        driver = TestDataUtil.getDriver()
    }

    @Test
    fun testCreateCar_ShouldReturnResponse_Valid() {
        `when`(carMapper.toEntity(carCreateRequest)).thenReturn(car)
        `when`(carRepository.existsByNumberAndStatus(carCreateRequest.number, EntryStatus.ACTIVE))
            .thenReturn(false)
        `when`(carRepository.save(car)).thenReturn(car)
        `when`(carMapper.toResponse(car)).thenReturn(carResponse)

        val result = carServiceImpl.createCar(carCreateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(carResponse, result) }
        )
        verify(carMapper).toEntity(carCreateRequest)
        verify(carRepository).save(car)
        verify(carMapper).toResponse(car)
        verifyNoMoreInteractions(carMapper, carRepository)
    }

    @Test
    fun testCreateCar_ShouldThrowDuplicateException() {
        `when`(carRepository.existsByNumberAndStatus(carCreateRequest.number, EntryStatus.ACTIVE))
            .thenReturn(true)
        val expectedMessage = TestDataUtil.getDuplicateCarErrorMessage(carCreateRequest.number)
        `when`(
            messageSource.getMessage(
                MessageKeyConstants.DUPLICATE_CAR_FOUND,
                arrayOf(carCreateRequest.number),
                LocaleContextHolder.getLocale()
            )
        ).thenReturn(expectedMessage)

        val exception = assertThrows<DuplicateCarFoundException> {
            carServiceImpl.createCar(carCreateRequest)
        }

        assertEquals(expectedMessage, exception.message)
        verify(carRepository).existsByNumberAndStatus(carCreateRequest.number, EntryStatus.ACTIVE)
        verifyNoMoreInteractions(carRepository)
    }

    @Test
    fun testUpdateCar_ShouldReturnResponse_Valid() {
        `when`(carRepository.findByIdAndStatus(TestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.of(car))
        doAnswer {
            val car = it.getArgument<Car>(1)
            car.apply {
                this.model = updatedCar.model
            }
            null
        }.`when`(carMapper).partialUpdate(carUpdateRequest, car)
        `when`(carRepository.save(car)).thenReturn(car)
        `when`(carMapper.toResponse(car)).thenReturn(updatedCarResponse)

        val result = carServiceImpl.updateCar(TestDataUtil.ID, carUpdateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(updatedCarResponse, result) }
        )
        verify(carMapper).partialUpdate(carUpdateRequest, car)
        verify(carRepository).save(car)
        verify(carMapper).toResponse(car)
        verifyNoMoreInteractions(carMapper, carRepository)
    }

    @Test
    fun testDeleteCar_ShouldReturnResponse_Valid() {
        `when`(carRepository.findByIdAndStatus(TestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.of(car))
        `when`(driverRepository.findAllByCarAndStatus(car, EntryStatus.ACTIVE)).thenReturn(listOf(driver))
        `when`(carRepository.save(car)).thenReturn(car)
        `when`(carMapper.toResponse(car)).thenReturn(carResponse)

        val result = carServiceImpl.deleteCar(TestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(carResponse, result) },
            { assertEquals(EntryStatus.DELETED, car.status) },
            { assertNull(driver.car) }
        )
        verify(driverRepository).findAllByCarAndStatus(car, EntryStatus.ACTIVE)
        verify(driverRepository).save(driver)
        verify(carRepository).save(car)
        verify(carMapper).toResponse(car)
        verifyNoMoreInteractions(carMapper, carRepository, driverRepository)
    }

    @Test
    fun testGetCar_ShouldReturnResponse_Valid() {
        `when`(carRepository.findByIdAndStatus(TestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.of(car))
        `when`(carMapper.toResponse(car)).thenReturn(carResponse)

        val result = carServiceImpl.getCar(TestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(carResponse, result) }
        )
        verify(carMapper).toResponse(car)
        verifyNoMoreInteractions(carMapper)
    }

    @Test
    fun testGetCar_ShouldThrowNotFoundException() {
        `when`(carRepository.findByIdAndStatus(TestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.empty())
        val expectedMessage = TestDataUtil.getCarNotFoundErrorMessage(TestDataUtil.ID)
        `when`(
            messageSource.getMessage(
                MessageKeyConstants.CAR_NOT_FOUND,
                arrayOf(TestDataUtil.ID.toString()),
                LocaleContextHolder.getLocale()
            )
        ).thenReturn(expectedMessage)

        val exception = assertThrows<CarNotFoundException> {
            carServiceImpl.getCar(TestDataUtil.ID)
        }

        assertEquals(expectedMessage, exception.message)
        verify(carRepository).findByIdAndStatus(TestDataUtil.ID, EntryStatus.ACTIVE)
        verifyNoMoreInteractions(carRepository)
    }

    @Test
    fun testGetAllCars_ShouldReturnResponse_Valid() {
        val page = 0
        val size = 10
        val pageRequest = PageRequest.of(page, size)
        val carList = listOf(car)
        val carPage = PageImpl(carList, pageRequest, 1L)
        `when`(carRepository.findAllByStatus(EntryStatus.ACTIVE, pageRequest)).thenReturn(carPage)
        val pageResponse = PageResponse(listOf(carResponse), size, page, 1, 1L) //Пофиксить по аналогии
        `when`(carMapper.toPageResponse(carPage)).thenReturn(pageResponse)

        val result = carServiceImpl.getAllCars(page, size)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(pageResponse, result) }
        )
        verify(carRepository).findAllByStatus(EntryStatus.ACTIVE, pageRequest)
        verify(carMapper).toPageResponse(carPage)
        verifyNoMoreInteractions(carRepository, carMapper)
    }
}
