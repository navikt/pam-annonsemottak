package no.nav.pam.annonsemottak.annonsemottak;


import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class GenericDateParserTest {

    @Test
    public void shouldParse() {
        DateTime compareDate = new DateTime(2017, 7, 19, 0, 0);

        DateTime date1 = GenericDateParser.parseDate("19.07.2017");
        DateTime date2 = GenericDateParser.parseDate("19.7.17");
        DateTime date3 = GenericDateParser.parseDate("19/07/2017");
        DateTime date4 = GenericDateParser.parseDate("19/7/17");
        DateTime date5 = GenericDateParser.parseDate("19/7-2017");
        DateTime date6 = GenericDateParser.parseDate("19/7-17");
        DateTime date7 = GenericDateParser.parseDate("2017-07-19");
        DateTime date11 = GenericDateParser.parseDate("19. juli 2017");
        DateTime date12 = GenericDateParser.parseDate("19. jul 2017");
        DateTime date13 = GenericDateParser.parseDate("19. jul 17");

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

    }

    @Test
    public void shouldNotParse(){
        Assert.assertNull(GenericDateParser.parseDate("Snarest"));
        Assert.assertNull(GenericDateParser.parseDate("Aktuelle kandidater vil bli kontaktet fortløpende"));
    }


}
