package io.eventdriven.slimdownaggregates.original.infrastructure.valueobjects;

public record Tuple<Key, Value>(Key key, Value value) {
}
