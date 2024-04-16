package io.eventdriven.slimdownaggregates.original.domain.books.entities;

import io.eventdriven.slimdownaggregates.original.infrastructure.valueobjects.NonNegativeInt;

import static io.eventdriven.slimdownaggregates.original.infrastructure.validation.Validation.assertNotNull;

public record ChapterContent(String value, NonNegativeInt totalPages,
                             NonNegativeInt numberOfIllustrations) {
  public ChapterContent(String value) {
    this(value, calculateTotalPages(value), calculateNumberOfIllustrations(value));
  }

  public ChapterContent {
    assertNotNull(value);
  }

  public static final ChapterContent empty = new ChapterContent("", new NonNegativeInt(0), new NonNegativeInt(0));

  private static NonNegativeInt calculateTotalPages(String content) {
    return new NonNegativeInt((int) Math.random() * 1000); // here we'd have some traversal through content to calculate the total number of pages
  }

  private static NonNegativeInt calculateNumberOfIllustrations(String content) {
    return new NonNegativeInt((int) Math.random() * 100); // here we'd have some traversal through content to calculate the number of images
  }
}
