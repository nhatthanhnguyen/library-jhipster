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
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "bookCopy", source = "bookCopy", qualifiedByName = "bookCopyId")
    NotificationDTO toDto(Notification s);

    @Named("userLogin")
    UserDTO toDtoUserLogin(User user);

    @Named("bookCopyId")
    BookCopyDTO toDtoBookCopyId(BookCopy bookCopy);
}
