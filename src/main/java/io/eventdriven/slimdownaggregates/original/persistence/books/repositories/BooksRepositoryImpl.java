package io.eventdriven.slimdownaggregates.original.persistence.books.repositories;

import io.eventdriven.slimdownaggregates.original.domain.books.Book;
import io.eventdriven.slimdownaggregates.original.domain.books.BookEvent;
import io.eventdriven.slimdownaggregates.original.domain.books.entities.BookId;
import io.eventdriven.slimdownaggregates.original.domain.books.factories.BookFactory;
import io.eventdriven.slimdownaggregates.original.domain.books.repositories.BooksRepository;
import io.eventdriven.slimdownaggregates.original.persistence.books.BookEntity;
import io.eventdriven.slimdownaggregates.original.persistence.books.mappers.BookEntityMapper;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;

import java.util.function.Function;

@Repository
public class BooksRepositoryImpl implements BooksRepository {

  @PersistenceContext
  private EntityManager entityManager;
  private BooksJpaRepository entityRepository;

  private final BookFactory bookFactory;

  @Autowired
  public BooksRepositoryImpl(BooksJpaRepository entityRepository, BookFactory bookFactory) {
      this.bookFactory = bookFactory;
      this.entityRepository = entityRepository;
  }

  @Override
  public void getAndUpdate(BookId bookId, Function<Book, BookEvent[]> decide) {
    var entity = entityRepository.findById(bookId.value()).orElse(new BookEntity());

    var events = decide.apply(BookEntityMapper.mapToAggregate(entity, bookFactory));

    for(BookEvent event : events) {
      entity = BookEntityMapper.mapToEntity(event, entity, entityManager);
    }

    entityRepository.save(entity);
  }
}

