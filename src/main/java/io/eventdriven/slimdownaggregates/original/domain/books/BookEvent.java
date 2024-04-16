package io.eventdriven.slimdownaggregates.original.domain.books;

import io.eventdriven.slimdownaggregates.original.domain.books.entities.*;
import io.eventdriven.slimdownaggregates.original.infrastructure.events.DomainEvent;
import io.eventdriven.slimdownaggregates.original.infrastructure.valueobjects.NonEmptyString;
import io.eventdriven.slimdownaggregates.original.infrastructure.valueobjects.NonNegativeInt;
import io.eventdriven.slimdownaggregates.original.infrastructure.valueobjects.PositiveInt;

import java.time.LocalDate;

public sealed interface BookEvent extends DomainEvent {
  record WritingStarted(
    BookId bookId,
    Genre genre,
    Title title,
    Author author,
    io.eventdriven.slimdownaggregates.original.infrastructure.valueobjects.PositiveInt edition,
    Publisher publisher
  ) implements BookEvent {
  }

  record ChapterAdded(
    BookId bookId,
    Chapter chapter
  ) implements BookEvent {
  }

  record MovedToEditing(
    BookId bookId
  ) implements BookEvent {
  }

  record FormatAdded(
    BookId bookId,
    Format format
  ) implements BookEvent {
  }

  record FormatRemoved(
    BookId bookId,
    Format format
  ) implements BookEvent {
  }

  record TranslationAdded(
    BookId bookId,
    Translation translation
  ) implements BookEvent {
  }

  record ReviewerAdded(
    BookId bookId,
    Reviewer reviewer
  ) implements BookEvent {
  }

  record ReviewerRemoved(
    BookId bookId,
    Reviewer reviewer
  ) implements BookEvent {
  }

  record Approved(
    BookId bookId,
    CommitteeApproval committeeApproval
  ) implements BookEvent {
  }

  record IsbnSet(
    BookId bookId,
    ISBN isbn
  ) implements BookEvent {
  }

  record MovedToPrinting(
    BookId bookId,
    PositiveInt totalPages,
    NonNegativeInt numberOfIllustrations,
    NonEmptyString bindingType,
    NonEmptyString summary
  ) implements BookEvent {
  }

  record Published(
    BookId bookId,
    ISBN isbn,
    Title title,
    Author author,
    LocalDate publishedAt
  ) implements BookEvent {
  }

  record MovedToOutOfPrint(
    BookId bookId
  ) implements BookEvent {
  }
}
