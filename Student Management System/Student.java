/**
 * Student model class representing a student record.
 */
public class Student {

    private String studentId;
    private String firstName;
    private String lastName;
    private String major;
    private String phone;
    private String cgpa;
    private String dob;

    public Student() {}

    public Student(String studentId, String firstName, String lastName,
                   String major, String phone, String cgpa, String dob) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.phone = phone;
        this.cgpa = cgpa;
        this.dob = dob;
    }

    // Getters
    public String getStudentId()  { return studentId; }
    public String getFirstName()  { return firstName; }
    public String getLastName()   { return lastName; }
    public String getMajor()      { return major; }
    public String getPhone()      { return phone; }
    public String getCgpa()        { return cgpa; }
    public String getDob()        { return dob; }

    // Setters
    public void setStudentId(String studentId)  { this.studentId = studentId; }
    public void setFirstName(String firstName)  { this.firstName = firstName; }
    public void setLastName(String lastName)    { this.lastName = lastName; }
    public void setMajor(String major)          { this.major = major; }
    public void setPhone(String phone)          { this.phone = phone; }
    public void setCgpa(String cgpa)            { this.cgpa = cgpa; }
    public void setDob(String dob)              { this.dob = dob; }

    @Override
    public String toString() {
        return studentId + " | " + firstName + " " + lastName +
               " | " + major + " | CGPA: " + cgpa;
    }
}
