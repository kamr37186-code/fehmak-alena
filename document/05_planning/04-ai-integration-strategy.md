# AI Integration Strategy

**Fahmak Alena** differentiates itself through deep AI integration using Google Gemini / Vertex AI. This document defines how AI features will be implemented.

## 1. Architectural Approach

AI interactions will be managed entirely by the backend Spring Boot application via an internal `AiIntegrationService`. 
*   **Security:** The frontend will *never* interact with the LLM API directly to prevent API key exposure.
*   **Abstraction:** The `AiIntegrationService` abstracts the specific API client, allowing easy switching of models or providers if necessary.

## 2. Key AI Features

### A. Adaptive Assessments (Dynamic Quizzing)
*   **Use Case:** Instead of static question banks, the system generates questions on the fly based on the student's current proficiency level.
*   **Implementation Flow:**
    1.  Student requests a quiz for a specific topic.
    2.  Backend retrieves student's past performance metrics.
    3.  Backend prompts the LLM: *"Generate a multiple-choice question on [Topic] at difficulty level [1-10]. Provide 4 options and identify the correct one. Format as JSON."*
    4.  Backend parses the response and serves the question to the frontend.

### B. Personalized Learning Paths
*   **Use Case:** Recommending the next optimal lesson or module based on areas where the student is struggling.
*   **Implementation Flow:**
    1.  A nightly cron job or real-time trigger analyzes a student's recent assessment scores.
    2.  The data is fed to the LLM to identify weak points and recommend a curated path of existing lessons.

### C. Content Summarization & Study Guides
*   **Use Case:** Providing quick summaries of long lectures or reading materials.
*   **Implementation Flow:**
    1.  Instructor uploads material.
    2.  Backend passes the text to the LLM with a prompt to generate bullet points and key takeaways.
    3.  Summary is stored in the database alongside the lesson content.

### D. Virtual Assistant (Tutor Bot)
*   **Use Case:** An interactive chat interface within the Immersive Hub for students to ask questions about the current lesson.
*   **Implementation Flow:**
    1.  Frontend sends user query to backend.
    2.  Backend augments the query with context (the current lesson text).
    3.  LLM generates an educational response, structured to guide the student rather than just giving the answer directly.

## 3. Data Privacy & Ethics

*   No Personally Identifiable Information (PII) will be sent to external LLM providers.
*   All user data sent for analysis will be anonymized.
*   AI-generated content (especially assessments) will be flagged for optional instructor review to ensure accuracy.
