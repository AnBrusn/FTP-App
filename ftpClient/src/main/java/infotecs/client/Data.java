package infotecs.client;

import infotecs.client.exceptions.NoSuchStudentException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Data {
    private final Map<Integer, String> students;

    public Data(Map<Integer, String> students) {
        this.students = new HashMap<>(students);
    }

    public Map<Integer, String> getStudents() {
        return students;
    }

    public Map<Integer, String> getStudentsByName(String name) {
        return students
                .entrySet()
                .stream()
                .filter(student -> student.getValue().equals(name))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String getStudentById(int id) {
        if (!students.containsKey(id)) {
            throw new NoSuchStudentException(id);
        }
        return students.get(id);
    }

    public int addStudent(String name) {
        int newId = students.isEmpty() ? 1 : Collections.max(students.keySet()) + 1;
        students.put(newId, name);
        return newId;
    }

    public String deleteStudentById(int id) {
        if (!students.containsKey(id)) {
            throw new NoSuchStudentException(id);
        }
        return students.remove(id);
    }
}
