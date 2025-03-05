package io.simakkoi9.driverservice.service.impl

import io.simakkoi9.driverservice.exception.CarIsNotAvailableException
import io.simakkoi9.driverservice.exception.DuplicateDriverFoundException
import io.simakkoi9.driverservice.exception.DriverNotFoundException
import io.simakkoi9.driverservice.exception.CarNotFoundException
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.data.domain.PageImpl
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
internal class DriverServiceImplTest {
    @Mock
    private lateinit var driverRepository: DriverRepository

    @Mock
    private lateinit var carRepository: CarRepository

    @Mock
    private lateinit var driverMapper: DriverMapper

    @Mock
    private lateinit var kafkaDriverMapper: KafkaDriverMapper

    @Mock
    private lateinit var messageSource: MessageSource

    @InjectMocks
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
        `when`(driverMapper.toEntity(driverCreateRequest)).thenReturn(driver)
        `when`(driverRepository.existsByEmailAndStatus(driverCreateRequest.email, EntryStatus.ACTIVE))
            .thenReturn(false)
        `when`(driverRepository.save(driver)).thenReturn(driver)
        `when`(driverMapper.toResponse(driver)).thenReturn(driverResponse)

        val result = driverServiceImpl.createDriver(driverCreateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify(driverMapper).toEntity(driverCreateRequest)
        verify(driverRepository).save(driver)
        verify(driverMapper).toResponse(driver)
        verifyNoMoreInteractions(driverMapper, driverRepository)
    }

    @Test
    fun testCreateDriver_ShouldThrowDuplicateException() {
        `when`(driverRepository.existsByEmailAndStatus(driverCreateRequest.email, EntryStatus.ACTIVE))
            .thenReturn(true)
        val expectedMessage = DriverTestDataUtil.getDuplicateDriverErrorMessage(driverCreateRequest.email)
        `when`(
            messageSource.getMessage(
                MessageKeyConstants.DUPLICATE_DRIVER_FOUND,
                arrayOf(driverCreateRequest.email),
                LocaleContextHolder.getLocale()
            )
        ).thenReturn(expectedMessage)

        val exception = assertThrows<DuplicateDriverFoundException> {
            driverServiceImpl.createDriver(driverCreateRequest)
        }

        assertEquals(expectedMessage, exception.message)
        verify(driverRepository).existsByEmailAndStatus(driverCreateRequest.email, EntryStatus.ACTIVE)
        verifyNoMoreInteractions(driverRepository)
    }

    @Test
    fun testUpdateDriver_ShouldReturnResponse_Valid() {
        `when`(driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.of(driver))
        Mockito.doAnswer {
            val driver = it.getArgument<Driver>(1)
            driver.apply {
                this.name = updatedDriver.name
            }
            null
        }.`when`(driverMapper).partialUpdate(driverUpdateRequest, driver)
        `when`(driverRepository.save(driver)).thenReturn(driver)
        `when`(driverMapper.toResponse(driver)).thenReturn(driverResponse)

        val result = driverServiceImpl.updateDriver(DriverTestDataUtil.ID, driverUpdateRequest)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify(driverMapper).partialUpdate(driverUpdateRequest, driver)
        verify(driverRepository).save(driver)
        verify(driverMapper).toResponse(driver)
        verifyNoMoreInteractions(driverMapper, driverRepository)
    }

