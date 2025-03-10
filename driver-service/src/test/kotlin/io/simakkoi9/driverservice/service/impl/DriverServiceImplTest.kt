package io.simakkoi9.driverservice.service.impl

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.simakkoi9.driverservice.exception.CarIsNotAvailableException
import io.simakkoi9.driverservice.exception.DriverNotFoundException
import io.simakkoi9.driverservice.exception.DuplicateDriverFoundException
import io.simakkoi9.driverservice.exception.NoAvailableDriverException
import io.simakkoi9.driverservice.model.dto.kafka.KafkaDriverResponse
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverCreateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.request.DriverUpdateRequest
import io.simakkoi9.driverservice.model.dto.rest.driver.response.DriverResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.Driver
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.model.mapper.DriverMapper
import io.simakkoi9.driverservice.model.mapper.KafkaDriverMapper
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
class DriverServiceImplTest {

    @MockK
    private lateinit var driverRepository: DriverRepository

    @MockK
    private lateinit var carRepository: CarRepository

    @MockK
    private lateinit var driverMapper: DriverMapper

    @MockK
    private lateinit var kafkaDriverMapper: KafkaDriverMapper

    @MockK
    private lateinit var messageSource: MessageSource

    @InjectMockKs
    private lateinit var driverServiceImpl: DriverServiceImpl

    private lateinit var driverCreateRequest: DriverCreateRequest
    private lateinit var driverUpdateRequest: DriverUpdateRequest
    private lateinit var driver: Driver
    private lateinit var driverWithCar: Driver
    private lateinit var updatedDriver: Driver
    private lateinit var car: Car
    private lateinit var driverResponse: DriverResponse
    private lateinit var driverResponseWithCar: DriverResponse
    private lateinit var kafkaDriverResponse: KafkaDriverResponse

    @BeforeEach
    fun setUp() {
        driverCreateRequest = DriverTestDataUtil.getDriverCreateRequest()
        driverUpdateRequest = DriverTestDataUtil.getDriverUpdateRequest()
        car = CarTestDataUtil.getCar()
        driver = DriverTestDataUtil.getDriver()
        driverWithCar = DriverTestDataUtil.getDriver(car = car)
        updatedDriver = DriverTestDataUtil.getDriver(name = "otherName")
        driverResponse = DriverTestDataUtil.getDriverResponse()
        driverResponseWithCar = DriverTestDataUtil.getDriverResponse(carId = car.id)
        kafkaDriverResponse = DriverTestDataUtil.getKafkaDriverResponse()
    }

