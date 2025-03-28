package validation;

import java.util.function.BiPredicate;

public class ObjectValidator {

    public static void validateNotNull(Object object, String cause) {
        if (object == null) {
            String message = String.format("The given %s object is null!", cause);

            throw new IllegalArgumentException(message);
        }
    }

    public static void validateArrayLength(Object[] array, int expectedLength, String cause) {
        if (array != null && array.length != expectedLength) {
            String message = String.format("The count of elements in the %s is expected to be %d, but is %d instead!",
                cause, expectedLength, array.length);

            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void validateOperatorIsApplied(T current, T expected, BiPredicate<T, T> predicate, String msg) {
        if (predicate != null && !predicate.test(current, expected)) {
            throw new IllegalArgumentException(msg);
        }
    }

}