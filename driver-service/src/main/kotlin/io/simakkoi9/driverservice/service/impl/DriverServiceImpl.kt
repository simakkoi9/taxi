package io.simakkoi9.driverservice.service.impl

import io.simakkoi9.driverservice.exception.CarIsNotAvailableException
import io.simakkoi9.driverservice.exception.CarNotFoundException
import io.simakkoi9.driverservice.exception.DriverNotFoundException
import io.simakkoi9.driverservice.exception.DuplicateDriverFoundException
import io.simakkoi9.driverservice.model.dto.PageResponse
import io.simakkoi9.driverservice.model.dto.driver.request.DriverCreateRequest
import io.simakkoi9.driverservice.model.dto.driver.request.DriverUpdateRequest
import io.simakkoi9.driverservice.model.dto.driver.response.DriverResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.Driver
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.model.mapper.DriverMapper
import io.simakkoi9.driverservice.repository.CarRepository
import io.simakkoi9.driverservice.repository.DriverRepository
import io.simakkoi9.driverservice.service.DriverService
import org.springframework.context.MessageSource
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DriverServiceImpl(
    private val driverRepository: DriverRepository,
    private val carRepository: CarRepository,
    private val driverMapper: DriverMapper,
    private val messageSource: MessageSource
) : DriverService {

    @Transactional
    override fun createDriver(driverCreateRequest: DriverCreateRequest): DriverResponse {
        val driver = driverMapper.toEntity(driverCreateRequest)
        if (driverRepository.existsByEmailAndStatus(driverCreateRequest.email, EntryStatus.ACTIVE)) {
            throw DuplicateDriverFoundException("duplicate.driver.found", messageSource, driverCreateRequest.email)
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
        if (!isCarAvailable(carId)) {
            throw CarIsNotAvailableException("car.is.not.available", messageSource)
        }
        val driver = findActiveDriverByIdOrElseThrow(driverId)
        val car = findActiveCarByIdOrElseThrow(carId)
        driver.car = car
        val updatedDriver = driverRepository.save(driver)
        return driverMapper.toResponse(updatedDriver)
    }

    @Transactional
    override fun removeCarForDriver(id: Long): DriverResponse {
        val driver = findActiveDriverByIdOrElseThrow(id)
        driver.car = null
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

    override fun getAllDrivers(page: Int, size: Int): PageResponse<DriverResponse> {
        val drivers = driverRepository.findAllByStatus(EntryStatus.ACTIVE, PageRequest.of(page, size))
        val driverList = drivers.content
        val driverResponseList = driverMapper.toResponseList(driverList)
        return PageResponse(
            driverResponseList,
            drivers.size,
            drivers.number,
            drivers.totalPages,
            drivers.totalElements
        )
    }

    private fun findActiveDriverByIdOrElseThrow(id: Long): Driver =
        driverRepository.findByIdAndStatus(id, EntryStatus.ACTIVE)
            .orElseThrow {
                DriverNotFoundException("driver.not.found", messageSource, id)
            }

    private fun findActiveCarByIdOrElseThrow(id: Long): Car =
        carRepository.findByIdAndStatus(id, EntryStatus.ACTIVE)
            .orElseThrow {
                CarNotFoundException("car.not.found", messageSource, id)
            }

    private fun isCarAvailable(id: Long): Boolean {
        val car = carRepository.findByIdAndStatus(id, EntryStatus.ACTIVE)
        return !(car.isPresent && driverRepository.existsByCarAndStatus(car.get(), EntryStatus.ACTIVE))
    }
}
