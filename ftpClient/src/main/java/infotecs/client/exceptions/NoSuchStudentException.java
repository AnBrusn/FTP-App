package infotecs.client.exceptions;

public class NoSuchStudentException extends RuntimeException {
    public NoSuchStudentException(int id) {super(String.format("There is no student with id=%d", id));}
}