    @Test
    fun testCreateDriver_ShouldReturnResponse_Valid() {
        every { driverMapper.toEntity(driverCreateRequest) } returns driver
        every { driverRepository.existsByEmailAndStatus(driverCreateRequest.email, EntryStatus.ACTIVE) } returns false
        every { driverRepository.save(driver) } returns driver
        every { driverMapper.toResponse(driver) } returns driverResponse

        val result = driverServiceImpl.createDriver(driverCreateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify(exactly = 1) { driverMapper.toEntity(driverCreateRequest) }
        verify(exactly = 1) { driverRepository.existsByEmailAndStatus(driverCreateRequest.email, EntryStatus.ACTIVE) }
        verify(exactly = 1) { driverRepository.save(driver) }
        verify(exactly = 1) { driverMapper.toResponse(driver) }
        confirmVerified(driverMapper, driverRepository)
    }

    @Test
    fun testCreateDriver_ShouldThrowDuplicateException() {
        every { driverMapper.toEntity(driverCreateRequest) } returns driver
        every { driverRepository.existsByEmailAndStatus(driverCreateRequest.email, EntryStatus.ACTIVE) } returns true
        val expectedMessage = DriverTestDataUtil.getDuplicateDriverErrorMessage(driverCreateRequest.email)
        every {
            messageSource.getMessage(
                MessageKeyConstants.DUPLICATE_DRIVER_FOUND,
                arrayOf(driverCreateRequest.email),
                LocaleContextHolder.getLocale()
            )
        } returns expectedMessage

        val exception = assertThrows<DuplicateDriverFoundException> {
            driverServiceImpl.createDriver(driverCreateRequest)
        }

        assertEquals(expectedMessage, exception.message)
        verify(exactly = 1) { driverMapper.toEntity(driverCreateRequest) }
        verify(exactly = 1) { driverRepository.existsByEmailAndStatus(driverCreateRequest.email, EntryStatus.ACTIVE) }
        confirmVerified(driverRepository)
    }

    @Test
    fun testUpdateDriver_ShouldReturnResponse_Valid() {
        every { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.of(driver)
        every { driverMapper.partialUpdate(driverUpdateRequest, driver) } answers {
            driver.apply { this.name = updatedDriver.name }
            driver
        }
        every { driverRepository.save(driver) } returns driver
        every { driverMapper.toResponse(driver) } returns driverResponse

        val result = driverServiceImpl.updateDriver(DriverTestDataUtil.ID, driverUpdateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify(exactly = 1) { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) }
        verify(exactly = 1) { driverMapper.partialUpdate(driverUpdateRequest, driver) }
        verify(exactly = 1) { driverRepository.save(driver) }
        verify(exactly = 1) { driverMapper.toResponse(driver) }
        confirmVerified(driverMapper, driverRepository)
    }

    @Test
    fun testUpdateDriver_ShouldThrowDriverNotFoundException() {
        every { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.empty()
        val expectedMessage = DriverTestDataUtil.getDriverNotFoundErrorMessage(DriverTestDataUtil.ID)
        every {
            messageSource.getMessage(
                MessageKeyConstants.DRIVER_NOT_FOUND,
                arrayOf(DriverTestDataUtil.ID.toString()),
                LocaleContextHolder.getLocale()
            )
        } returns expectedMessage

        val exception = assertThrows<DriverNotFoundException> {
            driverServiceImpl.updateDriver(DriverTestDataUtil.ID, driverUpdateRequest)
        }

        assertEquals(expectedMessage, exception.message)
        verify(exactly = 1) { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) }
        confirmVerified(driverRepository, driverMapper)
    }

    @Test
    fun testSetCarForDriver_ShouldReturnResponse_Valid() {
        every { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.of(car)
        every { driverRepository.existsByCarAndStatus(car, EntryStatus.ACTIVE) } returns false
        every { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.of(driver)
        every { driverRepository.save(driver) } returns driver
        every { driverMapper.toResponse(driver) } returns driverResponseWithCar

        val result = driverServiceImpl.setCarForDriver(DriverTestDataUtil.ID, CarTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponseWithCar, result) },
            { assertEquals(car, driver.car) }
        )
        verify(exactly = 2) { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) }
        verify(exactly = 1) { driverRepository.existsByCarAndStatus(car, EntryStatus.ACTIVE) }
        verify(exactly = 1) { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) }
        verify(exactly = 1) { driverRepository.save(driver) }
        verify(exactly = 1) { driverMapper.toResponse(driver) }
        confirmVerified(driverMapper, driverRepository, carRepository)
    }

