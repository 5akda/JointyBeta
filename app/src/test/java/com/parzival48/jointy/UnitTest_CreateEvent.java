package com.parzival48.jointy;
import org.junit.Test;
import static org.junit.Assert.*;

public class UnitTest_CreateEvent {
    @Test
    public void allCorrectEnglish(){
        String result = CreateEventUI.validE("Karoke Eiei",
                "Mega Bangna",
                "C'mon Everyone");

        assertEquals("",result);
    }

    @Test
    public void allCorrectThai(){
        String result = CreateEventUI.validE("คาราโอเกะ อิอิ",
                "เมกะ บางนา",
                "ทุกเพศทุกวัย");

        assertEquals("",result);
    }

    @Test
    public void incorrectName_4Char(){
        String result = CreateEventUI.validE("Run!",
                "RamaIX Park",
                "");

        assertEquals("Please Re-Check Name",result);
    }

    @Test
    public void incorrectName_31Char(){
        String result = CreateEventUI.validE("Run Run Run Run Run Run Run Run",
                "RamaIX Park",
                "");

        assertEquals("Please Re-Check Name",result);
    }

    @Test
    public void incorrectLocation_4Char(){
        String result = CreateEventUI.validE("Karaoke",
                "Mega",
                "");

        assertEquals("Please Re-Check Location",result);
    }

    @Test
    public void incorrectLocation_31Char(){
        String result = CreateEventUI.validE("Karaoke",
                "2ndFloor, Mega Bangna, Bangkok, TH",
                "");

        assertEquals("Please Re-Check Location",result);
    }

    @Test
    public void incorrectDescription_51Char(){
        String result = CreateEventUI.validE("Karaoke",
                "2ndFloor, Mega Bangna",
                "Sing Sang Sung Sing Sang Sung Sing Sang Sung ( 0.0 )");

        assertEquals("Please Re-Check Description",result);
    }

    @Test
    public void pastDateTime(){
        boolean result = CreateEventUI.dateCheck("1/1/2009","12:30");
        assertEquals(false,result);
    }

    @Test
    public void futureDateTime(){
        boolean result = CreateEventUI.dateCheck("1/1/2020","0:00");
        assertEquals(true,result);
    }

}
