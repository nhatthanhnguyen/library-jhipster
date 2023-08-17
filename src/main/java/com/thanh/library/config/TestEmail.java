package com.thanh.library.config;

import com.thanh.library.domain.Book;
import com.thanh.library.domain.User;
import com.thanh.library.repository.BookRepository;
import com.thanh.library.repository.UserRepository;
import com.thanh.library.service.MailService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestEmail implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private final MailService mailService;

    public TestEmail(BookRepository bookRepository, UserRepository userRepository, MailService mailService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    public void run(String... args) throws Exception {
        //        User user = userRepository.findById(3L).get();
        //        Book book = bookRepository.findById(9L).get();
        //        mailService.sendReminderToReturnBook(user, book);
    }
}
