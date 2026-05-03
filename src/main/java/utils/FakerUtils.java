package utils;
import com.github.javafaker.Faker;

public class FakerUtils {

    private static final Faker faker = new Faker();

    public static String generateEmail() {
        return faker.internet().emailAddress();
    }

    public static String generateFullName() {
        return faker.name().fullName();
    }
    public static String someMessage() {
        return faker.esports().player();
    }
    public static String generatePhone() {
        return faker.phoneNumber().cellPhone();
    }

    public static String generateNumber() {
        return faker.phoneNumber().cellPhone();
    }

    public static String getNumber() {
        return faker.number().digits(7);
    }
    public static String generatePassword() {
        return faker.internet().password(8, 12, true, true, true);
    }



}
