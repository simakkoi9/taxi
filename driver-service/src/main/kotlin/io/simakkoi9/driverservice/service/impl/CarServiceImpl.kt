package io.simakkoi9.driverservice.service.impl

import io.simakkoi9.driverservice.exception.CarNotFoundException
import io.simakkoi9.driverservice.exception.DuplicateCarFoundException
import io.simakkoi9.driverservice.model.dto.PageResponse
import io.simakkoi9.driverservice.model.dto.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.car.response.CarResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.model.mapper.CarMapper
import io.simakkoi9.driverservice.repository.CarRepository
import io.simakkoi9.driverservice.repository.DriverRepository
import io.simakkoi9.driverservice.service.CarService
import org.springframework.context.MessageSource
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CarServiceImpl(
    private val carRepository: CarRepository,
    private val driverRepository: DriverRepository,
    private val carMapper: CarMapper,
    private val messageSource: MessageSource
) : CarService {

    @Transactional
    override fun createCar(carCreateRequest: CarCreateRequest): CarResponse {
        val car = carMapper.toEntity(carCreateRequest)
        if (carRepository.existsByNumberAndStatus(carCreateRequest.number, EntryStatus.ACTIVE)) {
            throw DuplicateCarFoundException("duplicate.car.found", messageSource, carCreateRequest.number)
        }
        val createdCar = carRepository.save(car)
        return carMapper.toResponse(createdCar)
    }

    @Transactional
    override fun updateCar(id: Long, carUpdateRequest: CarUpdateRequest): CarResponse {
        val car = findActiveCarByIdOrElseThrow(id)
        carMapper.partialUpdate(carUpdateRequest, car)
        val updatedCar = carRepository.save(car)
        return carMapper.toResponse(updatedCar)
    }

    @Transactional
    override fun deleteCar(id: Long): CarResponse {
        val car = findActiveCarByIdOrElseThrow(id)

        driverRepository.findAllByCarAndStatus(car, EntryStatus.ACTIVE).forEach { driver ->
            driver.car = null
            driverRepository.save(driver)
        }

        car.status = EntryStatus.DELETED
        val deletedCar = carRepository.save(car)
        return carMapper.toResponse(deletedCar)
    }

    override fun getCar(id: Long): CarResponse {
        val car = findActiveCarByIdOrElseThrow(id)
        return carMapper.toResponse(car)
    }

    override fun getAllCars(page: Int, size: Int): PageResponse<CarResponse> {
        val carPage = carRepository.findAllByStatus(EntryStatus.ACTIVE, PageRequest.of(page, size))
        val carList = carPage.content
        val carResponseList = carMapper.toResponseList(carList)

        return PageResponse(
            carResponseList,
            carPage.size,
            carPage.number,
            carPage.totalPages,
            carPage.totalElements
        )
    }

    private fun findActiveCarByIdOrElseThrow(id: Long): Car =
        carRepository.findByIdAndStatus(id, EntryStatus.ACTIVE)
            .orElseThrow {
                CarNotFoundException("car.not.found", messageSource, id)
            }
}