    @Test
    fun testSetCarForDriver_ShouldThrowIsNotAvailableException_CarIsNotPresent() {
        every { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) } returns
                Optional.of(driver)
        every { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) } returns
                Optional.empty()
        val expectedMessage = DriverTestDataUtil.getCarIsNotAvailableErrorMessage()
        every {
            messageSource.getMessage(
                MessageKeyConstants.CAR_IS_NOT_AVAILABLE,
                emptyArray(),
                LocaleContextHolder.getLocale()
            )
        } returns expectedMessage

        val exception = assertThrows<CarIsNotAvailableException> {
            driverServiceImpl.setCarForDriver(DriverTestDataUtil.ID, CarTestDataUtil.ID)
        }

        assertEquals(expectedMessage, exception.message)
        verify(exactly = 1) { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) }
        verify(exactly = 1) { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) }
        confirmVerified(carRepository, driverRepository)
    }

    @Test
    fun testSetCarForDriver_ShouldThrowIsNotAvailableException_CarAlreadyAssigned() {
        every { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) } returns
                Optional.of(driver)
        every { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) } returns
                Optional.of(car)
        every { driverRepository.existsByCarAndStatus(car, EntryStatus.ACTIVE) } returns true
        val expectedMessage = DriverTestDataUtil.getCarIsNotAvailableErrorMessage()
        every {
            messageSource.getMessage(
                MessageKeyConstants.CAR_IS_NOT_AVAILABLE,
                emptyArray(),
                LocaleContextHolder.getLocale()
            )
        } returns expectedMessage

        val exception = assertThrows<CarIsNotAvailableException> {
            driverServiceImpl.setCarForDriver(DriverTestDataUtil.ID, CarTestDataUtil.ID)
        }

        assertEquals(expectedMessage, exception.message)
        verify(exactly = 1) { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) }
        verify(exactly = 1) { carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE) }
        verify(exactly = 1) { driverRepository.existsByCarAndStatus(car, EntryStatus.ACTIVE) }
        confirmVerified(carRepository, driverRepository)
    }

    @Test
    fun testRemoveCarForDriver_ShouldReturnResponse_Valid() {
        every { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.of(driverWithCar)
        every { driverRepository.save(driverWithCar) } returns driverWithCar
        every { driverMapper.toResponse(driverWithCar) } returns driverResponse

        val result = driverServiceImpl.removeCarForDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) },
            { assertNull(driverWithCar.car) }
        )
        verify(exactly = 1) { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) }
        verify(exactly = 1) { driverRepository.save(driverWithCar) }
        verify(exactly = 1) { driverMapper.toResponse(driverWithCar) }
        confirmVerified(driverMapper, driverRepository)
    }

    @Test
    fun testDeleteDriver_ShouldReturnResponse_Valid() {
        every { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.of(driver)
        every { driverRepository.save(driver) } returns driver
        every { driverMapper.toResponse(driver) } returns driverResponse

        val result = driverServiceImpl.deleteDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) },
            { assertEquals(EntryStatus.DELETED, driver.status) }
        )
        verify(exactly = 1) { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) }
        verify(exactly = 1) { driverRepository.save(driver) }
        verify(exactly = 1) { driverMapper.toResponse(driver) }
        confirmVerified(driverRepository, driverMapper)
    }

    @Test
    fun testGetDriver_ShouldReturnResponse_Valid() {
        every { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.of(driver)
        every { driverMapper.toResponse(driver) } returns driverResponse

        val result = driverServiceImpl.getDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify(exactly = 1) { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) }
        verify(exactly = 1) { driverMapper.toResponse(driver) }
        confirmVerified(driverRepository, driverMapper)
    }

    @Test
    fun testGetAvailableDriverForRide_ShouldReturnKafkaResponse() {
        every {
            driverRepository.findFirstByStatusAndCarNotNullAndIdNotIn(
                EntryStatus.ACTIVE,
                listOf(DriverTestDataUtil.ID)
            )
        } returns listOf(driver)
        every { kafkaDriverMapper.toDto(driver) } returns kafkaDriverResponse

        val result = driverServiceImpl.getAvailableDriverForRide(listOf(DriverTestDataUtil.ID))

        assertAll(
            { assertNotNull(result) },
            { assertEquals(kafkaDriverResponse, result) }
        )
        verify(exactly = 1) {
            driverRepository.findFirstByStatusAndCarNotNullAndIdNotIn(
                EntryStatus.ACTIVE,
                listOf(DriverTestDataUtil.ID)
            )
        }
        verify(exactly = 1) { kafkaDriverMapper.toDto(driver) }
        confirmVerified(driverRepository, kafkaDriverMapper)
    }

    @Test
    fun testGetAvailableDriverForRide_ShouldThrowNoAvailableDriverException() {
        every {
            driverRepository.findFirstByStatusAndCarNotNullAndIdNotIn(
                EntryStatus.ACTIVE,
                listOf(DriverTestDataUtil.ID)
            )
        } returns emptyList()

        assertThrows<NoAvailableDriverException> {
            driverServiceImpl.getAvailableDriverForRide(listOf(DriverTestDataUtil.ID))
        }

        verify(exactly = 1) {
            driverRepository.findFirstByStatusAndCarNotNullAndIdNotIn(
                EntryStatus.ACTIVE,
                listOf(DriverTestDataUtil.ID)
            )
        }
        confirmVerified(driverRepository, kafkaDriverMapper)
    }

    @Test
    fun testGetAllDrivers_ShouldReturnPagedResponse_Valid() {
        val driverPage = PageImpl(
            listOf(driver),
            DriverTestDataUtil.getPageRequest(),
            DriverTestDataUtil.TOTAL_ELEMENTS
        )
        val pageResponse = DriverTestDataUtil.getPageResponse(listOf(driverResponse))
        every { driverRepository.findAllByStatus(EntryStatus.ACTIVE, DriverTestDataUtil.getPageRequest()) } returns driverPage
        every { driverMapper.toPageResponse(driverPage) } returns pageResponse

        val result = driverServiceImpl.getAllDrivers(DriverTestDataUtil.PAGE, DriverTestDataUtil.SIZE)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(pageResponse, result) }
        )
        verify(exactly = 1) { driverRepository.findAllByStatus(EntryStatus.ACTIVE, DriverTestDataUtil.getPageRequest()) }
        verify(exactly = 1) { driverMapper.toPageResponse(driverPage) }
        confirmVerified(driverRepository, driverMapper)
    }

    @Test
    fun testDeleteDriver_ShouldThrowDriverNotFoundException() {
        every { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.empty()
        val expectedMessage = DriverTestDataUtil.getDriverNotFoundErrorMessage(DriverTestDataUtil.ID)
        every {
            messageSource.getMessage(
                MessageKeyConstants.DRIVER_NOT_FOUND,
                arrayOf(DriverTestDataUtil.ID.toString()),
                LocaleContextHolder.getLocale()
            )
        } returns expectedMessage

        val exception = assertThrows<DriverNotFoundException> {
            driverServiceImpl.deleteDriver(DriverTestDataUtil.ID)
        }

        assertEquals(expectedMessage, exception.message)
        verify(exactly = 1) { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) }
        confirmVerified(driverRepository, driverMapper)
    }

    @Test
    fun testGetDriver_ShouldThrowDriverNotFoundException() {
        every { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.empty()
        val expectedMessage = DriverTestDataUtil.getDriverNotFoundErrorMessage(DriverTestDataUtil.ID)
        every {
            messageSource.getMessage(
                MessageKeyConstants.DRIVER_NOT_FOUND,
                arrayOf(DriverTestDataUtil.ID.toString()),
                LocaleContextHolder.getLocale()
            )
        } returns expectedMessage

        val exception = assertThrows<DriverNotFoundException> {
            driverServiceImpl.getDriver(DriverTestDataUtil.ID)
        }

        assertEquals(expectedMessage, exception.message)
        verify(exactly = 1) { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) }
        confirmVerified(driverRepository, driverMapper)
    }

    @Test
    fun testRemoveCarForDriver_ShouldThrowDriverNotFoundException() {
        every { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) } returns Optional.empty()
        val expectedMessage = DriverTestDataUtil.getDriverNotFoundErrorMessage(DriverTestDataUtil.ID)
        every {
            messageSource.getMessage(
                MessageKeyConstants.DRIVER_NOT_FOUND,
                arrayOf(DriverTestDataUtil.ID.toString()),
                LocaleContextHolder.getLocale()
            )
        } returns expectedMessage

        val exception = assertThrows<DriverNotFoundException> {
            driverServiceImpl.removeCarForDriver(DriverTestDataUtil.ID)
        }

        assertEquals(expectedMessage, exception.message)
        verify(exactly = 1) { driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE) }
        confirmVerified(driverRepository, driverMapper)
    }
}
