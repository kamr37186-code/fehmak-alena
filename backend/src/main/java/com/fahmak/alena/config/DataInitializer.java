package com.fahmak.alena.config;

import com.fahmak.alena.assessment.entity.Question;
import com.fahmak.alena.assessment.entity.Quiz;
import com.fahmak.alena.assessment.repository.QuestionRepository;
import com.fahmak.alena.assessment.repository.QuizRepository;
import com.fahmak.alena.course.entity.Course;
import com.fahmak.alena.course.repository.CourseRepository;
import com.fahmak.alena.payment.entity.SubscriptionPlan;
import com.fahmak.alena.payment.repository.SubscriptionPlanRepository;
import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds the database with real production-like data for development.
 * Automatically clears previous data to ensure a clean state.
 */
@Slf4j
@Component
@Profile("seed-legacy")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String DEFAULT_PASSWORD = "password123";
    private static final String ACTIVE_STATUS = "ACTIVE";

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final com.fahmak.alena.course.repository.CourseEnrollmentRepository courseEnrollmentRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping seed to preserve existing users.");
            return;
        }

        log.info("Empty database detected. Injecting seed data...");
        initializeRealUsers();
        initializeRealCourses();
        initializeRealQuizzes();
        initializeEnrollments();
        initializeSubscriptionPlans();
        generateMockCustomersAndEnrollments();
        
        log.info("Seed data initialized successfully. You can now log in with the default accounts.");
    }

    private void initializeRealUsers() {
        createUser("omar.student@fahmak.com", "Omar", "Khaled", Role.STUDENT);
        createUser("ahmed.instructor@fahmak.com", "Ahmed", "Tarek", Role.INSTRUCTOR);
        createUser("admin@fahmak.com", "Seif", "Mahmoud", Role.ADMIN);
    }
                            
    private void createUser(String email, String firstName, String lastName, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(email.split("@")[0]);
        user.setPasswordHash(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setAuthProvider(com.fahmak.alena.user.entity.AuthProvider.LOCAL);
        user.setRegistrationDate(LocalDateTime.now());
        user.setStatus(ACTIVE_STATUS);
        userRepository.save(user);
        log.debug("Created real user: {} ({})", email, role);
    }

    private void initializeRealCourses() {
        User instructor = userRepository.findByEmail("ahmed.instructor@fahmak.com").orElseThrow();

        List<Course> courses = List.of(
                buildCourse("Full-Stack Web Development with Spring Boot & Angular",
                        "Learn to build enterprise-level applications from scratch using Java Spring Boot for the backend and Angular 17 for the frontend. Covers REST APIs, Security, and State Management.",
                        instructor, "Intermediate", "Software Engineering", 5),
                buildCourse("Artificial Intelligence & Machine Learning Fundamentals",
                        "A comprehensive guide to understanding AI concepts, Machine Learning algorithms, and Deep Learning neural networks using Python, TensorFlow, and PyTorch.",
                        instructor, "Advanced", "Data Science", 10),
                buildCourse("UI/UX Design Masterclass",
                        "Master Figma and learn how to create stunning, user-centered designs. We cover wireframing, prototyping, and design systems.",
                        instructor, "Beginner", "Design", 2)
        );

        courseRepository.saveAll(courses);
        log.debug("Created {} real courses.", courses.size());
    }

    private Course buildCourse(String title, String description, User instructor,
                               String difficulty, String category, int daysAgo) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setInstructor(instructor);
        course.setDifficultyLevel(difficulty);
        course.setCategory(category);
        course.setCreationDate(LocalDateTime.now().minusDays(daysAgo));
        return course;
    }

    private void initializeRealQuizzes() {
        // Get the AI course to attach a quiz to it
        Course aiCourse = courseRepository.findAll().stream()
                .filter(c -> c.getTitle().contains("Artificial Intelligence"))
                .findFirst().orElseThrow();

        Quiz aiQuiz = new Quiz();
        aiQuiz.setTitle("Neural Networks & Deep Learning Core Exam");
        aiQuiz.setDescription("This exam tests your fundamental understanding of Neural Networks, Activation Functions, and Backpropagation algorithms.");
        aiQuiz.setDifficultyLevel("Advanced");
        aiQuiz.setPassScore(70.0);
        aiQuiz.setMaxAttempts(3);
        aiQuiz.setCourse(aiCourse);
        quizRepository.save(aiQuiz);

        // Q1
        Question q1 = new Question();
        q1.setQuiz(aiQuiz);
        q1.setQuestionText("Which activation function is most commonly used in the hidden layers of modern deep neural networks to prevent the vanishing gradient problem?");
        q1.setOptions("[\"Sigmoid\", \"Tanh\", \"ReLU\", \"Linear\"]");
        q1.setQuestionType("MULTIPLE_CHOICE");
        q1.setCorrectAnswer("ReLU");
        q1.setExplanation("ReLU (Rectified Linear Unit) is heavily used because it does not saturate for positive values, mitigating the vanishing gradient problem found in Sigmoid and Tanh.");
        questionRepository.save(q1);

        // Q2
        Question q2 = new Question();
        q2.setQuiz(aiQuiz);
        q2.setQuestionText("What is the primary purpose of the backpropagation algorithm in neural networks?");
        q2.setOptions("[\"To initialize weights optimally before training\", \"To calculate gradients of the loss function with respect to the network weights\", \"To normalize the input data automatically\", \"To add bias to the output neurons\"]");
        q2.setQuestionType("MULTIPLE_CHOICE");
        q2.setCorrectAnswer("To calculate gradients of the loss function with respect to the network weights");
        q2.setExplanation("Backpropagation computes the gradient of the loss function, allowing optimization algorithms like Stochastic Gradient Descent (SGD) to correctly update the network's weights.");
        questionRepository.save(q2);
        
        // Q3
        Question q3 = new Question();
        q3.setQuiz(aiQuiz);
        q3.setQuestionText("Which technique is used to prevent a neural network from overfitting the training data by randomly dropping neurons during training?");
        q3.setOptions("[\"Batch Normalization\", \"Dropout\", \"Data Augmentation\", \"Max Pooling\"]");
        q3.setQuestionType("MULTIPLE_CHOICE");
        q3.setCorrectAnswer("Dropout");
        q3.setExplanation("Dropout is a regularization technique where randomly selected neurons are ignored during training, forcing the network to learn more robust features that generalize better to new data.");
        questionRepository.save(q3);

        log.debug("Created real AI quiz with 3 technical questions.");
    }

    private void initializeEnrollments() {
        Course aiCourse = courseRepository.findAll().stream()
                .filter(c -> c.getTitle().contains("Artificial Intelligence"))
                .findFirst().orElseThrow();
                
        User student = userRepository.findByEmail("omar.student@fahmak.com").orElseThrow();
        
        com.fahmak.alena.course.entity.CourseEnrollment enrollment = new com.fahmak.alena.course.entity.CourseEnrollment();
        enrollment.setCourse(aiCourse);
        enrollment.setStudent(student);
        enrollment.setEnrollmentDate(java.time.LocalDateTime.now());
        enrollment.setProgressPercentage(10); // started the course
        enrollment.setStatus("ACTIVE");
        
        courseEnrollmentRepository.save(enrollment);
        log.debug("Enrolled student in AI course.");
    }

    private void generateMockCustomersAndEnrollments() {
        log.info("Generating 50 mock customers...");
        
        List<Course> allCourses = courseRepository.findAll();
        if (allCourses.isEmpty()) return;

        for (int i = 1; i <= 50; i++) {
            User user = new User();
            user.setEmail("customer" + i + "@fahmak.com");
            user.setUsername("customer" + i);
            user.setPasswordHash(passwordEncoder.encode(DEFAULT_PASSWORD));
            user.setFirstName("Customer");
            user.setLastName(String.valueOf(i));
            user.setRole(Role.STUDENT);
            user.setRegistrationDate(LocalDateTime.now().minusDays((int) (Math.random() * 30)));
            user.setStatus(ACTIVE_STATUS);
            userRepository.save(user);

            // Enroll them randomly in 1 or 2 courses
            int numEnrollments = (int) (Math.random() * 2) + 1;
            for (int j = 0; j < numEnrollments; j++) {
                Course randomCourse = allCourses.get((int) (Math.random() * allCourses.size()));
                
                if (!courseEnrollmentRepository.existsByStudentAndCourse(user, randomCourse)) {
                    com.fahmak.alena.course.entity.CourseEnrollment enrollment = new com.fahmak.alena.course.entity.CourseEnrollment();
                    enrollment.setCourse(randomCourse);
                    enrollment.setStudent(user);
                    enrollment.setEnrollmentDate(LocalDateTime.now().minusDays((int) (Math.random() * 10)));
                    enrollment.setProgressPercentage((int) (Math.random() * 100));
                    enrollment.setStatus("ACTIVE");
                    courseEnrollmentRepository.save(enrollment);
                }
            }
        }
        log.info("Successfully generated 50 mock customers and random enrollments.");
    }

    private void initializeSubscriptionPlans() {
        if (subscriptionPlanRepository.count() == 0) {
            SubscriptionPlan basic = new SubscriptionPlan();
            basic.setName("BASIC");
            basic.setStripePriceId("price_1OxxxxxxxxxxxxxxBASIC");
            basic.setPrice(9.99);
            
            SubscriptionPlan premium = new SubscriptionPlan();
            premium.setName("PREMIUM");
            premium.setStripePriceId("price_1OxxxxxxxxxxxxxxPREMIUM");
            premium.setPrice(19.99);

            subscriptionPlanRepository.saveAll(List.of(basic, premium));
            log.debug("Created real subscription plans.");
        }
    }
}

