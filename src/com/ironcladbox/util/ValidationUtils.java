package com.ironcladbox.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{6,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern ONLY_LETTERS_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");

    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) return "El email es obligatorio";
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) return "Formato de email inv\u00e1lido";
        return null;
    }

    public static String validateCrossfitEmail(String email) {
        String err = validateEmail(email);
        if (err != null) return err;
        if (!email.trim().toLowerCase().endsWith("@crossfit.com"))
            return "El email debe ser del dominio @crossfit.com";
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) return "La contrase\u00f1a no puede estar vac\u00eda";
        if (!PASSWORD_PATTERN.matcher(password).matches())
            return "La contrase\u00f1a debe tener al menos 6 caracteres, 1 may\u00fascula y 1 n\u00famero";
        return null;
    }

    public static String validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) return fieldName + " no puede estar vac\u00edo";
        return null;
    }

    public static String validateMinLength(String value, int min, String fieldName) {
        if (value == null || value.trim().length() < min)
            return fieldName + " debe tener al menos " + min + " caracteres";
        return null;
    }

    public static String validateOnlyLetters(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) return fieldName + " no puede estar vac\u00edo";
        if (!ONLY_LETTERS_PATTERN.matcher(value.trim()).matches())
            return fieldName + " solo puede contener letras y espacios";
        return null;
    }

    public static String validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return "El tel\u00e9fono es obligatorio";
        if (!PHONE_PATTERN.matcher(phone.trim()).matches())
            return "El tel\u00e9fono debe tener exactamente 10 d\u00edgitos";
        return null;
    }

    public static String validatePhoneOptional(String phone) {
        if (phone == null || phone.trim().isEmpty()) return null;
        if (!PHONE_PATTERN.matcher(phone.trim()).matches())
            return "El tel\u00e9fono debe tener exactamente 10 d\u00edgitos";
        return null;
    }

    public static String validateDate(String date, String fieldName) {
        if (date == null || date.trim().isEmpty()) return fieldName + " es obligatorio";
        try {
            LocalDate.parse(date.trim());
            return null;
        } catch (Exception e) {
            return fieldName + " debe tener formato YYYY-MM-DD";
        }
    }

    public static String validateDateOptional(String date, String fieldName) {
        if (date == null || date.trim().isEmpty()) return null;
        try {
            LocalDate.parse(date.trim());
            return null;
        } catch (Exception e) {
            return fieldName + " debe tener formato YYYY-MM-DD";
        }
    }

    public static String validateDatePastMinAge(String date, int minAge, String fieldName) {
        String err = validateDate(date, fieldName);
        if (err != null) return err;
        LocalDate d = LocalDate.parse(date.trim());
        if (d.isAfter(LocalDate.now())) return fieldName + " no puede ser una fecha futura";
        if (Period.between(d, LocalDate.now()).getYears() < minAge)
            return "La edad m\u00ednima es de " + minAge + " a\u00f1os";
        return null;
    }

    public static String validateDatePastMinAgeOptional(String date, int minAge, String fieldName) {
        if (date == null || date.trim().isEmpty()) return null;
        String err = validateDate(date, fieldName);
        if (err != null) return err;
        LocalDate d = LocalDate.parse(date.trim());
        if (d.isAfter(LocalDate.now())) return fieldName + " no puede ser una fecha futura";
        if (Period.between(d, LocalDate.now()).getYears() < minAge)
            return "La edad m\u00ednima es de " + minAge + " a\u00f1os";
        return null;
    }

    public static String validateDateNotPast(String date, String fieldName) {
        String err = validateDate(date, fieldName);
        if (err != null) return err;
        LocalDate d = LocalDate.parse(date.trim());
        if (d.isBefore(LocalDate.now())) return fieldName + " no puede ser una fecha pasada";
        return null;
    }

    public static String validateDateNotFuture(String date, String fieldName) {
        if (date == null || date.trim().isEmpty()) return fieldName + " es obligatorio";
        try {
            LocalDate d = LocalDate.parse(date.trim());
            if (d.isAfter(LocalDate.now())) return fieldName + " no puede ser una fecha futura";
            return null;
        } catch (Exception e) {
            return fieldName + " debe tener formato YYYY-MM-DD";
        }
    }

    public static String validateTime(String time, String fieldName) {
        if (time == null || time.trim().isEmpty()) return fieldName + " es obligatorio";
        try {
            LocalTime.parse(time.trim());
            return null;
        } catch (Exception e) {
            return fieldName + " debe tener formato HH:MM";
        }
    }

    public static String validateTimeRange(String time, String fieldName) {
        String err = validateTime(time, fieldName);
        if (err != null) return err;
        LocalTime t = LocalTime.parse(time.trim());
        if (t.isBefore(LocalTime.of(6, 0)) || t.isAfter(LocalTime.of(21, 0)))
            return fieldName + " debe estar entre 06:00 y 21:00";
        return null;
    }

    public static String validatePositiveInteger(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) return fieldName + " es obligatorio";
        try {
            int n = Integer.parseInt(value.trim());
            if (n <= 0) return fieldName + " debe ser mayor a 0";
            return null;
        } catch (NumberFormatException e) {
            return fieldName + " debe ser un n\u00famero entero v\u00e1lido";
        }
    }

    public static String validatePositiveDouble(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) return fieldName + " es obligatorio";
        try {
            double n = Double.parseDouble(value.trim());
            if (n <= 0) return fieldName + " debe ser mayor a 0";
            return null;
        } catch (NumberFormatException e) {
            return fieldName + " debe ser un n\u00famero v\u00e1lido";
        }
    }

    public static String validateInRangeOptional(String value, double min, double max, String fieldName) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            double n = Double.parseDouble(value.trim());
            if (n < min || n > max) return fieldName + " debe estar entre " + min + " y " + max;
            return null;
        } catch (NumberFormatException e) {
            return fieldName + " debe ser un n\u00famero v\u00e1lido";
        }
    }
}
