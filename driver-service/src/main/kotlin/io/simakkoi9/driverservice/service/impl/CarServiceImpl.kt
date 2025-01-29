package io.simakkoi9.driverservice.service.impl

import io.simakkoi9.driverservice.exception.CarNotFoundException
import io.simakkoi9.driverservice.exception.DuplicateCarFoundException
import io.simakkoi9.driverservice.model.dto.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.car.response.CarResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.EntryStatus
import io.simakkoi9.driverservice.model.mapper.CarMapper
import io.simakkoi9.driverservice.repository.CarRepository
import io.simakkoi9.driverservice.service.CarService
import io.simakkoi9.driverservice.util.ErrorMessages.CAR_NOT_FOUND_MESSAGE
import io.simakkoi9.driverservice.util.ErrorMessages.DUPLICATE_CAR_FOUND_MESSAGE
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CarServiceImpl(
    private val carRepository: CarRepository,
    private val carMapper: CarMapper
) : CarService {

    @Transactional
    override fun createCar(carCreateRequest: CarCreateRequest): CarResponse {
        val car = carMapper.toEntity(carCreateRequest)
        if (carRepository.existsByNumberAndStatus(carCreateRequest.number, EntryStatus.ACTIVE)) {
            throw DuplicateCarFoundException(DUPLICATE_CAR_FOUND_MESSAGE.format(carCreateRequest.number))
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
        car.status = EntryStatus.DELETED
        val deletedCar = carRepository.save(car)
        return carMapper.toResponse(deletedCar)
    }

    override fun getCar(id: Long): CarResponse {
        val car = findActiveCarByIdOrElseThrow(id)
        return carMapper.toResponse(car)
    }

    override fun getAllCars(): List<CarResponse> {
        val cars = carRepository.findAllByStatus(EntryStatus.ACTIVE)
        return cars.stream()
            .map(carMapper::toResponse)
            .toList()
    }

    private fun findActiveCarByIdOrElseThrow(id: Long): Car {
        return carRepository.findByIdAndStatus(id, EntryStatus.ACTIVE)
            .orElseThrow { CarNotFoundException(CAR_NOT_FOUND_MESSAGE.format(id)) }
    }
}