package com.crimsonlogic.open_petal_backend.common.message.constants;

public final class MessageCodes {

    private MessageCodes() {
    }

    public static final String COMMON_ERROR = "COMMON_ERROR";
    public static final String COMMON_VALIDATION_FAILED = "COMMON_VALIDATION_FAILED";
    public static final String COMMON_BAD_REQUEST = "COMMON_BAD_REQUEST";
    public static final String COMMON_METHOD_NOT_ALLOWED = "COMMON_METHOD_NOT_ALLOWED";
    public static final String COMMON_DATABASE_CONFLICT = "COMMON_DATABASE_CONFLICT";
    public static final String COMMON_BUSINESS_ERROR = "COMMON_BUSINESS_ERROR";
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String DUPLICATE_RESOURCE = "DUPLICATE_RESOURCE";
    public static final String AUTH_INVALID_CREDENTIALS = "AUTH_INVALID_CREDENTIALS";
    public static final String AUTHENTICATION_REQUIRED = "AUTHENTICATION_REQUIRED";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";

    public static final String ADMIN_CONFLICTS_RETRIEVED = "ADMIN_CONFLICTS_RETRIEVED";
    public static final String ADMIN_CONFLICT_DETAILS_RETRIEVED = "ADMIN_CONFLICT_DETAILS_RETRIEVED";
    public static final String ADMIN_CONFLICT_RESOLVED = "ADMIN_CONFLICT_RESOLVED";
    public static final String ADMIN_DASHBOARD_WELCOME = "ADMIN_DASHBOARD_WELCOME";
    public static final String ADMIN_USERS_RETRIEVED = "ADMIN_USERS_RETRIEVED";
    public static final String ADMIN_USER_RETRIEVED = "ADMIN_USER_RETRIEVED";
    public static final String ADMIN_USER_DETAILS_RETRIEVED = "ADMIN_USER_DETAILS_RETRIEVED";
    public static final String ADMIN_SKILL_CATEGORY_CREATED = "ADMIN_SKILL_CATEGORY_CREATED";
    public static final String ADMIN_SKILLS_RETRIEVED = "ADMIN_SKILLS_RETRIEVED";
    public static final String ADMIN_SKILL_DETAILS_RETRIEVED = "ADMIN_SKILL_DETAILS_RETRIEVED";
    public static final String ADMIN_SKILL_ADDED = "ADMIN_SKILL_ADDED";
    public static final String ADMIN_USER_BLOCKED = "ADMIN_USER_BLOCKED";
    public static final String ADMIN_USER_UNBLOCKED = "ADMIN_USER_UNBLOCKED";
    public static final String ADMIN_QUERIES_RETRIEVED = "ADMIN_QUERIES_RETRIEVED";
    public static final String ADMIN_QUERY_REPLIED = "ADMIN_QUERY_REPLIED";
    public static final String ADMIN_SESSIONS_RETRIEVED = "ADMIN_SESSIONS_RETRIEVED";
    public static final String ADMIN_SESSION_DETAILS_RETRIEVED = "ADMIN_SESSION_DETAILS_RETRIEVED";
    public static final String ADMIN_SESSION_CANCELLED = "ADMIN_SESSION_CANCELLED";

    public static final String AUTH_REGISTER_SUCCESS = "AUTH_REGISTER_SUCCESS";
    public static final String AUTH_LOGIN_SUCCESS = "AUTH_LOGIN_SUCCESS";
    public static final String AUTH_EMAIL_VERIFIED = "AUTH_EMAIL_VERIFIED";
    public static final String AUTH_PASSWORD_RESET_EMAIL_SENT = "AUTH_PASSWORD_RESET_EMAIL_SENT";
    public static final String AUTH_PASSWORD_RESET = "AUTH_PASSWORD_RESET";

    public static final String CHAT_CONVERSATIONS_RETRIEVED = "CHAT_CONVERSATIONS_RETRIEVED";
    public static final String CHAT_MESSAGES_RETRIEVED = "CHAT_MESSAGES_RETRIEVED";
    public static final String CHAT_MESSAGE_SENT = "CHAT_MESSAGE_SENT";
    public static final String CHAT_MESSAGES_MARKED_READ = "CHAT_MESSAGES_MARKED_READ";
    public static final String CHAT_UNREAD_COUNT_RETRIEVED = "CHAT_UNREAD_COUNT_RETRIEVED";
    public static final String CONNECTION_REQUEST_SENT = "CONNECTION_REQUEST_SENT";
    public static final String CONNECTION_REQUEST_ACCEPTED = "CONNECTION_REQUEST_ACCEPTED";

