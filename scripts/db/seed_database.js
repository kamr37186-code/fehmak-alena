const BASE_URL = 'http://localhost:8080/api';

async function fetchApi(endpoint, options = {}, cookies = '') {
    const url = `${BASE_URL}${endpoint}`;
    if (!options.headers) options.headers = {};
    if (cookies) options.headers['Cookie'] = cookies;
    if (!options.headers['Content-Type']) options.headers['Content-Type'] = 'application/json';

    try {
        const response = await fetch(url, options);
        let setCookie = response.headers.get('set-cookie');
        let newCookies = cookies;

        if (setCookie) {
            newCookies = setCookie.split(', ')
                .map(c => c.split(';')[0])
                .join('; ');
        }

        const text = await response.text();
        let data = {};
        if (text) {
            try {
                data = JSON.parse(text);
            } catch (e) {
                data = text;
            }
        }

        return { data, cookies: newCookies || cookies, status: response.status };
    } catch (error) {
        console.error(`Error fetching ${url}:`, error.message);
        return { data: null, status: 500, cookies };
    }
}

async function seedDatabase() {
    console.log("🚀 Starting Data Seeding Process...");

    // 1. Instructors
    console.log("👨‍🏫 Registering Instructors...");
    const instructors = [
        {
            firstName: "Dr. Ahmed", lastName: "Khalil", email: "ahmed@fahmak.com", password: "Password@123", role: "INSTRUCTOR"
        },
        {
            firstName: "Eng. Sara", lastName: "Mahmoud", email: "sara@fahmak.com", password: "Password@123", role: "INSTRUCTOR"
        }
    ];

    let instructorCookiesMap = {};
    for (let inst of instructors) {
        let { data, cookies, status } = await fetchApi('/auth/register', {
            method: 'POST', body: JSON.stringify(inst)
        });
        if (status !== 201 && status !== 200) {
            const loginRes = await fetchApi('/auth/login', {
                method: 'POST', body: JSON.stringify({ email: inst.email, password: inst.password })
            });
            data = loginRes.data;
            cookies = loginRes.cookies;
        }
        let instId = data.id || data.userId || 1;
        instructorCookiesMap[inst.email] = { cookies, id: instId };
        console.log(`✅ Instructor Ready: ${inst.firstName} ${inst.lastName}`);
    }

    // 2. Student
    console.log("👨‍🎓 Registering Student...");
    const studentReq = {
        firstName: "Youssef", lastName: "Ahmed", email: "youssef@fahmak.com", password: "Password@123", role: "STUDENT"
    };
let { data: studentRes, cookies: studentCookies, status: stuStatus } = await fetchApi('/auth/register', {
    method: 'POST', body: JSON.stringify(studentReq)
});
if (stuStatus !== 201 && stuStatus !== 200) {
    const loginRes = await fetchApi('/auth/login', {
        method: 'POST', body: JSON.stringify({ email: studentReq.email, password: studentReq.password })
    });
    studentRes = loginRes.data;
    studentCookies = loginRes.cookies;
}
let studentId = studentRes.id || studentRes.userId || 2;
console.log(`✅ Student Ready: ${studentReq.firstName} ${studentReq.lastName} (ID: ${studentId})`);

// 3. Realistic Courses
console.log("📚 Creating Real English Courses...");
const coursesToCreate = [
    {
        title: "Fundamentals of Web App Development using Angular",
        description: "A comprehensive and intensive course to learn frontend web development using the Angular framework from scratch to mastery with real-world practical applications.",
        category: "Programming & Development",
        difficultyLevel: "INTERMEDIATE",
        durationHours: 35,
        price: 199.99,
        imageUrl: "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=500&q=80",
        instructorEmail: "ahmed@fahmak.com"
    },
    {
        title: "Modern Physics for High School - Comprehensive Curriculum",
        description: "Detailed and simplified explanation of the modern physics curriculum for high school students, including interactive examples, experiments, and periodic tests.",
        category: "School Education",
        difficultyLevel: "BEGINNER",
        durationHours: 20,
        price: 49.99,
        imageUrl: "https://images.unsplash.com/photo-1636466497217-26a8cbeaf0aa?w=500&q=80",
        instructorEmail: "ahmed@fahmak.com"
    },
    {
        title: "Mobile App Development with Flutter",
        description: "Learn how to build mobile applications for both Android and iOS using a single codebase through the powerful Flutter platform.",
        category: "Mobile Programming",
        difficultyLevel: "ADVANCED",
        durationHours: 40,
        price: 250.00,
        imageUrl: "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=500&q=80",
        instructorEmail: "sara@fahmak.com"
    },
    {
        title: "Digital Marketing & Ad Campaign Management",
        description: "Discover the secrets of marketing on social media platforms and how to launch successful advertising campaigns that achieve the highest return on investment.",
        category: "Marketing",
        difficultyLevel: "BEGINNER",
        durationHours: 15,
        price: 99.99,
        imageUrl: "https://images.unsplash.com/photo-1432888498266-38ffec3eaf0a?w=500&q=80",
        instructorEmail: "sara@fahmak.com"
    },
    {
        title: "Learn English Conversation in 30 Days",
        description: "An intensive training program on English speaking skills in various daily situations and personal interviews with fluency.",
        category: "Languages",
        difficultyLevel: "INTERMEDIATE",
        durationHours: 30,
        price: 150.00,
        imageUrl: "https://images.unsplash.com/photo-1546410531-dd4cbd106410?w=500&q=80",
        instructorEmail: "ahmed@fahmak.com"
    },
    {
        title: "User Interface Design (UI/UX) with Figma",
        description: "A complete methodology to learn UI/UX design for apps and websites using Figma, and how to think like a user experience designer.",
        category: "Design",
        difficultyLevel: "BEGINNER",
        durationHours: 25,
        price: 120.00,
        imageUrl: "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=500&q=80",
        instructorEmail: "sara@fahmak.com"
    }
];

let createdCourses = [];
for (const course of coursesToCreate) {
    const instCookies = instructorCookiesMap[course.instructorEmail].cookies;
    const { data: createdCourse } = await fetchApi('/courses', {
        method: 'POST',
        body: JSON.stringify(course)
    }, instCookies);
    if (createdCourse && createdCourse.id) {
        createdCourses.push(createdCourse);
        console.log(`   - 📚 Created: ${createdCourse.title}`);
    }
}

// 4. Enroll Student
console.log("📝 Enrolling Student in Courses...");
for (const course of createdCourses) {
    await fetchApi(`/courses/${course.id}/enroll`, { method: 'POST' }, studentCookies);
}
console.log(`✅ Student enrolled in ${createdCourses.length} courses.`);

// 5. XP Awarding
console.log("🏆 Awarding XP to Student...");
const ahmedCookies = instructorCookiesMap["ahmed@fahmak.com"].cookies;
await fetchApi(`/gamification/users/${studentId}/award`, {
    method: 'POST',
    body: JSON.stringify({ xpAmount: 1500, reason: "Completing outstanding projects" })
}, ahmedCookies);
console.log(`✅ 1500 XP Awarded.`);

console.log("🎉 Seeding Completed Successfully! Your database is now fresh and realistic.");
}

seedDatabase();
