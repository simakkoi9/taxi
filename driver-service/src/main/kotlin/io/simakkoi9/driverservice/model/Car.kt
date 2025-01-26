package io.simakkoi9.driverservice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import lombok.Getter
import lombok.Setter
import lombok.ToString

@Getter
@Setter
@ToString
@Entity
@Table(name = "cars")
open class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @NotNull
    @Column(name = "brand", nullable = false)
    open var brand: String? = null

    @NotNull
    @Column(name = "model", nullable = false)
    open var model: String? = null

    @NotNull
    @Column(name = "color", nullable = false)
    open var color: String? = null

    @NotNull
    @Column(name = "number", nullable = false)
    open var number: String? = null

    @NotNull
    @Column(name = "status", nullable = false)
    open var status: UserStatus? = UserStatus.ACTIVE

    @OneToOne(mappedBy = "car")
    open var driver: Driver? = null
}