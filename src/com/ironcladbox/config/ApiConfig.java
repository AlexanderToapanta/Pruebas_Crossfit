package com.ironcladbox.config;

public class ApiConfig {
    public static final String BASE_URL = "http://10.40.22.167:3000";

    public static final String AUTH_LOGIN = BASE_URL + "/api/auth/login";
    public static final String AUTH_REGISTER = BASE_URL + "/api/auth/register";
    public static final String AUTH_VERIFY = BASE_URL + "/api/auth/verify";
    public static final String AUTH_PROFILE = BASE_URL + "/api/auth/profile";
    public static final String AUTH_LOGOUT = BASE_URL + "/api/auth/logout";
    public static final String AUTH_CHANGE_PASSWORD = BASE_URL + "/api/auth/change-password";
    public static final String AUTH_UPLOAD_PROFILE_IMAGE = BASE_URL + "/api/auth/upload-profile-image";
    public static final String AUTH_MEMBERSHIPS = BASE_URL + "/api/auth/memberships";

    public static final String MEMBERS_MEMBERSHIPS = BASE_URL + "/api/members/memberships";
    public static final String MEMBERS_CHECK = BASE_URL + "/api/members/check-membership";
    public static final String MEMBERS_MY_MEMBERSHIP = BASE_URL + "/api/members/my-membership";

    public static final String ADMIN_MEMBERSHIPS = BASE_URL + "/api/admin/memberships";
    public static final String ADMIN_TRAINERS = BASE_URL + "/api/admin/trainers";
    public static final String ADMIN_CLASSES = BASE_URL + "/api/admin/classes";
    public static final String ADMIN_ATHLETES = BASE_URL + "/api/admin/athletes";
    public static final String ADMIN_STATS = BASE_URL + "/api/admin/stats";
    public static final String ADMIN_ASSIGN_MEMBERSHIP = BASE_URL + "/api/admin/memberships/assign";

    public static final String WOD_CALENDAR = BASE_URL + "/api/wod/calendar";
    public static final String WOD = BASE_URL + "/api/wod";
    public static final String WOD_MY_SCHEDULES = BASE_URL + "/api/wod/my-schedules";
    public static final String WOD_RACHA = BASE_URL + "/api/wod/racha";
    public static final String WOD_ASISTENCIA = BASE_URL + "/api/wod/asistencia";

    public static final String CLASSES = BASE_URL + "/api/classes";
    public static final String CLASSES_AVAILABLE = BASE_URL + "/api/classes/available";
    public static final String CLASSES_MY = BASE_URL + "/api/classes/my-classes";
    public static final String CLASSES_ENROLL = BASE_URL + "/api/classes/enroll";

    public static final String TRAINERS = BASE_URL + "/api/trainers";
    public static final String TRAINERS_MY_CLASSES = BASE_URL + "/api/trainers/my-classes";
    public static final String TRAINERS_MY_WODS = BASE_URL + "/api/trainers/my-wods";
    public static final String TRAINERS_MY_ATHLETES = BASE_URL + "/api/trainers/my-athletes";

    public static final String EJERCICIOS = BASE_URL + "/api/ejercicios";
    public static final String PROGRESO_EJERCICIOS = BASE_URL + "/api/progreso/ejercicios";
    public static final String PROGRESO_MARCA = BASE_URL + "/api/progreso/marca";
    public static final String PROGRESO_ESTADISTICAS = BASE_URL + "/api/progreso/estadisticas";

    public static final String CONTACT = BASE_URL + "/api/contact";

    public static final int TIMEOUT_SECONDS = 30;
}
