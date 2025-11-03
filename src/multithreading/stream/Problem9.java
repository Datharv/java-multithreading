package multithreading.stream;


//Group employees by department using Java Streams (Collectors.groupingBy).


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Employee {
    String name;
    String department;
    double salary;

    public Employee(String name, String department, double salary) {
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }
}


public class Problem9 {

    public static void main(String[] args) {

        List<Employee> employees = Arrays.asList(
                new Employee("Atharv", "IT", 70000),
                new Employee("Riya", "HR", 50000),
                new Employee("Sam", "IT", 60000),
                new Employee("Aman", "Finance", 55000),
                new Employee("Neha", "HR", 52000)
        );

//        group by departments

        Map<String, List<Employee>> byDept = employees.stream().collect(Collectors.groupingBy(e -> e.getDepartment()));

        System.out.println("Employees group by department ");
        byDept.forEach((k, v) -> {
            System.out.println(k + " : " + v.stream().map(Employee::getName).toList());
        });


//        only names

        Map<String, List<String>> onlyNames = employees.stream().collect(Collectors.groupingBy(Employee::getDepartment, Collectors.mapping(Employee::getName, Collectors.toList())));
        System.out.println(onlyNames);

//        Count employees per department

        Map<String, Long> onlyCount = employees.stream().collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
        System.out.println(onlyCount);

//        Average salary per department

        Map<String, Double> avgSalary = employees.stream().collect(Collectors.groupingBy(Employee::getDepartment, Collectors.averagingDouble(Employee::getSalary)));
        System.out.println(avgSalary);

        Map<String, Double> avgSalary2 = employees.stream().collect(Collectors.groupingBy(Employee::getDepartment, Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().mapToDouble(Employee::getSalary).average().orElse(0.0))));
        System.out.println(avgSalary2);

        Map<String, double[]> deptData = new HashMap<>();

        employees.forEach(e-> {
            deptData.computeIfAbsent(e.getDepartment(),k-> new double[2]);
            double[] data = deptData.get(e.getDepartment());
            data[0] += e.getSalary();
            data[1] += 1;
        });

        Map<String, Double> avgSalary3 = deptData.entrySet().stream().collect(Collectors.toMap(Map.Entry :: getKey, e-> e.getValue()[0]/ e.getValue()[1]));
        System.out.println(avgSalary3);
    }

}
