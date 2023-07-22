package com.thanh.library.service.mapper;

import com.thanh.library.domain.Author;
import com.thanh.library.domain.Book;
import com.thanh.library.domain.Category;
import com.thanh.library.domain.Publisher;
import com.thanh.library.service.dto.AuthorDTO;
import com.thanh.library.service.dto.BookDTO;
import com.thanh.library.service.dto.CategoryDTO;
import com.thanh.library.service.dto.PublisherDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Mapping(target = "authors", source = "authors", qualifiedByName = "authorIdSet")
    @Mapping(target = "categories", source = "categories", qualifiedByName = "categoryIdSet")
    @Mapping(target = "publisher", source = "publisher", qualifiedByName = "publisherName")
    BookDTO toDto(Book s);

    @Mapping(target = "removeAuthor", ignore = true)
    @Mapping(target = "removeCategory", ignore = true)
    Book toEntity(BookDTO bookDTO);

    @Named("authorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AuthorDTO toDtoAuthorId(Author author);

    @Named("authorIdSet")
    default Set<AuthorDTO> toDtoAuthorIdSet(Set<Author> author) {
        return author.stream().map(this::toDtoAuthorId).collect(Collectors.toSet());
    }

    @Named("categoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoryDTO toDtoCategoryId(Category category);

    @Named("categoryIdSet")
    default Set<CategoryDTO> toDtoCategoryIdSet(Set<Category> category) {
        return category.stream().map(this::toDtoCategoryId).collect(Collectors.toSet());
    }

    @Named("publisherName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PublisherDTO toDtoPublisherName(Publisher publisher);
}
