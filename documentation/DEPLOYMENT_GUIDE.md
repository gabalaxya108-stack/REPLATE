# REPLATE: End-to-End Production Deployment Guide
## Making Your Full-Stack Application Live on the Internet

This guide explains three battle-tested ways to deploy REPLATE live on the internet so that anyone—including university examiners, recruiters, and evaluators—can access your working application worldwide.

---

## 🌟 Recommended Deployment Path: 100% Free Cloud Stack
This is the most popular, zero-cost architecture for B.Tech capstone projects:
- **Managed PostgreSQL Database:** [Neon.tech](https://neon.tech) or [Supabase](https://supabase.com) (Free 500 MB PostgreSQL 15/16)
- **Java 21 Spring Boot API:** [Render.com](https://render.com) (Free Web Service)
- **React 18 Frontend:** [Vercel.com](https://vercel.com) (Free Global Edge CDN)

---

### STEP 1: Provision Free Cloud PostgreSQL (5 Minutes)

1. Go to [Neon.tech](https://neon.tech) and sign up with GitHub.
2. Click **"New Project"**:
   - **Name:** `replate-db`
   - **Postgres version:** `15` or `16`
   - **Region:** Choose closest to your users (e.g. `Singapore` / `ap-southeast-1` or `Frankfurt`).
3. Once created, go to the **SQL Editor** tab in Neon:
   - Copy the contents of [`database/schema.sql`](../database/schema.sql) and click **Run**.
   - Copy the contents of [`database/seed-data.sql`](../database/seed-data.sql) and click **Run**.
4. Go to **Dashboard** $\to$ **Connection Details**:
   - Copy the JDBC connection parameters:
     - **Host:** e.g., `ep-quiet-wave-123456.ap-southeast-1.aws.neon.tech`
     - **Database:** `neondb` (or `replate_db`)
     - **User:** `neondb_owner`
     - **Password:** *(your generated password)*
     - **JDBC URL:** `jdbc:postgresql://<host>/<database>?sslmode=require`

---

### STEP 2: Deploy Spring Boot Backend on Render (10 Minutes)

1. Go to [Render.com](https://render.com) and log in with your GitHub account.
2. Click **"New +"** $\to$ **"Web Service"**.
3. Select your GitHub repository: `https://github.com/gabalaxya108-stack/REPLATE.git`.
4. Configure the service settings:
   - **Name:** `replate-backend`
   - **Region:** Same as your database (e.g., `Singapore` or `Frankfurt`).
   - **Root Directory:** `backend`
   - **Language:** `Docker` (Render will automatically detect `backend/Dockerfile`)
     *(Or select `Java`, Build Command: `mvn clean package -DskipTests`, Start Command: `java -jar target/replate-backend-1.0.0-SNAPSHOT.jar`)*
   - **Instance Type:** `Free`
5. Scroll down to **"Environment Variables"** and add:
   | Key | Value | Description |
   |---|---|---|
   | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<neon-host>/neondb?sslmode=require` | Your Neon JDBC connection string |
   | `SPRING_DATASOURCE_USERNAME` | `<neon-user>` | Neon database username |
   | `SPRING_DATASOURCE_PASSWORD` | `<neon-password>` | Neon database password |
   | `JWT_SECRET` | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` | 256-bit token signing secret |
   | `SPRING_PROFILES_ACTIVE` | `prod` | Production Spring profile |
6. Click **"Create Web Service"**.
7. Render will build and launch your backend. Once complete, your live API URL will be ready:
   - Example: `https://replate-backend.onrender.com`
8. Verify it by visiting: `https://replate-backend.onrender.com/api/v1/donations/public` in your browser (it will return JSON with all 27 active listings).

---

### STEP 3: Deploy React Frontend on Vercel (3 Minutes)

1. Go to [Vercel.com](https://vercel.com) and log in with GitHub.
2. Click **"Add New..."** $\to$ **"Project"**.
3. Import your GitHub repository: `gabalaxya108-stack/REPLATE`.
4. In the Project Configuration:
   - **Framework Preset:** `Vite`
   - **Root Directory:** Click Edit and select `frontend`
5. Expand **"Environment Variables"** and add:
   | Key | Value |
   |---|---|
   | `VITE_API_BASE_URL` | `https://replate-backend.onrender.com/api/v1` |
   *(Replace with your actual live Render backend URL, appending `/api/v1`)*
6. Click **"Deploy"**.
7. In ~60 seconds, Vercel will provide your public HTTPS domain:
   - Example: `https://replate.vercel.app`

🎉 **Congratulations! Your application is now live on the global internet with HTTPS!**

---

## 🛠 Option 2: 1-Click All-in-One Cloud with Railway.app

If you prefer deploying everything inside a single unified dashboard:

1. Sign up at [Railway.app](https://railway.app).
2. Click **"New Project"** $\to$ **"Provision PostgreSQL"**:
   - Railway spins up a dedicated PostgreSQL instance.
   - Run `schema.sql` and `seed-data.sql` via Railway's query tool.
3. Click **"New"** $\to$ **"GitHub Repo"** $\to$ Select `REPLATE`:
   - Set Root Directory: `backend`
   - Link environment variable `DATABASE_URL` to the provisioned PostgreSQL instance.
4. Click **"New"** $\to$ **"GitHub Repo"** $\to$ Select `REPLATE`:
   - Set Root Directory: `frontend`
   - Set `VITE_API_BASE_URL` to backend service internal/external domain.
5. Railway provisions custom HTTPS domains for both services automatically.

---

## 🐳 Option 3: Self-Hosted Cloud VPS (AWS EC2 / DigitalOcean / Oracle)

If you have a Linux virtual machine (Ubuntu 22.04 LTS):

1. **Install Docker & Docker Compose on the VPS:**
   ```bash
   sudo apt update && sudo apt install -y docker.io docker-compose-v2
   sudo systemctl enable --now docker
   ```
2. **Clone your GitHub repository:**
   ```bash
   git clone https://github.com/gabalaxya108-stack/REPLATE.git
   cd REPLATE
   ```
3. **Launch the entire multi-container stack:**
   ```bash
   docker compose up -d --build
   ```
4. **Verify running containers:**
   ```bash
   docker compose ps
   ```
   - `replate-postgres`: running on port `5432`
   - `replate-backend`: running on port `8080`
   - `replate-frontend`: running on port `3000` (via Nginx)
5. Access your application via your VPS Public IP: `http://<your-vps-ip>:3000`.

---

## 🔒 Verification & Health Check Checklist

After deploying, verify the live system:

- [ ] **Public Feed:** Visit your frontend URL `/explore` and confirm all 27 food cards render with live timers.
- [ ] **Authentication:** Log in as Donor (`contact@saravanabhavan.in` / `Password@123`) and verify that dashboard cards load without CORS errors.
- [ ] **NGO Claims:** Log in as NGO (`foodrescue@robinhoodarmy.org` / `Password@123`) and submit a test claim.
- [ ] **Volunteer Logistics:** Log in as Volunteer (`aarav.sharma@gmail.com` / `Password@123`) and test the OTP verification modal.

