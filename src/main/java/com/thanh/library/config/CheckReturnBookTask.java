package com.thanh.library.config;

import com.thanh.library.domain.Checkout;
import com.thanh.library.domain.User;
import com.thanh.library.repository.CheckoutRepository;
import com.thanh.library.repository.NotificationRepository;
import com.thanh.library.repository.UserRepository;
import com.thanh.library.service.MailService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CheckReturnBookTask {

    private final UserRepository userRepository;

    private final CheckoutRepository checkoutRepository;

    private final NotificationRepository notificationRepository;

    private final MailService mailService;

    public CheckReturnBookTask(
        UserRepository userRepository,
        CheckoutRepository checkoutRepository,
        NotificationRepository notificationRepository,
        MailService mailService
    ) {
        this.userRepository = userRepository;
        this.checkoutRepository = checkoutRepository;
        this.notificationRepository = notificationRepository;
        this.mailService = mailService;
    }

    @Async
    @Scheduled(cron = "0 * * * * *")
    public void runTask() {
        List<Checkout> checkouts = checkoutRepository.findCheckoutsThatNotReturned();
        for (Checkout checkout : checkouts) {
            Instant currentTime = Instant.now();
            long minutesDifference = ChronoUnit.MINUTES.between(checkout.getStartTime(), currentTime);
            if (minutesDifference >= 5 && minutesDifference <= 10 && checkout.getUser().isActivated()) {
                mailService.sendReminderToReturnBook(checkout.getUser(), checkout.getBookCopy().getBook());
            }
            if (minutesDifference > 15 && checkout.getUser().isActivated()) {
                User user = checkout.getUser();
                user.setActivated(false);
                userRepository.save(user);
            }
        }
    }
}
