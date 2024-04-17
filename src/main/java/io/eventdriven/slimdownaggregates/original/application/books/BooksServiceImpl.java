package io.eventdriven.slimdownaggregates.original.application.books;

import io.eventdriven.slimdownaggregates.original.application.books.commands.*;
import io.eventdriven.slimdownaggregates.original.domain.books.Book;
import io.eventdriven.slimdownaggregates.original.domain.books.BookEvent;
import io.eventdriven.slimdownaggregates.original.domain.books.authors.AuthorProvider;
import io.eventdriven.slimdownaggregates.original.domain.books.entities.BookId;
import io.eventdriven.slimdownaggregates.original.domain.books.publishers.PublisherProvider;
import io.eventdriven.slimdownaggregates.original.domain.books.repositories.BooksRepository;
import io.eventdriven.slimdownaggregates.original.domain.books.services.PublishingHouse;

import java.time.LocalDate;
import java.util.function.Function;

public class BooksServiceImpl implements BooksService {
  @Override
  public void createDraft(CreateDraftCommand command) {
    getAndUpdate(
      command.bookId(),
      (ignore) ->
        Book.createDraft(
          command.bookId(),
          command.title(),
          authorProvider.getOrCreate(command.author()),
          publisherProvider.getById(command.publisherId()),
          command.edition(),
          command.genre()
        )
    );
  }

  @Override
  public void addChapter(AddChapterCommand command) {
    getAndUpdate(
      command.bookId(),
      book -> book.addChapter(command.title(), command.content())
    );
  }

  @Override
  public void moveToEditing(MoveToEditingCommand command) {
    getAndUpdate(
      command.bookId(),
      book -> book.moveToEditing()
    );
  }

  @Override
  public void addTranslation(AddTranslationCommand command) {
    getAndUpdate(
      command.bookId(),
      book -> book.addTranslation(command.translation())
    );
  }

  @Override
  public void addFormat(AddFormatCommand command) {
    getAndUpdate(
      command.bookId(),
      book -> book.addFormat(command.format())
    );
  }

  @Override
  public void removeFormat(RemoveFormatCommand command) {
    getAndUpdate(
      command.bookId(),
      book -> book.removeFormat(command.format())
    );
  }

  @Override
  public void addReviewer(AddReviewerCommand command) {
    getAndUpdate(
      command.bookId(),
      book -> book.addReviewer(command.reviewer())
    );
  }

  @Override
  public void approve(ApproveCommand command) {
    getAndUpdate(
      command.bookId(),
      book -> book.approve(command.committeeApproval())
    );
  }

  @Override
  public void setISBN(SetISBNCommand command) {
    getAndUpdate(
      command.bookId(),
      book -> book.setISBN(command.isbn())
    );
  }

  @Override
  public void moveToPublished(MoveToPublishedCommand command) {
    getAndUpdate(
      command.bookId(),
      book -> book.moveToPublished(LocalDate.now())
    );
  }

  @Override
  public void moveToPrinting(MoveToPrintingCommand command) {
    getAndUpdate(
      command.bookId(),
      book -> book.moveToPrinting(command.bindingType(), command.summary())
    );
  }

  @Override
  public void moveToOutOfPrint(MoveToOutOfPrintCommand command) {
    getAndUpdate(
      command.bookId(),
      book -> book.moveToOutOfPrint()
    );
  }

  private void getAndUpdate(BookId bookId, Function<Book, BookEvent> decide) {
    repository.getAndUpdate(
      bookId, book -> new BookEvent[]{
        decide.apply(book)
      }
    );
  }


  public BooksServiceImpl(
    BooksRepository repository,
    AuthorProvider authorProvider,
    PublisherProvider publisherProvider,
    PublishingHouse publishingHouse
  ) {
    this.repository = repository;
    this.authorProvider = authorProvider;
    this.publisherProvider = publisherProvider;
    this.publishingHouse = publishingHouse;
  }

  private final BooksRepository repository;
  private final AuthorProvider authorProvider;
  private final PublisherProvider publisherProvider;
  private final PublishingHouse publishingHouse;
}
