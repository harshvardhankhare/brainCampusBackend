//package com.braincampus.config;
//
//import com.braincampus.academicYear.entity.AcademicYear;
//import com.braincampus.academicYear.entity.StudentEnrollment;
//import com.braincampus.auth.entity.Permission;
//import com.braincampus.auth.entity.Role;
//import com.braincampus.auth.entity.Tenant;
//import com.braincampus.auth.entity.User;
//import com.braincampus.classSubject.entity.ClassSubject;
//import com.braincampus.common.enums.PermissionType;
//import com.braincampus.common.enums.RoleType;
//import com.braincampus.exam.entity.Exam;
//import com.braincampus.exam.entity.ExamSubject;
//import com.braincampus.exam.entity.StudentResult;
//import com.braincampus.management.entity.Expense;
//import com.braincampus.management.entity.ExpensePaymentMethod;
//import com.braincampus.management.entity.SalaryPaymentStatus;
//import com.braincampus.management.staff.entity.Staff;
//import com.braincampus.management.staff.entity.StaffType;
//import com.braincampus.schoolClass.entity.SchoolClass;
//import com.braincampus.student.entity.Student;
//import com.braincampus.student.fees.entity.FeePayment;
//import com.braincampus.student.fees.FeePaymentMethod;
//import com.braincampus.student.fees.FeeType;
//import com.braincampus.student.fees.entity.StudentFee;
//import com.braincampus.subject.entity.Subject;
//import com.braincampus.teacher.entity.Teacher;
//import com.braincampus.management.entity.SalaryPayment;
//import com.braincampus.management.entity.SalaryStructure;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.support.TransactionTemplate;
//
//import jakarta.persistence.EntityManager;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.*;
//import java.util.concurrent.ThreadLocalRandom;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final EntityManager entityManager;
//    private final PasswordEncoder passwordEncoder;
//    private final TransactionTemplate transactionTemplate;
//
//    private static final String SCHOOL_CODE = "DEMO001";
//    private static final String SCHOOL_NAME = "BrainCampus Demo School";
//    private static final String ACADEMIC_YEAR = "2026-27";
//
//    @Override
//    public void run(String... args) {
//
//        transactionTemplate.executeWithoutResult(status -> {
//            seed();
//        });
//    }
//
//    private void seed() {
//
//        /*
//         * ---------------------------------------------------------
//         * 1. TENANT
//         * ---------------------------------------------------------
//         */
//
//        Tenant tenant = findTenant();
//
//        if (tenant == null) {
//            tenant = Tenant.builder()
//                    .schoolName(SCHOOL_NAME)
//                    .schoolCode(SCHOOL_CODE)
//                    .subdomain("demo")
//                    .email("admin@demo.com")
//                    .phone("9876543210")
//                    .active(true)
//                    .build();
//
//            entityManager.persist(tenant);
//        }
//
//        /*
//         * ---------------------------------------------------------
//         * 2. PERMISSIONS
//         * ---------------------------------------------------------
//         */
//
//        Map<PermissionType, Permission> permissions = new EnumMap<>(PermissionType.class);
//
//        for (PermissionType type : PermissionType.values()) {
//
//            Permission permission = entityManager.createQuery(
//                            "SELECT p FROM Permission p WHERE p.name = :name",
//                            Permission.class
//                    )
//                    .setParameter("name", type)
//                    .getResultStream()
//                    .findFirst()
//                    .orElse(null);
//
//            if (permission == null) {
//                permission = Permission.builder()
//                        .name(type)
//                        .description(permissionDescription(type))
//                        .build();
//
//                entityManager.persist(permission);
//            }
//
//            permissions.put(type, permission);
//        }
//
//        /*
//         * ---------------------------------------------------------
//         * 3. ROLES
//         * ---------------------------------------------------------
//         */
//
//        Map<RoleType, Role> roles = new EnumMap<>(RoleType.class);
//
//        for (RoleType roleType : RoleType.values()) {
//
//            Role role = entityManager.createQuery(
//                            "SELECT r FROM Role r " +
//                                    "WHERE r.tenant = :tenant AND r.name = :name",
//                            Role.class
//                    )
//                    .setParameter("tenant", tenant)
//                    .setParameter("name", roleType)
//                    .getResultStream()
//                    .findFirst()
//                    .orElse(null);
//
//            if (role == null) {
//
//                role = Role.builder()
//                        .name(roleType)
//                        .description(roleType.name() + " role")
//                        .tenant(tenant)
//                        .permissions(new HashSet<>())
//                        .build();
//
//                entityManager.persist(role);
//            }
//
//            /*
//             * ADMIN gets everything.
//             *
//             * Other roles get permissions based on the permission
//             * name. This intentionally avoids hardcoding enum values
//             * that may not exist in your PermissionType.
//             */
//            if (roleType == RoleType.ADMIN) {
//                role.getPermissions().addAll(permissions.values());
//            } else {
//                for (PermissionType permissionType : PermissionType.values()) {
//                    if (roleHasPermission(roleType, permissionType)) {
//                        role.getPermissions().add(permissions.get(permissionType));
//                    }
//                }
//            }
//
//            entityManager.persist(role);
//            roles.put(roleType, role);
//        }
//
//        /*
//         * ---------------------------------------------------------
//         * 4. ADMIN USER
//         * ---------------------------------------------------------
//         */
//
//        User admin = findUser(tenant, "admin@demo.com");
//
//        if (admin == null) {
//
//            admin = User.builder()
//                    .firstName("Demo")
//                    .lastName("Admin")
//                    .email("admin@demo.com")
//                    .password(passwordEncoder.encode("Admin@123"))
//                    .phone("9876543210")
//                    .enabled(true)
//                    .accountLocked(false)
//                    .accountExpired(false)
//                    .credentialsExpired(false)
//                    .tenant(tenant)
//                    .role(roles.get(RoleType.ADMIN))
//                    .build();
//
//            entityManager.persist(admin);
//        }
//
//        /*
//         * ---------------------------------------------------------
//         * 5. ACADEMIC YEAR
//         * ---------------------------------------------------------
//         */
//
//        AcademicYear academicYear = entityManager.createQuery(
//                        "SELECT a FROM AcademicYear a " +
//                                "WHERE a.tenant = :tenant AND a.name = :name",
//                        AcademicYear.class
//                )
//                .setParameter("tenant", tenant)
//                .setParameter("name", ACADEMIC_YEAR)
//                .getResultStream()
//                .findFirst()
//                .orElse(null);
//
//        if (academicYear == null) {
//
//            academicYear = AcademicYear.builder()
//                    .name(ACADEMIC_YEAR)
//                    .startDate(LocalDate.of(2026, 4, 1))
//                    .endDate(LocalDate.of(2027, 3, 31))
//                    .active(true)
//                    .tenant(tenant)
//                    .build();
//
//            entityManager.persist(academicYear);
//        }
//
//        /*
//         * ---------------------------------------------------------
//         * 6. SUBJECTS
//         * ---------------------------------------------------------
//         */
//
//        List<Subject> subjects = createSubjects(tenant);
//
//        /*
//         * ---------------------------------------------------------
//         * 7. SCHOOL CLASSES
//         * ---------------------------------------------------------
//         */
//
//        List<SchoolClass> classes = createClasses(tenant);
//
//        /*
//         * ---------------------------------------------------------
//         * 8. STAFF
//         * ---------------------------------------------------------
//         */
//
//        List<Staff> staffList = createStaff(tenant, roles);
//
//        /*
//         * ---------------------------------------------------------
//         * 9. TEACHERS
//         * ---------------------------------------------------------
//         */
//
//        List<Teacher> teachers = createTeachers(tenant);
//
//        /*
//         * ---------------------------------------------------------
//         * 10. CLASS SUBJECTS
//         * ---------------------------------------------------------
//         */
//
//        Map<Long, List<ClassSubject>> classSubjects =
//                createClassSubjects(tenant, classes, subjects, teachers);
//
//        /*
//         * ---------------------------------------------------------
//         * 11. STUDENTS
//         * ---------------------------------------------------------
//         */
//
//        List<Student> students =
//                createStudents(tenant, classes);
//
//        /*
//         * ---------------------------------------------------------
//         * 12. STUDENT ENROLLMENTS
//         * ---------------------------------------------------------
//         */
//
//        createEnrollments(
//                tenant,
//                academicYear,
//                students
//        );
//
//        /*
//         * ---------------------------------------------------------
//         * 13. EXAMS + EXAM SUBJECTS + RESULTS
//         * ---------------------------------------------------------
//         */
//
//        createExamsAndResults(
//                tenant,
//                academicYear,
//                classes,
//                students,
//                classSubjects
//        );
//
//        /*
//         * ---------------------------------------------------------
//         * 14. FEES
//         * ---------------------------------------------------------
//         */
//
//        createFeesAndPayments(
//                tenant,
//                academicYear,
//                students
//        );
//
//        /*
//         * ---------------------------------------------------------
//         * 15. SALARY
//         * ---------------------------------------------------------
//         */
//
//        createSalaryData(
//                tenant,
//                staffList
//        );
//
//        /*
//         * ---------------------------------------------------------
//         * 16. EXPENSES
//         * ---------------------------------------------------------
//         */
//
//        createExpenses(tenant);
//
//        entityManager.flush();
//
//        System.out.println();
//        System.out.println("==============================================");
//        System.out.println(" BrainCampus demo data initialized");
//        System.out.println("==============================================");
//        System.out.println("School Code : DEMO001");
//        System.out.println("Email       : admin@demo.com");
//        System.out.println("Password    : Admin@123");
//        System.out.println("Students    : " + students.size());
//        System.out.println("Classes     : " + classes.size());
//        System.out.println("Subjects    : " + subjects.size());
//        System.out.println("Staff       : " + staffList.size());
//        System.out.println("==============================================");
//        System.out.println();
//    }
//
//    /*
//     * ============================================================
//     * TENANT
//     * ============================================================
//     */
//
//    private Tenant findTenant() {
//
//        return entityManager.createQuery(
//                        "SELECT t FROM Tenant t WHERE t.schoolCode = :code",
//                        Tenant.class
//                )
//                .setParameter("code", SCHOOL_CODE)
//                .getResultStream()
//                .findFirst()
//                .orElse(null);
//    }
//
//    private User findUser(Tenant tenant, String email) {
//
//        return entityManager.createQuery(
//                        "SELECT u FROM User u " +
//                                "WHERE u.tenant = :tenant AND u.email = :email",
//                        User.class
//                )
//                .setParameter("tenant", tenant)
//                .setParameter("email", email)
//                .getResultStream()
//                .findFirst()
//                .orElse(null);
//    }
//
//    /*
//     * ============================================================
//     * SUBJECTS
//     * ============================================================
//     */
//
//    private List<Subject> createSubjects(Tenant tenant) {
//
//        String[][] data = {
//                {"English", "ENG"},
//                {"Hindi", "HIN"},
//                {"Mathematics", "MATH"},
//                {"Science", "SCI"},
//                {"Social Science", "SST"},
//                {"Computer Science", "CS"},
//                {"General Knowledge", "GK"},
//                {"Physical Education", "PE"}
//        };
//
//        List<Subject> result = new ArrayList<>();
//
//        for (String[] row : data) {
//
//            Subject subject = entityManager.createQuery(
//                            "SELECT s FROM Subject s " +
//                                    "WHERE s.tenant = :tenant AND s.code = :code",
//                            Subject.class
//                    )
//                    .setParameter("tenant", tenant)
//                    .setParameter("code", row[1])
//                    .getResultStream()
//                    .findFirst()
//                    .orElse(null);
//
//            if (subject == null) {
//
//                subject = Subject.builder()
//                        .name(row[0])
//                        .code(row[1])
//                        .active(true)
//                        .tenant(tenant)
//                        .build();
//
//                entityManager.persist(subject);
//            }
//
//            result.add(subject);
//        }
//
//        return result;
//    }
//
//    /*
//     * ============================================================
//     * CLASSES
//     * ============================================================
//     */
//
//    private List<SchoolClass> createClasses(Tenant tenant) {
//
//        List<SchoolClass> result = new ArrayList<>();
//
//        for (int grade = 1; grade <= 12; grade++) {
//
//            for (String section : List.of("A", "B")) {
//
//                String name = "Grade " + grade;
//
//                SchoolClass schoolClass = entityManager.createQuery(
//                                "SELECT c FROM SchoolClass c " +
//                                        "WHERE c.tenant = :tenant " +
//                                        "AND c.name = :name " +
//                                        "AND c.section = :section " +
//                                        "AND c.academicYear = :year",
//                                SchoolClass.class
//                        )
//                        .setParameter("tenant", tenant)
//                        .setParameter("name", name)
//                        .setParameter("section", section)
//                        .setParameter("year", ACADEMIC_YEAR)
//                        .getResultStream()
//                        .findFirst()
//                        .orElse(null);
//
//                if (schoolClass == null) {
//
//                    schoolClass = SchoolClass.builder()
//                            .name(name)
//                            .section(section)
//                            .academicYear(ACADEMIC_YEAR)
//                            .active(true)
//                            .tenant(tenant)
//                            .build();
//
//                    entityManager.persist(schoolClass);
//                }
//
//                result.add(schoolClass);
//            }
//        }
//
//        return result;
//    }
//
//    /*
//     * ============================================================
//     * STAFF
//     * ============================================================
//     */
//
//    private List<Staff> createStaff(
//            Tenant tenant,
//            Map<RoleType, Role> roles
//    ) {
//
//        List<Staff> result = new ArrayList<>();
//
//        String[][] data = {
//                {"Amit", "Sharma", "TEACHER"},
//                {"Priya", "Verma", "TEACHER"},
//                {"Rahul", "Gupta", "TEACHER"},
//                {"Neha", "Singh", "TEACHER"},
//                {"Vikas", "Kumar", "TEACHER"},
//                {"Pooja", "Mishra", "TEACHER"},
//                {"Rohit", "Yadav", "TEACHER"},
//                {"Anjali", "Tiwari", "TEACHER"},
//                {"Sandeep", "Jain", "TEACHER"},
//                {"Kavita", "Sharma", "TEACHER"},
//
//                {"Rajesh", "Mehta", "ACCOUNTANT"},
//                {"Sunita", "Agarwal", "ACCOUNTANT"},
//
//                {"Manoj", "Patel", "RECEPTIONIST"},
//
//                {"Deepak", "Joshi", "LIBRARIAN"},
//
//                {"Ramesh", "Khan", "DRIVER"},
//                {"Mahesh", "Das", "DRIVER"},
//
//                {"Arun", "Singh", "SECURITY"},
//                {"Mohan", "Rawat", "SECURITY"},
//
//                {"Nitin", "Saxena", "ADMINISTRATIVE"},
//                {"Seema", "Gupta", "ADMINISTRATIVE"}
//        };
//
//        for (int i = 0; i < data.length; i++) {
//
//            String firstName = data[i][0];
//            String lastName = data[i][1];
//            StaffType staffType = StaffType.valueOf(data[i][2]);
//
//            String employeeCode = String.format("EMP%03d", i + 1);
//            String email = "staff" + (i + 1) + "@demo.com";
//
//            Staff existing = entityManager.createQuery(
//                            "SELECT s FROM Staff s " +
//                                    "WHERE s.tenant = :tenant " +
//                                    "AND s.employeeCode = :employeeCode",
//                            Staff.class
//                    )
//                    .setParameter("tenant", tenant)
//                    .setParameter("employeeCode", employeeCode)
//                    .getResultStream()
//                    .findFirst()
//                    .orElse(null);
//
//            if (existing != null) {
//                result.add(existing);
//                continue;
//            }
//
//            RoleType roleType;
//
//            switch (staffType) {
//                case TEACHER -> roleType = RoleType.TEACHER;
//                case ACCOUNTANT -> roleType = RoleType.ACCOUNTANT;
//                case LIBRARIAN -> roleType = RoleType.LIBRARIAN;
//                default -> roleType = RoleType.ADMIN;
//            }
//
//            User user = User.builder()
//                    .firstName(firstName)
//                    .lastName(lastName)
//                    .email(email)
//                    .password(passwordEncoder.encode("Staff@123"))
//                    .phone("98" + String.format("%08d", i + 1))
//                    .enabled(true)
//                    .accountLocked(false)
//                    .accountExpired(false)
//                    .credentialsExpired(false)
//                    .tenant(tenant)
//                    .role(roles.get(roleType))
//                    .build();
//
//            entityManager.persist(user);
//
//            Staff staff = Staff.builder()
//                    .employeeCode(employeeCode)
//                    .firstName(firstName)
//                    .lastName(lastName)
//                    .email(email)
//                    .phone(user.getPhone())
//                    .dateOfBirth(LocalDate.of(
//                            1985 + (i % 10),
//                            1 + (i % 12),
//                            5 + (i % 20)
//                    ))
//                    .joiningDate(LocalDate.of(
//                            2020 + (i % 5),
//                            4 + (i % 6),
//                            1 + (i % 20)
//                    ))
//                    .type(staffType)
//                    .designation(staffType == StaffType.TEACHER
//                            ? "Teacher"
//                            : staffType.name())
//                    .address("Demo School Campus")
//                    .active(true)
//                    .tenant(tenant)
//                    .user(user)
//                    .build();
//
//            entityManager.persist(staff);
//            result.add(staff);
//        }
//
//        return result;
//    }
//
//    /*
//     * ============================================================
//     * TEACHERS
//     * ============================================================
//     */
//
//    private List<Teacher> createTeachers(Tenant tenant) {
//
//        List<Teacher> result = new ArrayList<>();
//
//        String[][] data = {
//                {"Amit", "Sharma"},
//                {"Priya", "Verma"},
//                {"Rahul", "Gupta"},
//                {"Neha", "Singh"},
//                {"Vikas", "Kumar"},
//                {"Pooja", "Mishra"},
//                {"Rohit", "Yadav"},
//                {"Anjali", "Tiwari"},
//                {"Sandeep", "Jain"},
//                {"Kavita", "Sharma"}
//        };
//
//        for (int i = 0; i < data.length; i++) {
//
//            String employeeCode = String.format("TCH%03d", i + 1);
//
//            Teacher teacher = entityManager.createQuery(
//                            "SELECT t FROM Teacher t " +
//                                    "WHERE t.tenant = :tenant " +
//                                    "AND t.employeeCode = :code",
//                            Teacher.class
//                    )
//                    .setParameter("tenant", tenant)
//                    .setParameter("code", employeeCode)
//                    .getResultStream()
//                    .findFirst()
//                    .orElse(null);
//
//            if (teacher == null) {
//
//                teacher = Teacher.builder()
//                        .employeeCode(employeeCode)
//                        .firstName(data[i][0])
//                        .lastName(data[i][1])
//                        .email("teacher" + (i + 1) + "@demo.com")
//                        .phone("97" + String.format("%08d", i + 1))
//                        .active(true)
//                        .tenant(tenant)
//                        .build();
//
//                entityManager.persist(teacher);
//            }
//
//            result.add(teacher);
//        }
//
//        return result;
//    }
//
//    /*
//     * ============================================================
//     * CLASS SUBJECTS
//     * ============================================================
//     */
//
//    private Map<Long, List<ClassSubject>> createClassSubjects(
//            Tenant tenant,
//            List<SchoolClass> classes,
//            List<Subject> subjects,
//            List<Teacher> teachers
//    ) {
//
//        Map<Long, List<ClassSubject>> result = new HashMap<>();
//
//        for (SchoolClass schoolClass : classes) {
//
//            List<ClassSubject> classSubjectList = new ArrayList<>();
//
//            for (int i = 0; i < subjects.size(); i++) {
//
//                Subject subject = subjects.get(i);
//                Teacher teacher = teachers.get(i % teachers.size());
//
//                ClassSubject classSubject = entityManager.createQuery(
//                                "SELECT cs FROM ClassSubject cs " +
//                                        "WHERE cs.tenant = :tenant " +
//                                        "AND cs.schoolClass = :schoolClass " +
//                                        "AND cs.subject = :subject " +
//                                        "AND cs.academicYear = :year",
//                                ClassSubject.class
//                        )
//                        .setParameter("tenant", tenant)
//                        .setParameter("schoolClass", schoolClass)
//                        .setParameter("subject", subject)
//                        .setParameter("year", ACADEMIC_YEAR)
//                        .getResultStream()
//                        .findFirst()
//                        .orElse(null);
//
//                if (classSubject == null) {
//
//                    classSubject = ClassSubject.builder()
//                            .schoolClass(schoolClass)
//                            .subject(subject)
//                            .teacher(teacher)
//                            .academicYear(ACADEMIC_YEAR)
//                            .weeklyPeriods(
//                                    subject.getCode().equals("PE") ? 2 : 5
//                            )
//                            .active(true)
//                            .tenant(tenant)
//                            .build();
//
//                    entityManager.persist(classSubject);
//                }
//
//                classSubjectList.add(classSubject);
//            }
//
//            result.put(schoolClass.getId(), classSubjectList);
//        }
//
//        return result;
//    }
//
//    /*
//     * ============================================================
//     * STUDENTS
//     * ============================================================
//     */
//
//    private List<Student> createStudents(
//            Tenant tenant,
//            List<SchoolClass> classes
//    ) {
//
//        List<Student> result = new ArrayList<>();
//
//        for (int i = 1; i <= 500; i++) {
//
//            String roleNumber = String.format("STU%04d", i);
//
//            Student existing = entityManager.createQuery(
//                            "SELECT s FROM Student s " +
//                                    "WHERE s.tenant = :tenant " +
//                                    "AND s.roleNumber = :roleNumber",
//                            Student.class
//                    )
//                    .setParameter("tenant", tenant)
//                    .setParameter("roleNumber", roleNumber)
//                    .getResultStream()
//                    .findFirst()
//                    .orElse(null);
//
//            if (existing != null) {
//                result.add(existing);
//                continue;
//            }
//
//            SchoolClass schoolClass = classes.get((i - 1) % classes.size());
//
//            Student student = Student.builder()
//                    .roleNumber(roleNumber)
//                    .firstName(firstName(i))
//                    .lastName(lastName(i))
//                    .email("student" + i + "@demo.com")
//                    .phone("96" + String.format("%08d", i))
//                    .dateOfBirth(
//                            LocalDate.of(
//                                    2010 + ((i % 8)),
//                                    1 + (i % 12),
//                                    1 + (i % 25)
//                            )
//                    )
//                    .address("Demo Student Address " + i)
//                    .parentName("Parent " + i)
//                    .parentPhone("95" + String.format("%08d", i))
//                    .active(true)
//                    .tenant(tenant)
//                    .schoolClass(schoolClass)
//                    .build();
//
//            entityManager.persist(student);
//            result.add(student);
//        }
//
//        return result;
//    }
//
//    /*
//     * ============================================================
//     * ENROLLMENTS
//     * ============================================================
//     */
//
//    private void createEnrollments(
//            Tenant tenant,
//            AcademicYear academicYear,
//            List<Student> students
//    ) {
//
//        for (Student student : students) {
//
//            Long count = entityManager.createQuery(
//                            "SELECT COUNT(e) FROM StudentEnrollment e " +
//                                    "WHERE e.tenant = :tenant " +
//                                    "AND e.student = :student " +
//                                    "AND e.academicYear = :academicYear",
//                            Long.class
//                    )
//                    .setParameter("tenant", tenant)
//                    .setParameter("student", student)
//                    .setParameter("academicYear", academicYear)
//                    .getSingleResult();
//
//            if (count > 0) {
//                continue;
//            }
//
//            StudentEnrollment enrollment = StudentEnrollment.builder()
//                    .student(student)
//                    .academicYear(academicYear)
//                    .schoolClass(student.getSchoolClass())
//                    .active(true)
//                    .tenant(tenant)
//                    .build();
//
//            entityManager.persist(enrollment);
//        }
//    }
//
//    /*
//     * ============================================================
//     * EXAMS + RESULTS
//     * ============================================================
//     */
//
//    private void createExamsAndResults(
//            Tenant tenant,
//            AcademicYear academicYear,
//            List<SchoolClass> classes,
//            List<Student> students,
//            Map<Long, List<ClassSubject>> classSubjects
//    ) {
//
//        String[] examNames = {
//                "Unit Test 1",
//                "Half Yearly",
//                "Unit Test 2",
//                "Annual Exam"
//        };
//
//        Random random = new Random(42);
//
//        for (SchoolClass schoolClass : classes) {
//
//            List<ClassSubject> subjects =
//                    classSubjects.get(schoolClass.getId());
//
//            List<Student> classStudents = students.stream()
//                    .filter(s -> s.getSchoolClass().getId()
//                            .equals(schoolClass.getId()))
//                    .toList();
//
//            for (int examIndex = 0;
//                 examIndex < examNames.length;
//                 examIndex++) {
//
//                String examName = examNames[examIndex];
//
//                Exam exam = entityManager.createQuery(
//                                "SELECT e FROM Exam e " +
//                                        "WHERE e.tenant = :tenant " +
//                                        "AND e.schoolClass = :schoolClass " +
//                                        "AND e.name = :name " +
//                                        "AND e.academicYear = :year",
//                                Exam.class
//                        )
//                        .setParameter("tenant", tenant)
//                        .setParameter("schoolClass", schoolClass)
//                        .setParameter("name", examName)
//                        .setParameter("year", ACADEMIC_YEAR)
//                        .getResultStream()
//                        .findFirst()
//                        .orElse(null);
//
//                if (exam == null) {
//
//                    LocalDate startDate = switch (examIndex) {
//                        case 0 -> LocalDate.of(2026, 5, 10);
//                        case 1 -> LocalDate.of(2026, 9, 15);
//                        case 2 -> LocalDate.of(2026, 11, 20);
//                        default -> LocalDate.of(2027, 2, 15);
//                    };
//
//                    exam = Exam.builder()
//                            .name(examName)
//                            .academicYear(ACADEMIC_YEAR)
//                            .schoolClass(schoolClass)
//                            .startDate(startDate)
//                            .endDate(startDate.plusDays(5))
//                            .active(true)
//                            .tenant(tenant)
//                            .build();
//
//                    entityManager.persist(exam);
//                }
//
//                for (ClassSubject classSubject : subjects) {
//
//                    ExamSubject examSubject = entityManager.createQuery(
//                                    "SELECT es FROM ExamSubject es " +
//                                            "WHERE es.tenant = :tenant " +
//                                            "AND es.exam = :exam " +
//                                            "AND es.classSubject = :classSubject",
//                                    ExamSubject.class
//                            )
//                            .setParameter("tenant", tenant)
//                            .setParameter("exam", exam)
//                            .setParameter("classSubject", classSubject)
//                            .getResultStream()
//                            .findFirst()
//                            .orElse(null);
//
//                    if (examSubject == null) {
//
//                        LocalDate examDate = exam.getStartDate()
//                                .plusDays(
//                                        Math.abs(classSubject.getId().hashCode())
//                                                % 5
//                                );
//
//                        examSubject = ExamSubject.builder()
//                                .exam(exam)
//                                .classSubject(classSubject)
//                                .examDate(examDate)
//                                .maxMarks(100)
//                                .passingMarks(33)
//                                .remarks("Demo examination")
//                                .active(true)
//                                .tenant(tenant)
//                                .build();
//
//                        entityManager.persist(examSubject);
//                    }
//
//                    for (Student student : classStudents) {
//
//                        Long count = entityManager.createQuery(
//                                        "SELECT COUNT(r) FROM StudentResult r " +
//                                                "WHERE r.tenant = :tenant " +
//                                                "AND r.student = :student " +
//                                                "AND r.examSubject = :examSubject",
//                                        Long.class
//                                )
//                                .setParameter("tenant", tenant)
//                                .setParameter("student", student)
//                                .setParameter("examSubject", examSubject)
//                                .getSingleResult();
//
//                        if (count > 0) {
//                            continue;
//                        }
//
//                        double marks = 45 + random.nextDouble() * 50;
//
//                        if (random.nextInt(20) == 0) {
//                            marks = 25 + random.nextDouble() * 8;
//                        }
//
//                        StudentResult result = StudentResult.builder()
//                                .student(student)
//                                .examSubject(examSubject)
//                                .marksObtained(
//                                        BigDecimal.valueOf(marks)
//                                                .setScale(2, java.math.RoundingMode.HALF_UP)
//                                )
//                                .remarks(marks < 33
//                                        ? "Needs improvement"
//                                        : "Good performance")
//                                .active(true)
//                                .tenant(tenant)
//                                .build();
//
//                        entityManager.persist(result);
//                    }
//                }
//            }
//        }
//    }
//
//    /*
//     * ============================================================
//     * FEES
//     * ============================================================
//     */
//
//    private void createFeesAndPayments(
//            Tenant tenant,
//            AcademicYear academicYear,
//            List<Student> students
//    ) {
//
//        Random random = new Random(100);
//
//        for (Student student : students) {
//
//            for (int month = 4; month <= 12; month++) {
//
//                StudentFee fee = entityManager.createQuery(
//                                "SELECT f FROM StudentFee f " +
//                                        "WHERE f.tenant = :tenant " +
//                                        "AND f.student = :student " +
//                                        "AND f.academicYear = :year " +
//                                        "AND f.feeType = :feeType " +
//                                        "AND f.feeMonth = :month",
//                                StudentFee.class
//                        )
//                        .setParameter("tenant", tenant)
//                        .setParameter("student", student)
//                        .setParameter("year", ACADEMIC_YEAR)
//                        .setParameter("feeType", FeeType.TUITION)
//                        .setParameter("month", month)
//                        .getResultStream()
//                        .findFirst()
//                        .orElse(null);
//
//                if (fee == null) {
//
//                    BigDecimal amount = BigDecimal.valueOf(
//                            2500 + random.nextInt(1501)
//                    );
//
//                    fee = StudentFee.builder()
//                            .tenant(tenant)
//                            .student(student)
//                            .academicYear(ACADEMIC_YEAR)
//                            .feeType(FeeType.TUITION)
//                            .feeMonth(month)
//                            .amount(amount)
//                            .dueDate(LocalDate.of(
//                                    2026,
//                                    month,
//                                    10
//                            ))
//                            .description("Monthly tuition fee")
//                            .build();
//
//                    entityManager.persist(fee);
//                }
//
//                /*
//                 * Around 80% paid.
//                 */
//                if (random.nextInt(100) < 80) {
//
//                    Long paymentCount = entityManager.createQuery(
//                                    "SELECT COUNT(p) FROM FeePayment p " +
//                                            "WHERE p.tenant = :tenant " +
//                                            "AND p.studentFee = :fee",
//                                    Long.class
//                            )
//                            .setParameter("tenant", tenant)
//                            .setParameter("fee", fee)
//                            .getSingleResult();
//
//                    if (paymentCount == 0) {
//
//                        FeePaymentMethod method =
//                                FeePaymentMethod.values()[
//                                        random.nextInt(
//                                                FeePaymentMethod.values().length
//                                        )
//                                        ];
//
//                        FeePayment payment = FeePayment.builder()
//                                .tenant(tenant)
//                                .studentFee(fee)
//                                .amount(fee.getAmount())
//                                .paymentDate(
//                                        fee.getDueDate().minusDays(
//                                                random.nextInt(5)
//                                        )
//                                )
//                                .paymentMethod(method)
//                                .transactionReference(
//                                        "TXN" + UUID.randomUUID()
//                                                .toString()
//                                                .substring(0, 8)
//                                                .toUpperCase()
//                                )
//                                .remarks("Demo payment")
//                                .build();
//
//                        entityManager.persist(payment);
//                    }
//                }
//            }
//        }
//    }
//
//    /*
//     * ============================================================
//     * SALARY
//     * ============================================================
//     */
//
//    private void createSalaryData(
//            Tenant tenant,
//            List<Staff> staffList
//    ) {
//
//        Random random = new Random(200);
//
//        for (Staff staff : staffList) {
//
//            BigDecimal basicSalary = switch (staff.getType()) {
//                case TEACHER -> BigDecimal.valueOf(35000);
//                case ACCOUNTANT -> BigDecimal.valueOf(30000);
//                case LIBRARIAN -> BigDecimal.valueOf(24000);
//                case RECEPTIONIST -> BigDecimal.valueOf(22000);
//                case DRIVER -> BigDecimal.valueOf(18000);
//                case SECURITY -> BigDecimal.valueOf(16000);
//                case ADMINISTRATIVE -> BigDecimal.valueOf(28000);
//                default -> BigDecimal.valueOf(20000);
//            };
//
//            SalaryStructure structure = entityManager.createQuery(
//                            "SELECT s FROM SalaryStructure s " +
//                                    "WHERE s.tenant = :tenant " +
//                                    "AND s.staff = :staff " +
//                                    "AND s.active = true",
//                            SalaryStructure.class
//                    )
//                    .setParameter("tenant", tenant)
//                    .setParameter("staff", staff)
//                    .getResultStream()
//                    .findFirst()
//                    .orElse(null);
//
//            if (structure == null) {
//
//                structure = SalaryStructure.builder()
//                        .staff(staff)
//                        .basicSalary(basicSalary)
//                        .allowances(basicSalary.multiply(
//                                BigDecimal.valueOf(0.15)
//                        ))
//                        .deductions(basicSalary.multiply(
//                                BigDecimal.valueOf(0.05)
//                        ))
//                        .effectiveFrom(LocalDate.of(2026, 4, 1))
//                        .effectiveTo(null)
//                        .active(true)
//                        .tenant(tenant)
//                        .build();
//
//                entityManager.persist(structure);
//            }
//
//            for (int month = 4; month <= 12; month++) {
//
//                SalaryPayment existing = entityManager.createQuery(
//                                "SELECT p FROM SalaryPayment p " +
//                                        "WHERE p.tenant = :tenant " +
//                                        "AND p.staff = :staff " +
//                                        "AND p.month = :month " +
//                                        "AND p.year = :year",
//                                SalaryPayment.class
//                        )
//                        .setParameter("tenant", tenant)
//                        .setParameter("staff", staff)
//                        .setParameter("month", month)
//                        .setParameter("year", 2026)
//                        .getResultStream()
//                        .findFirst()
//                        .orElse(null);
//
//                if (existing != null) {
//                    continue;
//                }
//
//                SalaryPaymentStatus status;
//
//                int randomValue = random.nextInt(100);
//
//                if (randomValue < 85) {
//                    status = SalaryPaymentStatus.PAID;
//                } else if (randomValue < 95) {
//                    status = SalaryPaymentStatus.PARTIAL;
//                } else {
//                    status = SalaryPaymentStatus.PENDING;
//                }
//
//                BigDecimal gross = structure.getBasicSalary()
//                        .add(structure.getAllowances())
//                        .subtract(structure.getDeductions());
//
//                BigDecimal amount;
//
//                if (status == SalaryPaymentStatus.PAID) {
//                    amount = gross;
//                } else if (status == SalaryPaymentStatus.PARTIAL) {
//                    amount = gross.multiply(
//                            BigDecimal.valueOf(0.5)
//                    );
//                } else {
//                    amount = BigDecimal.ZERO;
//                }
//
//                SalaryPayment payment = SalaryPayment.builder()
//                        .staff(staff)
//                        .month(month)
//                        .year(2026)
//                        .amount(amount)
//                        .paymentDate(
//                                status == SalaryPaymentStatus.PENDING
//                                        ? null
//                                        : LocalDate.of(
//                                        2026,
//                                        month,
//                                        28
//                                )
//                        )
//                        .status(status)
//                        .remarks("Demo salary payment")
//                        .tenant(tenant)
//                        .build();
//
//                entityManager.persist(payment);
//            }
//        }
//    }
//
//    /*
//     * ============================================================
//     * EXPENSES
//     * ============================================================
//     */
//
//    private void createExpenses(Tenant tenant) {
//
//        String[] categories = {
//                "Electricity",
//                "Maintenance",
//                "Stationery",
//                "Internet",
//                "Cleaning",
//                "Transport",
//                "School Supplies",
//                "Events",
//                "Repairs",
//                "Other"
//        };
//
//        ExpensePaymentMethod[] methods =
//                ExpensePaymentMethod.values();
//
//        Random random = new Random(300);
//
//        for (int i = 1; i <= 120; i++) {
//
//            LocalDate expenseDate = LocalDate.of(
//                    2026,
//                    4 + random.nextInt(5),
//                    1 + random.nextInt(27)
//            );
//
//            Expense expense = Expense.builder()
//                    .tenant(tenant)
//                    .category(categories[random.nextInt(categories.length)])
//                    .amount(BigDecimal.valueOf(
//                            500 + random.nextInt(49501)
//                    ))
//                    .expenseDate(expenseDate)
//                    .description("Demo school expense #" + i)
//                    .paymentMethod(
//                            methods[random.nextInt(methods.length)]
//                    )
//                    .referenceNumber(
//                            "EXP-" + String.format("%04d", i)
//                    )
//                    .build();
//
//            entityManager.persist(expense);
//        }
//    }
//
//    /*
//     * ============================================================
//     * PERMISSION HELPERS
//     * ============================================================
//     */
//
//    private boolean roleHasPermission(
//            RoleType role,
//            PermissionType permission
//    ) {
//
//        String p = permission.name();
//
//        if (role == RoleType.TEACHER) {
//            return p.contains("STUDENT")
//                    || p.contains("ATTENDANCE")
//                    || p.contains("EXAM")
//                    || p.contains("RESULT")
//                    || p.contains("ACADEMIC");
//        }
//
//        if (role == RoleType.ACCOUNTANT) {
//            return p.contains("FEE")
//                    || p.contains("PAYMENT")
//                    || p.contains("SALARY")
//                    || p.contains("EXPENSE")
//                    || p.contains("FINANCE");
//        }
//
//        if (role == RoleType.LIBRARIAN) {
//            return p.contains("LIBRARY");
//        }
//
//        return false;
//    }
//
//    private String permissionDescription(PermissionType type) {
//
//        String name = type.name()
//                .toLowerCase(Locale.ROOT)
//                .replace("_", " ");
//
//        return "Permission to " + name;
//    }
//
//    /*
//     * ============================================================
//     * DEMO NAMES
//     * ============================================================
//     */
//
//    private String firstName(int index) {
//
//        String[] names = {
//                "Aarav",
//                "Vivaan",
//                "Aditya",
//                "Arjun",
//                "Vihaan",
//                "Reyansh",
//                "Kabir",
//                "Anaya",
//                "Diya",
//                "Aadhya",
//                "Ishaan",
//                "Riya",
//                "Myra",
//                "Sara",
//                "Anika",
//                "Krishna",
//                "Rudra",
//                "Aanya",
//                "Meera",
//                "Kavya"
//        };
//
//        return names[(index - 1) % names.length];
//    }
//
//    private String lastName(int index) {
//
//        String[] names = {
//                "Sharma",
//                "Verma",
//                "Gupta",
//                "Singh",
//                "Kumar",
//                "Yadav",
//                "Mishra",
//                "Jain",
//                "Agarwal",
//                "Patel",
//                "Tiwari",
//                "Joshi"
//        };
//
//        return names[(index - 1) % names.length];
//    }
//}