package io.eventdriven.slimdownaggregates.original.domain.books.repositories;

import io.eventdriven.slimdownaggregates.original.domain.books.Book;
import io.eventdriven.slimdownaggregates.original.domain.books.BookEvent;
import io.eventdriven.slimdownaggregates.original.domain.books.entities.BookId;
import io.eventdriven.slimdownaggregates.original.persistence.books.BookEntity;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public interface BooksRepository {
  void getAndUpdate(BookId bookId, Function<Book, BookEvent[]> decide);
}
