package com.facturacionAutonomo.auth_service.application.command;

public record RegisterUserCommand(String email, String password) {
}
