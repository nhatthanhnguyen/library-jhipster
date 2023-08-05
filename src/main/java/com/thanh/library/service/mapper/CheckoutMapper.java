package com.thanh.library.service.mapper;

import com.thanh.library.domain.BookCopy;
import com.thanh.library.domain.Checkout;
import com.thanh.library.domain.User;
import com.thanh.library.service.dto.BookCopyDTO;
import com.thanh.library.service.dto.CheckoutDTO;
import com.thanh.library.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Checkout} and its DTO {@link CheckoutDTO}.
 */
@Mapper(componentModel = "spring")
public interface CheckoutMapper extends EntityMapper<CheckoutDTO, Checkout> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "bookCopy", source = "bookCopy", qualifiedByName = "bookCopyId")
    CheckoutDTO toDto(Checkout s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    UserDTO toDtoUserLogin(User user);

    @Named("bookCopyId")
    BookCopyDTO toDtoBookCopyId(BookCopy bookCopy);
}
