package io.eventdriven.slimdownaggregates.original.persistence.books.mappers;

import io.eventdriven.slimdownaggregates.original.domain.books.Book;
import io.eventdriven.slimdownaggregates.original.domain.books.BookEvent;
import io.eventdriven.slimdownaggregates.original.domain.books.entities.*;
import io.eventdriven.slimdownaggregates.original.domain.books.factories.BookFactory;
import io.eventdriven.slimdownaggregates.original.infrastructure.valueobjects.NonEmptyString;
import io.eventdriven.slimdownaggregates.original.infrastructure.valueobjects.NonNegativeInt;
import io.eventdriven.slimdownaggregates.original.infrastructure.valueobjects.PositiveInt;
import io.eventdriven.slimdownaggregates.original.persistence.authors.AuthorEntity;
import io.eventdriven.slimdownaggregates.original.persistence.books.BookEntity;
import io.eventdriven.slimdownaggregates.original.persistence.books.entities.ChapterEntity;
import io.eventdriven.slimdownaggregates.original.persistence.books.entities.FormatEntity;
import io.eventdriven.slimdownaggregates.original.persistence.books.valueobjects.CommitteeApprovalVO;
import io.eventdriven.slimdownaggregates.original.persistence.books.valueobjects.TranslationVO;
import io.eventdriven.slimdownaggregates.original.persistence.publishers.PublisherEntity;
import io.eventdriven.slimdownaggregates.original.persistence.reviewers.ReviewerEntity;
import jakarta.persistence.EntityManager;

import static io.eventdriven.slimdownaggregates.original.domain.books.BookEvent.*;

public class BookEntityMapper {

  public static Book mapToAggregate(BookEntity bookEntity, BookFactory bookFactory) {
    var id = new BookId(bookEntity.getId());
    var state = Book.State.valueOf(bookEntity.getCurrentState().name());
    var title = new Title(bookEntity.getTitle());
    var author = new Author(
      new AuthorId(bookEntity.getAuthor().getId()),
      new AuthorFirstName(bookEntity.getAuthor().getFirstName()),
      new AuthorLastName(bookEntity.getAuthor().getLastName())
    );
    var publisher = new Publisher(
      new PublisherId(bookEntity.getPublisher().getId()),
      new PublisherName(bookEntity.getPublisher().getName())
    );
    var edition = new PositiveInt(bookEntity.getEdition());
    var genre = bookEntity.getGenre() != null ? new Genre(bookEntity.getGenre()) : null;
    var isbn = bookEntity.getIsbn() != null ? new ISBN(bookEntity.getIsbn()) : null;
    var publicationDate = bookEntity.getPublicationDate();
    var totalPages = bookEntity.getTotalPages() != null ? new PositiveInt(bookEntity.getTotalPages()) : null;
    var numberOfIllustrations = bookEntity.getNumberOfIllustrations() != null ? new PositiveInt(bookEntity.getNumberOfIllustrations()) : null;
    var bindingType = bookEntity.getBindingType() != null ? new NonEmptyString(bookEntity.getBindingType()) : null;
    var summary = bookEntity.getSummary() != null ? new NonEmptyString(bookEntity.getSummary()) : null;
    var committeeApproval = bookEntity.getCommitteeApproval() != null
      ? new CommitteeApproval(
      bookEntity.getCommitteeApproval().isApproved(),
      new NonEmptyString(bookEntity.getCommitteeApproval().getFeedback())
    ) : null;
    var reviewers = bookEntity.getReviewers().stream()
      .map(r -> new Reviewer(new ReviewerId(r.getId()), new ReviewerName(r.getName())))
      .toList();
    var chapters = bookEntity.getChapters().stream()
      .map(c -> new Chapter(
        new ChapterNumber(c.getNumber()),
        new ChapterTitle(c.getTitle()),
        new ChapterContent(c.getContent(), new NonNegativeInt(0), new NonNegativeInt(0))))
      .toList();
    var translations = bookEntity.getTranslations().stream()
      .map(c -> new Translation(
        new Language(new LanguageId(c.getLanguageId()), new LanguageName(c.getLanguage().getName())),
        new Translator(new TranslatorId(c.getTranslatorId()), new TranslatorName(c.getTranslator().getName()))
      ))
      .toList();
    var formats = bookEntity.getFormats().stream()
      .map(c -> new Format(
        new FormatType(c.getFormatType()),
        new PositiveInt(c.getTotalCopies()),
        new PositiveInt(c.getSoldCopies())
      ))
      .toList();

    return bookFactory.create(
      id,
      state,
      title,
      author,
      null, // TODO: Change that to something better
      publisher,
      edition,
      genre,
      isbn,
      publicationDate,
      totalPages,
      numberOfIllustrations,
      bindingType,
      summary,
      committeeApproval,
      reviewers,
      chapters,
      translations,
      formats
    );
  }

