package io.simakkoi9.driverservice.util

object RegularExpressionsConstants {
    const val PHONE_REGEX = "^\\+?[1-9][0-9]{7,14}$"

    const val NAME_REGEX = "^[A-Za-zА-Яа-яЁё\\s\\-']{1,70}$"

    const val CAR_NUMBER_REGEX = "^[A-ZА-Я0-9\\s\\-]{4,10}$"
}