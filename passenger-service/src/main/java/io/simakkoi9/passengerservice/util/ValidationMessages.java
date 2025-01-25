package io.simakkoi9.passengerservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationMessages {
    public static final String BLANK_NAME = "Укажите имя пассажира.";
    public static final String NOT_VALID_EMAIL = "Неверный адрес электронной почты.";
    public static final String BLANK_EMAIL = "Укажите электронную почту.";
    public static final String NOT_VALID_PHONE = "Неверный номер мобильного телефона.";
    public static final String BLANK_PHONE = "Укажите номер мобильного телефона.";
    public static final String PHONE_REGEX = "^\\+?[1-9][0-9]{7,14}$";
    public static final String EMAIL_REGEX = "^(.+)@(\\S+)$";

}
