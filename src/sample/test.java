package sample;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class test {

    public static void main(String[] args) {
        //System.out.println(LocalDate.now(ZoneId.of("Asia/Tokyo")));
        System.out.println(LocalDateTime.now(ZoneId.of("Asia/Tokyo")));
    }
}
