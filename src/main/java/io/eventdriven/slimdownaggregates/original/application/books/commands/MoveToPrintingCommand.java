package io.eventdriven.slimdownaggregates.original.application.books.commands;

import io.eventdriven.slimdownaggregates.original.domain.books.entities.*;
import io.eventdriven.slimdownaggregates.original.infrastructure.valueobjects.NonEmptyString;

public record MoveToPrintingCommand(
  BookId bookId,
  NonEmptyString bindingType,
  NonEmptyString summary) {
}
