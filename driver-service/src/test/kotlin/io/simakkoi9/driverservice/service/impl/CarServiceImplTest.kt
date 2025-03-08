package io.simakkoi9.driverservice.service.impl

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.simakkoi9.driverservice.exception.CarNotFoundException
import io.simakkoi9.driverservice.exception.DuplicateCarFoundException
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.car.response.CarResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.Driver
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.model.mapper.CarMapper
import io.simakkoi9.driverservice.repository.CarRepository
import io.simakkoi9.driverservice.repository.DriverRepository
import io.simakkoi9.driverservice.util.CarTestDataUtil
import io.simakkoi9.driverservice.util.DriverTestDataUtil
import io.simakkoi9.driverservice.util.MessageKeyConstants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.data.domain.PageImpl
import java.util.Optional
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockKExtension::class)
class CarServiceImplTest {

    @MockK
    lateinit var carRepository: CarRepository

    @MockK
    lateinit var driverRepository: DriverRepository

    @MockK
    lateinit var carMapper: CarMapper

    @MockK
    lateinit var messageSource: MessageSource

    @InjectMockKs
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
        car = CarTestDataUtil.getCar()
        updatedCar = CarTestDataUtil.getCar(model = "Corolla")
        carCreateRequest = CarTestDataUtil.getCarCreateRequest()
        carUpdateRequest = CarTestDataUtil.getCarUpdateRequest()
        carResponse = CarTestDataUtil.getCarResponse()
        updatedCarResponse = CarTestDataUtil.getCarResponse(model = "Corolla")
        driver = DriverTestDataUtil.getDriver()
    }

    @Test
    fun testCreateCar_ShouldReturnResponse_Valid() {
        every { carMapper.toEntity(carCreateRequest) } returns car
        every { carRepository.existsByNumberAndStatus(carCreateRequest.number, EntryStatus.ACTIVE) } returns false
        every { carRepository.save(car) } returns car
        every { carMapper.toResponse(car) } returns carResponse

        val result = carServiceImpl.createCar(carCreateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(carResponse, result) }
        )
        verify(exactly = 1) { carMapper.toEntity(carCreateRequest) }
        verify(exactly = 1) { carRepository.existsByNumberAndStatus(carCreateRequest.number, EntryStatus.ACTIVE) }
        verify(exactly = 1) { carRepository.save(car) }
        verify(exactly = 1) { carMapper.toResponse(car) }
        confirmVerified(carMapper, carRepository)
    }

    @Test
    fun testCreateCar_ShouldThrowDuplicateException() {
        every { carMapper.toEntity(carCreateRequest) } returns car
        every { carRepository.existsByNumberAndStatus(carCreateRequest.number, EntryStatus.ACTIVE) } returns true
        val expectedMessage = CarTestDataUtil.getDuplicateCarErrorMessage(carCreateRequest.number)
        every {
            messageSource.getMessage(
                MessageKeyConstants.DUPLICATE_CAR_FOUND,
                arrayOf(carCreateRequest.number),
                LocaleContextHolder.getLocale()
            )
        } returns expectedMessage

        val exception = assertThrows<DuplicateCarFoundException> {
            carServiceImpl.createCar(carCreateRequest)
        }

        assertEquals(expectedMessage, exception.message)
        verify(exactly = 1) { carRepository.existsByNumberAndStatus(carCreateRequest.number, EntryStatus.ACTIVE) }
        confirmVerified(carRepository)
    }

    @Test
    fun testUpdateCar_ShouldReturnResponse_Valid() {
        every { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.of(car)
        every { carMapper.partialUpdate(carUpdateRequest, car) } answers { car.apply { this.model = updatedCar.model } }
        every { carRepository.save(car) } returns car
        every { carMapper.toResponse(car) } returns updatedCarResponse

        val result = carServiceImpl.updateCar(CarTestDataUtil.ID, carUpdateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(updatedCarResponse, result) }
        )
        verify(exactly = 1) { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) }
        verify(exactly = 1) { carMapper.partialUpdate(carUpdateRequest, car) }
        verify(exactly = 1) { carRepository.save(car) }
        verify(exactly = 1) { carMapper.toResponse(car) }
        confirmVerified(carMapper, carRepository)
    }

    @Test
    fun testUpdateCar_ShouldThrowNotFoundException() {
        every { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.empty()
        val expectedMessage = CarTestDataUtil.getCarNotFoundErrorMessage(CarTestDataUtil.ID)
        every {
            messageSource.getMessage(
                MessageKeyConstants.CAR_NOT_FOUND,
                arrayOf(CarTestDataUtil.ID.toString()),
                LocaleContextHolder.getLocale()
            )
        } returns expectedMessage

        val exception = assertThrows<CarNotFoundException> {
            carServiceImpl.updateCar(CarTestDataUtil.ID, carUpdateRequest)
        }

        assertEquals(expectedMessage, exception.message)
        verify(exactly = 1) { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) }
        confirmVerified(carRepository, carMapper)
    }

    @Test
    fun testDeleteCar_ShouldReturnResponse_Valid() {
        every { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.of(car)
        every { driverRepository.findAllByCarAndStatus(car, EntryStatus.ACTIVE) } returns listOf(driver)
        every { driverRepository.save(driver) } returns driver
        every { carRepository.save(car) } returns car
        every { carMapper.toResponse(car) } returns carResponse

        val result = carServiceImpl.deleteCar(CarTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(carResponse, result) },
            { assertEquals(EntryStatus.DELETED, car.status) },
            { assertNull(driver.car) }
        )

        verify(exactly = 1) { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) }
        verify(exactly = 1) { driverRepository.findAllByCarAndStatus(car, EntryStatus.ACTIVE) }
        verify(exactly = 1) { driverRepository.save(driver) }
        verify(exactly = 1) { carRepository.save(car) }
        verify(exactly = 1) { carMapper.toResponse(car) }
        confirmVerified(carMapper, carRepository, driverRepository)
    }

    @Test
    fun testDeleteCar_ShouldThrowNotFoundException() {
        every { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.empty()
        val expectedMessage = CarTestDataUtil.getCarNotFoundErrorMessage(CarTestDataUtil.ID)
        every {
            messageSource.getMessage(
                MessageKeyConstants.CAR_NOT_FOUND,
                arrayOf(CarTestDataUtil.ID.toString()),
                LocaleContextHolder.getLocale()
            )
        } returns expectedMessage

        val exception = assertThrows<CarNotFoundException> {
            carServiceImpl.deleteCar(CarTestDataUtil.ID)
        }

        assertEquals(expectedMessage, exception.message)
        verify(exactly = 1) { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) }
        confirmVerified(carRepository, driverRepository)
    }

    @Test
    fun testGetCar_ShouldReturnResponse_Valid() {
        every { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.of(car)
        every { carMapper.toResponse(car) } returns carResponse

        val result = carServiceImpl.getCar(CarTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(carResponse, result) }
        )
        verify(exactly = 1) { carMapper.toResponse(car) }
        confirmVerified(carMapper)
    }

    @Test
    fun testGetCar_ShouldThrowNotFoundException() {
        every { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.empty()
        val expectedMessage = CarTestDataUtil.getCarNotFoundErrorMessage(CarTestDataUtil.ID)
        every {
            messageSource.getMessage(
                MessageKeyConstants.CAR_NOT_FOUND,
                arrayOf(CarTestDataUtil.ID.toString()),
                LocaleContextHolder.getLocale()
            )
        } returns expectedMessage

        val exception = assertThrows<CarNotFoundException> {
            carServiceImpl.getCar(CarTestDataUtil.ID)
        }

        assertEquals(expectedMessage, exception.message)
        verify(exactly = 1) { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) }
        confirmVerified(carRepository)
    }

    @Test
    fun testGetAllCars_ShouldReturnResponse_Valid() {
        val carPage = PageImpl(
            listOf(car),
            CarTestDataUtil.getPageRequest(),
            CarTestDataUtil.TOTAL_ELEMENTS
        )
        val pageResponse = CarTestDataUtil.getPageResponse(listOf(carResponse))
        every { carRepository.findAllByStatus(EntryStatus.ACTIVE, CarTestDataUtil.getPageRequest()) } returns carPage
        every { carMapper.toPageResponse(carPage) } returns pageResponse

        val result = carServiceImpl.getAllCars(CarTestDataUtil.PAGE, CarTestDataUtil.SIZE)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(pageResponse, result) }
        )
        verify(exactly = 1) { carRepository.findAllByStatus(EntryStatus.ACTIVE, CarTestDataUtil.getPageRequest()) }
        verify(exactly = 1) { carMapper.toPageResponse(carPage) }
        confirmVerified(carRepository, carMapper)
    }
}

