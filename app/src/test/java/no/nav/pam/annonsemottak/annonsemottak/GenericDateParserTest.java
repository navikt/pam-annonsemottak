package no.nav.pam.annonsemottak.annonsemottak;


import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;


public class GenericDateParserTest {

    @Test
    public void shouldParse() {
        LocalDateTime compareDate = LocalDateTime.of(2017, 7, 19, 0, 0);

        LocalDateTime date1 = GenericDateParser.parseDate("19.07.2017");
        LocalDateTime date2 = GenericDateParser.parseDate("19.7.17");
        LocalDateTime date3 = GenericDateParser.parseDate("19/07/2017");
        LocalDateTime date4 = GenericDateParser.parseDate("19/7/17");
        LocalDateTime date5 = GenericDateParser.parseDate("19/7-2017");
        LocalDateTime date6 = GenericDateParser.parseDate("19/7-17");
        LocalDateTime date7 = GenericDateParser.parseDate("2017-07-19");
        LocalDateTime date11 = GenericDateParser.parseDate("19. juli 2017");
        LocalDateTime date12 = GenericDateParser.parseDate("19. jul 2017");
        LocalDateTime date13 = GenericDateParser.parseDate("19. jul 17");
        LocalDateTime date14 = GenericDateParser.parseDate("19. juli 17");
        LocalDateTime date15 = GenericDateParser.parseDate("19. mai 2017");
        LocalDateTime date16 = GenericDateParser.parseDate("19. 7");
        LocalDateTime date17 = GenericDateParser.parseDate("19. jul");
        LocalDateTime date18 = GenericDateParser.parseDate("19. juli");


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
    }

    @Test
    public void shouldNotParse() {
        Assert.assertNull(GenericDateParser.parseDate("Snarest"));
        Assert.assertNull(GenericDateParser.parseDate("Aktuelle kandidater vil bli kontaktet fortløpende"));
    }

}
