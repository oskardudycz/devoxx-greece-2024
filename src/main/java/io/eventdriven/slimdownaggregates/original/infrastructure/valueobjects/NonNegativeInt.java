package io.eventdriven.slimdownaggregates.original.infrastructure.valueobjects;

import static io.eventdriven.slimdownaggregates.original.infrastructure.validation.Validation.assertNonNegative;

public record NonNegativeInt(int value) {
  public NonNegativeInt {
    assertNonNegative(value);
  }
}