    @Test
    fun testUpdateDriver_ShouldThrowDriverNotFoundException() {
        `when`(driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.empty())

        val expectedMessage = DriverTestDataUtil.getDriverNotFoundErrorMessage(DriverTestDataUtil.ID)
        `when`(
            messageSource.getMessage(
                MessageKeyConstants.DRIVER_NOT_FOUND,
                arrayOf(DriverTestDataUtil.ID.toString()),
                LocaleContextHolder.getLocale()
            )
        ).thenReturn(expectedMessage)

        val exception = assertThrows<DriverNotFoundException> {
            driverServiceImpl.updateDriver(DriverTestDataUtil.ID, driverUpdateRequest)
        }

        assertEquals(expectedMessage, exception.message)

        verify(driverRepository).findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE)
        verifyNoMoreInteractions(driverRepository, driverMapper)
    }

    @Test
    fun testSetCarForDriver_ShouldReturnResponse_Valid() {
        `when`(carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.of(car))
        `when`(driverRepository.existsByCarAndStatus(car, EntryStatus.ACTIVE)).thenReturn(false)
        `when`(driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.of(driver))
        `when`(carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.of(car))
        `when`(driverRepository.save(driver)).thenReturn(driver)
        `when`(driverMapper.toResponse(driver)).thenReturn(driverResponseWithCar)

        val result = driverServiceImpl.setCarForDriver(DriverTestDataUtil.ID, CarTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponseWithCar, result) },
            { assertEquals(car, driver.car) }
        )
        verify(driverRepository).save(driver)
        verify(driverMapper).toResponse(driver)
        verifyNoMoreInteractions(driverMapper, driverRepository)
    }

    @Test
    fun testSetCarForDriver_ShouldThrowIsNotAvailableException_CarIsNotPresent() {
        `when`(carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.empty())
        val expectedMessage = DriverTestDataUtil.getCarIsNotAvailableErrorMessage()
        `when`(
            messageSource.getMessage(
                MessageKeyConstants.CAR_IS_NOT_AVAILABLE,
                emptyArray(),
                LocaleContextHolder.getLocale()
            )
        ).thenReturn(expectedMessage)

        val exception = assertThrows<CarIsNotAvailableException> {
            driverServiceImpl.setCarForDriver(DriverTestDataUtil.ID, CarTestDataUtil.ID)
        }

        assertEquals(expectedMessage, exception.message)
        verify(carRepository).findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE)
        verifyNoMoreInteractions(carRepository, driverRepository)
    }

    @Test
    fun testSetCarForDriver_ShouldThrowIsNotAvailableException_CarAlreadyAssigned() {
        `when`(carRepository.findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.of(car))
        `when`(driverRepository.existsByCarAndStatus(car, EntryStatus.ACTIVE))
            .thenReturn(true)
        val expectedMessage = DriverTestDataUtil.getCarIsNotAvailableErrorMessage()
        `when`(
            messageSource.getMessage(
                MessageKeyConstants.CAR_IS_NOT_AVAILABLE,
                emptyArray(),
                LocaleContextHolder.getLocale()
            )
        ).thenReturn(expectedMessage)

        val exception = assertThrows<CarIsNotAvailableException> {
            driverServiceImpl.setCarForDriver(DriverTestDataUtil.ID, CarTestDataUtil.ID)
        }

        assertEquals(expectedMessage, exception.message)
        verify(carRepository).findByIdAndStatus(CarTestDataUtil.ID, EntryStatus.ACTIVE)
        verify(driverRepository).existsByCarAndStatus(car, EntryStatus.ACTIVE)
        verifyNoMoreInteractions(carRepository, driverRepository)
    }

    @Test
    fun testRemoveCarForDriver_ShouldReturnResponse_Valid() {
        `when`(driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.of(driverWithCar))
        `when`(driverRepository.save(driverWithCar)).thenReturn(driverWithCar)
        `when`(driverMapper.toResponse(driverWithCar)).thenReturn(driverResponse)

        val result = driverServiceImpl.removeCarForDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) },
            { assertNull(driverWithCar.car) }
        )
        verify(driverRepository).save(driverWithCar)
        verify(driverMapper).toResponse(driverWithCar)
        verifyNoMoreInteractions(driverMapper, driverRepository)
    }

    @Test
    fun testDeleteDriver_ShouldReturnResponse_Valid() {
        `when`(driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.of(driver))
        `when`(driverRepository.save(driver)).thenReturn(driver)
        `when`(driverMapper.toResponse(driver)).thenReturn(driverResponse)

        val result = driverServiceImpl.deleteDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) },
            { assertEquals(EntryStatus.DELETED, driver.status) }
        )
        verify(driverRepository).save(driver)
        verify(driverMapper).toResponse(driver)
        verifyNoMoreInteractions(driverRepository, driverMapper)
    }

    @Test
    fun testGetDriver_ShouldReturnResponse_Valid() {
        `when`(driverRepository.findByIdAndStatus(DriverTestDataUtil.ID, EntryStatus.ACTIVE))
            .thenReturn(Optional.of(driver))
        `when`(driverMapper.toResponse(driver)).thenReturn(driverResponse)

        val result = driverServiceImpl.getDriver(DriverTestDataUtil.ID)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(driverResponse, result) }
        )
        verify(driverMapper).toResponse(driver)
        verifyNoMoreInteractions(driverRepository, driverMapper)
    }

    @Test
    fun testGetAvailableDriverForRide_ShouldReturnKafkaResponse() {
        `when`(driverRepository
            .findFirstByStatusAndCarNotNullAndIdNotIn(
                EntryStatus.ACTIVE,
                listOf(DriverTestDataUtil.ID)
            )
        ).thenReturn(listOf(driver))
        `when`(kafkaDriverMapper.toDto(driver)).thenReturn(kafkaDriverResponse)

        val result = driverServiceImpl.getAvailableDriverForRide(listOf(DriverTestDataUtil.ID))

        assertAll(
            { assertNotNull(result) },
            { assertEquals(kafkaDriverResponse, result) }
        )
        verify(kafkaDriverMapper).toDto(driver)
        verifyNoMoreInteractions(kafkaDriverMapper)
    }

    @Test
    fun testGetAvailableDriverForRide_ShouldThrowNoAvailableDriverException() {
        `when`(driverRepository
            .findFirstByStatusAndCarNotNullAndIdNotIn(
                EntryStatus.ACTIVE,
                listOf(DriverTestDataUtil.ID)
            )
        ).thenReturn(emptyList())

        assertThrows<NoAvailableDriverException> {
            driverServiceImpl.getAvailableDriverForRide(listOf(DriverTestDataUtil.ID))
        }

        verify(driverRepository).findFirstByStatusAndCarNotNullAndIdNotIn(
            EntryStatus.ACTIVE,
            listOf(DriverTestDataUtil.ID)
        )
        verifyNoMoreInteractions(driverRepository, kafkaDriverMapper)
    }

    @Test
    fun testGetAllDrivers_ShouldReturnPagedResponse_Valid() {
        val driverPage = PageImpl(
            listOf(driver),
            DriverTestDataUtil.getPageRequest(),
            DriverTestDataUtil.TOTAL_ELEMENTS
        )
        val pageResponse = DriverTestDataUtil.getPageResponse(listOf(driverResponse))
        `when`(driverRepository.findAllByStatus(EntryStatus.ACTIVE, DriverTestDataUtil.getPageRequest()))
            .thenReturn(driverPage)
        `when`(driverMapper.toPageResponse(driverPage)).thenReturn(pageResponse)

        val result = driverServiceImpl.getAllDrivers(DriverTestDataUtil.PAGE, DriverTestDataUtil.SIZE)

        assertAll(
            { assertNotNull(result) },
            { Assertions.assertEquals(pageResponse, result) }
        )
        verify(driverRepository).findAllByStatus(EntryStatus.ACTIVE, DriverTestDataUtil.getPageRequest())
        verify(driverMapper).toPageResponse(driverPage)
        verifyNoMoreInteractions(driverRepository, driverMapper)
    }
}