    public static final String QUERY_SUBMITTED = "QUERY_SUBMITTED";
    public static final String QUERY_RETRIEVED = "QUERY_RETRIEVED";
    public static final String SEARCH_RESULTS_RETRIEVED = "SEARCH_RESULTS_RETRIEVED";
    public static final String SEARCH_USER_DETAILS_RETRIEVED = "SEARCH_USER_DETAILS_RETRIEVED";

    public static final String USER_CONFLICTS_RETRIEVED = "USER_CONFLICTS_RETRIEVED";
    public static final String SESSION_CREATED = "SESSION_CREATED";
    public static final String SESSIONS_RETRIEVED = "SESSIONS_RETRIEVED";
    public static final String SESSION_DETAILS_RETRIEVED = "SESSION_DETAILS_RETRIEVED";
    public static final String SESSION_CANCELLED = "SESSION_CANCELLED";
    public static final String SESSION_ACCEPTED = "SESSION_ACCEPTED";
    public static final String SESSION_DECLINED = "SESSION_DECLINED";
    public static final String SESSION_OTP_RETRIEVED = "SESSION_OTP_RETRIEVED";
    public static final String SESSION_START_VERIFIED = "SESSION_START_VERIFIED";
    public static final String SESSION_CONFLICT_DETAILS_RETRIEVED = "SESSION_CONFLICT_DETAILS_RETRIEVED";
    public static final String SESSION_CONFLICT_STORY_SUBMITTED = "SESSION_CONFLICT_STORY_SUBMITTED";
    public static final String SESSION_END_VERIFIED = "SESSION_END_VERIFIED";
    public static final String SESSION_CONFLICT_RAISED = "SESSION_CONFLICT_RAISED";

    public static final String LEARNING_GOAL_CREATED = "LEARNING_GOAL_CREATED";
    public static final String LEARNING_GOAL_RETRIEVED = "LEARNING_GOAL_RETRIEVED";
    public static final String LEARNING_GOALS_RETRIEVED = "LEARNING_GOALS_RETRIEVED";
    public static final String LEARNING_GOAL_UPDATED = "LEARNING_GOAL_UPDATED";
    public static final String LEARNING_GOAL_DELETED = "LEARNING_GOAL_DELETED";
    public static final String LEARNING_GOAL_ROADPLAN_UPDATED = "LEARNING_GOAL_ROADPLAN_UPDATED";
    public static final String SKILL_CATEGORIES_RETRIEVED = "SKILL_CATEGORIES_RETRIEVED";
    public static final String SKILL_CATEGORY_CREATED = "SKILL_CATEGORY_CREATED";
    public static final String SKILL_CATEGORY_RETRIEVED = "SKILL_CATEGORY_RETRIEVED";
    public static final String SKILL_CATEGORY_UPDATED = "SKILL_CATEGORY_UPDATED";
    public static final String SKILLS_RETRIEVED = "SKILLS_RETRIEVED";

    public static final String USER_PROFILE_RETRIEVED = "USER_PROFILE_RETRIEVED";
    public static final String USER_PASSWORD_CHANGED = "USER_PASSWORD_CHANGED";
    public static final String USER_PROFILE_UPDATED = "USER_PROFILE_UPDATED";
    public static final String USER_STATUS_UPDATED = "USER_STATUS_UPDATED";
    public static final String USER_AVAILABILITY_RETRIEVED = "USER_AVAILABILITY_RETRIEVED";
    public static final String USER_AVAILABILITY_ADDED = "USER_AVAILABILITY_ADDED";
    public static final String USER_AVAILABILITY_REMOVED = "USER_AVAILABILITY_REMOVED";
    public static final String USER_QUERIES_RETRIEVED = "USER_QUERIES_RETRIEVED";
    public static final String USER_SKILLS_RETRIEVED = "USER_SKILLS_RETRIEVED";
    public static final String USER_LEARNING_GOALS_RETRIEVED = "USER_LEARNING_GOALS_RETRIEVED";
    public static final String USER_TEACHING_SKILL_ADDED = "USER_TEACHING_SKILL_ADDED";
    public static final String USER_LEARNING_GOAL_ADDED = "USER_LEARNING_GOAL_ADDED";
    public static final String USER_TEACHING_SKILL_DELETED = "USER_TEACHING_SKILL_DELETED";
    public static final String USER_LEARNING_GOAL_DELETED = "USER_LEARNING_GOAL_DELETED";

    public static final String LOCATION_CREATED = "LOCATION_CREATED";
    public static final String LOCATIONS_RETRIEVED = "LOCATIONS_RETRIEVED";
    public static final String LOCATION_RETRIEVED = "LOCATION_RETRIEVED";
}
