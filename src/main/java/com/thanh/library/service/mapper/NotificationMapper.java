package com.thanh.library.service.mapper;

import com.thanh.library.domain.BookCopy;
import com.thanh.library.domain.Notification;
import com.thanh.library.domain.User;
import com.thanh.library.service.dto.BookCopyDTO;
import com.thanh.library.service.dto.NotificationDTO;
import com.thanh.library.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "bookCopy", source = "bookCopy", qualifiedByName = "bookCopyId")
    NotificationDTO toDto(Notification s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("bookCopyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookCopyDTO toDtoBookCopyId(BookCopy bookCopy);
}
