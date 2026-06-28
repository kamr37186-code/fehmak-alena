# Fahmak 3alena

A modern learning platform with AI-driven features, adaptive assessments, and immersive video classrooms.

## 🚀 Quick Start Guide

To run this project on your local machine, you will need:
1. **Java 17** or higher
2. **Node.js** (v18+)

### Option A: Start Both Apps on Windows
From the repository root, run:
```bat
scripts\run.bat
```

You can also run the script from the `scripts` folder:
```bat
cd scripts
run.bat
```

The script installs frontend dependencies, opens Angular in a new terminal, and runs the Spring Boot backend in the current terminal.

### Option B: Start Manually

#### Step 1: Start the Backend (Spring Boot 4.1.0)
Open a terminal in the `backend` folder and run:
```bash
cd backend
./mvnw spring-boot:run
```
*(The backend runs on `http://localhost:8080`. It connects to a PostgreSQL database on `localhost:5432/project_L_Plateform`, and the frontend communicates with the backend via `http://localhost:8080/api`.)*

#### Step 2: Start the Frontend (Angular 21 Zoneless with GSAP)
Open a new terminal in the `frontend` folder and run:
```bash
cd frontend
npm install
npm start
```
*(The frontend runs on `http://localhost:4200`)*

---

### 🎨 Latest Updates
- **Frontend Migration:** Successfully migrated the frontend to **Angular 21** with **Zoneless** change detection and **Signal-based** reactivity.
- **Backend Migration:** Successfully upgraded the backend to **Spring Boot 4.1.0**, updating dependencies and mock utilities.
- **Testing:** Replaced Jasmine/Karma with **Vitest** for robust and modern unit testing.
- **Styling:** Migrated styling system to **Tailwind CSS v4**.
- **WebRTC:** Integrated ZegoCloud for virtual tutoring rooms.
- **Animations:** Fully integrated **GSAP** into all new frontend components for premium UI/UX.
- **API Port Fix:** Corrected frontend API calls to connect to port **8080** instead of 8081.

---

### ⚙️ Environment Variables (Optional)
To enable AI features, set your Groq/OpenAI API key in your environment before starting the backend:
**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="your-api-key"
```
**Mac/Linux:**
```bash
export OPENAI_API_KEY="your-api-key"
```

---

### 🔑 Default Test Accounts
When the backend starts and the `seed_database.js` script is run, it seeds the database with the following test accounts:

| Role | Email | Password |
| :--- | :--- | :--- |
| **Instructor 1** | `ahmed@fahmak.com` | `Password@123` |
| **Instructor 2** | `sara@fahmak.com` | `Password@123` |
| **Student** | `youssef@fahmak.com` | `Password@123` |
| **Admin** | `admin@fahmak.com` | `password123` |

---

### 📂 Repository Structure
- `backend/`: Spring Boot 4.1.0 application (Modular Monolithic Architecture).
- `frontend/`: Angular 21 Standalone application (Zoneless, Signals) with Tailwind CSS v4 and GSAP.
- `document/`: Project specifications, analysis, system contexts, and API endpoints.
- `devops/`: Docker Compose and infrastructure files.
- `schema/`: Database schemas and architecture diagrams.
- `scripts/`: Helper scripts like `run.bat` for quickly booting both environments.
# fehmak-alena
# fehmak-alena
