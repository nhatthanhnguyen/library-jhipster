package com.thanh.library.service.mapper;

import com.thanh.library.domain.Book;
import com.thanh.library.domain.Queue;
import com.thanh.library.domain.User;
import com.thanh.library.service.dto.BookDTO;
import com.thanh.library.service.dto.QueueDTO;
import com.thanh.library.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Queue} and its DTO {@link QueueDTO}.
 */
@Mapper(componentModel = "spring")
public interface QueueMapper extends EntityMapper<QueueDTO, Queue> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "book", source = "book", qualifiedByName = "bookTitle")
    QueueDTO toDto(Queue s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("bookTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    BookDTO toDtoBookTitle(Book book);
}
