package com.roshik.command;

public interface ICommandValidator {
    ValidationResult validateMessage(String message, Long chatId);
}
