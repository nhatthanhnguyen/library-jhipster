package com.thanh.library.service.mapper;

import com.thanh.library.domain.Book;
import com.thanh.library.domain.BookCopy;
import com.thanh.library.domain.Publisher;
import com.thanh.library.service.dto.BookCopyDTO;
import com.thanh.library.service.dto.BookDTO;
import com.thanh.library.service.dto.PublisherDTO;
import jdk.jfr.Name;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookCopy} and its DTO {@link BookCopyDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookCopyMapper extends EntityMapper<BookCopyDTO, BookCopy> {
    @Mapping(target = "book", source = "book", qualifiedByName = "bookTitle")
    BookCopyDTO toDto(BookCopy s);

    @Named("bookTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "publisher", source = "publisher")
    BookDTO toDtoBookTitle(Book book);
}
