package com.pricecomparatormarket.util.notification;

import com.pricecomparatormarket.exception.MailDeliveryException;
import com.pricecomparatormarket.model.PriceAlertTrigger;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationAdapter implements NotificationPort {

  private final JavaMailSender mail;

  @Override
  public void notifyHit(String to, PriceAlertTrigger trig) {
    SimpleMailMessage m = new SimpleMailMessage();
    m.setTo(to);
    m.setSubject("🎯 Price alert hit – " + trig.getAlert().getProduct().getProductName());
    m.setText(
        """
                  Good news!

                  %s at %s now costs %s RON (target was %s).
                  """
            .formatted(
                trig.getAlert().getProduct().getProductName(),
                trig.getStoreName(),
                trig.getHitPrice(),
                trig.getAlert().getTargetPrice()));
    try {
      mail.send(m);
    } catch (Exception ex) {
      throw new MailDeliveryException("Could not send e-mail", ex);
    }
  }
}
