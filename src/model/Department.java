package model;

public class Department {
    private int id;
    private String name;

    public Department(int id, String name) {
        this.id = id;
        setName(name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Department name cannot be empty"
            );
        }

        this.name = name.trim();
    }

    public String toFileString() {
        return id + "," + name;
    }

    public static Department fromFileString(String line) {

        String[] parts = line.split(",", 2);

        int id = Integer.parseInt(parts[0]);
        String name = parts[1];

        return new Department(id, name);
    }

    @Override
    public String toString() {
        return name;
    }
}