  public static BookEntity mapToEntity(BookEvent bookEvent, BookEntity entity, EntityManager em) {
    switch (bookEvent) {
      case WritingStarted writingStarted -> {
        entity.setId(writingStarted.bookId().value());
        entity.setCurrentState(BookEntity.State.Writing);

        entity.setEdition(writingStarted.edition().value());
        entity.setTitle(writingStarted.title().value());
        entity.setGenre(writingStarted.genre() != null ? writingStarted.genre().value() : null);

        var authorEntity = em.find(AuthorEntity.class, writingStarted.author().id().value());
        entity.setAuthor(authorEntity);

        var publisherEntity = em.find(PublisherEntity.class, writingStarted.publisher().id().value());
        entity.setPublisher(publisherEntity);
      }
      case ChapterAdded chapterAdded -> {
        var chapter = chapterAdded.chapter();

        em.persist(
          new ChapterEntity(
            chapterAdded.bookId().value(),
            chapter.chapterNumber().value(),
            chapter.title().value(),
            chapter.content().value())
        );
      }
      case MovedToEditing ignore -> {
        entity.setCurrentState(BookEntity.State.Editing);
      }
      case FormatAdded formatAdded -> {
        var format = formatAdded.format();

        em.persist(
          new FormatEntity(
            formatAdded.bookId().value(),
            format.formatType().value(),
            format.totalCopies().value(),
            format.soldCopies().value())
        );
      }
      case FormatRemoved formatRemoved -> {
        var format = entity.getFormats().stream()
          .filter(f -> f.getFormatType().equals(formatRemoved.format().formatType().value()))
          .findFirst();

        em.remove(format);
      }
      case TranslationAdded translationAdded -> {
        var translation = translationAdded.translation();

        em.persist(
          new TranslationVO(
            translation.language().id().value(),
            translation.translator().id().value()
          )
        );
      }
      case ReviewerAdded reviewerAdded -> {
        var reviewer = reviewerAdded.reviewer();

        em.persist(
          new ReviewerEntity(
            reviewerAdded.bookId().value(),
            reviewer.name().value()
          )
        );
      }
      case ReviewerRemoved reviewerRemoved -> {
        var format = entity.getReviewers().stream()
          .filter(f -> f.getId().equals(reviewerRemoved.reviewer().id().value()))
          .findFirst();

        em.remove(format);
      }
      case IsbnSet isbnSet -> {
        entity.setIsbn(isbnSet.isbn().value());
      }
      case Approved approved -> {
        entity.setCommitteeApproval(new CommitteeApprovalVO(
          approved.committeeApproval().isApproved(),
          approved.committeeApproval().feedback().value()
        ));
      }
      case MovedToPrinting movedToPrinting -> {
        entity.setCurrentState(BookEntity.State.Printing);
        entity.setTotalPages(movedToPrinting.totalPages().value());
        entity.setNumberOfIllustrations(movedToPrinting.numberOfIllustrations().value());
        entity.setBindingType(movedToPrinting.bindingType().value());
        entity.setSummary(movedToPrinting.summary().value());
      }
      case Published published -> {
        entity.setCurrentState(BookEntity.State.Published);
        entity.setPublicationDate(published.publishedAt());
      }
      case MovedToOutOfPrint ignore -> {
        entity.setCurrentState(BookEntity.State.OutOfPrint);
      }
    }
    return entity;
  }
}
