package io.simakkoi9.driverservice.service.impl

import io.simakkoi9.driverservice.exception.CarNotFoundException
import io.simakkoi9.driverservice.exception.DuplicateCarFoundException
import io.simakkoi9.driverservice.exception.EmptyCarListException
import io.simakkoi9.driverservice.model.dto.car.request.CarCreateRequest
import io.simakkoi9.driverservice.model.dto.car.request.CarUpdateRequest
import io.simakkoi9.driverservice.model.dto.car.response.CarResponse
import io.simakkoi9.driverservice.model.entity.Car
import io.simakkoi9.driverservice.model.entity.UserStatus
import io.simakkoi9.driverservice.model.mapper.CarMapper
import io.simakkoi9.driverservice.repository.CarRepository
import io.simakkoi9.driverservice.service.CarService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CarServiceImpl(private val carRepository: CarRepository, private val carMapper: CarMapper) : CarService {

    @Transactional
    override fun createCar(carCreateRequest: CarCreateRequest): CarResponse {
        val car = carMapper.toEntity(carCreateRequest)
        if (carRepository.existsByNumberAndStatus(carCreateRequest.number, UserStatus.ACTIVE)){
            throw DuplicateCarFoundException("")
        }
        val createdCar = carRepository.save(car)
        return carMapper.toResponse(createdCar)
    }

    @Transactional
    override fun updateCar(id: Long, carUpdateRequest: CarUpdateRequest): CarResponse {
        val car = findActiveCarOrElseThrow(id)
        carMapper.partialUpdate(carUpdateRequest, car)
        val updatedCar = carRepository.save(car)
        return carMapper.toResponse(updatedCar)
    }

    @Transactional
    override fun deleteCar(id: Long): CarResponse {
        val car = findActiveCarOrElseThrow(id)
        car.status = UserStatus.DELETED
        return carMapper.toResponse(car)
    }

    override fun getCar(id: Long): CarResponse {
        val car = findActiveCarOrElseThrow(id)
        return carMapper.toResponse(car)
    }

    override fun getAllCars(): List<CarResponse> {
        val cars = carRepository.findAllByStatus(UserStatus.ACTIVE)
        if (cars.isEmpty()){
            throw EmptyCarListException("")
        }
        return cars.stream()
            .map(carMapper::toResponse)
            .toList()
    }

    private fun findActiveCarOrElseThrow(id: Long): Car {
        return carRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
            .orElseThrow { CarNotFoundException("") }
    }

}