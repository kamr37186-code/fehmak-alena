package com.fahmak.alena.config;

import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fahmak.alena.user.entity.*;
import com.fahmak.alena.course.entity.*;
import com.fahmak.alena.course.entity.Module;
import com.fahmak.alena.gamification.entity.*;
import com.fahmak.alena.assessment.entity.*;
import com.fahmak.alena.ai.entity.*;
import com.fahmak.alena.immersive.entity.*;
import com.fahmak.alena.payment.entity.*;
import com.fahmak.alena.notification.entity.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    private final EntityManager em;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    public DatabaseSeeder(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!seedEnabled) {
            System.out.println("DatabaseSeeder is disabled.");
            return;
        }

        Long count = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        if (count > 0) {
            System.out.println("Database already seeded. Skipping.");
            return;
        }

        System.out.println("🚀 Starting FULL database seeding in English...");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pass = encoder.encode("password123");

        // 1. USERS
        User admin = new User();
        admin.setFirstName("Super");
        admin.setLastName("Admin");
        admin.setUsername("admin");
        admin.setEmail("admin@fahmak.com");
        admin.setPasswordHash(pass);
        admin.setRole(Role.ADMIN);
        admin.setAuthProvider(AuthProvider.LOCAL);
        admin.setStatus("ACTIVE");
        admin.setRegistrationDate(LocalDateTime.now());
        em.persist(admin);

        User inst1 = new User();
        inst1.setFirstName("John");
        inst1.setLastName("Doe");
        inst1.setUsername("johndoe");
        inst1.setEmail("john.doe@fahmak.com");
        inst1.setPasswordHash(pass);
        inst1.setRole(Role.INSTRUCTOR);
        inst1.setAuthProvider(AuthProvider.LOCAL);
        inst1.setStatus("ACTIVE");
        inst1.setRegistrationDate(LocalDateTime.now());
        em.persist(inst1);

        User student1 = new User();
        student1.setFirstName("Alice");
        student1.setLastName("Smith");
        student1.setUsername("alicesmith");
        student1.setEmail("alice.smith@fahmak.com");
        student1.setPasswordHash(pass);
        student1.setRole(Role.STUDENT);
        student1.setAuthProvider(AuthProvider.LOCAL);
        student1.setStatus("ACTIVE");
        student1.setRegistrationDate(LocalDateTime.now());
        em.persist(student1);

        // 2. USER PREFERENCES
        UserPreferences prefs = new UserPreferences();
        prefs.setUser(student1);
        prefs.setHighContrastMode(false);
        prefs.setColorBlindnessFilters(false);
        prefs.setTextScaling(110);
        prefs.setReducedMotion(false);
        prefs.setAiPersona("Socratic Tutor");
        em.persist(prefs);

        // 3. PAYMENTS & SUBSCRIPTIONS
        SubscriptionPlan freePlan = new SubscriptionPlan();
        freePlan.setName("Basic Plan");
        freePlan.setStripePriceId("price_basic");
        freePlan.setPrice(0.0);
        freePlan.setCurrency("usd");
        freePlan.setInterval("month");
        em.persist(freePlan);

        SubscriptionPlan proPlan = new SubscriptionPlan();
        proPlan.setName("Pro Plan");
        proPlan.setStripePriceId("price_pro");
        proPlan.setPrice(29.99);
        proPlan.setCurrency("usd");
        proPlan.setInterval("month");
        em.persist(proPlan);

        UserSubscription sub = new UserSubscription();
        sub.setUser(student1);
        sub.setPlan(proPlan);
        sub.setStatus("ACTIVE");
        sub.setCurrentPeriodStart(LocalDateTime.now());
        sub.setCurrentPeriodEnd(LocalDateTime.now().plusMonths(1));
        sub.setStripeSubscriptionId("sub_stripe_" + UUID.randomUUID().toString());
        sub.setStripeCustomerId("cus_stripe_" + UUID.randomUUID().toString());
        em.persist(sub);

        PaymentTransaction tx = new PaymentTransaction();
        tx.setUser(student1);
        tx.setAmount(29.99);
        tx.setCurrency("usd");
        tx.setStatus("SUCCEEDED");
        tx.setStripePaymentIntentId("pi_stripe_" + UUID.randomUUID().toString());
        tx.setCreatedAt(LocalDateTime.now());
        em.persist(tx);

        // 4. NOTIFICATIONS
        Notification notif = new Notification();
        notif.setUser(student1);
        notif.setTitle("Welcome to Fahmak!");
        notif.setMessage("Your Pro Plan is active. Enjoy learning!");
        notif.setType(NotificationType.SYSTEM);
        notif.setRead(false);
        notif.setCreatedAt(LocalDateTime.now());
        em.persist(notif);

        // 5. GAMIFICATION (Badges, Activities)
        Badge badge1 = new Badge();
        badge1.setName("First Blood");
        badge1.setDescription("Completed your first lesson");
        badge1.setIconUrl("icon-first-blood.png");
        badge1.setCriteria("COMPLETE_FIRST_LESSON");
        em.persist(badge1);

        GamificationActivity gAct = new GamificationActivity();
        gAct.setUser(student1);
        gAct.setActivityType("COURSE_COMPLETION");
        gAct.setXpGained(500);
        gAct.setActivityDate(LocalDateTime.now());
        gAct.setBadgeAwarded(badge1);
        em.persist(gAct);

        // 6. COURSES, MODULES, LESSONS
        Course c1 = new Course();
        c1.setTitle("Full-Stack Web Development BootCamp");
        c1.setDescription("Master Angular and Spring Boot by building real-world enterprise applications.");
        c1.setCategory("Programming");
        c1.setDifficultyLevel("INTERMEDIATE");
        c1.setDurationHours(40);
        c1.setPrice(199.99);
        c1.setInstructor(inst1);
        c1.setStatus("PUBLISHED");
        c1.setCreationDate(LocalDateTime.now());
        em.persist(c1);

        Course c2 = new Course();
        c2.setTitle("Advanced Machine Learning and AI");
        c2.setDescription("Deep dive into neural networks, PyTorch, and Generative AI models.");
        c2.setCategory("Data Science");
        c2.setDifficultyLevel("ADVANCED");
        c2.setDurationHours(50);
        c2.setPrice(299.99);
        c2.setInstructor(inst1);
        c2.setStatus("PUBLISHED");
        c2.setCreationDate(LocalDateTime.now());
        em.persist(c2);

        Module m1 = new Module();
        m1.setCourse(c1);
        m1.setTitle("Getting Started with Angular");
        m1.setOrderIndex(1);
        em.persist(m1);

        Lesson l1 = new Lesson();
        l1.setModule(m1);
        l1.setTitle("Components and Directives");
        l1.setLessonType("VIDEO");
        l1.setContentUrl("https://example.com/video1");
        l1.setDurationMinutes(20);
        l1.setOrderIndex(1);
        em.persist(l1);

        // 7. COURSE ENROLLMENTS
        CourseEnrollment ce = new CourseEnrollment();
        ce.setCourse(c1);
        ce.setStudent(student1);
        ce.setEnrollmentDate(LocalDateTime.now());
        ce.setProgressPercentage(15);
        ce.setStatus("IN_PROGRESS");
        em.persist(ce);

        // 8. ASSESSMENT (Quiz, Questions, Attempts, Sessions, Cheat Events)
        Quiz qz = new Quiz();
        qz.setCourse(c1);
        qz.setModule(m1);
        qz.setTitle("Angular Basics Assessment");
        qz.setDescription("Test your knowledge on Angular components and decorators.");
        qz.setPassScore(70.0);
        qz.setDifficultyLevel("MEDIUM");
        qz.setMaxAttempts(3);
        em.persist(qz);

        Question qst = new Question();
        qst.setQuiz(qz);
        qst.setQuestionText("What is a Component in Angular?");
        qst.setQuestionType("MULTIPLE_CHOICE");
        qst.setCorrectAnswer("A TypeScript class with an HTML template");
        qst.setOptions(
                "[\"A Java class\", \"A database table\", \"A TypeScript class with an HTML template\", \"A CSS framework\"]");
        em.persist(qst);

        QuizSession qs = new QuizSession();
        qs.setQuiz(qz);
        qs.setUser(student1);
        qs.setStartedAt(LocalDateTime.now().minusMinutes(20));
        qs.setSubmittedAt(LocalDateTime.now());
        qs.setIntegrityVerdict(IntegrityVerdict.SUSPICIOUS);
        qs.setRiskScore(85.0);
        em.persist(qs);

        QuizAttempt qa = new QuizAttempt();
        qa.setUser(student1);
        qa.setQuestion(qst);
        qa.setStudentAnswer("A TypeScript class with an HTML template");
        qa.setCorrect(true);
        qa.setAttemptDate(LocalDateTime.now());
        em.persist(qa);

        CheatEvent cevent = new CheatEvent();
        cevent.setSession(qs);
        cevent.setEventType(CheatEventType.WINDOW_BLUR);
        cevent.setEventTimestamp(LocalDateTime.now().minusMinutes(5));
        cevent.setMetadata("{\"durationMs\": 5000}");
        em.persist(cevent);

        // 9. AI CHAT
        ChatMessageEntity chat1 = new ChatMessageEntity();
        chat1.setUser(student1);
        chat1.setSender("user");
        chat1.setText("Can you explain Dependency Injection in Spring Boot?");
        chat1.setTimestamp(LocalDateTime.now().minusHours(1));
        em.persist(chat1);

        ChatMessageEntity chat2 = new ChatMessageEntity();
        chat2.setUser(student1);
        chat2.setSender("ai");
        chat2.setText("Certainly! Dependency Injection (DI) is a core concept in Spring...");
        chat2.setTimestamp(LocalDateTime.now().minusHours(1).plusSeconds(5));
        em.persist(chat2);

        // 10. IMMERSIVE SESSIONS
        ImmersiveSession is = new ImmersiveSession();
        is.setUser(student1);
        is.setModelName("Human Heart");
        is.setAiScore(85);
        is.setNotesGenerated(2);
        is.setCurrentTranscriptIndex(5);
        em.persist(is);

        System.out.println("✅ All Database Tables Successfully Seeded with English Data!");
    }
}
