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
    @Mapping(target = "publisher", source = "publisher", qualifiedByName = "publisherName")
    @Mapping(target = "authors", source = "authors", qualifiedByName = "authorFirstNameSet")
    @Mapping(target = "categories", source = "categories", qualifiedByName = "categoryNameSet")
    BookDTO toDto(Book s);

    @Mapping(target = "removeAuthor", ignore = true)
    @Mapping(target = "removeCategory", ignore = true)
    Book toEntity(BookDTO bookDTO);

    @Named("publisherName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PublisherDTO toDtoPublisherName(Publisher publisher);

    @Named("authorFirstName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    AuthorDTO toDtoAuthorFirstName(Author author);

    @Named("authorFirstNameSet")
    default Set<AuthorDTO> toDtoAuthorFirstNameSet(Set<Author> author) {
        return author.stream().map(this::toDtoAuthorFirstName).collect(Collectors.toSet());
    }

    @Named("categoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDtoCategoryName(Category category);

    @Named("categoryNameSet")
    default Set<CategoryDTO> toDtoCategoryNameSet(Set<Category> category) {
        return category.stream().map(this::toDtoCategoryName).collect(Collectors.toSet());
    }
}
