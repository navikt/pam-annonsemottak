package no.nav.pam.annonsemottak.receivers;


import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;


public class GenericDateParserTest {

    @Test
    public void shouldParse() {
        LocalDateTime compareDate = LocalDateTime.of(2017, 7, 19, 0, 0);

        LocalDateTime date1 = GenericDateParser.parse("19.07.2017").orElse(null);
        LocalDateTime date2 = GenericDateParser.parse("19.7.17").orElse(null);
        LocalDateTime date3 = GenericDateParser.parse("19/07/2017").orElse(null);
        LocalDateTime date4 = GenericDateParser.parse("19/7/17").orElse(null);
        LocalDateTime date5 = GenericDateParser.parse("19/7-2017").orElse(null);
        LocalDateTime date6 = GenericDateParser.parse("19/7-17").orElse(null);
        LocalDateTime date7 = GenericDateParser.parse("2017-07-19").orElse(null);
        LocalDateTime date11 = GenericDateParser.parse("19. juli 2017").orElse(null);
        LocalDateTime date12 = GenericDateParser.parse("19. jul 2017").orElse(null);
        LocalDateTime date13 = GenericDateParser.parse("19. jul 17").orElse(null);
        LocalDateTime date14 = GenericDateParser.parse("19. juli 17").orElse(null);
        LocalDateTime date15 = GenericDateParser.parse("19. mai 2017").orElse(null);
        LocalDateTime date16 = GenericDateParser.parse("19. 7").orElse(null);
        LocalDateTime date17 = GenericDateParser.parse("19. jul").orElse(null);
        LocalDateTime date18 = GenericDateParser.parse("19. juli").orElse(null);
        LocalDateTime date19 = GenericDateParser.parse("1 september").orElse(null);

        Assert.assertEquals(compareDate, date1);
        Assert.assertEquals(compareDate, date2);
        Assert.assertEquals(compareDate, date3);
        Assert.assertEquals(compareDate, date4);
        Assert.assertEquals(compareDate, date5);
        Assert.assertEquals(compareDate, date6);
        Assert.assertEquals(compareDate, date7);
        Assert.assertEquals(compareDate, date11);
        Assert.assertEquals(compareDate, date12);
        Assert.assertEquals(compareDate, date13);
        Assert.assertEquals(compareDate, date14);
        Assert.assertEquals(LocalDateTime.of(2017, 5, 19, 0, 0), date15);
        Assert.assertEquals(compareDate.withYear(ZonedDateTime.now().getYear()), date16);
        Assert.assertEquals(compareDate.withYear(ZonedDateTime.now().getYear()), date17);
        Assert.assertEquals(compareDate.withYear(ZonedDateTime.now().getYear()), date18);
        Assert.assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 9, 1, 0, 0), date19);
    }

    @Test
    public void shouldNotParse() {
        Assert.assertFalse(GenericDateParser.parse("Snarest").isPresent());
        Assert.assertFalse(GenericDateParser.parse("Aktuelle kandidater vil bli kontaktet fortløpende").isPresent());
    }

}
