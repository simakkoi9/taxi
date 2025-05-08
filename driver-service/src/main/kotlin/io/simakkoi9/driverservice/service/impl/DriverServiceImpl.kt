package io.simakkoi9.driverservice.service.impl

import io.simakkoi9.driverservice.exception.CarIsNotAvailableException
import io.simakkoi9.driverservice.exception.CarNotFoundException
import io.simakkoi9.driverservice.exception.DriverNotFoundException
import io.simakkoi9.driverservice.exception.DuplicateDriverFoundException
import io.simakkoi9.driverservice.exception.NoAvailableDriverException
import io.simakkoi9.driverservice.model.dto.kafka.KafkaDriverResponse
import io.simakkoi9.driverservice.model.dto.rest.PageResponse
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
import io.simakkoi9.driverservice.service.DriverService
import io.simakkoi9.driverservice.util.MessageKeyConstants
import org.hibernate.Hibernate
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DriverServiceImpl(
    private val driverRepository: DriverRepository,
    private val carRepository: CarRepository,
    private val driverMapper: DriverMapper,
    private val kafkaDriverMapper: KafkaDriverMapper,
    private val messageSource: MessageSource
) : DriverService {

    @Transactional
    override fun createDriver(driverCreateRequest: DriverCreateRequest): DriverResponse {
        val driver = driverMapper.toEntity(driverCreateRequest)
        if (driverRepository.existsByEmailAndStatus(driverCreateRequest.email, EntryStatus.ACTIVE)) {
            throw DuplicateDriverFoundException(
                MessageKeyConstants.DUPLICATE_DRIVER_FOUND,
                messageSource,
                driverCreateRequest.email
            )
        }
        val createdDriver = driverRepository.save(driver)
        return driverMapper.toResponse(createdDriver)
    }

    @Transactional
    override fun updateDriver(id: Long, driverUpdateRequest: DriverUpdateRequest): DriverResponse {
        val driver = findActiveDriverByIdOrElseThrow(id)
        driverMapper.partialUpdate(driverUpdateRequest, driver)
        val updatedDriver = driverRepository.save(driver)
        return driverMapper.toResponse(updatedDriver)
    }

    @Transactional
    override fun setCarForDriver(driverId: Long, carId: Long): DriverResponse {
        val driver = findActiveDriverByIdOrElseThrow(driverId)

        if (!isCarAvailable(carId)) {
            throw CarIsNotAvailableException(MessageKeyConstants.CAR_IS_NOT_AVAILABLE, messageSource)
        }
        val car = findActiveCarByIdOrElseThrow(carId)
        driver.apply {
            this.car = car
        }

        val updatedDriver = driverRepository.save(driver)
        return driverMapper.toResponse(updatedDriver)
    }

    @Transactional
    override fun removeCarForDriver(id: Long): DriverResponse {
        val driver = findActiveDriverByIdOrElseThrow(id)
        driver.apply {
            this.car = null
        }
        val updatedDriver = driverRepository.save(driver)
        return driverMapper.toResponse(updatedDriver)
    }

    @Transactional
    override fun deleteDriver(id: Long): DriverResponse {
        val driver = findActiveDriverByIdOrElseThrow(id)
        driver.status = EntryStatus.DELETED
        val deletedDriver = driverRepository.save(driver)
        return driverMapper.toResponse(deletedDriver)
    }

    override fun getDriver(id: Long): DriverResponse {
        val driver = findActiveDriverByIdOrElseThrow(id)
        return driverMapper.toResponse(driver)
    }

    @Transactional
    override fun getAvailableDriverForRide(driverIdList: List<Long>): KafkaDriverResponse {
        val driver = driverRepository
            .findFirstByStatusAndCarNotNullAndIdNotIn(EntryStatus.ACTIVE, driverIdList)
            .firstOrNull()

        driver?.let {
            Hibernate.initialize(it.car)
            return kafkaDriverMapper.toDto(it)
        } ?: throw NoAvailableDriverException()
    }

    override fun getAllDrivers(page: Int, size: Int): PageResponse<DriverResponse> {
        val drivers: Page<Driver> = driverRepository.findAllByStatus(EntryStatus.ACTIVE, PageRequest.of(page, size))
        return driverMapper.toPageResponse(drivers)
    }

    override fun isDriverOwner(driverId: Long, userId: String): Boolean =
        driverRepository.findById(driverId)
            .map { it.externalId == userId }
            .orElse(false)

    private fun findActiveDriverByIdOrElseThrow(id: Long): Driver =
        driverRepository.findByIdAndStatus(id, EntryStatus.ACTIVE)
            .orElseThrow {
                DriverNotFoundException(MessageKeyConstants.DRIVER_NOT_FOUND, messageSource, id)
            }

    private fun findActiveCarByIdOrElseThrow(id: Long): Car =
        carRepository.findByIdAndStatus(id, EntryStatus.ACTIVE)
            .orElseThrow {
                CarNotFoundException(MessageKeyConstants.CAR_NOT_FOUND, messageSource, id)
            }

    private fun isCarAvailable(id: Long): Boolean {
        val carOptional = carRepository.findByIdAndStatus(id, EntryStatus.ACTIVE)
        if (carOptional.isEmpty) {
            return false
        }
        return !driverRepository.existsByCarAndStatus(carOptional.get(), EntryStatus.ACTIVE)
    }
}
